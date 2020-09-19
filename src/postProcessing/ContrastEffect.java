package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import renderEngine.DisplayManager;

public class ContrastEffect {
  
  private final ContrastShader shader;
  private final Fbo fbo;
  
  public ContrastEffect() {
    shader = new ContrastShader();
    int[] size = DisplayManager.getFrameBufferSize();
    fbo = new Fbo(size[0], size[1], size[0], size[1], 0, 0, Fbo.NONE);
  }
  
  public Fbo render(int texturedID) {
    shader.start();
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedID);
    ImageRenderer.renderToFBO(fbo);
    shader.stop();
    return fbo;
  }
  
  public void cleanUp() {
    shader.cleanUp();
    fbo.cleanUp();
  }
}
