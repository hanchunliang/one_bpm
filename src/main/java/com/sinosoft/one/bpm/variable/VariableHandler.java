package com.sinosoft.one.bpm.variable;

import com.sinosoft.one.bpm.aspect.GlobalVariable;

public interface VariableHandler {
	void handler(Object[] args, GlobalVariable variable) throws Exception;
}
