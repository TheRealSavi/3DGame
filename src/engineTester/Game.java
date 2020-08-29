package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import guis.Gui;
import models.Model;
import models.RawModel;
import objConverter.OBJFileLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderEngine.Loader;
import terrains.Terrain;
import terrains.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;

public class Game {
  
  enum State {MENU, GAME}
  
  public static State state = State.MENU;
  
  public static List<Light> lights = new ArrayList<>();
  public static List<Camera> cameras = new ArrayList<>();
  public static List<Entity> entities = new ArrayList<>();
  public static List<Terrain> terrains = new ArrayList<>();
  public static List<Gui> guis = new ArrayList<>();
  
  
  public static void loadLightsAndCamera() {
    //create lights
    lights.add(new Light(new Vector3f(400, 800, -400), new Vector3f(1.0f, 1.0f, 1.2f)));
    lights.add(new Light(new Vector3f(185, 10, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
    lights.add(new Light(new Vector3f(370, 17, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
    lights.add(new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
    
    //create camera
    Camera camera = new Camera(90, 1, 2000);
    camera.setPosition(new Vector3f(0, 100, 0));
    camera.setPitch(15);
    camera.setYaw(45);
    
    cameras.add(camera);
  }
  
  public static void loadGameObjects() {
    System.out.println("Loading game objects...");
    
    //create sky fern
    RawModel rawFernModel = OBJFileLoader.loadOBJ("fern");
    Model fernModel = new Model(Loader.loadTexture("fern"), rawFernModel);
    fernModel.setShineDamper(5);
    fernModel.setReflectivity(0f);
    fernModel.setTransparency(true);
    fernModel.setUseFakeLighting(false);
    
    entities.add(new Entity(fernModel, new Vector3f(400, 400, -390), 0, 0, 0, 2.0f));
    
    //create terrain
    int backgroundTextureID = Loader.loadTexture("grass");
    int rTextureID = Loader.loadTexture("mud");
    int gTextureID = Loader.loadTexture("grassFlowers");
    int bTextureID = Loader.loadTexture("path");
    int blendMapID = Loader.loadTexture("blendMap");
    TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTextureID, rTextureID, gTextureID, bTextureID);
    
    terrains.add(new Terrain(0, -1, texturePack, blendMapID));
    
    //create gui
    Gui gui = new Gui(Loader.loadTexture("cross"), new Vector2f(0f, 0f), new Vector2f(0.02f, 0.02f));
    guis.add(gui);
    
    RawModel raw = OBJFileLoader.loadOBJ("stall");
    Model model = new Model(Loader.loadTexture("stallTexture"), raw);
    model.setShineDamper(5);
    model.setReflectivity(1f);
    model.setTransparency(false);
    model.setUseFakeLighting(false);
    
    //create a whole frack ton of stalls
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        for (int k = 0; k < 5; k++) {
          //entities.add(new Entity(model, new Vector3f(i * 50, j * 50, k * -50), 0, 0, 0, 1.0f));
        }
      }
    }
    
    System.out.println("Done!");
    
  }
  
}
