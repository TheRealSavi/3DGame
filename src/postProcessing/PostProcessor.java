package postProcessing;

import entities.Camera;

import java.util.ArrayList;
import java.util.List;

public class PostProcessor {
  private static final List<Fbo> finalScreenFBOs = new ArrayList<>();
  
  private static final List<ContrastEffect> contrastEffects = new ArrayList<>();
  private static final List<UnderwaterEffect> underwaterEffects = new ArrayList<>();
  
  public static void doPostProcessing(int colorTexture, Camera camera) {
    
    ContrastEffect contrastEffect = new ContrastEffect();
    contrastEffects.add(contrastEffect);
    Fbo contrastFbo = contrastEffect.render(colorTexture);
    
    UnderwaterEffect underwaterEffect = new UnderwaterEffect();
    underwaterEffects.add(underwaterEffect);
    underwaterEffect.loadDistortionMap();
    underwaterEffect.loadWaterHeight(-7);
    Fbo underwaterFbo = underwaterEffect.render(contrastFbo.getColorTexture(), (int)camera.getPosition().y);
    
    finalScreenFBOs.add(underwaterFbo);
  }
  
  public static Fbo getFinalScreenFBO(int index) {
    return finalScreenFBOs.get(index);
  }
  
  public static int getScreenCount() {
    return finalScreenFBOs.size();
  }
  
  public static void clear() {
    cleanUp();
    
    contrastEffects.clear();
    underwaterEffects.clear();
    finalScreenFBOs.clear();
  }
  
  public static void cleanUp() {
    for (UnderwaterEffect fx : underwaterEffects) {
      fx.cleanUp();
    }
    for (ContrastEffect fx : contrastEffects) {
      fx.cleanUp();
    }
  }
  
}
