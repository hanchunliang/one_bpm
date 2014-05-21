package com.sinosoft.one.bpm.variable;

import com.sinosoft.one.bpm.support.BpmServiceSupport;

public final class VariableHandlerFactory {
	private VariableHandlerFactory() {
		
	}
	
	public static VariableHandler buildVariableHandler(VariableType variableType, BpmServiceSupport bpmServiceSupport) {
		switch(variableType) {
		case MAP : 
			return MapVariableHandler.build(bpmServiceSupport);
		
		case LIST : 
			return ListVariableHandler.build(bpmServiceSupport);
		
		case DEFAULT : 
			return DefaultVariableHandler.build(bpmServiceSupport);
		default : 
			throw new IllegalArgumentException("Can not support the variable type [" + variableType.name() + "]");
		}
	}
}
