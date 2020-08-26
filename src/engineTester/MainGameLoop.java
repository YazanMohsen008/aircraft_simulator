package engineTester;

import Models.TexturedModel;
import Terrain.Terrain;
import entitites.Airplane;
import entitites.Camera;
import entitites.Entity;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import Models.RawModel;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {
    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();


        RawModel bunny = OBJLoader.loadObjModel("bunny", loader);

        ModelTexture bunnyTexture = new ModelTexture(loader.loadTexture("white"));

        TexturedModel bunnyTexturedModel = new TexturedModel(bunny, bunnyTexture);

        // Airplane class suppose to load an airplane, but for now it loads a bunny
        Airplane movingBunny = new Airplane(bunnyTexturedModel, new Vector3f(100, 0, -50), 0, 0, 0, 1);
        Camera camera = new Camera(movingBunny);

        // loading groundTexture
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        //loading terrain
        Terrain terrain0 = new Terrain(0, 0, loader, texturePack, blendMap);
        Terrain terrain1 = new Terrain(-1, 0, loader, texturePack, blendMap);
        Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);
        Terrain terrain3 = new Terrain(0, -1, loader, texturePack, blendMap);

        MasterRenderer renderer = new MasterRenderer();
        while (!Display.isCloseRequested()) {
            camera.move();
            movingBunny.move();
            renderer.processEntity(movingBunny);
            renderer.processTerrain(terrain0);
            renderer.processTerrain(terrain1);
            renderer.processTerrain(terrain2);
            renderer.processTerrain(terrain3);

            renderer.render(camera);

            DisplayManager.updateDisplay();
        }

        renderer.cleanUP();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
