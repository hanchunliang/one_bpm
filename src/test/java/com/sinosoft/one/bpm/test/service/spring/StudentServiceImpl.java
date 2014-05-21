package com.sinosoft.one.bpm.test.service.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinosoft.one.bpm.test.data.StudentStore;
import com.sinosoft.one.bpm.test.domain.Student;
import com.sinosoft.one.bpm.test.service.facade.StudentService;

/**
 * Created with IntelliJ IDEA.
 * User: carvin
 * Date: 12-8-16
 * Time: 下午3:08
 * To change this template use File | Settings | File Templates.
 */
@Service
public class StudentServiceImpl implements StudentService{

    @Autowired
    private StudentStore studentStore;
    public void saveStudent(Student student) {
       studentStore.saveStudent(student);
    }
}
