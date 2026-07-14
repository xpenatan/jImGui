package io.github.libfdx.imgui;

import io.github.libfdx.Fdx;
import io.github.libfdx.display.Display;
import io.github.libfdx.graphics.GraphicsContext;
import io.github.libfdx.input.Input;

public final class FdxImGuiRendererContext {
    private final Fdx fdx;
    private final Display display;
    private final Input input;
    private final GraphicsContext graphics;
    private final FdxImGuiTextureRegistry textures;

    FdxImGuiRendererContext(Fdx fdx, Display display, Input input, GraphicsContext graphics,
            FdxImGuiTextureRegistry textures) {
        this.fdx = fdx;
        this.display = display;
        this.input = input;
        this.graphics = graphics;
        this.textures = textures;
    }

    public Fdx fdx() {
        return fdx;
    }

    public Display display() {
        return display;
    }

    public Input input() {
        return input;
    }

    public GraphicsContext graphics() {
        return graphics;
    }

    public FdxImGuiTextureRegistry textures() {
        return textures;
    }
}
