package imgui.example.basic.gdx.gl.android;

import android.os.Bundle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import imgui.example.basic.BasicExample;
import imgui.example.renderer.ExampleTexture;
import imgui.example.renderer.ImGuiRenderer;
import imgui.example.renderer.gdx.GdxGraphicsAdapter;
import imgui.example.renderer.gdx.GdxImGuiExampleApplication;
import imgui.gdx.ImGuiGdxGLImpl;
import java.nio.ByteBuffer;

public final class GdxAndroidGlActivity extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useGyroscope = false;
        config.useImmersiveMode = true;
        config.useWakelock = true;
        config.useGL30 = true;
        initialize(new GdxImGuiExampleApplication(ImGuiGdxGLImpl::new, new GlGraphicsAdapter()) {
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
