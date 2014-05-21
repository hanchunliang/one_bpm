package com.sinosoft.one.bpm.model;


public class ActiveNodeInfo
{
  private int width = -1;
  private int height = -1;
  private DiagramNodeInfo activeNode;

  public ActiveNodeInfo()
  {
  }

  public ActiveNodeInfo(int width, int height, DiagramNodeInfo activeNode)
  {
    this.width = width;
    this.height = height;
    this.activeNode = activeNode;
  }

  public int getWidth() {
    return this.width;
  }

  public void setWidth(int width)
  {
    this.width = width;
  }

  public int getHeight() {
    return this.height;
  }

  public void setHeight(int height)
  {
    this.height = height;
  }

  public DiagramNodeInfo getActiveNode() {
    return this.activeNode;
  }

  public void setActiveNode(DiagramNodeInfo activeNode)
  {
    this.activeNode = activeNode;
  }
}