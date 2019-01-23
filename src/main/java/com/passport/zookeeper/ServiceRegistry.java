package com.passport.zookeeper;

import com.passport.constant.NodeListConstant;
import com.passport.dto.ResultDto;
import com.passport.utils.GsonUtils;
import com.passport.utils.HttpUtils;
import com.passport.utils.SHA1Utils;
<<<<<<< HEAD
=======
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务注册
 * @author: xujianfeng
 * @create: 2018-07-05 17:21
 **/
@Component
public class ServiceRegistry {
=======

@Component
public class ServiceRegistry {

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
  private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

  @Autowired
  private NodeListConstant nodeListConstant;

  @Value("${rpc.discoverUrl}")
  private String discoverUrl;

<<<<<<< HEAD
  public void register(String serviceAddress) {
    //请求参数数据组装
    Map<String, Object> msg = new HashMap<>();
    msg.put("IP", serviceAddress);

    //请求参数签名后传输
=======
  @Value("${rpc.findNodeListUrl}")
  private String findNodeListUrl;

  public void register(String serviceAddress) {

    Map<String, Object> msg = new HashMap<>();
    msg.put("IP", serviceAddress);

>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    Map<String, Object> data = new HashMap<>();
    data.put("appno", "CIIiJYEa");
    data.put("msg", GsonUtils.toJson(msg));
    data.put("sign", SHA1Utils.encode("w48xOD1Lr3kRiJsc", GsonUtils.toJson(msg)));

<<<<<<< HEAD
    //POST请求数据
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
    String result = HttpUtils.doPost(discoverUrl, data);
    ResultDto<List<String>> resultDto = new ResultDto<>();
    resultDto = GsonUtils.fromJson(resultDto.getClass(), result);

<<<<<<< HEAD
    //数据处理
    if(resultDto.getCode() == 200){
      //缓存到本地
      List<String> list = resultDto.getData();
      logger.info("注册时从discover节点取到的地址列表：{}", GsonUtils.toJson(list));
      nodeListConstant.putAll(list);
    }
  }
=======
    if (resultDto.getCode() == 200) {
      List<String> list = resultDto.getData();
      logger.info("discover node list：{}", GsonUtils.toJson(list));
      nodeListConstant.putAll(list);
    }
  }

  public List<String> findNode(String nodeIp) {

    Map<String, Object> msg = new HashMap<>();
    msg.put("IP", nodeIp);

    Map<String, Object> data = new HashMap<>();
    data.put("appno", "CIIiJYEa");
    data.put("msg", GsonUtils.toJson(msg));
    data.put("sign", SHA1Utils.encode("w48xOD1Lr3kRiJsc", GsonUtils.toJson(msg)));

    String result = HttpUtils.doPost(findNodeListUrl, data);
    ResultDto<List<String>> resultDto = new ResultDto<>();
    resultDto = GsonUtils.fromJson(resultDto.getClass(), result);
    List<String> findIpList = new ArrayList<>();

    if (resultDto.getCode() == 200) {

      List<String> list = resultDto.getData();
      for (String ip : list) {
        if (nodeListConstant.put(ip)) {
          findIpList.add(ip);
        }
      }
    }
    return findIpList;
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}