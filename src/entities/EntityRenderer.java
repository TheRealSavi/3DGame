package entities;

import models.Model;
import models.RawModel;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.MasterRenderer;
import toolBox.Maths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRenderer {
  
  private static final EntityShader shader = new EntityShader();
  
  private static final Map<Model, List<Entity>> entities = new HashMap<>();
  
  public static void render(Camera camera, List<Light> lights) {
    render(camera, lights, new Vector4f(0, 0, 0, 0));
  }
  
  public static void render(Camera camera, List<Light> lights, Vector4f clippingPlane) {
    shader.start();
    shader.loadClippingPlane(clippingPlane);
    shader.loadProjectionMatrix(camera.getProjectionMatrix());
    shader.loadLights(lights);
    shader.loadViewMatrix(camera);
    
    for (Model model : entities.keySet()) {
      prepareModel(model);
      List<Entity> batch = entities.get(model);
      for (Entity entity : batch) {
        loadModelMatrix(entity);
        
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0); //DRAW VAO
      }
      unbindModel();
    }
    shader.stop();
    
    entities.clear();
  }
  
  private static void prepareModel(Model model) {
    RawModel rawModel = model.getRawModel();
    GL30.glBindVertexArray(rawModel.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);
    GL20.glEnableVertexAttribArray(2);
    
    if (model.isTransparent()) {
      MasterRenderer.disableCulling();
    }
    shader.loadFakeLightingVariable(model.isUsingFakeLighting());
    shader.loadShineVariables(model.getShineDamper(), model.getReflectivity());
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTextureID());
  }
  
  private static void unbindModel() {
    MasterRenderer.enableCulling();
    GL20.glDisableVertexAttribArray(0);
    GL20.glDisableVertexAttribArray(1);
    GL20.glDisableVertexAttribArray(2);
    
    GL30.glBindVertexArray(0);
  }
  
  private static void loadModelMatrix(Entity entity) {
    Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
    shader.loadTransformationMatrix(transformationMatrix);
  }
  
  public static void addEntity(Entity entity) {
    Model entityModel = entity.getModel();
    
    List<Entity> batch = entities.get(entityModel);
    if (batch != null) {
      batch.add(entity);
    } else {
      List<Entity> newBatch = new ArrayList<>();
      newBatch.add(entity);
      entities.put(entityModel, newBatch);
    }
  }
  
  public static void cleanUp() {
    shader.cleanUp();
  }
  
}
