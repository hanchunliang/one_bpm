package com.sinosoft.one.bpm.service.spring;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import com.sinosoft.one.bpm.service.facade.KSessionService;

public class KSessionServiceSupport implements KSessionService {
	
	private EntityManager entityManager;
	
	public KSessionServiceSupport(EntityManagerFactory entityManagerFactory) {
		this.entityManager = entityManagerFactory.createEntityManager();
	}
	
	@SuppressWarnings("unchecked")
	public List<BigDecimal> selectSessionIds() {
		String sqlString = "select id from SessionInfo";
		Query query = entityManager.createNativeQuery(sqlString);
		return query.getResultList();
	}

}
