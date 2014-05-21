package com.sinosoft.one.bpm.service.facade;

import java.util.List;

import com.sinosoft.one.bpm.model.TaskNodeEntity;

public interface TaskNodeInfoService {
	/**
	 * 批量保存任务节点信息
	 * @param taskNodeInfos
	 */
	void saveOrUpdateTaskNodeEntities(List<TaskNodeEntity> taskNodeEntities);
	/**
	 * 通过流程Id 和 actorId 查询任务节点信息
	 * @param processId 流程Id
	 * @param actorId 执行者Id
	 * @return
	 */
	TaskNodeEntity queryTaskNodeEntity(String processId, String actorId);

}
