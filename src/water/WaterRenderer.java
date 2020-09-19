package water;

import engineTester.Game;
import entities.Camera;
import lights.DirectionalLight;
import lights.PointLight;
import models.RawModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import postProcessing.Fbo;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import toolBox.Maths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaterRenderer {
  
  private static final float[] vertices = {-1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1};
  private static final RawModel quad = Loader.loadToVAO(vertices, 2);
  
  private static final WaterShader shader = new WaterShader();
  private static final int distortionMap = Loader.loadTexture("waterDistortionMap");
  private static final float MOVE_SPEED = 0.03f;
  private static float currentMoveFactor = 0;
  
  private static final Map<Camera, List<Fbo>> waterTextures = new HashMap<>();
  
  private static final List<WaterTile> waters = new ArrayList<>();
  
  public static void renderTextures(Camera camera) {
    if (waterTextures.get(camera) == null) {
      List<Fbo> newTargets = new ArrayList<>();
      int[] size = DisplayManager.getFrameBufferSize();
      newTargets.add(new Fbo(size[0] / 2, size[1] / 2, Fbo.DEPTH_RENDER_BUFFER)); //reflection
      newTargets.add(new Fbo(size[0] / 2, size[1] / 2, Fbo.DEPTH_TEXTURE)); // refraction
      waterTextures.put(camera, newTargets);
    }
    
    GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
    
    List<Fbo> renderTargets = waterTextures.get(camera);
    for (WaterTile water : waters) {
      loadReflectionTex(water, renderTargets.get(0), camera);
      loadRefractionTex(water, renderTargets.get(1), camera);
    }
    waters.clear();
    
    GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
  }
  
  public static void render(Camera camera, List<PointLight> pointLights, List<DirectionalLight> directionalLights) {
    shader.start();
    
    GL30.glBindVertexArray(quad.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, waterTextures.get(camera).get(0).getColorTexture());
    
    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, waterTextures.get(camera).get(1).getColorTexture());
    
    GL13.glActiveTexture(GL13.GL_TEXTURE2);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, distortionMap);
    
    GL13.glActiveTexture(GL13.GL_TEXTURE3);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, waterTextures.get(camera).get(1).getDepthTexture());
    
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    
    shader.connectTextureUnits();
    
    shader.loadProjectionMatrix(camera.getProjectionMatrix());
    shader.loadViewMatrix(camera);
    shader.loadCameraPosition(camera.getPosition());
    
    shader.loadShineVariables(30f, 0.5f);
    shader.loadPointLights(pointLights);
    shader.loadDirectionalLights(directionalLights);
    
    shader.loadFogDensity(Game.fogDensity);
    
    shader.loadMoveFactor(currentMoveFactor);
    
    for (WaterTile tile : waters) {
      Matrix4f modelMatrix = Maths.createModelMatrix(new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0, WaterTile.TILE_SIZE);
      shader.loadModelMatrix(modelMatrix);
      GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
    }
    
    GL11.glDisable(GL11.GL_BLEND);
    
    GL20.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);
    
    shader.stop();
    
    waters.clear();
  }
  
  public static void updateMoveFactor() {
    currentMoveFactor += MOVE_SPEED * DisplayManager.getDeltaTime();
    currentMoveFactor %= 1;
  }
  
  public static void addWater(WaterTile water) {
    waters.add(water);
  }
  
  private static void loadReflectionTex(WaterTile water, Fbo reflectionFBO, Camera camera) {
    Vector4f clippingPlane = new Vector4f(0, 1, 0, -water.getHeight());//- 1.0f);
    
    Fbo oldFbo = camera.swapFBO(reflectionFBO);
    camera.reflectY(water.getHeight());
    MasterRenderer.renderScene(camera, false, clippingPlane);
    camera.reflectY(water.getHeight());
    camera.swapBackFBO(oldFbo);
  }
  
  private static void loadRefractionTex(WaterTile water, Fbo refractionFBO, Camera camera) {
    Vector4f clippingPlane = new Vector4f(0, -1, 0, water.getHeight() + 1.0f);
    
    Fbo oldFbo = camera.swapFBO(refractionFBO);
    MasterRenderer.renderScene(camera, false, clippingPlane);
    camera.swapBackFBO(oldFbo);
  }
  
  public static void cleanUp() {
    waterTextures.values().forEach((n) -> n.forEach((o) -> o.cleanUp()));
    waterTextures.clear();
    shader.cleanUp();
  }
  
}
