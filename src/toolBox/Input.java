package toolBox;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
  private final long windowHandleReference;
  
  public Input(long window) {
    this.windowHandleReference = window;
  }

  public boolean isKeyDown(int key) {
    int state = glfwGetKey(this.windowHandleReference, key);
    return state == GLFW_PRESS;
  }
  
  public boolean isMouseButtonDown(int button) {
    return (glfwGetMouseButton(this.windowHandleReference, button) == GLFW_PRESS);
  }
}
