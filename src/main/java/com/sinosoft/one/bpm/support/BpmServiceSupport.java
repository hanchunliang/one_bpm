package com.sinosoft.one.bpm.support;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.definition.process.WorkflowProcess;
import org.drools.event.process.ProcessEventListener;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.TaskService;
import org.jbpm.task.event.TaskEventListener;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.task.utils.OnErrorAction;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import com.sinosoft.one.bpm.cache.ProcessInstanceBOCache;
import com.sinosoft.one.bpm.cache.TaskNodeInfoCache;
import com.sinosoft.one.bpm.listener.BpmProcessEventListener;
import com.sinosoft.one.bpm.listener.BusinessProcessEventListener;
import com.sinosoft.one.bpm.listener.BusinessTaskEventListener;
import com.sinosoft.one.bpm.model.ActiveNodeInfo;
import com.sinosoft.one.bpm.model.DiagramInfo;
import com.sinosoft.one.bpm.model.DiagramNodeInfo;
import com.sinosoft.one.bpm.model.NodeInfo;
import com.sinosoft.one.bpm.model.NodeInstanceLogComparator;
import com.sinosoft.one.bpm.model.TaskNodeInfo;
import com.sinosoft.one.bpm.service.facade.ProcessInstanceBOService;
import com.sinosoft.one.bpm.service.facade.TaskNodeInfoService;
import com.sinosoft.one.bpm.service.spring.ProcessInstanceBOServiceSupport;
import com.sinosoft.one.bpm.service.spring.TaskNodeInfoServiceSupport;

public class BpmServiceSupport {
	static Logger logger = Logger.getLogger(BpmServiceSupport.class);

	private KnowledgeBase kbase;
	
	private EntityManagerFactory bpmEMF;
	private AbstractPlatformTransactionManager bpmTxManager;
	private ProcessEventListener bpmProcessEventListener;
	private BusinessProcessEventListener businessProcessEventListener;
	private BusinessTaskEventListener businessTaskEventListener;
	
	private ProcessInstanceBOService processInstanceBOService;
	private ProcessInstanceBOCache processInstanceBOCache;
	private TaskNodeInfoService taskNodeInfoService;
	private TaskNodeInfoCache taskNodeInfoCache;
	
	private StatefulKnowledgeSession ksession;
	private TaskService taskService;
	
	private boolean useJTA;
	
	private Environment env;
	private int currentKSessionId = 1;
	private Map<String, TaskService> taskServices = new ConcurrentHashMap<String, TaskService>();

	public void init() {
		try {
			initKnowledgeBase("drools.properties");
			initEnvironment();
			initTaskService();
			initStatefulKnowledgeSesssion();
//			initKSessionPool();
			initServiceInstances();
			initBpmProcessEventListener();
		} catch (Exception e) {
			logger.error("init BpmService exception.", e);
		}
	}

	/**
	 * create KnowledgeBase by agent
	 * 
	 * @param propertiesFilePath
	 * @return
	 */
	private void initKnowledgeBase(String propertiesFilePath) {
		KnowledgeAgent kAgent = KnowledgeAgentGenerator.getKnowledgeAgent(propertiesFilePath);
		kbase = kAgent.getKnowledgeBase();
	}
	
	private void initTaskService() {
		org.jbpm.task.service.TaskService taskService = new org.jbpm.task.service.TaskService(bpmEMF, SystemEventListenerFactory.getSystemEventListener());
//		org.jbpm.task.service.TaskService taskService = new org.jbpm.task.service.TaskService();
//		taskService.setSystemEventListener(SystemEventListenerFactory.getSystemEventListener());
//		
//		TaskSessionSpringFactoryImpl taskSessionSpringFactory = new TaskSessionSpringFactoryImpl();
//		taskSessionSpringFactory.setEntityManagerFactory(bpmEMF);
//		taskSessionSpringFactory.setTransactionManager(new HumanTaskSpringTransactionManager(bpmTxManager));
//		taskService.setTaskSessionFactory(taskSessionSpringFactory);
//		taskSessionSpringFactory.setTaskService(taskService);
//		taskSessionSpringFactory.setUseJTA(useJTA);
//		taskService.initialize();
		
		if(businessTaskEventListener != null) {
			List<TaskEventListener> eventListeners = businessTaskEventListener.getTaskEventListeners();
			if(eventListeners != null && eventListeners.size() > 0) {
				for(TaskEventListener taskEventListener : eventListeners) {
					taskService.addEventListener(taskEventListener);
				}
			}
		}
		this.taskService = new LocalTaskService(taskService);
	}
	
	private void initStatefulKnowledgeSesssion() {
		if(kbase == null) {
			initKnowledgeBase("drools.properties");
		}
		if(this.taskService == null) {
			this.initTaskService();
		}
        //避免开发时多人访问同一session记录造成死锁
        //removed by hanchunliang 2014-03-26 start
//		try {
//			this.ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(currentKSessionId, kbase, null, env);
//		} catch (Exception e) {
//			logger.warn("No knowledge session for id [" + currentKSessionId + "]");
//			this.ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
//			currentKSessionId = ksession.getId();
//		}
        //removed by hanchunliang 2014-03-26 end

        //added by hanchunliang 2014-03-26 start
        this.ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        this.currentKSessionId = ksession.getId();
        //added by hanchunliang 2014-03-26 end
		
		new JPAWorkingMemoryDbLogger(ksession);
		if(businessProcessEventListener != null) {
			List<ProcessEventListener> eventListeners = businessProcessEventListener.getProcessEventListeners();
			if(eventListeners != null && eventListeners.size() > 0) {
				for(ProcessEventListener processEventListener : eventListeners) {
					this.ksession.addEventListener(processEventListener);
				}
			}
		}
		
		// registe taskService
		LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler(this.taskService, this.ksession, OnErrorAction.RETHROW);
		humanTaskHandler.connect();
		this.ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
		System.setProperty(UserGroupCallbackManager.USER_GROUP_CALLBACK_KEY, "com.sinosoft.one.bpm.identity.BpmDefaultUserGroupCallback");
	}

	
	private void initBpmProcessEventListener() {
		if(bpmProcessEventListener == null) {
			bpmProcessEventListener = new BpmProcessEventListener(processInstanceBOCache);
			ksession.addEventListener(bpmProcessEventListener);
		}
	}


	/**
	 * get a task server
	 * 
	 * @return
	 * @throws Exception
	 */
	public TaskService getTaskService(String businessId) throws Exception {
		TaskService taskService = taskServices.get(businessId);
		if(taskService != null) {
			return taskService;
		} 
		org.jbpm.task.service.TaskService innerTaskService = new org.jbpm.task.service.TaskService(bpmEMF, SystemEventListenerFactory.getSystemEventListener());
//		org.jbpm.task.service.TaskService innerTaskService = new org.jbpm.task.service.TaskService();
//		innerTaskService.setSystemEventListener(SystemEventListenerFactory.getSystemEventListener());
//		
//		TaskSessionSpringFactoryImpl taskSessionSpringFactory = new TaskSessionSpringFactoryImpl();
//		taskSessionSpringFactory.setEntityManagerFactory(bpmEMF);
//		taskSessionSpringFactory.setTransactionManager(new HumanTaskSpringTransactionManager(bpmTxManager));
//		innerTaskService.setTaskSessionFactory(taskSessionSpringFactory);
//		taskSessionSpringFactory.setTaskService(innerTaskService);
//		taskSessionSpringFactory.setUseJTA(useJTA);
//		innerTaskService.initialize();
//		
		if(businessTaskEventListener != null) {
			List<TaskEventListener> eventListeners = businessTaskEventListener.getTaskEventListeners();
			if(eventListeners != null && eventListeners.size() > 0) {
				for(TaskEventListener taskEventListener : eventListeners) {
					innerTaskService.addEventListener(taskEventListener);
				}
			}
		}
		TaskService tempTaskService =  new LocalTaskService(innerTaskService);
		LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler(tempTaskService, this.ksession, OnErrorAction.RETHROW);
		humanTaskHandler.connect();
		taskServices.put(businessId, tempTaskService);
		return tempTaskService;
		
	}
	
	public StatefulKnowledgeSession getSession() {
		if(this.ksession == null) {
			initStatefulKnowledgeSesssion();
		}
		return this.ksession;
	}
	
	public Environment getEnvironment() {
		if(this.env == null) {
			initEnvironment();
		}
		return this.env;
	}


	/**
	 * create Environment and set some attributes
	 * 
	 * @param emf
	 * @return
	 */
	public void initEnvironment() {
		Environment env = KnowledgeBaseFactory.newEnvironment();  
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, bpmEMF);   
		env.set(EnvironmentName.TRANSACTION_MANAGER, bpmTxManager);
		
		this.env = env;
		JPAProcessInstanceDbLog.setEnvironment(env);
//		return env;
	}
	
	public TransactionManager getTransactionManager() {
		return (TransactionManager) env.get(EnvironmentName.TRANSACTION_MANAGER);
	}

	
	/**
	 * init some service instance 
	 */
	private void initServiceInstances() {
		if(processInstanceBOService == null) {
			EntityManager em = (EntityManager)env.get(EnvironmentName.APP_SCOPED_ENTITY_MANAGER);
			if(em == null || !em.isOpen()) {
				em = (EntityManager)env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
			}
			if(em == null || !em.isOpen()) {
				em = bpmEMF.createEntityManager();
			}
//			EntityManager em = bpmEMF.createEntityManager();
			processInstanceBOService = new ProcessInstanceBOServiceSupport(em, useJTA);
			taskNodeInfoService = new TaskNodeInfoServiceSupport(em);
		} 
		if(processInstanceBOCache == null) {
			processInstanceBOCache = new ProcessInstanceBOCache(processInstanceBOService);
		}
		if(taskNodeInfoCache == null) {
			taskNodeInfoCache = new TaskNodeInfoCache(taskNodeInfoService);
			taskNodeInfoCache.init(kbase.getProcesses());
		}
	}
	
	/**
	 * get global variable value by variable name
	 * @param variableName the variable name
	 * @return variable value
	 * @throws Exception 
	 */
	public Object getGlobalVariable(String variableName) throws Exception {
		return getSession().getGlobal(variableName);
	}
	
	public void setGlobalVariable(String variableName, Object variableValue) throws Exception {
		getSession().setGlobal(variableName, variableValue);
	}
	
	public Object getProcessInstanceVariable(String processId, String businessId, String variableName) throws Exception {
		ProcessInstance pi = getSession().getProcessInstance(getProcessInstanceId(processId, businessId));
		if(pi instanceof WorkflowProcessInstance) {
			return ((WorkflowProcessInstance)pi).getVariable(variableName);
		}
		return null;
	}
	
	public void setProcessInstanceVariable(String processId, String businessId, final String variableName, final Object variableValue) throws Exception {
		try {
			final long piId = getProcessInstanceId(processId, businessId);
//			ksession.execute(new GenericCommand<Object>() {
//
//				public Object execute(Context context) {
//					 org.jbpm.process.instance.ProcessInstance processInstance = (org.jbpm.process.instance.ProcessInstance) ksession.getProcessInstance(piId);
//
//				     VariableScopeInstance variableScope = (VariableScopeInstance) processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
//				     variableScope.setVariable(variableName, variableValue);
//				     return null;
//				}
//				
//			});
			org.jbpm.process.instance.ProcessInstance processInstance = (org.jbpm.process.instance.ProcessInstance) ksession.getProcessInstance(piId);
			VariableScopeInstance variableScope = (VariableScopeInstance) processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
			variableScope.setVariable(variableName, variableValue);
//			ProcessInstance pi = getSession().getProcessInstance(getProcessInstanceId(processId, businessId));
//			if(pi instanceof WorkflowProcessInstance) {
//				((WorkflowProcessInstance)pi).setVariable(variableName, variableValue);
//			}
		} catch(Throwable t) {
			t.printStackTrace();
			logger.warn("set process instance variable exception:" + t.getMessage());
		}
	}
	
	public String getBusinessId(long processInstanceId) throws Exception {
		String businessId = processInstanceBOCache.getBusinessId(processInstanceId);
		logger.info("businessId:" + businessId + ", processInstanceId : " + processInstanceId);
		if(StringUtils.isBlank(businessId)) {
			ProcessInstance processInstance = getSession().getProcessInstance(processInstanceId);
			if(processInstance instanceof WorkflowProcessInstance) {
				WorkflowProcessInstance wpi = (WorkflowProcessInstance) processInstance;
				if (wpi != null) {
					businessId = (String) wpi.getVariable("businessId");
					if(StringUtils.isNotBlank(businessId)) {
						processInstanceBOCache.putAndSaveWithNone(wpi.getProcessId(), businessId, processInstanceId);
					}
				}
			}
		}
		return businessId;
	}
	
	public List<TaskSummary> getTasks(String userId, String businessId) throws Exception {
		TaskService taskService = getTaskService(businessId);
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(
				userId, "en-UK");
		return tasks;
	}

	public long getProcessInstanceId(String processId, String businessId) {
		Long instanceId = processInstanceBOCache.getProcessInstanceId(processId, businessId);
		if (instanceId == null) {
			List<ProcessInstanceLog> processInstanceLogs = JPAProcessInstanceDbLog.findActiveProcessInstances(processId);
			for(ProcessInstanceLog tempProcessInstanceLog : processInstanceLogs) {
				Long processInstanceId = tempProcessInstanceLog.getProcessInstanceId();
				ProcessInstance targetProcessInstance = getSession().getProcessInstance(processInstanceId);
				if(targetProcessInstance instanceof WorkflowProcessInstance) {
					WorkflowProcessInstance wpi = (WorkflowProcessInstance) targetProcessInstance;
					String targetBusinessId = (String)wpi.getVariable("businessId");
					if(businessId.equals(targetBusinessId)) {
						instanceId = processInstanceId;
						processInstanceBOCache.putAndSaveWithNone(processId, businessId, processInstanceId);
						break;
					}
				}
				
			}
		} 
		return instanceId == null ? -1 : instanceId;
	}
	
	
	public Collection<NodeInstanceLog> getNextNodeInfoes(String processId, String businessId) {
		Long instanceId = getProcessInstanceId(processId, businessId);
		ProcessInstanceLog processInstanceLog = JPAProcessInstanceDbLog.findProcessInstance(instanceId);
		if (processInstanceLog == null) {
			throw new IllegalArgumentException(
					"Could not find process instance by instance id : " + instanceId);
		}
		Map<String, NodeInstanceLog> nodeInstances = new HashMap<String, NodeInstanceLog>();
		List<NodeInstanceLog> nodeInstanceList = JPAProcessInstanceDbLog.findNodeInstances(instanceId);
		Collections.sort(nodeInstanceList, new NodeInstanceLogComparator());
		for (NodeInstanceLog nodeInstance : nodeInstanceList) {
			if (nodeInstance.getType() == NodeInstanceLog.TYPE_ENTER) {
				nodeInstances.put(nodeInstance.getNodeInstanceId(),
						nodeInstance);
			} else {
				nodeInstances.remove(nodeInstance.getNodeInstanceId());
			}
		}
		return nodeInstances.values();
	}
	
	
	public List<String> getNextActorIds(String processId, String actorId, Map<String, Object> conditions) {
		TaskNodeInfo taskNodeInfo = taskNodeInfoCache.getTaskNodeInfo(processId, actorId);
		return taskNodeInfo.nextActorIds(conditions);
	}
	
	public List<String> getNextActorIds(String processId, String actorId) {
		TaskNodeInfo taskNodeInfo = taskNodeInfoCache.getTaskNodeInfo(processId, actorId);
		return taskNodeInfo.nextActorIds();
	}
	
	public List<NodeInfo> getNextNodeInfos(String processId, String actorId, Map<String, Object> conditions) {
		TaskNodeInfo taskNodeInfo = taskNodeInfoCache.getTaskNodeInfo(processId, actorId);
		return taskNodeInfo.nextNodeInfos(conditions);
	}
	
	public List<NodeInfo> getNextNodeInfos(String processId, String actorId) {
		TaskNodeInfo taskNodeInfo = taskNodeInfoCache.getTaskNodeInfo(processId, actorId);
		return taskNodeInfo.nextNodeInfos();
	}
	
	public NodeInfo getCurrentNodeInfo(String processId, String actorId) {
		TaskNodeInfo taskNodeInfo = taskNodeInfoCache.getTaskNodeInfo(processId, actorId);
		return taskNodeInfo.toNodeInfo();
	}
	
	public String getBusinessIdByTaskId(long taskId) {
		String businessId = "";
		try {
			BigDecimal processInstanceId = processInstanceBOService.queryProcessInstanceIdByTaskId(taskId);
			businessId = this.getBusinessId(processInstanceId.longValue());
		} catch (Exception e) {
			logger.warn("No any task find for id [" + taskId + "]");
		}
		return businessId;
	}
	public List<ActiveNodeInfo> getActiveNodeInfo(String processId, String businessId) {
		Collection<NodeInstanceLog> nodeInstances = getNextNodeInfoes(processId, businessId);
		
		Long instanceId = getProcessInstanceId(processId, businessId);
		ProcessInstanceLog processInstanceLog = JPAProcessInstanceDbLog.findProcessInstance(instanceId);
		if (processInstanceLog == null) {
			throw new IllegalArgumentException(
					"Could not find process instance by instance id : " + instanceId);
		}
		
		if (!nodeInstances.isEmpty()) {
			List<ActiveNodeInfo> result = new ArrayList<ActiveNodeInfo>();
			for (NodeInstanceLog nodeInstance : nodeInstances) {
				boolean found = false;
				DiagramInfo diagramInfo = getDiagramInfo(processInstanceLog
						.getProcessId());
				if (diagramInfo != null) {
					for (DiagramNodeInfo nodeInfo : diagramInfo.getNodeList()) {
						if (nodeInfo.getName().equals(
								"id=" + nodeInstance.getNodeId())) {
							result.add(new ActiveNodeInfo(diagramInfo
									.getWidth(), diagramInfo.getHeight(),
									nodeInfo));
							found = true;
							break;
						}
					}
				} else {
					throw new IllegalArgumentException(
							"Could not find info for diagram for process "
									+ processInstanceLog.getProcessId());
				}
				if (!found) {
					throw new IllegalArgumentException(
							"Could not find info for node "
									+ nodeInstance.getNodeId() + " of process "
									+ processInstanceLog.getProcessId());
				}
			}
			return result;
		}
		return Collections.emptyList();
	}

	public DiagramInfo getDiagramInfo(String processId) {
		org.drools.definition.process.Process process = kbase
				.getProcess(processId);
		if (process == null) {
			return null;
		}

		DiagramInfo result = new DiagramInfo();
		// TODO: diagram width and height?
		result.setWidth(932);
		result.setHeight(541);
		List<DiagramNodeInfo> nodeList = new ArrayList<DiagramNodeInfo>();
		if (process instanceof WorkflowProcess) {
			addNodesInfo(nodeList, ((WorkflowProcess) process).getNodes(),
					"id=");
		}
		result.setNodeList(nodeList);
		return result;
	}

	private void addNodesInfo(List<DiagramNodeInfo> nodeInfos,
			Node[] nodes, String prefix) {
		for (Node node : nodes) {
			nodeInfos.add(new DiagramNodeInfo(prefix + node.getId(),
					(Integer) node.getMetaData().get("x"), (Integer) node
							.getMetaData().get("y"), (Integer) node
							.getMetaData().get("width"), (Integer) node
							.getMetaData().get("height")));
			if (node instanceof NodeContainer) {
				addNodesInfo(nodeInfos, ((NodeContainer) node).getNodes(),
						prefix + node.getId() + ":");
			}
		}
	}

	public void setProcessInstanceBOCache(ProcessInstanceBOCache processInstanceBOCache) {
		this.processInstanceBOCache = processInstanceBOCache;
	}

	public void setBpmProcessEventListener(
			ProcessEventListener bpmProcessEventListener) {
		this.bpmProcessEventListener = bpmProcessEventListener;
	}

	public void setBpmTxManager(AbstractPlatformTransactionManager bpmTxManager) {
		this.bpmTxManager = bpmTxManager;
	}

	public boolean isUseJTA() {
		return useJTA;
	}

	public void setUseJTA(boolean useJTA) {
		this.useJTA = useJTA;
	}

	public void setBpmEMF(EntityManagerFactory bpmEMF) {
		this.bpmEMF = bpmEMF;
	}

	public void setBusinessProcessEventListener(
			BusinessProcessEventListener businessProcessEventListener) {
		this.businessProcessEventListener = businessProcessEventListener;
	}

	public void setBusinessTaskEventListener(
			BusinessTaskEventListener businessTaskEventListener) {
		this.businessTaskEventListener = businessTaskEventListener;
	}
}
