package entities;

import models.Model;
import models.RawModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.MasterRenderer;
import toolBox.Maths;

import java.util.List;
import java.util.Map;

public class EntityRenderer {
  
  private final EntityShader shader;
  
  public EntityRenderer(EntityShader shader) {
    this.shader = shader;
  }
  
  public void render(Map<Model, List<Entity>> entities) {
    for (Model model : entities.keySet()) {
      prepareModel(model);
      List<Entity> batch = entities.get(model);
      for (Entity entity : batch) {
        prepareInstance(entity);
        
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0); //DRAW VAO
      }
      unbindTexturedModel();
    }
  }
  
  private void prepareModel(Model model) {
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
  
  private void unbindTexturedModel() {
    MasterRenderer.enableCulling();
    GL20.glDisableVertexAttribArray(0);
    GL20.glDisableVertexAttribArray(1);
    GL20.glDisableVertexAttribArray(2);
    
    GL30.glBindVertexArray(0);
  }
  
  private void prepareInstance(Entity entity) {
    Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
    shader.loadTransformationMatrix(transformationMatrix);
    
  }
  
}
