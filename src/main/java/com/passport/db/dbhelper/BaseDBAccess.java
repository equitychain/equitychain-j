package com.passport.db.dbhelper;

import com.alibaba.fastjson.JSONObject;
import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;
import com.passport.constant.Constant;
import com.passport.utils.ClassUtil;
import com.passport.utils.SerializeUtils;
import org.rocksdb.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class BaseDBAccess implements DBAccess {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));
//    public rocksDB rocksDB;
    public RocksDB rocksDB;
    @Value("${db.dataDir}")
    private String dataDir;


    //列的handler
    protected final Map<String, ColumnFamilyHandle> handleMap = new HashMap<>();
    protected final Set<Class> dtoClasses = new HashSet<>();

    protected void initDB() {
        try {
            //数据库目录不存在就创建
            File directory = new File(System.getProperty("user.dir") + "/" + dataDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            List<String> fields = new ArrayList<>();
            IndexColumnNames[] indexColumnNames = IndexColumnNames.values();
            //TODO: 添加字节码,加载dto属性--cannot not read.
            List<Class<?>> classes = ClassUtil.getClasses("com.passport.core");
            for(Class c : classes){
                fields.addAll(getClassCols(c));
                dtoClasses.add(c);
            }
            try {
                rocksDB = rocksDB.open(new Options().setCreateIfMissing(true).setDbLogDir("d:/dbLog"),dataDir);

                //添加默认的列族
                handleMap.put("default", rocksDB.getDefaultColumnFamily());
                for (String field : fields) {
                    ColumnFamilyDescriptor descriptor = new ColumnFamilyDescriptor(field.getBytes());
                    ColumnFamilyHandle handle = rocksDB.createColumnFamily(descriptor);
                    handleMap.put(field, handle);
                }
                for (IndexColumnNames columnNames : indexColumnNames) {
                    ColumnFamilyHandle indexNameHandle = rocksDB.createColumnFamily(columnNames.getIndexName());
                    ColumnFamilyHandle indexOverHandle = rocksDB.createColumnFamily(columnNames.getOverAndNextName());
                    handleMap.put(columnNames.indexName, indexNameHandle);
                    handleMap.put(columnNames.overAndNextName, indexOverHandle);
                }
            } catch (Exception e) {
                //列集合
                List<ColumnFamilyDescriptor> curHasColumns = new ArrayList<>();
                List<ColumnFamilyDescriptor> curDontHasColumns = new ArrayList<>();
                List<byte[]> curColbyts = RocksDB.listColumnFamilies(new Options(), dataDir);
                List<String> curColStrs = new ArrayList<>();
                for (byte[] byts : curColbyts){
                    curColStrs.add(new String(byts));
                }
                ColumnFamilyDescriptor defaultDescriptor = new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY);
                curHasColumns.add(defaultDescriptor);
                for (String s : fields) {
                    ColumnFamilyDescriptor descriptor = new ColumnFamilyDescriptor(s.getBytes());
                    if(curColStrs.contains(s)) {
                        curHasColumns.add(descriptor);
                    }else{
                        curDontHasColumns.add(descriptor);
                    }
                }
                for (IndexColumnNames names : indexColumnNames) {
                    if(curColStrs.contains(names.indexName)) {
                        curHasColumns.add(names.getIndexName());
                        curHasColumns.add(names.getOverAndNextName());
                    }else{
                        curDontHasColumns.add(names.getIndexName());
                        curDontHasColumns.add(names.getOverAndNextName());
                    }
                }
                //打开数据库  加载旧列族,创建新列族
                List<ColumnFamilyHandle> handleList = new ArrayList<>();
//                rocksDB = OptimisticrocksDBDB.open(new DBOptions().setCreateIfMissing(true), dataDir, curHasColumns, handleList);
                rocksDB = rocksDB.open(new DBOptions().setCreateIfMissing(true), dataDir, curHasColumns, handleList);

                for(ColumnFamilyDescriptor descriptor : curDontHasColumns) {
                    ColumnFamilyHandle handle = rocksDB.createColumnFamily(descriptor);
                    String name = new String(handle.getName());
                    handleMap.put(name, handle);
                }
                for(ColumnFamilyHandle handler : handleList){
                    String name = new String(handler.getName());
                    handleMap.put(name, handler);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 解析获取类里的所有有注解的field名和类名的拼接
     *
     * @param clazzClass
     * @return
     */
    protected final List<String> getClassCols(Class clazzClass) {
        List<String> colNames = new ArrayList<>();
        if (clazzClass.isAnnotationPresent(EntityClaz.class)) {
            EntityClaz entityClaz = (EntityClaz) clazzClass.getAnnotation(EntityClaz.class);
            String className = entityClaz.name();
            Field[] fields = clazzClass.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(FaildClaz.class)) {
                    FaildClaz faildClaz = f.getAnnotation(FaildClaz.class);
                    String fieldName = faildClaz.name();
                    colNames.add(getColName(className, fieldName));
                }
            }
        }
        return colNames;
    }

    //类名和字段名的拼接
    protected final String getColName(String className, String fieldName) {
        return className + "-" + fieldName;
    }

    //根据拼接的解析出类名和字段名
    protected final String[] getClassNameAndFieldName(String colName) {
        return colName.split("-");
    }

    public final <T> void delObj(String keyField, String fieldVale, Class<T> dtoClazz, boolean deleteCase) throws Exception {
        if (dtoClazz.isAnnotationPresent(EntityClaz.class)) {
            //获取到EntityClaz注解
            EntityClaz entityClaz = (EntityClaz) dtoClazz.getAnnotation(EntityClaz.class);
            //获取到类名
            String className = entityClaz.name();
            //所有的字段
            Field[] fields = dtoClazz.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(FaildClaz.class)) {
                    FaildClaz faildClaz = f.getAnnotation(FaildClaz.class);
                    String fieldName = faildClaz.name();
                    ColumnFamilyHandle colHandle = handleMap.get(getColName(className, fieldName));
                    if (deleteCase && (faildClaz.type() == List.class || dtoClasses.contains(faildClaz.type()))) {
                        //删除级联
                        byte[] fieldVal = getByColumnFamilyHandle(colHandle, fieldVale.getBytes());
                        if (faildClaz.type() == List.class) {
                            List list = (List) SerializeUtils.unSerialize(fieldVal);
                            if (list != null && list.size() > 0) {
                                Class listType = faildClaz.genericParadigm();
                                String conKeyF = getKeyFieldByClass(listType);
                                for (Object o : list) {
                                    String conKeyV = o instanceof byte[]?new String((byte[])o):String.valueOf(o);
                                    delObj(conKeyF, conKeyV, listType, true);
                                }
                            }
                        } else {
                            String conKeyF = getKeyFieldByClass(faildClaz.type());
                            if (conKeyF != null) {
                                delObj(conKeyF, new String(fieldVal), faildClaz.type(), true);
                            }
                        }
                    }
                    //删除列的值
                    deleteByColumnFamilyHandle(colHandle, fieldVale.getBytes());
                }
            }
        }
    }

    protected String getClassNameByClass(Class claz) {
        if (claz.isAnnotationPresent(EntityClaz.class)) {
            EntityClaz entityClaz = (EntityClaz) claz.getAnnotation(EntityClaz.class);
            return entityClaz.name();
        }
        return null;
    }

    protected String getKeyFieldByClass(Class claz) {
        if (claz.isAnnotationPresent(EntityClaz.class)) {
            Field[] fields = claz.getDeclaredFields();
            Field keyField = null;
            for (Field f : fields) {
                f.setAccessible(true);
                if (f.isAnnotationPresent(KeyField.class)) {
                    keyField = f;
                    break;
                }
            }
            if (keyField != null) {
                return keyField.getAnnotation(FaildClaz.class).name();
            }
        }
        return null;
    }
    protected <T> byte[] getKeyValByDto(T t) throws IllegalAccessException {
        Class tClas = t.getClass();
        if (tClas.isAnnotationPresent(EntityClaz.class)) {
            Field[] fields = tClas.getDeclaredFields();
            Field keyField = null;
            for (Field f : fields) {
                f.setAccessible(true);
                if (f.isAnnotationPresent(KeyField.class)) {
                    keyField = f;
                    break;
                }
            }
            if(keyField != null){
                keyField.setAccessible(true);
                Object keyVal = keyField.get(t);
                if(keyVal instanceof byte[]){
                    return (byte[])keyVal;
                }else{
                    return keyVal.toString().getBytes();
                }
            }
        }
        return null;
    }
    public final void addObjs(List objs) throws Exception {
        for (Object o : objs) {
             addObj(o);
        }
    }

    public final void addObj(Object obj) throws Exception {
        Class c = obj.getClass();
        //判断是否是dto
        if (c.isAnnotationPresent(EntityClaz.class)) {
            //获取到EntityClaz注解
            EntityClaz entityClaz = (EntityClaz) c.getAnnotation(EntityClaz.class);
            //获取到类名
            String className = entityClaz.name();
            //所有的字段
            Field[] fields = c.getDeclaredFields();
            //筛选出主键key，只能有一个
            Field keyField = null;
            for (Field f : fields) {
                if (f.isAnnotationPresent(KeyField.class)) {
                    keyField = f;
                    break;
                }
            }
            //没有主键key
            if (keyField == null) {
                throw new Exception("no key field");
            }
            //设置可以强制解析
            keyField.setAccessible(true);
            for (Field f : fields) {
                f.setAccessible(true);
                byte[] key = keyField.get(obj).toString().getBytes();
                if (keyField.getType() == byte[].class) {
                    key = (byte[]) keyField.get(obj);
                }
                //判断字段是否有字段注解，只解析有字段注解
                if (f.isAnnotationPresent(FaildClaz.class)) {
                    FaildClaz faildClaz = f.getAnnotation(FaildClaz.class);
                    f.setAccessible(true);
                    if (f.get(obj) == null) continue;
                    if (faildClaz.type() == byte[].class) {
                        byte[] value = (byte[]) f.get(obj);
                        String fieldName = faildClaz.name();
                        ColumnFamilyHandle handle = handleMap.get(getColName(className, fieldName));

                        putByColumnFamilyHandle(handle, key, value);
//                        writeBatch.put(handle,key,value);
//                        bachCount++;
                    } else if (faildClaz.type() == long.class || faildClaz.type() == Long.class
                            || faildClaz.type() == int.class || faildClaz.type() == Integer.class
                            || faildClaz.type() == String.class || faildClaz.type() == BigDecimal.class || faildClaz.type() == float.class || faildClaz.type() == Float.class) {
                        String fieldValue = f.get(obj).toString();
                        String fieldName = faildClaz.name();
                        ColumnFamilyHandle handle = handleMap.get(getColName(className, fieldName));
                        byte[] val = fieldValue.getBytes();
                        putByColumnFamilyHandle(handle, key, val);
                    } else if (faildClaz.type() == List.class) {
                        List arr = (List) f.get(obj);
                        if (arr == null) continue;
                        //主键集合
                        List value = new ArrayList();
                        String fieldName = faildClaz.name();
                        for (Object o : arr) {
                            Class itemClazz = o.getClass();
                            //外键解析只解析有注解的
                            if (itemClazz.isAnnotationPresent(EntityClaz.class)) {
                                Field[] itemFields = itemClazz.getDeclaredFields();
                                boolean hasKey = false;
                                for (Field field : itemFields) {
                                    if (field.isAnnotationPresent(KeyField.class)) {
                                        field.setAccessible(true);
                                        hasKey = true;
                                        Object item = field.get(o);
                                        //只保存对应的主键
                                        value.add(item);
                                        addObj(o);
                                    }
                                }
                                if (!hasKey) break;
                            }
                        }
                        putByColumnFamilyHandle(handleMap.get(getColName(className, fieldName)), key, SerializeUtils.serialize(value));
//                        writeBatch.put(handleMap.get(getColName(className, fieldName)), key, SerializeUtils.serialize(value));
//                        bachCount ++;
                    } else if (dtoClasses.contains(faildClaz.type())) {
                        //一对一的数据  比如block中有blockHead
                        Class contancClass = faildClaz.type();
                        if (contancClass.isAnnotationPresent(EntityClaz.class)) {
                            String fieldName = faildClaz.name();
                            //拿到对象
                            Object contanc = f.get(obj);
                            if (contanc == null) {
                                continue;
                            }
                            //判断对象是否合法   是否有主键、添加这个对象是否成功
                            Field[] contancFields = contancClass.getDeclaredFields();
                            Field contancKeyF = null;
                            for (Field contancF : contancFields) {
                                contancF.setAccessible(true);
                                if (contancF.isAnnotationPresent(KeyField.class)) {
                                    contancKeyF = contancF;
                                    break;
                                }
                            }
                            if (contancKeyF != null) {
                                contancKeyF.setAccessible(true);
                                Object val = contancKeyF.get(contanc);
                                if (val == null) {
                                    continue;
                                }
                                addObj(contanc);
                                //然后获取这个对象的主键，把他的主键保存在对应的key中
                                contancKeyF.setAccessible(true);
                                putByColumnFamilyHandle(handleMap.get(getColName(className, fieldName)), key, val instanceof byte[]?(byte[])val:val.toString().getBytes());
//                                writeBatch.put(handleMap.get(getColName(className, fieldName)), key, keyValue.getBytes());
//                                bachCount++;
                            }
                        }
                    }
                }
            }
        } else {
            throw new Exception("is not a Entity");
        }
    }

    // 添加有注解的对象
    protected final int addObj(Object obj, WriteBatch writeBatch) throws Exception {
        int bachCount = 0;
        Class c = obj.getClass();
        //判断是否是dto
        if (c.isAnnotationPresent(EntityClaz.class)) {
            //获取到EntityClaz注解
            EntityClaz entityClaz = (EntityClaz) c.getAnnotation(EntityClaz.class);
            //获取到类名
            String className = entityClaz.name();
            //所有的字段
            Field[] fields = c.getDeclaredFields();
            //筛选出主键key，只能有一个
            Field keyField = null;
            for (Field f : fields) {
                if (f.isAnnotationPresent(KeyField.class)) {
                    keyField = f;
                    break;
                }
            }
            //没有主键key
            if (keyField == null) {
                throw new Exception("no key field");
            }
            //设置可以强制解析
            keyField.setAccessible(true);
            for (Field f : fields) {
                f.setAccessible(true);
                byte[] key = keyField.get(obj).toString().getBytes();
                if (keyField.getType() == byte[].class) {
                    key = (byte[]) keyField.get(obj);
                }
                //判断字段是否有字段注解，只解析有字段注解
                if (f.isAnnotationPresent(FaildClaz.class)) {
                    FaildClaz faildClaz = f.getAnnotation(FaildClaz.class);
                    f.setAccessible(true);
                    if (f.get(obj) == null) continue;
                    if (faildClaz.type() == byte[].class) {
                        byte[] value = (byte[]) f.get(obj);
                        String fieldName = faildClaz.name();
                        ColumnFamilyHandle handle = handleMap.get(getColName(className, fieldName));
//                        putByColumnFamilyHandle(handle,key, value);
                        writeBatch.put(handle, key, value);
                        bachCount++;
                    } else if (faildClaz.type() == long.class || faildClaz.type() == Long.class
                            || faildClaz.type() == int.class || faildClaz.type() == Integer.class
                            || faildClaz.type() == String.class || faildClaz.type() == BigDecimal.class || faildClaz.type() == float.class || faildClaz.type() == Float.class) {
                        String fieldValue = f.get(obj).toString();
                        String fieldName = faildClaz.name();
                        ColumnFamilyHandle handle = handleMap.get(getColName(className, fieldName));
                        byte[] val = fieldValue.getBytes();
//                        putByColumnFamilyHandle(handle, key, val);
                        writeBatch.put(handle, key, val);
                        bachCount++;
                    } else if (faildClaz.type() == List.class) {
                        List arr = (List) f.get(obj);
                        if (arr == null) continue;
                        //主键集合
                        List value = new ArrayList();
                        String fieldName = faildClaz.name();
                        for (Object o : arr) {
                            Class itemClazz = o.getClass();
                            //外键解析只解析有注解的
                            if (itemClazz.isAnnotationPresent(EntityClaz.class)) {
                                Field[] itemFields = itemClazz.getDeclaredFields();
                                boolean hasKey = false;
                                for (Field field : itemFields) {
                                    if (field.isAnnotationPresent(KeyField.class)) {
                                        field.setAccessible(true);
                                        hasKey = true;
                                        Object item = field.get(o);
                                        //只保存对应的主键
                                        value.add(item);
                                    }
                                }
                                if (!hasKey) break;
                            }
                        }
//                        putByColumnFamilyHandle(handleMap.get(getColName(className, fieldName)), key, SerializeUtils.serialize(value));
                        writeBatch.put(handleMap.get(getColName(className, fieldName)), key, SerializeUtils.serialize(value));
                        bachCount++;
                    } else if (dtoClasses.contains(faildClaz.type())) {
                        //一对一的数据  比如block中有blockHead
                        Class contancClass = faildClaz.type();
                        if (contancClass.isAnnotationPresent(EntityClaz.class)) {
                            String fieldName = faildClaz.name();
                            //拿到对象
                            Object contanc = f.get(obj);
                            if (contanc == null) {
                                continue;
                            }
                            //判断对象是否合法   是否有主键、添加这个对象是否成功
                            Field[] contancFields = contancClass.getDeclaredFields();
                            Field contancKeyF = null;
                            for (Field contancF : contancFields) {
                                if (contancF.isAnnotationPresent(KeyField.class)) {
                                    contancKeyF = contancF;
                                    break;
                                }
                            }
                            if (contancKeyF != null) {
                                String keyValue = contancKeyF.get(contanc).toString();
                                if (keyValue == null) {
                                    continue;
                                }
                                bachCount = bachCount + addObj(contanc, writeBatch);
                                //然后获取这个对象的主键，把他的主键保存在对应的key中
                                contancKeyF.setAccessible(true);
//                                putByColumnFamilyHandle(handleMap.get(getColName(className, fieldName)), key, keyValue.getBytes());
                                writeBatch.put(handleMap.get(getColName(className, fieldName)), key, keyValue.getBytes());
                                bachCount++;
                            }
                        }
                    }
                }
            }
        } else {
            throw new Exception("is not a Entity");
        }
        return bachCount;
    }

    //获取有注解的对象
    public <T> T getObj(String keyField, Object fieldValue, Class<T> dtoClazz) throws Exception {
        byte[] fieldValByt = fieldValue instanceof byte[]?(byte[])fieldValue:fieldValue.toString().getBytes();
        //获取字段对应的列名集合
        List<String> colNames = getClassCols(dtoClazz);
        //需要返回的对象
        T t;
        //获取空参数构造方法并示例化一个对象
        Constructor<T> constructor = dtoClazz.getConstructor();
        t = constructor.newInstance();
        colNames.forEach((colName) -> {

            try {
                //该列的handler
                ColumnFamilyHandle handle = handleMap.get(colName);
                if (handle == null) {
                    return;
                }
                //获取到了该列的值
                byte[] value = getByColumnFamilyHandle(handle, fieldValByt);
                if (value == null) {
                    if (colName.contains("-" + keyField)) {
                        Field field = dtoClazz.getDeclaredField(keyField);
                        field.setAccessible(true);
                        Class typeClass = field.getType();
                        if (typeClass == String.class) {
                            field.set(t, fieldValue);
                        } else if (typeClass == byte[].class) {
                            field.set(t, fieldValByt);
                        }
                    }
                    return;
                }
                //赋值给account对象
                String fieldName = getClassNameAndFieldName(colName)[1];
                Field field = dtoClazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                if (field.isAnnotationPresent(FaildClaz.class)) {
                    FaildClaz faildClaz = field.getDeclaredAnnotation(FaildClaz.class);
                    if (faildClaz.type() == byte[].class) {
                        field.set(t, value);
                    } else if (faildClaz.type() == float.class || faildClaz.type() == Float.class) {
                        field.set(t, Float.parseFloat(new String(value)));
                    } else if (faildClaz.type() == long.class || faildClaz.type() == Long.class) {
                        field.set(t, Long.parseLong(new String(value)));
                    } else if (faildClaz.type() == int.class || faildClaz.type() == Integer.class) {
                        field.set(t, Integer.parseInt(new String(value)));
                    } else if (faildClaz.type() == String.class) {
                        field.set(t, new String(value));
                    } else if (faildClaz.type() == BigDecimal.class) {
                        field.set(t, new BigDecimal(new String(value)));
                    } else if (faildClaz.type() == List.class) {
                        List valList = new ArrayList();
                        List keyList = (List) SerializeUtils.unSerialize(value);
                        Class listGen = faildClaz.genericParadigm();
                        if (listGen == FaildClaz.class) {
                            return;
                        }
                        String keyFieldKey = null;
                        Field[] genField = listGen.getDeclaredFields();
                        for (Field f : genField) {
                            if (f.isAnnotationPresent(KeyField.class)) {
                                keyFieldKey = f.getName();
                                break;
                            }
                        }
                        if (keyField == null) {
                            return;
                        }
                        String finalKeyFieldKey = keyFieldKey;
                        keyList.forEach((k) -> {
                            try {
                                Object item = getObj(finalKeyFieldKey, k instanceof byte[]?(byte[])k:k.toString(), listGen);
                                valList.add(item);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        field.set(t, valList);
                    } else if (dtoClasses.contains(faildClaz.type())) {
                        Class contancClass = faildClaz.type();
                        Field[] contacFileds = contancClass.getDeclaredFields();
                        Field contacKeyField = null;
                        //获取主键
                        for (Field fie : contacFileds) {
                            fie.setAccessible(true);
                            if (fie.isAnnotationPresent(KeyField.class)) {
                                contacKeyField = fie;
                                break;
                            }
                        }

                        if (contacKeyField != null) {
                            //主键Faild注解的class
                            FaildClaz keyFieClas = contacKeyField.getAnnotation(FaildClaz.class);
                            String keyVal = new String(value);
                            if (keyVal != null && !"".equals(keyVal)) {
                                Object contancObj = getObj(keyFieClas.name(), value, contancClass);
                                if (contancObj != null){
                                    field.set(t, contancObj);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return t;
    }

    //索引数据的添加
    protected final void putIndexesKey(ColumnFamilyHandle handle, byte[] key, byte[] valueItem) throws RocksDBException {
        synchronized (handle) {
            long valK = Long.parseLong(new String(key));
            valK = valK / Constant.INDEX_GROUPSIZE;
            valK = valK * Constant.INDEX_GROUPSIZE;
            byte[] listByte = getByColumnFamilyHandle(handle, ("" + valK).getBytes());
            Set<String> valueList = new HashSet<>();
            if (listByte != null && listByte.length != 0) {
                valueList = (Set<String>) SerializeUtils.unSerialize(listByte);
            }
            valueList.add(new String(valueItem));
            putByColumnFamilyHandle(handle, ("" + valK).getBytes(), SerializeUtils.serialize(valueList));
        }
    }
    protected final void removeIndexesKey(ColumnFamilyHandle handle, byte[] key, byte[] valueItem){
        synchronized (handle) {
            long valK = Long.parseLong(new String(key));
            valK = valK / Constant.INDEX_GROUPSIZE;
            valK = valK * Constant.INDEX_GROUPSIZE;
            byte[] listByte = getByColumnFamilyHandle(handle, ("" + valK).getBytes());
            if(listByte == null || listByte.length == 0){
                return;
            }
            Set<String> valueList = (Set<String>)SerializeUtils.unSerialize(listByte);
            valueList.remove(new String(valueItem));
            putByColumnFamilyHandle(handle, ("" + valK).getBytes(), SerializeUtils.serialize(valueList));
        }
    }
    //索引数据的获取
    protected final Set<String> getIndexesValue(ColumnFamilyHandle handle, byte[] key) throws RocksDBException {
        long valK = Long.parseLong(new String(key));
        valK = valK / Constant.INDEX_GROUPSIZE;
        valK = valK * Constant.INDEX_GROUPSIZE;
        byte[] listByte = getByColumnFamilyHandle(handle, ("" + valK).getBytes());
        Set<String> valueList = null;
        if (listByte != null && listByte.length != 0) {
            valueList = (Set<String>) SerializeUtils.unSerialize(listByte);
        }
        return valueList;
    }

    //索引数据关系的添加
    protected final void putOverAndNext(ColumnFamilyHandle overAndNextHandle, byte[] time) throws RocksDBException, ParseException {
        synchronized (overAndNextHandle) {
            long valK = Long.parseLong(new String(time));
            valK = valK / Constant.INDEX_GROUPSIZE;
            valK = valK * Constant.INDEX_GROUPSIZE;
            time = (valK + "").getBytes();
            byte[] value = getByColumnFamilyHandle(overAndNextHandle, time);
            if (value == null || value.length == 0) {
                //只有值不存在才会存，存在的话不需要存
                long curTime = Long.parseLong(new String(time));
                //先遍历出最大的
                RocksIterator iterator = rocksDB.newIterator(overAndNextHandle);
                byte[] maxTime = null;
                byte[] maxTimeValue = null;
                for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                    byte[] overAndNextValue = iterator.value();
                    if (overAndNextValue != null && overAndNextValue.length != 0) {
                        String overAndNextStr = new String(overAndNextValue);
                        JSONObject object = JSONObject.parseObject(overAndNextStr);
                        String over = object.getString("over");
                        if (over == null || "".equals(over)) {
                            maxTime = iterator.key();
                            maxTimeValue = iterator.value();
                        }
                    }
                }
                if (maxTime == null) {
                    //还没有数据,添加第一个
                    JSONObject oneTime = new JSONObject();
                    oneTime.put("over", "");
                    oneTime.put("next", "");
                    putByColumnFamilyHandle(overAndNextHandle, time, oneTime.toJSONString().getBytes());
                }
                if (maxTime != null) {
                    //有数据
                    if (curTime < Long.parseLong(new String(maxTime))) {
                        //插入到中间某个值
                        byte[] tempKey = maxTime;
                        byte[] tempValue = maxTimeValue;
                        while (true) {
                            //循环遍历，定位我这个时间要插入到那条记录
                            JSONObject tempOverAndNext = JSONObject.parseObject(new String(tempValue));
                            String tempNext = tempOverAndNext.getString("next");
                            if (tempNext == null || "".equals(tempNext) || curTime > Long.parseLong(tempNext)) {
                                //这个tempKey是新值的over
                                if (tempNext == null || "".equals(tempNext)) {
                                    //新值是最小值
                                    JSONObject minTime = new JSONObject();
                                    minTime.put("over", new String(tempKey));
                                    minTime.put("next", "");
                                    putByColumnFamilyHandle(overAndNextHandle, time, minTime.toJSONString().getBytes());
                                    //更改最小值
                                    tempOverAndNext.put("next", curTime + "");
                                    putByColumnFamilyHandle(overAndNextHandle, tempKey, tempOverAndNext.toJSONString().getBytes());
                                } else {
                                    //中间的值
                                    //查出下一个值需要更新用
                                    byte[] nextTempKey = tempNext.getBytes();
                                    byte[] nextTempValue = getByColumnFamilyHandle(overAndNextHandle, tempNext.getBytes());
                                    //先插入新值
                                    JSONObject newTime = new JSONObject();
                                    newTime.put("over", new String(tempKey));
                                    newTime.put("next", tempNext);
                                    putByColumnFamilyHandle(overAndNextHandle, time, newTime.toJSONString().getBytes());
                                    //旧值的更新，更新两个
                                    tempOverAndNext.put("next", curTime + "");
                                    putByColumnFamilyHandle(overAndNextHandle, tempKey, tempOverAndNext.toJSONString().getBytes());
                                    JSONObject nextObj = JSONObject.parseObject(new String(nextTempValue));
                                    nextObj.put("over", curTime + "");
                                    putByColumnFamilyHandle(overAndNextHandle, nextTempKey, nextObj.toJSONString().getBytes());
                                }
                                break;
                            } else {
                                //这个tempKey不是新值的over,继续往下移动
                                tempKey = tempNext.getBytes();
                                tempValue = getByColumnFamilyHandle(overAndNextHandle, tempNext.getBytes());
                            }
                        }
                    } else {
                        //这个新值就是最大值
                        JSONObject oneTime = new JSONObject();
                        oneTime.put("over", "");
                        oneTime.put("next", new String(maxTime));
                        putByColumnFamilyHandle(overAndNextHandle, time, oneTime.toJSONString().getBytes());
                        //更新之前旧值的over
                        JSONObject oldValue = JSONObject.parseObject(new String(maxTimeValue));
                        oldValue.put("over", curTime + "");
                        putByColumnFamilyHandle(overAndNextHandle, maxTime, oldValue.toJSONString().getBytes());
                    }
                }
            }
        }
    }

    /**
     * 排序/筛选查询
     * @param fields
     * @param values
     * @param screenTypes        0 =     1 >=     2 <=
     *                           如果是0,那么比较的可以是任意的类型,如果是1或者2比较的只能是数字
     * @param tClass
     * @param overAndNextHandle
     * @param indexHandle
     * @param orderByFieldHandle
     * @param orderByType
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> getDtoListByField(List<String> fields, List<byte[]> values,
                                         List<Integer> screenTypes, Class<T> tClass,
                                         ColumnFamilyHandle overAndNextHandle, ColumnFamilyHandle indexHandle,
                                         ColumnFamilyHandle orderByFieldHandle, int orderByType) throws Exception {
        String className = getClassNameByClass(tClass);
        int flushSize = 300;
        //段判断筛选的字和字段对应的值是否匹配
        if (fields != null && values != null && screenTypes != null) {
            if (fields.size() != values.size()) {
                throw new Exception("Filter fields and values do not match.");
            }
            if (fields.size() != screenTypes.size()) {
                throw new Exception("Filter fields and values do not match.");
            }
        } else if (fields == null && values == null && screenTypes == null) {
            fields = new ArrayList<>();
            values = new ArrayList<>();
            screenTypes = new ArrayList<>();
        } else {
            throw new Exception("Filter fields and values do not match.");
        }
        //排序字段索引区间的索引
        int keysIndex = 1;
        //当页需要返回的数据
        ArrayList<T> tList = new ArrayList<>();
        //循环遍历排序字段索引的各个区间
        while (true) {
            //根据排序字段的索引区间缓存大小和区间的索引位置获取当前位置的区间    排序
            byte[][] keys = handleOrder(flushSize, keysIndex, overAndNextHandle, orderByType);
            //如果排序字段索引区间都遍历完了就退出返回
            if (keys == null || keys.length == 0 || Arrays.equals(keys, new byte[flushSize][])) {
                break;
            }
            //索引区间位置的移动
            keysIndex++;
            //遍历各个排序字段的类型的主键集合
            for (int i = 0; i < keys.length; i++) {
                byte[] suoyinKey = keys[i];
                if (suoyinKey == null) {
                    continue;
                }
                //这是改排序字段类型的主键集合
                Set<String> shaixuanSet = getIndexesValue(indexHandle, suoyinKey);
                if (shaixuanSet == null || shaixuanSet.size() == 0) {
                    continue;
                }
                //筛选主键集合，根据需要的筛选字段进行筛选，重新装入到集合
                Set<String> heightList = new HashSet<>();
                for (String heightS : shaixuanSet) {
                    byte[] bytes = heightS.getBytes();
                    boolean add = true;
                    for (int k = 0; k < fields.size(); k++) {
                        ColumnFamilyHandle handle = handleMap.get(getColName(className, fields.get(k)));
                        byte[] val = getByColumnFamilyHandle(handle, bytes);
                        byte[] value = values.get(k);

                        long val1 = 0;
                        long val2 = 0;
                        int screenType = screenTypes.get(k);
                        if (val == null || val.length == 0) {
                            if (screenType != 0) {
                                add = false;
                                break;
                            }
                            if (value != null && value.length != 0) {
                                add = false;
                                break;
                            }
                        } else {
                            switch (screenType) {
                                case 0:
                                    if (!Arrays.equals(val, value)) {
                                        add = false;
                                    }
                                    break;
                                case 1:
                                    //>=
                                    val1 = Long.parseLong(new String(val));
                                    val2 = Long.parseLong(new String(value));
                                    if (!(val1 >= val2)) {
                                        add = false;
                                    }
                                    break;
                                case 2:
                                    //<=
                                    val1 = Long.parseLong(new String(val));
                                    val2 = Long.parseLong(new String(value));
                                    if (!(val1 <= val2)) {
                                        add = false;
                                    }
                                    break;
                                default:
                                    if (!Arrays.equals(val, value)) {
                                        add = false;
                                    }
                                    break;
                            }
                            if (add == false) {
                                break;
                            }
                        }
                    }
                    if (add) {
                        heightList.add(new String(bytes));
                    }
                }
                //释放内存，这时我们只需要筛选后的集合，大的集合可以释放
                shaixuanSet = null;
                if (heightList == null || heightList.size() == 0) {
                    continue;
                }
                String[] paixuHeight = longOrder(heightList, orderByFieldHandle);
                String keyFiledName = getKeyFieldByClass(tClass);
                for (String heightByt : paixuHeight) {
                    T tObj = getObj(keyFiledName, heightByt, tClass);
                    tList.add(tObj);
                }
            }
        }
        return tList;
    }

    /**
     * 根据多字段筛选排序分页
     *
     * @param pageCount         分页的每页条数
     * @param pageNumber        分页的当前页数
     * @param indexHandle       排序字段的索引Handle
     * @param screenHands       筛选字段的Handle集合
     * @param vals              筛选字段的值集合   可以有多个值
     * @param screenType        筛选类型   0 and   1 or  （注意，要么全是and，要么全是or）
     * @param overAndNextHandle 索引字段的排序关系handle
     * @param tClass            对象的字节码
     * @param orderByType       排序类型 1升序,0降序
     * @param flushSize         排序字段区间的缓存
     * @param dtoType           对象主键的类型  根据这个类型匹配不同的排序方法
     * @param <T>               对象
     * @return 该页的数据
     * @throws Exception
     */
    public final <T> ArrayList<T> getDtoOrderByHandle(int pageCount, int pageNumber,
                                                      ColumnFamilyHandle indexHandle, List<ColumnFamilyHandle> screenHands,
                                                      List<byte[][]> vals, int screenType,
                                                      ColumnFamilyHandle overAndNextHandle, Class<T> tClass,
                                                      int orderByType, int flushSize, int dtoType, ColumnFamilyHandle orderByFieldHandle) throws Exception {
        //段判断筛选的字和字段对应的值是否匹配
        if (screenHands != null && vals != null) {
            if (screenHands.size() != vals.size()) {
                throw new Exception("Filter fields and values do not match.");
            }
        } else if (vals == null && screenHands == null) {
            screenHands = new ArrayList<>();
            vals = new ArrayList<>();
        } else {
            throw new Exception("Filter fields and values do not match.");
        }
        String keyFiledName = getKeyFieldByClass(tClass);
        //当页的数据区间的开始索引
        int beginItem = pageCount * (pageNumber - 1) + 1;
        //当页的数据区间的结束索引
        int endItem = pageCount * pageNumber;
        //排序字段索引区间的索引
        int keysIndex = 1;
        //当前页数据添加到了那个索引
        int curIndex = 0;
        //排序字段索引区间的开始索引
        int itemTempIndex = 1;
        //排序字段索引区间的结束索引
        int itemIndex = 0;
        //当页需要返回的数据
        ArrayList<T> tList = new ArrayList<>(pageCount);
        //循环遍历排序字段索引的各个区间
        while (true) {
            //根据排序字段的索引区间缓存大小和区间的索引位置获取当前位置的区间    排序
            byte[][] keys = handleOrder(flushSize, keysIndex, overAndNextHandle, orderByType);
            //如果排序字段索引区间都遍历完了就退出返回
            if (keys == null || keys.length == 0 || Arrays.equals(keys, new byte[flushSize][])) {
                break;
            }
            //索引区间位置的移动
            keysIndex++;
            //是否查询完了，进行break
            boolean ok = false;
            //遍历各个排序字段的类型的主键集合
            for (int i = 0; i < keys.length; i++) {
                byte[] suoyinKey = keys[i];
                if (suoyinKey == null) {
                    continue;
                }
                //这是改排序字段类型的主键集合
                Set<String> shaixuanSet = getIndexesValue(indexHandle, suoyinKey);
                if (shaixuanSet == null || shaixuanSet.size() == 0) {
                    continue;
                }
                //筛选主键集合，根据需要的筛选字段进行筛选，重新装入到集合
                Set<String> heightList = new HashSet<>();
                for (String bytes : shaixuanSet) {
                    boolean add = true;
                    switch (screenType) {
                        case 0:
                            add = true;
                            //and
                            for (int k = 0; k < screenHands.size(); k++) {
                                ColumnFamilyHandle handle = screenHands.get(k);
                                byte[] val = getByColumnFamilyHandle(handle, bytes.getBytes());
                                boolean valInVals = false;
                                for (byte[] inByte : vals.get(k)) {
                                    if (val == null || val.length == 0) {
                                        if (inByte == null || inByte.length == 0) {
                                            valInVals = true;
                                            break;
                                        }
                                    } else {
                                        if (Arrays.equals(val, inByte)) {
                                            valInVals = true;
                                            break;
                                        }
                                    }
                                }
                                if (!valInVals) {
                                    add = false;
                                    break;
                                }
                            }
                            break;
                        case 1:
                            //or
                            if (screenHands != null && screenHands.size() > 0) {
                                add = false;
                                for (int k = 0; k < screenHands.size(); k++) {
                                    ColumnFamilyHandle handle = screenHands.get(k);
                                    byte[] val = getByColumnFamilyHandle(handle, bytes.getBytes());
                                    boolean valInVals = false;
                                    for (byte[] inByte : vals.get(k)) {
                                        if (val == null || val.length == 0) {
                                            if (inByte == null || inByte.length == 0) {
                                                valInVals = true;
                                                break;
                                            }
                                        } else {
                                            if (Arrays.equals(val, inByte)) {
                                                valInVals = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (valInVals) {
                                        add = true;
                                        break;
                                    }
                                }
                            }
                            break;
                    }

                    if (add) {
                        heightList.add(bytes);
                    }
                }
                //释放内存，这时我们只需要筛选后的集合，大的集合可以释放
                shaixuanSet = null;
                if (heightList == null || heightList.size() == 0) {
                    continue;
                }
                itemIndex += heightList.size();
                //记录set中需要获取的那些
                int curSetBeginIndex = -1;
                int curSetEndIndex = -1;
                //判断区间位置,那些区间的数据是需要的
                if (itemIndex < beginItem) {
                    //还没到开始
                    itemTempIndex = itemIndex + 1;
                    continue;
                } else if (itemIndex >= beginItem && itemIndex <= endItem) {
                    if (itemTempIndex < beginItem) {
                        //set中部分是需要的,部分是不需要的
                        curSetBeginIndex = beginItem - itemTempIndex;
                        curSetEndIndex = itemIndex - itemTempIndex;
                    } else if (itemTempIndex >= beginItem) {
                        //set中全部都是需要的
                        curSetBeginIndex = 0;
                        curSetEndIndex = itemIndex - itemTempIndex;
                    }
                } else {
                    if (itemTempIndex < beginItem) {
                        //set中部分是需要的
                        curSetBeginIndex = beginItem - itemTempIndex;
                        curSetEndIndex = endItem - itemTempIndex;
                    } else if (itemTempIndex >= beginItem && itemTempIndex <= endItem) {
                        //set中部分是需要的
                        curSetBeginIndex = 0;
                        curSetEndIndex = endItem - itemTempIndex;
                    } else {
                        //完了  全都是不需要的
                        ok = true;
                    }
                }
                itemTempIndex = itemIndex + 1;
                if (ok) {
                    break;
                }
                if (curSetBeginIndex == -1 || curSetEndIndex == -1) {
                    continue;
                } else {
                    //todo 排序方式是以long类型排序
                    String[] paixuHeight = null;
                    if (dtoType == 0) {
                        paixuHeight = longOrder(heightList, orderByFieldHandle);

                    } else {
                        //todo 这里是不同排序方式
                    }
                    //循环需要的元素进行添加
                    for (int index = curSetBeginIndex; index <= curSetEndIndex; index++) {
                        String heightByt = paixuHeight[index];
                        T tObj = getObj(keyFiledName, heightByt, tClass);
                        tList.add(curIndex, tObj);
                        curIndex++;
                        if (curIndex == pageCount) {
                            break;
                        }
                    }
                }
            }
            //当页填满了
            if (curIndex == pageCount || ok) {
                break;
            }
        }
        return tList;
    }

    /**
     * 索引区间的获取，按照排序获取
     *
     * @param flushSize         缓存大小
     * @param fushIndex         缓存位置
     * @param overAndNextHandle 关系的handle
     * @param orderType         排序类型       1升序,0降序
     * @return
     * @throws RocksDBException
     */
    protected final byte[][] handleOrder(int flushSize, int fushIndex, ColumnFamilyHandle overAndNextHandle, int orderType) throws RocksDBException {
        //这是需要获取到的区间
        int begin = flushSize * (fushIndex - 1) + 1;
        int end = flushSize * fushIndex;
        //先获取最大/小值
        RocksIterator iterator = rocksDB.newIterator(overAndNextHandle);
        byte[] tempTime = null;
        byte[] tempTimeValue = null;
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            byte[] value = iterator.value();
            if (value != null && !"".equals(value)) {
                JSONObject valueObj = JSONObject.parseObject(new String(value));
                String overTime = "";
                if (orderType == 0) {
                    overTime = valueObj.getString("over");
                } else {
                    overTime = valueObj.getString("next");
                }
                if (overTime == null || "".equals(overTime)) {
                    tempTime = iterator.key();
                    tempTimeValue = iterator.value();
                }
            }
        }
        byte[][] result = new byte[flushSize][];
        if (tempTime == null) {
            return result;
        }
        //获取到最大/小值之后然后一个一个移动遍历
        int curIndex = 1;//这是当前索引的位置，初始是在第一个
        int resultIndex = 0;//这是获取到的数组的索引，代表当前要添加哪一个了
        while (true) {
            //判断当前元素是否在所需的区间内
            if (curIndex >= begin && curIndex <= end) {
                //如果所需的区间个数满了，就返回，break掉
                if (resultIndex >= result.length) {
                    break;
                }
                //没满就往数组的当前索引添加元素
                result[resultIndex] = tempTime;
                resultIndex++;
            } else if (curIndex > end) {
                break;
            }
            //解析当前元素
            JSONObject tempObj = JSONObject.parseObject(new String(tempTimeValue));
            //获取上一个/下一个的值
            String next = "";
            if (orderType == 0) {
                next = tempObj.getString("next");
            } else {
                next = tempObj.getString("over");
            }
            //遍历完了就返回，break掉
            if (next == null || "".equals(next)) {
                break;
            }
            //元素的下移
            tempTime = next.getBytes();
            tempTimeValue = getByColumnFamilyHandle(overAndNextHandle, tempTime);
            curIndex++;
        }
        return result;
    }

    /**
     * 把long类型的byte[]排序返回
     *
     * @param longBytes 需要排序的byte[]
     * @return
     */
    public String[] longOrder(Set<String> longBytes, ColumnFamilyHandle orderbyHandle) throws RocksDBException {
        String[] result = new String[longBytes.size()];
        result = longBytes.toArray(result);
        //冒泡排序
        for (int i = 0; i < result.length; i++) {
            String cur = result[i];
            long curVal = Long.parseLong(new String(getByColumnFamilyHandle(orderbyHandle, cur.getBytes())));
            for (int j = i + 1; j < result.length; j++) {
                byte[] place = result[j].getBytes();
                long placeHeight = Long.parseLong(new String(getByColumnFamilyHandle(orderbyHandle, place)));
                if (placeHeight > curVal) {
                    String temp = result[i];
                    result[i] = result[j];
                    result[j] = temp;
                    cur = result[i];
                    curVal = Long.parseLong(new String(getByColumnFamilyHandle(orderbyHandle, cur.getBytes())));
                }
            }
        }
        return result;
    }

    @Override
    public boolean put(byte[] key, byte[] value) {
        boolean res = false;
        try {
            rocksDB.put(key, value);
            res = true;
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean putByColumnFamilyHandle(ColumnFamilyHandle columnFamilyHandle, byte[] key, byte[] value) {
        boolean res = false;
        try {
            rocksDB.put(columnFamilyHandle, key, value);
            res = true;
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public byte[] get(byte[] key) {
        byte[] res = null;
        try {
            res = rocksDB.get(key);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public byte[] getByColumnFamilyHandle(ColumnFamilyHandle columnFamilyHandle, byte[] key) {
        byte[] res = null;
        try {
            res = rocksDB.get(columnFamilyHandle,new ReadOptions(), key);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean delete(byte[] key) {
        boolean res = false;
        try {
            rocksDB.delete(key);
            res = true;
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean deleteByColumnFamilyHandle(ColumnFamilyHandle columnFamilyHandle, byte[] key) {
        boolean res = false;
        try {
            rocksDB.delete(columnFamilyHandle, key);
            res = true;
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public <T> void addIndex(T t, IndexColumnNames columnNames,byte[] indexKey) {
        try {
//            System.out.println(getKeyValByDto(t));
            removeIndexesKey(handleMap.get(columnNames.indexName),indexKey,getKeyValByDto(t));
            putIndexesKey(handleMap.get(columnNames.indexName),indexKey,getKeyValByDto(t));

            putOverAndNext(handleMap.get(columnNames.overAndNextName),indexKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public <T> List<T> seekByKey(String keyPrefix) {
        ArrayList<T> ts = new ArrayList<>();
        ReadOptions options = new ReadOptions();
        options.setPrefixSameAsStart(true);
        RocksIterator iterator = rocksDB.newIterator(options);
        byte[] key = keyPrefix.getBytes();
        for (iterator.seek(key); iterator.isValid(); iterator.next()) {
            if (!new String(iterator.key()).startsWith(keyPrefix)) continue;
            ts.add((T) SerializeUtils.unSerialize(iterator.value()));
        }
        return ts;
    }
}
