package terrains;

public class TerrainTexturePack {
  private final int backgroundTextureID;
  private final int rTextureID;
  private final int gTextureID;
  private final int bTextureID;
  
  public TerrainTexturePack(int backgroundTextureID, int rTextureID, int gTextureID, int bTextureID) {
    this.backgroundTextureID = backgroundTextureID;
    this.rTextureID = rTextureID;
    this.gTextureID = gTextureID;
    this.bTextureID = bTextureID;
  }
  
  public int getBackgroundTextureID() {
    return backgroundTextureID;
  }
  
  public int getrTextureID() {
    return rTextureID;
  }
  
  public int getgTextureID() {
    return gTextureID;
  }
  
  public int getbTextureID() {
    return bTextureID;
  }
}
