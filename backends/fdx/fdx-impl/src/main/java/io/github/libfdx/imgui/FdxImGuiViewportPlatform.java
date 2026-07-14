package io.github.libfdx.imgui;

import com.github.xpenatan.jparser.runtime.helper.NativeString;
import imgui.ImGui;
import imgui.ImGuiPlatformIO;
import imgui.ImGuiViewport;
import imgui.ImGuiViewportPlatformCallbacks;
import imgui.enums.ImGuiBackendFlags;
import imgui.enums.ImGuiViewportFlags;
import io.github.libfdx.Fdx;
import io.github.libfdx.core.FdxException;
import io.github.libfdx.display.Display;
import io.github.libfdx.display.DisplayConfig;
import io.github.libfdx.graphics.GraphicsAttachmentProvider;
import io.github.libfdx.graphics.GraphicsAttachment;
import io.github.libfdx.graphics.GraphicsConfig;
import io.github.libfdx.input.Input;

import java.util.HashMap;
import java.util.Map;

final class FdxImGuiViewportPlatform extends ImGuiViewportPlatformCallbacks {
    private final Fdx fdx;
    private final Display mainDisplay;
    private final Input input;
    private final FdxImGuiRenderer mainRenderer;
    private final FdxImGuiTextureRegistry textures;
    private final GraphicsAttachmentProvider graphicsProvider;
    private final Map<Integer, WindowData> windows = new HashMap<>();
    private boolean installed;
    private boolean disposed;

    FdxImGuiViewportPlatform(Fdx fdx, Display mainDisplay, Input input, FdxImGuiRenderer mainRenderer,
            FdxImGuiTextureRegistry textures,
            GraphicsAttachmentProvider graphicsProvider) {
        this.fdx = fdx;
        this.mainDisplay = mainDisplay;
        this.input = input;
        this.mainRenderer = mainRenderer;
        this.textures = textures;
        this.graphicsProvider = graphicsProvider;
    }

    void install() {
        if (installed) {
            return;
        }
        if (!fdx.displays().supportsMultiple()) {
            throw new FdxException("The active FDX display backend does not support multiple displays");
        }
        if (!fdx.graphics().supportsMultiple()) {
            throw new FdxException("The active FDX graphics backend does not support multiple graphics contexts");
        }
        if (graphicsProvider == null) {
            throw new FdxException("GraphicsAttachmentProvider is required when ImGuiConfigFlags.ViewportsEnable is set");
        }
        if (!(mainRenderer instanceof FdxImGuiViewportRendererFactory)) {
            throw new FdxException("The active FDX ImGui renderer cannot create viewport renderers");
        }
        ImGuiPlatformIO platformIO = ImGui.GetPlatformIO();
        ImGuiViewportPlatformCallbacks.setCallbacks(platformIO, this);
        updateMainMonitor(platformIO);
        ImGuiViewportPlatformCallbacks.setMainViewportPlatformHandle(ImGui.GetMainViewport(), this);
        addBackendFlags(ImGuiBackendFlags.PlatformHasViewports.getValue()
                | ImGuiBackendFlags.RendererHasViewports.getValue());
        installed = true;
    }

    void updateMainMonitor() {
        updateMainMonitor(ImGui.GetPlatformIO());
    }

    void renderPlatformWindows() {
        if (!installed || disposed) {
            return;
        }
        updateMainMonitor();
        ImGui.UpdatePlatformWindows();
        ImGui.RenderPlatformWindowsDefault();
    }

    void shutdown() {
        if (disposed) {
            return;
        }
        disposed = true;
        if (installed) {
            ImGui.DestroyPlatformWindows();
            ImGuiViewportPlatformCallbacks.clearViewportPlatformHandles(ImGui.GetMainViewport());
            ImGuiViewportPlatformCallbacks.clearCallbacks(ImGui.GetPlatformIO());
            clearBackendFlags(ImGuiBackendFlags.PlatformHasViewports.getValue()
                    | ImGuiBackendFlags.RendererHasViewports.getValue());
        }
        for (WindowData window : windows.values()) {
            disposeWindow(window);
        }
        windows.clear();
        dispose();
    }

    @Override
    protected void onCreateWindow(ImGuiViewport viewport) {
        int id = viewport.get_ID();
        if (windows.containsKey(id)) {
            return;
        }
        ImGuiViewport sizeSource = viewport;
        int width = Math.max(1, Math.round(sizeSource.get_Size().get_x()));
        int height = Math.max(1, Math.round(sizeSource.get_Size().get_y()));
        Display display = fdx.displays().create(new DisplayConfig()
                .title("Dear ImGui")
                .size(width, height)
                .resizable(true)
                .visible(false));
        GraphicsAttachment graphics = fdx.graphics().create(
                GraphicsConfig.provider(graphicsProvider).display(display));
        FdxImGuiRenderer renderer = ((FdxImGuiViewportRendererFactory) mainRenderer).createViewportRenderer();
        if (renderer == null) {
            display.requestClose();
            throw new FdxException("FdxImGuiViewportRendererFactory returned null");
        }
        renderer.initialize(new FdxImGuiRendererContext(fdx, display, input, graphics, textures));
        windows.put(id, new WindowData(display, graphics, renderer));
    }

    @Override
    protected void onDestroyWindow(ImGuiViewport viewport) {
        WindowData window = windows.remove(viewport.get_ID());
        if (window != null) {
            disposeWindow(window);
        }
    }

    @Override
    protected void onShowWindow(ImGuiViewport viewport) {
        window(viewport).display.show();
    }

    @Override
    protected void onSetWindowPos(ImGuiViewport viewport, float x, float y) {
        WindowData window = window(viewport);
        window.display.position(Math.round(x), Math.round(y));
    }

    @Override
    protected float onGetWindowPosX(ImGuiViewport viewport) {
        return window(viewport).display.x();
    }

    @Override
    protected float onGetWindowPosY(ImGuiViewport viewport) {
        return window(viewport).display.y();
    }

    @Override
    protected void onSetWindowSize(ImGuiViewport viewport, float width, float height) {
        WindowData window = window(viewport);
        window.display.size(Math.max(1, Math.round(width)), Math.max(1, Math.round(height)));
        resizeGraphics(window);
    }

    @Override
    protected float onGetWindowSizeX(ImGuiViewport viewport) {
        WindowData window = window(viewport);
        return window.display.width();
    }

    @Override
    protected float onGetWindowSizeY(ImGuiViewport viewport) {
        WindowData window = window(viewport);
        return window.display.height();
    }

    @Override
    protected float onGetWindowFramebufferScaleX(ImGuiViewport viewport) {
        WindowData window = window(viewport);
        int width = window.display.width();
        return width > 0 ? window.display.framebufferWidth() / (float) width : 1.0f;
    }

    @Override
    protected float onGetWindowFramebufferScaleY(ImGuiViewport viewport) {
        WindowData window = window(viewport);
        int height = window.display.height();
        return height > 0 ? window.display.framebufferHeight() / (float) height : 1.0f;
    }

    @Override
    protected void onSetWindowFocus(ImGuiViewport viewport) {
        window(viewport).display.focus();
    }

    @Override
    protected boolean onGetWindowFocus(ImGuiViewport viewport) {
        return window(viewport).display.focused();
    }

    @Override
    protected boolean onGetWindowMinimized(ImGuiViewport viewport) {
        return window(viewport).display.minimized();
    }

    @Override
    protected void onSetWindowTitle(ImGuiViewport viewport, NativeString title) {
        WindowData window = window(viewport);
        if (window.display != null) {
            window.display.title(title.c_str());
        }
    }

    @Override
    protected void onSetWindowAlpha(ImGuiViewport viewport, float alpha) {
        window(viewport).display.opacity(alpha);
    }

    @Override
    protected void onUpdateWindow(ImGuiViewport viewport) {
        WindowData window = windows.get(viewport.get_ID());
        if (window != null && window.display.closeRequested()) {
            viewport.set_PlatformRequestClose(true);
        }
    }

    @Override
    protected void onRenderWindow(ImGuiViewport viewport) {
    }

    @Override
    protected void onSwapBuffers(ImGuiViewport viewport) {
    }

    @Override
    protected float onGetWindowDpiScale(ImGuiViewport viewport) {
        WindowData window = window(viewport);
        Display display = window.display != null ? window.display : mainDisplay;
        return Math.max(0.01f, display.contentScale());
    }

    @Override
    protected void onChangedViewport(ImGuiViewport viewport) {
    }

    @Override
    protected void onRendererCreateWindow(ImGuiViewport viewport) {
    }

    @Override
    protected void onRendererDestroyWindow(ImGuiViewport viewport) {
        WindowData window = windows.get(viewport.get_ID());
        if (window != null) {
            window.disposeRenderer();
        }
    }

    @Override
    protected void onRendererSetWindowSize(ImGuiViewport viewport, float width, float height) {
        resizeGraphics(window(viewport));
    }

    @Override
    protected void onRendererRenderWindow(ImGuiViewport viewport) {
        WindowData window = windows.get(viewport.get_ID());
        if (window == null || window.renderer == null || window.renderer.isDisposed()
                || window.graphics == null || window.graphics.isDisposed() || window.display.minimized()) {
            return;
        }
        window.graphics.processEvents();
        resizeGraphics(window);
        if (window.graphics.beginFrame()) {
            try {
                if ((viewport.get_Flags().getValue() & ImGuiViewportFlags.NoRendererClear.getValue()) == 0) {
                    window.graphics.clear(0.1f, 0.1f, 0.1f, 1.0f);
                }
                window.renderer.render(viewport.get_DrawData());
            } finally {
                window.graphics.endFrame();
            }
        }
    }

    @Override
    protected void onRendererSwapBuffers(ImGuiViewport viewport) {
    }

    private WindowData window(ImGuiViewport viewport) {
        WindowData window = windows.get(viewport.get_ID());
        if (window != null) {
            return window;
        }
        return new WindowData(mainDisplay, null, mainRenderer);
    }

    private void updateMainMonitor(ImGuiPlatformIO platformIO) {
        float monitorX = mainDisplay.monitorX();
        float monitorY = mainDisplay.monitorY();
        float monitorWidth = Math.max(1, mainDisplay.monitorWidth());
        float monitorHeight = Math.max(1, mainDisplay.monitorHeight());
        ImGuiViewportPlatformCallbacks.setMainMonitor(platformIO, monitorX, monitorY, monitorWidth, monitorHeight,
                mainDisplay.workAreaX(), mainDisplay.workAreaY(), Math.max(1, mainDisplay.workAreaWidth()),
                Math.max(1, mainDisplay.workAreaHeight()), Math.max(0.01f, mainDisplay.contentScale()));
    }

    private static void addBackendFlags(int flagsToAdd) {
        int flags = ImGui.GetIO().get_BackendFlags().getValue() | flagsToAdd;
        ImGui.GetIO().set_BackendFlags(ImGuiBackendFlags.CUSTOM.setValue(flags));
    }

    private static void clearBackendFlags(int flagsToClear) {
        int flags = ImGui.GetIO().get_BackendFlags().getValue() & ~flagsToClear;
        ImGui.GetIO().set_BackendFlags(ImGuiBackendFlags.CUSTOM.setValue(flags));
    }

    private void resizeGraphics(WindowData window) {
        if (window.graphics != null && !window.graphics.isDisposed()) {
            window.graphics.resize(window.display.framebufferWidth(), window.display.framebufferHeight());
        }
    }

    private void disposeWindow(WindowData window) {
        window.disposeRenderer();
        if (window.graphics != null) {
            fdx.graphics().destroy(window.graphics);
        }
        if (window.display != null && window.display != mainDisplay) {
            fdx.displays().destroy(window.display);
        }
    }

    private static final class WindowData {
        private final Display display;
        private final GraphicsAttachment graphics;
        private FdxImGuiRenderer renderer;

        private WindowData(Display display, GraphicsAttachment graphics, FdxImGuiRenderer renderer) {
            this.display = display;
            this.graphics = graphics;
            this.renderer = renderer;
        }

        private void disposeRenderer() {
            if (renderer != null && !renderer.isDisposed()) {
                renderer.dispose();
            }
            renderer = null;
        }

    }
}
