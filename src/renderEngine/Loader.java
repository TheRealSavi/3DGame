package renderEngine;

import de.matthiasmann.twl.utils.PNGDecoder;
import models.RawModel;
import org.lwjgl.opengl.*;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;

public class Loader {
  
  private static final List<VAO> vaos = new ArrayList<>();

  private static final List<Integer> textures = new ArrayList<>();
  
  public static RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
    VAO vao = new VAO();
    vaos.add(vao);
    vao.bind();

    vao.bindIndicesBuffer(indices);

    vao.storeDataInAttributeList(0, 3, positions);
    vao.storeDataInAttributeList(1, 2, textureCoords);
    vao.storeDataInAttributeList(2, 3, normals);

    vao.unbind();

    return new RawModel(vao, indices.length);
  }
  
  public static RawModel loadToVAO(float[] positions, int dimensions) {
    VAO vao = new VAO();
    vaos.add(vao);
    vao.bind();

    vao.storeDataInAttributeList(0, dimensions, positions);
    
    vao.unbind();

    return new RawModel(vao, positions.length / dimensions);
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

  
  public static void cleanUp() {
    for (VAO vao : vaos) {
      vao.delete();
    }

    for (int texture : textures) {
      GL11.glDeleteTextures(texture);
    }
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
