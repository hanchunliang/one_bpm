package com.sinosoft.one.bpm.service.spring;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.sinosoft.one.bpm.model.ProcessInstanceBOInfo;
import com.sinosoft.one.bpm.service.facade.ProcessInstanceBOService;

public class ProcessInstanceBOServiceSupport implements ProcessInstanceBOService{
	private EntityManager em;
	private boolean useJTA;
    private static Logger logger = LoggerFactory.getLogger(ProcessInstanceBOServiceSupport.class);
	
	public ProcessInstanceBOServiceSupport(EntityManager em, boolean useJTA) {
		this.em = em;
		this.useJTA = useJTA;
	}
	
	
	public ProcessInstanceBOInfo getProcessInstanceBOInfo(
			String processId, String businessId) {
		ProcessInstanceBOInfo result = null;
		try {
	        Query query = em.createNamedQuery("ProcessInstanceBOInfoForProcessIdAndBusinessId");
	        query.setParameter("processId", processId);
	        query.setParameter("businessId", businessId);
	        result = (ProcessInstanceBOInfo) query.getSingleResult();
		} catch(NoResultException exception) {
			logger.warn("businessId : " + businessId + ", processId : " + processId);
			logger.warn(exception.getLocalizedMessage());
		} 
		return result;
	}
	
	

	public void createProcessInstanceBOInfo(final ProcessInstanceBOInfo info) {
		if(useJTA && TransactionSynchronizationManager.isActualTransactionActive()) {
			em.joinTransaction();
		}
		em.persist(info); 
	}

	public ProcessInstanceBOInfo getProcessInstanceBOInfo(long processInstanceId) {
		ProcessInstanceBOInfo result = null;
		Query query = null;
		try {
	        query = em.createNamedQuery("ProcessInstanceBOInfoForProcessInstanceId");
	        query.setParameter("processInstanceId", processInstanceId);
	        result = (ProcessInstanceBOInfo)  query.getSingleResult();
		} catch(NoResultException exception) {
			logger.warn(exception.getLocalizedMessage());
		} catch (NonUniqueResultException e) {
			logger.warn(e.getLocalizedMessage());
			result = (ProcessInstanceBOInfo) query.getResultList().get(0);
		}
		return result;
	}

	public void removeProcessInstanceBOInfo(final ProcessInstanceBOInfo info) {
		info.setModifyTime(new Date());
		info.setStatus(String.valueOf(ProcessInstanceBOInfo.Status.REMOVE.ordinal()));
		em.merge(info);
	}
	
	public void removeProcessInstanceBOInfo(final long piId) {
		String hqlString = "update ProcessInstanceBOInfo p set p.modifyTime=?, p.status=? where p.processInstanceId=?";
		Query query = em.createQuery(hqlString);
		query.setParameter(1, new Date());
		query.setParameter(2, String.valueOf(ProcessInstanceBOInfo.Status.REMOVE.ordinal()));
		query.setParameter(3, piId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<ProcessInstanceBOInfo> getAllNormalProcessInstanceBOInfo() {
		List<ProcessInstanceBOInfo> result = null;
		try {
	        Query query = em.createNamedQuery("AllNormalProcessInstanceBOInfoes");
	        result = query.getResultList();
		} catch(NoResultException exception) {
			logger.warn(exception.getLocalizedMessage());
		}
		return result;
	}


	public BigDecimal queryProcessInstanceIdByTaskId(long taskId) {
		String sqlString = "SELECT processInstanceId FROM task WHERE id=?";
		Query query = em.createNativeQuery(sqlString);
		query.setParameter(1, taskId);
		return (BigDecimal) query.getSingleResult();
	}
}
