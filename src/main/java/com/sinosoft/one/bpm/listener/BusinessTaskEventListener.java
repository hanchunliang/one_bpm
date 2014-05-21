package com.sinosoft.one.bpm.listener;

import java.util.List;

import org.jbpm.task.event.TaskEventListener;

public class BusinessTaskEventListener {
	private List<TaskEventListener> taskEventListeners;

	public List<TaskEventListener> getTaskEventListeners() {
		return taskEventListeners;
	}

	public void setTaskEventListeners(List<TaskEventListener> taskEventListeners) {
		this.taskEventListeners = taskEventListeners;
	}
}
