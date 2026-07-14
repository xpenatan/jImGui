package io.github.libfdx.imgui;

import imgui.ImDrawData;
import io.github.libfdx.core.Disposable;

public interface FdxImGuiRenderer extends Disposable {
    void initialize(FdxImGuiRendererContext context);

    void render(ImDrawData drawData);
}
