package com.sinosoft.one.bpm.test.data;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sinosoft.one.bpm.test.domain.Combo;

public class DataStoreImpl extends HibernateDaoSupport implements DataStore {

	public void store(Combo c) throws Exception {
//		this.getHibernateTemplate().executeWithNewSession(new HibernateCallback<Object>() {
//
//			public Object doInHibernate(Session session) throws HibernateException,
//					SQLException {
//				session.doWork(new Work() {
//					public void execute(Connection connection) throws SQLException {
//						PreparedStatement statement = connection.prepareStatement("sql ");
//						
//					}
//				});
//				return null;
//			}
//			
//		});
		this.getHibernateTemplate().save(c);
	}

	@SuppressWarnings("unchecked")
	public List<Combo> getCombos() {
		return this.getHibernateTemplate().find("from Combo");
	}
	
	public Combo getCombo(String comboCode) {
		return this.getHibernateTemplate().get(Combo.class, comboCode);
	}

}
