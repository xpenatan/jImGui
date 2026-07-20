package imgui.example.basic;

import com.badlogic.gdx.graphics.Pixmap;
import com.github.xpenatan.webgpu.JWebGPUBackend;
import com.monstrous.gdx.webgpu.backends.desktop.WgDesktopApplication;
import com.monstrous.gdx.webgpu.backends.desktop.WgDesktopApplicationConfiguration;
import com.monstrous.gdx.webgpu.graphics.WgTexture;
import com.monstrous.gdx.webgpu.graphics.utils.WgScreenUtils;
import imgui.example.renderer.ExampleTexture;
import imgui.example.renderer.ImGuiRenderer;
import imgui.example.renderer.gdx.GdxGraphicsAdapter;
import imgui.example.renderer.gdx.GdxImGuiExampleApplication;
import imgui.gdx.ImGuiGdxWGPUImpl;

import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) {
        WgDesktopApplicationConfiguration config = new WgDesktopApplicationConfiguration();
        config.setWindowedMode(1444, 800);
        config.setTitle("ImGui GDX WGPU FFM Basic Example");
        config.backendWebGPU = JWebGPUBackend.WGPU;
        config.useVsync(false);
        new WgDesktopApplication(new GdxImGuiExampleApplication(ImGuiGdxWGPUImpl::new,
                new WgpuGraphicsAdapter()) {
            @Override
            protected ImGuiRenderer createScreen() {
                return new BasicExample();
            }
        }, config);
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
