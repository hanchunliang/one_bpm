package com.sinosoft.one.bpm.cache;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sinosoft.one.bpm.model.ProcessInstanceBOInfo;
import com.sinosoft.one.bpm.service.facade.ProcessInstanceBOService;

public class ProcessInstanceBOCache {
	private ProcessInstanceBOService processInstanceBOService;
	private static Logger logger = LoggerFactory.getLogger(ProcessInstanceBOCache.class);
	
	private Map<String, Long> processInstanceIdCache = new ConcurrentHashMap<String, Long>(16);
	private Map<Long, String> businessIdCache = new ConcurrentHashMap<Long, String>(16);
	
	public ProcessInstanceBOCache(ProcessInstanceBOService processInstanceBOService) {
		this.processInstanceBOService = processInstanceBOService;
		init();
	}
	
	public void init() {
		List<ProcessInstanceBOInfo> infoes = processInstanceBOService.getAllNormalProcessInstanceBOInfo();
		if(infoes != null && infoes.size() > 0) {
			for(ProcessInstanceBOInfo info : infoes) {
				put(info.getProcessId(), info.getBusinessId(), info.getProcessInstanceId());
			}
		}
	}
	
	public void put(String processId, String businessId, Long processInstanceId) {
		processInstanceIdCache.put(processId + "_" + businessId, processInstanceId);
		businessIdCache.put(processInstanceId, businessId);
	}
	
	public void putAndSave(String processId, String businessId, Long processInstanceId) {
		logger.info("put and save : processId : " + processId + ", processInstanceId : "  + processInstanceId + ", businessId:" + businessId);
		ProcessInstanceBOInfo info = new ProcessInstanceBOInfo();
		info.setBusinessId(businessId);
		info.setProcessId(processId);
		info.setProcessInstanceId(processInstanceId);
		info.setCreateTime(new Date());
		info.setStatus(String.valueOf(ProcessInstanceBOInfo.Status.NORMAL.ordinal()));
		processInstanceBOService.createProcessInstanceBOInfo(info);
		put(processId, businessId, processInstanceId);
	}
	
	public void putAndSaveWithNone(String processId, String businessId, Long processInstanceId) {
		logger.info("put and save : processId : " + processId + ", processInstanceId : "  + processInstanceId + ", businessId:" + businessId);
		ProcessInstanceBOInfo info = processInstanceBOService.getProcessInstanceBOInfo(processId, businessId);
				
		if(info == null) {
			info = new ProcessInstanceBOInfo();
			info.setBusinessId(businessId);
			info.setProcessId(processId);
			info.setProcessInstanceId(processInstanceId);
			info.setCreateTime(new Date());
			info.setStatus(String.valueOf(ProcessInstanceBOInfo.Status.NORMAL.ordinal()));
			processInstanceBOService.createProcessInstanceBOInfo(info);
		}
		put(processId, businessId, processInstanceId);
	}
	
	public Long getProcessInstanceId(String processId, String businessId) {
		Long value = processInstanceIdCache.get(processId + "_" + businessId);
		if(value == null) {
			synchronized (ProcessInstanceBOCache.class) {
				if(value == null) {
					logger.info("fetch from database");
					ProcessInstanceBOInfo info = processInstanceBOService.getProcessInstanceBOInfo(processId, businessId);
					value = info != null ? info.getProcessInstanceId() : null;
					if(value != null) {
						put(processId, businessId, value);
					}
				}
			}
		}
		return value;
	}
	
	public String getBusinessId(Long processInstanceId) {
		String businessId = businessIdCache.get(processInstanceId);
		if(businessId == null) {
			synchronized (ProcessInstanceBOCache.class) {
				if(businessId == null) {
					logger.info("fetch from database");
					ProcessInstanceBOInfo info = processInstanceBOService.getProcessInstanceBOInfo(processInstanceId);
					businessId = info != null ? info.getBusinessId() : null;
					if(businessId != null) {
						put(info.getProcessId(), businessId, processInstanceId);
					}
				}
			}
		}
		return businessId;
	}

	public void setProcessInstanceBOService(
			ProcessInstanceBOService processInstanceBOService) {
		this.processInstanceBOService = processInstanceBOService;
	}
	
	public void removeFromCache(String processId, long processInstanceId) {
		String businessId = businessIdCache.get(processInstanceId);
		if(StringUtils.isNotBlank(businessId)) {
			processInstanceIdCache.remove(processId + "_" + businessId);
			businessIdCache.remove(processInstanceId);
			
			processInstanceBOService.removeProcessInstanceBOInfo(processInstanceId);
			ProcessInstanceBOInfo info = processInstanceBOService.getProcessInstanceBOInfo(processInstanceId);
			if(info != null) {
				processInstanceBOService.removeProcessInstanceBOInfo(info);
			}
		}
	}
}
