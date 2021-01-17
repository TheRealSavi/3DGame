package terrains;

import renderEngine.DisplayManager;
import toolBox.FastNoise;

import java.util.Random;

public class HeightsGenerator {
  private static float amplitude = 280f;
  private static float jitter = 0.5f;
  
  private static final Random random = new Random();
  private static int seed = random.nextInt(1000000000);

  private static final FastNoise myNoise = new FastNoise(seed);

  private static double timeOfLastSeed = 0;

  public static int newSeed() {
    if (timeOfLastSeed + 0.3f > DisplayManager.getCurrentTime()) {
      return seed;
    }
    timeOfLastSeed = DisplayManager.getCurrentTime();
    System.out.println("Time called: " + timeOfLastSeed);
    seed = random.nextInt(1000000000);
    myNoise.SetSeed(seed);
    return seed;
  }

  public static int newSeed(int _seed) {
    seed = _seed;
    myNoise.SetSeed(seed);
    return seed;
  }

  public static int getSeed() {
    return seed;
  }

  public static void setAmplitude(float _amp) {
    amplitude = _amp;
  }

  public static float getAmplitude() {
    return amplitude;
  }

  public static float getJitter() {
    return jitter;
  }

  public static void setJitter(float _jitter) {
    jitter = _jitter;
  }

  public static void setNoiseType(FastNoise.NoiseType type) {
    myNoise.SetNoiseType(type);
  }

  public static float generateHeight(float x, float z) {
    float total = 0f;

    total += myNoise.GetNoise(x, z) * amplitude;
    total += myNoise.GetNoise(x * 2f, z * 2f) * amplitude * jitter;
    total += myNoise.GetNoise(x * 4f, z * 4f) * amplitude * jitter / 2;

    return total;
  }

}
