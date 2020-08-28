package terrains;

import models.RawModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import toolBox.Maths;

import java.util.List;

public class TerrainRenderer {
  private TerrainShader shader;
  
  public TerrainRenderer(TerrainShader shader) {
    this.shader = shader;
    shader.start();
    shader.loadTextureUnits();
    shader.stop();
  }
  
  public void render(List<Terrain> terrains) {
    for (Terrain terrain : terrains) {
      prepareTerrain(terrain);
      loadModelMatrix(terrain);
      GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0); //DRAW VAO
      unbindTerrain();
    }
  }
  
  private void prepareTerrain(Terrain terrain) {
    RawModel rawModel = terrain.getModel();
    GL30.glBindVertexArray(rawModel.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);
    GL20.glEnableVertexAttribArray(2);
    
    bindTextures(terrain);
    shader.loadShineVariables(1, 0);
  }
  
  private void bindTextures(Terrain terrain) {
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
  
  private void unbindTerrain() {
    GL20.glDisableVertexAttribArray(0);
    GL20.glDisableVertexAttribArray(1);
    GL20.glDisableVertexAttribArray(2);
    
    GL30.glBindVertexArray(0);
  }
  
  private void loadModelMatrix(Terrain terrain) {
    Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
    shader.loadTransformationMatrix(transformationMatrix);
  }
  
}
