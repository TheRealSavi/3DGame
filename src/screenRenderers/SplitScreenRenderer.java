package screenRenderers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import postProcessing.ImageRenderer;
import postProcessing.PostProcessor;

public class SplitScreenRenderer {
  
  private static final SplitScreenShader shader = new SplitScreenShader();
  
  public static void render() {
    shader.start();
    
    for (int i = 0; i < PostProcessor.getFbos().size(); i++) {
      GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, PostProcessor.getFbos().get(i).getColorTexture());
    }
    
    shader.connectTextureUnits();
    
    ImageRenderer.renderToScreen();
    
    shader.stop();
    
    PostProcessor.clear();
    
  }
  
}
