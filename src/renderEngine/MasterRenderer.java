package renderEngine;

import engineTester.Game;
import entities.Entity;
import entities.EntityRenderer;
import guis.GuiRenderer;
import org.lwjgl.opengl.GL11;
import postProcessing.Fbo;
import postProcessing.PostProcessor;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import terrains.TerrainRenderer;
import water.WaterRenderer;
import water.WaterTile;

public class MasterRenderer {
  
  private static final int[] size = DisplayManager.getFrameBufferSize();
  private static final Fbo sceneFBO = new Fbo(size[0], size[1], Fbo.DEPTH_RENDER_BUFFER);
  
  public static void enableCulling() {
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glCullFace(GL11.GL_BACK);
  }
  
  public static void disableCulling() {
    GL11.glDisable(GL11.GL_CULL_FACE);
  }
  
  public static void prepare() {
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    GL11.glClearColor(1, 1, 1, 1);
  }
  
  public static void render() {
    
    for (WaterTile water : Game.waters) {
      WaterRenderer.addWater(water);
    }
    
    WaterRenderer.renderTextures();
    
    for (Entity entity : Game.entities) {
      EntityRenderer.addEntity(entity);
    }
    for (Terrain terrain : Game.terrains) {
      TerrainRenderer.addTerrain(terrain);
    }
    
    sceneFBO.bindFrameBuffer();
    
    MasterRenderer.prepare();
    SkyboxRenderer.render(Game.cameras.get(0));
    EntityRenderer.render(Game.cameras.get(0), Game.lights);
    TerrainRenderer.render(Game.cameras.get(0), Game.lights);
    WaterRenderer.render(Game.cameras.get(0), Game.lights);
    
    sceneFBO.unbindFrameBuffer();
    
    PostProcessor.doPostProcessing(sceneFBO.getColorTexture());
    
    GuiRenderer.render(Game.guis);
  }
  
  public static void cleanUp() {
    sceneFBO.cleanUp();
  }
  
}
