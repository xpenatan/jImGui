package imgui.example.basic.fdx.android;

import io.github.libfdx.graphics.GraphicsAttachmentProvider;

public final class FdxAndroidGlesActivity extends FdxAndroidActivity {
    @Override
    protected GraphicsAttachmentProvider createGraphicsProvider() {
        return gles();
    }

    @Override
    protected String graphicsName() {
        return "OpenGL ES";
    }
}
