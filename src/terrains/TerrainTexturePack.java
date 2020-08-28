package terrains;

public class TerrainTexturePack {
  private int backgroundTextureID;
  private int rTextureID;
  private int gTextureID;
  private int bTextureID;
  
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
