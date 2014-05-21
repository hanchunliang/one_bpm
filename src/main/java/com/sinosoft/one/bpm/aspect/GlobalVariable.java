package com.sinosoft.one.bpm.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sinosoft.one.bpm.variable.VariableOperate;
import com.sinosoft.one.bpm.variable.VariableType;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GlobalVariable {
	String name();
	VariableOperate operate() default VariableOperate.ADD;
	VariableType type() default VariableType.DEFAULT;
	String mapKey() default "";
	String variableValue() default "";
	int variableValueBeanOffset() default 0;
	String variableValueAttributeName() default "";
	
	// process instace variable need to set
	String processId() default "";
	int businessBeanOffset() default 0;
	String businessIdAttributeName() default "";
}
