package com.sin.java.comm.binmsg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息字段注解 <br/>
 * 
 * @author RobinTang
 * @date 2014-12-26
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MsgField {
	public enum FieldType {
		BYTE, // 单字节
		BYTES, // 字节数组

		BIG16, // 大端16位数据
		BIG16S, // 大端16位数据数组

		LIT16, // 小端16位数据
		LIT16S, // 小端16位数据数组

		MESSAGE, // 消息对象
	}

	public int offset(); // 字段位置偏移，当使用Order布局模式的时候该值为排序依据（越小越靠前）

	public FieldType type(); // 字段数据类型

	public int size() default 0; // 数据长度（字节单位），0根据实际情况写，优先级:size(!=0)>sizedepend(!="")>field.length

	public String sizedepend() default ""; // 数据长度依赖字段

	public int sizeunit() default 0; // 单个数据的字节数，size/sizeunit为数组数据个数，默认值为0（自动）
}
