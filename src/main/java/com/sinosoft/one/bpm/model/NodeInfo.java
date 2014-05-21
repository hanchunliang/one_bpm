package com.sinosoft.one.bpm.model;

import java.util.Map;

/**
 * 节点信息
 * @author carvin
 *
 */
public class NodeInfo {
	private String actorId;
	private String taskName;
	private String nodeName;
	private Map<String, Object> metaDataMap;
	private String comment;
	private String content;
	
	public NodeInfo(String actorId, String taskName, String nodeName,
			Map<String, Object> metaDataMap, String comment, String content) {
		this.actorId = actorId;
		this.taskName = taskName;
		this.nodeName = nodeName;
		this.metaDataMap = metaDataMap;
		this.comment = comment;
		this.content = content;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public String getMetaData(String key) {
		if(metaDataMap == null) {
			return "";
		}
		if(metaDataMap.get(key) != null) {
			return String.valueOf(metaDataMap.get(key));
		}
		return "";
	}

	public String getComment() {
		return comment;
	}

	public String getContent() {
		return content;
	}
}
