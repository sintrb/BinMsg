package com.sin.java.comm.binmsg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息类注解，只有该注解的对象能被序列化<br/>
 * 
 * @author RobinTang
 * @date 2014-12-26
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MsgBody {

}
