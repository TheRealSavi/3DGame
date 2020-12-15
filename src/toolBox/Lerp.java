package toolBox;

import org.lwjgl.glfw.GLFW;

public class Lerp {
  
  private final double startTime;
  private final double endTime;
  private final int range;
  private final int startValue;
  
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
