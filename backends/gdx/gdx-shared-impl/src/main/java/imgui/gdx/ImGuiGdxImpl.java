package imgui.gdx;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.jparser.runtime.helper.NativeString;
import imgui.ClipboardTextFunction;
import imgui.ImGui;
import imgui.ImGuiImpl;
import imgui.ImGuiPlatformIO;
import imgui.ImGuiViewportPlatformCallbacks;
import imgui.enums.ImGuiBackendFlags;
import imgui.enums.ImGuiConfigFlags;

public abstract class ImGuiGdxImpl implements ImGuiImpl {

    private ClipboardTextFunction clipboardTextFunction;
    private ImGuiViewportPlatformCallbacks viewportPlatformCallbacks;

    public ImGuiGdxImpl() {
        setupClipboard();
    }

    private void setupClipboard() {
        ImGuiPlatformIO platformIO = ImGui.GetPlatformIO();
        ClipboardTextFunction.setClipboardTextFunction(platformIO, clipboardTextFunction = new ClipboardTextFunction() {
            @Override
            public void onGetClipboardText(NativeString strOut) {
                String contents = Gdx.app.getClipboard().getContents();
                strOut.append(contents);
            }

            @Override
            public void onSetClipboardText(NativeString text) {
                String contents = text.c_str();
                Gdx.app.getClipboard().setContents(contents);
            }
        });
    }

    ImGuiViewportPlatformCallbacks createViewportPlatform() {
        return null;
    }

    void syncViewportPlatform() {
        boolean viewportsEnabled = hasConfigFlag(ImGuiConfigFlags.ViewportsEnable);
        if(!viewportsEnabled) {
            shutdownViewportPlatform();
            return;
        }
        if(viewportPlatformCallbacks == null) {
            ImGuiViewportPlatformCallbacks callbacks = createViewportPlatform();
            if(callbacks == null) {
                clearConfigFlag(ImGuiConfigFlags.ViewportsEnable);
                return;
            }
            viewportPlatformCallbacks = callbacks;
            ImGuiPlatformIO platformIO = ImGui.GetPlatformIO();
            ImGuiViewportPlatformCallbacks.setCallbacks(platformIO, callbacks);
            updateViewportMonitor(platformIO);
            ImGuiViewportPlatformCallbacks.setMainViewportPlatformHandle(ImGui.GetMainViewport(), callbacks);
            addBackendFlags(ImGuiBackendFlags.PlatformHasViewports.getValue()
                    | ImGuiBackendFlags.RendererHasViewports.getValue());
        }
        updateViewportMonitor();
    }

    private void shutdownViewportPlatform() {
        if(viewportPlatformCallbacks == null) {
            return;
        }
        ImGui.DestroyPlatformWindows();
        ImGuiViewportPlatformCallbacks.clearViewportPlatformHandles(ImGui.GetMainViewport());
        ImGuiViewportPlatformCallbacks.clearCallbacks(ImGui.GetPlatformIO());
        clearBackendFlags(ImGuiBackendFlags.PlatformHasViewports.getValue()
                | ImGuiBackendFlags.RendererHasViewports.getValue());
        viewportPlatformCallbacks.dispose();
        viewportPlatformCallbacks = null;
    }

    private void updateViewportMonitor() {
        updateViewportMonitor(ImGui.GetPlatformIO());
    }

    void updatePlatformWindows() {
        if(viewportPlatformCallbacks == null || !hasConfigFlag(ImGuiConfigFlags.ViewportsEnable)) {
            return;
        }
        updateViewportMonitor();
        ImGui.UpdatePlatformWindows();
    }

    private void updateViewportMonitor(ImGuiPlatformIO platformIO) {
        float width = Math.max(1, Gdx.graphics.getWidth());
        float height = Math.max(1, Gdx.graphics.getHeight());
        ImGuiViewportPlatformCallbacks.setMainMonitor(platformIO, 0.0f, 0.0f, width, height,
                0.0f, 0.0f, width, height, 1.0f);
    }

    private static void addBackendFlags(int flagsToAdd) {
        int flags = ImGui.GetIO().get_BackendFlags().getValue() | flagsToAdd;
        ImGui.GetIO().set_BackendFlags(ImGuiBackendFlags.CUSTOM.setValue(flags));
    }

    private static void clearBackendFlags(int flagsToClear) {
        int flags = ImGui.GetIO().get_BackendFlags().getValue() & ~flagsToClear;
        ImGui.GetIO().set_BackendFlags(ImGuiBackendFlags.CUSTOM.setValue(flags));
    }

    private static void addConfigFlag(ImGuiConfigFlags flag) {
        int flags = ImGui.GetIO().get_ConfigFlags().getValue() | flag.getValue();
        ImGui.GetIO().set_ConfigFlags(ImGuiConfigFlags.CUSTOM.setValue(flags));
    }

    private static void clearConfigFlag(ImGuiConfigFlags flag) {
        int flags = ImGui.GetIO().get_ConfigFlags().getValue() & ~flag.getValue();
        ImGui.GetIO().set_ConfigFlags(ImGuiConfigFlags.CUSTOM.setValue(flags));
    }

    private static boolean hasConfigFlag(ImGuiConfigFlags flag) {
        return (ImGui.GetIO().get_ConfigFlags().getValue() & flag.getValue()) != 0;
    }

    public void dispose() {
        clearConfigFlag(ImGuiConfigFlags.ViewportsEnable);
        shutdownViewportPlatform();
        if(clipboardTextFunction != null) {
            clipboardTextFunction.dispose();
            clipboardTextFunction = null;
        }
    }
}
