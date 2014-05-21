package com.sinosoft.one.bpm.test.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: carvin
 * Date: 12-8-16
 * Time: 下午3:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "bpm_demo_course")
public class Course {
    private String id;
    private String name;
    
    
    
    public Course() {
    	
    }

    public Course(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
