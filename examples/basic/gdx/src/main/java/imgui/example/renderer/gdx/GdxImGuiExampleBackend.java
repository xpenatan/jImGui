package imgui.example.renderer.gdx;

import com.badlogic.gdx.Gdx;
import imgui.ImGui;
import imgui.example.renderer.ExampleTexture;
import imgui.example.renderer.ImGuiExampleBackend;
import imgui.gdx.ImGuiGdxImpl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

final class GdxImGuiExampleBackend implements ImGuiExampleBackend {
    private final ImGuiGdxImpl renderer;
    private final GdxGraphicsAdapter graphics;
    private final List<ExampleTexture> textures = new ArrayList<>();

    GdxImGuiExampleBackend(ImGuiGdxImpl renderer, GdxGraphicsAdapter graphics) {
        this.renderer = renderer;
        this.graphics = graphics;
    }

    @Override
    public void beginFrame() {
        renderer.newFrame();
    }

    @Override
    public void render() {
        ImGui.Render();
        renderer.render(ImGui.GetDrawData());
    }

    @Override
    public void clearScreen(float red, float green, float blue, float alpha) {
        graphics.clear(red, green, blue, alpha);
    }

    @Override
    public ExampleTexture createTexture(String label, int width, int height, ByteBuffer pixels) {
        ExampleTexture texture = graphics.createTexture(label, width, height, pixels);
        textures.add(texture);
        return texture;
    }

    @Override
    public int framesPerSecond() {
        return Gdx.graphics.getFramesPerSecond();
    }

    @Override
    public void dispose() {
        for (ExampleTexture texture : textures) {
            texture.close();
        }
        textures.clear();
        renderer.dispose();
        ImGui.DestroyContext();
    }
}
