package com.sinosoft.one.bpm.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author zhujinwei
 * 
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProcessTask {
	String processId();
	
	String userId() default "";
	
	int userIdBeanOffset() default -1;
	
	String userIdAttributeName() default "";

	int businessBeanOffset();

	String businessIdAttributeName() default "";
}
