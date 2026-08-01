package imgui;

import imgui.enums.ImDrawListFlags;
import imgui.enums.ImGuiColorEditFlags;
import imgui.enums.ImGuiItemFlags;
import imgui.enums.ImGuiNavRenderCursorFlags;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
    }

    @Test
    public void roundTripsNew1929ConfigurationFields() {
        ImGuiIO io = ImGui.GetIO();
        io.set_MouseSingleClickDelay(0.75f);
        io.set_ConfigIniSettingsSaveLastUsedDate(false);
        io.set_ConfigIniSettingsAutoDiscardMonths(9);
        assertEquals(0.75f, io.get_MouseSingleClickDelay(), 0.0f);
        assertFalse(io.get_ConfigIniSettingsSaveLastUsedDate());
        assertEquals(9, io.get_ConfigIniSettingsAutoDiscardMonths());

        ImGuiStyle style = ImGui.GetStyle();
        style.set_MenuItemRounding(2.0f);
        style.set_SelectableRounding(3.0f);
        style.set_InputTextCursorSize(4.0f);
        assertEquals(2.0f, style.get_MenuItemRounding(), 0.0f);
        assertEquals(3.0f, style.get_SelectableRounding(), 0.0f);
        assertEquals(4.0f, style.get_InputTextCursorSize(), 0.0f);

        ImGuiPlatformIO platformIO = ImGui.GetPlatformIO();
        platformIO.set_Platform_SessionDate(20260726);
        assertEquals(20260726, platformIO.get_Platform_SessionDate());
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
        assertFalse(hasPublicMethod(ImDrawData.class, "get_CmdListsCount"));
        assertFalse(hasPublicMethod(ImDrawList.class, "PushTextureID"));
        assertFalse(hasPublicMethod(ImDrawList.class, "PopTextureID"));
        assertFalse(hasPublicMethod(ImFontConfig.class, "get_PixelSnapV"));
        assertFalse(hasPublicMethod(ImFontConfig.class, "set_PixelSnapV"));
        assertFalse(hasEnumConstant(ImGuiNavRenderCursorFlags.class, "NoRounding"));
        assertFalse(hasClass("imgui.enums.ImGuiOldColumnFlags"));
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
