package imgui.example.basic.fdx.android;

import io.github.libfdx.graphics.GraphicsAttachmentProvider;

public final class FdxAndroidWgpuActivity extends FdxAndroidActivity {
    @Override
    protected GraphicsAttachmentProvider createGraphicsProvider() {
        return wgpu();
    }

    @Override
    protected String graphicsName() {
        return "WGPU";
    }
}
