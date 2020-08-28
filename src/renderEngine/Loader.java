package renderEngine;

import de.matthiasmann.twl.utils.PNGDecoder;
import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;

public class Loader {
  
  private static final List<Integer> vaos = new ArrayList<>();
  private static final List<Integer> vbos = new ArrayList<>();
  private static final List<Integer> textures = new ArrayList<>();
  
  public static RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
    int vaoID = createVAO();
    bindIndicesBuffer(indices);
    storeDataInAttributeList(0, 3, positions);
    storeDataInAttributeList(1, 2, textureCoords);
    storeDataInAttributeList(2, 3, normals);
    
    unbindVAO();
    return new RawModel(vaoID, (indices.length));
  }
  
  public static RawModel loadToVAO(float[] positions, int dimensions) {
    int vaoID = createVAO();
    storeDataInAttributeList(0, dimensions, positions);
    
    unbindVAO();
    return new RawModel(vaoID, positions.length / dimensions);
  }
  
  public static int loadTexture(String fileName) {
    int textureID = GL11.glGenTextures();
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL13.GL_TEXTURE_2D, textureID);
    
    TextureData data = decodeTextureFile("res/" + fileName + ".png");
    glTexImage2D(GL13.GL_TEXTURE_2D, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL_UNSIGNED_BYTE, data.getBuffer());
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    
    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
    
    textures.add(textureID);
    return textureID;
  }
  
  public static int loadCubeMap(String[] fileNames) {
    int textureID = GL11.glGenTextures();
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
    
    for (int i = 0; i < fileNames.length; i++) {
      TextureData data = decodeTextureFile("res/" + fileNames[i] + ".png");
      GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
    }
    
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    textures.add(textureID);
    return textureID;
  }
  
  private static TextureData decodeTextureFile(String fileName) {
    int width = 0;
    int height = 0;
    ByteBuffer buffer = null;
    try {
      FileInputStream in = new FileInputStream(fileName);
      PNGDecoder decoder = new PNGDecoder(in);
      width = decoder.getWidth();
      height = decoder.getHeight();
      buffer = ByteBuffer.allocateDirect(4 * width * height);
      decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
      buffer.flip();
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Tried to load texture " + fileName + ", didn't work");
      System.exit(-1);
    }
    return new TextureData(buffer, width, height);
  }
  
  private static void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
    int vboID = GL15.glGenBuffers();
    vbos.add(vboID);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
    FloatBuffer buffer = storeDataInFloatBuffer(data);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
    
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // UNBIND CURRENT VBO
  }
  
  public static void cleanUp() {
    for (int vao : vaos) {
      GL30.glDeleteVertexArrays(vao);
    }
    for (int vbo : vbos) {
      GL15.glDeleteBuffers(vbo);
    }
    for (int texture : textures) {
      GL11.glDeleteTextures(texture);
    }
  }
  
  private static int createVAO() {
    int vaoID = GL30.glGenVertexArrays();
    vaos.add(vaoID);
    GL30.glBindVertexArray(vaoID);
    return vaoID;
  }
  
  private static void unbindVAO() {
    GL30.glBindVertexArray(0);
  }
  
  private static void bindIndicesBuffer(int[] indices) {
    int vboID = GL15.glGenBuffers();
    vbos.add(vboID);
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
    IntBuffer buffer = storeDataInIntBuffer(indices);
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
  }
  
  private static IntBuffer storeDataInIntBuffer(int[] data) {
    IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }
  
  private static FloatBuffer storeDataInFloatBuffer(float[] data) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }
  
  private static class TextureData {
    private final int width;
    private final int height;
    private final ByteBuffer buffer;
    
    public TextureData(ByteBuffer buffer, int width, int height) {
      this.buffer = buffer;
      this.width = width;
      this.height = height;
    }
    
    public int getWidth() {
      return width;
    }
    
    public int getHeight() {
      return height;
    }
    
    public ByteBuffer getBuffer() {
      return buffer;
    }
  }
  
}
