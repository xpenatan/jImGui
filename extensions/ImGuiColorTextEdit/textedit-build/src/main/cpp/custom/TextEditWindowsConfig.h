#pragma once

// ImGuiColorTextEdit uses imgui_internal.h and is built as a separate DLL.
// Resolve the implicit context through the ImGui DLL instead of referencing
// its GImGui data symbol directly.
#ifndef GImGui
#define GImGui ImGui::GetCurrentContext()
#endif
