package com.sinosoft.one.bpm.test.data;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sinosoft.one.bpm.test.domain.Student;

/**
 * Created with IntelliJ IDEA.
 * User: carvin
 * Date: 12-8-16
 * Time: 下午3:19
 * To change this template use File | Settings | File Templates.
 */

public class StudentStoreImpl extends HibernateDaoSupport implements StudentStore {
    public void saveStudent(Student student) {
        this.getHibernateTemplate().save(student);
    }

    public Student findStudent(String id) {
        return this.getHibernateTemplate().get(Student.class, id);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
