package com.sinosoft.one.bpm.support;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

public final class BpmContextPool {
	private static final int SIZE = 16;
	private final Map<String, String> businessContextMap;
	private final Map<String, BpmContext> unusedBpmContextMap;
	private final Map<String, BpmContext> usedBpmContextMap;
	private int poolSize = 0;
	
	public BpmContextPool(int poolSize) {
		this.poolSize = poolSize == 0 ? SIZE : poolSize;
		unusedBpmContextMap = new ConcurrentHashMap<String, BpmContext>(this.poolSize);
		usedBpmContextMap = new ConcurrentHashMap<String, BpmContext>(this.poolSize);
		this.businessContextMap = new ConcurrentHashMap<String, String>(this.poolSize);
	}
	
	public void addBpmContext(BpmContext bpmContext) {
		unusedBpmContextMap.put(bpmContext.getId(), bpmContext);
	}
	
	public Collection<BpmContext> getBpmContextes() {
		return unusedBpmContextMap.values();
	}
	
	public int getPoolSize() {
		return this.poolSize;
	}
	
	public BpmContext getBpmContext(String businessId) {
		String bpmContextId = businessContextMap.get(businessId);
		if(!StringUtils.isBlank(bpmContextId)) {
			return usedBpmContextMap.get(bpmContextId);
		} else {
			if(!unusedBpmContextMap.isEmpty()) {
				synchronized (this) {
					Iterator<String> keyIterator = unusedBpmContextMap.keySet().iterator();
					if(keyIterator.hasNext()) {
						String key = keyIterator.next();
						BpmContext bpmContext = unusedBpmContextMap.get(key);
						
						usedBpmContextMap.put(key, bpmContext);
						businessContextMap.put(businessId, key);
						unusedBpmContextMap.remove(key);
						
						return bpmContext;
					}
				}
			}
		}
		return null;
	}
}
