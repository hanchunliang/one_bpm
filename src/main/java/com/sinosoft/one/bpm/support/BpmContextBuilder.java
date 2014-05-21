package com.sinosoft.one.bpm.support;

import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.drools.KnowledgeBase;
import org.drools.event.process.ProcessEventListener;
import org.drools.impl.EnvironmentFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.TaskService;
import org.jbpm.task.utils.OnErrorAction;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import com.sinosoft.one.bpm.listener.BusinessProcessEventListener;


public class BpmContextBuilder {
		private KnowledgeBase kbase;
		private TaskService taskService;
		private Environment env;
		private BusinessProcessEventListener businessProcessEventListener;
		private KnowledgeSessionConfiguration conf;
		private AbstractPlatformTransactionManager txManager;
		private EntityManagerFactory emf;
		
		public BpmContextBuilder(KnowledgeBase kbase, Environment env) {
			this.kbase = kbase;
			this.env = env;
		}
		
		public BpmContextBuilder businessProcessEventListener(BusinessProcessEventListener businessProcessEventListener) {
			this.businessProcessEventListener = businessProcessEventListener;
			return this;
		}
		
		public BpmContextBuilder taskService(TaskService taskService) {
			this.taskService = taskService;
			return this;
		}
		
		public BpmContextBuilder conf(KnowledgeSessionConfiguration conf) {
			this.conf = conf;
			return this;
		}
		
		public BpmContextBuilder emf(EntityManagerFactory emf) {
			this.emf = emf;
			return this;
		}
		
		public BpmContextBuilder txManager(AbstractPlatformTransactionManager txManager) {
			this.txManager = txManager;
			return this;
		}
		
		public BpmContext build() {
			// create ksession
			StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, conf, env);
			fillKSession(ksession);
			BpmContext bpmContext = new BpmContext();
			bpmContext.setSession(ksession);
			bpmContext.setTaskService(taskService);
			bpmContext.setEnvironment(env);
			return bpmContext;
		}
		
		public BpmContext rebuild(int sessionId) {
			// create ksession
			StatefulKnowledgeSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, conf, env);
			fillKSession(ksession);
			BpmContext bpmContext = new BpmContext();
			bpmContext.setSession(ksession);
			bpmContext.setTaskService(taskService);
			bpmContext.setEnvironment(env);
			return bpmContext;
		}
		
		private void fillKSession(StatefulKnowledgeSession ksession) {
			Environment newEnvironment = EnvironmentFactory.newEnvironment();
			newEnvironment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
			newEnvironment.set(EnvironmentName.TRANSACTION_MANAGER, txManager);
			
			new JPAWorkingMemoryDbLogger(ksession);
			if(businessProcessEventListener != null) {
				List<ProcessEventListener> eventListeners = businessProcessEventListener.getProcessEventListeners();
				if(eventListeners != null && eventListeners.size() > 0) {
					for(ProcessEventListener processEventListener : eventListeners) {
						ksession.addEventListener(processEventListener);
					}
				}
			}
			
			// registe taskService
			LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler(this.taskService, ksession, OnErrorAction.RETHROW);
			humanTaskHandler.connect();
			ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
		}
}
