package imgui.example.basic.fdx.android;

import io.github.libfdx.graphics.GraphicsAttachmentProvider;

public final class FdxAndroidVulkanActivity extends FdxAndroidActivity {
    @Override
    protected GraphicsAttachmentProvider createGraphicsProvider() {
        return vulkan();
    }

    @Override
    protected String graphicsName() {
        return "Vulkan";
    }
}
