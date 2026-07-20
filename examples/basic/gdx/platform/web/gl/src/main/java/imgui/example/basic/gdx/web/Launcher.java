package imgui.example.basic.gdx.web;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import imgui.example.basic.BasicExample;
import imgui.example.renderer.ExampleTexture;
import imgui.example.renderer.ImGuiRenderer;
import imgui.example.renderer.gdx.GdxGraphicsAdapter;
import imgui.example.renderer.gdx.GdxImGuiExampleApplication;
import imgui.gdx.ImGuiGdxGLImpl;
import java.nio.ByteBuffer;

public final class Launcher {
    private Launcher() {
    }

    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.useGL30 = true;
        new WebApplication(new GdxImGuiExampleApplication(ImGuiGdxGLImpl::new, new GlGraphicsAdapter()) {
            @Override
            protected ImGuiRenderer createScreen() {
                return new BasicExample();
            }
        }, config);
    }

    private static final class GlGraphicsAdapter implements GdxGraphicsAdapter {
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
}
