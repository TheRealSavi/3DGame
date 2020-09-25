package toolBox;

import org.lwjgl.glfw.GLFW;

public class Lerp {
  
  private double startTime;
  private double endTime;
  private int range;
  private int startValue;
  
  public Lerp(int start, int end, double time) {
    startTime = GLFW.glfwGetTime();
    endTime = startTime + time;
    
    startValue = start;
    range = end - start;
  }
  
  public int update() {
    float percentComplete = (float)GLFW.glfwGetTime() / (float)endTime;
    percentComplete = Float.min(percentComplete, 1.0f);
    
    float changeFromStart = range * percentComplete;
    return startValue + (int)changeFromStart;
  }
}
