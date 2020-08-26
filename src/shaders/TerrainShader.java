package shaders;

import entitites.Camera;
import org.lwjgl.util.vector.Matrix4f;
import toolbox.Maths;

public class TerrainShader extends ShaderProgram{

    private static final String VERTEX_FILE = "src/shaders/terrainVertexShader";
    private static final String FRAGMENT_FILE = "src/shaders/terrainFragmentShader";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;

    private int location_backgroundTexture;
    private int location_bTexture;
    private int location_rTexture;
    private int location_gTexture;
    private int location_blendMap;

    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");

        location_backgroundTexture= super.getUniformLocation("backgroundTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_blendMap = super.getUniformLocation("blendMap");

    }

    public void connectTextureUnits() {
        super.loadInteger(location_backgroundTexture, 0);
        super.loadInteger(location_bTexture, 1);
        super.loadInteger(location_gTexture, 2);
        super.loadInteger(location_bTexture, 3);
        super.loadInteger(location_blendMap, 4);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }
    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }
}
