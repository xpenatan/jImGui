package io.github.libfdx.imgui;

import imgui.ImGui;
import imgui.ImGuiContext;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.enums.ImGuiConfigFlags;
import io.github.libfdx.Fdx;
import io.github.libfdx.core.FdxException;
import io.github.libfdx.display.Display;
import io.github.libfdx.graphics.GraphicsAttachmentProvider;
import io.github.libfdx.graphics.GraphicsContext;
import io.github.libfdx.input.Input;

public final class FdxImGui implements io.github.libfdx.core.Disposable {
    private final Fdx fdx;
    private final Display display;
    private final Input input;
    private final GraphicsContext graphics;
    private final FdxImGuiRenderer renderer;
    private final FdxImGuiTextureRegistry textures;
    private final FdxImGuiInputBridge inputBridge;
    private final FdxImGuiRendererContext rendererContext;
    private final ImGuiContext context;
    private final GraphicsAttachmentProvider viewportGraphicsProvider;
    private FdxImGuiViewportPlatform viewportPlatform;
    private boolean frameBegun;
    private boolean disposed;

    public static FdxImGui create(Fdx fdx) {
        return create(fdx, FdxImGuiRenderers.auto());
    }

    public static FdxImGui create(Fdx fdx, GraphicsAttachmentProvider viewportGraphicsProvider) {
        return create(fdx, FdxImGuiRenderers.auto(), viewportGraphicsProvider);
    }

    public static FdxImGui create(Fdx fdx, FdxImGuiRenderer renderer) {
        return create(fdx, renderer, null);
    }

    public static FdxImGui create(Fdx fdx, FdxImGuiRenderer renderer,
            GraphicsAttachmentProvider viewportGraphicsProvider) {
        if (fdx == null) {
            throw new FdxException("Fdx cannot be null");
        }
        return new FdxImGui(fdx, fdx.displays().main(), fdx.input(), fdx.graphics().main(), renderer,
                viewportGraphicsProvider);
    }

    public FdxImGui(Fdx fdx, Display display, Input input, GraphicsContext graphics, FdxImGuiRenderer renderer) {
        this(fdx, display, input, graphics, renderer, null);
    }

    public FdxImGui(Fdx fdx, Display display, Input input, GraphicsContext graphics, FdxImGuiRenderer renderer,
            GraphicsAttachmentProvider viewportGraphicsProvider) {
        if (fdx == null) {
            throw new FdxException("Fdx cannot be null");
        }
        if (display == null) {
            throw new FdxException("Display cannot be null");
        }
        if (input == null) {
            throw new FdxException("Input cannot be null");
        }
        if (graphics == null) {
            throw new FdxException("GraphicsContext cannot be null");
        }
        if (renderer == null) {
            throw new FdxException("FdxImGuiRenderer cannot be null");
        }
        this.fdx = fdx;
        this.display = display;
        this.input = input;
        this.graphics = graphics;
        this.renderer = renderer;
        this.viewportGraphicsProvider = viewportGraphicsProvider;
        ImGuiContext createdContext = ImGui.CreateContext();
        context = createdContext != null && !createdContext.native_isNULL()
                ? createdContext
                : ImGui.GetCurrentContext();
        if (context == null || context.native_isNULL()) {
            throw new FdxException("Could not create ImGui context");
        }
        ImGui.SetCurrentContext(context);
        ImGui.StyleColorsDark();
        textures = new FdxImGuiTextureRegistry();
        rendererContext = new FdxImGuiRendererContext(fdx, display, input, graphics, textures);
        renderer.initialize(rendererContext);
        inputBridge = new FdxImGuiInputBridge(context, input);
        input.addProcessor(inputBridge);
    }

    public ImGuiContext context() {
        return context;
    }

    public FdxImGuiTextureRegistry textures() {
        return textures;
    }

    public FdxImGuiRenderer renderer() {
        return renderer;
    }

    public void beginFrame() {
        ensureNotDisposed();
        if (frameBegun) {
            throw new FdxException("ImGui frame has already begun");
        }
        ImGui.SetCurrentContext(context);
        ImGuiIO io = ImGui.GetIO();
        syncViewportPlatform();
        if (viewportPlatform != null) {
            viewportPlatform.updateMainMonitor();
        }
        int width = display.width();
        int height = display.height();
        int framebufferWidth = display.framebufferWidth();
        int framebufferHeight = display.framebufferHeight();
        ImVec2 displaySize = io.get_DisplaySize();
        displaySize.set_x(width);
        displaySize.set_y(height);
        ImVec2 displayScale = io.get_DisplayFramebufferScale();
        displayScale.set_x(width > 0 ? framebufferWidth / (float) width : 1.0f);
        displayScale.set_y(height > 0 ? framebufferHeight / (float) height : 1.0f);
        float deltaTime = fdx.app().deltaTime();
        io.set_DeltaTime(deltaTime > 0.0f && Float.isFinite(deltaTime) ? deltaTime : 1.0f / 60.0f);
        if (viewportPlatform != null) {
            io.AddMousePosEvent(input.pointerScreenX(), input.pointerScreenY());
        } else {
            io.AddMousePosEvent(input.pointerX(), input.pointerY());
        }
        ImGui.NewFrame();
        frameBegun = true;
    }

    public void render() {
        ensureNotDisposed();
        if (!frameBegun) {
            throw new FdxException("FdxImGui.beginFrame() must be called before render()");
        }
        ImGui.SetCurrentContext(context);
        ImGui.Render();
        renderer.render(ImGui.GetDrawData());
        syncViewportPlatform();
        if (viewportPlatform != null) {
            viewportPlatform.renderPlatformWindows();
        }
        frameBegun = false;
    }

    public void endFrame() {
        render();
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        ImGui.SetCurrentContext(context);
        if (viewportPlatform != null) {
            viewportPlatform.shutdown();
            viewportPlatform = null;
        }
        input.removeProcessor(inputBridge);
        if (!renderer.isDisposed()) {
            renderer.dispose();
        }
        textures.disposeOwned();
        ImGui.DestroyContext(context);
        frameBegun = false;
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    private void ensureNotDisposed() {
        if (disposed) {
            throw new FdxException("FdxImGui has been disposed");
        }
    }

    private void syncViewportPlatform() {
        boolean viewportsEnabled = hasConfigFlag(ImGuiConfigFlags.ViewportsEnable);
        if (!viewportsEnabled) {
            if (viewportPlatform != null) {
                viewportPlatform.shutdown();
                viewportPlatform = null;
            }
            return;
        }
        if (viewportPlatform != null) {
            return;
        }
        if (!fdx.displays().supportsMultiple() || !fdx.graphics().supportsMultiple()) {
            clearConfigFlag(ImGuiConfigFlags.ViewportsEnable);
            return;
        }
        if (viewportGraphicsProvider == null) {
            throw new FdxException("ImGuiConfigFlags.ViewportsEnable requires a GraphicsAttachmentProvider. "
                    + "Pass the same provider used by the FDX application to FdxImGui.create(...).");
        }
        if (!(renderer instanceof FdxImGuiViewportRendererFactory)) {
            throw new FdxException("ImGuiConfigFlags.ViewportsEnable requires a renderer that can create viewport renderers");
        }
        FdxImGuiViewportPlatform platform = new FdxImGuiViewportPlatform(fdx, display, input, renderer, textures,
                viewportGraphicsProvider);
        platform.install();
        viewportPlatform = platform;
    }

    private static void clearConfigFlag(ImGuiConfigFlags flag) {
        int flags = ImGui.GetIO().get_ConfigFlags().getValue() & ~flag.getValue();
        ImGui.GetIO().set_ConfigFlags(ImGuiConfigFlags.CUSTOM.setValue(flags));
    }

    private static boolean hasConfigFlag(ImGuiConfigFlags flag) {
        return (ImGui.GetIO().get_ConfigFlags().getValue() & flag.getValue()) != 0;
    }
}
