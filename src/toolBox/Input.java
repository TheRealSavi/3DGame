package toolBox;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
  private final long windowHandleReference;
  
  public Input(long window) {
    this.windowHandleReference = window;
  }
  
  public boolean isKeyDown(int key) {
    return glfwGetKey(this.windowHandleReference, key) == GLFW_TRUE;
  }
  
  public boolean isMouseButtonDown(int button) {
    return (glfwGetMouseButton(this.windowHandleReference, button) == GLFW_TRUE);
  }
}
