package imgui;

import com.github.xpenatan.jParser.api.NativeObject;

public final class ImDrawData extends NativeObject {

    public final static ImDrawData NULL = native_new();

    public static ImDrawData native_new() {
        return new ImDrawData((byte) 0, (char) 0);
    }

    private ImDrawData(byte b, char c) {
    }
}
