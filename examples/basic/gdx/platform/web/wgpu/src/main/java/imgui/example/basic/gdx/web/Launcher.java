package imgui.example.basic.gdx.web;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Pixmap;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.monstrous.gdx.webgpu.backends.teavm.WgTeaApplication;
import com.monstrous.gdx.webgpu.backends.teavm.WgTeaPreloadApplicationListener;
import com.monstrous.gdx.webgpu.graphics.WgTexture;
import com.monstrous.gdx.webgpu.graphics.utils.WgScreenUtils;
import imgui.example.basic.BasicExample;
import imgui.example.renderer.ExampleTexture;
import imgui.example.renderer.ImGuiRenderer;
import imgui.example.renderer.gdx.GdxGraphicsAdapter;
import imgui.example.renderer.gdx.GdxImGuiExampleApplication;
import imgui.gdx.ImGuiGdxWGPUImpl;
import java.nio.ByteBuffer;

public final class Launcher {
    private Launcher() {
    }

    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        ApplicationListener listener = new GdxImGuiExampleApplication(ImGuiGdxWGPUImpl::new,
                new WgpuGraphicsAdapter()) {
            @Override
            protected ImGuiRenderer createScreen() {
                return new BasicExample();
            }
        };
        new WgTeaApplication(listener, new WgTeaPreloadApplicationListener(), config);
    }

    private static final class WgpuGraphicsAdapter implements GdxGraphicsAdapter {
        @Override
        public void clear(float red, float green, float blue, float alpha) {
            WgScreenUtils.clear(red, green, blue, alpha);
        }

        @Override
        public ExampleTexture createTexture(String label, int width, int height, ByteBuffer pixels) {
            Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
            ByteBuffer target = pixmap.getPixels();
            target.clear();
            target.put(pixels.duplicate());
            target.flip();
            WgTexture texture = new WgTexture(pixmap, label, true);
            long id = texture.getTextureView().native_getAddressLong();
            return new ExampleTexture(id, width, height, texture::dispose);
        }
    }
}
