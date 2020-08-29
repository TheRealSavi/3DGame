package engineTester;

import entities.Camera;
import entities.Entity;
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
import terrains.Terrain;
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
    
    //create the master renderer
    MasterRenderer renderer = new MasterRenderer();
    
    Game.loadLightsAndCamera();
    
    
    List<WaterTile> waters = new ArrayList<>();
    WaterTile water = new WaterTile(400, -400, -7);
    waters.add(water);
    
    while (!DisplayManager.shouldClose()) {
      DisplayManager.pollSystemEvents();
      
      DisplayManager.logFPS();
      
      switch (Game.state) {
        case MENU:
          
          renderer.render(Game.lights, Game.cameras.get(0), new Vector4f(0, 0, 0, 0));
          
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
            renderer.processEntity(entity);
          }
          for (Terrain terrain : Game.terrains) {
            renderer.processTerrain(terrain);
          }
          
          float distanceToMove = 2 * (Game.cameras.get(0).getPosition().y - water.getHeight());
          Camera reflectionCamera = new Camera(Game.cameras.get(0).getFOV(), Game.cameras.get(0).getNEAR_PLANE(), Game.cameras.get(0).getFAR_PLANE());
          reflectionCamera.setPosition(Game.cameras.get(0).getPosition());
          reflectionCamera.setPosition(new Vector3f(reflectionCamera.getPosition().x, reflectionCamera.getPosition().y - distanceToMove, reflectionCamera.getPosition().z));
          reflectionCamera.setPitch(-Game.cameras.get(0).getPitch());
          reflectionCamera.setYaw(Game.cameras.get(0).getYaw());
          reflectionCamera.setRoll(Game.cameras.get(0).getRoll());
          
          renderer.render(Game.lights, reflectionCamera, new Vector4f(0, 1, 0, -water.getHeight()));
          WaterRenderer.unbindReflectionFBO();
          
          WaterRenderer.bindRefractionFBO();
          for (Entity entity : Game.entities) {
            renderer.processEntity(entity);
          }
          for (Terrain terrain : Game.terrains) {
            renderer.processTerrain(terrain);
          }
          renderer.render(Game.lights, Game.cameras.get(0), new Vector4f(0, -1, 0, water.getHeight()));
          WaterRenderer.unbindRefractionFBO();
          
          GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
          
          sceneFBO.bindFrameBuffer();
          for (Entity entity : Game.entities) {
            renderer.processEntity(entity);
          }
          for (Terrain terrain : Game.terrains) {
            renderer.processTerrain(terrain);
          }
          renderer.render(Game.lights, Game.cameras.get(0), new Vector4f(0, 0, 0, 0));
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
    
    renderer.cleanUp();
    GuiRenderer.cleanUp();
    WaterRenderer.cleanUp();
    
    Loader.cleanUp();
    DisplayManager.closeDisplay();
  }
  
}
