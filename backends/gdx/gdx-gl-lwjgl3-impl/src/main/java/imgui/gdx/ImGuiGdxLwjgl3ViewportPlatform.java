package imgui.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.github.xpenatan.jparser.runtime.helper.NativeString;
import imgui.ImDrawData;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImGuiViewportPlatformCallbacks;

import java.util.HashMap;
import java.util.Map;

final class ImGuiGdxLwjgl3ViewportPlatform extends ImGuiViewportPlatformCallbacks {
    private final ImGuiGdxGLImpl renderer;
    private final Lwjgl3Application application;
    private final Map<Integer, ViewportWindow> windows = new HashMap<>();

    ImGuiGdxLwjgl3ViewportPlatform(ImGuiGdxGLImpl renderer) {
        if (!(Gdx.app instanceof Lwjgl3Application)) {
            throw new IllegalStateException("GDX multi-viewports require Lwjgl3Application");
        }
        this.renderer = renderer;
        application = (Lwjgl3Application)Gdx.app;
    }

    @Override
    protected void onCreateWindow(ImGuiViewport viewport) {
        int id = viewport.get_ID();
        if (windows.containsKey(id)) {
            return;
        }
        ViewportWindow window = new ViewportWindow(id);
        window.x = viewport.get_Pos().get_x();
        window.y = viewport.get_Pos().get_y();
        window.width = Math.max(1.0f, viewport.get_Size().get_x());
        window.height = Math.max(1.0f, viewport.get_Size().get_y());
        windows.put(id, window);

        Lwjgl3WindowConfiguration config = new Lwjgl3WindowConfiguration();
        config.setTitle(window.title);
        config.setWindowedMode(Math.round(window.width), Math.round(window.height));
        config.setWindowPosition(Math.round(window.x), Math.round(window.y));
        config.setResizable(true);
        config.setInitialVisible(false);
        config.setWindowListener(new ViewportWindowListener(window));
        window.window = application.newWindow(new ViewportApplicationListener(id), config);
    }

    @Override
    protected void onDestroyWindow(ImGuiViewport viewport) {
        ViewportWindow window = windows.remove(viewport.get_ID());
        if (window == null) {
            return;
        }
        window.destroyed = true;
        if (window.created) {
            window.window.closeWindow();
        }
    }

    @Override
    protected void onShowWindow(ImGuiViewport viewport) {
        ViewportWindow window = windows.get(viewport.get_ID());
        if (window == null) {
            return;
        }
        window.visible = true;
        if (window.created) {
            window.window.setVisible(true);
        }
    }

    @Override
    protected void onSetWindowPos(ImGuiViewport viewport, float x, float y) {
        ViewportWindow window = window(viewport);
        window.x = x;
        window.y = y;
        if (window.created) {
            window.window.setPosition(Math.round(x), Math.round(y));
        }
    }

    @Override
    protected float onGetWindowPosX(ImGuiViewport viewport) {
        ViewportWindow window = window(viewport);
        return window.created ? window.window.getPositionX() : window.x;
    }

    @Override
    protected float onGetWindowPosY(ImGuiViewport viewport) {
        ViewportWindow window = window(viewport);
        return window.created ? window.window.getPositionY() : window.y;
    }

    @Override
    protected void onSetWindowSize(ImGuiViewport viewport, float width, float height) {
        ViewportWindow window = window(viewport);
        window.width = width;
        window.height = height;
        if (window.graphics != null) {
            window.graphics.setWindowedMode(Math.round(width), Math.round(height));
        }
    }

    @Override
    protected float onGetWindowSizeX(ImGuiViewport viewport) {
        ViewportWindow window = window(viewport);
        return window.graphics != null ? window.graphics.getWidth() : window.width;
    }

    @Override
    protected float onGetWindowSizeY(ImGuiViewport viewport) {
        ViewportWindow window = window(viewport);
        return window.graphics != null ? window.graphics.getHeight() : window.height;
    }

    @Override
    protected float onGetWindowFramebufferScaleX(ImGuiViewport viewport) {
        ViewportWindow window = window(viewport);
        if (!window.created) {
            return Gdx.graphics.getWidth() > 0
                    ? Gdx.graphics.getBackBufferWidth() / (float) Gdx.graphics.getWidth()
                    : 1.0f;
        }
        Lwjgl3Graphics graphics = window.graphics;
        if (graphics == null) {
            return Gdx.graphics.getWidth() > 0
                    ? Gdx.graphics.getBackBufferWidth() / (float) Gdx.graphics.getWidth()
                    : 1.0f;
        }
        float width = graphics.getWidth();
        return width > 0.0f ? graphics.getBackBufferWidth() / width : 1.0f;
    }

    @Override
    protected float onGetWindowFramebufferScaleY(ImGuiViewport viewport) {
        ViewportWindow window = window(viewport);
        if (!window.created) {
            return Gdx.graphics.getHeight() > 0
                    ? Gdx.graphics.getBackBufferHeight() / (float) Gdx.graphics.getHeight()
                    : 1.0f;
        }
        Lwjgl3Graphics graphics = window.graphics;
        if (graphics == null) {
            return Gdx.graphics.getHeight() > 0
                    ? Gdx.graphics.getBackBufferHeight() / (float) Gdx.graphics.getHeight()
                    : 1.0f;
        }
        float height = graphics.getHeight();
        return height > 0.0f ? graphics.getBackBufferHeight() / height : 1.0f;
    }

    @Override
    protected void onSetWindowFocus(ImGuiViewport viewport) {
        ViewportWindow window = window(viewport);
        if (window.created) {
            window.window.focusWindow();
        }
    }

    @Override
    protected boolean onGetWindowFocus(ImGuiViewport viewport) {
        ViewportWindow window = window(viewport);
        return window.created && window.window.isFocused();
    }

    @Override
    protected boolean onGetWindowMinimized(ImGuiViewport viewport) {
        ViewportWindow window = window(viewport);
        return window.created && window.window.isIconified();
    }

    @Override
    protected void onSetWindowTitle(ImGuiViewport viewport, NativeString title) {
        ViewportWindow window = window(viewport);
        window.title = title.c_str();
        if (window.created) {
            window.window.setTitle(window.title);
        }
    }

    @Override
    protected void onSetWindowAlpha(ImGuiViewport viewport, float alpha) {
    }

    @Override
    protected void onUpdateWindow(ImGuiViewport viewport) {
        ViewportWindow window = windows.get(viewport.get_ID());
        if (window != null && window.closeRequested) {
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
        return Math.max(onGetWindowFramebufferScaleX(viewport), onGetWindowFramebufferScaleY(viewport));
    }

    @Override
    protected void onChangedViewport(ImGuiViewport viewport) {
    }

    @Override
    protected void onRendererCreateWindow(ImGuiViewport viewport) {
    }

    @Override
    protected void onRendererDestroyWindow(ImGuiViewport viewport) {
    }

    @Override
    protected void onRendererSetWindowSize(ImGuiViewport viewport, float width, float height) {
    }

    @Override
    protected void onRendererRenderWindow(ImGuiViewport viewport) {
    }

    @Override
    protected void onRendererSwapBuffers(ImGuiViewport viewport) {
    }

    private ViewportWindow window(ImGuiViewport viewport) {
        ViewportWindow window = windows.get(viewport.get_ID());
        if (window != null) {
            return window;
        }
        ViewportWindow main = new ViewportWindow(viewport.get_ID());
        main.width = Gdx.graphics.getWidth();
        main.height = Gdx.graphics.getHeight();
        return main;
    }

    private final class ViewportApplicationListener implements ApplicationListener {
        private final int viewportId;

        private ViewportApplicationListener(int viewportId) {
            this.viewportId = viewportId;
        }

        @Override
        public void create() {
            ViewportWindow window = windows.get(viewportId);
            if (window != null && Gdx.graphics instanceof Lwjgl3Graphics) {
                window.graphics = (Lwjgl3Graphics)Gdx.graphics;
                window.graphics.setWindowedMode(Math.round(window.width), Math.round(window.height));
            }
        }

        @Override
        public void resize(int width, int height) {
            ViewportWindow window = windows.get(viewportId);
            if (window != null) {
                window.width = width;
                window.height = height;
            }
        }

        @Override
        public void render() {
            ViewportWindow window = windows.get(viewportId);
            if (window == null || !window.created || window.closeRequested) {
                return;
            }
            ImGuiViewport viewport = ImGui.FindViewportByID(viewportId);
            if (viewport == null || viewport.native_isNULL()) {
                return;
            }
            ImDrawData drawData = viewport.get_DrawData();
            if (drawData == null || drawData.native_isNULL()) {
                return;
            }
            renderer.renderViewport(drawData);
        }

        @Override
        public void pause() {
        }

        @Override
        public void resume() {
        }

        @Override
        public void dispose() {
        }
    }

    private static final class ViewportWindowListener implements Lwjgl3WindowListener {
        private final ViewportWindow window;

        private ViewportWindowListener(ViewportWindow window) {
            this.window = window;
        }

        @Override
        public void created(Lwjgl3Window window) {
            this.window.window = window;
            this.window.created = true;
            applyPending();
            if (this.window.destroyed) {
                window.closeWindow();
            }
        }

        @Override
        public void iconified(boolean isIconified) {
        }

        @Override
        public void maximized(boolean isMaximized) {
        }

        @Override
        public void focusLost() {
        }

        @Override
        public void focusGained() {
        }

        @Override
        public boolean closeRequested() {
            window.closeRequested = true;
            return false;
        }

        @Override
        public void filesDropped(String[] files) {
        }

        @Override
        public void refreshRequested() {
        }

        private void applyPending() {
            window.window.setPosition(Math.round(window.x), Math.round(window.y));
            window.window.setTitle(window.title);
            window.window.setVisible(window.visible);
        }
    }

    private static final class ViewportWindow {
        private final int id;
        private Lwjgl3Window window;
        private Lwjgl3Graphics graphics;
        private boolean created;
        private boolean visible;
        private boolean closeRequested;
        private boolean destroyed;
        private float x;
        private float y;
        private float width = 1.0f;
        private float height = 1.0f;
        private String title = "Dear ImGui";

        private ViewportWindow(int id) {
            this.id = id;
        }
    }
}
