package imgui.example.basic.fdx.android;

import android.content.Intent;
import imgui.example.basic.ImGuiGame;
import imgui.example.renderer.FdxExampleCapture;
import io.github.libfdx.application.ApplicationListener;
import io.github.libfdx.backend.android.AndroidApplicationActivity;
import io.github.libfdx.backend.android.AndroidApplicationConfig;
import io.github.libfdx.backend.android.AndroidGlesProvider;
import io.github.libfdx.backend.android.AndroidVulkanProvider;
import io.github.libfdx.graphics.GraphicsAttachmentProvider;
import io.github.libfdx.graphics.wgpu.WGPUProvider;
import io.github.libfdx.imgui.FdxImGuiRenderers;

import java.io.File;

abstract class FdxAndroidActivity extends AndroidApplicationActivity {
    private GraphicsAttachmentProvider graphicsProvider;

    @Override
    protected final AndroidApplicationConfig createApplicationConfig() {
        graphicsProvider = createGraphicsProvider();
        return new AndroidApplicationConfig()
                .title("ImGui FDX Android " + graphicsName() + " Basic Example")
                .graphics(graphicsProvider);
    }

    @Override
    protected final ApplicationListener createApplicationListener() {
        configureCapture();
        FdxExampleCapture capture = new FdxExampleCapture();
        return new ImGuiGame(FdxImGuiRenderers.auto(), graphicsProvider) {
            @Override
            public void onFrameEnd() {
                capture.onFrameEnd(fdx());
            }
        };
    }

    private void configureCapture() {
        Intent intent = getIntent();
        String screenshot = intent.getStringExtra("jimgui.example.screenshot");
        if (screenshot == null || screenshot.trim().length() == 0) {
            return;
        }
        File output = new File(getFilesDir(), new File(screenshot).getName());
        System.setProperty("jimgui.example.screenshot", output.getAbsolutePath());
        setPropertyFromExtra(intent, "jimgui.example.screenshotAfterFrames");
        setPropertyFromExtra(intent, "jimgui.example.exitAfterFrames");
    }

    private static void setPropertyFromExtra(Intent intent, String name) {
        String value = intent.getStringExtra(name);
        if (value != null && value.length() > 0) {
            System.setProperty(name, value);
        }
    }

    protected abstract GraphicsAttachmentProvider createGraphicsProvider();

    protected abstract String graphicsName();

    protected static GraphicsAttachmentProvider gles() {
        return new AndroidGlesProvider();
    }

    protected static GraphicsAttachmentProvider vulkan() {
        return new AndroidVulkanProvider();
    }

    protected static GraphicsAttachmentProvider wgpu() {
        return new WGPUProvider();
    }
}
