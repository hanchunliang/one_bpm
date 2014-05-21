package com.sinosoft.one.bpm.support;

import java.util.UUID;

import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.task.TaskService;

public class BpmContext {
	private String id = "";
	private StatefulKnowledgeSession session;
	private TaskService taskService;
	private Environment environment;
	private boolean used;
	
	public BpmContext() {
		this.id = UUID.randomUUID().toString();
	}
	
	public StatefulKnowledgeSession getSession() {
		return session;
	}
	public void setSession(StatefulKnowledgeSession session) {
		this.session = session;
	}
	public TaskService getTaskService() {
		return taskService;
	}
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	public Environment getEnvironment() {
		return environment;
	}
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	public String getId() {
		return id;
	}
}
