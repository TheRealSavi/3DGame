package renderEngine;

import entities.*;
import models.Model;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import skybox.SkyboxRenderer;
import skybox.SkyboxShader;
import terrains.Terrain;
import terrains.TerrainRenderer;
import terrains.TerrainShader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {
  
  //private static final Vector3f skyColor = new Vector3f(0.25f, 0.27f, 0.33f);
  
  private final EntityShader entityShader = new EntityShader();
  private final EntityRenderer entityRenderer;
  
  private final TerrainShader terrainShader = new TerrainShader();
  private final TerrainRenderer terrainRenderer;
  
  private final SkyboxShader skyboxShader = new SkyboxShader();
  private final SkyboxRenderer skyboxRenderer;
  
  private final Map<Model, List<Entity>> entities = new HashMap<>();
  private final List<Terrain> terrains = new ArrayList<>();
  
  public MasterRenderer() {
    enableCulling();
    entityRenderer = new EntityRenderer(entityShader);
    terrainRenderer = new TerrainRenderer(terrainShader);
    skyboxRenderer = new SkyboxRenderer(skyboxShader);
  }
  
  public static void enableCulling() {
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glCullFace(GL11.GL_BACK);
  }
  
  public static void disableCulling() {
    GL11.glDisable(GL11.GL_CULL_FACE);
  }
  
  public void render(List<Light> lights, Camera camera, Vector4f clippingPlane) {
    prepare();
    
    skyboxShader.start();
    skyboxShader.loadProjectionMatrix(camera.getProjectionMatrix());
    //skyboxShader.loadFogColor(skyColor);
    skyboxShader.loadViewMatrix(camera);
    skyboxRenderer.render();
    skyboxShader.stop();
    
    entityShader.start();
    entityShader.loadClippingPlane(clippingPlane);
    entityShader.loadProjectionMatrix(camera.getProjectionMatrix());
    //entityShader.loadSkyColor(skyColor);
    entityShader.loadLights(lights);
    entityShader.loadViewMatrix(camera);
    entityRenderer.render(entities);
    entityShader.stop();
    
    terrainShader.start();
    terrainShader.loadClippingPlane(clippingPlane);
    terrainShader.loadProjectionMatrix(camera.getProjectionMatrix());
    //terrainShader.loadSkyColor(skyColor);
    terrainShader.loadLights(lights);
    terrainShader.loadViewMatrix(camera);
    terrainRenderer.render(terrains);
    terrainShader.stop();
    
    entities.clear();
    terrains.clear();
  }
  
  public void processEntity(Entity entity) {
    Model entityModel = entity.getModel();
    List<Entity> batch = entities.get(entityModel);
    if (batch != null) {
      batch.add(entity);
    } else {
      List<Entity> newBatch = new ArrayList<>();
      newBatch.add(entity);
      entities.put(entityModel, newBatch);
    }
  }
  
  public void processTerrain(Terrain terrain) {
    terrains.add(terrain);
  }
  
  public void cleanUp() {
    entityShader.cleanUp();
    terrainShader.cleanUp();
  }
  
  public void prepare() {
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    GL11.glClearColor(0, 0, 1, 1);
  }
  
}
