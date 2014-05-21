package com.sinosoft.one.bpm.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiagramInfo
{
  private int width = -1;
  private int height = -1;
  private List<DiagramNodeInfo> nodeList = new ArrayList<DiagramNodeInfo>();

  public DiagramInfo()
  {
  }

  public DiagramInfo(int height, int width, List<DiagramNodeInfo> l) {
    this.height = height;
    this.width = width;
    List<DiagramNodeInfo> list = new ArrayList<DiagramNodeInfo>();
    for (DiagramNodeInfo nodeInfo : l) {
      list.add(nodeInfo);
    }
    this.nodeList = Collections.unmodifiableList(list);
  }

  public int getWidth()
  {
    return this.width;
  }

  public void setWidth(int width)
  {
    this.width = width;
  }

  public int getHeight()
  {
    return this.height;
  }

  public void setHeight(int height)
  {
    this.height = height;
  }

  public List<DiagramNodeInfo> getNodeList()
  {
    return this.nodeList;
  }

  public void setNodeList(List<DiagramNodeInfo> nodeList)
  {
    this.nodeList = nodeList;
  }
}