package com.sinosoft.one.bpm.service.spring;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.sinosoft.one.bpm.model.TaskNodeEntity;
import com.sinosoft.one.bpm.service.facade.TaskNodeInfoService;

public class TaskNodeInfoServiceSupport implements TaskNodeInfoService {
	private EntityManager entityManager;
	
	public TaskNodeInfoServiceSupport(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public void saveOrUpdateTaskNodeEntities(
			List<TaskNodeEntity> taskNodeEntities) {
//		entityManager.joinTransaction();
		for(TaskNodeEntity entity : taskNodeEntities) {
			entityManager.merge(entity);
		}
	}

	public TaskNodeEntity queryTaskNodeEntity(String processId,
			String actorId) {
		Query query = entityManager.createQuery("from TaskNodeEntity b where b.processId=? and b.actorId=?");
		query.setParameter(1, processId);
		query.setParameter(2, actorId);
		return (TaskNodeEntity) query.getSingleResult();
	}

}
