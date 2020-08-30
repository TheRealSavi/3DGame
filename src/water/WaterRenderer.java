package water;

import engineTester.Game;
import entities.Camera;
import entities.Entity;
import entities.EntityRenderer;
import entities.Light;
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
  private static final Fbo reflection = new Fbo(size[0] / 4, size[1] / 4, Fbo.NONE);
  private static final Fbo refraction = new Fbo(size[0] / 4, size[1] / 4, Fbo.NONE);
  
  private static final List<WaterTile> waters = new ArrayList<>();
  
  public static void renderTextures() {
    GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
    
    for (WaterTile water : waters) {
      loadReflectionTex(water);
      loadRefractionTex(water);
    }
    
    GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
  }
  
  public static void render(Camera camera, List<Light> lights) {
    shader.start();
    
    GL30.glBindVertexArray(quad.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, reflection.getColorTexture());
    
    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, refraction.getColorTexture());
    
    GL13.glActiveTexture(GL13.GL_TEXTURE2);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, distortionMap);
    
    shader.connectTextureUnits();
    
    shader.loadProjectionMatrix(camera.getProjectionMatrix());
    shader.loadViewMatrix(camera);
    shader.loadCameraPosition(camera.getPosition());
    
    shader.loadShineVariables(15f, 0.6f);
    shader.loadLights(lights);
    
    currentMoveFactor += MOVE_SPEED * DisplayManager.getDeltaTime();
    currentMoveFactor %= 1;
    shader.loadMoveFactor(currentMoveFactor);
    
    for (WaterTile tile : waters) {
      Matrix4f modelMatrix = Maths.createTransformationMatrix(new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0, WaterTile.TILE_SIZE);
      shader.loadModelMatrix(modelMatrix);
      GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
    }
    
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
    
    Vector4f clippingPlane = new Vector4f(0, 1, 0, -water.getHeight());
    
    MasterRenderer.prepare();
    SkyboxRenderer.render(reflectionCamera);
    EntityRenderer.render(reflectionCamera, Game.lights, clippingPlane);
    TerrainRenderer.render(reflectionCamera, Game.lights, clippingPlane);
    
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
    
    Vector4f clippingPlane = new Vector4f(0, -1, 0, water.getHeight());
    
    MasterRenderer.prepare();
    //SkyboxRenderer.render(Game.cameras.get(0));
    EntityRenderer.render(Game.cameras.get(0), Game.lights, clippingPlane);
    TerrainRenderer.render(Game.cameras.get(0), Game.lights, clippingPlane);
    
    refraction.unbindFrameBuffer();
  }
  
  public static void cleanUp() {
    refraction.cleanUp();
    reflection.cleanUp();
    shader.cleanUp();
  }
  
}
