package com.passport.annotations;

<<<<<<< HEAD
import org.springframework.stereotype.Service;

=======
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
<<<<<<< HEAD

/**
 * 注解服务
 * @author: xujianfeng
=======
import org.springframework.stereotype.Service;

/**
 * reg service
 *
 * @author: Bee xu
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
 * @create: 2018-07-05 17:21
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface RpcService {

  /**
<<<<<<< HEAD
   * 服务接口类
=======
   * Server interface
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
   */
  Class<?> value();
}
