package skybox;

import engineTester.Game;
import entities.Camera;
import lights.DirectionalLight;
import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;

public class SkyboxRenderer {
  
  private static final SkyboxShader shader = new SkyboxShader();
  
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
  
  private static final int textureID = Loader.loadCubeMap(TEXTURE_FILES);
  private static final int nightTextureID = Loader.loadCubeMap(NIGHT_TEXTURE_FILES);
  
  public static void loadInUnits() {
    shader.start();
    shader.connectTextureUnits();
    shader.stop();
  }
  
  public static void render(Camera camera, DirectionalLight directionalLight) {
    shader.start();
    shader.loadProjectionMatrix(camera.getProjectionMatrix());
    shader.loadViewMatrix(camera);
    shader.loadCameraPosition(camera.getPosition());
    shader.loadDirectionalLight(directionalLight);
    shader.loadFogDensity(Game.fogDensity);
    
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    
    GL30.glBindVertexArray(CUBE.getVAO().getId());
    GL20.glEnableVertexAttribArray(0);
    
    bindTextures();
    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, CUBE.getVertexCount());
    
    GL20.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);
    
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    
    shader.stop();
    
  }
  
  private static void bindTextures() {
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, nightTextureID);
    shader.loadBlendFactor(0.0f);
  }
  
  public static void rotateSkybox() {
    shader.rotateSkybox();
  }
  
  public static void cleanUp() {
    shader.cleanUp();
  }
}
