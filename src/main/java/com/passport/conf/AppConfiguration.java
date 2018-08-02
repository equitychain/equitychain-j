package com.passport.conf;

import com.passport.utils.StoryFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.io.File;

/**
 * @author Wu Created by SKINK on 2018/6/20.
 */
@Configuration
public class AppConfiguration {
  @Value("${wallet.keystoreDir}")
  protected String keystoreDir;
  @Bean
  public ServerEndpointExporter serverEndpointExporter(){
    return new ServerEndpointExporter();
  }
  @Bean
  public StoryFileUtil storyFileUtil() throws Exception {
      return StoryFileUtil.getStoryFileUtil(new File(keystoreDir));
  }
}
