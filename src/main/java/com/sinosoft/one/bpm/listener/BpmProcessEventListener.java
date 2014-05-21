package com.sinosoft.one.bpm.listener;

import org.apache.commons.lang3.StringUtils;
import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;

import com.sinosoft.one.bpm.cache.ProcessInstanceBOCache;

public class BpmProcessEventListener extends DefaultProcessEventListener {
	private ProcessInstanceBOCache cache;
	
	public BpmProcessEventListener(ProcessInstanceBOCache cache) {
		this.cache = cache;
	}
	
	@Override
	public void afterProcessCompleted(ProcessCompletedEvent event) {
		ProcessInstance pi = event.getProcessInstance();
		String processId = pi.getProcessId();
		long processInstanceId = pi.getId();
		cache.removeFromCache(processId, processInstanceId);
	}
	
	@Override
	public void afterProcessStarted(ProcessStartedEvent event) {
		ProcessInstance pi = event.getProcessInstance();
		String businessId = "";
		if(pi instanceof WorkflowProcessInstance) {
			WorkflowProcessInstance wpi = (WorkflowProcessInstance)pi;
			businessId = (String) wpi.getVariable("businessId");
			if(StringUtils.isNotBlank(businessId)) {
				cache.putAndSave(wpi.getProcessId(), businessId, pi.getId());
			}
		}
		
	}

	public void setCache(ProcessInstanceBOCache cache) {
		this.cache = cache;
	}
}
