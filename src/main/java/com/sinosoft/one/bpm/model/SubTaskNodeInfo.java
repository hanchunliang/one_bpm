package com.sinosoft.one.bpm.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 子任务节点信息类
 * @author carvin
 *
 */
public class SubTaskNodeInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7033148931418814050L;
	
	public static final SubTaskNodeInfo EMPTY = new SubTaskNodeInfo(); 
	private String actorId;
	private String taskName;
	private String nodeName;
	private List<String> constraints;

	private Map<String, Object> metaDataMap;
	private String comment;
	private String content;
	
	public SubTaskNodeInfo() {
		this.constraints = new LinkedList<String>();
	};

	public SubTaskNodeInfo(String actorId, String taskName, String nodeName) {
		this();
		this.actorId = actorId;
		this.taskName = taskName;
		this.nodeName = nodeName;
	}
	
	public SubTaskNodeInfo(String actorId, String taskName, String nodeName,
			Map<String, Object> metaDataMap, String comment, String content) {
		this(actorId, taskName, nodeName);
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

	public void addConstraint(String constraint) {
		this.constraints.add(0, constraint);
	}

	public List<String> getConstraints() {
		return constraints;
	}

	public Map<String, Object> getMetaDataMap() {
		return metaDataMap;
	}

	public void setMetaDataMap(Map<String, Object> metaDataMap) {
		this.metaDataMap = metaDataMap;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public NodeInfo toNodeInfo() {
		return new NodeInfo(this.getActorId(), this.getTaskName(),
				this.getNodeName(), this.getMetaDataMap(), comment, content);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 1;
		result = result * prime + (this.actorId != null ? this.actorId.hashCode() : 0);
		result = result * prime + (this.taskName != null ? this.taskName.hashCode() : 0);
		result = result * prime + (this.nodeName != null ? this.nodeName.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TaskNodeInfo)) {
			return false;
		}
		TaskNodeInfo taskNodeInfo = (TaskNodeInfo) obj;
		return (this.actorId != null && this.actorId.equals(taskNodeInfo.getActorId()))
				&& (this.taskName != null && this.taskName.equals(taskNodeInfo.getTaskName()))
				&& (this.nodeName != null && this.nodeName.equals(taskNodeInfo.getNodeName()));
	}
}
