package imgui;

public interface ImGuiImpl {
    void newFrame();
    void render(ImDrawData drawData);
    void dispose();
}