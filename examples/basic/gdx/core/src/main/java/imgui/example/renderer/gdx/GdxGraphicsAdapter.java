package imgui.example.renderer.gdx;

import imgui.example.renderer.ExampleTexture;

import java.nio.ByteBuffer;

public interface GdxGraphicsAdapter {
    void clear(float red, float green, float blue, float alpha);

    ExampleTexture createTexture(String label, int width, int height, ByteBuffer pixels);
}
