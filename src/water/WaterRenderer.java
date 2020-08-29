package water;

import entities.Camera;
import entities.Light;
import models.RawModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import postProcessing.Fbo;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import toolBox.Maths;

import java.util.List;

public class WaterRenderer {
  
  private static final float[] vertices = {-1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1};
  private static final RawModel quad = Loader.loadToVAO(vertices, 2);
  
  private static final WaterShader shader = new WaterShader();
  private static final int distortionMap = Loader.loadTexture("waterDistortionMap");
  private static final float MOVE_SPEED = 0.03f;
  private static float currentMoveFactor = 0;
  
  private static final int[] size = DisplayManager.getFrameBufferSize();
  private static final Fbo reflection = new Fbo(size[0] / 4, size[1] / 4, Fbo.NONE);
  private static final Fbo refraction = new Fbo(size[0] / 4, size[1] / 4, Fbo.NONE);
  
  public static void render(List<WaterTile> water, Camera camera, List<Light> lights) {
    shader.start();
    
    GL30.glBindVertexArray(quad.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, reflection.getColorTexture());
    
    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, refraction.getColorTexture());
    
    GL13.glActiveTexture(GL13.GL_TEXTURE2);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, distortionMap);
    
    shader.connectTextureUnits();
    
    shader.loadProjectionMatrix(camera.getProjectionMatrix());
    shader.loadViewMatrix(camera);
    shader.loadCameraPosition(camera.getPosition());
    
    shader.loadShineVariables(15f, 0.6f);
    shader.loadLights(lights);
    
    currentMoveFactor += MOVE_SPEED * DisplayManager.getDeltaTime();
    currentMoveFactor %= 1;
    shader.loadMoveFactor(currentMoveFactor);
    
    for (WaterTile tile : water) {
      Matrix4f modelMatrix = Maths.createTransformationMatrix(new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0, WaterTile.TILE_SIZE);
      shader.loadModelMatrix(modelMatrix);
      GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
    }
    
    GL20.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);
    
    shader.stop();
  }
  
  public static void bindReflectionFBO() {
    reflection.bindFrameBuffer();
  }
  
  public static void unbindReflectionFBO() {
    reflection.unbindFrameBuffer();
  }
  
  public static void bindRefractionFBO() {
    refraction.bindFrameBuffer();
  }
  
  public static void unbindRefractionFBO() {
    refraction.unbindFrameBuffer();
  }
  
  public static void cleanUp() {
    refraction.cleanUp();
    reflection.cleanUp();
    shader.cleanUp();
  }
  
}
