package engineTester;

import entities.Entity;

import guis.GuiRenderer;

import postProcessing.Fbo;
import postProcessing.PostProcessor;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;


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
    
    while (!DisplayManager.shouldClose()) {
      DisplayManager.pollSystemEvents();
      
      DisplayManager.logFPS();
      
      switch (Game.state) {
        case MENU:
          
          renderer.render(Game.lights, Game.cameras.get(0));
          
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_ENTER)) {
            Game.loadGameObjects();
            Game.state = Game.State.GAME;
          }
          
          break;
        
        case GAME:
          
          for (Entity entity: Game.entities) {
            entity.increaseRotation(entity.getPosition().x / 500,entity.getPosition().y / 500,entity.getPosition().z / 500);
            renderer.processEntity(entity);
          }
          
          sceneFBO.bindFrameBuffer();
          renderer.render(Game.lights, Game.cameras.get(0));
          sceneFBO.unbindFrameBuffer();
          PostProcessor.doPostProcessing(sceneFBO.getColorTexture());
          
          GuiRenderer.render(Game.guis);
  
          Game.cameras.get(0).checkForUserInput();
  
          if(DisplayManager.getInput().isKeyDown(GLFW_KEY_ESCAPE)) {
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
    Loader.cleanUp();
    DisplayManager.closeDisplay();
  }
  
}
