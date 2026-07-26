#pragma once

#include "imgui.h"
#include "imgui_internal.h"
#include "RuntimeHelper.h"

#if defined(_MSC_VER) && _MSC_VER <= 1500 // MSVC 2008 or earlier
#include <stddef.h>     // intptr_t
#else
#include <stdint.h>     // intptr_t
#endif
#include <string>
#include <cstdint>
#include <iostream>

using ImVectorImDrawCmd = ImVector<ImDrawCmd>;
using ImVectorImDrawIdx = ImVector<ImDrawIdx>;
using ImVectorImDrawVert = ImVector<ImDrawVert>;
using ImVectorUnsignedInt = ImVector<unsigned int>;
using ImVectorImDrawListPtr = ImVector<ImDrawList*>;
using ImVectorImGuiViewportPtr = ImVector<ImGuiViewport*>;
using ImVectorImGuiWindowPtr = ImVector<ImGuiWindow*>;
using ImVectorImGuiWindowStackData = ImVector<ImGuiWindowStackData>;
using ImVectorImTextureDataPtr = ImVector<ImTextureData*>;
using ImVectorImTextureRect = ImVector<ImTextureRect>;
using ImVectorImGuiStoragePair = ImVector<ImGuiStoragePair>;
using ImVectorImGuiID = ImVector<ImGuiID>;
using ImVectorDOMString = ImVector<const char*>;


namespace im = ImGui;

namespace ImGuiWrapper {

class ImGuiInternal {
// Emscripten webidl don't support binding methods without a class so we need to create a wrapper
public:

    static ImGuiID                  ImHashData_1(const void* data, size_t data_size, ImGuiID seed = 0) { return ImHashData(data, data_size, seed); }
    static ImGuiID                  ImHashStr_1(const char* data, size_t data_size = 0, ImGuiID seed = 0) { return ImHashStr(data, data_size, seed); }

    // Windows
    static ImGuiIO&         GetIO(ImGuiContext* ctx) { return im::GetIO(ctx); }
    static ImGuiPlatformIO& GetPlatformIO(ImGuiContext* ctx) { return im::GetPlatformIO(ctx); }
    static ImGuiWindow*  GetCurrentWindowRead() { return im::GetCurrentWindowRead(); }
    static ImGuiWindow*  GetCurrentWindow() { return im::GetCurrentWindow(); }
    static ImGuiWindow*  FindWindowByID(ImGuiID id) { return im::FindWindowByID(id); }
    static ImGuiWindow*  FindWindowByName(const char* name) { return im::FindWindowByName(name); }
    static void          UpdateWindowParentAndRootLinks(ImGuiWindow* window, ImGuiWindowFlags flags, ImGuiWindow* parent_window) { im::UpdateWindowParentAndRootLinks(window, flags, parent_window); }
    static void          UpdateWindowSkipRefresh(ImGuiWindow* window) { im::UpdateWindowSkipRefresh(window); }
    static ImVec2        CalcWindowNextAutoFitSize(ImGuiWindow* window) { return im::CalcWindowNextAutoFitSize(window); }
    static bool          IsWindowChildOf(ImGuiWindow* window, ImGuiWindow* potential_parent, bool popup_hierarchy, bool dock_hierarchy) { return im::IsWindowChildOf(window, potential_parent, popup_hierarchy, dock_hierarchy); }
    static bool          IsWindowWithinBeginStackOf(ImGuiWindow* window, ImGuiWindow* potential_parent) { return im::IsWindowWithinBeginStackOf(window, potential_parent); }
    static bool          IsWindowAbove(ImGuiWindow* potential_above, ImGuiWindow* potential_below) { return im::IsWindowAbove(potential_above, potential_below); }
    static bool          IsWindowNavFocusable(ImGuiWindow* window) { return im::IsWindowNavFocusable(window); }
    static void          SetWindowPos(ImGuiWindow* window, const ImVec2& pos, ImGuiCond cond = 0) { im::SetWindowPos(pos, cond); }
    static void          SetWindowSize(ImGuiWindow* window, const ImVec2& size, ImGuiCond cond = 0) { im::SetWindowSize(size, cond); }
    static void          SetWindowCollapsed(ImGuiWindow* window, bool collapsed, ImGuiCond cond = 0) { im::SetWindowCollapsed(window, collapsed, cond); }
    static void          SetWindowHitTestHole(ImGuiWindow* window, const ImVec2& pos, const ImVec2& size) { im::SetWindowHitTestHole(window, pos, size); }
    static void          SetWindowHiddenAndSkipItemsForCurrentFrame(ImGuiWindow* window) { im::SetWindowHiddenAndSkipItemsForCurrentFrame(window); }
    static void          SetWindowParentWindowForFocusRoute(ImGuiWindow* window, ImGuiWindow* parent_window) { im::SetWindowParentWindowForFocusRoute(window, parent_window); }
    static ImRect        WindowRectAbsToRel(ImGuiWindow* window, const ImRect& r) { return im::WindowRectAbsToRel(window, r); }
    static ImRect        WindowRectRelToAbs(ImGuiWindow* window, const ImRect& r) { return im::WindowRectRelToAbs(window, r); }
    static ImVec2        WindowPosAbsToRel(ImGuiWindow* window, const ImVec2& p) { return im::WindowPosAbsToRel(window, p); }
    static ImVec2        WindowPosRelToAbs(ImGuiWindow* window, const ImVec2& p) { return im::WindowPosRelToAbs(window, p); }

    // Windows: Display Order and Focus Order
    static void          FocusWindow(ImGuiWindow* window, ImGuiFocusRequestFlags flags = 0) { im::FocusWindow(window, flags); }
    static void          FocusTopMostWindowUnderOne(ImGuiWindow* under_this_window, ImGuiWindow* ignore_window, ImGuiViewport* filter_viewport, ImGuiFocusRequestFlags flags) { im::FocusTopMostWindowUnderOne(under_this_window, ignore_window, filter_viewport, flags); }
    static void          BringWindowToFocusFront(ImGuiWindow* window) { im::BringWindowToFocusFront(window); }
    static void          BringWindowToDisplayFront(ImGuiWindow* window) { im::BringWindowToDisplayFront(window); }
    static void          BringWindowToDisplayBack(ImGuiWindow* window) { im::BringWindowToDisplayBack(window); }
    static void          BringWindowToDisplayBehind(ImGuiWindow* window, ImGuiWindow* above_window) { im::BringWindowToDisplayBehind(window, above_window); }
    static int           FindWindowDisplayIndex(ImGuiWindow* window) { return im::FindWindowDisplayIndex(window); }
    static ImGuiWindow*  FindBottomMostVisibleWindowWithinBeginStack(ImGuiWindow* window) { return im::FindBottomMostVisibleWindowWithinBeginStack(window); }

    // Windows: Idle, Refresh Policies [EXPERIMENTAL]
    static void          SetNextWindowRefreshPolicy(ImGuiWindowRefreshFlags flags) { im::SetNextWindowRefreshPolicy(flags); }

    // Fonts, drawing
    static void          RegisterUserTexture(ImTextureData* tex) { im::RegisterUserTexture(tex); }
    static void          UnregisterUserTexture(ImTextureData* tex) { im::UnregisterUserTexture(tex); }
    static void          RegisterFontAtlas(ImFontAtlas* atlas) { im::RegisterFontAtlas(atlas); }
    static void          UnregisterFontAtlas(ImFontAtlas* atlas) { im::UnregisterFontAtlas(atlas); }
    static void          SetCurrentFont(ImFont* font, float font_size_before_scaling, float font_size_after_scaling) { im::SetCurrentFont(font, font_size_before_scaling, font_size_after_scaling); }
    static void          UpdateCurrentFontSize(float restore_font_size_after_scaling) { im::UpdateCurrentFontSize(restore_font_size_after_scaling); }
    static void          SetFontRasterizerDensity(float rasterizer_density) { im::SetFontRasterizerDensity(rasterizer_density); }
    static float            GetFontRasterizerDensity() { return im::GetFontRasterizerDensity(); }
    static float            GetRoundedFontSize(float size) { return im::GetRoundedFontSize(size); }
    static ImFont*       GetDefaultFont() { return im::GetDefaultFont(); }
    static void          PushPasswordFont() { im::PushPasswordFont(); }
    static void          PopPasswordFont() { im::PopPasswordFont(); }
    static ImDrawList*      GetForegroundDrawList(ImGuiWindow* window) { return im::GetForegroundDrawList(window); }
    static void          AddDrawListToDrawDataEx(ImDrawData* draw_data, ImVector<ImDrawList*>* out_list, ImDrawList* draw_list) { im::AddDrawListToDrawDataEx(draw_data, out_list, draw_list); }

    // Init
    static void          Initialize() { im::Initialize(); }
    static void          Shutdown() { im::Shutdown(); }

    // NewFrame
    static void          UpdateInputEvents(bool trickle_fast_inputs) { im::UpdateInputEvents(trickle_fast_inputs); }
    static void          UpdateHoveredWindowAndCaptureFlags(const ImVec2& mouse_pos) { im::UpdateHoveredWindowAndCaptureFlags(mouse_pos); }
    static void          FindHoveredWindowEx(const ImVec2& pos, bool find_first_and_in_any_viewport, ImGuiWindow** out_hovered_window, ImGuiWindow** out_hovered_window_under_moving_window) { im::FindHoveredWindowEx(pos, find_first_and_in_any_viewport, out_hovered_window, out_hovered_window_under_moving_window); }
    static void          StartMouseMovingWindow(ImGuiWindow* window) { im::StartMouseMovingWindow(window); }
    static void          StartMouseMovingWindowOrNode(ImGuiWindow* window, ImGuiDockNode* node, bool undock) { im::StartMouseMovingWindowOrNode(window, node, undock); }
    static void          StopMouseMovingWindow() { im::StopMouseMovingWindow(); }
    static void          UpdateMouseMovingWindowNewFrame() { im::UpdateMouseMovingWindowNewFrame(); }
    static void          UpdateMouseMovingWindowEndFrame() { im::UpdateMouseMovingWindowEndFrame(); }

//    // Generic context hooks
//    ImGuiID       AddContextHook(ImGuiContext* context, const ImGuiContextHook* hook);
//    void          RemoveContextHook(ImGuiContext* context, ImGuiID hook_to_remove);
//    void          CallContextHooks(ImGuiContext* context, ImGuiContextHookType type);
//
//    // Viewports
//    void          TranslateWindowsInViewport(ImGuiViewportP* viewport, const ImVec2& old_pos, const ImVec2& new_pos, const ImVec2& old_size, const ImVec2& new_size);
//    void          ScaleWindowsInViewport(ImGuiViewportP* viewport, float scale);
//    void          DestroyPlatformWindow(ImGuiViewportP* viewport);
//    void          SetWindowViewport(ImGuiWindow* window, ImGuiViewportP* viewport);
//    void          SetCurrentViewport(ImGuiWindow* window, ImGuiViewportP* viewport);
//    const ImGuiPlatformMonitor*   GetViewportPlatformMonitor(ImGuiViewport* viewport);
//    ImGuiViewportP*               FindHoveredViewportFromPlatformWindowStack(const ImVec2& mouse_platform_pos);
//
//    // Settings
//    void                  MarkIniSettingsDirty();
//    void                  MarkIniSettingsDirty(ImGuiWindow* window);
//    void                  ClearIniSettings();
//    void                  AddSettingsHandler(const ImGuiSettingsHandler* handler);
//    void                  RemoveSettingsHandler(const char* type_name);
//    ImGuiSettingsHandler* FindSettingsHandler(const char* type_name);
//
//    // Settings - Windows
//    ImGuiWindowSettings*  CreateNewWindowSettings(const char* name);
//    ImGuiWindowSettings*  FindWindowSettingsByID(ImGuiID id);
//    ImGuiWindowSettings*  FindWindowSettingsByWindow(ImGuiWindow* window);
//    void                  ClearWindowSettings(const char* name);
//
//    // Localization
//    void          LocalizeRegisterEntries(const ImGuiLocEntry* entries, int count);
//    const char*      LocalizeGetMsg(ImGuiLocKey key) { ImGuiContext& g = *GImGui; const char* msg = g.LocalizationTable[key]; return msg ? msg : "*Missing Text*"; }
//
//    // Scrolling
//    void          SetScrollX(ImGuiWindow* window, float scroll_x);
//    void          SetScrollY(ImGuiWindow* window, float scroll_y);
//    void          SetScrollFromPosX(ImGuiWindow* window, float local_x, float center_x_ratio);
//    void          SetScrollFromPosY(ImGuiWindow* window, float local_y, float center_y_ratio);
//
//    // Early work-in-progress API (ScrollToItem() will become public)
//    void          ScrollToItem(ImGuiScrollFlags flags = 0);
//    void          ScrollToRect(ImGuiWindow* window, const ImRect& rect, ImGuiScrollFlags flags = 0);
//    ImVec2        ScrollToRectEx(ImGuiWindow* window, const ImRect& rect, ImGuiScrollFlags flags = 0);

    // Basic Accessors
    static ImGuiItemStatusFlags GetItemStatusFlags() { return im::GetItemStatusFlags(); }
    static ImGuiID          GetActiveID() { return im::GetActiveID(); }
    static ImGuiID          GetFocusID() { return im::GetFocusID(); }
    static void          SetActiveID(ImGuiID id, ImGuiWindow* window) { im::SetActiveID(id, window); }
    static void          SetFocusID(ImGuiID id, ImGuiWindow* window) { im::SetFocusID(id, window); }
    static void          ClearActiveID() { im::ClearActiveID(); }
    static ImGuiID       GetHoveredID() { return im::GetHoveredID(); }
    static void          SetHoveredID(ImGuiID id) { im::SetHoveredID(id); }
    static void          KeepAliveID(ImGuiID id) { im::KeepAliveID(id); }
    static void          MarkItemEdited(ImGuiID id) { im::MarkItemEdited(id); }
    static void          PushOverrideID(ImGuiID id) { im::PushOverrideID(id); }
    static ImGuiID       GetIDWithSeed(const char* str_id_begin, const char* str_id_end, ImGuiID seed) { return im::GetIDWithSeed(str_id_begin, str_id_end, seed); }
    static ImGuiID       GetIDWithSeed(int n, ImGuiID seed) { return im::GetIDWithSeed(n, seed); }

    // Basic Helpers for widget code
    static void          ItemSize(const ImVec2& size, float text_baseline_y = -1.0f) { im::ItemSize(size, text_baseline_y); }
    static void          ItemSize(const ImRect& bb, float text_baseline_y = -1.0f) { im::ItemSize(bb, text_baseline_y); }
    static bool          ItemAdd(const ImRect& bb, ImGuiID id, const ImRect* nav_bb = NULL, ImGuiItemFlags extra_flags = 0) { return im::ItemAdd(bb, id, nav_bb, extra_flags); }
    static bool          ItemHoverable(const ImRect& bb, ImGuiID id, ImGuiItemFlags item_flags) { return im::ItemHoverable(bb, id, item_flags); }
    static bool          IsWindowContentHoverable(ImGuiWindow* window, ImGuiHoveredFlags flags = 0) { return im::IsWindowContentHoverable(window, flags); }
    static bool          IsClippedEx(const ImRect& bb, ImGuiID id) { return im::IsClippedEx(bb, id); }
    static void          SetLastItemData(ImGuiID item_id, ImGuiItemFlags item_flags, ImGuiItemStatusFlags status_flags, const ImRect& item_rect) { im::SetLastItemData(item_id, item_flags, status_flags, item_rect); }
    static ImVec2        CalcItemSize(ImVec2 size, float default_w, float default_h) { return im::CalcItemSize(size, default_w, default_h); }
    static float         CalcWrapWidthForPos(const ImVec2& pos, float wrap_pos_x) { return im::CalcWrapWidthForPos(pos, wrap_pos_x); }
    static void          PushMultiItemsWidths(int components, float width_full) { return im::PushMultiItemsWidths(components, width_full); }
    static void          ShrinkWidths(ImGuiShrinkWidthItem* items, int count, float width_excess, float width_min) { im::ShrinkWidths(items, count, width_excess, width_min); }
    static void          CalcClipRectVisibleItemsY(const ImRect& clip_rect, const ImVec2& pos, float items_height, int* out_visible_start, int* out_visible_end) { im::CalcClipRectVisibleItemsY(clip_rect, pos, items_height, out_visible_start, out_visible_end); }

    // Parameter stacks (shared)
//    const ImGuiStyleVarInfo* GetStyleVarInfo(ImGuiStyleVar idx);
//    void          BeginDisabledOverrideReenable();
//    void          EndDisabledOverrideReenable();
//
//    // Logging/Capture
//    void          LogBegin(ImGuiLogFlags flags, int auto_open_depth);         // -> BeginCapture() when we design v2 api, for now stay under the radar by using the old name.
//    void          LogToBuffer(int auto_open_depth = -1);                      // Start logging/capturing to internal buffer
//    void          LogRenderedText(const ImVec2* ref_pos, const char* text, const char* text_end = NULL);
//    void          LogSetNextTextDecoration(const char* prefix, const char* suffix);
//
//    // Childs
//    bool          BeginChildEx(const char* name, ImGuiID id, const ImVec2& size_arg, ImGuiChildFlags child_flags, ImGuiWindowFlags window_flags);
//
//    // Popups, Modals
//    bool          BeginPopupEx(ImGuiID id, ImGuiWindowFlags extra_window_flags);
//    bool          BeginPopupMenuEx(ImGuiID id, const char* label, ImGuiWindowFlags extra_window_flags);
//    void          OpenPopupEx(ImGuiID id, ImGuiPopupFlags popup_flags = ImGuiPopupFlags_None);
//    void          ClosePopupToLevel(int remaining, bool restore_focus_to_window_under_popup);
//    void          ClosePopupsOverWindow(ImGuiWindow* ref_window, bool restore_focus_to_window_under_popup);
//    void          ClosePopupsExceptModals();
//    bool          IsPopupOpen(ImGuiID id, ImGuiPopupFlags popup_flags);
//    ImRect        GetPopupAllowedExtentRect(ImGuiWindow* window);
//    ImGuiWindow*  GetTopMostPopupModal();
//    ImGuiWindow*  GetTopMostAndVisiblePopupModal();
//    ImGuiWindow*  FindBlockingModal(ImGuiWindow* window);
//    ImVec2        FindBestWindowPosForPopup(ImGuiWindow* window);
//    ImVec2        FindBestWindowPosForPopupEx(const ImVec2& ref_pos, const ImVec2& size, ImGuiDir* last_dir, const ImRect& r_outer, const ImRect& r_avoid, ImGuiPopupPositionPolicy policy);
//
//    // Tooltips
//    bool          BeginTooltipEx(ImGuiTooltipFlags tooltip_flags, ImGuiWindowFlags extra_window_flags);
//    bool          BeginTooltipHidden();
//
//    // Menus
//    bool          BeginViewportSideBar(const char* name, ImGuiViewport* viewport, ImGuiDir dir, float size, ImGuiWindowFlags window_flags);
//    bool          BeginMenuEx(const char* label, const char* icon, bool enabled = true);
//    bool          MenuItemEx(const char* label, const char* icon, const char* shortcut = NULL, bool selected = false, bool enabled = true);
//
//    // Combos
//    bool          BeginComboPopup(ImGuiID popup_id, const ImRect& bb, ImGuiComboFlags flags);
//    bool          BeginComboPreview();
//    void          EndComboPreview();
//
//    // Keyboard/Gamepad Navigation
//    void          NavInitWindow(ImGuiWindow* window, bool force_reinit);
//    void          NavInitRequestApplyResult();
//    bool          NavMoveRequestButNoResultYet();
//    void          NavMoveRequestSubmit(ImGuiDir move_dir, ImGuiDir clip_dir, ImGuiNavMoveFlags move_flags, ImGuiScrollFlags scroll_flags);
//    void          NavMoveRequestForward(ImGuiDir move_dir, ImGuiDir clip_dir, ImGuiNavMoveFlags move_flags, ImGuiScrollFlags scroll_flags);
//    void          NavMoveRequestResolveWithLastItem(ImGuiNavItemData* result);
//    void          NavMoveRequestResolveWithPastTreeNode(ImGuiNavItemData* result, const ImGuiTreeNodeStackData* tree_node_data);
//    void          NavMoveRequestCancel();
//    void          NavMoveRequestApplyResult();
//    void          NavMoveRequestTryWrapping(ImGuiWindow* window, ImGuiNavMoveFlags move_flags);
//    void          NavHighlightActivated(ImGuiID id);
//    void          NavClearPreferredPosForAxis(ImGuiAxis axis);
//    void          SetNavCursorVisibleAfterMove();
//    void          NavUpdateCurrentWindowIsScrollPushableX();
//    void          SetNavWindow(ImGuiWindow* window);
//    void          SetNavID(ImGuiID id, ImGuiNavLayer nav_layer, ImGuiID focus_scope_id, const ImRect& rect_rel);
//    void          SetNavFocusScope(ImGuiID focus_scope_id);
//
//    // Focus/Activation
//    void          FocusItem();                    // Focus last item (no selection/activation).
//    void          ActivateItemByID(ImGuiID id);   // Activate an item by ID (button, checkbox, tree node etc.). Activation is queued and processed on the next frame when the item is encountered again. Was called 'ActivateItem()' before 1.89.7.
//
//    // Inputs
//    bool             IsNamedKey(ImGuiKey key)                    { return key >= ImGuiKey_NamedKey_BEGIN && key < ImGuiKey_NamedKey_END; }
//    bool             IsNamedKeyOrMod(ImGuiKey key)               { return (key >= ImGuiKey_NamedKey_BEGIN && key < ImGuiKey_NamedKey_END) || key == ImGuiMod_Ctrl || key == ImGuiMod_Shift || key == ImGuiMod_Alt || key == ImGuiMod_Super; }
//    bool             IsLegacyKey(ImGuiKey key)                   { return key >= ImGuiKey_LegacyNativeKey_BEGIN && key < ImGuiKey_LegacyNativeKey_END; }
//    bool             IsKeyboardKey(ImGuiKey key)                 { return key >= ImGuiKey_Keyboard_BEGIN && key < ImGuiKey_Keyboard_END; }
//    bool             IsGamepadKey(ImGuiKey key)                  { return key >= ImGuiKey_Gamepad_BEGIN && key < ImGuiKey_Gamepad_END; }
//    bool             IsMouseKey(ImGuiKey key)                    { return key >= ImGuiKey_Mouse_BEGIN && key < ImGuiKey_Mouse_END; }
//    bool             IsAliasKey(ImGuiKey key)                    { return key >= ImGuiKey_Aliases_BEGIN && key < ImGuiKey_Aliases_END; }
//    bool             IsLRModKey(ImGuiKey key)                    { return key >= ImGuiKey_LeftCtrl && key <= ImGuiKey_RightSuper; }
//    ImGuiKeyChord           FixupKeyChord(ImGuiKeyChord key_chord);
//    ImGuiKey         ConvertSingleModFlagToKey(ImGuiKey key);
//
//    ImGuiKeyData* GetKeyData(ImGuiContext* ctx, ImGuiKey key);
//    ImGuiKeyData*    GetKeyData(ImGuiKey key)                                    { ImGuiContext& g = *GImGui; return GetKeyData(&g, key); }
//    const char*   GetKeyChordName(ImGuiKeyChord key_chord);
//    ImGuiKey         MouseButtonToKey(ImGuiMouseButton button)                   { IM_ASSERT(button >= 0 && button < ImGuiMouseButton_COUNT); return (ImGuiKey)(ImGuiKey_MouseLeft + button); }
//    bool          IsMouseDragPastThreshold(ImGuiMouseButton button, float lock_threshold = -1.0f);
//    ImVec2        GetKeyMagnitude2d(ImGuiKey key_left, ImGuiKey key_right, ImGuiKey key_up, ImGuiKey key_down);
//    float         GetNavTweakPressedAmount(ImGuiAxis axis);
//    int           CalcTypematicRepeatAmount(float t0, float t1, float repeat_delay, float repeat_rate);
//    void          GetTypematicRepeatRate(ImGuiInputFlags flags, float* repeat_delay, float* repeat_rate);
//    void          TeleportMousePos(const ImVec2& pos);
//    void          SetActiveIdUsingAllKeyboardKeys();
//    bool             IsActiveIdUsingNavDir(ImGuiDir dir)                         { ImGuiContext& g = *GImGui; return (g.ActiveIdUsingNavDirMask & (1 << dir)) != 0; }
//
//    // [EXPERIMENTAL] Low-Level: Key/Input Ownership
//    ImGuiID       GetKeyOwner(ImGuiKey key);
//    void          SetKeyOwner(ImGuiKey key, ImGuiID owner_id, ImGuiInputFlags flags = 0);
//    void          SetKeyOwnersForKeyChord(ImGuiKeyChord key, ImGuiID owner_id, ImGuiInputFlags flags = 0);
//    void          SetItemKeyOwner(ImGuiKey key, ImGuiInputFlags flags);       // Set key owner to last item if it is hovered or active. Equivalent to 'if (IsItemHovered() || IsItemActive()) { SetKeyOwner(key, GetItemID());'.
//    bool          TestKeyOwner(ImGuiKey key, ImGuiID owner_id);               // Test that key is either not owned, either owned by 'owner_id'
//    ImGuiKeyOwnerData* GetKeyOwnerData(ImGuiContext* ctx, ImGuiKey key)          { if (key & ImGuiMod_Mask_) key = ConvertSingleModFlagToKey(key); IM_ASSERT(IsNamedKey(key)); return &ctx->KeysOwnerData[key - ImGuiKey_NamedKey_BEGIN]; }
//
//    // [EXPERIMENTAL] High-Level: Input Access functions w/ support for Key/Input Ownership
//    bool          IsKeyDown(ImGuiKey key, ImGuiID owner_id);
//    bool          IsKeyPressed(ImGuiKey key, ImGuiInputFlags flags, ImGuiID owner_id = 0);    // Important: when transitioning from old to new IsKeyPressed(): old API has "bool repeat = true", so would default to repeat. New API requiress explicit ImGuiInputFlags_Repeat.
//    bool          IsKeyReleased(ImGuiKey key, ImGuiID owner_id);
//    bool          IsKeyChordPressed(ImGuiKeyChord key_chord, ImGuiInputFlags flags, ImGuiID owner_id = 0);
//    bool          IsMouseDown(ImGuiMouseButton button, ImGuiID owner_id);
//    bool          IsMouseClicked(ImGuiMouseButton button, ImGuiInputFlags flags, ImGuiID owner_id = 0);
//    bool          IsMouseReleased(ImGuiMouseButton button, ImGuiID owner_id);
//    bool          IsMouseDoubleClicked(ImGuiMouseButton button, ImGuiID owner_id);
//
//    // Shortcut Testing & Routing
//    bool          Shortcut(ImGuiKeyChord key_chord, ImGuiInputFlags flags, ImGuiID owner_id);
//    bool          SetShortcutRouting(ImGuiKeyChord key_chord, ImGuiInputFlags flags, ImGuiID owner_id); // owner_id needs to be explicit and cannot be 0
//    bool          TestShortcutRouting(ImGuiKeyChord key_chord, ImGuiID owner_id);
//    ImGuiKeyRoutingData* GetShortcutRoutingData(ImGuiKeyChord key_chord);
//
//    // Docking
//    void          DockContextInitialize(ImGuiContext* ctx);
//    void          DockContextShutdown(ImGuiContext* ctx);
//    void          DockContextClearNodes(ImGuiContext* ctx, ImGuiID root_id, bool clear_settings_refs); // Use root_id==0 to clear all
//    void          DockContextRebuildNodes(ImGuiContext* ctx);
//    void          DockContextNewFrameUpdateUndocking(ImGuiContext* ctx);
//    void          DockContextNewFrameUpdateDocking(ImGuiContext* ctx);
//    void          DockContextEndFrame(ImGuiContext* ctx);
//    ImGuiID       DockContextGenNodeID(ImGuiContext* ctx);
//    void          DockContextQueueDock(ImGuiContext* ctx, ImGuiWindow* target, ImGuiDockNode* target_node, ImGuiWindow* payload, ImGuiDir split_dir, float split_ratio, bool split_outer);
//    void          DockContextQueueUndockWindow(ImGuiContext* ctx, ImGuiWindow* window);
//    void          DockContextQueueUndockNode(ImGuiContext* ctx, ImGuiDockNode* node);
//    void          DockContextProcessUndockWindow(ImGuiContext* ctx, ImGuiWindow* window, bool clear_persistent_docking_ref = true);
//    void          DockContextProcessUndockNode(ImGuiContext* ctx, ImGuiDockNode* node);
//    bool          DockContextCalcDropPosForDocking(ImGuiWindow* target, ImGuiDockNode* target_node, ImGuiWindow* payload_window, ImGuiDockNode* payload_node, ImGuiDir split_dir, bool split_outer, ImVec2* out_pos);
//    ImGuiDockNode*DockContextFindNodeByID(ImGuiContext* ctx, ImGuiID id);
//    void          DockNodeWindowMenuHandler_Default(ImGuiContext* ctx, ImGuiDockNode* node, ImGuiTabBar* tab_bar);
//    bool          DockNodeBeginAmendTabBar(ImGuiDockNode* node);
//    void          DockNodeEndAmendTabBar();
//    ImGuiDockNode*   DockNodeGetRootNode(ImGuiDockNode* node)                 { while (node->ParentNode) node = node->ParentNode; return node; }
//    bool             DockNodeIsInHierarchyOf(ImGuiDockNode* node, ImGuiDockNode* parent) { while (node) { if (node == parent) return true; node = node->ParentNode; } return false; }
//    int              DockNodeGetDepth(const ImGuiDockNode* node)              { int depth = 0; while (node->ParentNode) { node = node->ParentNode; depth++; } return depth; }
//    ImGuiID          DockNodeGetWindowMenuButtonId(const ImGuiDockNode* node) { return ImHashStr("#COLLAPSE", 0, node->ID); }
//    ImGuiDockNode*   GetWindowDockNode()                                      { ImGuiContext& g = *GImGui; return g.CurrentWindow->DockNode; }
//    bool          GetWindowAlwaysWantOwnTabBar(ImGuiWindow* window);
//    void          BeginDocked(ImGuiWindow* window, bool* p_open);
//    void          BeginDockableDragDropSource(ImGuiWindow* window);
//    void          BeginDockableDragDropTarget(ImGuiWindow* window);
//    void          SetWindowDock(ImGuiWindow* window, ImGuiID dock_id, ImGuiCond cond);

    // Docking - Builder function needs to be generally called before the node is used/submitted.
    static void          DockBuilderDockWindow(const char* window_name, ImGuiID node_id) { im::DockBuilderDockWindow(window_name, node_id); }
    static ImGuiDockNode*   DockBuilderGetNode(ImGuiID node_id) { return im::DockBuilderGetNode(node_id); }
    static ImGuiDockNode*   DockBuilderGetCentralNode(ImGuiID node_id) { return im::DockBuilderGetCentralNode(node_id); }
    static ImGuiID       DockBuilderAddNode(ImGuiID node_id = 0, ImGuiDockNodeFlags flags = 0) { return im::DockBuilderAddNode(node_id, flags); }
    static void          DockBuilderRemoveNode(ImGuiID node_id) { im::DockBuilderRemoveNode(node_id); }
    static void          DockBuilderRemoveNodeDockedWindows(ImGuiID node_id, bool clear_settings_refs = true) { im::DockBuilderRemoveNodeDockedWindows(node_id, clear_settings_refs); }
    static void          DockBuilderRemoveNodeChildNodes(ImGuiID node_id) { im::DockBuilderRemoveNodeChildNodes(node_id); }
    static void          DockBuilderSetNodePos(ImGuiID node_id, ImVec2 pos) { im::DockBuilderSetNodePos(node_id, pos); }
    static void          DockBuilderSetNodeSize(ImGuiID node_id, ImVec2 size) { im::DockBuilderSetNodeSize(node_id, size); }
    static ImGuiID       DockBuilderSplitNode(ImGuiID node_id, ImGuiDir split_dir, float size_ratio_for_node_at_dir, int* out_id_at_dir, int* out_id_at_opposite_dir) { return im::DockBuilderSplitNode(node_id, split_dir, size_ratio_for_node_at_dir, (ImGuiID*)out_id_at_dir, (ImGuiID*)out_id_at_opposite_dir); }
    static void          DockBuilderCopyDockSpace(ImGuiID src_dockspace_id, ImGuiID dst_dockspace_id, ImVector<const char*>* in_window_remap_pairs) { im::DockBuilderCopyDockSpace(src_dockspace_id, dst_dockspace_id, in_window_remap_pairs); }
    static void          DockBuilderCopyNode(ImGuiID src_node_id, ImGuiID dst_node_id, ImVector<ImGuiID>* out_node_remap_pairs) { im::DockBuilderCopyNode(src_node_id, dst_node_id, out_node_remap_pairs); }
    static void          DockBuilderCopyWindowSettings(const char* src_name, const char* dst_name) { im::DockBuilderCopyWindowSettings(src_name, dst_name); }
    static void          DockBuilderFinish(ImGuiID node_id) { im::DockBuilderFinish(node_id); }
//
//    // [EXPERIMENTAL] Focus Scope
//    void          PushFocusScope(ImGuiID id);
//    void          PopFocusScope();
//    ImGuiID          GetCurrentFocusScope() { ImGuiContext& g = *GImGui; return g.CurrentFocusScopeId; }   // Focus scope we are outputting into, set by PushFocusScope()

    // Drag and Drop
    static bool          IsDragDropActive() { return im::IsDragDropActive(); }
    static bool          BeginDragDropTargetCustom(const ImRect& bb, ImGuiID id) { return im::BeginDragDropTargetCustom(bb, id); }
    static bool          BeginDragDropTargetViewport(ImGuiViewport* viewport, const ImRect* p_bb = NULL) { return im::BeginDragDropTargetViewport(viewport, p_bb); }
    static void          ClearDragDrop() { im::ClearDragDrop(); }
    static bool          IsDragDropPayloadBeingAccepted() { return im::IsDragDropPayloadBeingAccepted(); }
    static void          RenderDragDropTargetRectForItem(const ImRect& bb) { return im::RenderDragDropTargetRectForItem(bb); }
    static void          RenderDragDropTargetRectEx(ImDrawList* draw_list, const ImRect& bb, float rounding) { return im::RenderDragDropTargetRectEx(draw_list, bb, rounding); }
//
//    // Typing-Select API
//    ImGuiTypingSelectRequest* GetTypingSelectRequest(ImGuiTypingSelectFlags flags = ImGuiTypingSelectFlags_None);
//    int           TypingSelectFindMatch(ImGuiTypingSelectRequest* req, int items_count, const char* (*get_item_name_func)(void*, int), void* user_data, int nav_item_idx);
//    int           TypingSelectFindNextSingleCharMatch(ImGuiTypingSelectRequest* req, int items_count, const char* (*get_item_name_func)(void*, int), void* user_data, int nav_item_idx);
//    int           TypingSelectFindBestLeadingMatch(ImGuiTypingSelectRequest* req, int items_count, const char* (*get_item_name_func)(void*, int), void* user_data);
//
//    // Box-Select API
//    bool          BeginBoxSelect(const ImRect& scope_rect, ImGuiWindow* window, ImGuiID box_select_id, ImGuiMultiSelectFlags ms_flags);
//    void          EndBoxSelect(const ImRect& scope_rect, ImGuiMultiSelectFlags ms_flags);
//
//    // Multi-Select API
//    void          MultiSelectItemHeader(ImGuiID id, bool* p_selected, ImGuiButtonFlags* p_button_flags);
//    void          MultiSelectItemFooter(ImGuiID id, bool* p_selected, bool* p_pressed);
//    void          MultiSelectAddSetAll(ImGuiMultiSelectTempData* ms, bool selected);
//    void          MultiSelectAddSetRange(ImGuiMultiSelectTempData* ms, bool selected, int range_dir, ImGuiSelectionUserData first_item, ImGuiSelectionUserData last_item);
//    ImGuiBoxSelectState*     GetBoxSelectState(ImGuiID id)   { ImGuiContext& g = *GImGui; return (id != 0 && g.BoxSelectState.ID == id && g.BoxSelectState.IsActive) ? &g.BoxSelectState : NULL; }
//    ImGuiMultiSelectState*   GetMultiSelectState(ImGuiID id) { ImGuiContext& g = *GImGui; return g.MultiSelectStorage.GetByKey(id); }
//
//    // Internal Columns API (this is not exposed because we will encourage transitioning to the Tables API)
//    void          SetWindowClipRectBeforeSetChannel(ImGuiWindow* window, const ImRect& clip_rect);
//    void          BeginColumns(const char* str_id, int count, ImGuiOldColumnFlags flags = 0); // setup number of columns. use an identifier to distinguish multiple column sets. close with EndColumns().
//    void          EndColumns();                                                               // close columns
//    void          PushColumnClipRect(int column_index);
//    void          PushColumnsBackground();
//    void          PopColumnsBackground();
//    ImGuiID       GetColumnsID(const char* str_id, int count);
//    ImGuiOldColumns* FindOrCreateColumns(ImGuiWindow* window, ImGuiID id);
//    float         GetColumnOffsetFromNorm(const ImGuiOldColumns* columns, float offset_norm);
//    float         GetColumnNormFromOffset(const ImGuiOldColumns* columns, float offset);
//
//    // Tables: Candidates for public API
//    void          TableOpenContextMenu(int column_n = -1);
//    void          TableSetColumnWidth(int column_n, float width);
//    void          TableSetColumnSortDirection(int column_n, ImGuiSortDirection sort_direction, bool append_to_sort_specs);
//    int           TableGetHoveredRow();       // Retrieve *PREVIOUS FRAME* hovered row. This difference with TableGetHoveredColumn() is the reason why this is not public yet.
//    float         TableGetHeaderRowHeight();
//    float         TableGetHeaderAngledMaxLabelWidth();
//    void          TablePushBackgroundChannel();
//    void          TablePopBackgroundChannel();
//    void          TablePushColumnChannel(int column_n);
//    void          TablePopColumnChannel();
//    void          TableAngledHeadersRowEx(ImGuiID row_id, float angle, float max_label_width, const ImGuiTableHeaderData* data, int data_count);
//
    // Tables: Internals
//    static ImGuiTable*   GetCurrentTable() { ImGuiContext& g = *GImGui; return g.CurrentTable; }
//    static ImGuiTable*   TableFindByID(ImGuiID id);
    static bool          BeginTableEx(const char* name, ImGuiID id, int columns_count, ImGuiTableFlags flags = 0, const ImVec2& outer_size = ImVec2(0, 0), float inner_width = 0.0f) { return im::BeginTableEx(name, id, columns_count, flags, outer_size, inner_width); }
//    static void          TableBeginInitMemory(ImGuiTable* table, int columns_count);
//    static void          TableBeginApplyRequests(ImGuiTable* table);
//    static void          TableSetupDrawChannels(ImGuiTable* table);
//    static void          TableUpdateLayout(ImGuiTable* table);
//    static void          TableUpdateBorders(ImGuiTable* table);
//    static void          TableUpdateColumnsWeightFromWidth(ImGuiTable* table);
//    static void          TableDrawBorders(ImGuiTable* table);
//    static void          TableDrawDefaultContextMenu(ImGuiTable* table, ImGuiTableFlags flags_for_section_to_display);
//    static bool          TableBeginContextMenuPopup(ImGuiTable* table);
//    static void          TableMergeDrawChannels(ImGuiTable* table);
//    static ImGuiTableInstanceData*  TableGetInstanceData(ImGuiTable* table, int instance_no) { if (instance_no == 0) return &table->InstanceDataFirst; return &table->InstanceDataExtra[instance_no - 1]; }
//    static ImGuiID                  TableGetInstanceID(ImGuiTable* table, int instance_no)   { return TableGetInstanceData(table, instance_no)->TableInstanceID; }
//    static void          TableSortSpecsSanitize(ImGuiTable* table);
//    static void          TableSortSpecsBuild(ImGuiTable* table);
//    static ImGuiSortDirection TableGetColumnNextSortDirection(ImGuiTableColumn* column);
//    static void          TableFixColumnSortDirection(ImGuiTable* table, ImGuiTableColumn* column);
//    static float         TableGetColumnWidthAuto(ImGuiTable* table, ImGuiTableColumn* column);
//    static void          TableBeginRow(ImGuiTable* table);
//    static void          TableEndRow(ImGuiTable* table);
//    static void          TableBeginCell(ImGuiTable* table, int column_n);
//    static void          TableEndCell(ImGuiTable* table);
//    static ImRect        TableGetCellBgRect(const ImGuiTable* table, int column_n);
//    static const char*   TableGetColumnName(const ImGuiTable* table, int column_n);
//    static ImGuiID       TableGetColumnResizeID(ImGuiTable* table, int column_n, int instance_no = 0);
//    static float         TableCalcMaxColumnWidth(const ImGuiTable* table, int column_n);
//    static void          TableSetColumnWidthAutoSingle(ImGuiTable* table, int column_n);
//    static void          TableSetColumnWidthAutoAll(ImGuiTable* table);
//    static void          TableRemove(ImGuiTable* table);
//    static void          TableGcCompactTransientBuffers(ImGuiTable* table);
//    static void          TableGcCompactTransientBuffers(ImGuiTableTempData* table);
//    static void          TableGcCompactSettings();
//
//    // Tables: Settings
//    void                  TableLoadSettings(ImGuiTable* table);
//    void                  TableSaveSettings(ImGuiTable* table);
//    void                  TableResetSettings(ImGuiTable* table);
//    ImGuiTableSettings*   TableGetBoundSettings(ImGuiTable* table);
//    void                  TableSettingsAddSettingsHandler();
//    ImGuiTableSettings*   TableSettingsCreate(ImGuiID id, int columns_count);
//    ImGuiTableSettings*   TableSettingsFindByID(ImGuiID id);
//
//    // Tab Bars
//    ImGuiTabBar*  GetCurrentTabBar() { ImGuiContext& g = *GImGui; return g.CurrentTabBar; }
//    ImGuiTabBar*  TabBarFindByID(ImGuiID id);
//    void          TabBarRemove(ImGuiTabBar* tab_bar);
//    bool          BeginTabBarEx(ImGuiTabBar* tab_bar, const ImRect& bb, ImGuiTabBarFlags flags);
//    ImGuiTabItem* TabBarFindTabByID(ImGuiTabBar* tab_bar, ImGuiID tab_id);
//    ImGuiTabItem* TabBarFindTabByOrder(ImGuiTabBar* tab_bar, int order);
//    ImGuiTabItem* TabBarFindMostRecentlySelectedTabForActiveWindow(ImGuiTabBar* tab_bar);
//    ImGuiTabItem* TabBarGetCurrentTab(ImGuiTabBar* tab_bar);
//    int              TabBarGetTabOrder(ImGuiTabBar* tab_bar, ImGuiTabItem* tab) { return tab_bar->Tabs.index_from_ptr(tab); }
//    const char*   TabBarGetTabName(ImGuiTabBar* tab_bar, ImGuiTabItem* tab);
//    void          TabBarAddTab(ImGuiTabBar* tab_bar, ImGuiTabItemFlags tab_flags, ImGuiWindow* window);
//    void          TabBarRemoveTab(ImGuiTabBar* tab_bar, ImGuiID tab_id);
//    void          TabBarCloseTab(ImGuiTabBar* tab_bar, ImGuiTabItem* tab);
//    void          TabBarQueueFocus(ImGuiTabBar* tab_bar, ImGuiTabItem* tab);
//    void          TabBarQueueFocus(ImGuiTabBar* tab_bar, const char* tab_name);
//    void          TabBarQueueReorder(ImGuiTabBar* tab_bar, ImGuiTabItem* tab, int offset);
//    void          TabBarQueueReorderFromMousePos(ImGuiTabBar* tab_bar, ImGuiTabItem* tab, ImVec2 mouse_pos);
//    bool          TabBarProcessReorder(ImGuiTabBar* tab_bar);
//    bool          TabItemEx(ImGuiTabBar* tab_bar, const char* label, bool* p_open, ImGuiTabItemFlags flags, ImGuiWindow* docked_window);
//    void          TabItemSpacing(const char* str_id, ImGuiTabItemFlags flags, float width);
//    ImVec2        TabItemCalcSize(const char* label, bool has_close_button_or_unsaved_marker);
//    ImVec2        TabItemCalcSize(ImGuiWindow* window);
//    void          TabItemBackground(ImDrawList* draw_list, const ImRect& bb, ImGuiTabItemFlags flags, ImU32 col);
//    void          TabItemLabelAndCloseButton(ImDrawList* draw_list, const ImRect& bb, ImGuiTabItemFlags flags, ImVec2 frame_padding, const char* label, ImGuiID tab_id, ImGuiID close_button_id, bool is_contents_visible, bool* out_just_closed, bool* out_text_clipped);
//
//    // Render helpers
//    void          RenderText(ImVec2 pos, const char* text, const char* text_end = NULL, bool hide_text_after_hash = true);
//    void          RenderTextWrapped(ImVec2 pos, const char* text, const char* text_end, float wrap_width);
//    void          RenderTextClipped(const ImVec2& pos_min, const ImVec2& pos_max, const char* text, const char* text_end, const ImVec2* text_size_if_known, const ImVec2& align = ImVec2(0, 0), const ImRect* clip_rect = NULL);
//    void          RenderTextClippedEx(ImDrawList* draw_list, const ImVec2& pos_min, const ImVec2& pos_max, const char* text, const char* text_end, const ImVec2* text_size_if_known, const ImVec2& align = ImVec2(0, 0), const ImRect* clip_rect = NULL);
//    void          RenderTextEllipsis(ImDrawList* draw_list, const ImVec2& pos_min, const ImVec2& pos_max, float ellipsis_max_x, const char* text, const char* text_end, const ImVec2* text_size_if_known);
//    void          RenderFrame(ImVec2 p_min, ImVec2 p_max, ImU32 fill_col, bool borders = true, float rounding = 0.0f);
//    void          RenderFrameBorder(ImVec2 p_min, ImVec2 p_max, float rounding = 0.0f);
//    void          RenderColorRectWithAlphaCheckerboard(ImDrawList* draw_list, ImVec2 p_min, ImVec2 p_max, ImU32 fill_col, float grid_step, ImVec2 grid_off, float rounding = 0.0f, ImDrawFlags flags = 0);
//    void          RenderNavCursor(const ImRect& bb, ImGuiID id, ImGuiNavRenderCursorFlags flags = ImGuiNavRenderCursorFlags_None); // Navigation highlight
//    const char*   FindRenderedTextEnd(const char* text, const char* text_end = NULL); // Find the optional ## from which we stop displaying text.
//    void          RenderMouseCursor(ImVec2 pos, float scale, ImGuiMouseCursor mouse_cursor, ImU32 col_fill, ImU32 col_border, ImU32 col_shadow);
//
//    // Render helpers (those functions don't access any ImGui state!)
//    void          RenderArrow(ImDrawList* draw_list, ImVec2 pos, ImU32 col, ImGuiDir dir, float scale = 1.0f);
//    void          RenderBullet(ImDrawList* draw_list, ImVec2 pos, ImU32 col);
//    void          RenderCheckMark(ImDrawList* draw_list, ImVec2 pos, ImU32 col, float sz);
//    void          RenderArrowPointingAt(ImDrawList* draw_list, ImVec2 pos, ImVec2 half_sz, ImGuiDir direction, ImU32 col);
//    void          RenderArrowDockMenu(ImDrawList* draw_list, ImVec2 p_min, float sz, ImU32 col);
//    void          RenderRectFilledRangeH(ImDrawList* draw_list, const ImRect& rect, ImU32 col, float x_start_norm, float x_end_norm, float rounding);
//    void          RenderRectFilledWithHole(ImDrawList* draw_list, const ImRect& outer, const ImRect& inner, ImU32 col, float rounding);
//    ImDrawFlags   CalcRoundingFlagsForRectInRect(const ImRect& r_in, const ImRect& r_outer, float threshold);
//
//    // Widgets: Text
//    void          TextEx(const char* text, const char* text_end = NULL, ImGuiTextFlags flags = 0);
//    void          TextAligned(float align_x, float size_x, const char* fmt, ...);               // FIXME-WIP: Works but API is likely to be reworked. This is designed for 1 item on the line. (#7024)
//    void          TextAlignedV(float align_x, float size_x, const char* fmt, va_list args);
//
//    // Widgets
//    bool          ButtonEx(const char* label, const ImVec2& size_arg = ImVec2(0, 0), ImGuiButtonFlags flags = 0);
//    bool          ArrowButtonEx(const char* str_id, ImGuiDir dir, ImVec2 size_arg, ImGuiButtonFlags flags = 0);
//    bool          ImageButtonEx(ImGuiID id, ImTextureRef tex_ref, const ImVec2& image_size, const ImVec2& uv0, const ImVec2& uv1, const ImVec4& bg_col, const ImVec4& tint_col, ImGuiButtonFlags flags = 0);
//    void          SeparatorEx(ImGuiSeparatorFlags flags, float thickness = 1.0f);
//    void          SeparatorTextEx(ImGuiID id, const char* label, const char* label_end, float extra_width);
//    bool          CheckboxFlags(const char* label, ImS64* flags, ImS64 flags_value);
//    bool          CheckboxFlags(const char* label, ImU64* flags, ImU64 flags_value);
//
//    // Widgets: Window Decorations
//    bool          CloseButton(ImGuiID id, const ImVec2& pos);
//    bool          CollapseButton(ImGuiID id, const ImVec2& pos, ImGuiDockNode* dock_node);
//    void          Scrollbar(ImGuiAxis axis);
//    bool          ScrollbarEx(const ImRect& bb, ImGuiID id, ImGuiAxis axis, ImS64* p_scroll_v, ImS64 avail_v, ImS64 contents_v, ImDrawFlags draw_rounding_flags = 0);
//    ImRect        GetWindowScrollbarRect(ImGuiWindow* window, ImGuiAxis axis);
//    ImGuiID       GetWindowScrollbarID(ImGuiWindow* window, ImGuiAxis axis);
//    ImGuiID       GetWindowResizeCornerID(ImGuiWindow* window, int n); // 0..3: corners
//    ImGuiID       GetWindowResizeBorderID(ImGuiWindow* window, ImGuiDir dir);
//
    // Widgets low-level behaviors
    static bool          ButtonBehavior(const ImRect& bb, ImGuiID id, bool* out_hovered, bool* out_held, ImGuiButtonFlags flags = 0) { return im::ButtonBehavior(bb, id, out_hovered, out_held, flags); }
    static bool          DragBehavior(ImGuiID id, ImGuiDataType data_type, void* p_v, float v_speed, const void* p_min, const void* p_max, const char* format, ImGuiSliderFlags flags) { return im::DragBehavior(id, data_type, p_v, v_speed, p_min, p_max, format, flags); }
    static bool          SliderBehavior(const ImRect& bb, ImGuiID id, ImGuiDataType data_type, void* p_v, const void* p_min, const void* p_max, const char* format, ImGuiSliderFlags flags, ImRect* out_grab_bb) { return im::SliderBehavior(bb, id, data_type, p_v, p_min, p_max, format, flags, out_grab_bb); }
    static bool          SplitterBehavior(const ImRect& bb, ImGuiID id, ImGuiAxis axis, float* size1, float* size2, float min_size1, float min_size2, float hover_extend = 0.0f, float hover_visibility_delay = 0.0f, ImU32 bg_col = 0) { return im::SplitterBehavior(bb, id, axis, size1, size2, min_size1, min_size2, hover_extend, hover_visibility_delay, bg_col); }
//
//    // Widgets: Tree Nodes
//    bool          TreeNodeBehavior(ImGuiID id, ImGuiTreeNodeFlags flags, const char* label, const char* label_end = NULL);
//    void          TreeNodeDrawLineToChildNode(const ImVec2& target_pos);
//    void          TreeNodeDrawLineToTreePop(const ImGuiTreeNodeStackData* data);
//    void          TreePushOverrideID(ImGuiID id);
//    bool          TreeNodeGetOpen(ImGuiID storage_id);
//    void          TreeNodeSetOpen(ImGuiID storage_id, bool open);
//    bool          TreeNodeUpdateNextOpen(ImGuiID storage_id, ImGuiTreeNodeFlags flags);   // Return open state. Consume previous SetNextItemOpen() data, if any. May return true when logging.
//
//    // Data type helpers
//    const ImGuiDataTypeInfo*  DataTypeGetInfo(ImGuiDataType data_type);
//    int           DataTypeFormatString(char* buf, int buf_size, ImGuiDataType data_type, const void* p_data, const char* format);
//    void          DataTypeApplyOp(ImGuiDataType data_type, int op, void* output, const void* arg_1, const void* arg_2);
//    bool          DataTypeApplyFromText(const char* buf, ImGuiDataType data_type, void* p_data, const char* format, void* p_data_when_empty = NULL);
//    int           DataTypeCompare(ImGuiDataType data_type, const void* arg_1, const void* arg_2);
//    bool          DataTypeClamp(ImGuiDataType data_type, void* p_data, const void* p_min, const void* p_max);
//    bool          DataTypeIsZero(ImGuiDataType data_type, const void* p_data);
//
//    // InputText
//    bool          InputTextEx(const char* label, const char* hint, char* buf, int buf_size, const ImVec2& size_arg, ImGuiInputTextFlags flags, ImGuiInputTextCallback callback = NULL, void* user_data = NULL);
//    void          InputTextDeactivateHook(ImGuiID id);
//    bool          TempInputText(const ImRect& bb, ImGuiID id, const char* label, char* buf, int buf_size, ImGuiInputTextFlags flags);
//    bool          TempInputScalar(const ImRect& bb, ImGuiID id, const char* label, ImGuiDataType data_type, void* p_data, const char* format, const void* p_clamp_min = NULL, const void* p_clamp_max = NULL);
//    bool             TempInputIsActive(ImGuiID id)       { ImGuiContext& g = *GImGui; return (g.ActiveId == id && g.TempInputId == id); }
//    ImGuiInputTextState* GetInputTextState(ImGuiID id)   { ImGuiContext& g = *GImGui; return (id != 0 && g.InputTextState.ID == id) ? &g.InputTextState : NULL; } // Get input text state if active
//    void          SetNextItemRefVal(ImGuiDataType data_type, void* p_data);
//    bool             IsItemActiveAsInputText() { ImGuiContext& g = *GImGui; return g.ActiveId != 0 && g.ActiveId == g.LastItemData.ID && g.InputTextState.ID == g.LastItemData.ID; } // This may be useful to apply workaround that a based on distinguish whenever an item is active as a text input field.
//
//    // Color
//    void          ColorTooltip(const char* text, const float* col, ImGuiColorEditFlags flags);
//    void          ColorEditOptionsPopup(const float* col, ImGuiColorEditFlags flags);
//    void          ColorPickerOptionsPopup(const float* ref_col, ImGuiColorEditFlags flags);
//
//    // Plot
//    int           PlotEx(ImGuiPlotType plot_type, const char* label, float (*values_getter)(void* data, int idx), void* data, int values_count, int values_offset, const char* overlay_text, float scale_min, float scale_max, const ImVec2& size_arg);
//
//    // Shade functions (write over already created vertices)
//    void          ShadeVertsLinearColorGradientKeepAlpha(ImDrawList* draw_list, int vert_start_idx, int vert_end_idx, ImVec2 gradient_p0, ImVec2 gradient_p1, ImU32 col0, ImU32 col1);
//    void          ShadeVertsLinearUV(ImDrawList* draw_list, int vert_start_idx, int vert_end_idx, const ImVec2& a, const ImVec2& b, const ImVec2& uv_a, const ImVec2& uv_b, bool clamp);
//    void          ShadeVertsTransformPos(ImDrawList* draw_list, int vert_start_idx, int vert_end_idx, const ImVec2& pivot_in, float cos_a, float sin_a, const ImVec2& pivot_out);
//
//    // Garbage collection
//    void          GcCompactTransientMiscBuffers();
//    void          GcCompactTransientWindowBuffers(ImGuiWindow* window);
//    void          GcAwakeTransientWindowBuffers(ImGuiWindow* window);
//
//    // Error handling, State Recovery
//    bool          ErrorLog(const char* msg);
//    void          ErrorRecoveryStoreState(ImGuiErrorRecoveryState* state_out);
//    void          ErrorRecoveryTryToRecoverState(const ImGuiErrorRecoveryState* state_in);
//    void          ErrorRecoveryTryToRecoverWindowState(const ImGuiErrorRecoveryState* state_in);
//    void          ErrorCheckUsingSetCursorPosToExtendParentBoundaries();
//    void          ErrorCheckEndFrameFinalizeErrorTooltip();
//    bool          BeginErrorTooltip();
//    void          EndErrorTooltip();
//
//    // Debug Tools
//    void          DebugAllocHook(ImGuiDebugAllocInfo* info, int frame_count, void* ptr, size_t size); // size >= 0 : alloc, size = -1 : free
//    void          DebugDrawCursorPos(ImU32 col = IM_COL32(255, 0, 0, 255));
//    void          DebugDrawLineExtents(ImU32 col = IM_COL32(255, 0, 0, 255));
//    void          DebugDrawItemRect(ImU32 col = IM_COL32(255, 0, 0, 255));
//    void          DebugTextUnformattedWithLocateItem(const char* line_begin, const char* line_end);
//    void          DebugLocateItem(ImGuiID target_id);                     // Call sparingly: only 1 at the same time!
//    void          DebugLocateItemOnHover(ImGuiID target_id);              // Only call on reaction to a mouse Hover: because only 1 at the same time!
//    void          DebugLocateItemResolveWithLastItem();
//    void          DebugBreakClearData();
//    bool          DebugBreakButton(const char* label, const char* description_of_location);
//    void          DebugBreakButtonTooltip(bool keyboard_only, const char* description_of_location);
//    void          ShowFontAtlas(ImFontAtlas* atlas);
//    void          DebugHookIdInfo(ImGuiID id, ImGuiDataType data_type, const void* data_id, const void* data_id_end);
//    void          DebugNodeColumns(ImGuiOldColumns* columns);
//    void          DebugNodeDockNode(ImGuiDockNode* node, const char* label);
//    void          DebugNodeDrawList(ImGuiWindow* window, ImGuiViewportP* viewport, const ImDrawList* draw_list, const char* label);
//    void          DebugNodeDrawCmdShowMeshAndBoundingBox(ImDrawList* out_draw_list, const ImDrawList* draw_list, const ImDrawCmd* draw_cmd, bool show_mesh, bool show_aabb);
//    void          DebugNodeFont(ImFont* font);
//    void          DebugNodeFontGlyphesForSrcMask(ImFont* font, ImFontBaked* baked, int src_mask);
//    void          DebugNodeFontGlyph(ImFont* font, const ImFontGlyph* glyph);
//    void          DebugNodeTexture(ImTextureData* tex, int int_id, const ImFontAtlasRect* highlight_rect = NULL); // ID used to facilitate persisting the "current" texture.
//    void          DebugNodeStorage(ImGuiStorage* storage, const char* label);
//    void          DebugNodeTabBar(ImGuiTabBar* tab_bar, const char* label);
//    void          DebugNodeTable(ImGuiTable* table);
//    void          DebugNodeTableSettings(ImGuiTableSettings* settings);
//    void          DebugNodeInputTextState(ImGuiInputTextState* state);
//    void          DebugNodeTypingSelectState(ImGuiTypingSelectState* state);
//    void          DebugNodeMultiSelectState(ImGuiMultiSelectState* state);
//    void          DebugNodeWindow(ImGuiWindow* window, const char* label);
//    void          DebugNodeWindowSettings(ImGuiWindowSettings* settings);
//    void          DebugNodeWindowsList(ImVector<ImGuiWindow*>* windows, const char* label);
//    void          DebugNodeWindowsListByBeginStackParent(ImGuiWindow** windows, int windows_size, ImGuiWindow* parent_in_begin_stack);
//    void          DebugNodeViewport(ImGuiViewportP* viewport);
//    void          DebugNodePlatformMonitor(ImGuiPlatformMonitor* monitor, const char* label, int idx);
//    void          DebugRenderKeyboardPreview(ImDrawList* draw_list);
//    void          DebugRenderViewportThumbnail(ImDrawList* draw_list, ImGuiViewportP* viewport, const ImRect& bb);
};

class ImGui {
    // Emscripten webidl don't support binding methods without a class so we need to create a wrapper
public:
    // Context creation and access
    static ImGuiContext* CreateContext(ImFontAtlas* shared_font_atlas = NULL) {
        ImGuiContext* ctx = im::CreateContext(shared_font_atlas);
        if (ctx != NULL) {
            im::SetCurrentContext(ctx);
            im::GetIO().IniFilename = NULL;
        }
        return ctx;
    }
    static void          DestroyContext(ImGuiContext* ctx = NULL) { im::DestroyContext(ctx); }
    static ImGuiContext* GetCurrentContext() { return im::GetCurrentContext(); }
    static void          SetCurrentContext(ImGuiContext* ctx) { im::SetCurrentContext(ctx); }

    // Main
    static ImGuiIO&      GetIO() { return im::GetIO(); }
    static ImGuiPlatformIO& GetPlatformIO() { return im::GetPlatformIO(); }
    static ImGuiStyle&   GetStyle() { return im::GetStyle(); }
    static void          NewFrame() { im::NewFrame(); }
    static void          EndFrame() { im::EndFrame(); }
    static void          Render() { im::Render(); }
    static ImDrawData*   GetDrawData() { return im::GetDrawData(); }

    // Demo, Debug, Information
    static void          ShowDemoWindow(bool* p_open = NULL) { im::ShowDemoWindow(p_open); }
    static void          ShowMetricsWindow(bool* p_open = NULL) { im::ShowMetricsWindow(p_open); }
    static void          ShowDebugLogWindow(bool* p_open = NULL) { im::ShowDebugLogWindow(p_open); }
    static void          ShowIDStackToolWindow(bool* p_open = NULL) { im::ShowIDStackToolWindow(p_open); }
    static void          ShowAboutWindow(bool* p_open = NULL) { im::ShowAboutWindow(p_open); }
    static void          ShowStyleEditor(ImGuiStyle* ref = NULL) { im::ShowStyleEditor(ref); }
    static bool          ShowStyleSelector(const char* label) { return im::ShowStyleSelector(label); }
    static void          ShowFontSelector(const char* label) { im::ShowFontSelector(label); }
    static void          ShowUserGuide() { im::ShowUserGuide(); }
    static const char*   GetVersion() { return im::GetVersion(); }

    // Styles
    static void          StyleColorsDark(ImGuiStyle* dst = NULL) { im::StyleColorsDark(dst); }
    static void          StyleColorsLight(ImGuiStyle* dst = NULL) { im::StyleColorsLight(dst); }
    static void          StyleColorsClassic(ImGuiStyle* dst = NULL) { im::StyleColorsClassic(dst); }

    // Windows
    static bool          Begin(const char* name, bool* p_open = NULL, ImGuiWindowFlags flags = 0) { return im::Begin(name, p_open, flags); }
    static void          End() { im::End(); }

    // Child Windows
    static bool          BeginChild(const char* str_id, const ImVec2& size = ImVec2(0, 0), ImGuiChildFlags child_flags = 0, ImGuiWindowFlags window_flags = 0) { return im::BeginChild(str_id, size, child_flags, window_flags); }
    static bool          BeginChild(ImGuiID id, const ImVec2& size = ImVec2(0, 0), ImGuiChildFlags child_flags = 0, ImGuiWindowFlags window_flags = 0) { return im::BeginChild(id, size, child_flags, window_flags); }
    static void          EndChild() { im::EndChild(); }

    // Windows Utilities
    static bool          IsWindowAppearing() { return im::IsWindowAppearing(); }
    static bool          IsWindowCollapsed() { return im::IsWindowCollapsed(); }
    static bool          IsWindowFocused(ImGuiFocusedFlags flags=0) { return im::IsWindowFocused(flags); }
    static bool          IsWindowHovered(ImGuiHoveredFlags flags=0) { return im::IsWindowHovered(flags); }
    static ImDrawList*   GetWindowDrawList() { return im::GetWindowDrawList(); }
    static float         GetWindowDpiScale() { return im::GetWindowDpiScale(); }
    static ImVec2        GetWindowPos() { return im::GetWindowPos(); }
    static ImVec2        GetWindowSize() { return im::GetWindowSize(); }
    static float         GetWindowWidth() { return im::GetWindowWidth(); }
    static float         GetWindowHeight() { return im::GetWindowHeight(); }
    static ImGuiViewport*GetWindowViewport() { return im::GetWindowViewport(); }

    // Window manipulation
    static void          SetNextWindowPos(const ImVec2& pos, ImGuiCond cond = 0, const ImVec2& pivot = ImVec2(0, 0)) { im::SetNextWindowPos(pos, cond, pivot); }
    static void          SetNextWindowSize(const ImVec2& size, ImGuiCond cond = 0) { im::SetNextWindowSize(size, cond); }
    static void          SetNextWindowSizeConstraints(const ImVec2& size_min, const ImVec2& size_max, ImGuiSizeCallback custom_callback = NULL, void* custom_callback_data = NULL) { im::SetNextWindowSizeConstraints(size_min, size_max, custom_callback, custom_callback_data); }
    static void          SetNextWindowContentSize(const ImVec2& size) { im::SetNextWindowContentSize(size); }
    static void          SetNextWindowCollapsed(bool collapsed, ImGuiCond cond = 0) { im::SetNextWindowCollapsed(collapsed, cond); }
    static void          SetNextWindowFocus() { im::SetNextWindowFocus(); }
    static void          SetNextWindowScroll(const ImVec2& scroll) { im::SetNextWindowScroll(scroll); }
    static void          SetNextWindowBgAlpha(float alpha) { im::SetNextWindowBgAlpha(alpha); }
    static void          SetNextWindowViewport(ImGuiID viewport_id) { im::SetNextWindowViewport(viewport_id); }
    static void          SetWindowPos(const ImVec2& pos, ImGuiCond cond = 0) { im::SetWindowPos(pos, cond); }
    static void          SetWindowSize(const ImVec2& size, ImGuiCond cond = 0) { im::SetWindowSize(size, cond); }
    static void          SetWindowCollapsed(bool collapsed, ImGuiCond cond = 0) { im::SetWindowCollapsed(collapsed, cond); }
    static void          SetWindowFocus() { im::SetWindowFocus(); }
    static void          SetWindowPos(const char* name, const ImVec2& pos, ImGuiCond cond = 0) { im::SetWindowPos(name, pos, cond); }
    static void          SetWindowSize(const char* name, const ImVec2& size, ImGuiCond cond = 0) { im::SetWindowSize(name, size, cond); }
    // TODO adding _1 fix overloaded functions have similar conversions
    static void          SetWindowCollapsed_1(const char* name, bool collapsed, ImGuiCond cond = 0) { im::SetWindowCollapsed(name, collapsed, cond); }
    static void          SetWindowFocus(const char* name) { im::SetWindowFocus(name); }

    // Windows Scrolling
    static float         GetScrollX() { return im::GetScrollX(); }
    static float         GetScrollY() { return im::GetScrollY(); }
    static void          SetScrollX(float scroll_x) { im::SetScrollX(scroll_x); }
    static void          SetScrollY(float scroll_y) { im::SetScrollY(scroll_y); }
    static float         GetScrollMaxX() { return im::GetScrollMaxX(); }
    static float         GetScrollMaxY() { return im::GetScrollMaxY(); }
    static void          SetScrollHereX(float center_x_ratio = 0.5f) { im::SetScrollHereX(center_x_ratio); }
    static void          SetScrollHereY(float center_y_ratio = 0.5f) { im::SetScrollHereY(center_y_ratio); }
    static void          SetScrollFromPosX(float local_x, float center_x_ratio = 0.5f) { im::SetScrollFromPosX(local_x, center_x_ratio); }
    static void          SetScrollFromPosY(float local_y, float center_y_ratio = 0.5f) { im::SetScrollFromPosY(local_y, center_y_ratio); }

    // Parameters stacks (font)
    static void          PushFont(ImFont* font, float font_size_base_unscaled) { im::PushFont(font, font_size_base_unscaled); }
    static void          PopFont() { im::PopFont(); }
    static ImFont*       GetFont() { return im::GetFont(); }
    static float         GetFontSize() { return im::GetFontSize(); }
    static ImFontBaked*  GetFontBaked() { return im::GetFontBaked(); }

    // Parameters stacks (shared)
    static void          PushStyleColor(ImGuiCol idx, ImU32 col) { im::PushStyleColor(idx, col); }
    static void          PushStyleColor(ImGuiCol idx, const ImVec4& col) { im::PushStyleColor(idx, col); }
    static void          PopStyleColor(int count = 1) { im::PopStyleColor(count); }
    static void          PushStyleVar(ImGuiStyleVar idx, float val) { im::PushStyleVar(idx, val); }
    static void          PushStyleVar(ImGuiStyleVar idx, const ImVec2& val) { im::PushStyleVar(idx, val); }
    static void          PushStyleVarX(ImGuiStyleVar idx, float val_x) { im::PushStyleVarX(idx, val_x); }
    static void          PushStyleVarY(ImGuiStyleVar idx, float val_y) { im::PushStyleVarY(idx, val_y); }
    static void          PopStyleVar(int count = 1) { im::PopStyleVar(count); }
    static void          PushItemFlag(ImGuiItemFlags option, bool enabled) { im::PushItemFlag(option, enabled); }
    static void          PopItemFlag() { im::PopItemFlag(); }

    // Parameters stacks (current window)
    static void          PushItemWidth(float item_width) { im::PushItemWidth(item_width); }
    static void          PopItemWidth() { im::PopItemWidth(); }
    static void          SetNextItemWidth(float item_width) { im::SetNextItemWidth(item_width); }
    static float         CalcItemWidth() { return im::CalcItemWidth(); }
    static void          PushTextWrapPos(float wrap_local_pos_x = 0.0f) { im::PushTextWrapPos(wrap_local_pos_x); }
    static void          PopTextWrapPos() { im::PopTextWrapPos(); }

    // Style read access
    static ImVec2        GetFontTexUvWhitePixel() { return im::GetFontTexUvWhitePixel(); }
    static ImU32         GetColorU32(ImGuiCol idx, float alpha_mul = 1.0f) { return im::GetColorU32(idx, alpha_mul); }
    static ImU32         GetColorU32(const ImVec4& col) { return im::GetColorU32(col); }
    static ImU32         GetColorU32(ImU32 col, float alpha_mul = 1.0f) { return im::GetColorU32(col, alpha_mul); }
    static const ImVec4& GetStyleColorVec4(ImGuiCol idx) { return im::GetStyleColorVec4(idx); }

    // Layout cursor positioning
    static ImVec2        GetCursorScreenPos() { return im::GetCursorScreenPos(); }
    static void          SetCursorScreenPos(const ImVec2& pos) { im::SetCursorScreenPos(pos); }
    static ImVec2        GetContentRegionAvail() { return im::GetContentRegionAvail(); }
    static ImVec2        GetCursorPos() { return im::GetCursorPos(); }
    static float         GetCursorPosX() { return im::GetCursorPosX(); }
    static float         GetCursorPosY() { return im::GetCursorPosY(); }
    static void          SetCursorPos(const ImVec2& local_pos) { im::SetCursorPos(local_pos); }
    static void          SetCursorPosX(float local_x) { im::SetCursorPosX(local_x); }
    static void          SetCursorPosY(float local_y) { im::SetCursorPosY(local_y); }
    static ImVec2        GetCursorStartPos() { return im::GetCursorStartPos(); }

    // Other layout functions
    static void          Separator() { im::Separator(); }
    static void          SameLine(float offset_from_start_x=0.0f, float spacing=-1.0f) { im::SameLine(offset_from_start_x, spacing); }
    static void          NewLine() { im::NewLine(); }
    static void          Spacing() { im::Spacing(); }
    static void          Dummy(const ImVec2& size) { im::Dummy(size); }
    static void          Indent(float indent_w = 0.0f) { im::Indent(indent_w); }
    static void          Unindent(float indent_w = 0.0f) { im::Unindent(indent_w); }
    static void          BeginGroup() { im::BeginGroup(); }
    static void          EndGroup() { im::EndGroup(); }
    static void          AlignTextToFramePadding() { im::AlignTextToFramePadding(); }
    static float         GetTextLineHeight() { return im::GetTextLineHeight(); }
    static float         GetTextLineHeightWithSpacing() { return im::GetTextLineHeightWithSpacing(); }
    static float         GetFrameHeight() { return im::GetFrameHeight(); }
    static float         GetFrameHeightWithSpacing() { return im::GetFrameHeightWithSpacing(); }

    // ID stack/scopes
    static void          PushID(const char* str_id) { im::PushID(str_id); }
    static void          PushID(const char* str_id_begin, const char* str_id_end) { im::PushID(str_id_begin, str_id_end); }
    static void          PushID(const void* ptr_id) { im::PushID(ptr_id); }
    static void          PushID(int int_id) { im::PushID(int_id); }
    static void          PopID() { im::PopID(); }
    static ImGuiID       GetID(const char* str_id) { return im::GetID(str_id); }
    static ImGuiID       GetID(const char* str_id_begin, const char* str_id_end) { return im::GetID(str_id_begin, str_id_end); }
    static ImGuiID       GetID(const void* ptr_id) { return im::GetID(ptr_id); }
    static ImGuiID       GetID(int int_id) { return im::GetID(int_id); }

    // Widgets: Text
    static void          TextUnformatted(const char* text, const char* text_end = NULL) { im::TextUnformatted(text, text_end); }
    static void          Text(const char* fmt) { im::Text(fmt); }
    static void          TextV(const char* fmt, va_list args) { im::TextV(fmt, args); }
    static void          TextColored(const ImVec4& col, const char* fmt) { im::TextColored(col, fmt); }
    static void          TextColoredV(const ImVec4& col, const char* fmt, va_list args) { im::TextColoredV(col, fmt, args); }
    static void          TextDisabled(const char* fmt) { im::TextDisabled(fmt); }
    static void          TextDisabledV(const char* fmt, va_list args) { im::TextDisabledV(fmt, args); }
    static void          TextWrapped(const char* fmt) { im::TextWrapped(fmt); }
    static void          TextWrappedV(const char* fmt, va_list args) { im::TextWrappedV(fmt, args); }
    static void          LabelText(const char* label, const char* fmt) { im::LabelText(label, fmt); }
    static void          LabelTextV(const char* label, const char* fmt, va_list args) { im::LabelTextV(label, fmt, args); }
    static void          BulletText(const char* fmt) { im::BulletText(fmt); }
    static void          BulletTextV(const char* fmt, va_list args) { im::BulletTextV(fmt, args); }
    static void          SeparatorText(const char* label) { im::SeparatorText(label); }

    // Widgets: Main
    static bool          Button(const char* label, const ImVec2& size = ImVec2(0, 0)) { return im::Button(label, size); }
    static bool          SmallButton(const char* label) { return im::SmallButton(label); }
    static bool          InvisibleButton(const char* str_id, const ImVec2& size, ImGuiButtonFlags flags = 0) { return im::InvisibleButton(str_id, size, flags); }
    static bool          ArrowButton(const char* str_id, ImGuiDir dir) { return im::ArrowButton(str_id, dir); }
    static bool          Checkbox(const char* label, bool* v) { return im::Checkbox(label, v); }
    static bool          CheckboxFlags(const char* label, int* flags, int flags_value) { return im::CheckboxFlags(label, flags, flags_value); }
    static bool          CheckboxFlags(const char* label, unsigned int* flags, unsigned int flags_value) { return im::CheckboxFlags(label, flags, flags_value); }
    static bool          RadioButton(const char* label, bool active) { return im::RadioButton(label, active); }
    static bool          RadioButton(const char* label, int* v, int v_button) { return im::RadioButton(label, v, v_button); }
    static void          ProgressBar(float fraction, const ImVec2& size_arg = ImVec2(-FLT_MIN, 0), const char* overlay = NULL) { im::ProgressBar(fraction, size_arg, overlay); }
    static void          Bullet() { im::Bullet(); }
    static bool          TextLink(const char* label) { return im::TextLink(label); }
    static bool          TextLinkOpenURL(const char* label, const char* url = NULL) { return im::TextLinkOpenURL(label, url); }

    // Widgets: Images
    static void          Image(ImTextureRef tex_ref, const ImVec2& image_size, const ImVec2& uv0 = ImVec2(0, 0), const ImVec2& uv1 = ImVec2(1, 1)) { im::Image(tex_ref, image_size, uv0, uv1); }
    static void          ImageWithBg(ImTextureRef tex_ref, const ImVec2& image_size, const ImVec2& uv0 = ImVec2(0, 0), const ImVec2& uv1 = ImVec2(1, 1), const ImVec4& bg_col = ImVec4(0, 0, 0, 0), const ImVec4& tint_col = ImVec4(1, 1, 1, 1)) { im::ImageWithBg(tex_ref, image_size, uv0, uv1, bg_col, tint_col); }
    static bool          ImageButton(const char* str_id, ImTextureRef tex_ref, const ImVec2& image_size, const ImVec2& uv0 = ImVec2(0, 0), const ImVec2& uv1 = ImVec2(1, 1), const ImVec4& bg_col = ImVec4(0, 0, 0, 0), const ImVec4& tint_col = ImVec4(1, 1, 1, 1)) { return im::ImageButton(str_id, tex_ref, image_size, uv0, uv1, bg_col, tint_col); }

    // Widgets: Combo Box (Dropdown)
    static bool          BeginCombo(const char* label, const char* preview_value, ImGuiComboFlags flags = 0) { return im::BeginCombo(label, preview_value, flags); }
    static void          EndCombo() { im::EndCombo(); }
    static bool          Combo(const char* label, int* current_item, const char* const items[], int items_count, int popup_max_height_in_items = -1) { return im::Combo(label, current_item, items, items_count, popup_max_height_in_items); }
    static bool          Combo(const char* label, int* current_item, const char* items_separated_by_zeros, int popup_max_height_in_items = -1) { return im::Combo(label, current_item, items_separated_by_zeros, popup_max_height_in_items); }
//    bool          Combo(const char* label, int* current_item, const char* (*getter)(void* user_data, int idx), void* user_data, int items_count, int popup_max_height_in_items = -1);

    // Widgets: Drag Sliders
    static bool          DragFloat(const char* label, float* v, float v_speed = 1.0f, float v_min = 0.0f, float v_max = 0.0f, const char* format = "%.3f", ImGuiSliderFlags flags = 0) { return im::DragFloat(label, v, v_speed, v_min, v_max, format, flags); }
    static bool          DragFloat2(const char* label, float v[2], float v_speed = 1.0f, float v_min = 0.0f, float v_max = 0.0f, const char* format = "%.3f", ImGuiSliderFlags flags = 0) { return im::DragFloat2(label, v, v_speed, v_min, v_max, format, flags); }
    static bool          DragFloat3(const char* label, float v[3], float v_speed = 1.0f, float v_min = 0.0f, float v_max = 0.0f, const char* format = "%.3f", ImGuiSliderFlags flags = 0) { return im::DragFloat3(label, v, v_speed, v_min, v_max, format, flags); }
    static bool          DragFloat4(const char* label, float v[4], float v_speed = 1.0f, float v_min = 0.0f, float v_max = 0.0f, const char* format = "%.3f", ImGuiSliderFlags flags = 0) { return im::DragFloat4(label, v, v_speed, v_min, v_max, format, flags); }
    static bool          DragFloatRange2(const char* label, float* v_current_min, float* v_current_max, float v_speed = 1.0f, float v_min = 0.0f, float v_max = 0.0f, const char* format = "%.3f", const char* format_max = NULL, ImGuiSliderFlags flags = 0) { return im::DragFloatRange2(label, v_current_min, v_current_max, v_speed, v_min, v_max, format, format_max, flags); }
    static bool          DragInt(const char* label, int* v, float v_speed = 1.0f, int v_min = 0, int v_max = 0, const char* format = "%d", ImGuiSliderFlags flags = 0) { return im::DragInt(label, v, v_speed, v_min, v_max, format, flags); }
    static bool          DragInt2(const char* label, int v[2], float v_speed = 1.0f, int v_min = 0, int v_max = 0, const char* format = "%d", ImGuiSliderFlags flags = 0) { return im::DragInt2(label, v, v_speed, v_min, v_max, format, flags); }
    static bool          DragInt3(const char* label, int v[3], float v_speed = 1.0f, int v_min = 0, int v_max = 0, const char* format = "%d", ImGuiSliderFlags flags = 0) { return im::DragInt3(label, v, v_speed, v_min, v_max, format, flags); }
    static bool          DragInt4(const char* label, int v[4], float v_speed = 1.0f, int v_min = 0, int v_max = 0, const char* format = "%d", ImGuiSliderFlags flags = 0) { return im::DragInt4(label, v, v_speed, v_min, v_max, format, flags); }
    static bool          DragIntRange2(const char* label, int* v_current_min, int* v_current_max, float v_speed = 1.0f, int v_min = 0, int v_max = 0, const char* format = "%d", const char* format_max = NULL, ImGuiSliderFlags flags = 0) { return im::DragIntRange2(label, v_current_min, v_current_max, v_speed, v_min, v_max, format, format_max, flags); }
    static bool          DragScalar(const char* label, ImGuiDataType data_type, void* p_data, float v_speed = 1.0f, const void* p_min = NULL, const void* p_max = NULL, const char* format = NULL, ImGuiSliderFlags flags = 0) { return im::DragScalar(label, data_type, p_data, v_speed, p_min, p_max, format, flags); }
    static bool          DragScalarN(const char* label, ImGuiDataType data_type, void* p_data, int components, float v_speed = 1.0f, const void* p_min = NULL, const void* p_max = NULL, const char* format = NULL, ImGuiSliderFlags flags = 0) { return im::DragScalarN(label, data_type, p_data, components, v_speed, p_min, p_max, format, flags); }

    // Widgets: Regular Sliders
    static bool          SliderFloat(const char* label, float* v, float v_min, float v_max, const char* format = "%.3f", ImGuiSliderFlags flags = 0) { return im::SliderFloat(label, v, v_min, v_max, format, flags); }
    static bool          SliderFloat2(const char* label, float v[2], float v_min, float v_max, const char* format = "%.3f", ImGuiSliderFlags flags = 0) { return im::SliderFloat2(label, v, v_min, v_max, format, flags); }
    static bool          SliderFloat3(const char* label, float v[3], float v_min, float v_max, const char* format = "%.3f", ImGuiSliderFlags flags = 0) { return im::SliderFloat3(label, v, v_min, v_max, format, flags); }
    static bool          SliderFloat4(const char* label, float v[4], float v_min, float v_max, const char* format = "%.3f", ImGuiSliderFlags flags = 0) { return im::SliderFloat4(label, v, v_min, v_max, format, flags); }
    static bool          SliderAngle(const char* label, float* v_rad, float v_degrees_min = -360.0f, float v_degrees_max = +360.0f, const char* format = "%.0f deg", ImGuiSliderFlags flags = 0) { return im::SliderAngle(label, v_rad, v_degrees_min, v_degrees_max, format, flags); }
    static bool          SliderInt(const char* label, int* v, int v_min, int v_max, const char* format = "%d", ImGuiSliderFlags flags = 0) { return im::SliderInt(label, v, v_min, v_max, format, flags); }
    static bool          SliderInt2(const char* label, int v[2], int v_min, int v_max, const char* format = "%d", ImGuiSliderFlags flags = 0) { return im::SliderInt2(label, v, v_min, v_max, format, flags); }
    static bool          SliderInt3(const char* label, int v[3], int v_min, int v_max, const char* format = "%d", ImGuiSliderFlags flags = 0) { return im::SliderInt3(label, v, v_min, v_max, format, flags); }
    static bool          SliderInt4(const char* label, int v[4], int v_min, int v_max, const char* format = "%d", ImGuiSliderFlags flags = 0) { return im::SliderInt4(label, v, v_min, v_max, format, flags); }
    static bool          SliderScalar(const char* label, ImGuiDataType data_type, void* p_data, const void* p_min, const void* p_max, const char* format = NULL, ImGuiSliderFlags flags = 0) { return im::SliderScalar(label, data_type, p_data, p_min, p_max, format, flags); }
    static bool          SliderScalarN(const char* label, ImGuiDataType data_type, void* p_data, int components, const void* p_min, const void* p_max, const char* format = NULL, ImGuiSliderFlags flags = 0) { return im::SliderScalarN(label, data_type, p_data, components, p_min, p_max, format, flags); }
    static bool          VSliderFloat(const char* label, const ImVec2& size, float* v, float v_min, float v_max, const char* format = "%.3f", ImGuiSliderFlags flags = 0) { return im::VSliderFloat(label, size, v, v_min, v_max, format, flags); }
    static bool          VSliderInt(const char* label, const ImVec2& size, int* v, int v_min, int v_max, const char* format = "%d", ImGuiSliderFlags flags = 0) { return im::VSliderInt(label, size, v, v_min, v_max, format, flags); }
    static bool          VSliderScalar(const char* label, const ImVec2& size, ImGuiDataType data_type, void* p_data, const void* p_min, const void* p_max, const char* format = NULL, ImGuiSliderFlags flags = 0) { return im::VSliderScalar(label, size, data_type, p_data, p_min, p_max, format, flags); }

    // Widgets: Input with Keyboard
    static bool          InputText(const char* label, char* buf, size_t buf_size, ImGuiInputTextFlags flags = 0, ImGuiInputTextCallback callback = NULL, void* user_data = NULL) { return im::InputText(label, buf, buf_size, flags, callback, user_data); }
    static bool          InputTextMultiline(const char* label, char* buf, size_t buf_size, const ImVec2& size = ImVec2(0, 0), ImGuiInputTextFlags flags = 0, ImGuiInputTextCallback callback = NULL, void* user_data = NULL) { return im::InputTextMultiline(label, buf, buf_size, size, flags, callback, user_data); }
    static bool          InputTextWithHint(const char* label, const char* hint, char* buf, size_t buf_size, ImGuiInputTextFlags flags = 0, ImGuiInputTextCallback callback = NULL, void* user_data = NULL) { return im::InputTextWithHint(label, hint, buf, buf_size, flags, callback, user_data); }
    static bool          InputFloat(const char* label, float* v, float step = 0.0f, float step_fast = 0.0f, const char* format = "%.3f", ImGuiInputTextFlags flags = 0) { return im::InputFloat(label, v, step, step_fast, format, flags); }
    static bool          InputFloat2(const char* label, float v[2], const char* format = "%.3f", ImGuiInputTextFlags flags = 0) { return im::InputFloat2(label, v, format, flags); }
    static bool          InputFloat3(const char* label, float v[3], const char* format = "%.3f", ImGuiInputTextFlags flags = 0) { return im::InputFloat3(label, v, format, flags); }
    static bool          InputFloat4(const char* label, float v[4], const char* format = "%.3f", ImGuiInputTextFlags flags = 0) { return im::InputFloat4(label, v, format, flags); }
    static bool          InputInt(const char* label, int* v, int step = 1, int step_fast = 100, ImGuiInputTextFlags flags = 0) { return im::InputInt(label, v, step, step_fast, flags); }
    static bool          InputInt2(const char* label, int v[2], ImGuiInputTextFlags flags = 0) { return im::InputInt2(label, v, flags); }
    static bool          InputInt3(const char* label, int v[3], ImGuiInputTextFlags flags = 0) { return im::InputInt3(label, v, flags); }
    static bool          InputInt4(const char* label, int v[4], ImGuiInputTextFlags flags = 0) { return im::InputInt4(label, v, flags); }
    static bool          InputDouble(const char* label, double* v, double step = 0.0, double step_fast = 0.0, const char* format = "%.6f", ImGuiInputTextFlags flags = 0) { return im::InputDouble(label, v, step, step_fast, format, flags); }
    static bool          InputScalar(const char* label, ImGuiDataType data_type, void* p_data, const void* p_step = NULL, const void* p_step_fast = NULL, const char* format = NULL, ImGuiInputTextFlags flags = 0) { return im::InputScalar(label, data_type, p_data, p_step, p_step_fast, format, flags); }
    static bool          InputScalarN(const char* label, ImGuiDataType data_type, void* p_data, int components, const void* p_step = NULL, const void* p_step_fast = NULL, const char* format = NULL, ImGuiInputTextFlags flags = 0) { return im::InputScalarN(label, data_type, p_data, components, p_step, p_step_fast, format, flags); }

    // Widgets: Color Editor/Picker (tip: the ColorEdit* functions have a little color square that can be left-clicked to open a picker, and right-clicked to open an option menu.)
    static bool          ColorEdit3(const char* label, float col[3], ImGuiColorEditFlags flags = 0) { return im::ColorEdit3(label, col, flags); }
    static bool          ColorEdit4(const char* label, float col[4], ImGuiColorEditFlags flags = 0) { return im::ColorEdit4(label, col, flags); }
    static bool          ColorPicker3(const char* label, float col[3], ImGuiColorEditFlags flags = 0) { return im::ColorPicker3(label, col, flags); }
    static bool          ColorPicker4(const char* label, float col[4], ImGuiColorEditFlags flags = 0, const float* ref_col = NULL) { return im::ColorPicker4(label, col, flags); }
    static bool          ColorButton(const char* desc_id, const ImVec4& col, ImGuiColorEditFlags flags = 0, const ImVec2& size = ImVec2(0, 0)) { return im::ColorButton(desc_id, col, flags); }
    // Widgets: Trees
    static bool          TreeNode(const char* label) { return im::TreeNode(label); }
    static bool          TreeNode(const char* str_id, const char* fmt) { return im::TreeNode(str_id, fmt); }
    static bool          TreeNode(const void* ptr_id, const char* fmt) { return im::TreeNode(ptr_id, fmt); }
    static bool          TreeNodeV(const char* str_id, const char* fmt, va_list args) { return im::TreeNodeV(str_id, fmt, args); }
    static bool          TreeNodeV(const void* ptr_id, const char* fmt, va_list args) { return im::TreeNodeV(ptr_id, fmt, args); }
    static bool          TreeNodeEx(const char* label, ImGuiTreeNodeFlags flags = 0) { return im::TreeNodeEx(label, flags); }
    static bool          TreeNodeEx(const char* str_id, ImGuiTreeNodeFlags flags, const char* fmt) { return im::TreeNodeEx(str_id, flags, fmt); }
    static bool          TreeNodeEx(const void* ptr_id, ImGuiTreeNodeFlags flags, const char* fmt) { return im::TreeNodeEx(ptr_id, flags, fmt); }
    static bool          TreeNodeExV(const char* str_id, ImGuiTreeNodeFlags flags, const char* fmt, va_list args) { return im::TreeNodeExV(str_id, flags, fmt, args); }
    static bool          TreeNodeExV(const void* ptr_id, ImGuiTreeNodeFlags flags, const char* fmt, va_list args) { return im::TreeNodeExV(ptr_id, flags, fmt, args); }
    static void          TreePush(const char* str_id) { im::TreePush(str_id); }
    static void          TreePush(const void* ptr_id) { im::TreePush(ptr_id); }
    static void          TreePop() { im::TreePop(); }
    static float         GetTreeNodeToLabelSpacing() { return im::GetTreeNodeToLabelSpacing(); }
    static bool          TreeNodeGetOpen(ImGuiID storage_id) { return im::TreeNodeGetOpen(storage_id); }
    static bool          CollapsingHeader(const char* label, ImGuiTreeNodeFlags flags = 0) { return im::CollapsingHeader(label, flags); }
    static bool          CollapsingHeader(const char* label, bool* p_visible, ImGuiTreeNodeFlags flags = 0) { return im::CollapsingHeader(label, p_visible, flags); }
    static void          SetNextItemOpen(bool is_open, ImGuiCond cond = 0) { im::SetNextItemOpen(is_open, cond); }
    static void          SetNextItemStorageID(ImGuiID storage_id) { im::SetNextItemStorageID(storage_id); }

    // Widgets: Selectables
    static bool          Selectable(const char* label, bool selected = false, ImGuiSelectableFlags flags = 0, const ImVec2& size = ImVec2(0, 0)) { return im::Selectable(label, selected, flags, size); }
    static bool          Selectable(const char* label, bool* p_selected, ImGuiSelectableFlags flags = 0, const ImVec2& size = ImVec2(0, 0)) { return im::Selectable(label, p_selected, flags, size); }

    // Multi-selection system for Selectable(), Checkbox(), TreeNode() functions [BETA]
    static ImGuiMultiSelectIO*   BeginMultiSelect(ImGuiMultiSelectFlags flags, int selection_size = -1, int items_count = -1) { return im::BeginMultiSelect(flags, selection_size, items_count); }
    static ImGuiMultiSelectIO*   EndMultiSelect() { return im::EndMultiSelect(); }
    static void                  SetNextItemSelectionUserData(ImGuiSelectionUserData selection_user_data) { im::SetNextItemSelectionUserData(selection_user_data); }
    static bool                  IsItemToggledSelection() { return im::IsItemToggledSelection(); }

    // Widgets: List Boxes
    static bool          BeginListBox(const char* label, const ImVec2& size = ImVec2(0, 0)) { return im::BeginListBox(label, size); }
    static void          EndListBox() { im::EndListBox(); }
    static bool          ListBox(const char* label, int* current_item, const char* const items[], int items_count, int height_in_items = -1) { return im::ListBox(label, current_item, items, items_count, height_in_items); }
//    bool          ListBox(const char* label, int* current_item, const char* (*getter)(void* user_data, int idx), void* user_data, int items_count, int height_in_items = -1);

    // Widgets: Data Plotting
    static void          PlotLines(const char* label, const float* values, int values_count, int values_offset = 0, const char* overlay_text = NULL, float scale_min = FLT_MAX, float scale_max = FLT_MAX, ImVec2 graph_size = ImVec2(0, 0), int stride = sizeof(float)) {
        return im::PlotLines(label, values, values_count, values_offset, overlay_text, scale_min, scale_max, graph_size, stride);
    }
//    void          PlotLines(const char* label, float(*values_getter)(void* data, int idx), void* data, int values_count, int values_offset = 0, const char* overlay_text = NULL, float scale_min = FLT_MAX, float scale_max = FLT_MAX, ImVec2 graph_size = ImVec2(0, 0));
    static void          PlotHistogram(const char* label, const float* values, int values_count, int values_offset = 0, const char* overlay_text = NULL, float scale_min = FLT_MAX, float scale_max = FLT_MAX, ImVec2 graph_size = ImVec2(0, 0), int stride = sizeof(float)) {
       return im::PlotHistogram(label, values, values_count, values_offset, overlay_text, scale_min, scale_max, graph_size, stride);
    }
//    void          PlotHistogram(const char* label, float (*values_getter)(void* data, int idx), void* data, int values_count, int values_offset = 0, const char* overlay_text = NULL, float scale_min = FLT_MAX, float scale_max = FLT_MAX, ImVec2 graph_size = ImVec2(0, 0));

    // Widgets: Value() Helpers.
    static void          Value(const char* prefix, bool b) { return im::Value(prefix, b); }
    static void          Value(const char* prefix, int v) { return im::Value(prefix, v); }
    static void          Value(const char* prefix, unsigned int v) { return im::Value(prefix, v); }
    static void          Value(const char* prefix, float v, const char* float_format = NULL) { return im::Value(prefix, v, float_format); }

    // Widgets: Menus
    static bool          BeginMenuBar() { return im::BeginMenuBar(); }
    static void          EndMenuBar() { im::EndMenuBar(); }
    static bool          BeginMainMenuBar() { return im::BeginMainMenuBar(); }
    static void          EndMainMenuBar() { im::EndMainMenuBar(); }
    static bool          BeginMenu(const char* label, bool enabled = true) { return im::BeginMenu(label, enabled); }
    static void          EndMenu() { im::EndMenu(); }
    static bool          MenuItem(const char* label, const char* shortcut = NULL, bool selected = false, bool enabled = true) { return im::MenuItem(label, shortcut, selected, enabled); }
    static bool          MenuItem(const char* label, const char* shortcut, bool* p_selected, bool enabled = true) { return im::MenuItem(label, shortcut, p_selected, enabled); }

    // Tooltips
    static bool          BeginTooltip() { return im::BeginTooltip(); }
    static void          EndTooltip() { im::EndTooltip(); }
    static void          SetTooltip(const char* fmt) { im::SetTooltip(fmt); }
    static void          SetTooltipV(const char* fmt, va_list args) { im::SetTooltipV(fmt, args); }

    // Tooltips: helpers for showing a tooltip when hovering an item
    static bool          BeginItemTooltip() { return im::BeginItemTooltip(); }
    static void          SetItemTooltip(const char* fmt) { im::SetItemTooltip(fmt); }
    static void          SetItemTooltipV(const char* fmt, va_list args) { im::SetItemTooltipV(fmt, args); }

    // Popups, Modals
    static bool          BeginPopup(const char* str_id, ImGuiWindowFlags flags = 0) { return im::BeginPopup(str_id, flags); }
    static bool          BeginPopupModal(const char* name, bool* p_open = NULL, ImGuiWindowFlags flags = 0) { return im::BeginPopupModal(name, p_open, flags); }
    static void          EndPopup() { im::EndPopup(); }

    // Popups: open/close functions
    static bool          OpenPopup(const char* str_id, ImGuiPopupFlags popup_flags = 0) { return im::OpenPopup(str_id, popup_flags); }
    static bool          OpenPopup(ImGuiID id, ImGuiPopupFlags popup_flags = 0) { return im::OpenPopup(id, popup_flags); }
    static bool          OpenPopupOnItemClick(const char* str_id = NULL, ImGuiPopupFlags popup_flags = 0) { return im::OpenPopupOnItemClick(str_id, popup_flags); }
    static void          CloseCurrentPopup() { im::CloseCurrentPopup(); }

    // Popups: open+begin combined functions helpers
    static bool          BeginPopupContextItem(const char* str_id = NULL, ImGuiPopupFlags popup_flags = 0) { return im::BeginPopupContextItem(str_id, popup_flags); }
    static bool          BeginPopupContextWindow(const char* str_id = NULL, ImGuiPopupFlags popup_flags = 0) { return im::BeginPopupContextWindow(str_id, popup_flags); }
    static bool          BeginPopupContextVoid(const char* str_id = NULL, ImGuiPopupFlags popup_flags = 0) { return im::BeginPopupContextVoid(str_id, popup_flags); }

    // Popups: query functions
    static bool          IsPopupOpen(const char* str_id, ImGuiPopupFlags flags = 0) { return im::IsPopupOpen(str_id, flags); }

    // Tables
    static bool          BeginTable(const char* str_id, int columns, ImGuiTableFlags flags = 0, const ImVec2& outer_size = ImVec2(0.0f, 0.0f), float inner_width = 0.0f) { return im::BeginTable(str_id, columns, flags, outer_size, inner_width); }
    static void          EndTable() { im::EndTable(); }
    static void          TableNextRow(ImGuiTableRowFlags row_flags = 0, float min_row_height = 0.0f) { im::TableNextRow(row_flags, min_row_height); }
    static bool          TableNextColumn() { return im::TableNextColumn(); }
    static bool          TableSetColumnIndex(int column_n) { return im::TableSetColumnIndex(column_n); }

    // Tables: Headers & Columns declaration
    static void          TableSetupColumn(const char* label, ImGuiTableColumnFlags flags = 0, float init_width_or_weight = 0.0f, ImGuiID user_data = 0) { im::TableSetupColumn(label, flags, init_width_or_weight, user_data); }
    static void          TableSetupScrollFreeze(int cols, int rows) { im::TableSetupScrollFreeze(cols, rows); }
    static void          TableHeader(const char* label) { im::TableHeader(label); }
    static void          TableHeadersRow() { im::TableHeadersRow(); }
    static void          TableAngledHeadersRow() { im::TableAngledHeadersRow(); }

    // Tables: Sorting & Miscellaneous functions
    static ImGuiTableSortSpecs*  TableGetSortSpecs() { return im::TableGetSortSpecs(); }
    static int                   TableGetColumnCount() { return im::TableGetColumnCount(); }
    static int                   TableGetColumnIndex() { return im::TableGetColumnIndex(); }
    static int                   TableGetRowIndex() { return im::TableGetRowIndex(); }
    static const char*           TableGetColumnName(int column_n = -1) { return im::TableGetColumnName(column_n); }
    static ImGuiTableColumnFlags TableGetColumnFlags(int column_n = -1) { return im::TableGetColumnFlags(column_n); }
    static void                  TableSetColumnEnabled(int column_n, bool v) { return im::TableSetColumnEnabled(column_n, v); }
    static int                   TableGetHoveredColumn() { return im::TableGetHoveredColumn(); }
    static void                  TableSetBgColor(ImGuiTableBgTarget target, ImU32 color, int column_n = -1) { im::TableSetBgColor(target, color, column_n); }

    // Legacy Columns API (prefer using Tables!)
    static void          Columns(int count = 1, const char* id = NULL, bool borders = true) { im::Columns(count, id, borders); }
    static void          NextColumn() { im::NextColumn(); }
    static int           GetColumnIndex() { return im::GetColumnIndex(); }
    static float         GetColumnWidth(int column_index = -1) { return im::GetColumnWidth(column_index); }
    static void          SetColumnWidth(int column_index, float width) { im::SetColumnWidth(column_index, width); }
    static float         GetColumnOffset(int column_index = -1) { return im::GetColumnOffset(column_index); }
    static void          SetColumnOffset(int column_index, float offset_x) { im::SetColumnOffset(column_index, offset_x); }
    static int           GetColumnsCount() { return im::GetColumnsCount(); }

    // Tab Bars, Tabs
    static bool          BeginTabBar(const char* str_id, ImGuiTabBarFlags flags = 0) { return im::BeginTabBar(str_id, flags); }
    static void          EndTabBar() { im::EndTabBar(); }
    static bool          BeginTabItem(const char* label, bool* p_open = NULL, ImGuiTabItemFlags flags = 0) { return im::BeginTabItem(label, p_open, flags); }
    static void          EndTabItem() { im::EndTabItem(); }
    static bool          TabItemButton(const char* label, ImGuiTabItemFlags flags = 0) { return im::TabItemButton(label, flags); }
    static void          SetTabItemClosed(const char* tab_or_docked_window_label) { im::SetTabItemClosed(tab_or_docked_window_label); }

    // Docking
    static ImGuiID       DockSpace(ImGuiID dockspace_id, const ImVec2& size = ImVec2(0, 0), ImGuiDockNodeFlags flags = 0, const ImGuiWindowClass* window_class = NULL) { return im::DockSpace(dockspace_id, size, flags, window_class); }
    static ImGuiID       DockSpaceOverViewport(ImGuiID dockspace_id = 0, const ImGuiViewport* viewport = NULL, ImGuiDockNodeFlags flags = 0, const ImGuiWindowClass* window_class = NULL) { return im::DockSpaceOverViewport(dockspace_id, viewport, flags, window_class); }
    static void          SetNextWindowDockID(ImGuiID dock_id, ImGuiCond cond = 0) { im::SetNextWindowDockID(dock_id, cond); }
    static void          SetNextWindowClass(const ImGuiWindowClass* window_class) { im::SetNextWindowClass(window_class); }
    static ImGuiID       GetWindowDockID() { return im::GetWindowDockID(); }
    static bool          IsWindowDocked() { return im::IsWindowDocked(); }

    // Logging/Capture
    static void          LogToTTY(int auto_open_depth = -1) { im::LogToTTY(auto_open_depth); }
    static void          LogToFile(int auto_open_depth = -1, const char* filename = NULL) { im::LogToFile(auto_open_depth, filename); }
    static void          LogToClipboard(int auto_open_depth = -1) { im::LogToClipboard(auto_open_depth); }
    static void          LogFinish() { im::LogFinish(); }
    static void          LogButtons() { im::LogButtons(); }
    static void          LogText(const char* fmt) { im::LogText(fmt); }
    static void          LogTextV(const char* fmt, va_list args) { im::LogTextV(fmt, args); }

    // Drag and Drop
    static bool          BeginDragDropSource(ImGuiDragDropFlags flags = 0) { return im::BeginDragDropSource(flags); }
    static bool          SetDragDropPayload(const char* type, const void* data, size_t sz, ImGuiCond cond = 0) { return im::SetDragDropPayload(type, data, sz, cond); }
    static void          EndDragDropSource() { im::EndDragDropSource(); }
    static bool                  BeginDragDropTarget() { return im::BeginDragDropTarget(); }
    static const ImGuiPayload*   AcceptDragDropPayload(const char* type, ImGuiDragDropFlags flags = 0) { return im::AcceptDragDropPayload(type, flags); }
    static void                  EndDragDropTarget() { im::EndDragDropTarget(); }
    static const ImGuiPayload*   GetDragDropPayload() { return im::GetDragDropPayload(); }

    // Disabling [BETA API]
    static void          BeginDisabled(bool disabled = true) { im::BeginDisabled(disabled); }
    static void          EndDisabled() { im::EndDisabled(); }

    // Clipping
    static void          PushClipRect(const ImVec2& clip_rect_min, const ImVec2& clip_rect_max, bool intersect_with_current_clip_rect) { im::PushClipRect(clip_rect_min, clip_rect_max, intersect_with_current_clip_rect); }
    static void          PopClipRect() { im::PopClipRect(); }

    // Focus, Activation
    static void          SetItemDefaultFocus() { im::SetItemDefaultFocus(); }
    static void          SetKeyboardFocusHere(int offset = 0) { im::SetKeyboardFocusHere(offset); }

    // Keyboard/Gamepad Navigation
    static void          SetNavCursorVisible(bool visible) { im::SetNavCursorVisible(visible); }

    // Overlapping mode
    static void          SetNextItemAllowOverlap() { im::SetNextItemAllowOverlap(); }

    // Item/Widgets Utilities and Query Functions
    static bool          IsItemHovered(ImGuiHoveredFlags flags = 0) { return im::IsItemHovered(flags); }
    static bool          IsItemActive() { return im::IsItemActive(); }
    static bool          IsItemFocused() { return im::IsItemFocused(); }
    static bool          IsItemClicked(ImGuiMouseButton mouse_button = 0) { return im::IsItemClicked(mouse_button); }
    static bool          IsItemVisible() { return im::IsItemVisible(); }
    static bool          IsItemEdited() { return im::IsItemEdited(); }
    static bool          IsItemActivated() { return im::IsItemActivated(); }
    static bool          IsItemDeactivated() { return im::IsItemDeactivated(); }
    static bool          IsItemDeactivatedAfterEdit() { return im::IsItemDeactivatedAfterEdit(); }
    static bool          IsItemToggledOpen() { return im::IsItemToggledOpen(); }
    static bool          IsAnyItemHovered() { return im::IsAnyItemHovered(); }
    static bool          IsAnyItemActive() { return im::IsAnyItemActive(); }
    static bool          IsAnyItemFocused() { return im::IsAnyItemFocused(); }
    static ImGuiID       GetItemID() { return im::GetItemID(); }
    static ImGuiItemFlags GetItemFlags() { return im::GetItemFlags(); }
    static ImVec2        GetItemRectMin() { return im::GetItemRectMin(); }
    static ImVec2        GetItemRectMax() { return im::GetItemRectMax(); }
    static ImVec2        GetItemRectSize() { return im::GetItemRectSize(); }
    static int           GetItemClickedCountWithSingleClickDelay(ImGuiMouseButton mouse_button = 0, float delay = -1.0f) { return im::GetItemClickedCountWithSingleClickDelay(mouse_button, delay); }

    // Viewports
    static ImGuiViewport* GetMainViewport() { return im::GetMainViewport(); }

    // Background/Foreground Draw Lists
    static ImDrawList*   GetBackgroundDrawList(ImGuiViewport* viewport = NULL) { return im::GetBackgroundDrawList(viewport); }
    static ImDrawList*   GetForegroundDrawList(ImGuiViewport* viewport = NULL) { return im::GetForegroundDrawList(viewport); }

    // Miscellaneous Utilities
    static bool          IsRectVisible(const ImVec2& size) { return im::IsRectVisible(size); }
    static bool          IsRectVisible(const ImVec2& rect_min, const ImVec2& rect_max) { return im::IsRectVisible(rect_min, rect_max); }
    static double        GetTime() { return im::GetTime(); }
    static int           GetFrameCount() { return im::GetFrameCount(); }
    static ImDrawListSharedData* GetDrawListSharedData() { return im::GetDrawListSharedData(); }
    static const char*   GetStyleColorName(ImGuiCol idx) { return im::GetStyleColorName(idx); }
    static void          SetStateStorage(ImGuiStorage* storage) { im::SetStateStorage(storage); }
    static ImGuiStorage* GetStateStorage() { return im::GetStateStorage(); }

    // Text Utilities
    static ImVec2        CalcTextSize(const char* text, const char* text_end = NULL, bool hide_text_after_double_hash = false, float wrap_width = -1.0f) { return im::CalcTextSize(text, text_end, hide_text_after_double_hash, wrap_width); }

    // Color Utilities
    static ImVec4        ColorConvertU32ToFloat4(ImU32 in) { return im::ColorConvertU32ToFloat4(in); }
    static ImU32         ColorConvertFloat4ToU32(const ImVec4& in) { return im::ColorConvertFloat4ToU32(in); }
    static void          ColorConvertRGBtoHSV(float r, float g, float b, float* out_h, float* out_s, float* out_v) { im::ColorConvertRGBtoHSV(r, g, b, *out_h, *out_s, *out_v); } // TODO changed "out" to be a pointer
    static void          ColorConvertHSVtoRGB(float h, float s, float v, float* out_r, float* out_g, float* out_b) { im::ColorConvertHSVtoRGB(h, s, v, *out_r, *out_g, *out_b); } // TODO changed "out" to be a pointer

    // Inputs Utilities: Keyboard/Mouse/Gamepad
    static bool          IsKeyDown(ImGuiKey key) { return im::IsKeyDown(key); }
    static bool          IsKeyPressed(ImGuiKey key, bool repeat = true) { return im::IsKeyPressed(key, repeat); }
    static bool          IsKeyReleased(ImGuiKey key) { return im::IsKeyReleased(key); }
    static bool          IsKeyChordPressed(ImGuiKeyChord key_chord) { return im::IsKeyChordPressed(key_chord); }
    static int           GetKeyPressedAmount(ImGuiKey key, float repeat_delay, float rate) { return im::GetKeyPressedAmount(key, repeat_delay, rate); }
    static const char*   GetKeyName(ImGuiKey key) { return im::GetKeyName(key); }
    static void          SetNextFrameWantCaptureKeyboard(bool want_capture_keyboard) { im::SetNextFrameWantCaptureKeyboard(want_capture_keyboard); }

    // Inputs Utilities: Shortcut Testing & Routing [BETA]
    static bool          Shortcut(ImGuiKeyChord key_chord, ImGuiInputFlags flags = 0) { return im::Shortcut(key_chord, flags); }
    static void          SetNextItemShortcut(ImGuiKeyChord key_chord, ImGuiInputFlags flags = 0) { im::SetNextItemShortcut(key_chord, flags); }

    // Inputs Utilities: Key/Input Ownership [BETA]
    static bool          SetItemKeyOwner(ImGuiKey key) { return im::SetItemKeyOwner(key); }

    // Inputs Utilities: Mouse
    static bool          IsMouseDown(ImGuiMouseButton button) { return im::IsMouseDown(button); }
    static bool          IsMouseClicked(ImGuiMouseButton button, bool repeat = false) { return im::IsMouseClicked(button, repeat); }
    static bool          IsMouseReleased(ImGuiMouseButton button) { return im::IsMouseReleased(button); }
    static bool          IsMouseDoubleClicked(ImGuiMouseButton button) { return im::IsMouseDoubleClicked(button); }
    static bool          IsMouseReleasedWithDelay(ImGuiMouseButton button, float delay = -1.0f) { return im::IsMouseReleasedWithDelay(button, delay); }
    static int           GetMouseClickedCount(ImGuiMouseButton button) { return im::GetMouseClickedCount(button); }
    static bool          IsMouseHoveringRect(const ImVec2& r_min, const ImVec2& r_max, bool clip = true) { return im::IsMouseHoveringRect(r_min, r_max, clip); }
    static bool          IsMousePosValid(const ImVec2* mouse_pos = NULL) { return im::IsMousePosValid(mouse_pos); }
    static bool          IsAnyMouseDown() { return im::IsAnyMouseDown(); }
    static ImVec2        GetMousePos() { return im::GetMousePos(); }
    static ImVec2        GetMousePosOnOpeningCurrentPopup() { return im::GetMousePosOnOpeningCurrentPopup(); }
    static bool          IsMouseDragging(ImGuiMouseButton button, float lock_threshold = -1.0f) { return im::IsMouseDragging(button, lock_threshold); }
    static ImVec2        GetMouseDragDelta(ImGuiMouseButton button = 0, float lock_threshold = -1.0f) { return im::GetMouseDragDelta(button, lock_threshold); }
    static void          ResetMouseDragDelta(ImGuiMouseButton button = 0) { im::ResetMouseDragDelta(button); }
    static ImGuiMouseCursor GetMouseCursor() { return im::GetMouseCursor(); }
    static void          SetMouseCursor(ImGuiMouseCursor cursor_type) { im::SetMouseCursor(cursor_type); }
    static void          SetNextFrameWantCaptureMouse(bool want_capture_mouse) { im::SetNextFrameWantCaptureMouse(want_capture_mouse); }

    // Clipboard Utilities
    static const char*   GetClipboardText() { return im::GetClipboardText(); }
    static void          SetClipboardText(const char* text) { im::SetClipboardText(text); }

    // Settings/.Ini Utilities
    static void          LoadIniSettingsFromDisk(const char* ini_filename) { im::LoadIniSettingsFromDisk(ini_filename); }
    static void          LoadIniSettingsFromMemory(const char* ini_data, size_t ini_size=0) { im::LoadIniSettingsFromMemory(ini_data, ini_size); }
    static void          SaveIniSettingsToDisk(const char* ini_filename) { im::SaveIniSettingsToDisk(ini_filename); }
    static const char*   SaveIniSettingsToMemory(size_t* out_ini_size = NULL) { return im::SaveIniSettingsToMemory(out_ini_size); }

    // Debug Utilities
    static void          DebugTextEncoding(const char* text) { im::DebugTextEncoding(text); }
    static void          DebugFlashStyleColor(ImGuiCol idx) { im::DebugFlashStyleColor(idx); }
    static void          DebugStartItemPicker() { im::DebugStartItemPicker(); }
    static bool          DebugCheckVersionAndDataLayout(const char* version_str, size_t sz_io, size_t sz_style, size_t sz_vec2, size_t sz_vec4, size_t sz_drawvert, size_t sz_drawidx) { return im::DebugCheckVersionAndDataLayout(version_str, sz_io, sz_style, sz_vec2, sz_vec4, sz_drawvert, sz_drawidx); }

    // Memory Allocators
    static void          SetAllocatorFunctions(ImGuiMemAllocFunc alloc_func, ImGuiMemFreeFunc free_func, void* user_data = NULL) { im::SetAllocatorFunctions(alloc_func, free_func, user_data); }
    static void          GetAllocatorFunctions(ImGuiMemAllocFunc* p_alloc_func, ImGuiMemFreeFunc* p_free_func, void** p_user_data) { im::GetAllocatorFunctions(p_alloc_func, p_free_func, p_user_data); }
    static void*         MemAlloc(size_t size) { return im::MemAlloc(size); }
    static void          MemFree(void* ptr) { im::MemFree( ptr); }

    // (Optional) Platform/OS interface for multi-viewport support
    static void          UpdatePlatformWindows() { im::UpdatePlatformWindows(); }
    static void          RenderPlatformWindowsDefault(void* platform_render_arg = NULL, void* renderer_render_arg = NULL) { im::RenderPlatformWindowsDefault(platform_render_arg, renderer_render_arg); }
    static void          DestroyPlatformWindows() { im::DestroyPlatformWindows(); }
    static ImGuiViewport* FindViewportByID(ImGuiID id) { return im::FindViewportByID(id); }
    static ImGuiViewport* FindViewportByPlatformHandle(void* platform_handle) { return im::FindViewportByPlatformHandle(platform_handle); }
};

} // END ImGuiWrapper

enum ImGuiDrawCallbackType {
    ImGuiDrawCallbackType_None,
    ImGuiDrawCallbackType_ResetRenderState,
    ImGuiDrawCallbackType_SetSamplerLinear,
    ImGuiDrawCallbackType_SetSamplerNearest,
    ImGuiDrawCallbackType_User
};

class ImGuiDrawCallbacks
{
    private:
        // Keep marker bodies observably distinct. Linkers may fold identical empty
        // functions, which would make callback identity comparisons ambiguous.
        static void resetRenderState(const ImDrawList*, const ImDrawCmd*) {
            volatile int marker = 0;
            (void)marker;
        }
        static void setSamplerLinear(const ImDrawList*, const ImDrawCmd*) {
            volatile int marker = 1;
            (void)marker;
        }
        static void setSamplerNearest(const ImDrawList*, const ImDrawCmd*) {
            volatile int marker = 2;
            (void)marker;
        }

        static ImDrawCallback callbackForType(ImGuiPlatformIO* platform_io, ImGuiDrawCallbackType type) {
            if (platform_io == nullptr)
                return nullptr;
            switch (type) {
                case ImGuiDrawCallbackType_ResetRenderState:
                    return platform_io->DrawCallback_ResetRenderState;
                case ImGuiDrawCallbackType_SetSamplerLinear:
                    return platform_io->DrawCallback_SetSamplerLinear;
                case ImGuiDrawCallbackType_SetSamplerNearest:
                    return platform_io->DrawCallback_SetSamplerNearest;
                default:
                    return nullptr;
            }
        }

    public:
        static void InstallStandardCallbacks(ImGuiPlatformIO* platform_io) {
            if (platform_io == nullptr)
                return;
            platform_io->DrawCallback_ResetRenderState = resetRenderState;
            platform_io->DrawCallback_SetSamplerLinear = setSamplerLinear;
            platform_io->DrawCallback_SetSamplerNearest = setSamplerNearest;
        }

        static void ClearStandardCallbacks(ImGuiPlatformIO* platform_io) {
            if (platform_io == nullptr)
                return;
            if (platform_io->DrawCallback_ResetRenderState == resetRenderState)
                platform_io->DrawCallback_ResetRenderState = nullptr;
            if (platform_io->DrawCallback_SetSamplerLinear == setSamplerLinear)
                platform_io->DrawCallback_SetSamplerLinear = nullptr;
            if (platform_io->DrawCallback_SetSamplerNearest == setSamplerNearest)
                platform_io->DrawCallback_SetSamplerNearest = nullptr;
        }

        static ImGuiDrawCallbackType GetType(const ImDrawCmd* draw_cmd) {
            if (draw_cmd == nullptr || draw_cmd->UserCallback == nullptr)
                return ImGuiDrawCallbackType_None;
            if (draw_cmd->UserCallback == resetRenderState)
                return ImGuiDrawCallbackType_ResetRenderState;
            if (draw_cmd->UserCallback == setSamplerLinear)
                return ImGuiDrawCallbackType_SetSamplerLinear;
            if (draw_cmd->UserCallback == setSamplerNearest)
                return ImGuiDrawCallbackType_SetSamplerNearest;
            return ImGuiDrawCallbackType_User;
        }

        static bool AddStandardCallback(ImDrawList* draw_list, ImGuiDrawCallbackType type) {
            if (draw_list == nullptr || ImGui::GetCurrentContext() == nullptr)
                return false;
            ImGuiPlatformIO& platform_io = ImGui::GetPlatformIO();
            ImDrawCallback callback = callbackForType(&platform_io, type);
            if (callback == nullptr)
                return false;
            draw_list->AddCallback(callback);
            return true;
        }

        static void InvokeUserCallback(const ImDrawList* parent_list, const ImDrawCmd* draw_cmd) {
            if (parent_list == nullptr || draw_cmd == nullptr || GetType(draw_cmd) != ImGuiDrawCallbackType_User)
                return;
            draw_cmd->UserCallback(parent_list, draw_cmd);
        }
};

#if defined(JIMGUI_ENABLE_TEST_HOOKS) && defined(_JAVASOFT_JNI_H_)
namespace {
    static int ImGuiDrawCallbackTestCount = 0;

    static void ImGuiDrawCallbackTestProbe(const ImDrawList*, const ImDrawCmd*) {
        ++ImGuiDrawCallbackTestCount;
    }
}

extern "C" JNIEXPORT jboolean JNICALL Java_imgui_DrawCallbackIntegrationTest_addNativeUserCallback(
        JNIEnv*, jclass, jlong draw_list_addr) {
    ImDrawList* draw_list = reinterpret_cast<ImDrawList*>(draw_list_addr);
    if (draw_list == nullptr)
        return JNI_FALSE;
    draw_list->AddCallback(ImGuiDrawCallbackTestProbe);
    return JNI_TRUE;
}

extern "C" JNIEXPORT void JNICALL Java_imgui_DrawCallbackIntegrationTest_resetNativeUserCallbackCount(
        JNIEnv*, jclass) {
    ImGuiDrawCallbackTestCount = 0;
}

extern "C" JNIEXPORT jint JNICALL Java_imgui_DrawCallbackIntegrationTest_getNativeUserCallbackCount(
        JNIEnv*, jclass) {
    return ImGuiDrawCallbackTestCount;
}
#endif

class ClipboardTextFunction
{
    public:
        std::string text;
        virtual ~ClipboardTextFunction() {
        }

        virtual void onGetClipboardText(std::string* strOut) = 0;
        virtual void onSetClipboardText(std::string* text) = 0;

        static void setClipboardTextFunction(ImGuiPlatformIO* platform_io, ClipboardTextFunction * clipboardFunction) {
            auto pointer = reinterpret_cast<std::uintptr_t>(clipboardFunction);
            platform_io->Platform_ClipboardUserData = (void*)pointer;
            platform_io->Platform_SetClipboardTextFn = [](ImGuiContext* context, const char* text) {
                void* user_data = context->PlatformIO.Platform_ClipboardUserData;
                auto addr = reinterpret_cast<std::uintptr_t>(user_data);
                ClipboardTextFunction* clipboardFunction = reinterpret_cast<ClipboardTextFunction*>(addr);
                std::string& str = clipboardFunction->text;
                str = text;
                clipboardFunction->onSetClipboardText(&str);

            };
            platform_io->Platform_GetClipboardTextFn = [](ImGuiContext* context) {
                void* user_data = context->PlatformIO.Platform_ClipboardUserData;
                auto addr = reinterpret_cast<std::uintptr_t>(user_data);
                ClipboardTextFunction* clipboardFunction = reinterpret_cast<ClipboardTextFunction*>(addr);
                std::string& str = clipboardFunction->text;
                str.clear();
                clipboardFunction->onGetClipboardText(&str);
                return str.c_str();
            };
        }
};

class ImGuiViewportPlatformCallbacks
{
    private:
        static ImGuiViewportPlatformCallbacks*& activeCallbacks() {
            static ImGuiViewportPlatformCallbacks* callbacks = nullptr;
            return callbacks;
        }

        static void* callbackPointer(ImGuiViewportPlatformCallbacks* callbacks) {
            auto pointer = reinterpret_cast<std::uintptr_t>(callbacks);
            return reinterpret_cast<void*>(pointer);
        }

        static ImGuiViewportPlatformCallbacks* callbackFromViewport(ImGuiViewport* viewport) {
            if (viewport != nullptr && viewport->PlatformUserData != nullptr) {
                auto addr = reinterpret_cast<std::uintptr_t>(viewport->PlatformUserData);
                return reinterpret_cast<ImGuiViewportPlatformCallbacks*>(addr);
            }
            return activeCallbacks();
        }

        static void assignViewportHandles(ImGuiViewport* viewport, ImGuiViewportPlatformCallbacks* callbacks) {
            if (viewport == nullptr || callbacks == nullptr) {
                return;
            }
            void* pointer = callbackPointer(callbacks);
            viewport->PlatformUserData = pointer;
            viewport->PlatformHandle = pointer;
        }

        static void clearViewportHandlers(ImGuiPlatformIO* platform_io) {
            platform_io->Platform_CreateWindow = nullptr;
            platform_io->Platform_DestroyWindow = nullptr;
            platform_io->Platform_ShowWindow = nullptr;
            platform_io->Platform_SetWindowPos = nullptr;
            platform_io->Platform_GetWindowPos = nullptr;
            platform_io->Platform_SetWindowSize = nullptr;
            platform_io->Platform_GetWindowSize = nullptr;
            platform_io->Platform_GetWindowFramebufferScale = nullptr;
            platform_io->Platform_SetWindowFocus = nullptr;
            platform_io->Platform_GetWindowFocus = nullptr;
            platform_io->Platform_GetWindowMinimized = nullptr;
            platform_io->Platform_SetWindowTitle = nullptr;
            platform_io->Platform_SetWindowAlpha = nullptr;
            platform_io->Platform_UpdateWindow = nullptr;
            platform_io->Platform_RenderWindow = nullptr;
            platform_io->Platform_SwapBuffers = nullptr;
            platform_io->Platform_GetWindowDpiScale = nullptr;
            platform_io->Platform_OnChangedViewport = nullptr;
            platform_io->Platform_GetWindowWorkAreaInsets = nullptr;
            platform_io->Platform_CreateVkSurface = nullptr;
            platform_io->Renderer_CreateWindow = nullptr;
            platform_io->Renderer_DestroyWindow = nullptr;
            platform_io->Renderer_SetWindowSize = nullptr;
            platform_io->Renderer_RenderWindow = nullptr;
            platform_io->Renderer_SwapBuffers = nullptr;
        }

    public:
        virtual ~ImGuiViewportPlatformCallbacks() {
        }

        virtual void onCreateWindow(ImGuiViewport* viewport) = 0;
        virtual void onDestroyWindow(ImGuiViewport* viewport) = 0;
        virtual void onShowWindow(ImGuiViewport* viewport) = 0;
        virtual void onSetWindowPos(ImGuiViewport* viewport, float x, float y) = 0;
        virtual float onGetWindowPosX(ImGuiViewport* viewport) = 0;
        virtual float onGetWindowPosY(ImGuiViewport* viewport) = 0;
        virtual void onSetWindowSize(ImGuiViewport* viewport, float width, float height) = 0;
        virtual float onGetWindowSizeX(ImGuiViewport* viewport) = 0;
        virtual float onGetWindowSizeY(ImGuiViewport* viewport) = 0;
        virtual float onGetWindowFramebufferScaleX(ImGuiViewport* viewport) = 0;
        virtual float onGetWindowFramebufferScaleY(ImGuiViewport* viewport) = 0;
        virtual void onSetWindowFocus(ImGuiViewport* viewport) = 0;
        virtual bool onGetWindowFocus(ImGuiViewport* viewport) = 0;
        virtual bool onGetWindowMinimized(ImGuiViewport* viewport) = 0;
        virtual void onSetWindowTitle(ImGuiViewport* viewport, std::string* title) = 0;
        virtual void onSetWindowAlpha(ImGuiViewport* viewport, float alpha) = 0;
        virtual void onUpdateWindow(ImGuiViewport* viewport) = 0;
        virtual void onRenderWindow(ImGuiViewport* viewport) = 0;
        virtual void onSwapBuffers(ImGuiViewport* viewport) = 0;
        virtual float onGetWindowDpiScale(ImGuiViewport* viewport) = 0;
        virtual void onChangedViewport(ImGuiViewport* viewport) = 0;
        virtual void onRendererCreateWindow(ImGuiViewport* viewport) = 0;
        virtual void onRendererDestroyWindow(ImGuiViewport* viewport) = 0;
        virtual void onRendererSetWindowSize(ImGuiViewport* viewport, float width, float height) = 0;
        virtual void onRendererRenderWindow(ImGuiViewport* viewport) = 0;
        virtual void onRendererSwapBuffers(ImGuiViewport* viewport) = 0;

        static void setCallbacks(ImGuiPlatformIO* platform_io, ImGuiViewportPlatformCallbacks* callbacks) {
            activeCallbacks() = callbacks;
            clearViewportHandlers(platform_io);
            platform_io->Platform_CreateWindow = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = activeCallbacks();
                assignViewportHandles(viewport, callbacks);
                if (callbacks != nullptr) {
                    callbacks->onCreateWindow(viewport);
                }
            };
            platform_io->Platform_DestroyWindow = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onDestroyWindow(viewport);
                }
                clearViewportPlatformHandles(viewport);
            };
            platform_io->Platform_ShowWindow = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onShowWindow(viewport);
                }
            };
            platform_io->Platform_SetWindowPos = [](ImGuiViewport* viewport, ImVec2 pos) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onSetWindowPos(viewport, pos.x, pos.y);
                }
            };
            platform_io->Platform_GetWindowPos = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks == nullptr) {
                    return ImVec2(0.0f, 0.0f);
                }
                return ImVec2(callbacks->onGetWindowPosX(viewport), callbacks->onGetWindowPosY(viewport));
            };
            platform_io->Platform_SetWindowSize = [](ImGuiViewport* viewport, ImVec2 size) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onSetWindowSize(viewport, size.x, size.y);
                }
            };
            platform_io->Platform_GetWindowSize = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks == nullptr) {
                    return ImVec2(0.0f, 0.0f);
                }
                return ImVec2(callbacks->onGetWindowSizeX(viewport), callbacks->onGetWindowSizeY(viewport));
            };
            platform_io->Platform_GetWindowFramebufferScale = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks == nullptr) {
                    return ImVec2(1.0f, 1.0f);
                }
                return ImVec2(callbacks->onGetWindowFramebufferScaleX(viewport), callbacks->onGetWindowFramebufferScaleY(viewport));
            };
            platform_io->Platform_SetWindowFocus = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onSetWindowFocus(viewport);
                }
            };
            platform_io->Platform_GetWindowFocus = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                return callbacks != nullptr ? callbacks->onGetWindowFocus(viewport) : false;
            };
            platform_io->Platform_GetWindowMinimized = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                return callbacks != nullptr ? callbacks->onGetWindowMinimized(viewport) : false;
            };
            platform_io->Platform_SetWindowTitle = [](ImGuiViewport* viewport, const char* title) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    std::string title_string = title != nullptr ? title : "";
                    callbacks->onSetWindowTitle(viewport, &title_string);
                }
            };
            platform_io->Platform_SetWindowAlpha = [](ImGuiViewport* viewport, float alpha) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onSetWindowAlpha(viewport, alpha);
                }
            };
            platform_io->Platform_UpdateWindow = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onUpdateWindow(viewport);
                }
            };
            platform_io->Platform_RenderWindow = [](ImGuiViewport* viewport, void*) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onRenderWindow(viewport);
                }
            };
            platform_io->Platform_SwapBuffers = [](ImGuiViewport* viewport, void*) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onSwapBuffers(viewport);
                }
            };
            platform_io->Platform_GetWindowDpiScale = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                return callbacks != nullptr ? callbacks->onGetWindowDpiScale(viewport) : 1.0f;
            };
            platform_io->Platform_OnChangedViewport = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onChangedViewport(viewport);
                }
            };
            platform_io->Renderer_CreateWindow = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onRendererCreateWindow(viewport);
                }
            };
            platform_io->Renderer_DestroyWindow = [](ImGuiViewport* viewport) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onRendererDestroyWindow(viewport);
                }
                if (viewport != nullptr) {
                    viewport->RendererUserData = nullptr;
                }
            };
            platform_io->Renderer_SetWindowSize = [](ImGuiViewport* viewport, ImVec2 size) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onRendererSetWindowSize(viewport, size.x, size.y);
                }
            };
            platform_io->Renderer_RenderWindow = [](ImGuiViewport* viewport, void*) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onRendererRenderWindow(viewport);
                }
            };
            platform_io->Renderer_SwapBuffers = [](ImGuiViewport* viewport, void*) {
                ImGuiViewportPlatformCallbacks* callbacks = callbackFromViewport(viewport);
                if (callbacks != nullptr) {
                    callbacks->onRendererSwapBuffers(viewport);
                }
            };
        }

        static void clearCallbacks(ImGuiPlatformIO* platform_io) {
            clearViewportHandlers(platform_io);
            activeCallbacks() = nullptr;
        }

        static void setMainMonitor(ImGuiPlatformIO* platform_io, float mainX, float mainY, float mainWidth, float mainHeight,
                                   float workX, float workY, float workWidth, float workHeight, float dpiScale) {
            platform_io->Monitors.resize(0);
            ImGuiPlatformMonitor monitor;
            monitor.MainPos = ImVec2(mainX, mainY);
            monitor.MainSize = ImVec2(mainWidth, mainHeight);
            monitor.WorkPos = ImVec2(workX, workY);
            monitor.WorkSize = ImVec2(workWidth, workHeight);
            monitor.DpiScale = dpiScale > 0.0f ? dpiScale : 1.0f;
            platform_io->Monitors.push_back(monitor);
        }

        static void setMainViewportPlatformHandle(ImGuiViewport* viewport, ImGuiViewportPlatformCallbacks* callbacks) {
            assignViewportHandles(viewport, callbacks);
        }

        static void clearViewportPlatformHandles(ImGuiViewport* viewport) {
            if (viewport == nullptr) {
                return;
            }
            viewport->RendererUserData = nullptr;
            viewport->PlatformUserData = nullptr;
            viewport->PlatformHandle = nullptr;
            viewport->PlatformHandleRaw = nullptr;
        }
};

static const char* ImGui_Impl_GetClipboardText(void* user_data) {
    auto addr = reinterpret_cast<std::uintptr_t>(user_data);
    ClipboardTextFunction* clipboardFunction = reinterpret_cast<ClipboardTextFunction*>(addr);
    std::string& str = clipboardFunction->text;
    str.clear();
    clipboardFunction->onGetClipboardText(&str);
    return str.c_str();
}

static void ImGui_Impl_SetClipboardText(void* user_data, const char* text) {
    auto addr = reinterpret_cast<std::uintptr_t>(user_data);
    ClipboardTextFunction* clipboardFunction = reinterpret_cast<ClipboardTextFunction*>(addr);
    std::string& str = clipboardFunction->text;
    str = text;
    clipboardFunction->onSetClipboardText(&str);
}

class ImTextureIDRef {
    private:
        ImTextureID value;

    public:
    ImTextureIDRef(ImTextureID v = 0) : value(v) {}
    ImTextureIDRef& operator=(ImTextureID v) { value = v; return *this; }
    operator ImTextureID() const { return value; }
    operator ImTextureID* () { return &value; }
    operator const ImTextureID* () const { return &value; }
    operator void* () const { return reinterpret_cast<void*>(const_cast<ImTextureID*>(&value)); }
    ImTextureID Get() const { return value; }
};




static ImVec2 vec2_temp1;
static ImVec2 vec2_temp2;
static ImVec2 vec2_temp3;
static ImVec2 vec2_temp4;

static ImVec4 vec4_temp1;
static ImVec4 vec4_temp2;
static ImVec4 vec4_temp3;
static ImVec4 vec4_temp4;

static ImRect rect_temp1;
static ImRect rect_temp2;
static ImRect rect_temp3;
static ImRect rect_temp4;

class ImTemp {
    public:

        static ImTextureIDRef* ImTextureIDRef_1(ImTextureID tex_id) {
            static ImTextureIDRef textureIDRef;
            textureIDRef = ImTextureIDRef(tex_id);
            return &textureIDRef;
        }

        static ImTextureRef* ImTextureRef_1(ImTextureID tex_id) {
            static ImTextureRef textureRef1;
            textureRef1._TexID = tex_id;
            return &textureRef1;
        }

        static ImTextureRef* ImTextureRef_2(ImTextureID tex_id) {
            static ImTextureRef ImTextureRef2;
            ImTextureRef2._TexID = tex_id;
            return &ImTextureRef2;
        }

        static ImTextureRef* ImTextureRef_3(ImTextureID tex_id) {
            static ImTextureRef textureRef3;
            textureRef3._TexID = tex_id;
            return &textureRef3;
        }

        static ImVec2* ImVec2_1() {
            vec2_temp1.x = 0;
            vec2_temp1.y = 0;
            return &vec2_temp1;
        }
        static ImVec2* ImVec2_1(float x, float y) {
            vec2_temp1.x = x;
            vec2_temp1.y = y;
            return &vec2_temp1;
        }
        static ImVec2* ImVec2_1(ImVec2& other) {
            vec2_temp2.x = other.x;
            vec2_temp2.y = other.y;
            return &vec2_temp2;
        }
        static ImVec2* ImVec2_2() {
            vec2_temp2.x = 0;
            vec2_temp2.y = 0;
            return &vec2_temp2;
        }
        static ImVec2* ImVec2_2(float x, float y) {
            vec2_temp2.x = x;
            vec2_temp2.y = y;
            return &vec2_temp2;
        }
        static ImVec2* ImVec2_2(ImVec2& other) {
            vec2_temp2.x = other.x;
            vec2_temp2.y = other.y;
            return &vec2_temp2;
        }
        static ImVec2* ImVec2_3() {
            vec2_temp3.x = 0;
            vec2_temp3.y = 0;
            return &vec2_temp3;
        }
        static ImVec2* ImVec2_3(float x, float y) {
            vec2_temp3.x = x;
            vec2_temp3.y = y;
            return &vec2_temp3;
        }
        static ImVec2* ImVec2_3(ImVec2& other) {
            vec2_temp3.x = other.x;
            vec2_temp3.y = other.y;
            return &vec2_temp3;
        }
        static ImVec2* ImVec2_4() {
            vec2_temp4.x = 0;
            vec2_temp4.y = 0;
            return &vec2_temp4;
        }
        static ImVec2* ImVec2_4(float x, float y) {
            vec2_temp4.x = x;
            vec2_temp4.y = y;
            return &vec2_temp4;
        }
        static ImVec2* ImVec2_4(ImVec2& other) {
            vec2_temp4.x = other.x;
            vec2_temp4.y = other.y;
            return &vec2_temp4;
        }

        static ImVec4* ImVec4_1() {
            vec4_temp1.x = 0;
            vec4_temp1.y = 0;
            vec4_temp1.z = 0;
            vec4_temp1.w = 0;
            return &vec4_temp1;
        }
        static ImVec4* ImVec4_1(float x, float y, float z, float w) {
            vec4_temp1.x = x;
            vec4_temp1.y = y;
            vec4_temp1.z = z;
            vec4_temp1.w = w;
            return &vec4_temp1;
        }
        static ImVec4* ImVec4_1(ImVec4& other) {
            vec4_temp1.x = other.x;
            vec4_temp1.y = other.y;
            vec4_temp1.z = other.z;
            vec4_temp1.w = other.w;
            return &vec4_temp1;
        }
        static ImVec4* ImVec4_2() {
            vec4_temp2.x = 0;
            vec4_temp2.y = 0;
            vec4_temp2.z = 0;
            vec4_temp2.w = 0;
            return &vec4_temp2;
        }
        static ImVec4* ImVec4_2(float x, float y, float z, float w) {
            vec4_temp2.x = x;
            vec4_temp2.y = y;
            vec4_temp2.z = z;
            vec4_temp2.w = w;
            return &vec4_temp2;
        }
        static ImVec4* ImVec4_2(ImVec4& other) {
            vec4_temp2.x = other.x;
            vec4_temp2.y = other.y;
            vec4_temp2.z = other.z;
            vec4_temp2.w = other.w;
            return &vec4_temp2;
        }
        static ImVec4* ImVec4_3() {
            vec4_temp3.x = 0;
            vec4_temp3.y = 0;
            vec4_temp3.z = 0;
            vec4_temp3.w = 0;
            return &vec4_temp3;
        }
        static ImVec4* ImVec4_3(float x, float y, float z, float w) {
            vec4_temp3.x = x;
            vec4_temp3.y = y;
            vec4_temp3.z = z;
            vec4_temp3.w = w;
            return &vec4_temp3;
        }
        static ImVec4* ImVec4_3(ImVec4& other) {
            vec4_temp3.x = other.x;
            vec4_temp3.y = other.y;
            vec4_temp3.z = other.z;
            vec4_temp3.w = other.w;
            return &vec4_temp3;
        }
        static ImVec4* ImVec4_4() {
            vec4_temp4.x = 0;
            vec4_temp4.y = 0;
            vec4_temp4.z = 0;
            vec4_temp4.w = 0;
            return &vec4_temp4;
        }
        static ImVec4* ImVec4_4(float x, float y, float z, float w) {
            vec4_temp4.x = x;
            vec4_temp4.y = y;
            vec4_temp4.z = z;
            vec4_temp4.w = w;
            return &vec4_temp4;
        }
        static ImVec4* ImVec4_4(ImVec4& other) {
            vec4_temp4.x = other.x;
            vec4_temp4.y = other.y;
            vec4_temp4.z = other.z;
            vec4_temp4.w = other.w;
            return &vec4_temp4;
        }

        static ImRect* ImRect_1() {
            rect_temp1.Min.x = 0;
            rect_temp1.Min.y = 0;
            rect_temp1.Max.x = 0;
            rect_temp1.Max.y = 0;
            return &rect_temp1;
        }
        static ImRect* ImRect_1(float minX, float minY, float maxX, float maxY) {
            rect_temp1.Min.x = minX;
            rect_temp1.Min.y = minY;
            rect_temp1.Max.x = maxX;
            rect_temp1.Max.y = maxY;
            return &rect_temp1;
        }
        static ImRect* ImRect_1(ImVec2& min, ImVec2& max) {
            rect_temp1.Min.x = min.x;
            rect_temp1.Min.y = min.y;
            rect_temp1.Max.x = max.x;
            rect_temp1.Max.y = max.y;
            return &rect_temp1;
        }
        static ImRect* ImRect_2() {
            rect_temp2.Min.x = 0;
            rect_temp2.Min.y = 0;
            rect_temp2.Max.x = 0;
            rect_temp2.Max.y = 0;
            return &rect_temp2;
        }
        static ImRect* ImRect_2(float minX, float minY, float maxX, float maxY) {
            rect_temp2.Min.x = minX;
            rect_temp2.Min.y = minY;
            rect_temp2.Max.x = maxX;
            rect_temp2.Max.y = maxY;
            return &rect_temp2;
        }
        static ImRect* ImRect_2(ImVec2& min, ImVec2& max) {
            rect_temp2.Min.x = min.x;
            rect_temp2.Min.y = min.y;
            rect_temp2.Max.x = max.x;
            rect_temp2.Max.y = max.y;
            return &rect_temp2;
        }
        static ImRect* ImRect_3() {
            rect_temp3.Min.x = 0;
            rect_temp3.Min.y = 0;
            rect_temp3.Max.x = 0;
            rect_temp3.Max.y = 0;
            return &rect_temp3;
        }
        static ImRect* ImRect_3(float minX, float minY, float maxX, float maxY) {
            rect_temp3.Min.x = minX;
            rect_temp3.Min.y = minY;
            rect_temp3.Max.x = maxX;
            rect_temp3.Max.y = maxY;
            return &rect_temp3;
        }
        static ImRect* ImRect_3(ImVec2& min, ImVec2& max) {
            rect_temp3.Min.x = min.x;
            rect_temp3.Min.y = min.y;
            rect_temp3.Max.x = max.x;
            rect_temp3.Max.y = max.y;
            return &rect_temp3;
        }
        static ImRect* ImRect_4() {
            rect_temp4.Min.x = 0;
            rect_temp4.Min.y = 0;
            rect_temp4.Max.x = 0;
            rect_temp4.Max.y = 0;
            return &rect_temp4;
        }
        static ImRect* ImRect_4(float minX, float minY, float maxX, float maxY) {
            rect_temp4.Min.x = minX;
            rect_temp4.Min.y = minY;
            rect_temp4.Max.x = maxX;
            rect_temp4.Max.y = maxY;
            return &rect_temp4;
        }
        static ImRect* ImRect_4(ImVec2& min, ImVec2& max) {
            rect_temp4.Min.x = min.x;
            rect_temp4.Min.y = min.y;
            rect_temp4.Max.x = max.x;
            rect_temp4.Max.y = max.y;
            return &rect_temp4;
        }
};


//class ImHelper {
//    public:
//        static void setClipboardTextFunction(ImGuiPlatformIO* platform_io, ClipboardTextFunction * clipboardFunction) {
//            auto pointer = reinterpret_cast<std::uintptr_t>(clipboardFunction);
//            platform_io->Platform_ClipboardUserData = (void*)pointer;
//            platform_io->Platform_SetClipboardTextFn = [](ImGuiContext* context, const char* text) {
//                void* user_data = context->PlatformIO.Platform_ClipboardUserData;
//                auto addr = reinterpret_cast<std::uintptr_t>(user_data);
//                ClipboardTextFunction* clipboardFunction = reinterpret_cast<ClipboardTextFunction*>(addr);
//                std::string& str = clipboardFunction->text;
//                str = text;
//                clipboardFunction->onSetClipboardText(&str);
//
//            };
//            platform_io->Platform_GetClipboardTextFn = [](ImGuiContext* context) {
//                void* user_data = context->PlatformIO.Platform_ClipboardUserData;
//                auto addr = reinterpret_cast<std::uintptr_t>(user_data);
//                ClipboardTextFunction* clipboardFunction = reinterpret_cast<ClipboardTextFunction*>(addr);
//                std::string& str = clipboardFunction->text;
//                str.clear();
//                clipboardFunction->onGetClipboardText(&str);
//                return str.c_str();
//            };
//        }
//
//};
