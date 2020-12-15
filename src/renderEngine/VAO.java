package renderEngine;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class VAO {

  private final int id;
  private final List<VBO> vbos = new ArrayList<>();

  public VAO() {
    int vaoID = GL30.glGenVertexArrays();
    this.id = vaoID;
  }

  //creates a VBO and stores it in current VAO
  public void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
    VBO vbo = new VBO();
    vbos.add(vbo);
    vbo.bind();

    FloatBuffer buffer = storeDataInFloatBuffer(data);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);

    vbo.unbind();
  }

  //creates a VBO for the indices
  public void bindIndicesBuffer(int[] indices) {
    VBO vbo = new VBO();
    vbos.add(vbo);
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo.getId());

    IntBuffer buffer = storeDataInIntBuffer(indices);
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

    vbo.unbind();
  }



  private IntBuffer storeDataInIntBuffer(int[] data) {
    IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }

  private FloatBuffer storeDataInFloatBuffer(float[] data) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }


  public void bind() {
    GL30.glBindVertexArray(this.id);
  }

  public void unbind() {
    GL30.glBindVertexArray(0);
  }

  public void delete() {
    GL30.glDeleteVertexArrays(this.id);
    for (VBO vbo : vbos) {
      vbo.delete();
    }
  }

  public int vboCount() {
    return vbos.size();
  }

  public VBO getVBO(int index) {
    return vbos.get(index);
  }

  public int getId() {
    return this.id;
  }
}
