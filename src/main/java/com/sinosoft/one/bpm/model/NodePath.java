package com.sinosoft.one.bpm.model;

import java.util.ArrayList;
import java.util.List;

public class NodePath {
	private String id;
	private String constraint;
	private boolean hasTaskNode;
	private List<NodePath> nodePaths = new ArrayList<NodePath>();
	private List<SubTaskNodeInfo> subTaskNodeInfos = new ArrayList<SubTaskNodeInfo>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getConstraint() {
		return constraint;
	}
	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}
	public boolean isHasTaskNode() {
		return hasTaskNode;
	}
	public void setHasTaskNode(boolean hasTaskNode) {
		this.hasTaskNode = hasTaskNode;
	}
	public List<NodePath> getNodePaths() {
		return nodePaths;
	}
	public void setNodePaths(List<NodePath> nodePaths) {
		this.nodePaths = nodePaths;
	}
	
	public void addNodePath(NodePath nodePath) {
		this.nodePaths.add(nodePath);
	}
	
	public List<SubTaskNodeInfo> getSubTaskNodeInfos() {
		List<SubTaskNodeInfo> targetSubTaskNodeInfos = new ArrayList<SubTaskNodeInfo>();
		if(subTaskNodeInfos.size() > 0) {
			addConstraits(targetSubTaskNodeInfos, subTaskNodeInfos);
		} else if(nodePaths.size() > 0) {
			for(NodePath nodePath : nodePaths) {
				addConstraits(targetSubTaskNodeInfos, nodePath.getSubTaskNodeInfos());
			}
		}
 		return targetSubTaskNodeInfos;
	}
	
	private void addConstraits(List<SubTaskNodeInfo> targetSubTaskNodeInfos, List<SubTaskNodeInfo> subTaskNodeInfos) {
		for(SubTaskNodeInfo subTaskNodeInfo : subTaskNodeInfos) {
			if(constraint != null && !"".equals(constraint) && !"return true;".equals(constraint)) {
				subTaskNodeInfo.addConstraint(constraint);
			}
			targetSubTaskNodeInfos.add(subTaskNodeInfo);
		}
	}
	public void addSubTaskNodeInfo(SubTaskNodeInfo subTaskNodeInfo) {
		this.subTaskNodeInfos.add(subTaskNodeInfo);
	}
}
