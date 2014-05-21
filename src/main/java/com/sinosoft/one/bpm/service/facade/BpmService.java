package com.sinosoft.one.bpm.service.facade;

import java.util.List;
import java.util.Map;

import org.jbpm.task.query.TaskSummary;

import com.sinosoft.one.bpm.aspect.GlobalVariable;

public interface BpmService {

	long createProcess(String processId,
			Map<String, Object> params, String businessId) throws Exception;

	List<TaskSummary> getTasks(String user, String processId, String businessId) throws Exception;

	void submitTask(long taskId, String user, Map<String, Object> data, String businessId)
			throws Exception;

	void startTask(long taskId, String user, String businessId) throws Exception;

	void releaseTask(long taskId, String user, String businessId) throws Exception;

	String getBusinessId(long processInstanceId) throws Exception;

	long getTaskId(String userId, String processId, String businessId) throws Exception;
	
	void doVariable(Object[] args, GlobalVariable globalVariable) throws Exception;
	
	void doVariables(List<GlobalVariable> variableList, Object[] args)  throws Exception;
}