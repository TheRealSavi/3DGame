package engineTester;

import entities.Entity;
import entities.EntityRenderer;
import guis.GuiRenderer;
import postProcessing.PostProcessor;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import skybox.SkyboxRenderer;
import terrains.TerrainRenderer;
import water.WaterRenderer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class mainGameLoop {
  
  public static void main(String[] args) {
    
    //create window
    DisplayManager.setSize(960, 720);
    DisplayManager.createDisplay();
    
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
          
          MasterRenderer.prepare();
          SkyboxRenderer.render(Game.cameras.get(0));
          
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_ENTER)) {
            Game.loadGameObjects();
            Game.state = Game.State.GAME;
          }
          
          break;
        
        case GAME:
          
          for (Entity entity : Game.entities) {
            entity.increaseRotation(0,(float)(10 * DisplayManager.getDeltaTime()), 0);
          }
          
          MasterRenderer.render();
          
          Game.cameras.get(0).checkForUserInput();
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_ESCAPE)) {
            Game.terrains.get(0).regenModel();
          }
          
          break;
      }
      
      DisplayManager.updateDisplay();
    }
    // on close
    PostProcessor.cleanUp();
    
    SkyboxRenderer.cleanUp();
    EntityRenderer.cleanUp();
    TerrainRenderer.cleanUp();
    WaterRenderer.cleanUp();
    GuiRenderer.cleanUp();
    MasterRenderer.cleanUp();
    
    Loader.cleanUp();
    DisplayManager.closeDisplay();
  }
  
}
