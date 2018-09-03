package com.passport.zookeeper;

import com.passport.constant.NodeListConstant;
import com.passport.dto.ResultDto;
import com.passport.utils.GsonUtils;
import com.passport.utils.HttpUtils;
import com.passport.utils.SHA1Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class ServiceRegistry {

  private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

  @Autowired
  private NodeListConstant nodeListConstant;

  @Value("${rpc.discoverUrl}")
  private String discoverUrl;

  @Value("${rpc.findNodeListUrl}")
  private String findNodeListUrl;

  public void register(String serviceAddress) {

    Map<String, Object> msg = new HashMap<>();
    msg.put("IP", serviceAddress);

    Map<String, Object> data = new HashMap<>();
    data.put("appno", "CIIiJYEa");
    data.put("msg", GsonUtils.toJson(msg));
    data.put("sign", SHA1Utils.encode("w48xOD1Lr3kRiJsc", GsonUtils.toJson(msg)));

    String result = HttpUtils.doPost(discoverUrl, data);
    ResultDto<List<String>> resultDto = new ResultDto<>();
    resultDto = GsonUtils.fromJson(resultDto.getClass(), result);

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
}