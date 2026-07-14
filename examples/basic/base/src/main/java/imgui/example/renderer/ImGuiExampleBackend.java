package imgui.example.renderer;

import java.nio.ByteBuffer;

public interface ImGuiExampleBackend {
    void beginFrame();

    void render();

    void clearScreen(float red, float green, float blue, float alpha);

    ExampleTexture createTexture(String label, int width, int height, ByteBuffer pixels);

    int framesPerSecond();

    void dispose();
}
