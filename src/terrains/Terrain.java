package terrains;

import models.RawModel;
import org.joml.Vector3f;
import renderEngine.Loader;

public class Terrain {
  private static final float SIZE = 800;
  private static final int VERTEX_COUNT = 128;
  
  private final float x;
  private final float z;
  private RawModel model;
  private final TerrainTexturePack texturePack;
  private final int blendMapID;
  
  public Terrain(int gridX, int gridZ, TerrainTexturePack texturePack, int blendMapID) {
    this.texturePack = texturePack;
    this.blendMapID = blendMapID;
    
    this.x = gridX * SIZE;
    this.z = gridZ * SIZE;
    this.model = generateTerrain();
  }
  
  public void regenModel() {
    this.model = generateTerrain();
  }
  
  private RawModel generateTerrain() {
    
    HeightsGenerator heightsGenerator = new HeightsGenerator();
    
    int count = VERTEX_COUNT * VERTEX_COUNT;
    float[] vertices = new float[count * 3];
    float[] normals = new float[count * 3];
    float[] textureCoords = new float[count * 2];
    int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
    int vertexPointer = 0;
    for (int i = 0; i < VERTEX_COUNT; i++) {
      for (int j = 0; j < VERTEX_COUNT; j++) {
        vertices[vertexPointer * 3] = (float)j / ((float)VERTEX_COUNT - 1) * SIZE; //x
        vertices[vertexPointer * 3 + 1] = heightsGenerator.generateHeight(j, i); //y
        vertices[vertexPointer * 3 + 2] = (float)i / ((float)VERTEX_COUNT - 1) * SIZE; //z
        Vector3f normal = calculateNormal(j, i, heightsGenerator);
        normals[vertexPointer * 3] = normal.x;
        normals[vertexPointer * 3 + 1] = normal.y;
        normals[vertexPointer * 3 + 2] = normal.z;
        textureCoords[vertexPointer * 2] = (float)j / ((float)VERTEX_COUNT - 1);
        textureCoords[vertexPointer * 2 + 1] = (float)i / ((float)VERTEX_COUNT - 1);
        vertexPointer++;
      }
    }
    int pointer = 0;
    for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
      for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
        int topLeft = (gz * VERTEX_COUNT) + gx;
        int topRight = topLeft + 1;
        int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
        int bottomRight = bottomLeft + 1;
        indices[pointer++] = topLeft;
        indices[pointer++] = bottomLeft;
        indices[pointer++] = topRight;
        indices[pointer++] = topRight;
        indices[pointer++] = bottomLeft;
        indices[pointer++] = bottomRight;
      }
    }
    return Loader.loadToVAO(vertices, textureCoords, normals, indices);
  }
  
  private Vector3f calculateNormal(int x, int z, HeightsGenerator heightsGenerator) {
    float heightL = heightsGenerator.generateHeight(x - 1, z);
    float heightR = heightsGenerator.generateHeight(x + 1, z);
    float heightD = heightsGenerator.generateHeight(x, z - 1);
    float heightU = heightsGenerator.generateHeight(x, z + 1);
    Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
    normal.normalize();
    return normal;
  }
  
  public float getX() {
    return x;
  }
  
  public float getZ() {
    return z;
  }
  
  public RawModel getModel() {
    return model;
  }
  
  public TerrainTexturePack getTexturePack() {
    return texturePack;
  }
  
  public int getBlendMapID() {
    return blendMapID;
  }
}
