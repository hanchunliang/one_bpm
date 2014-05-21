package com.sinosoft.one.bpm.support;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.task.TaskService;

public final class BpmKSessionTaskServicePool {
	private static final int SIZE = 16;
	private final Map<Integer, StatefulKnowledgeSession> ksessionMap;
	private final Map<Integer, TaskService> taskServiceMap;
	private final Map<Integer, Environment> envMap;
	private int poolSize = 0;
	
	public BpmKSessionTaskServicePool(int poolSize) {
		this.poolSize = poolSize == 0 ? SIZE : poolSize;
		this.ksessionMap = new ConcurrentHashMap<Integer, StatefulKnowledgeSession>(this.poolSize);
		this.taskServiceMap = new ConcurrentHashMap<Integer, TaskService>(this.poolSize);
		this.envMap = new ConcurrentHashMap<Integer, Environment>(this.poolSize);
	}
	
	public int getPoolSize() {
		return this.poolSize;
	}
	
	
	public void addKSession(Integer key, StatefulKnowledgeSession ksession) {
		ksessionMap.put(key, ksession);
	}
	
	public void addTaskService(Integer key, TaskService taskService) {
		taskServiceMap.put(key, taskService);
	}
	
	public void addEnvironment(Integer key, Environment environment) {
		envMap.put(key, environment);
	}
	
	public StatefulKnowledgeSession getKSession(String businessId) {
		int key = Math.abs((businessId.hashCode() % poolSize));
		return ksessionMap.get(key);
	}
	
	public TaskService getTaskService(String businessId) {
		int key = Math.abs((businessId.hashCode() % poolSize));
		return taskServiceMap.get(key);
	}
	
	public Environment getEnvironment(String businessId) {
		int key = Math.abs((businessId.hashCode() % poolSize));
		return envMap.get(key);
	}
	
	
	public Collection<StatefulKnowledgeSession> getKSessions() {
		return ksessionMap.values();
	}
}
