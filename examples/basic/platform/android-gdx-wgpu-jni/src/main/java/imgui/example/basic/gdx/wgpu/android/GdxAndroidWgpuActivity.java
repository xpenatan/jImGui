package imgui.example.basic.gdx.wgpu.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.monstrous.gdx.webgpu.backends.android.WgAndroidApplication;
import com.monstrous.gdx.webgpu.graphics.WgTexture;
import com.monstrous.gdx.webgpu.graphics.utils.WgScreenUtils;
import imgui.example.basic.BasicExample;
import imgui.example.renderer.ExampleTexture;
import imgui.example.renderer.ImGuiRenderer;
import imgui.example.renderer.gdx.GdxGraphicsAdapter;
import imgui.example.renderer.gdx.GdxImGuiExampleApplication;
import imgui.gdx.ImGuiGdxWGPUImpl;
import java.nio.ByteBuffer;

public final class GdxAndroidWgpuActivity extends WgAndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useGyroscope = false;
        config.useImmersiveMode = true;
        config.useWakelock = true;
        initialize(new GdxImGuiExampleApplication(ImGuiGdxWGPUImpl::new, new WgpuGraphicsAdapter()) {
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
