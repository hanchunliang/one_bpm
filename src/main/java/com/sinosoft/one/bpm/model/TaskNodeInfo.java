package com.sinosoft.one.bpm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.drools.rule.builder.dialect.java.parser.JavaLexer;
import org.drools.rule.builder.dialect.java.parser.JavaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * 任务节点信息类
 * @author carvin
 *
 */
public class TaskNodeInfo implements Serializable {
	private Logger logger = LoggerFactory.getLogger(TaskNodeInfo.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -8141750273459559014L;
	public static final TaskNodeInfo EMPTY = new TaskNodeInfo(); 
	private String actorId;
	private String taskName;
	private String nodeName;

	private List<SubTaskNodeInfo> nextTaskNodeInfoes;
	private String comment;
	private String content;
	
	private Map<String, Object> metaDataMap;
	
	public TaskNodeInfo() {
		this.nextTaskNodeInfoes = new ArrayList<SubTaskNodeInfo>();
	};

	public TaskNodeInfo(String actorId, String taskName, String nodeName) {
		this();
		this.actorId = actorId;
		this.taskName = taskName;
		this.nodeName = nodeName;
	}
	
	public TaskNodeInfo(String actorId, String taskName, String nodeName,
			Map<String, Object> metaDataMap, String comment, String content) {
		this(actorId, taskName, nodeName);
		this.metaDataMap = metaDataMap;
		this.comment = comment;
		this.content = content;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void addNextTaskNodeInfo(SubTaskNodeInfo nextNodeInfo) {
		this.nextTaskNodeInfoes.add(nextNodeInfo);
	}
	
	public void addAllNextTaskNodeInfo(List<SubTaskNodeInfo> nextNodeInfos) {
		this.nextTaskNodeInfoes.addAll(nextNodeInfos);
	}

	public List<SubTaskNodeInfo> getNextTaskNodeInfoes() {
		return nextTaskNodeInfoes;
	}

	public Map<String, Object> getMetaDataMap() {
		return metaDataMap;
	}

	public void setMetaDataMap(Map<String, Object> metaDataMap) {
		this.metaDataMap = metaDataMap;
	}
	

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public List<String> nextActorIds() {
		return nextActorIds(null);
	}
	
	public List<String> nextActorIds(Map<String, Object> conditions) {
		List<String> actorIdList = new ArrayList<String>();
		if(this.nextTaskNodeInfoes.size() > 0) {
			for(SubTaskNodeInfo subTaskNodeInfo : this.nextTaskNodeInfoes) {
				List<String> constrainList = subTaskNodeInfo.getConstraints();
				if(conditions != null && !conditions.isEmpty()) {
					boolean flag = true;
					for(String constraint : constrainList) {
						if(constraint != null && !"".equals(constraint)) {
							flag = flag && eval(constraint, conditions);
						}
					}
					if(flag) {
						actorIdList.add(subTaskNodeInfo.getActorId());
					} 
				} else {
					actorIdList.add(subTaskNodeInfo.getActorId());
				}
			}
		}
		return actorIdList;
	}
	
	public List<NodeInfo> nextNodeInfos() {
		return nextNodeInfos(null);
	}
	
	public List<NodeInfo> nextNodeInfos(Map<String, Object> conditions) {
		List<NodeInfo> nodeInfos = new ArrayList<NodeInfo>();
		if(this.nextTaskNodeInfoes.size() > 0) {
			for(SubTaskNodeInfo subTaskNodeInfo : this.nextTaskNodeInfoes) {
				List<String> constrainList = subTaskNodeInfo.getConstraints();
				if(conditions != null && !conditions.isEmpty()) {
					boolean flag = true;
					for(String constraint : constrainList) {
						if(constraint != null && !"".equals(constraint)) {
							flag = flag && eval(constraint, conditions);
						}
					}
					if(flag) {
						nodeInfos.add(subTaskNodeInfo.toNodeInfo());
					} 
				} else {
					nodeInfos.add(subTaskNodeInfo.toNodeInfo());
				}
			}
		}
		return nodeInfos;
	}
	
	private JavaParser parse(final String expr) {
        final CharStream charStream = new ANTLRStringStream(expr);
        final JavaLexer lexer = new JavaLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        return new JavaParser( tokenStream );
    }
	
	private Boolean eval(String expr, Map<String, Object> params) {
		try {
			JavaParser parser = parse("{" + expr + "}");
			parser.block();
			@SuppressWarnings("unchecked")
			List<String> identifiersList = parser.getIdentifiers();
			parser.getRuleInvocationStack();
			parser.booleanLiteral();
			Set<String> identifiers = new HashSet<String>(identifiersList);
			String tempExpr = expr;

			Iterator<String> identifierIterator = identifiers.iterator();
			while(identifierIterator.hasNext()) {
				String identifier = identifierIterator.next();
				tempExpr = tempExpr.replaceAll(identifier, "condition." + identifier + "");
			}
			
			String source = "function evalExpression(conditionStr) { var condition=eval('(' + conditionStr + ')'); " + tempExpr + "}";

			ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
			ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("js");
			scriptEngine.eval(source);
			Invocable invoke = (Invocable) scriptEngine;  
			Object result = invoke.invokeFunction("evalExpression", JSON.toJSONString(params));
			return (Boolean)result;
		} catch (Exception e) {
			logger.warn("eval split expression exception.", e);
			return Boolean.FALSE;
		} 
	}
	
	public NodeInfo toNodeInfo() {
		return new NodeInfo(this.getActorId(), this.getTaskName(),
				this.getNodeName(), this.getMetaDataMap(), comment, content);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 1;
		result = result * prime + (this.actorId != null ? this.actorId.hashCode() : 0);
		result = result * prime + (this.taskName != null ? this.taskName.hashCode() : 0);
		result = result * prime + (this.nodeName != null ? this.nodeName.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TaskNodeInfo)) {
			return false;
		}
		TaskNodeInfo taskNodeInfo = (TaskNodeInfo) obj;
		return (this.actorId != null && this.actorId.equals(taskNodeInfo.getActorId()))
				&& (this.taskName != null && this.taskName.equals(taskNodeInfo.getTaskName()))
				&& (this.nodeName != null && this.nodeName.equals(taskNodeInfo.getNodeName()));
	}
}
