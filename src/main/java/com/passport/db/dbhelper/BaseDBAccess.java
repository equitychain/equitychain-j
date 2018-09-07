package com.passport.db.dbhelper;

import com.alibaba.fastjson.JSONObject;
import com.passport.annotations.EntityClaz;
import com.passport.annotations.FaildClaz;
import com.passport.annotations.KeyField;
import com.passport.utils.SerializeUtils;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

public abstract class BaseDBAccess implements DBAccess {
    protected RocksDB rocksDB;
    //列的handler
    protected final Map<String,ColumnFamilyHandle> handleMap = new HashMap<>();
    protected final Set<Class> dtoClasses = new HashSet<>();
    protected abstract void initDB();

    /**
     * 解析获取类里的所有有注解的field名和类名的拼接
     * @param clazzClass
     * @return
     */
    protected final List<String> getClassCols(Class clazzClass){
        List<String> colNames = new ArrayList<>();
        if(clazzClass.isAnnotationPresent(EntityClaz.class)){
            EntityClaz entityClaz = (EntityClaz)clazzClass.getAnnotation(EntityClaz.class);
            String className = entityClaz.name();
            Field[] fields = clazzClass.getDeclaredFields();
            for(Field f : fields){
                if(f.isAnnotationPresent(FaildClaz.class)){
                    FaildClaz faildClaz = f.getAnnotation(FaildClaz.class);
                    String fieldName = faildClaz.name();
                    colNames.add(getColName(className,fieldName));
                }
            }
        }
        return colNames;
    }
    //类名和字段名的拼接
    protected final String getColName(String className,String fieldName){
        return className + "-" + fieldName;
    }
    //根据拼接的解析出类名和字段名
    protected final String[] getClassNameAndFieldName(String colName){
        return colName.split("-");
    }
    // 添加有注解的对象
    protected final void addObj(Object obj) throws Exception {
        Class c = obj.getClass();
        //判断是否是dto
        if(c.isAnnotationPresent(EntityClaz.class)){
            //获取到EntityClaz注解
            EntityClaz entityClaz = (EntityClaz)c.getAnnotation(EntityClaz.class);
            //获取到类名
            String className = entityClaz.name();
            //所有的字段
            Field[] fields = c.getDeclaredFields();
            //筛选出主键key，只能有一个
            Field keyField = null;
            for(Field f : fields){
                if(f.isAnnotationPresent(KeyField.class)){
                    keyField = f;
                    break;
                }
            }
            //没有主键key
            if(keyField == null){
                throw new Exception("no key field");
            }
            //设置可以强制解析
            keyField.setAccessible(true);
            for(Field f : fields){
                f.setAccessible(true);
                //判断字段是否有字段注解，只解析有字段注解
                if(f.isAnnotationPresent(FaildClaz.class)){
                    FaildClaz faildClaz = f.getAnnotation(FaildClaz.class);
                    f.setAccessible(true);
                    if(f.get(obj) == null) continue;
                    if(faildClaz.type() == byte[].class){
                        byte[] value = (byte[])f.get(obj);
                        String fieldName = faildClaz.name();
                        rocksDB.put(handleMap.get(getColName(className,fieldName)),keyField.get(obj).toString().getBytes(),value);
                    }else if(faildClaz.type() == long.class || faildClaz.type() == Long.class
                            ||faildClaz.type() == int.class || faildClaz.type() == Integer.class
                            || faildClaz.type() == String.class || faildClaz.type() == BigDecimal.class){
                        String fieldValue = f.get(obj).toString();
                        String fieldName = faildClaz.name();
                        rocksDB.put(handleMap.get(getColName(className,fieldName)),keyField.get(obj).toString().getBytes(),fieldValue.getBytes());
                    }else if(faildClaz.type() == List.class){
                        List arr = (List) f.get(obj);
                        if(arr == null) continue;
                        //主键集合
                        List value = new ArrayList();
                        String fieldName = faildClaz.name();
                        for (Object o : arr){
                            Class itemClazz = o.getClass();
                            //外键解析只解析有注解的
                            if(itemClazz.isAnnotationPresent(EntityClaz.class)){
                                Field[] itemFields = itemClazz.getDeclaredFields();
                                boolean hasKey = false;
                                for(Field field : itemFields){
                                    if(field.isAnnotationPresent(KeyField.class)){
                                        field.setAccessible(true);
                                        hasKey = true;
                                        Object item = field.get(o);
                                        //只保存对应的主键
                                        value.add(item);
                                    }
                                }
                                if(!hasKey) break;
                            }
                        }
                        rocksDB.put(handleMap.get(getColName(className,fieldName)),keyField.get(obj).toString().getBytes(),SerializeUtils.serialize(value));
                    }else if(dtoClasses.contains(faildClaz.type())){
                        //一对一的数据  比如block中有blockHead
                        Class contancClass = faildClaz.type();
                        if(contancClass.isAnnotationPresent(EntityClaz.class)){
                            String fieldName = faildClaz.name();
                            //拿到对象
                            Object contanc = f.get(obj);
                            if(contanc == null){
                                continue;
                            }
                            //判断对象是否合法   是否有主键、添加这个对象是否成功
                            Field[] contancFields = contancClass.getDeclaredFields();
                            Field contancKeyF = null;
                            for(Field contancF : contancFields){
                                if(contancF.isAnnotationPresent(KeyField.class)){
                                    contancKeyF = contancF;
                                    break;
                                }
                            }
                            if(contancKeyF != null){
                                String keyValue = contancKeyF.get(contanc).toString();
                                if(keyValue == null){
                                    continue;
                                }
                                addObj(contanc);
                                //然后获取这个对象的主键，把他的主键保存在对应的key中
                                contancKeyF.setAccessible(true);
                                rocksDB.put(handleMap.get(getColName(className,fieldName)),keyField.get(obj).toString().getBytes(),keyValue.getBytes());
                            }
                        }
                    }
                }
            }
        }else{
            throw new Exception("is not a Entity");
        }
    }
    //获取有注解的对象
    protected final <T> T getObj(String keyField,String fieldValue,Class<T> dtoClazz) throws Exception {
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
                if(handle == null){
                    return;
                }
                //获取到了该列的值
                byte[] value = rocksDB.get(handle,fieldValue.getBytes());
                if(value == null){
                    if(colName.contains("-"+keyField)){
                        Field field = dtoClazz.getDeclaredField(keyField);
                        field.setAccessible(true);
                        field.set(t,fieldValue);
                    }
                    return;
                }
                //赋值给account对象
                String fieldName = getClassNameAndFieldName(colName)[1];
                Field field = dtoClazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                if(field.isAnnotationPresent(FaildClaz.class)) {
                    FaildClaz faildClaz = field.getDeclaredAnnotation(FaildClaz.class);
                    if(faildClaz.type() == byte[].class){
                        field.set(t,value);
                    }else if(faildClaz.type() == long.class || faildClaz.type() == Long.class){
                        field.set(t,Long.parseLong(new String(value)));
                    }else if(faildClaz.type() == int.class||faildClaz.type() == Integer.class){
                        field.set(t,Integer.parseInt(new String(value)));
                    }else if(faildClaz.type() == String.class) {
                        field.set(t, new String(value));
                    }else if(faildClaz.type() == BigDecimal.class){
                        field.set(t,new BigDecimal(new String(value)));
                    }else if(faildClaz.type() == List.class){
                        List valList = new ArrayList();
                        List keyList = (List) SerializeUtils.unSerialize(value);
                        Class listGen = faildClaz.genericParadigm();
                        if(listGen == FaildClaz.class){
                            return;
                        }
                        String keyFieldKey = null;
                        Field[] genField = listGen.getDeclaredFields();
                        for(Field f : genField){
                            if(f.isAnnotationPresent(KeyField.class)){
                                keyFieldKey = f.getName();
                                break;
                            }
                        }
                        if(keyField == null){
                            return;
                        }
                        String finalKeyFieldKey = keyFieldKey;
                        keyList.forEach((k)->{
                            String keyFieldValue = k.toString();
                            try {
                                Object item = getObj(finalKeyFieldKey,keyFieldValue,listGen);
                                valList.add(item);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        field.set(t,valList);
                    }else if(dtoClasses.contains(faildClaz.type())){
                        Class contancClass = faildClaz.type();
                        Field[] contacFileds = contancClass.getDeclaredFields();
                        Field contacKeyField = null;
                        //获取主键
                        for (Field fie : contacFileds){
                            if(fie.isAnnotationPresent(KeyField.class)){
                                contacKeyField = fie;
                                break;
                            }
                        }

                        if(contacKeyField != null) {
                            //主键Faild注解的class
                            FaildClaz keyFieClas = contacKeyField.getAnnotation(FaildClaz.class);
                            String keyVal = new String(value);
                            if(keyVal != null && !"".equals(keyVal)) {
                                Object contancObj = getObj(keyFieClas.name(), keyVal, contancClass);
                                if(contancObj != null) field.set(t, contancObj);
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
    protected final void putSuoyinKey(ColumnFamilyHandle handle, byte[] key, byte[] valueItem) throws RocksDBException {
        byte[] listByte = rocksDB.get(handle,key);
        Set<byte[]> valueList = new HashSet<>();
        if(listByte != null && listByte.length != 0){
            valueList = (Set<byte[]>) SerializeUtils.unSerialize(listByte);
        }
        valueList.add(valueItem);
        rocksDB.put(handle,key,SerializeUtils.serialize(valueList));
    }
    //索引数据的获取
    protected final Set<byte[]> getSuoyinValue(ColumnFamilyHandle handle, byte[] key) throws RocksDBException {
        byte[] listByte = rocksDB.get(handle,key);
        Set<byte[]> valueList = null;
        if(listByte != null && listByte.length != 0){
            valueList = (Set<byte[]>) SerializeUtils.unSerialize(listByte);
        }
        return valueList;
    }
    //索引数据关系的添加
    protected final void putOverAndNext(ColumnFamilyHandle overAndNextHandle,byte[] time) throws RocksDBException {
        byte[] value = rocksDB.get(overAndNextHandle,time);
        if(value == null || value.length == 0){
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
            if(maxTime != null){
                //有数据
                if(curTime < Long.parseLong(new String(maxTime))) {
                    //插入到中间某个值
                    byte[] tempKey = maxTime;
                    byte[] tempValue = maxTimeValue;
                    while (true) {
                        //循环遍历，定位我这个时间要插入到那条记录
                        JSONObject tempOverAndNext = JSONObject.parseObject(new String(tempValue));
                        String tempNext = tempOverAndNext.getString("next");
                        if (tempNext == null || "".equals(tempNext) || curTime > Long.parseLong(tempNext)) {
                            //这个tempKey是新值的over
                            if(tempNext == null || "".equals(tempNext)){
                                //新值是最小值
                                JSONObject minTime = new JSONObject();
                                minTime.put("over",new String(tempKey));
                                minTime.put("next","");
                                rocksDB.put(overAndNextHandle,time,minTime.toJSONString().getBytes());
                                //更改最小值
                                tempOverAndNext.put("next",curTime+"");
                                rocksDB.put(overAndNextHandle,tempKey,tempOverAndNext.toJSONString().getBytes());
                            }else{
                                //中间的值
                                //查出下一个值需要更新用
                                byte[] nextTempKey = tempNext.getBytes();
                                byte[] nextTempValue = rocksDB.get(overAndNextHandle,tempNext.getBytes());
                                //先插入新值
                                JSONObject newTime = new JSONObject();
                                newTime.put("over",new String(tempKey));
                                newTime.put("next",tempNext);
                                rocksDB.put(overAndNextHandle,time,newTime.toJSONString().getBytes());
                                //旧值的更新，更新两个
                                tempOverAndNext.put("next",curTime+"");
                                rocksDB.put(overAndNextHandle,tempKey,tempOverAndNext.toJSONString().getBytes());
                                JSONObject nextObj = JSONObject.parseObject(new String(nextTempValue));
                                nextObj.put("over",curTime+"");
                                rocksDB.put(overAndNextHandle,nextTempKey,nextObj.toJSONString().getBytes());
                            }
                            break;
                        }else{
                            //这个tempKey不是新值的over,继续往下移动
                            tempKey = tempNext.getBytes();
                            tempValue = rocksDB.get(overAndNextHandle,tempNext.getBytes());
                        }
                    }
                }else{
                    //这个新值就是最大值
                    JSONObject oneTime = new JSONObject();
                    oneTime.put("over","");
                    oneTime.put("next",new String(maxTime));
                    rocksDB.put(overAndNextHandle,time,oneTime.toJSONString().getBytes());
                    //更新之前旧值的over
                    JSONObject oldValue = JSONObject.parseObject(new String(maxTimeValue));
                    oldValue.put("over",curTime+"");
                    rocksDB.put(overAndNextHandle,maxTime,oldValue.toJSONString().getBytes());
                }
            }
        }
    }
    public <T> ArrayList<T> getDtoOrderByHandle(int pageCount, int pageNumber, ColumnFamilyHandle indexHandle,List<ColumnFamilyHandle> shaixuanHands,List<byte[]> vals, ColumnFamilyHandle overAndNextHandle, Class<T> tClass, String keyFiledName, int orderByType, int flushSize, int dtoType) throws Exception {
        long begin = System.currentTimeMillis();
        int beginItem = pageCount*(pageNumber-1)+1;
        int endItem = pageCount*pageNumber;
        //区间的移动
        int keysIndex = 1;
        //block[]的索引
        int curIndex = 0;
        //key时间戳的区间
        //区间的开始
        int itemTempIndex = 1;
        //区间的结束
        int itemIndex = 0;
        ArrayList<T> tList = new ArrayList<>(pageCount);
        while(true) {
            //得到排序后的key
            byte[][] keys = testIndexPaixu(flushSize, keysIndex,overAndNextHandle,orderByType);
            if(keys == null || keys.length == 0 || Arrays.equals(keys,new byte[flushSize][])){
                break;
            }
            keysIndex ++;
            boolean ok = false;
            //遍历各个时间戳的高度集合
            for (int i = 0; i < keys.length; i++) {
                byte[] suoyinKey = keys[i];
                if(suoyinKey == null) {
                    continue;
                }
                Set<byte[]> shaixuanSet = getSuoyinValue(indexHandle, suoyinKey);
                if(shaixuanSet == null || shaixuanSet.size() == 0){
                    continue;
                }
                //筛选集合
                Set<byte[]> heightList = new HashSet<>();
                for (byte[] bytes : shaixuanSet) {
                    boolean add = true;
                    for (int k =0; k < shaixuanHands.size(); k ++) {
                        ColumnFamilyHandle handle = shaixuanHands.get(k);
                        byte[] val = rocksDB.get(handle,bytes);
                        if(!Arrays.equals(val,vals.get(k))){
                            add = false;
                            break;
                        }
                    }
                    if(add){
                        heightList.add(bytes);
                    }
                }
                if(heightList == null || heightList.size() == 0){
                    continue;
                }
                itemIndex += heightList.size();
                //记录set中需要获取的那些
                int curSetBeginIndex = -1;
                int curSetEndIndex = -1;
                //判断区间位置,那些区间的数据是需要的
                if (itemIndex < beginItem) {
                    //还没到开始
                    itemTempIndex = itemIndex+1;
                    continue;
                } else if (itemIndex >= beginItem && itemIndex <= endItem) {
                    if (itemTempIndex < beginItem) {
                        //set中部分是需要的,部分是不需要的
                        curSetBeginIndex = beginItem-itemTempIndex;
                        curSetEndIndex = itemIndex - itemTempIndex;
                    } else if (itemTempIndex >= beginItem) {
                        //set中全部都是需要的
                        curSetBeginIndex = 0;
                        curSetEndIndex = itemIndex-itemTempIndex;
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
                itemTempIndex = itemIndex+1;
                if(ok){
                    break;
                }
                if(curSetBeginIndex == -1 || curSetEndIndex == -1){
                    continue;
                }else{
                    //todo 排序方式是以long类型排序
                    byte[][] paixuHeight = null;
                    if(dtoType == 0) {
                        paixuHeight = paixuLong(heightList);
                    }else {
                        paixuHeight = paixuTransTest(heightList);
                    }
                    //循环需要的元素进行添加
                    for(int index = curSetBeginIndex; index <= curSetEndIndex; index ++){
                        byte[] heightByt = paixuHeight[index];
                        T tObj = getObj(keyFiledName, new String(heightByt), tClass);
                        tList.add(curIndex,tObj);
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
        System.out.println("查询耗时:"+(end-begin));
        return tList;
    }
    /**
     *
     * @param flushSize            缓存大小
     * @param fushIndex            缓存位置
     * @param overAndNextHandle   关系的handle   handleMap.get(getColName("blockHeightIndex","overAndNext"))
     * @param orderType            排序类型       0升序,1降序
     * @return
     * @throws RocksDBException
     */
    public byte[][] testIndexPaixu(int flushSize,int fushIndex,ColumnFamilyHandle overAndNextHandle,int orderType) throws RocksDBException {
        int begin = flushSize*(fushIndex-1)+1;
        int end = flushSize*fushIndex;
        //先获取最大值
        RocksIterator iterator = rocksDB.newIterator(overAndNextHandle);
        byte[] tempTime = null;
        byte[] tempTimeValue = null;
        for(iterator.seekToFirst();iterator.isValid();iterator.next()){
            byte[] value = iterator.value();
            if(value != null && !"".equals(value)){
                JSONObject valueObj = JSONObject.parseObject(new String(value));
                String overTime = "";
                if(orderType == 0) {
                    overTime = valueObj.getString("over");
                }else{
                    overTime = valueObj.getString("next");
                }
                if(overTime == null || "".equals(overTime)){
                    tempTime = iterator.key();
                    tempTimeValue = iterator.value();
                }
            }
        }
        byte[][] result = new byte[flushSize][];
        if(tempTime == null){
            return result;
        }
        int curIndex = 1;
        int resultIndex = 0;
        while(true){
            JSONObject tempObj = JSONObject.parseObject(new String(tempTimeValue));
            String next = "";
            if(orderType == 0) {
                next = tempObj.getString("next");
            }else{
                next = tempObj.getString("over");
            }

            if(curIndex >= begin && curIndex <= end){
                if(resultIndex >= result.length){
                    break;
                }
                result[resultIndex] = tempTime;
                resultIndex ++;
            }else if (curIndex > end){
                break;
            }
            if(next == null || "".equals(next)){
                break;
            }
            tempTime = next.getBytes();
            tempTimeValue = rocksDB.get(overAndNextHandle,tempTime);
            curIndex ++;
        }
        return result;
    }
    public byte[][] paixuLong(Set<byte[]> heightSet){
        byte[][] result = new byte[heightSet.size()][];
        result = heightSet.toArray(result);
        for (int i = 0; i < result.length; i ++){
            byte[] cur = result[i];
            long curHeight = Long.parseLong(new String(cur));
            for (int j = i+1; j < result.length;j ++){
                byte[] place = result[j];
                long placeHeight = Long.parseLong(new String(place));
                if(placeHeight > curHeight){
                    byte[] temp = result[i];
                    result[i] = result[j];
                    result[j] = temp;
                    cur = result[i];
                    curHeight = Long.parseLong(new String(cur));
                }
            }
        }
        return result;
    }
    public byte[][] paixuTransTest(Set<byte[]> heightSet){
        byte[][] result = new byte[heightSet.size()][];
        result = heightSet.toArray(result);
        for (int i = 0; i < result.length; i ++){
            byte[] cur = result[i];
            long curHeight = Long.parseLong(new String(cur).substring("hash==".length()));
            for (int j = i+1; j < result.length;j ++){
                byte[] place = result[j];
                long placeHeight = Long.parseLong(new String(place).substring("hash==".length()));
                if(placeHeight > curHeight){
                    byte[] temp = result[i];
                    result[i] = result[j];
                    result[j] = temp;
                    cur = result[i];
                    curHeight = Long.parseLong(new String(cur).substring("hash==".length()));
                }
            }
        }
        return result;
    }
}
