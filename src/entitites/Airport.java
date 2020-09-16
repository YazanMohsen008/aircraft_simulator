package entitites;

import Models.TexturedModel;
import Terrain.Terrain;
import renderEngine.Loader;
import textures.TerrainTexturePack;

// TODO: finish this class by providing textures and blendMap for an appropriate airport
public class Airport {
    Loader loader;
    Terrain terrain;

    Airport(Loader loader) {
        this.loader = loader;
        //this.terrain = new Terrain(0, 0, loader, texturePack, blendMap)
    }




    public Terrain getModel() {
        return terrain;
    }
}
