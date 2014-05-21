package com.sinosoft.one.bpm.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.persistence.NoResultException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.io.Resource;
import org.jbpm.process.instance.impl.ConstraintEvaluator;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.CompositeNode.CompositeNodeEnd;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sinosoft.one.bpm.model.NodePath;
import com.sinosoft.one.bpm.model.SubTaskNodeInfo;
import com.sinosoft.one.bpm.model.TaskNodeEntity;
import com.sinosoft.one.bpm.model.TaskNodeInfo;
import com.sinosoft.one.bpm.model.TaskNodeKey;
import com.sinosoft.one.bpm.service.facade.TaskNodeInfoService;

/**
 * 任务节点信息缓存
 * 
 * @author carvin
 * 
 */
public class TaskNodeInfoCache {
	private final static Logger logger = LoggerFactory
			.getLogger(TaskNodeInfoCache.class);

	private TaskNodeInfoService taskNodeInfoService;
	private LoadingCache<TaskNodeKey, TaskNodeInfo> taskNodeInfoCache = CacheBuilder
			.newBuilder().build(new CacheLoader<TaskNodeKey, TaskNodeInfo>() {
				public TaskNodeInfo load(TaskNodeKey key) throws Exception {
					logger.info("Fetch task node info from database.");
					TaskNodeEntity taskNodeEntity = null;
					try {
						taskNodeEntity = taskNodeInfoService
								.queryTaskNodeEntity(key.getProcessId(),
										key.getActorId());
					} catch(Throwable t) {
						if(t instanceof NoResultException) {
							taskNodeEntity = null;
							logger.warn("No such task node info for key  : " + key);
						}
					}
					if (taskNodeEntity == null
							|| taskNodeEntity.getTaskNodeInfo() == null) {
						return TaskNodeInfo.EMPTY;
					}

					return taskNodeEntity.getTaskNodeInfo();
				}
			});

	public TaskNodeInfoCache(TaskNodeInfoService taskNodeInfoService) {
		this.taskNodeInfoService = taskNodeInfoService;
	}

	/**
	 * 初始化任务节点信息
	 * 
	 * @param process
	 * @throws Exception 
	 */
	public void init(Collection<org.drools.definition.process.Process> processes){
		try {
			if (processes != null && processes.size() > 0) {
				SAXReader reader = new SAXReader();  
				reader.setEncoding(Charsets.UTF_8.displayName());
				for (org.drools.definition.process.Process process : processes) {
					RuleFlowProcess ruleFlowProcess = null;
					if (process instanceof RuleFlowProcess) {
						ruleFlowProcess = (RuleFlowProcess) process;
						String processId = ruleFlowProcess.getId();
						
						Resource resource = ruleFlowProcess.getResource();
			            Document doc = reader.read(resource.getReader());  
			            Element root = doc.getRootElement(); 
						
						Node[] nodes = ruleFlowProcess.getNodes();
						List<TaskNodeEntity> taskNodeEntities = new ArrayList<TaskNodeEntity>();
						for (Node node : nodes) {
							addTaskNodeInfo(node, processId, taskNodeEntities, root);
							if(node instanceof CompositeNode) {
								CompositeNode compositeNode = (CompositeNode) node;
								Node[] subNodes = compositeNode.getNodes();
								for(Node subNode : subNodes) {
									addTaskNodeInfo(subNode, processId, taskNodeEntities, root);
								}
							} 
						}
	
						if(taskNodeEntities.size() > 0) {
							logger.info("Save task node info to database begin.");
							taskNodeInfoService.saveOrUpdateTaskNodeEntities(taskNodeEntities);
							logger.info("Save task node info to database end.");
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("init task node info cache exception.", e);
		} finally {
		}
	}
	
	/**
	 * 增加任务节点信息
	 * @param node
	 * @param processId
	 * @param taskNodeEntities
	 * @param root
	 */
	private void addTaskNodeInfo(Node node, String processId, List<TaskNodeEntity> taskNodeEntities, Element root) {
		if (node instanceof WorkItemNode) {
			final WorkItemNode workItemNode = (WorkItemNode) node;
			Map<String, Object> workParamMap = workItemNode.getWork().getParameters();
			String actorId = String.valueOf(workParamMap.get("ActorId"));
			String taskName = String.valueOf(workParamMap.get("TaskName"));
			String comment = workParamMap.get("Comment") == null ? "" : String.valueOf(workParamMap.get("Comment"));
			String content = workParamMap.get("Content") == null ? "" : String.valueOf(workParamMap.get("Content"));
			
			Map<String, Object> metaDataMap = workItemNode.getMetaData();
			
			String nodeName = workItemNode.getName();

			TaskNodeInfo taskNodeInfo = new TaskNodeInfo(actorId, taskName, nodeName, metaDataMap, comment, content);
			NodePath nodePath = new NodePath();
			recursiveParseNode(((WorkItemNode) node).getDefaultOutgoingConnections().get(0).getTo(), nodePath, root, processId);

			taskNodeInfo.addAllNextTaskNodeInfo(nodePath.getSubTaskNodeInfos());
			TaskNodeKey key = TaskNodeKey.valueOf(processId, actorId);
			taskNodeInfoCache.put(key, taskNodeInfo);

			TaskNodeEntity entity = new TaskNodeEntity();
			entity.setId(processId + "_" + actorId);
			entity.setActorId(actorId);
			entity.setProcessId(processId);
			entity.setTaskNodeInfo(taskNodeInfo);
			taskNodeEntities.add(entity);
		} 
	}
	
	/**
	 * 通过流程ID和ActorID获取流程节点信息
	 * @param processId 流程id
	 * @param actorId actor id
	 * @return 任务节点信息
	 */
	public TaskNodeInfo getTaskNodeInfo(String processId, String actorId) {
		TaskNodeKey taskNodeKey = TaskNodeKey.valueOf(processId, actorId);
		try {
			return taskNodeInfoCache.get(taskNodeKey);
		} catch (ExecutionException e) {
			logger.warn("get task node info excpetion by processId=[" + processId + "], actorId=[" + actorId + "]");
			return TaskNodeInfo.EMPTY;
		}
	}
	
	/**
	 * 递归解析节点
	 * @param currentNode 当前节点
	 * @param nodePath 路由节点
	 * @param root 流程图根节点
	 * @param processId 流程ID
	 * 
	 */
	private void recursiveParseNode(Node currentNode,	NodePath nodePath, Element root, String processId) {
		if(currentNode instanceof Split) {
			final Split split = (Split) currentNode;
			List<Connection> connections = split.getDefaultOutgoingConnections();
			for (Connection connection : connections) {
				NodePath tempNodePath = new  NodePath();
				if(split.getType() == Split.TYPE_OR || split.getType() == Split.TYPE_XOR) {
					ConstraintEvaluator constraint = (ConstraintEvaluator) split.getConstraint( connection );
					
                    if ( constraint != null  
                            && !constraint.isDefault() ) {
                    	String connectionId = String.valueOf(connection.getMetaData().get("UniqueId"));
                    	String constraintString = getConstraintById(root, processId, connectionId);
                    	if(!constraintString.equals("") && !constraintString.equals("return true;")) {
                    		tempNodePath.setConstraint(constraintString);
                    	}
                    }
				}
				nodePath.addNodePath(tempNodePath);
				recursiveParseNode(connection.getTo(), tempNodePath, root, processId);
			}
		} else if (currentNode instanceof WorkItemNode) {
			SubTaskNodeInfo newSubTaskNodeInfo = new SubTaskNodeInfo();
			final WorkItemNode nextWorkItemNode = (WorkItemNode) currentNode;
			Map<String, Object> workParamMap = nextWorkItemNode.getWork().getParameters();
			String actorId = String.valueOf(workParamMap.get("ActorId"));
			String taskName = String.valueOf(workParamMap.get("TaskName"));
			String nodeName = nextWorkItemNode.getName();
			String comment = workParamMap.get("Comment") == null ? "" : String.valueOf(workParamMap.get("Comment"));
			String content = workParamMap.get("Content") == null ? "" : String.valueOf(workParamMap.get("Content"));
			
			Map<String, Object> metaDataMap = nextWorkItemNode.getMetaData();
			
			newSubTaskNodeInfo.setActorId(actorId);
			newSubTaskNodeInfo.setTaskName(taskName);
			newSubTaskNodeInfo.setNodeName(nodeName);
			newSubTaskNodeInfo.setMetaDataMap(metaDataMap);
			newSubTaskNodeInfo.setComment(comment);
			newSubTaskNodeInfo.setContent(content);

			nodePath.addSubTaskNodeInfo(newSubTaskNodeInfo);
		} else if(currentNode instanceof CompositeNode) {
			CompositeNode compositeNode = (CompositeNode) currentNode;
			Node[] subNodes = compositeNode.getNodes();
			recursiveParseNode(subNodes[0], nodePath, root, processId);
		}  else if(currentNode instanceof EndNode) {
			return;
		} else if(currentNode instanceof CompositeNodeEnd) {
			recursiveParseNode(((CompositeNodeEnd)currentNode).getOutNode(), nodePath, root, processId);
		} else {
			recursiveParseNode(getNextNode(currentNode), nodePath, root, processId);
		}
	}
	
	private Node getNextNode(Node currentNode) {
		return ((NodeImpl)currentNode).getDefaultOutgoingConnections().get(0).getTo();
	}
	
	/**
	 * 得到约束条件 
	 * @param root
	 * @param processId
	 * @param elementId
	 * @return
	 */
	private String getConstraintById(Element root, String processId, String elementId) {
		Element processElement = elementByID(root, processId);
		Element sequenceElement = elementByID(processElement, elementId);
		if(sequenceElement == null) {
			@SuppressWarnings("unchecked")
			List<Element> subProcessElements = processElement.elements("subProcess");
			for(Element subProcessElement : subProcessElements) {
				sequenceElement = elementByID(subProcessElement, elementId);
				if(sequenceElement != null) {
					break;
				}
			}
		}
		logger.info("processId : {}, elementId : {}, sequenceElement : {}", new Object[] {processId, elementId, sequenceElement});
		Element conditionElement = sequenceElement.element("conditionExpression");
		return conditionElement != null ? conditionElement.getStringValue() : "";
	}
	
	private Element elementByID(Element sourceElement, String elementID) {
        for (int i = 0, size = sourceElement.nodeCount(); i < size; i++) {
            org.dom4j.Node node = sourceElement.node(i);

            if (node instanceof Element) {
                Element element = (Element) node;
                String id = element.attributeValue("id");

                if ((id != null) && id.equals(elementID)) {
                    return element;
                } else {
                    element = element.elementByID(elementID);

                    if (element != null) {
                        return element;
                    }
                }
            }
        }

        return null;
    }
}
