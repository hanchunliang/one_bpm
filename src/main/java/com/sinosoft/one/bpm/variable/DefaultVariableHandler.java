package com.sinosoft.one.bpm.variable;

import org.apache.commons.lang3.StringUtils;

import com.sinosoft.one.bpm.aspect.GlobalVariable;
import com.sinosoft.one.bpm.support.BpmServiceSupport;

public final class DefaultVariableHandler extends AbstractVariableHandler {
	private  static volatile  DefaultVariableHandler stringVariableHandler;
	
	private DefaultVariableHandler(BpmServiceSupport bpmServiceSupport) {
		super(bpmServiceSupport);
	} 

	public static DefaultVariableHandler build(BpmServiceSupport bpmServiceSupport) {
		if(stringVariableHandler == null) {
			synchronized(DefaultVariableHandler.class) {
				if(stringVariableHandler == null) {
					stringVariableHandler = new DefaultVariableHandler(bpmServiceSupport);
				}
			}
		}
		return stringVariableHandler;
	}
	
	
	public void handler(Object[] args,
			GlobalVariable variable) throws Exception {
		String variableName = variable.name();
		if(StringUtils.isBlank(variableName)) {
			throw new IllegalArgumentException("The name of @Variable can not be empty and null for default variable type.");
		}
		
		Object variableValue = getVariableValue(args, variable);
		
		switch(variable.operate()) {
			case ADD :{
				bpmServiceSupport.setGlobalVariable(variableName, variableValue);
				break;
			}
			default : {
				throw new UnsupportedOperationException("The Default variable type only support ADD operate");
			}
		}
	}
}
