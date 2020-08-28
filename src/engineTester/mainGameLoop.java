package engineTester;

import entities.Entity;
import entities.Camera;
import entities.Light;
import models.Model;
import models.RawModel;

import terrains.Terrain;
import terrains.TerrainTexturePack;

import guis.Gui;
import guis.GuiRenderer;

import objConverter.OBJFileLoader;

import postProcessing.Fbo;
import postProcessing.PostProcessor;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;

import org.joml.Vector2f;
import org.joml.Vector3f;

import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class mainGameLoop {
  
  public enum State {MENU, GAME}
  
  public static void main(String[] args) {
    if (!glfwInit()) {
      throw new IllegalStateException("Failed to initialize GLFW!");
    }
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
    // -- end glfw initialization -- \\
    
    //create window
    DisplayManager.setSize(1280, 720);
    DisplayManager.createDisplay();
    GL.createCapabilities();
    
    //enter the menu state
    State state = State.MENU;
    
    //create stall
    RawModel rawStallModel = OBJFileLoader.loadOBJ("stall");
    Model stallModel = new Model(Loader.loadTexture("stallTexture"), rawStallModel);
    stallModel.setShineDamper(5);
    stallModel.setReflectivity(1f);
    stallModel.setTransparency(false);
    stallModel.setUseFakeLighting(false);
    
    Entity stall = new Entity(stallModel, new Vector3f(50, 0, -300), 0, 0, 0, 2.0f);
    
    //create sky fern
    RawModel rawFernModel = OBJFileLoader.loadOBJ("fern");
    Model fernModel = new Model(Loader.loadTexture("fern"), rawFernModel);
    fernModel.setShineDamper(5);
    fernModel.setReflectivity(0f);
    fernModel.setTransparency(true);
    fernModel.setUseFakeLighting(false);
    
    Entity fern = new Entity(fernModel, new Vector3f(400, 400, -390), 0, 0, 0, 2.0f);
    
    //create lights
    List<Light> lights = new ArrayList<>();
    lights.add(new Light(new Vector3f(400, 800, -400), new Vector3f(1.0f, 1.0f, 1.2f)));
    lights.add(new Light(new Vector3f(185, 10, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
    lights.add(new Light(new Vector3f(370, 17, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
    lights.add(new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
    
    //create terrain
    int backgroundTextureID = Loader.loadTexture("grass");
    int rTextureID = Loader.loadTexture("mud");
    int gTextureID = Loader.loadTexture("grassFlowers");
    int bTextureID = Loader.loadTexture("path");
    int blendMapID = Loader.loadTexture("blendMap");
    TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTextureID, rTextureID, gTextureID, bTextureID);
  
    Entity[] entities = new Entity[12 * 12 * 12];
    for (int i = 0; i < 12; i++) {
      for (int j = 0; j < 12; j++) {
        for (int k = 0; k < 12; k++) {
          RawModel raw = OBJFileLoader.loadOBJ("stall");
          Model model = new Model(Loader.loadTexture("stallTexture"), raw);
          model.setShineDamper(5);
          model.setReflectivity(1f);
          model.setTransparency(false);
          model.setUseFakeLighting(false);
          entities[i * 12 * 12 + j * 12 + k] = new Entity(model, new Vector3f(i * 50, j * 50, k * -50), 0, 0, 0, 1.0f);
        }
      }
    }
    
    //create camera
    Camera camera = new Camera(90, 1, 2000);
    camera.setPosition(new Vector3f(0, 100, 0));
    camera.setPitch(15);
    camera.setYaw(45);
    
    Camera camera2 = new Camera(90, 1, 2000);
    camera2.setPosition(new Vector3f(400, 100, -400));
    camera2.setPitch(15);
    camera2.setYaw(45);
    
    
    //create gui
    List<Gui> guis = new ArrayList<>();
    Gui gui = new Gui(Loader.loadTexture("cross"), new Vector2f(0f, 0f), new Vector2f(0.02f, 0.02f));
    guis.add(gui);
    
    int[] size = DisplayManager.getFrameBufferSize();
    Fbo sceneFBO = new Fbo(size[0], size[1], Fbo.DEPTH_RENDER_BUFFER);
    
    //create the master renderer
    MasterRenderer renderer = new MasterRenderer();
    
    Camera usingCamera = camera;
    
    while (!DisplayManager.shouldClose()) {
      System.out.println(DisplayManager.getCurrentFPS());
      glfwPollEvents();
      
      switch (state) {
        case MENU:
          
          if (DisplayManager.getInput().isKeyDown(GLFW_KEY_ENTER)) {
            state = State.GAME;
          }
          renderer.render(lights, camera);
          break;
        
        case GAME:
          camera.checkForUserInput();
          
          //stall.increasePosition((float)(20 * DisplayManager.getDeltaTime()), 0, 0);
          
         // renderer.processEntity(stall);
         // renderer.processEntity(fern);
          
          for (Entity entity: entities) {
            entity.increaseRotation(1,0,0);
            renderer.processEntity(entity);
          }
          
          sceneFBO.bindFrameBuffer();
          renderer.render(lights, usingCamera);
          sceneFBO.unbindFrameBuffer();
          PostProcessor.doPostProcessing(sceneFBO.getColorTexture());
          
          GuiRenderer.render(guis);
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
