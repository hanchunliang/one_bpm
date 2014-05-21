package com.sinosoft.one.bpm.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sinosoft.one.bpm.model.ActiveNodeInfo;
import com.sinosoft.one.bpm.model.NodeInfo;
import com.sinosoft.one.bpm.support.BpmServiceSupport;
import com.sinosoft.one.bpm.variable.ListVariableHandler;
import com.sinosoft.one.bpm.variable.MapVariableHandler;
import com.sinosoft.one.bpm.variable.VariableOperate;

public class JbpmAPIUtil {

	private static BpmServiceSupport bpmServiceSupport;

	public static String getImageInfoes(String processId, String businessId,
			String imageUrl, String contextPath) {
		if (StringUtils.isBlank(processId)) {
			throw new IllegalArgumentException("the process id is not blank.");
		}
		if (StringUtils.isBlank(businessId)) {
			throw new IllegalArgumentException("the bussiness id is not blank.");
		}

		List<ActiveNodeInfo> activeNodeInfos = bpmServiceSupport
				.getActiveNodeInfo(processId, businessId);
		String s = "<div style='width:1024px; height:768px; background-color:#ffffff;'>"
				+ "<div id=\"imageContainer\" style=\"position:relative;top:-1;left:-1;\">"
				+ "<img src=\""
				+ imageUrl
				+ "\" style=\"position:absolute;top:0;left:0\" />";
		for (ActiveNodeInfo activeNodeInfo : activeNodeInfos) {

			s += "<div class=\"bpm-graphView-activityImage\" style=\"position:absolute;top:"
					+ (activeNodeInfo.getActiveNode().getY() - 8)
					+ "px;left:"
					+ (activeNodeInfo.getActiveNode().getX() - 8)
					+ "px;width:50px;height:50px; z-index:1000;background-image: url("
					+ contextPath
					+ "/images/play_red_big.png);background-repeat:no-repeat;\"></div>";
		}
		s += "</div>" + "</div>";
		return s;
	}

	public static Object getGlobalVariable(String variableName)
			throws Exception {
		return bpmServiceSupport.getGlobalVariable(variableName);
	}

	public static void setGlobalVariable(String variableName,
			Object variableValue) throws Exception {
		bpmServiceSupport.setGlobalVariable(variableName, variableValue);
	}

	public static Object getProcessInstanceVariable(String processId,
			String businessId, String variableName) throws Exception {
		return bpmServiceSupport.getProcessInstanceVariable(processId,
				businessId, variableName);
	}
	@Deprecated
	public static void setProcessInstanceVariable(String processId,
			String businessId, String variableName, Object variableValue)
			throws Exception {
		bpmServiceSupport.setProcessInstanceVariable(processId, businessId,
				variableName, variableValue);
	}

	public static void putGlobalVariableWithMap(String variableName,
			String mapKey, Object variableValue) throws Exception {
		MapVariableHandler.build(bpmServiceSupport).handler(variableName,
				 VariableOperate.ADD, mapKey,
				variableValue);
	}

	public static void removeGlobalVariableWithMap(String variableName,
			String mapKey) throws Exception {
		MapVariableHandler.build(bpmServiceSupport).handler(variableName,
				VariableOperate.REMOVE, 
				mapKey, null);
	}

	public static void addGlobalVariableWithList(String variableName,
			Object variableValue) throws Exception {
		ListVariableHandler.build(bpmServiceSupport).handler(variableName,
				VariableOperate.ADD,
				variableValue);
	}

	public static void removeGlobalVariableWithList(String variableName,
			Object variableValue) throws Exception {
		ListVariableHandler.build(bpmServiceSupport).handler(variableName,
				VariableOperate.REMOVE, 
				variableValue);
	}
	
	public static String getBusinessIdByTaskId(long taskId) {
		return bpmServiceSupport.getBusinessIdByTaskId(taskId);
	}
	
	/**
	 * 根据流程ID和当前节点ActorID获取后续节点ActorID集合
	 * @param processId 流程ID
	 * @param actorId 当前ActorID
	 * @return 下一级节点的ActorID集合
	 */
	public static List<String> getNextActorIds(String processId, String actorId) {
		return bpmServiceSupport.getNextActorIds(processId, actorId);
	}
	

	/**
	 * 根据流程ID,当前节点ActorID, 约束条件获取后续节点ActorID列表
	 * @param processId 流程ID
	 * @param actorId 当前ActorID
	 * @param conditions 条件Map
	 * @return 下一级节点的ActorID列表
	 */
	public static List<String> getNextActorIds(String processId, String actorId, Map<String, Object> conditions) {
		return bpmServiceSupport.getNextActorIds(processId, actorId, conditions);
	}
	
	/**
	 * 根据流程ID,当前节点ActorID, 约束条件获取后续节点信息集合
	 * @param processId 流程ID
	 * @param actorId 当前ActorID
	 * @param conditions 条件Map
	 * @return 下一级节点信息集合
	 */
	public static List<NodeInfo> getNextNodeInfos(String processId, String actorId, Map<String, Object> conditions) {
		return bpmServiceSupport.getNextNodeInfos(processId, actorId, conditions);
	}
	
	/**
	 * 根据流程ID和当前节点ActorID获取后续节点信息集合
	 * @param processId 流程ID
	 * @param actorId 当前ActorID
	 * @return 下一级节点信息集合
	 */
	public static List<NodeInfo> getNextNodeInfos(String processId, String actorId) {
		return bpmServiceSupport.getNextNodeInfos(processId, actorId);
	}
	
	/**
	 * 根据流程ID和当前节点ActorID获取当前节点信息
	 * @param processId 流程ID
	 * @param actorId 当前ActorID
	 * @return 当前节点信息集合
	 */
	public static NodeInfo getCurrentNodeInfo(String processId, String actorId) {
		return bpmServiceSupport.getCurrentNodeInfo(processId, actorId);
	}

	public void setBpmServiceSupport(BpmServiceSupport bpmServiceSupport) {
		JbpmAPIUtil.bpmServiceSupport = bpmServiceSupport;
	}
}
