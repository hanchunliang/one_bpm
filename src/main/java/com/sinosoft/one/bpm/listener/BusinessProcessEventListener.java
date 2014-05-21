package com.sinosoft.one.bpm.listener;

import java.util.List;

import org.drools.event.process.ProcessEventListener;

public class BusinessProcessEventListener {
	
	private List<ProcessEventListener> processEventListeners;

	public List<ProcessEventListener> getProcessEventListeners() {
		return processEventListeners;
	}

	public void setProcessEventListeners(
			List<ProcessEventListener> processEventListeners) {
		this.processEventListeners = processEventListeners;
	}
}
