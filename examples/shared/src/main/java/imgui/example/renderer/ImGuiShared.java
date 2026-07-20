package imgui.example.renderer;

import imgui.ImGui;
import imgui.ImTemp;
import imgui.ImTextureRef;
import imgui.enums.ImGuiConfigFlags;

import java.nio.ByteBuffer;

public final class ImGuiShared {
    private static ImGuiExampleBackend backend;

    private ImGuiShared() {
    }

    public static void initialize(ImGuiExampleBackend backend) {
        if (ImGuiShared.backend != null) {
            return;
        }
        if (backend == null) {
            throw new IllegalArgumentException("ImGui example backend cannot be null");
        }
        ImGuiShared.backend = backend;
        ImGui.GetIO().set_ConfigFlags(ImGuiConfigFlags.DockingEnable.or(ImGuiConfigFlags.ViewportsEnable));
        ImGui.StyleColorsDark();
    }

    public static void beginFrame() {
        backend().beginFrame();
    }

    public static void render() {
        backend().render();
    }

    public static void clearScreen(float red, float green, float blue, float alpha) {
        backend().clearScreen(red, green, blue, alpha);
    }

    public static ExampleTexture createSolidTexture(String label, int width, int height, int red, int green, int blue,
            int alpha) {
        ByteBuffer pixels = ByteBuffer.allocateDirect(width * height * 4);
        for (int i = 0; i < width * height; i++) {
            pixels.put((byte) red);
            pixels.put((byte) green);
            pixels.put((byte) blue);
            pixels.put((byte) alpha);
        }
        pixels.flip();
        return createTexture(label, width, height, pixels);
    }

    public static ExampleTexture createCheckerTexture(String label, int width, int height) {
        ByteBuffer pixels = ByteBuffer.allocateDirect(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean bright = ((x / 8) + (y / 8)) % 2 == 0;
                pixels.put((byte) (bright ? 235 : 60));
                pixels.put((byte) (bright ? 190 : 85));
                pixels.put((byte) (bright ? 75 : 145));
                pixels.put((byte) 255);
            }
        }
        pixels.flip();
        return createTexture(label, width, height, pixels);
    }

    public static ExampleTexture createCircleTexture(String label, int width, int height) {
        ByteBuffer pixels = ByteBuffer.allocateDirect(width * height * 4);
        float cx = (width - 1) * 0.5f;
        float cy = (height - 1) * 0.5f;
        float radius = Math.min(width, height) * 0.42f;
        float radius2 = radius * radius;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float dx = x - cx;
                float dy = y - cy;
                boolean inside = dx * dx + dy * dy <= radius2;
                pixels.put((byte) (inside ? 115 : 230));
                pixels.put((byte) (inside ? 225 : 120));
                pixels.put((byte) (inside ? 130 : 120));
                pixels.put((byte) 255);
            }
        }
        pixels.flip();
        return createTexture(label, width, height, pixels);
    }

    public static ImTextureRef textureRef(ExampleTexture texture) {
        return ImTemp.ImTextureRef_1(texture.id());
    }

    public static int framesPerSecond() {
        return backend().framesPerSecond();
    }

    public static int rgba(int red, int green, int blue, int alpha) {
        return (alpha & 255) << 24 | (blue & 255) << 16 | (green & 255) << 8 | (red & 255);
    }

    public static void dispose() {
        if (backend != null) {
            backend.dispose();
        }
        backend = null;
    }

    private static ExampleTexture createTexture(String label, int width, int height, ByteBuffer pixels) {
        return backend().createTexture(label, width, height, pixels);
    }

    private static ImGuiExampleBackend backend() {
        if (backend == null) {
            throw new IllegalStateException("Example ImGui backend has not been initialized");
        }
        return backend;
    }
}
