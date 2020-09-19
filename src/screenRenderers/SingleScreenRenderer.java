package screenRenderers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import postProcessing.ImageRenderer;
import postProcessing.PostProcessor;

public class SingleScreenRenderer {
  
  private static final SingleScreenShader shader = new SingleScreenShader();
  
  public static void render() {
    shader.start();
    
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, PostProcessor.getFbos().get(0).getColorTexture());
    
    shader.connectTextureUnits();
    
    ImageRenderer.renderToScreen();
    
    shader.stop();
    
    PostProcessor.clear();
  }
}
