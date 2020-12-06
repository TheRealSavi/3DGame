package models;

import renderEngine.VAO;

public class RawModel {
  
  private final VAO vao;
  private final int vertexCount;
  
  public RawModel(VAO vao, int vertexCount) {
    this.vao = vao;
    this.vertexCount = vertexCount;
  }
  
  public VAO getVAO() {
    return vao;
  }
  
  public int getVertexCount() {
    return vertexCount;
  }
  
}
