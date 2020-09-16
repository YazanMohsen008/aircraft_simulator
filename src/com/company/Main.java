package com.company;

import Models.TexturedModel;
import Physics.MathVector;
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

public class Main {
    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
    

        RawModel airplaneObjectModel = OBJLoader.loadObjModel("A", loader);

        ModelTexture airPlaneTexture = new ModelTexture(loader.loadTexture("A_tex"));

        TexturedModel airplaneTexturedModel = new TexturedModel(airplaneObjectModel, airPlaneTexture);

        // Airplane class suppose to load an airplane, but for now it loads a bunny
        Airplane airplane = new Airplane(airplaneTexturedModel, new MathVector(100, 0, 50), 0,
                (float) Math.toRadians(180), 0,
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

        //loading terrain
        Terrain terrain0 = new Terrain(0, 0, loader, texturePack, blendMap,"grassy");
        Terrain terrain1 = new Terrain(1, 0, loader, texturePack, blendMap,"dirt");
        Terrain terrain2 = new Terrain(2, 0, loader, texturePack, blendMap,"grassFlowers");
        Terrain[] terrains = new Terrain[15];

        for (int i = 0; i < 10; i ++) {
            terrains[i] = new Terrain(i, 0, loader, texturePack, blendMap,"path");
        }

        MasterRenderer renderer = new MasterRenderer(loader);
        while (!Display.isCloseRequested()) {
            camera.move();
            airplane.StepSimulation();
            renderer.processEntity(airplane);
            for (int i = 0; i < 4; i ++) {
                renderer.processTerrain(terrains[i]);
            }

            renderer.render(camera);

            DisplayManager.updateDisplay();
        }

        renderer.cleanUP();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
