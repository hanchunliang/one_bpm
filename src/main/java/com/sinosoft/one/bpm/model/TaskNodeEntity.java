package com.sinosoft.one.bpm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSON;

@Entity
public class TaskNodeEntity {
	private String id;
	private String processId;
	private String actorId;
	private byte[] taskNodeInfoBytes;
	
	@Id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name = "processId")
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	@Column(name = "actorId")
	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	
	@Lob
	@Column(name = "taskNodeInfo")
	public byte[] getTaskNodeInfoBytes() {
		return taskNodeInfoBytes;
	}
	public void setTaskNodeInfoBytes(byte[] taskNodeInfoBytes) {
		this.taskNodeInfoBytes = taskNodeInfoBytes;
	}
	
	@Transient
	public TaskNodeInfo getTaskNodeInfo() {
		return JSON.parseObject(this.taskNodeInfoBytes, TaskNodeInfo.class);
	}
	
	public void setTaskNodeInfo(TaskNodeInfo taskNodeInfo) {
		this.taskNodeInfoBytes = JSON.toJSONBytes(taskNodeInfo);
	}
}
