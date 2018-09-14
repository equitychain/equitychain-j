package com.passport.db.dbhelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;
import com.passport.utils.SerializeUtils;
import org.rocksdb.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class BaseDBAccess implements DBAccess {
    protected RocksDB rocksDB;
    //列的handler
    protected final Map<String, ColumnFamilyHandle> handleMap = new HashMap<>();
    protected final Set<Class> dtoClasses = new HashSet<>();
    protected abstract void initDB();

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

    // 添加有注解的对象
    protected final void addObj(Object obj) throws Exception {
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
                        rocksDB.put(handle, key, value);
                    } else if (faildClaz.type() == long.class || faildClaz.type() == Long.class
                            || faildClaz.type() == int.class || faildClaz.type() == Integer.class
                            || faildClaz.type() == String.class || faildClaz.type() == BigDecimal.class || faildClaz.type() == float.class || faildClaz.type() == Float.class) {
                        String fieldValue = f.get(obj).toString();
                        String fieldName = faildClaz.name();
                        ColumnFamilyHandle handle = handleMap.get(getColName(className, fieldName));
                        byte[] val = fieldValue.getBytes();
                        rocksDB.put(handle, key, val);
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
                        rocksDB.put(handleMap.get(getColName(className, fieldName)), key, SerializeUtils.serialize(value));
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
                                addObj(contanc);
                                //然后获取这个对象的主键，把他的主键保存在对应的key中
                                contancKeyF.setAccessible(true);
                                rocksDB.put(handleMap.get(getColName(className, fieldName)), key, keyValue.getBytes());
                            }
                        }
                    }
                }
            }
        } else {
            throw new Exception("is not a Entity");
        }
    }

    //获取有注解的对象
    protected final <T> T getObj(String keyField, String fieldValue, Class<T> dtoClazz) throws Exception {
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
                byte[] value = rocksDB.get(handle, fieldValue.getBytes());
                if (value == null) {
                    if (colName.contains("-" + keyField)) {
                        Field field = dtoClazz.getDeclaredField(keyField);
                        field.setAccessible(true);
                        Class typeClass = field.getType();
                        if (typeClass == String.class) {
                            field.set(t, fieldValue);
                        } else if (typeClass == byte[].class) {
                            field.set(t, fieldValue.getBytes());
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
                            String keyFieldValue = k.toString();
                            try {
                                Object item = getObj(finalKeyFieldKey, keyFieldValue, listGen);
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
                                Object contancObj = getObj(keyFieClas.name(), keyVal, contancClass);
                                if (contancObj != null) field.set(t, contancObj);
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
    long gropSize = 3600*1000;
    //索引数据的添加
    protected final void putSuoyinKey(ColumnFamilyHandle handle, byte[] key, byte[] valueItem) throws RocksDBException {
        long valK = Long.parseLong(new String(key));
        valK = valK/gropSize;
        valK = valK*gropSize;
        byte[] listByte = rocksDB.get(handle, (""+valK).getBytes());
        Set<byte[]> valueList = new HashSet<>();
        if (listByte != null && listByte.length != 0) {
            valueList = (Set<byte[]>) SerializeUtils.unSerialize(listByte);
        }
        valueList.add(valueItem);
        rocksDB.put(handle, (""+valK).getBytes(), SerializeUtils.serialize(valueList));
    }

    //索引数据的获取
    protected final Set<byte[]> getSuoyinValue(ColumnFamilyHandle handle, byte[] key) throws RocksDBException {
        long valK = Long.parseLong(new String(key));
        valK = valK/gropSize;
        valK = valK*gropSize;
        byte[] listByte = rocksDB.get(handle, (""+valK).getBytes());
        Set<byte[]> valueList = null;
        if (listByte != null && listByte.length != 0) {
            valueList = (Set<byte[]>) SerializeUtils.unSerialize(listByte);
        }
        return valueList;
    }

    //索引数据关系的添加
    protected final void putOverAndNext(ColumnFamilyHandle overAndNextHandle, byte[] time) throws RocksDBException, ParseException {
        long valK = Long.parseLong(new String(time));
        valK = valK/gropSize;
        valK = valK*gropSize;
        time = (valK+"").getBytes();
        byte[] value = rocksDB.get(overAndNextHandle, time);
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
                rocksDB.put(overAndNextHandle, time, oneTime.toJSONString().getBytes());
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
                                rocksDB.put(overAndNextHandle, time, minTime.toJSONString().getBytes());
                                //更改最小值
                                tempOverAndNext.put("next", curTime + "");
                                rocksDB.put(overAndNextHandle, tempKey, tempOverAndNext.toJSONString().getBytes());
                            } else {
                                //中间的值
                                //查出下一个值需要更新用
                                byte[] nextTempKey = tempNext.getBytes();
                                byte[] nextTempValue = rocksDB.get(overAndNextHandle, tempNext.getBytes());
                                //先插入新值
                                JSONObject newTime = new JSONObject();
                                newTime.put("over", new String(tempKey));
                                newTime.put("next", tempNext);
                                rocksDB.put(overAndNextHandle, time, newTime.toJSONString().getBytes());
                                //旧值的更新，更新两个
                                tempOverAndNext.put("next", curTime + "");
                                rocksDB.put(overAndNextHandle, tempKey, tempOverAndNext.toJSONString().getBytes());
                                JSONObject nextObj = JSONObject.parseObject(new String(nextTempValue));
                                nextObj.put("over", curTime + "");
                                rocksDB.put(overAndNextHandle, nextTempKey, nextObj.toJSONString().getBytes());
                            }
                            break;
                        } else {
                            //这个tempKey不是新值的over,继续往下移动
                            tempKey = tempNext.getBytes();
                            tempValue = rocksDB.get(overAndNextHandle, tempNext.getBytes());
                        }
                    }
                } else {
                    //这个新值就是最大值
                    JSONObject oneTime = new JSONObject();
                    oneTime.put("over", "");
                    oneTime.put("next", new String(maxTime));
                    rocksDB.put(overAndNextHandle, time, oneTime.toJSONString().getBytes());
                    //更新之前旧值的over
                    JSONObject oldValue = JSONObject.parseObject(new String(maxTimeValue));
                    oldValue.put("over", curTime + "");
                    rocksDB.put(overAndNextHandle, maxTime, oldValue.toJSONString().getBytes());
                }
            }
        }
    }

    /**
     * 根据多字段筛选排序分页
     *
     * @param pageCount         分页的每页条数
     * @param pageNumber        分页的当前页数
     * @param indexHandle       排序字段的索引Handle
     * @param screenHands     筛选字段的Handle集合
     * @param vals              筛选字段的值集合   可以有多个值
     * @param screenType        筛选类型   0 and   1 or  （注意，要么全是and，要么全是or）
     * @param overAndNextHandle 索引字段的排序关系handle
     * @param tClass            对象的字节码
     * @param keyFiledName      主键字段的名
     * @param orderByType       排序类型 1升序,0降序
     * @param flushSize         排序字段区间的缓存
     * @param dtoType           对象主键的类型  根据这个类型匹配不同的排序方法
     * @param <T>               对象
     * @return                  该页的数据
     * @throws Exception
     */
    protected final <T> ArrayList<T> getDtoOrderByHandle(int pageCount, int pageNumber,
                                                         ColumnFamilyHandle indexHandle, List<ColumnFamilyHandle> screenHands,
                                                         List<byte[][]> vals,int screenType,
                                                         ColumnFamilyHandle overAndNextHandle, Class<T> tClass, String keyFiledName,
                                                         int orderByType, int flushSize, int dtoType,ColumnFamilyHandle orderByFieldHandle) throws Exception {
        //段判断筛选的字和字段对应的值是否匹配
        if (screenHands != null && vals != null) {
            if(screenHands.size() != vals.size()) {
                throw new Exception("Filter fields and values do not match.");
            }
        } else if (vals == null && screenHands == null) {
            screenHands = new ArrayList<>();
            vals = new ArrayList<>();
        } else {
            throw new Exception("Filter fields and values do not match.");
        }
        long begin = System.currentTimeMillis();
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
                Set<byte[]> shaixuanSet = getSuoyinValue(indexHandle, suoyinKey);
                if (shaixuanSet == null || shaixuanSet.size() == 0) {
                    continue;
                }
                //筛选主键集合，根据需要的筛选字段进行筛选，重新装入到集合
                Set<byte[]> heightList = new HashSet<>();
                for (byte[] bytes : shaixuanSet) {
                    boolean add = true;
                    switch (screenType){
                        case 0:
                            add = true;
                            //and
                            for (int k = 0; k < screenHands.size(); k++) {
                                ColumnFamilyHandle handle = screenHands.get(k);
                                byte[] val = rocksDB.get(handle, bytes);
                                boolean valInVals = false;
                                for(byte[] inByte : vals.get(k)){
                                    if(val == null || val.length == 0){
                                        if(inByte == null || inByte.length == 0){
                                            valInVals = true;
                                            break;
                                        }
                                    }else {
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
                            if(screenHands != null && screenHands.size() > 0) {
                                add = false;
                                for (int k = 0; k < screenHands.size(); k++){
                                    ColumnFamilyHandle handle = screenHands.get(k);
                                    byte[] val = rocksDB.get(handle, bytes);
                                    boolean valInVals = false;
                                    for(byte[] inByte : vals.get(k)){
                                        if(val == null || val.length == 0) {
                                            if(inByte == null || inByte.length == 0){
                                                valInVals = true;
                                                break;
                                            }
                                        }else{
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
                    byte[][] paixuHeight = null;
                    if (dtoType == 0) {
                        paixuHeight = longOrder(heightList,orderByFieldHandle);

                    } else {
                        //todo 这里是不同排序方式
                    }
                    //循环需要的元素进行添加
                    for (int index = curSetBeginIndex; index <= curSetEndIndex; index++) {
                        byte[] heightByt = paixuHeight[index];
                        T tObj = getObj(keyFiledName, new String(heightByt), tClass);
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
        long end = System.currentTimeMillis();
        System.out.println("查询耗时:" + (end - begin));
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
            tempTimeValue = rocksDB.get(overAndNextHandle, tempTime);
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
    public byte[][] longOrder(Set<byte[]> longBytes,ColumnFamilyHandle orderbyHandle) throws RocksDBException {
        byte[][] result = new byte[longBytes.size()][];
        result = longBytes.toArray(result);
        //冒泡排序
        for (int i = 0; i < result.length; i++) {
            byte[] cur = result[i];
            long curVal = Long.parseLong(new String(rocksDB.get(orderbyHandle,cur)));
            for (int j = i + 1; j < result.length; j++) {
                byte[] place = result[j];
                long placeHeight = Long.parseLong(new String(rocksDB.get(orderbyHandle,place)));
                if (placeHeight > curVal) {
                    byte[] temp = result[i];
                    result[i] = result[j];
                    result[j] = temp;
                    cur = result[i];
                    curVal =  Long.parseLong(new String(rocksDB.get(orderbyHandle,cur)));
                }
            }
        }
        return result;
    }

}
