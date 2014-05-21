package com.sinosoft.one.bpm.test;

import ins.framework.common.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.sinosoft.one.bpm.service.facade.BpmService;
import com.sinosoft.one.bpm.test.domain.Combo;
import com.sinosoft.one.bpm.test.domain.Kind;
import com.sinosoft.one.bpm.test.service.facade.ComboService;

@DirtiesContext
@ContextConfiguration(locations = { "/spring/applicationContext-bpm.xml", "/applicationContext-test.xml" })
public class BpmServiceImplTest extends AbstractJUnit4SpringContextTests {
	
	private AtomicInteger success = new AtomicInteger();
	private AtomicInteger fail = new AtomicInteger();
	private AtomicInteger index = new AtomicInteger();

	private final int COUNT = 3;
	@Autowired
	public BpmService bpmService;
	@Autowired
	public ComboService comboService;
//	@Autowired
//	public BpmServiceSupport bpmServiceSupport;

	private Page page;
	@Test
	public void testComboProcess() throws Exception {

//		Set<String> result = bpmServiceSupport.getNextActorIds("comboProcess", "combo001");
//		System.out.println(result);
//		result = bpmServiceSupport.getNextActorIds("comboProcess", "combo002");
//		System.out.println(result);
//		result = bpmServiceSupport.getNextActorIds("comboProcess", "combo003");
//		System.out.println(result);
//		result = bpmServiceSupport.getNextActorIds("comboProcess", "combo004");
//		System.out.println(result);
//		result = bpmServiceSupport.getNextActorIds("comboProcess", "combo005");
//		System.out.println(result);
		
		
//		List<String> result = bpmServiceSupport.getNextActorIds("claim_05", "sched", new HashMap<String, Object> () {
//			{
//				put("check", "true");
//				put("nodeType", "certa");
//			}
//		});
		
//		
//		System.out.println(result);
//		Assert.assertEquals(3, result.size());
//		result = bpmServiceSupport.getNextActorIds("claim_05", "regis");
//		Assert.assertEquals(5, result.size());
//		result = bpmServiceSupport.getNextActorIds("claim_05", "sched");
//		Assert.assertEquals(6, result.size());
//		
//		result = bpmServiceSupport.getNextActorIds("claim_05", "regist_cancel", new HashMap<String, Object> () {
//			{
//				put("nodeType", "false");
//			}
//		});
//		Assert.assertEquals(1, result.size());
//		result = bpmServiceSupport.getNextActorIds("claim_05", "claim", new HashMap<String, Object> () {
//			{
//				put("nodeType", "claim");
//			}
//		});
//		Assert.assertEquals(1, result.size());
//		
//		NodeInfo currentNodeInfo = bpmServiceSupport.getCurrentNodeInfo("claim_05", "regis");
//		Assert.assertEquals("", currentNodeInfo.getMetaData("aaaaa"));
//		Assert.assertEquals("", currentNodeInfo.getMetaData("bbb"));
//		Assert.assertEquals("", currentNodeInfo.getMetaData("ddd"));
//		Assert.assertEquals("aaaaa=bbbbb, bbb=ccc,    ddd=中国    ", currentNodeInfo.getComment());
//		Assert.assertEquals("aaaa=bbbb", currentNodeInfo.getContent());
//		
//		currentNodeInfo = bpmServiceSupport.getCurrentNodeInfo("claim_05", "sched");
//		Assert.assertEquals("", currentNodeInfo.getMetaData("aaaaa"));
//		Assert.assertEquals("", currentNodeInfo.getMetaData("bbb"));
//		Assert.assertEquals("", currentNodeInfo.getMetaData("ddd"));
//		Assert.assertEquals("", currentNodeInfo.getComment());
//		Assert.assertEquals("", currentNodeInfo.getContent());
		
//		result = bpmServiceSupport.getNextActorIds("comboProcess", "combo002");
//		System.out.println(result);
//		result = bpmServiceSupport.getNextActorIds("comboProcess", "combo003");
//		System.out.println(result);
//		result = bpmServiceSupport.getNextActorIds("comboProcess", "combo004");
//		System.out.println(result);
//		result = bpmServiceSupport.getNextActorIds("comboProcess", "combo005");
//		System.out.println(result);
		
		final CountDownLatch beginLatch = new CountDownLatch(1);
		final CountDownLatch endLatch = new CountDownLatch(COUNT);
		
		 final ExecutorService exec = Executors.newFixedThreadPool(COUNT);  
		 
		 for(int i=0; i<COUNT; i++) {
			 exec.execute(new Runnable() {
				public void run() {
					try {
							beginLatch.await();
	
		        		    System.out.println("Thread name : " + Thread.currentThread().getName());
							String businessId = createCombo();
							logger.info("businessID-----" + businessId);
							getStep1(businessId);
							processStep1(businessId);
							
							getStep2(businessId);
							processStep2(businessId);
							
							getStep3(businessId);
							processStep3(businessId);
							
							getStep4(businessId);
							processStep4(businessId);
							
							getStep5(businessId);
							processStep5(businessId);
							
							success.incrementAndGet();
							
							System.out.println("Finish : " + index.incrementAndGet());
						} catch (Throwable e) {
							System.out.println(Thread.currentThread().getName() + "********************");
							e.printStackTrace();
							fail.incrementAndGet();
							throw new RuntimeException(e);
						} finally {
							endLatch.countDown();
						}
				}
			});
		 }
		 
		 beginLatch.countDown();
		 endLatch.await();
		 
		 System.out.println("total : " + COUNT + ", success : " + success.intValue() + ", fail : " + fail.intValue());
	}

	
	public String createCombo() throws Exception {
		Combo combo = new Combo();
		combo.setNo(new Random().nextInt());
		String uuid = System.currentTimeMillis() + RandomStringUtils.randomNumeric(10);
		combo.setComboCode(uuid);
		
		Kind kind = new Kind();
		kind.setKindCode(uuid);
		kind.setComboCode(uuid);
		kind.setKindName("Kind-" + uuid);
		
		combo.setKind(kind);
//		
		combo.getKind().setComboCode(combo.getComboCode());
		Map<String, Object> mapData = new HashMap<String, Object>();
		mapData.put("111", "1111");
		comboService.createCombo(combo.getComboCode(), combo, mapData);
		
		return uuid;
	}

	public String getStep1(String comboCode) {
		this.page = comboService.getCombos("combo001", "", comboCode);
		return "SUCCESS";
	}

	public void processStep1(String businessId) {
		Combo combo = comboService.getCombo(businessId);
		combo.getKind().setComboCode(combo.getComboCode());
		comboService.processComboStepOne("combo001", combo.getComboCode(), combo);
	}

	public String getStep2(String comboCode) {
		this.page = comboService.getCombos("combo002", "", comboCode);
		return "SUCCESS";
	}

	public void processStep2(String businessId) throws Exception {
		Combo combo = comboService.getCombo(businessId);
		combo.getKind().setComboCode(combo.getComboCode());
		comboService.processComboStepTwo(combo.getComboCode(), combo, "true");

	}
	
	public String getStep3(String comboCode) {
		this.page = comboService.getCombos("combo003", "", comboCode);
		return "SUCCESS";
	}

	public void processStep3(String businessId) throws Exception {
		Combo combo = comboService.getCombo(businessId);
		combo.getKind().setComboCode(combo.getComboCode());
		List<String> strList = new ArrayList<String>();
		strList.add("aaa");
		strList.add("bbb");
		comboService.processComboStepThree(combo.getComboCode(), combo, strList);

	}

	public String getStep4(String comboCode) {
		this.page = new Page();
		List<Combo> results = comboService.getCombosStepFour("", comboCode);

		for (Combo c : results) {
			page.getResult().add(c);
		}
		return "SUCCESS";
	}

	public void processStep4(String businessId) {
		Combo combo = comboService.getCombo(businessId);
		combo.getKind().setComboCode(combo.getComboCode());
		List<String> strList = new ArrayList<String>();
		strList.add("aaa");
		strList.add("bbb");
		comboService.processComboStepFour(combo.getComboCode(), combo);

	}
	
	public String getStep5(String comboCode) {
		this.page = new Page();
		List<Combo> results = comboService.getCombosStepFive("", comboCode);

		for (Combo c : results) {
			page.getResult().add(c);
		}
		return "SUCCESS";
	}
	
	public void processStep5(String businessId) {
		Combo combo = comboService.getCombo(businessId);
		combo.getKind().setComboCode(combo.getComboCode());
		List<String> listData = new ArrayList<String>() {
			{
				add("aaaa");
				add("bbbb");
			}
		};
		for(String strData : listData) {
			comboService.processComboStepFive(combo.getComboCode(), combo);
		}
	}
	

	class ExecuteProcessRunnable implements Runnable {

		public void run() {
			try {
				String businessId = createCombo();
//				System.out.println("businessId is : " + businessId);
				getStep1(businessId);
				
				processStep1(businessId);
//				
				getStep2(businessId);
				processStep2(businessId);
				
				getStep3(businessId);
				processStep3(businessId);
//				
				getStep4(businessId);
				processStep4(businessId);
				
				getStep5(businessId);
				processStep5(businessId);
				
			} catch (Throwable e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
	}
	public void setComboService(ComboService comboService) {
		this.comboService = comboService;
	}

}
