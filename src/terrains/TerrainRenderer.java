package terrains;

import entities.Camera;
import entities.Light;
import models.RawModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.MasterRenderer;
import toolBox.Maths;

import java.util.ArrayList;
import java.util.List;

public class TerrainRenderer {
  private static final TerrainShader shader = new TerrainShader();
  
  private static final List<Terrain> terrains = new ArrayList<>();
  
  public static void loadInUnits() {
    shader.start();
    shader.loadTextureUnits();
    shader.stop();
  }
  
  public static void render(Camera camera, List<Light> lights) {
    render(camera,lights, new Vector4f(0,0,0,0));
  }
  
  public static void render(Camera camera, List<Light> lights, Vector4f clippingPlane) {
    shader.start();
    shader.loadClippingPlane(clippingPlane);
    shader.loadProjectionMatrix(camera.getProjectionMatrix());
    shader.loadLights(lights);
    shader.loadViewMatrix(camera);
    
    for (Terrain terrain : terrains) {
      prepareModel(terrain);
      loadModelMatrix(terrain);
      GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0); //DRAW VAO
      unbindModel();
    }
    
    shader.stop();
    
    terrains.clear();
  }
  
  private static void prepareModel(Terrain terrain) {
    RawModel rawModel = terrain.getModel();
    GL30.glBindVertexArray(rawModel.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);
    GL20.glEnableVertexAttribArray(2);
    
    bindTextures(terrain);
    shader.loadShineVariables(1, 0);
  }
  
  private static void bindTextures(Terrain terrain) {
    TerrainTexturePack texturePack = terrain.getTexturePack();
    
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTextureID());
    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTextureID());
    GL13.glActiveTexture(GL13.GL_TEXTURE2);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTextureID());
    GL13.glActiveTexture(GL13.GL_TEXTURE3);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTextureID());
    GL13.glActiveTexture(GL13.GL_TEXTURE4);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMapID());
    
  }
  
  private static void unbindModel() {
    MasterRenderer.enableCulling();
    GL20.glDisableVertexAttribArray(0);
    GL20.glDisableVertexAttribArray(1);
    GL20.glDisableVertexAttribArray(2);
    
    GL30.glBindVertexArray(0);
  }
  
  private static void loadModelMatrix(Terrain terrain) {
    Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
    shader.loadTransformationMatrix(transformationMatrix);
  }
  
  public static void addTerrain(Terrain terrain) {
    terrains.add(terrain);
  }
  
  public static void cleanUp() {
    shader.cleanUp();
  }
  
}
