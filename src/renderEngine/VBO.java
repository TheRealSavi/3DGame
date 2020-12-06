package renderEngine;

import org.lwjgl.opengl.GL15;

public class VBO {

  private int id;

  public VBO() {
    int vboID = GL15.glGenBuffers();
    this.id = vboID;
  }

  public void bind() {
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.id);
  }

  public void unbind() {
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
  }

  public int getId() {
    return this.id;
  }

  public void delete() {
    GL15.glDeleteBuffers(this.id);
  }
}
