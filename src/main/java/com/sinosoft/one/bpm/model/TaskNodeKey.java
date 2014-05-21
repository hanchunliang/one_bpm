package com.sinosoft.one.bpm.model;


public final class TaskNodeKey {
	private String processId;
	private String actorId;
	
	public static TaskNodeKey valueOf(String processId, String actorId) {
		return new TaskNodeKey(processId, actorId);
	}
	
	private TaskNodeKey(String processId, String actorId) {
		this.processId = processId;
		this.actorId = actorId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 1;
		result = prime * result + (this.processId != null ? this.processId.hashCode() : 0);
		result = prime * result +  (this.actorId != null ? this.actorId.hashCode() : 0);
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
		if (!(obj instanceof TaskNodeKey)) {
			return false;
		}
		TaskNodeKey taskNodeKey = (TaskNodeKey) obj;
		return (this.processId != null && this.processId.equals(taskNodeKey.getProcessId()))
				&& (this.actorId != null && this.actorId.equals(taskNodeKey.getActorId()));
	}
	
	@Override
	public String toString() {
		return "[" + processId + ", " + actorId + "]";
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
}
