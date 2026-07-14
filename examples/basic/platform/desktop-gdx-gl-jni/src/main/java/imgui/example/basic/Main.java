package imgui.example.basic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import imgui.example.renderer.ExampleTexture;
import imgui.example.renderer.ImGuiRenderer;
import imgui.example.renderer.gdx.GdxGraphicsAdapter;
import imgui.example.renderer.gdx.GdxImGuiExampleApplication;
import imgui.gdx.ImGuiGdxLwjgl3GLImpl;

import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1444, 800);
        config.setTitle("ImGui GDX GL JNI Basic Example");
        config.useVsync(false);
        new Lwjgl3Application(new GdxImGuiExampleApplication(ImGuiGdxLwjgl3GLImpl::new, new GlGraphicsAdapter()) {
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
