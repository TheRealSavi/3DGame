package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Player;
import guis.Gui;
import lights.DirectionalLight;
import lights.PointLight;
import models.Model;
import models.RawModel;
import objConverter.OBJFileLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import particles.Particle;
import postProcessing.Fbo;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import terrains.Terrain;
import terrains.TerrainTexturePack;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;

public class Game {
  
  enum State {MENU, GAME}
  
  public static State state = State.MENU;
  
  public static List<PointLight> pointLights = new ArrayList<>();
  public static List<DirectionalLight> directionalLights = new ArrayList<>();
  public static List<Camera> cameras = new ArrayList<>();
  public static List<Entity> entities = new ArrayList<>();
  public static List<Player> players = new ArrayList<>();
  public static List<Terrain> terrains = new ArrayList<>();
  public static List<Particle> particles = new ArrayList<>();
  public static List<Gui> guis = new ArrayList<>();
  public static List<WaterTile> waters = new ArrayList<>();

  public static Entity fernEntity;
  
  // public static float fogDensity = 0.01146f;
  public static float fogDensity = 0.005060278f;
  
  
  public static void loadMainMenu() {
    //create a camera
    Camera camera = new Camera(90, 1, 3500);
    camera.setPosition(new Vector3f(0, 100, 0));
    camera.setPitch(15);
    camera.setYaw(45);
    cameras.add(camera);
    
    //create a light
    directionalLights.add(new DirectionalLight(new Vector3f(0.4596f, 0.8151f, -0.3526f), new Vector3f(1.0f, 0.9f, 0.7f)));
  }
  
  public static void unloadMainMenu() {
    cameras.clear();
    directionalLights.clear();
  }
  
  public static void addPlayer2() {
    if (players.size() == 1) {
      //create fern model
      RawModel rawFernModel = OBJFileLoader.loadOBJ("fern");
      Model fernModel = new Model(Loader.loadTexture("fern"), rawFernModel);
      fernModel.setShineDamper(5);
      fernModel.setReflectivity(0f);
      fernModel.setTransparency(true);
      fernModel.setUseFakeLighting(false);
      
      int[] size = DisplayManager.getFrameBufferSize();
      //create player2
      Player player2 = new Player(fernModel, new Vector3f(50, 10, -50), 0f, 0f, 0f, 0.25f);
      entities.add(player2);
      players.add(player2);
      player2.getCamera().setFBO(new Fbo(size[0], size[1], size[0] / 2, size[1], size[0] / 2, 0, Fbo.DEPTH_RENDER_BUFFER));
      cameras.add(player2.getCamera());
      players.get(0).getCamera().setFBO(new Fbo(size[0], size[1], size[0] / 2, size[1], 0, 0, Fbo.DEPTH_RENDER_BUFFER));
      //update player1's fbo size and viewport
    }
    
  }
  
  
  public static void loadGameObjects() {
    DisplayManager.removeFPSLog();
    System.out.println("Loading game objects...");
    
    //create lights
    directionalLights.add(new DirectionalLight(new Vector3f(0.4596f, 0.8151f, -0.3526f), new Vector3f(1.0f, 0.9f, 0.7f)));
    pointLights.add(new PointLight(new Vector3f(185, 202, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
    pointLights.add(new PointLight(new Vector3f(370, 202, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
    pointLights.add(new PointLight(new Vector3f(293, 202, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
    
    //create fern model
    RawModel rawFernModel = OBJFileLoader.loadOBJ("fern");
    Model fernModel = new Model(Loader.loadTexture("fern"), rawFernModel);
    fernModel.setShineDamper(5);
    fernModel.setReflectivity(0f);
    fernModel.setTransparency(true);
    fernModel.setUseFakeLighting(false);

    fernEntity = new Entity(fernModel, new Vector3f(0,0,0), 0, 0, 0, 1);
    entities.add(fernEntity);
    
    //create stall model
    RawModel rawStallModel = OBJFileLoader.loadOBJ("stall");
    Model stallModel = new Model(Loader.loadTexture("stallTexture"), rawStallModel);
    stallModel.setShineDamper(5);
    stallModel.setReflectivity(1f);
    stallModel.setTransparency(false);
    stallModel.setUseFakeLighting(false);
    
    //create a terrain texture
    int backgroundTextureID = Loader.loadTexture("grass");
    int rTextureID = Loader.loadTexture("mud");
    int gTextureID = Loader.loadTexture("grassFlowers");
    int bTextureID = Loader.loadTexture("path");
    int blendMapID = Loader.loadTexture("blendMap");
    TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTextureID, rTextureID, gTextureID, bTextureID);
    
    //create a gui
    Gui gui = new Gui(Loader.loadTexture("cross"), new Vector2f(0f, 0f), new Vector2f(0.02f, 0.02f));
    // guis.add(gui);
    
    int[] size = DisplayManager.getFrameBufferSize();
    //create player1
    Player player1 = new Player(fernModel, new Vector3f(0, 10, 0), 0f, 0f, 0f, 0.25f);
    entities.add(player1);
    players.add(player1);
    cameras.add(player1.getCamera());
    
    //create a whole frack ton of stall entities
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        for (int k = 0; k < 5; k++) {
          entities.add(new Entity(stallModel, new Vector3f(i * 50, j * 50, k * -50), 0, 0, 0, 1.0f));
        }
      }
    }
    
    //create terrains and waters
    for (int i = -2; i < 2; i++) {
      for (int j = -2; j < 2; j++) {
        terrains.add(new Terrain(i, j, texturePack, blendMapID));
        waters.add(new WaterTile(400 + i * 800, 400 + j * 800, -7));
      }
    }
    
    DisplayManager.removeFPSLog();
    System.out.println("Done!");
    
  }
  
}
