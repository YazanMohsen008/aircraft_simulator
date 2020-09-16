package renderEngine;

import Models.TexturedModel;
import Terrain.Terrain;
import entitites.Camera;
import entitites.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import toolbox.Maths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {
    private StaticShader staticShader = new StaticShader();
    private EntityRenderer entityRenderer;


    private TerrainShader terrainShader = new TerrainShader();
    private TerrainRenderer terrainRenderer;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<Terrain>();

    private SkyboxRenderer skyboxRenderer;

    public MasterRenderer(Loader loader) {

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);


        Matrix4f projectionMatrix  = Maths.createProjectionMatrix();
        entityRenderer = new EntityRenderer(staticShader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);

    }


    public void render(Camera camera) {
        prepare();
        staticShader.start();
        staticShader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        staticShader.stop();

        terrainShader.start();
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        skyboxRenderer.render(camera);

        terrains.clear();
        entities.clear();
    }

    public void cleanUP () {
        staticShader.cleanUp();
        terrainShader.cleanUp();
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);

        if (batch != null) {
            batch.add(entity);
        }
        else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(0.3f, 0.3f, 0.3f, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

}
