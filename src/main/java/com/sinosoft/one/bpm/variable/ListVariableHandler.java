package com.sinosoft.one.bpm.variable;

import java.util.ArrayList;
import java.util.List;

import com.sinosoft.one.bpm.aspect.GlobalVariable;
import com.sinosoft.one.bpm.support.BpmServiceSupport;
import com.sinosoft.one.bpm.util.BpmCommonUtils;

public class ListVariableHandler extends AbstractVariableHandler {

	private static volatile ListVariableHandler listVariableHandler;

	private ListVariableHandler(BpmServiceSupport bpmServiceSupport) {
		super(bpmServiceSupport);
	}

	public static ListVariableHandler build(BpmServiceSupport bpmServiceSupport) {
		if (listVariableHandler == null) {
			synchronized (ListVariableHandler.class) {
				if (listVariableHandler == null) {
					listVariableHandler = new ListVariableHandler(bpmServiceSupport);
				}
			}
		}
		return listVariableHandler;
	}

	public void handler(String variableName, 
		VariableOperate variableOperate,
		Object toOperateVariableValue) throws Exception {
		
		
		BpmCommonUtils.hasText(variableName, 
					"The name of @Variable can not be empty and null for List variable type.");
		
		Object variableValue = null;
			variableValue = bpmServiceSupport.getGlobalVariable(variableName);

		if (variableValue == null) {
			List<Object> toAddList = new ArrayList<Object>();
			toAddList.add(toOperateVariableValue);
				bpmServiceSupport.setGlobalVariable(variableName, toAddList);
			return;
		}

		if (!(variableValue instanceof List)) {
			throw new IllegalArgumentException("The variable [" + variableName
					+ "] is not a List.");
		}

		@SuppressWarnings("unchecked")
		List<Object> listVariableValue = (List<Object>) variableValue;

		switch (variableOperate) {
			case ADD: {
				listVariableValue.add(toOperateVariableValue);
				break;
			}
			case REMOVE: {
				listVariableValue.remove(toOperateVariableValue);
				break;
			}
			default: {
				throw new UnsupportedOperationException(
						"The List variable type only support ADD and REMOVE operate");
			}
		}
	}

	public void handler(Object[] args, GlobalVariable variable) throws Exception {

		Object toOperateVariableValue = getVariableValue(args, variable);
		this.handler(variable.name(),
				variable.operate(),
				toOperateVariableValue);
	}

}
