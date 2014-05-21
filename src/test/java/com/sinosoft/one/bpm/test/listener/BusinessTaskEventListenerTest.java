package com.sinosoft.one.bpm.test.listener;

import org.jbpm.task.event.DefaultTaskEventListener;
import org.jbpm.task.event.entity.TaskUserEvent;

import com.sinosoft.one.bpm.util.JbpmAPIUtil;

public class BusinessTaskEventListenerTest extends DefaultTaskEventListener {
	
	/**
	 * 任务完成后调用
	 */
	@Override
	public void taskCompleted(TaskUserEvent event) {
    }
	
	/**
	 * 任务完成后调用
	 */
	@Override
	public void taskStarted(TaskUserEvent event) {
    }
	
	/**
	 * 任务创建后调用
	 */
	@Override
	public void taskClaimed(TaskUserEvent event) {  
    }
}
