package imgui;

import com.github.xpenatan.jparser.runtime.helper.NativeByteArray;
import com.github.xpenatan.jparser.runtime.helper.NativeIntArray;
import com.github.xpenatan.jParser.api.NativeObject;
import imgui.enums.ImDrawListFlags;
import imgui.enums.ImDrawFlags;
import imgui.enums.ImGuiCol;
import imgui.enums.ImGuiColorEditFlags;
import imgui.enums.ImGuiConfigFlags;
import imgui.enums.ImGuiInputTextFlags;
import imgui.enums.ImGuiItemFlags;
import imgui.enums.ImGuiListClipperFlags;
import imgui.enums.ImGuiMultiSelectFlags;
import imgui.enums.ImGuiNavRenderCursorFlags;
import imgui.enums.ImGuiSelectableFlags;
import imgui.enums.ImGuiTabBarFlags;
import imgui.enums.ImGuiTreeNodeFlags;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ImGuiBindingVersionTest {
    private static ImGuiContext context;

    @BeforeClass
    public static void setUpNativeLibrary() {
        String nativeLibrary = System.getProperty("jimgui.test.imgui.native");
        assertNotNull("The host ImGui native library was not configured", nativeLibrary);
        System.load(nativeLibrary);
        context = ImGui.CreateContext();
        assertNotNull(context);
    }

    @AfterClass
    public static void tearDownContext() {
        ImGui.DestroyContext(context);
    }

    @Test
    public void exposesVersionString() {
        assertFalse(ImGui.GetVersion().native_isNULL());
        assertEquals("1.92.9b", getNativeVersionString());
    }

    @Test
    public void exposesChanged1929FlagValues() {
        assertEquals(1 << 7, ImGuiItemFlags.LiveEditOnInputText.getValue());
        assertEquals(1 << 8, ImGuiItemFlags.LiveEditOnInputScalar.getValue());
        assertEquals((1 << 7) | (1 << 8), ImGuiItemFlags.LiveEditOnInput.getValue());
        assertEquals(1 << 27, ImGuiColorEditFlags.PickerNoRotate.getValue());
        assertEquals(1 << 28, ImGuiColorEditFlags.InputRGB.getValue());
        assertEquals(1 << 29, ImGuiColorEditFlags.InputHSV.getValue());
        assertEquals(1 << 4, ImDrawListFlags.TextNoPixelSnap.getValue());
        assertEquals(0, ImGuiListClipperFlags.None.getValue());
        assertEquals(1, ImGuiListClipperFlags.NoSetTableRowCounters.getValue());
    }

    @Test
    public void roundTripsNew1929ConfigurationFields() {
        ImGuiIO io = ImGui.GetIO();
        io.set_MouseSingleClickDelay(0.75f);
        io.set_ConfigIniSettingsSaveLastUsedDate(false);
        io.set_ConfigIniSettingsAutoDiscardMonths(9);
        io.set_ConfigDebugIsDebuggerPresent(true);
        io.set_ConfigDebugHighlightIdConflicts(false);
        assertEquals(0.75f, io.get_MouseSingleClickDelay(), 0.0f);
        assertFalse(io.get_ConfigIniSettingsSaveLastUsedDate());
        assertEquals(9, io.get_ConfigIniSettingsAutoDiscardMonths());
        assertTrue(io.get_ConfigDebugIsDebuggerPresent());
        assertFalse(io.get_ConfigDebugHighlightIdConflicts());

        ImGuiStyle style = ImGui.GetStyle();
        style.set_MenuItemRounding(2.0f);
        style.set_SelectableRounding(3.0f);
        style.set_InputTextCursorSize(4.0f);
        style.set_FontSizeBase(15.0f);
        style.set_TabMinWidthBase(12.0f);
        style.set_TreeLinesSize(2.0f);
        style.set_DockingNodeHasCloseButton(false);
        assertEquals(2.0f, style.get_MenuItemRounding(), 0.0f);
        assertEquals(3.0f, style.get_SelectableRounding(), 0.0f);
        assertEquals(4.0f, style.get_InputTextCursorSize(), 0.0f);
        assertEquals(15.0f, style.get_FontSizeBase(), 0.0f);
        assertEquals(12.0f, style.get_TabMinWidthBase(), 0.0f);
        assertEquals(2.0f, style.get_TreeLinesSize(), 0.0f);
        assertFalse(style.get_DockingNodeHasCloseButton());

        ImGuiPlatformIO platformIO = ImGui.GetPlatformIO();
        platformIO.set_Platform_SessionDate(20260726);
        platformIO.set_Platform_LocaleDecimalPoint((short) '.');
        platformIO.set_Renderer_TextureMaxWidth(8192);
        platformIO.set_Renderer_TextureMaxHeight(4096);
        assertEquals(20260726, platformIO.get_Platform_SessionDate());
        assertEquals((short) '.', platformIO.get_Platform_LocaleDecimalPoint());
        assertEquals(8192, platformIO.get_Renderer_TextureMaxWidth());
        assertEquals(4096, platformIO.get_Renderer_TextureMaxHeight());
    }

    @Test
    public void exposesSynchronizedPublicApiAndJavaSafeAdapters() throws Exception {
        ImGui.class.getMethod(
                "SetNextWindowSizeConstraints",
                ImVec2.class,
                ImVec2.class,
                ImGuiSizeCallbackFunction.class);
        ImGui.class.getMethod(
                "Combo",
                String.class,
                NativeIntArray.class,
                ImGuiStringList.class);
        ImGui.class.getMethod(
                "InputText",
                String.class,
                NativeByteArray.class,
                int.class,
                ImGuiInputTextFlags.class,
                ImGuiInputTextCallbackFunction.class);
        ImGui.class.getMethod(
                "ListBox",
                String.class,
                NativeIntArray.class,
                ImGuiStringList.class);
        ImGui.class.getMethod("PlotLines", String.class, ImGuiPlotGetter.class, int.class);
        ImGui.class.getMethod("SetAllocatorFunctions", long.class, long.class, long.class);
        ImGui.class.getMethod("DebugLog", String.class);
        ImDrawList.class.getMethod(
                "AddRect",
                ImVec2.class,
                ImVec2.class,
                int.class,
                float.class,
                float.class,
                ImDrawFlags.class);
        ImGuiDrawCallbacks.class.getMethod(
                "AddCallback",
                ImDrawList.class,
                ImDrawCallbackFunction.class);
        ImGuiTableSortSpecsHelpers.class.getMethod(
                "GetSpec",
                ImGuiTableSortSpecs.class,
                int.class);

        assertTrue(hasClass("imgui.ImDrawListSplitter"));
        assertTrue(hasClass("imgui.ImFontGlyphRangesBuilder"));
        assertTrue(hasClass("imgui.ImGuiListClipper"));
        assertTrue(hasClass("imgui.ImGuiSelectionBasicStorage"));
        assertTrue(hasClass("imgui.ImGuiTableColumnSortSpecs"));
        assertTrue(hasClass("imgui.ImGuiPlatformMonitor"));
        assertTrue(hasClass("imgui.ImGuiTextBuffer"));
    }

    @Test
    public void roundTripsOwnedStringListsAndSelectionIteration() {
        ImGuiStringList items = new ImGuiStringList();
        ImGuiSelectionBasicStorage selection = new ImGuiSelectionBasicStorage();
        ImGuiSelectionBasicStorageIterator iterator = new ImGuiSelectionBasicStorageIterator();
        try {
            assertTrue(items.Empty());
            items.Add("Alpha");
            items.Add("Beta");
            assertEquals(2, items.Size());
            items.Set(1, "Gamma");

            selection.SetItemSelected(17, true);
            selection.SetItemSelected(41, true);
            assertEquals(2, selection.get_Size());
            assertTrue(iterator.Next(selection));
            assertEquals(17, iterator.GetID());
            assertTrue(iterator.Next(selection));
            assertEquals(41, iterator.GetID());
            assertFalse(iterator.Next(selection));
        } finally {
            iterator.dispose();
            selection.dispose();
            items.dispose();
        }
    }

    @Test
    public void constructsAndDisposesPublicValueTypes() {
        NativeObject[] values = {
                new ImDrawData(),
                new ImDrawListSplitter(),
                new ImFontAtlasRect(),
                new ImFontGlyph(),
                new ImGuiTableColumnSortSpecs(),
                new ImGuiTableSortSpecs(),
                new ImTextureData()
        };
        for (int i = values.length - 1; i >= 0; i--) {
            values[i].dispose();
        }
    }

    @Test
    public void matchesChangedAndRemovedMethodSurface() throws Exception {
        assertEquals(boolean.class, ImGui.class.getMethod("OpenPopup", String.class).getReturnType());
        assertEquals(boolean.class, ImGui.class.getMethod("OpenPopupOnItemClick").getReturnType());
        assertEquals(boolean.class, ImGui.class.getMethod("ShowStyleSelector", String.class).getReturnType());
        assertEquals(boolean.class, ImGui.class.getMethod("TextLinkOpenURL", String.class).getReturnType());
        ImGui.class.getMethod("SetNavCursorVisible", boolean.class);
        ImGui.class.getMethod("GetItemClickedCountWithSingleClickDelay");
        ImDrawData.class.getMethod("get_FrameCount");
        ImDrawList.class.getMethod("PushTexture", ImTextureRef.class);
        ImDrawList.class.getMethod("PopTexture");

        assertFalse(hasPublicMethod(ImGui.class, "SetColorEditOptions"));
        assertFalse(hasPublicMethod(ImGui.class, "SetWindowFontScale"));
        assertNoPublicMethods(
                ImGui.class,
                "Columns",
                "GetColumnIndex",
                "GetColumnOffset",
                "GetColumnWidth",
                "GetColumnsCount",
                "IsAnyMouseDown",
                "NextColumn",
                "SetColumnOffset",
                "SetColumnWidth");
        assertFalse(hasPublicMethod(ImDrawData.class, "get_CmdListsCount"));
        assertFalse(hasPublicMethod(ImDrawList.class, "PushTextureID"));
        assertFalse(hasPublicMethod(ImDrawList.class, "PopTextureID"));
        assertFalse(hasPublicMethod(ImFontAtlas.class, "ClearInputData"));
        assertFalse(hasPublicMethod(ImFontAtlas.class, "ClearTexData"));
        assertFalse(hasPublicMethod(ImFontConfig.class, "get_PixelSnapV"));
        assertFalse(hasPublicMethod(ImFontConfig.class, "set_PixelSnapV"));
        assertFalse(hasPublicMethod(ImGuiStorage.class, "SetAllInt"));
        assertFalse(hasEnumConstant(ImGuiCol.class, "TabActive"));
        assertFalse(hasEnumConstant(ImGuiConfigFlags.class, "NavEnableSetMousePos"));
        assertFalse(hasEnumConstant(ImGuiMultiSelectFlags.class, "SelectOnClick"));
        assertFalse(hasEnumConstant(ImGuiSelectableFlags.class, "DontClosePopups"));
        assertFalse(hasEnumConstant(ImGuiTabBarFlags.class, "FittingPolicyResizeDown"));
        assertFalse(hasEnumConstant(ImGuiTreeNodeFlags.class, "NavLeftJumpsBackHere"));
        assertFalse(hasEnumConstant(ImGuiNavRenderCursorFlags.class, "NoRounding"));
        assertFalse(hasClass("imgui.ImColor"));
        assertFalse(hasClass("imgui.enums.ImGuiOldColumnFlags"));
    }

    private static void assertNoPublicMethods(Class<?> type, String... names) {
        for (String name : names) {
            assertFalse(type.getSimpleName() + "." + name + " must not be bound", hasPublicMethod(type, name));
        }
    }

    private static boolean hasPublicMethod(Class<?> type, String name) {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static <T extends Enum<T>> boolean hasEnumConstant(Class<T> type, String name) {
        try {
            Enum.valueOf(type, name);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private static boolean hasClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static native String getNativeVersionString();
}
