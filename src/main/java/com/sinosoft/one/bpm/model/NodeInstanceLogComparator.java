package com.sinosoft.one.bpm.model;

import java.util.Comparator;

import org.jbpm.process.audit.NodeInstanceLog;

public class NodeInstanceLogComparator implements Comparator<NodeInstanceLog> {

	public int compare(NodeInstanceLog o1, NodeInstanceLog o2) {
		return o1.getType() - o2.getType();
	}

}
