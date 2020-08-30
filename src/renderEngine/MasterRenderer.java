package renderEngine;

import org.lwjgl.opengl.GL11;

public class MasterRenderer {
  
  public static void enableCulling() {
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glCullFace(GL11.GL_BACK);
  }
  
  public static void disableCulling() {
    GL11.glDisable(GL11.GL_CULL_FACE);
  }
  
  public static void prepare() {
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    GL11.glClearColor(0, 0, 1, 1);
  }
  
}
