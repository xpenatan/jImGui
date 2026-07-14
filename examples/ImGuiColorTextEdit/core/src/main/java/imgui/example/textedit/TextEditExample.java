package imgui.example.textedit;

import com.github.xpenatan.jparser.runtime.helper.NativeInt;
import imgui.ImGui;
import imgui.ImTemp;
import imgui.example.renderer.ImGuiRenderer;
import imgui.enums.ImGuiCond;
import imgui.extension.textedit.LanguageDefinitionId;
import imgui.extension.textedit.TextEditor;

public class TextEditExample extends ImGuiRenderer {

    private TextEditor editor;

    private NativeInt outLine;
    private NativeInt outColumn;

    @Override
    public void show() {
        super.show();

        outLine = new NativeInt();
        outColumn = new NativeInt();

        editor = new TextEditor();

        LanguageDefinitionId lua = LanguageDefinitionId.Lua;
        editor.SetLanguageDefinition(lua);


        String code = "\n" +
                "function onCreate()\n" +
                "\n" +
                "end\n" +
                "\n\n" +
                "function onRender(delta)\n" +
                "\n" +
                "end\n";
        editor.SetText(code);
    }

    @Override
    public void renderImGui() {
        editor.GetCursorPosition(outLine, outColumn);

        ImGui.SetNextWindowSize(ImTemp.ImVec2_1(900, 600), ImGuiCond.Once);
        ImGui.Begin("Editor");

        String text = "\t" + (outLine.getValue() + 1) + "/" + (outColumn.getValue() + 1) + " " + editor.GetLineCount() + " | " + (editor.CanUndo() ? "*" : " ") + " | " + editor.GetLanguageDefinitionName().c_str();
        ImGui.Text(text);

        editor.Render("Title", ImGui.IsWindowFocused(), ImGui.GetContentRegionAvail());
        ImGui.End();
    }
}
