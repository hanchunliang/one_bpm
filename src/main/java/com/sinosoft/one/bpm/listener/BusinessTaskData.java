package com.sinosoft.one.bpm.listener;

import java.util.HashSet;
import java.util.Set;

public class BusinessTaskData {
	private String currentActorId;
	private Set<String> nextActorIds;
	
	public BusinessTaskData(String currentActorId) {
		this.currentActorId = currentActorId;
		this.nextActorIds = new HashSet<String>();
	}
	
	public void addNextActorId(String nextActorId) {
		this.nextActorIds.add(nextActorId);
	}
	
	public Set<String> getNextActorIds() {
		return nextActorIds;
	}

	public String getCurrentActorId() {
		return currentActorId;
	}
}
