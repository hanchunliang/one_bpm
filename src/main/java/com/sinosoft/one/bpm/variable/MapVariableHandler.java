package com.sinosoft.one.bpm.variable;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sinosoft.one.bpm.aspect.GlobalVariable;
import com.sinosoft.one.bpm.support.BpmServiceSupport;

public class MapVariableHandler extends AbstractVariableHandler {
	
	private  static volatile  MapVariableHandler mapVariableHandler;
	
	private MapVariableHandler(BpmServiceSupport bpmServiceSupport) {
		super(bpmServiceSupport);
	} 

	public static MapVariableHandler build(BpmServiceSupport bpmServiceSupport) {
		if(mapVariableHandler == null) {
			synchronized(MapVariableHandler.class) {
				if(mapVariableHandler == null) {
					mapVariableHandler = new MapVariableHandler(bpmServiceSupport);
				}
			}
		}
		return mapVariableHandler;
	}

	@SuppressWarnings("unchecked")
	public void handler(String variableName, 
			VariableOperate variableOperate,
			String mapKey, Object toOperateVariableValue) throws Exception {
		
		if(StringUtils.isBlank(variableName) || StringUtils.isBlank(mapKey)) {
			throw new IllegalArgumentException("The name and mapKey of @GlobalVariable can not be empty and null for Map variable type.");
		}
		
		Object variableValue = null;
		
		variableValue = bpmServiceSupport.getGlobalVariable(variableName);

		
		if(variableValue == null) {
			Map<String, Object> toAddMap = new HashMap<String, Object>();
			toAddMap.put(mapKey, toOperateVariableValue);
			bpmServiceSupport.setGlobalVariable(variableName, toAddMap);
			return;
		}
		
		if(!(variableValue instanceof Map)) {
			throw new IllegalArgumentException("The variable [" + variableName + "] is not a Map.");
		}
		
		Map<String, Object> mapVariableValue = (Map<String, Object>)variableValue;
		
		switch(variableOperate) {
			case ADD : {
				mapVariableValue.put(mapKey, toOperateVariableValue);
				break;
			}
			case REMOVE : {
				mapVariableValue.remove(mapKey);
				break;
			}
			default : {
				throw new UnsupportedOperationException("The Map variable type only support ADD and REMOVE operate");
			}
		}
		
	}

	public void handler(Object[] args,
			GlobalVariable variable) throws Exception {
		Object toOperateVariableValue = getVariableValue(args, variable);
		handler(variable.name(), 
				variable.operate(),
				variable.mapKey(), toOperateVariableValue);
	}

}
