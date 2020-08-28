package guis;

import models.RawModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;
import toolBox.Maths;

import java.util.List;

public class GuiRenderer {
  
  private static final float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
  private static final RawModel quad = Loader.loadToVAO(positions, 2);
  private static final GuiShader shader = new GuiShader();
  
  public static void render(List<Gui> guis) {
    shader.start(); //starts the gui shader
    
    GL30.glBindVertexArray(quad.getVaoID()); //binds the VAO
    GL20.glEnableVertexAttribArray(0); //enables access to the gui VAO's vertex position data
    GL11.glEnable(GL11.GL_BLEND); //enables alpha blending for this draw routine
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); //sets the alpha blending parameters
    GL11.glDisable(GL11.GL_DEPTH_TEST); //disables the depth buffer testing for this draw routine
    
    for (Gui gui : guis) { //loops through all the guis
      GL13.glActiveTexture(GL13.GL_TEXTURE0); //select texture unit 0
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTextureID()); //bind the gui's texture the selected texture unit
      Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale()); //generates the model transformation matrix
      shader.loadTransformation(matrix); //loads the model transformation matrix to the gui shader
      GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount()); //draw the triangles
    }
    
    GL11.glEnable(GL11.GL_DEPTH_TEST); //re-enable the depth buffer testing now that rendering is done
    GL11.glDisable(GL11.GL_BLEND); //re-disable alpha blending since rendering is done
    GL20.glDisableVertexAttribArray(0); //disables access to the gui VAO's vertex position data
    GL30.glBindVertexArray(0); //unbind the gui VAO
    
    shader.stop(); //stops the gui shader
  }
  
  public static void cleanUp() {
    shader.cleanUp();
  }
}
