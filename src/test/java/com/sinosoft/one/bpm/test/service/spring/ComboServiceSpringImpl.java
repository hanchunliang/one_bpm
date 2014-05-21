package com.sinosoft.one.bpm.test.service.spring;

import ins.framework.common.Page;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinosoft.one.bpm.aspect.GetTask;
import com.sinosoft.one.bpm.aspect.ProcessTask;
import com.sinosoft.one.bpm.aspect.StartProcess;
import com.sinosoft.one.bpm.aspect.TaskParam;
import com.sinosoft.one.bpm.aspect.TaskParams;
import com.sinosoft.one.bpm.support.BpmServiceSupport;
import com.sinosoft.one.bpm.test.data.DataStore;
import com.sinosoft.one.bpm.test.data.StudentStore;
import com.sinosoft.one.bpm.test.domain.Combo;
import com.sinosoft.one.bpm.test.domain.Student;
import com.sinosoft.one.bpm.test.service.facade.ComboService;

@Service
public class ComboServiceSpringImpl implements ComboService {
    @Autowired
    private StudentStore studentStore;
    @Autowired
    private DataStore dataStore;
    @Autowired
    private BpmServiceSupport bpmServiceSupport;
	public void init() {
		System.out.println(Thread.currentThread().getName() + "--------------init");
	}

	public ComboServiceSpringImpl() {
		System.out.println(Thread.currentThread().getName() + "--------------ComboServiceSpringImpl");
	}

	/**
	 * 支持嵌套
	 */
	@SuppressWarnings("unchecked")
	@GetTask(processId="comboProcess", userIdBeanOffset=0, businessIdAttributeName = "result.comboCode", businessBeanOffset=2)
	public Page getCombos(String userId, String condation, String comboCode) {
		System.out.println(Thread.currentThread().getName() + "--------------getCombos");
		List<Combo> results = dataStore.getCombos();
		System.out.println(Thread.currentThread().getName() + "return result size:" + results.size());
		Page page = new Page();
		page.getResult().addAll(results);
		return page;
	}

	
	@GetTask(processId="comboProcess", userId = "combo004", businessIdAttributeName = "comboCode", businessBeanOffset=1)
	public List<Combo> getCombosStepFour(String condation, String comboCode) {
		System.out.println(Thread.currentThread().getName() + "--------------getCombosStepFour");
		List<Combo> results = dataStore.getCombos();
		System.out.println("return result size:" + results.size());
		return results;
	}
	
	@GetTask(processId="comboProcess", userId = "combo005", businessIdAttributeName = "comboCode", businessBeanOffset=1)
	public List<Combo> getCombosStepFive(String condation, String comboCode) {
		System.out.println(Thread.currentThread().getName() + "--------------getCombosStepFive");
		List<Combo> results = dataStore.getCombos();
		System.out.println(Thread.currentThread().getName() + "return result size:" + results.size());
		return results;
	}

	/**
	 * 简单类型的解析属性
	 */
	@ProcessTask(processId="comboProcess", userIdBeanOffset=0, businessBeanOffset = 1)
	public void processComboStepOne(String userId, String comboCode, Combo c) {
		System.out.println(Thread.currentThread().getName() + "--------------processCombo_StepOne ");
	}

	/**
	 * 简单复合类型的解析属性
	 * @throws Exception 
	 */
	@ProcessTask(processId="comboProcess", userId = "combo002", businessBeanOffset = 1, businessIdAttributeName = "comboCode")
	@TaskParams(taskParams={@TaskParam(key="isPassed", paramValueBeanOffset=2)})
	public void processComboStepTwo(String comboCode, Combo c, String isPassed) throws Exception {
		System.out.println(Thread.currentThread().getName() + "--------------processCombo_StepTwo");
//		System.out.print(((Map<String, Object>)JbpmAPIUtil.getProcessInstanceVariable("comboProcess", comboCode, "mapData")).get("one") + "++++++++++++");
	}

	/**
	 * 嵌套复合类型的解析属性
	 */
	@ProcessTask(processId="comboProcess", userId = "combo003", businessBeanOffset = 1, businessIdAttributeName = "kind.comboCode")
	@TaskParam(key="listData", paramValueBeanOffset=2)
	public void processComboStepThree(String comboCode, Combo c, List<String> listData) {
		System.out.println(Thread.currentThread().getName() + "--------------processCombo_StepThree");
	}

	/**
	 *  
	 */
	@StartProcess(processId = "comboProcess", businessBeanOffset = 1, businessIdAttributeName = "comboCode")
	@TaskParam(key="mapData", paramValueBeanOffset=2)
	public void createCombo(String comboCode, Combo c, Map<String, Object> mapData) throws Exception {
//		try {
			if(c==null) System.out.println("c==null");
			dataStore.store(c);
            studentStore.saveStudent(new Student(UUID.randomUUID().toString().replaceAll("-", ""), "carvin"));
            
//            StatefulKnowledgeSession ksession = bpmServiceSupport.getSession();
//            Map<String, Object> params = new HashMap<String, Object>();
//            params.put("businessId", comboCode);
//    		ksession.startProcess("comboProcess", params);
//    		ksession.fireAllRules();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
		System.out.println(Thread.currentThread().getName() + "------creat--------combo:" + comboCode);
	}

    public Student findStudent(String id) {
        return studentStore.findStudent(id);
    }

    @ProcessTask(processId="comboProcess", userId = "combo004", businessBeanOffset = 1, businessIdAttributeName = "comboCode")
//    @Variable(name = "listData", scope = VariableScope.PROCESSINSTANCE, variableValueBeanOffset=2, 
//	processId = "comboProcess", businessBeanOffset = 1, businessIdAttributeName = "comboCode")
	public void processComboStepFour(String comboCode, Combo c) {
		System.out.println(Thread.currentThread().getName() + "--------------processCombo_StepFour");
	}

	public Combo getCombo(String comboCode) {
		return dataStore.getCombo(comboCode);
	}

	 @ProcessTask(processId="comboProcess", userId = "combo005", businessBeanOffset = 1, businessIdAttributeName = "comboCode")
	public void processComboStepFive(String comboCode, Combo c) {
		
	}

	public void createComboPrev(String comboCode, Combo c,
			Map<String, Object> mapData) throws Exception {
		this.createCombo(comboCode, c, mapData);
	}

}
