package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import renderEngine.DisplayManager;
import water.WaterRenderer;

public class UnderwaterEffect {
  
  private final UnderwaterShader shader;
  private final Fbo fbo;
  
  public UnderwaterEffect() {
    shader = new UnderwaterShader();
    int[] size = DisplayManager.getFrameBufferSize();
    fbo = new Fbo(size[0], size[1], size[0], size[1], 0, 0, Fbo.NONE);
  }
  
  public Fbo render(int texturedID, int cameraHeight) {
    shader.start();
    shader.loadCameraHeight(cameraHeight);
    shader.loadMoveFactor(WaterRenderer.currentMoveFactor);
    
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedID);
    ImageRenderer.renderToFBO(fbo);
    
    shader.stop();
    
    return fbo;
  }
  
  public void loadWaterHeight(int height) {
    shader.start();
    shader.loadWaterHeight(height);
    shader.stop();
  }
  
  public void loadDistortionMap() {
    GL13.glActiveTexture(GL13.GL_TEXTURE1);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, WaterRenderer.distortionMap);
  }
  
  public void cleanUp() {
    shader.cleanUp();
    fbo.cleanUp();
  }
}
