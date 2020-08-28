package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class ContrastEffect {
  
  private ImageRenderer renderer;
  private ContrastShader shader;
  
  public ContrastEffect() {
    shader = new ContrastShader();
    renderer = new ImageRenderer();
  }
  
  public void render(int texturedID) {
    shader.start();
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedID);
    renderer.renderQuad();
    shader.stop();
  }
  
  public void cleanUp() {
    renderer.cleanUp();
    shader.cleanUp();
  }
}
