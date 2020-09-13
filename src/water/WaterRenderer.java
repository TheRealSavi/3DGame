package water;

import engineTester.Game;
import entities.Camera;
import entities.Entity;
import entities.EntityRenderer;
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
import skybox.SkyboxRenderer;
import terrains.Terrain;
import terrains.TerrainRenderer;
import toolBox.Maths;

import java.util.ArrayList;
import java.util.List;

public class WaterRenderer {
  
  private static final float[] vertices = {-1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1};
  private static final RawModel quad = Loader.loadToVAO(vertices, 2);
  
  private static final WaterShader shader = new WaterShader();
  private static final int distortionMap = Loader.loadTexture("waterDistortionMap");
  private static final float MOVE_SPEED = 0.03f;
  private static float currentMoveFactor = 0;
  
  private static final int[] size = DisplayManager.getFrameBufferSize();
  public static final Fbo reflection = new Fbo(size[0] / 2, size[1] / 2, Fbo.DEPTH_RENDER_BUFFER);
  private static final Fbo refraction = new Fbo(size[0] / 2, size[1] / 2, Fbo.DEPTH_TEXTURE);
  
  private static final List<WaterTile> waters = new ArrayList<>();
  
  public static void renderTextures() {
    GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
    
    for (WaterTile water : waters) {
      loadReflectionTex(water);
      loadRefractionTex(water);
    }
    
    GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
  }
  
  public static void render(Camera camera, List<PointLight> pointLights, List<DirectionalLight> directionalLights) {
    shader.start();
    
    GL30.glBindVertexArray(quad.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, reflection.getColorTexture());
    
    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, refraction.getColorTexture());
    
    GL13.glActiveTexture(GL13.GL_TEXTURE2);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, distortionMap);
  
    GL13.glActiveTexture(GL13.GL_TEXTURE3);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, refraction.getDepthTexture());
  
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
    
    currentMoveFactor += MOVE_SPEED * DisplayManager.getDeltaTime();
    currentMoveFactor %= 1;
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
  
  public static void addWater(WaterTile water) {
    waters.add(water);
  }
  
  private static void loadReflectionTex(WaterTile water) {
    reflection.bindFrameBuffer();
    
    for (Entity entity : Game.entities) {
      EntityRenderer.addEntity(entity);
    }
    for (Terrain terrain : Game.terrains) {
      TerrainRenderer.addTerrain(terrain);
    }
    
    float distanceToMove = 2 * (Game.cameras.get(0).getPosition().y - water.getHeight());
    Camera reflectionCamera = new Camera(Game.cameras.get(0).getFOV(), Game.cameras.get(0).getNEAR_PLANE(), Game.cameras.get(0).getFAR_PLANE());
    reflectionCamera.setPosition(Game.cameras.get(0).getPosition());
    reflectionCamera.setPosition(new Vector3f(reflectionCamera.getPosition().x, reflectionCamera.getPosition().y - distanceToMove, reflectionCamera.getPosition().z));
    reflectionCamera.setPitch(-Game.cameras.get(0).getPitch());
    reflectionCamera.setYaw(Game.cameras.get(0).getYaw());
    reflectionCamera.setRoll(Game.cameras.get(0).getRoll());
    
    Vector4f clippingPlane = new Vector4f(0, 1, 0, -water.getHeight() );//- 1.0f);
    
    MasterRenderer.prepare();
    SkyboxRenderer.render(reflectionCamera, Game.directionalLights.get(0));
    EntityRenderer.render(reflectionCamera, Game.pointLights, Game.directionalLights, clippingPlane);
    TerrainRenderer.render(reflectionCamera, Game.pointLights, Game.directionalLights, clippingPlane);
    
    reflection.unbindFrameBuffer();
  }
  
  private static void loadRefractionTex(WaterTile water) {
    refraction.bindFrameBuffer();
    
    for (Entity entity : Game.entities) {
      EntityRenderer.addEntity(entity);
    }
    for (Terrain terrain : Game.terrains) {
      TerrainRenderer.addTerrain(terrain);
    }
    
    Vector4f clippingPlane = new Vector4f(0, -1, 0, water.getHeight() + 1.0f);
    
    MasterRenderer.prepare();
    SkyboxRenderer.render(Game.cameras.get(0), Game.directionalLights.get(0));
    EntityRenderer.render(Game.cameras.get(0), Game.pointLights, Game.directionalLights, clippingPlane);
    TerrainRenderer.render(Game.cameras.get(0), Game.pointLights, Game.directionalLights, clippingPlane);
    
    refraction.unbindFrameBuffer();
  }
  
  public static void cleanUp() {
    refraction.cleanUp();
    reflection.cleanUp();
    shader.cleanUp();
  }
  
}
