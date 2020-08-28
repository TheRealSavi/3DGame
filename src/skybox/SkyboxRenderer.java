package skybox;

import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;

public class SkyboxRenderer {
  
  private static final float SIZE = 500f;
  
  private static final float[] VERTICES = {
    -SIZE, SIZE, -SIZE,
    -SIZE, -SIZE, -SIZE,
    SIZE, -SIZE, -SIZE,
    SIZE, -SIZE, -SIZE,
    SIZE, SIZE, -SIZE,
    -SIZE, SIZE, -SIZE,
    
    -SIZE, -SIZE, SIZE,
    -SIZE, -SIZE, -SIZE,
    -SIZE, SIZE, -SIZE,
    -SIZE, SIZE, -SIZE,
    -SIZE, SIZE, SIZE,
    -SIZE, -SIZE, SIZE,
    
    SIZE, -SIZE, -SIZE,
    SIZE, -SIZE, SIZE,
    SIZE, SIZE, SIZE,
    SIZE, SIZE, SIZE,
    SIZE, SIZE, -SIZE,
    SIZE, -SIZE, -SIZE,
    
    -SIZE, -SIZE, SIZE,
    -SIZE, SIZE, SIZE,
    SIZE, SIZE, SIZE,
    SIZE, SIZE, SIZE,
    SIZE, -SIZE, SIZE,
    -SIZE, -SIZE, SIZE,
    
    -SIZE, SIZE, -SIZE,
    SIZE, SIZE, -SIZE,
    SIZE, SIZE, SIZE,
    SIZE, SIZE, SIZE,
    -SIZE, SIZE, SIZE,
    -SIZE, SIZE, -SIZE,
    
    -SIZE, -SIZE, -SIZE,
    -SIZE, -SIZE, SIZE,
    SIZE, -SIZE, -SIZE,
    SIZE, -SIZE, -SIZE,
    -SIZE, -SIZE, SIZE,
    SIZE, -SIZE, SIZE
  };
  
  private static final String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
  private static final String[] NIGHT_TEXTURE_FILES = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"};
  private static final RawModel CUBE = Loader.loadToVAO(VERTICES, 3);
  
  private final int textureID;
  private final int nightTextureID;
  private final SkyboxShader shader;
  
  public SkyboxRenderer(SkyboxShader shader) {
    this.shader = shader;
    
    this.textureID = Loader.loadCubeMap(TEXTURE_FILES);
    this.nightTextureID = Loader.loadCubeMap(NIGHT_TEXTURE_FILES);
    
    shader.start();
    shader.connectTextureUnits();
    shader.stop();
  }
  
  public void render() {
    
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    
    GL30.glBindVertexArray(CUBE.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    
    bindTextures();
    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, CUBE.getVertexCount());
    
    GL20.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);
    
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    
  }
  
  private void bindTextures() {
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, nightTextureID);
    shader.loadBlendFactor(0.0f);
  }
}
