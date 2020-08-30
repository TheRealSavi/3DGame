package engineTester;

import entities.Camera;
import entities.Entity;
import entities.EntityRenderer;
import guis.GuiRenderer;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import postProcessing.Fbo;
import postProcessing.PostProcessor;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import terrains.TerrainRenderer;
import water.WaterRenderer;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class mainGameLoop {
  
  public static void main(String[] args) {
    
    //create window
    DisplayManager.setSize(1280, 720);
    DisplayManager.createDisplay();
    
    //create the PostProcessing FBO
    int[] size = DisplayManager.getFrameBufferSize();
    Fbo sceneFBO = new Fbo(size[0], size[1], Fbo.DEPTH_RENDER_BUFFER);
  
    //create some water
    List<WaterTile> waters = new ArrayList<>();
    WaterTile water = new WaterTile(400, -400, -7);
    waters.add(water);
    
    //prepare terrain and skybox renderers
    TerrainRenderer.loadInUnits();
    SkyboxRenderer.loadInUnits();
    
    //pre load lights and cameras
    Game.loadLightsAndCamera();
    
    //enable culling by default
    MasterRenderer.enableCulling();
    
    //main game loop
    while (!DisplayManager.shouldClose()) {
      DisplayManager.pollSystemEvents(); //allows glfw to do system level window management
      
      DisplayManager.logFPS(); //logs fps to console
      
      switch (Game.state) {
        case MENU:
  
          MasterRenderer.prepare(); //clears the render buffer
          SkyboxRenderer.render(Game.cameras.get(0));
          
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_ENTER)) {
            Game.loadGameObjects();
            Game.state = Game.State.GAME;
          }
          
          break;
        
        case GAME:
          
          for (Entity entity : Game.entities) {
            entity.increaseRotation(entity.getPosition().x / 500, entity.getPosition().y / 500, entity.getPosition().z / 500);
          }
          
          GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
          
          WaterRenderer.bindReflectionFBO();
          
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
  
          MasterRenderer.prepare(); //clears the render buffer
          SkyboxRenderer.render(reflectionCamera);
          EntityRenderer.render(reflectionCamera, Game.lights, clippingPlane);
          TerrainRenderer.render(reflectionCamera, Game.lights, clippingPlane);
          WaterRenderer.unbindReflectionFBO();
          
          WaterRenderer.bindRefractionFBO();
          for (Entity entity : Game.entities) {
            EntityRenderer.addEntity(entity);
          }
          for (Terrain terrain : Game.terrains) {
            TerrainRenderer.addTerrain(terrain);
          }
          
          clippingPlane = new Vector4f(0, -1, 0, water.getHeight());
  
          MasterRenderer.prepare(); //clears the render buffer
          //SkyboxRenderer.render(Game.cameras.get(0));
          EntityRenderer.render(Game.cameras.get(0), Game.lights, clippingPlane);
          TerrainRenderer.render(Game.cameras.get(0), Game.lights, clippingPlane);
          WaterRenderer.unbindRefractionFBO();
          
          GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
          
          sceneFBO.bindFrameBuffer();
          for (Entity entity : Game.entities) {
            EntityRenderer.addEntity(entity);
          }
          for (Terrain terrain : Game.terrains) {
            TerrainRenderer.addTerrain(terrain);
          }
          MasterRenderer.prepare(); //clears the render buffer
          SkyboxRenderer.render(Game.cameras.get(0));
          EntityRenderer.render(Game.cameras.get(0), Game.lights);
          TerrainRenderer.render(Game.cameras.get(0), Game.lights);
          WaterRenderer.render(waters, Game.cameras.get(0), Game.lights);
          sceneFBO.unbindFrameBuffer();
          PostProcessor.doPostProcessing(sceneFBO.getColorTexture());
          
          GuiRenderer.render(Game.guis);
          
          Game.cameras.get(0).checkForUserInput();
          
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_ESCAPE)) {
            DisplayManager.unlockCursor();
          }
          
          break;
      }
      
      DisplayManager.updateDisplay();
    }
    // on close
    PostProcessor.cleanUp();
    sceneFBO.cleanUp();
  
    SkyboxRenderer.cleanUp();
    EntityRenderer.cleanUp();
    TerrainRenderer.cleanUp();
    WaterRenderer.cleanUp();
    
    GuiRenderer.cleanUp();
    
    Loader.cleanUp();
    DisplayManager.closeDisplay();
  }
  
}
