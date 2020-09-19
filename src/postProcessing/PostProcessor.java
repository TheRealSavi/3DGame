package postProcessing;

import java.util.ArrayList;
import java.util.List;

public class PostProcessor {
  
  private static final List<Fbo> fbos = new ArrayList<>();
  private static final List<ContrastEffect> contrastEffects = new ArrayList<>();
  
  public static void doPostProcessing(int colourTexture) {
    ContrastEffect contrastEffect = new ContrastEffect();
    contrastEffects.add(contrastEffect);
    
    Fbo contrastFbo = contrastEffect.render(colourTexture);
    fbos.add(contrastFbo);
  }
  
  public static List<Fbo> getFbos() {
    return fbos;
  }
  
  public static void clear() {
    cleanUp();
    fbos.clear();
    contrastEffects.clear();
  }
  
  public static void cleanUp() {
    for (ContrastEffect fx : contrastEffects) {
      fx.cleanUp();
    }
    for (Fbo fbo : fbos) {
      fbo.cleanUp();
    }
  }
  
}
