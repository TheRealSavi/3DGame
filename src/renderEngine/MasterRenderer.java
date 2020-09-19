package renderEngine;

import engineTester.Game;
import entities.Camera;
import entities.Entity;
import entities.EntityRenderer;
import guis.GuiRenderer;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import screenRenderers.SingleScreenRenderer;
import screenRenderers.SplitScreenRenderer;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import terrains.TerrainRenderer;
import water.WaterRenderer;
import water.WaterTile;

public class MasterRenderer {
  
  public static void enableCulling() {
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glCullFace(GL11.GL_BACK);
  }
  
  public static void disableCulling() {
    GL11.glDisable(GL11.GL_CULL_FACE);
  }
  
  public static void prepare() {
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glClearColor(1, 1, 1, 1);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
  }
  
  public static void renderScene(Camera camera, boolean doWaterRendering, Vector4f clippingPlane) {
    for (Entity entity : Game.entities) {
      EntityRenderer.addEntity(entity);
    }
    for (Terrain terrain : Game.terrains) {
      TerrainRenderer.addTerrain(terrain);
    }
    if (doWaterRendering) {
      for (WaterTile water : Game.waters) {
        WaterRenderer.addWater(water);
      }
    }
    
    camera.bindFBO();
    MasterRenderer.prepare();
    
    SkyboxRenderer.render(camera, Game.directionalLights.get(0));
    EntityRenderer.render(camera, Game.pointLights, Game.directionalLights, clippingPlane);
    TerrainRenderer.render(camera, Game.pointLights, Game.directionalLights, clippingPlane);
    if (doWaterRendering) {
      WaterRenderer.render(camera, Game.pointLights, Game.directionalLights);
    }
    
    camera.unbindFBO();
  }
  
  public static void render() {
    
    for (Camera camera : Game.cameras) {
      for (WaterTile water : Game.waters) {
        WaterRenderer.addWater(water);
      }
      WaterRenderer.renderTextures(camera);
    }
    
    WaterRenderer.updateMoveFactor();
    
    for (Camera camera : Game.cameras) {
      renderScene(camera, true, new Vector4f(0, 0, 0, 0));
      camera.doPostProcessing();
    }
    
    if (Game.players.size() >= 2)
      SplitScreenRenderer.render();
    else if (Game.players.size() == 1) {
      SingleScreenRenderer.render();
    }
    
    GuiRenderer.render(Game.guis);
  }
  
}
