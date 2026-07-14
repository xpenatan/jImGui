package io.github.libfdx.imgui;

public final class FdxImGuiRenderers {
    private FdxImGuiRenderers() {
    }

    public static FdxImGuiRenderer auto() {
        return new FdxImGuiGraphicsRenderer();
    }
}
