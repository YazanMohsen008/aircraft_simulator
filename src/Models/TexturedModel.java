package Models;

import textures.ModelTexture;

public class TexturedModel {
    private RawModel rawModel;
    private ModelTexture texture;

    public RawModel getRawModel() {
        return rawModel;
    }

    public ModelTexture getTexture() {
        return texture;
    }

    public TexturedModel(RawModel rawModel, ModelTexture texture) {
        this.rawModel = rawModel;
        this.texture = texture;
    }
}
