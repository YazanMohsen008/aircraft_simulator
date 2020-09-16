package engineTester;

import Models.TexturedModel;
import Physics.MathVector;
import Terrain.Terrain;
import entitites.Airplane;
import entitites.Camera;
import org.lwjgl.opengl.Display;
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


        RawModel airplaneRawModel = OBJLoader.loadObjModel("A", loader);

        ModelTexture bunnyTexture = new ModelTexture(loader.loadTexture("white"));

        TexturedModel airpalaneTexturedModel = new TexturedModel(airplaneRawModel, bunnyTexture);

        // Airplane class suppose to load an airplane, but for now it loads a bunny
        Airplane airplane= new Airplane(airpalaneTexturedModel, new MathVector(500,0 ,50),
                0, 0, 0, 0.25f,null);

        Camera camera = new Camera(airplane);

        // loading groundTexture
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        //loading terrain
        Terrain terrain0 = new Terrain(0, 0, loader, texturePack, blendMap, "heightMap");
        airplane.InitializeAirplane();
        MasterRenderer renderer = new MasterRenderer(loader);
        while (!Display.isCloseRequested()) {
            camera.move();
            // TODO: need to check which terrain the airplane is moving on for proper collision detection
            airplane.StepSimulation();
            renderer.processEntity(airplane);
            renderer.processTerrain(terrain0);

            renderer.render(camera);

            DisplayManager.updateDisplay();
        }

        renderer.cleanUP();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
