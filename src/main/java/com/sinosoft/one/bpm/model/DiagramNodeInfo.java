package com.sinosoft.one.bpm.model;


public class DiagramNodeInfo
{
  private String name;
  private int x;
  private int y;
  private int width;
  private int height;

  public DiagramNodeInfo()
  {
  }

  public DiagramNodeInfo(String name, int x, int y, int width, int height)
  {
    this.height = height;
    this.name = name;
    this.width = width;
    this.x = x;
    this.y = y;
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public int getX()
  {
    return this.x;
  }

  public void setX(int x)
  {
    this.x = x;
  }

  public int getY()
  {
    return this.y;
  }

  public void setY(int y)
  {
    this.y = y;
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
}