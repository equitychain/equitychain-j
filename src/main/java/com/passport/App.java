package com.passport;


import java.util.Arrays;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


/**
 * @author Aron wu
 * TODO: 2018/6/20  BlockChain Passport implement Java
 */
@SpringBootApplication
public class App {

  public static void main(String[] args) {
    //enable console?
    if(Arrays.stream(args).anyMatch("console"::equalsIgnoreCase)){
      System.getProperties().setProperty("spring.shell.interactive.enabled","true");
    }else{
      System.getProperties().setProperty("spring.shell.interactive.enabled","false");
    }
    SpringApplication.run(App.class, args);
  }
}
