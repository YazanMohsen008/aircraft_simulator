package com.company;

import Models.TexturedModel;
import Physics.MathVector;
import Terrain.Terrain;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import entitites.Airplane;
import entitites.Camera;
import gui.GuiRenderer;
import gui.ParametersScreenController;
import gui.TextDecorator;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import renderEngine.*;
import Models.RawModel;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class Main {
    public static void main(String[] args) {

        final int MAX_TERRAIN_IN_ONE_QUARTER = 4;

        DisplayManager.createDisplay();
        Loader loader = new Loader();
    

        RawModel airplaneObjectModel = OBJLoader.loadObjModel("A", loader);

        ModelTexture airPlaneTexture = new ModelTexture(loader.loadTexture("A_tex"));

        TexturedModel airplaneTexturedModel = new TexturedModel(airplaneObjectModel, airPlaneTexture);

        // Airplane class suppose to load an airplane, but for now it loads a bunny

        Airplane airplane = new Airplane(airplaneTexturedModel, new MathVector(100, 3, 50), 0,
                0, 0,
                0.5f,null);
        airplane.InitializeAirplane();
        Camera camera = new Camera(airplane);
    
        // loading groundTexture
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        Terrain[] terrains1 = new Terrain[MAX_TERRAIN_IN_ONE_QUARTER];
        Terrain[] terrains2 = new Terrain[MAX_TERRAIN_IN_ONE_QUARTER];

            for (int j = 0; j < MAX_TERRAIN_IN_ONE_QUARTER; j++)
                terrains1[j] = new Terrain(j, 0, loader, texturePack, blendMap);

            for (int j = 0; j < MAX_TERRAIN_IN_ONE_QUARTER; j++)
                terrains2[j] = new Terrain(j, -1, loader, texturePack, blendMap);


        MasterRenderer renderer = new MasterRenderer(loader);
        GuiRenderer guiRenderer = new GuiRenderer();
        Nifty nifty = guiRenderer.initNifty();
        airplane.setParametersController((ParametersScreenController) nifty.getCurrentScreen().getScreenController());

        long counter = 0;
        while (!Display.isCloseRequested()) {
            DisplayManager.updateDisplay();
            camera.move();
            airplane.StepSimulation();
            renderer.processEntity(airplane);

                for (int j = 0; j < MAX_TERRAIN_IN_ONE_QUARTER; j++)
                    renderer.processTerrain(terrains1[j]);

            for (int j = 0; j < MAX_TERRAIN_IN_ONE_QUARTER; j++)
                renderer.processTerrain(terrains2[j]);

            renderer.render(camera);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            nifty.render(false);
            nifty.update();
            nifty.getCurrentScreen().getFocusHandler().lostKeyboardFocus(nifty.getCurrentScreen().
                    findElementById("gravity_scroll"));

            nifty.getCurrentScreen().getFocusHandler().lostKeyboardFocus(nifty.getCurrentScreen().
                    findElementById("air_density_scroll"));



            if (counter % 15 == 0)
                updateNitfyValueScreen(nifty, airplane);

            GL11.glEnable(GL11.GL_DEPTH_TEST);

            counter ++;
            if (counter == 1000000000) counter = 0;
        }

        guiRenderer.shutDown();
        renderer.cleanUP();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

    private static void updateNitfyValueScreen(Nifty nifty, Airplane airplane) {
        TextDecorator decorator = new TextDecorator(airplane);

        nifty.getCurrentScreen().findNiftyControl("thrust_force", Label.class).setText(decorator
                .getThrust());
        nifty.getCurrentScreen().findNiftyControl("total_force", Label.class).setText(decorator
                .getForces());
        nifty.getCurrentScreen().findNiftyControl("positions", Label.class).setText(decorator
                .getPosition());
        nifty.getCurrentScreen().findNiftyControl("rotations", Label.class).setText(decorator
                .getRotations());
        nifty.getCurrentScreen().findNiftyControl("velocity", Label.class).setText(decorator
                .getVelocity());
        nifty.getCurrentScreen().findNiftyControl("angular_velocity", Label.class).setText(decorator
                .getAngularVelocity());
        nifty.getCurrentScreen().findNiftyControl("aoa_1", Label.class).setText(decorator
                .getAOA1());
        nifty.getCurrentScreen().findNiftyControl("aoa_2", Label.class).setText(decorator
                .getAOA2());
        nifty.getCurrentScreen().findNiftyControl("air_density", Label.class).setText(decorator
                .getAirDensity());
        nifty.getCurrentScreen().findNiftyControl("gravity", Label.class).setText(decorator
                .getGravity());
        nifty.getCurrentScreen().findNiftyControl("air_speed", Label.class).setText(decorator
                .getAirVelocity());
    }

}
