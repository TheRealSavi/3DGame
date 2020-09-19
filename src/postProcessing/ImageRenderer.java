package postProcessing;

import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;
import renderEngine.MasterRenderer;

public class ImageRenderer {
  
  private static final float[] POSITIONS = {-1, 1, -1, -1, 1, 1, 1, -1};
  private static final RawModel QUAD = Loader.loadToVAO(POSITIONS, 2);
  
  public static void renderToFBO(Fbo fbo) {
    fbo.bindFrameBuffer();
    MasterRenderer.prepare();
    
    GL30.glBindVertexArray(QUAD.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    
    GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
    
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL20.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);
    fbo.unbindFrameBuffer();
  }
  
  public static void renderToScreen() {
    //needs a texture and shader bound first
    MasterRenderer.prepare();
    
    GL30.glBindVertexArray(QUAD.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    
    GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
    
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL20.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);
  }
  
}
