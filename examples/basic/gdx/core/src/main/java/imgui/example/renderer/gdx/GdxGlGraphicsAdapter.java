package imgui.example.renderer.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import imgui.example.renderer.ExampleTexture;
import java.nio.ByteBuffer;

public final class GdxGlGraphicsAdapter implements GdxGraphicsAdapter {
    @Override
    public void clear(float red, float green, float blue, float alpha) {
        Gdx.gl.glClearColor(red, green, blue, alpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public ExampleTexture createTexture(String label, int width, int height, ByteBuffer pixels) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        ByteBuffer target = pixmap.getPixels();
        target.clear();
        target.put(pixels.duplicate());
        target.flip();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new ExampleTexture(texture.getTextureObjectHandle(), width, height, texture::dispose);
    }
}
