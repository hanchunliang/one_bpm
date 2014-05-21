package com.sinosoft.one.bpm.test.listener;


import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;

public class BusinessProcessEventListenerTest extends
		DefaultProcessEventListener {
	@Override
	public void afterNodeLeft(ProcessNodeLeftEvent event) {
		System.out.println("==================Node " + event.getNodeInstance().getNodeName() + " left================.");
    }
	
	@Override
	public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        System.out.println("==================Node " + event.getNodeInstance().getNodeName() + " triggered================.");
    }
}
