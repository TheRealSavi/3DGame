package renderEngine;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import toolBox.Input;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class DisplayManager {
  
  private static long windowReference;
  
  private static int WIDTH = 600;
  private static int HEIGHT = 600;
  
  private static double lastFrameTime;
  private static double deltaTime;
  
  private static int lastFPSChars = 0;
  
  private static Vector2f lastFrameCursorPos = new Vector2f(0, 0);
  private static Vector2f deltaCursorPos = new Vector2f(0, 0);

  private static boolean cursorLocked = false;
  
  private static Input input;
  
  public static void createDisplay() {
    if (!glfwInit()) { //initialize GLFW
      throw new IllegalStateException("Failed to initialize GLFW!");
    } else {
      System.out.println("GLFW initialized successfully!");
    }
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); //GLFW settings
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
    // -- end glfw initialization -- \\
    
    windowReference = glfwCreateWindow(WIDTH, HEIGHT, "Hello World", 0, 0); //spawn a GLFW Window
    if (windowReference == 0) {
      throw new IllegalStateException("Failed to create Window!");
    }
    
    GLFWVidMode monitor = glfwGetVideoMode(glfwGetPrimaryMonitor());
    if (monitor != null) {
      glfwSetWindowPos(windowReference, (monitor.width() - WIDTH) / 2, (monitor.height() - HEIGHT) / 2);
    } else {
      System.out.println("Could not get Primary Monitor");
    }
    
    glfwShowWindow(windowReference);
    glfwMakeContextCurrent(windowReference);
    GL.createCapabilities();
    
    input = new Input(windowReference);
    lastFrameTime = getCurrentTime();
    
    lockCursor();
  }
  
  public static double getCurrentTime() {
    return GLFW.glfwGetTime();
  }
  
  public static float getCurrentFPS() {
    return (float)(1 / deltaTime);
  }
  
  public static Vector2f getDeltaCursor() {
    return deltaCursorPos;
  }
  
  public static int getWidth() {
    return WIDTH;
  }
  
  public static int getHeight() {
    return HEIGHT;
  }
  
  public static void lockCursor() {
    cursorLocked = true;
    glfwSetInputMode(windowReference, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    if (glfwRawMouseMotionSupported()) {
      glfwSetInputMode(windowReference, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
      DisplayManager.removeFPSLog();
      System.out.println("Raw Input is enabled!");
    } else {
      DisplayManager.removeFPSLog();
      System.out.println("Raw Input not enabled!");
    }
  }
  
  public static void unlockCursor() {
    cursorLocked = false;
    glfwSetInputMode(windowReference, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    if (glfwRawMouseMotionSupported()) {
      glfwSetInputMode(windowReference, GLFW_RAW_MOUSE_MOTION, GLFW_FALSE);
    }
  }

  public static void toggleCursorLock() {
    if (cursorLocked) {
      unlockCursor();
    } else {
      lockCursor();
    }
  }

  public static boolean isCursorLocked() {
    return cursorLocked;
  }
  
  public static Vector2f getCursorPos() {
    Vector2f pos = new Vector2f();
    DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
    DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
    glfwGetCursorPos(windowReference, xBuffer, yBuffer);
    pos.x = (float)xBuffer.get(0);
    pos.y = (float)yBuffer.get(0);
    return pos;
  }
  
  public static int[] getFrameBufferSize() {
    int[] size = new int[2];
    IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
    IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
    glfwGetFramebufferSize(windowReference, widthBuffer, heightBuffer);
    size[0] = widthBuffer.get(0);
    size[1] = heightBuffer.get(0);
    return size;
  }
  
  public static void setSize(int w, int h) {
    WIDTH = w;
    HEIGHT = h;
  }
  
  public static void updateDisplay() {
    double currentFrameTime = getCurrentTime();
    deltaTime = currentFrameTime - lastFrameTime;
    lastFrameTime = currentFrameTime;
    
    Vector2f currentCursorPos = getCursorPos();
    deltaCursorPos = currentCursorPos.sub(lastFrameCursorPos, deltaCursorPos);
    lastFrameCursorPos = currentCursorPos;
    
    glfwSwapBuffers(windowReference);
  }
  
  public static void logFPS() {
    removeFPSLog();
    System.out.print("FPS : " + DisplayManager.getCurrentFPS());
    lastFPSChars = Float.toString(DisplayManager.getCurrentFPS()).length();
  }
  
  public static void removeFPSLog() {
    for (int i = 0; i < lastFPSChars + 6; i++) {
      System.out.print("\b");
    }
  }
  
  public static double getDeltaTime() {
    return deltaTime;
  }
  
  public static void closeDisplay() {
    glfwDestroyWindow(windowReference);
    glfwTerminate();
  }
  
  public static boolean shouldClose() {
    return glfwWindowShouldClose(windowReference);
  }
  
  public static Input getInput() {
    return input;
  }
  
  public static void pollSystemEvents() {
    glfwPollEvents();
  }
  
}
