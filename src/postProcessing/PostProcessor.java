package postProcessing;

import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;

public class PostProcessor {
  
  private static final float[] POSITIONS = {-1, 1, -1, -1, 1, 1, 1, -1};
  private static final RawModel QUAD = Loader.loadToVAO(POSITIONS, 2);
  
  private static final ContrastEffect contrastEffect = new ContrastEffect();
  
  public static void doPostProcessing(int colourTexture) {
    start();
    contrastEffect.render(colourTexture);
    end();
  }
  
  public static void cleanUp() {
    contrastEffect.cleanUp();
  }
  
  private static void start() {
    GL30.glBindVertexArray(QUAD.getVaoID());
    GL20.glEnableVertexAttribArray(0);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
  }
  
  private static void end() {
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL20.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);
  }
  
  
}
