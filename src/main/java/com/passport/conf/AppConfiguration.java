package com.passport.conf;

<<<<<<< HEAD
import com.passport.utils.StoryFileUtil;
import org.springframework.beans.factory.annotation.Value;
=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

<<<<<<< HEAD
import java.io.File;

=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
/**
 * @author Wu Created by SKINK on 2018/6/20.
 */
@Configuration
public class AppConfiguration {
<<<<<<< HEAD
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
=======

  @Bean
  public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4

}
