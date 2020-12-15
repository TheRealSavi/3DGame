package particles;

import entities.Camera;
import models.RawModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.Loader;
import toolBox.Maths;

import java.util.ArrayList;
import java.util.List;

public class ParticleRenderer {
  
  private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
  
  private static final RawModel quad = Loader.loadToVAO(VERTICES, 2);
  private static final ParticleShader shader = new ParticleShader();
  
  private static final List<Particle> particles = new ArrayList<>();
  
  public static void updateParticles() {
    for (Particle particle : particles) {
      particle.update();
    }
    particles.clear();
  }
  
  public static void addParticle(Particle particle) {
    particles.add(particle);
  }
  
  public static void render(Camera camera) {
    
    quad.getVAO().bind();
    GL20.glEnableVertexAttribArray(0);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glDepthMask(false);
    
    shader.start();
    
    shader.loadProjectionMatrix(camera.getProjectionMatrix());
    shader.loadViewMatrix(camera);
    
    for (Particle particle : particles) {
      if (particle.isAlive()) {
        Matrix4f modelMatrix = Maths.createModelMatrix(particle.getPosition(), 0, 0, 0, particle.getScale());
        shader.loadModelMatrix(modelMatrix);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
      }
    }
    
    shader.stop();
    
    GL11.glDepthMask(true);
    GL11.glDisable(GL11.GL_BLEND);
    GL20.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);
    
    particles.clear();
    
  }
  
  public static void cleanUp() {
    shader.cleanUp();
  }
  
}
