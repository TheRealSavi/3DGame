package engineTester;

import entities.Entity;
import entities.EntityRenderer;
import entities.Player;
import guis.GuiRenderer;
import models.Model;
import models.RawModel;
import objConverter.OBJFileLoader;
import org.joml.Vector3f;
import particles.Particle;
import particles.ParticleRenderer;
import postProcessing.PostProcessor;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import skybox.SkyboxRenderer;
import terrains.HeightsGenerator;
import terrains.Terrain;
import terrains.TerrainRenderer;
import toolBox.FastNoise;
import toolBox.MousePicker;
import water.WaterRenderer;

import static org.lwjgl.glfw.GLFW.*;

public class mainGameLoop {
  
  public static void main(String[] args) {
    
    //create window
    DisplayManager.setSize(2560, 1440);
    DisplayManager.createDisplay();
    
    //prepare terrain and skybox renderers
    TerrainRenderer.loadInUnits();
    SkyboxRenderer.loadInUnits();
    
    //enable culling by default
    MasterRenderer.enableCulling();
    
    //load the main menu
    Game.loadMainMenu();
    DisplayManager.lockCursor();

    HeightsGenerator.setNoiseType(FastNoise.NoiseType.CubicFractal);
    
    //main game loop
    while (!DisplayManager.shouldClose()) {
      DisplayManager.pollSystemEvents(); //allows glfw to do system level window management
      
      // DisplayManager.logFPS(); //logs fps to console
      
      switch (Game.state) {
        case MENU:
          
          MasterRenderer.prepare();
          SkyboxRenderer.render(Game.cameras.get(0), Game.directionalLights.get(0));
          
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_ENTER)) {
            Game.unloadMainMenu();
            Game.loadGameObjects();
            Game.state = Game.State.GAME;
          }

          break;
        
        case GAME:
          
          for (Entity entity : Game.entities) {
            entity.increaseRotation(0, (float)(10 * DisplayManager.getDeltaTime()), 0);
          }
          
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_Y)) {
            Game.particles.add(new Particle(new Vector3f(20, 20, 20), new Vector3f(0, 300, 0), 5, 0, 1, 4));
          }
          
          MasterRenderer.render();
          
          for (Player player : Game.players) {
            player.checkForUserInput();
          }
          
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_BACKSLASH)) {
            DisplayManager.unlockCursor();
          }
          
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_ESCAPE)) {
            HeightsGenerator.newSeed();
            for (Terrain terrain : Game.terrains) {
              terrain.regenModel();
            }
          }

          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_UP)) {
            HeightsGenerator.setAmplitude(HeightsGenerator.getAmplitude() + (float)(30 * DisplayManager.getDeltaTime()) );
            for (Terrain terrain : Game.terrains) {
              terrain.regenModel();
            }
          }
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_DOWN)) {
            HeightsGenerator.setAmplitude(HeightsGenerator.getAmplitude() - (float)(30 * DisplayManager.getDeltaTime()) );
            for (Terrain terrain : Game.terrains) {
              terrain.regenModel();
            }
          }

          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_MINUS)) {
            HeightsGenerator.setJitter(HeightsGenerator.getJitter() - (float)(0.1 * DisplayManager.getDeltaTime()) );
            for (Terrain terrain : Game.terrains) {
              terrain.regenModel();
            }
          }
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_EQUAL)) {
            HeightsGenerator.setJitter(HeightsGenerator.getJitter() + (float)(0.1 * DisplayManager.getDeltaTime()) );
            for (Terrain terrain : Game.terrains) {
              terrain.regenModel();
            }
          }

          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_PERIOD)) {
            Game.addPlayer2();
          }
          
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_LEFT)) {
            Game.fogDensity -= 0.01 * DisplayManager.getDeltaTime();
            DisplayManager.removeFPSLog();
            System.out.println(Game.fogDensity);
          }
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_RIGHT)) {
            Game.fogDensity += 0.01 * DisplayManager.getDeltaTime();
            DisplayManager.removeFPSLog();
            System.out.println(Game.fogDensity);
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
    ParticleRenderer.cleanUp();
    Game.cameras.forEach((c) -> c.cleanUpFBO());
    Loader.cleanUp();
    DisplayManager.closeDisplay();
    
    DisplayManager.removeFPSLog();
    System.out.println("Clean up complete.");
  }
  
}
