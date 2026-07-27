package imgui.example.textedit;

import com.github.xpenatan.jparser.runtime.helper.NativeLong;
import imgui.ImGui;
import imgui.ImTemp;
import imgui.example.renderer.ImGuiRenderer;
import imgui.enums.ImGuiCond;
import imgui.extension.textedit.TextEditor;
import imgui.extension.textedit.TextEditorLanguage;

public class TextEditExample extends ImGuiRenderer {

    private TextEditor editor;

    private NativeLong outLine;
    private NativeLong outColumn;

    @Override
    public void show() {
        super.show();

        outLine = new NativeLong();
        outColumn = new NativeLong();

        editor = new TextEditor();
        editor.SetLanguage(TextEditorLanguage.Lua);

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
        editor.GetCurrentCursorPosition(outLine, outColumn);

        ImGui.SetNextWindowSize(ImTemp.ImVec2_1(900, 600), ImGuiCond.Once);
        ImGui.Begin("Editor");

        String text = "\t" + (outLine.getValue() + 1) + "/" + (outColumn.getValue() + 1) + " " + editor.GetLineCount() + " | " + (editor.CanUndo() ? "*" : " ") + " | " + editor.GetLanguageName().c_str();
        ImGui.Text(text);

        editor.Render("Title", ImGui.GetContentRegionAvail());
        ImGui.End();
    }
}
