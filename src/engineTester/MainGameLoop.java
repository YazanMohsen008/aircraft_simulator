package engineTester;

import Models.TexturedModel;
import Terrain.Terrain;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import entitites.Airplane;
import entitites.Camera;
import gui.GuiRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import Models.RawModel;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;



public class MainGameLoop {
    public static void main(String[] args) throws Exception {

        final int MAX_TERRAIN_IN_ONE_QUARTER = 2;
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        MasterRenderer renderer = new MasterRenderer(loader);
        // gui rendering panel

        RawModel bunny = OBJLoader.loadObjModel("bunny", loader);
        ModelTexture bunnyTexture = new ModelTexture(loader.loadTexture("dirt"));
        TexturedModel bunnyTexturedModel = new TexturedModel(bunny, bunnyTexture);
        // Airplane class suppose to load an airplane, but for now it loads a bunny
        Airplane movingBunny = new Airplane(bunnyTexturedModel, new Vector3f(100, 0, -50),
                0, 0, 0, 0.2f);
        Camera camera = new Camera(movingBunny);


        // loading groundTexture
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        //loading terrain
        Terrain[][] terrains = new Terrain[MAX_TERRAIN_IN_ONE_QUARTER][MAX_TERRAIN_IN_ONE_QUARTER];

        for (int i = 0; i < MAX_TERRAIN_IN_ONE_QUARTER; i++) {
            for (int j = 0; j < MAX_TERRAIN_IN_ONE_QUARTER; j++)
                terrains[i][j] = new Terrain(i - (MAX_TERRAIN_IN_ONE_QUARTER / 2),
                        j - (MAX_TERRAIN_IN_ONE_QUARTER / 2),
                        loader, texturePack, blendMap);
        }

        GuiRenderer guiRenderer = new GuiRenderer();
        Nifty nifty = guiRenderer.initNifty();
        while (!Display.isCloseRequested()) {
            DisplayManager.updateDisplay();
            camera.move();
            // TODO: need to check which terrain the airplane is moving on for proper collision detection
            movingBunny.move(null);
            renderer.processEntity(movingBunny);
            for (int i = 0; i < MAX_TERRAIN_IN_ONE_QUARTER; i++) {
                for (int j = 0; j < MAX_TERRAIN_IN_ONE_QUARTER; j++)
                    renderer.processTerrain(terrains[i][j]);
            }

            renderer.render(camera);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            nifty.render(false);
            nifty.update();
/*

            nifty.getCurrentScreen().findNiftyControl("tl", Label.class).setText(movingBunny.
                    getPosition().toString());
*/

            GL11.glEnable(GL11.GL_DEPTH_TEST);


        }
        guiRenderer.shutDown();
        renderer.cleanUP();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}

