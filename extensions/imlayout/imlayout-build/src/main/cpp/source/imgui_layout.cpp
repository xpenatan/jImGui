#include "imgui.h"
#include "imgui_internal.h"
#include "imgui_layout.h"
#include <iostream>

// ##################################  ImGuiLayout  ###############################################

ImGuiLayout::ImGuiLayout(ImGuiID id) {
    parentLayout = NULL;
    this->id = id;
    paddingLeft = 0;
    paddingRight = 0;
    paddingTop = 0;
    paddingBottom = 0;
    clipping = false;
    debugClipping = false;
    debug = false;
    error = false;
    skipping = false;
    isWrapParentX = false;
    isMatchParentX = false;
    isWrapParentY = false;
    isMatchParentY = false;
    orientation = ImOrientation::NONE;
}

bool ImGuiLayout::haveParent() {
    return parentLayout != NULL;
}

ImVec2 ImGuiLayout::getAbsoluteSize() {
    return ImVec2(position.x + size.x, position.y + size.y);
}

ImVec2 ImGuiLayout::getContentSize() {
    return ImVec2(contentSize.x, contentSize.y);
}

ImVec2 ImGuiLayout::getAbsoluteSizePadding() {
    return ImVec2(position.x + size.x - paddingRight, position.y + size.y - paddingBottom);
}

ImVec2 ImGuiLayout::getPositionPadding() {
    return ImVec2(position.x + paddingLeft, position.y + paddingTop);
}

ImRect ImGuiLayout::getBoundingBox() {
    return ImRect(position, getAbsoluteSize());
}


void ImGuiLayout::drawSizeDebug() {
    // Render layout space
    //Green
    ImLayout::DrawBoundingBox_2(position, getAbsoluteSize(), 0, 255, 0, 255);
}

void ImGuiLayout::drawContentDebug() {
    // Render content space
    // Blue
    float minX = positionContents.x;
    float minY = positionContents.y;
    float maxX = positionContents.x + contentSize.x;
    float maxY = positionContents.y + contentSize.y;
    ImLayout::DrawBoundingBox_2(ImVec2(minX, minY), ImVec2(maxX, maxY), 0, 100, 255, 255);
}

void ImGuiLayout::drawPaddingAreaDebug() {
    // Render size with padding
    ImLayout::DrawBoundingBox_2(getPositionPadding(), getAbsoluteSizePadding(), 255, 255, 255, 255);
}

void ImGuiLayout::drawError() {
    ImLayout::DrawBoundingBox_2(position, getAbsoluteSize(), 255, 0, 0, 200, true);
}

// ##################################  ImGuiLayoutOptions  ########################################

ImGuiLayoutOptions::ImGuiLayoutOptions() {
    init(0, 0, 0, 0, true);
}

ImGuiLayoutOptions::ImGuiLayoutOptions(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom, bool clipping) {
    init(paddingLeft, paddingRight, paddingTop, paddingBottom, clipping);
}

void ImGuiLayoutOptions::init(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom, bool clipping) {
    //fix delegating constructors are permitted only in C++11
    this->paddingLeft = paddingLeft;
    this->paddingRight = paddingRight;
    this->paddingTop = paddingTop;
    this->paddingBottom = paddingBottom;
    this->clipping = clipping;
}



// ##################################  ImGuiCollapseLayoutOptions  ################################

ImGuiCollapseLayoutOptions::ImGuiCollapseLayoutOptions() {
    ImGuiLayoutOptions::init(2, 2, 2, 2);
    init();
}

ImGuiCollapseLayoutOptions::ImGuiCollapseLayoutOptions(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
    ImGuiLayoutOptions::init(paddingLeft, paddingRight, paddingTop, paddingBottom);
    init();
}

void ImGuiCollapseLayoutOptions::init() {
    arrowColor = ImGui::GetColorU32(ImVec4(0xFF / 255.0f, 0xFF / 255.0f, 0xFF / 255.0f, 0xFF / 255.0f));
    arrowBackgroundHoveredColor = ImGui::GetColorU32(ImVec4(0x77 / 255.0f, 0x77 / 255.0f, 0x77 / 255.0f, 0xFF / 255.0f));
    arrowBackgroundClickedColor = ImGui::GetColorU32(ImVec4(0x55 / 255.0f, 0x55 / 255.0f, 0x55 / 255.0f, 0xFF / 255.0f));
    frameColor = ImGui::GetColorU32(ImVec4(0x24 / 255.0f, 0x24 / 255.0f, 0x24 / 255.0f, 255 / 255.0f));
    borderColor = ImGui::GetColorU32(ImVec4(0x40 / 255.0f, 0x40 / 255.0f, 0x49 / 255.0f, 255 / 255.0f));
    borderRound = 4;
    roundingCorners = ImDrawFlags_RoundCornersTopLeft | ImDrawFlags_RoundCornersTopRight;
}

// ##################################  ImGuiLayout  ###############################################

static ImVector<ImGuiLayout*> layoutStack;

static ImGuiLayout* createOrFind(ImGuiID id) {
    ImGuiContext& g = *GImGui;
    ImGuiLayout* childLayout = NULL;
    childLayout = (ImGuiLayout*)g.WindowsById.GetVoidPtr(id);
    if (childLayout == NULL) {
        childLayout = IM_NEW(ImGuiLayout)(id);
        g.WindowsById.SetVoidPtr(childLayout->id, childLayout);
    }
    return childLayout;
}

static ImGuiLayout* pushLayout(ImGuiID id) {
    ImGuiLayout* parentLayout = NULL;
    if (!layoutStack.empty())
        parentLayout = layoutStack.back();

    ImGui::PushID(id);

    ImGuiLayout* childLayout = createOrFind(id);
    childLayout->parentLayout = parentLayout;
    childLayout->childsLayout.clear();
    if (parentLayout != NULL)
        parentLayout->childsLayout.push_back(childLayout);
    layoutStack.push_back(childLayout);
    return childLayout;
}

ImGuiLayout* ImLayout::GetCurrentLayout() {
    if (layoutStack.empty())
        return NULL;
    return layoutStack.back();
}

static void popLayout() {
    layoutStack.pop_back();
    ImGui::PopID();
}

void ImLayout::DrawBoundingBox_1(float x1, float y1, float x2, float y2, int r, int g, int b, int a, bool clipping) {
    ImDrawList* drawList = clipping ? ImGui::GetWindowDrawList() : ImGui::GetForegroundDrawList();
    int color = IM_COL32(r, g, b, a);
    drawList->AddRect(ImVec2(x1, y1), ImVec2(x2, y2), color, 0, 1.0f, 0);
}

void ImLayout::DrawBoundingBox_2(const ImVec2& min, const ImVec2& max, int r, int g, int b, int a, bool clipping) {
    ImLayout::DrawBoundingBox_1(min.x, min.y, max.x, max.y, r, g, b, a, clipping);
}

void ImLayout::DrawBoundingBox_3(const ImRect& rect, int r, int g, int b, int a, bool clipping) {
    ImLayout::DrawBoundingBox_2(rect.Min, rect.Max, r, g, b, a, clipping);
}

void ImLayout::FillWidth(int r, int g, int b, int a, ImVec2 size) {
    ImU32 bgColor = ImGui::GetColorU32(ImVec4(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f));
    ImDrawList* drawList = ImGui::GetWindowDrawList();
    ImVec2 regionMax = ImGui::GetContentRegionAvail();

    float height = regionMax.y;
    float width = ImGui::GetContentRegionAvail().x;

    ImVec2 cursorPos = ImGui::GetCursorScreenPos();

    if (size.x != ImLayout::MATCH_PARENT && size.x > 0) {
        width = size.x;
    }

    if (size.y != ImLayout::MATCH_PARENT && size.y > 0) {
        height = size.y;
    }
    drawList->AddRectFilled(cursorPos, ImVec2(cursorPos.x + width, cursorPos.y + height), bgColor);

    ImGui::ItemSize(ImVec2(width, height));
}

void ImLayout::ShowLayoutDebug() {
    ImGuiLayout* curLayout = ImLayout::GetCurrentLayout();
    if (curLayout != NULL) {
        curLayout->debug = true;
    }
}
void ImLayout::ShowLayoutDebugClipping() {
    ImGuiLayout* curLayout = ImLayout::GetCurrentLayout();
    if (curLayout != NULL) {
        curLayout->debugClipping = true;
    }
}

void ImLayout::SetOrientation(ImOrientation orientation) {
    ImGuiLayout* curLayout = ImLayout::GetCurrentLayout();
    if (curLayout != NULL) {
        curLayout->orientation = orientation;
    }
}

ImVec2 ImLayout::GetLayoutSize() {
    ImGuiLayout* curLayout = ImLayout::GetCurrentLayout();
    if (curLayout != NULL) {
        return curLayout->size;
    }
    return ImVec2();
}

void ImLayout::BeginLayoutEx(ImGuiID id)
{
    pushLayout(id);
}

void ImLayout::BeginLayoutEx(const char* strID)
{
    BeginLayoutEx(ImGui::GetID(strID));
}

void ImLayout::PrepareLayoutType(float sizeX, float sizeY)
{
    ImGuiLayout* curLayout = ImLayout::GetCurrentLayout();
    curLayout->isMatchParentX = sizeX == ImLayout::MATCH_PARENT;
    curLayout->isWrapParentX = sizeX == ImLayout::WRAP_PARENT;
    curLayout->isMatchParentY = sizeY == ImLayout::MATCH_PARENT;
    curLayout->isWrapParentY = sizeY == ImLayout::WRAP_PARENT;
}

void ImLayout::PrepareLayout(float sizeX, float sizeY, ImGuiLayoutOptions options)
{
    ImGuiContext& g = *GImGui;
    ImGuiWindow* window = g.CurrentWindow;
    ImGuiLayout* curLayout = ImLayout::GetCurrentLayout();

    if (sizeX == ImLayout::MATCH_WRAP_PARENT) {
        float availX = ImGui::GetContentRegionAvail().x;
        float contentSizeX = curLayout->getContentSize().x;

        if (availX > contentSizeX) {
            sizeX = ImLayout::MATCH_PARENT;
        }
        else {
            sizeX = ImLayout::WRAP_PARENT;
        }
    }
    if (sizeY == ImLayout::MATCH_WRAP_PARENT) {
        float availY = ImGui::GetContentRegionAvail().y;
        float contentSizeY = curLayout->getContentSize().y;

        if (availY > contentSizeY) {
            sizeY = ImLayout::MATCH_PARENT;
        }
        else {
            sizeY = ImLayout::WRAP_PARENT;
        }
    }


    ImVec2 position = window->DC.CursorPos;

    ImLayout::PrepareLayoutType(sizeX, sizeY);

    float x2 = position.x + sizeX;
    float y2 = position.y + sizeY;

    PrepareLayout(position.x, position.y, x2, y2, options);
}

void ImLayout::PrepareLayout(float x1, float y1, float x2, float y2, ImGuiLayoutOptions options)
{
    ImGuiContext& g = *GImGui;
    ImGuiWindow* window = g.CurrentWindow;

    ImGuiLayout* curLayout = ImLayout::GetCurrentLayout();
    curLayout->clipping = options.clipping;
    // Update layout

    // Backup windows data
    curLayout->DC = window->DC;
    curLayout->WorkRect = window->WorkRect;
    curLayout->skipping = window->SkipItems;
    curLayout->Pos = window->Pos;
    curLayout->ContentsRegionRect = window->ContentRegionRect;
    // ******** End Backup windows data

    float sizeX = 0;
    float sizeY = 0;


    curLayout->position.x = x1;
    curLayout->position.y = y1;

    curLayout->paddingLeft = options.paddingLeft;
    curLayout->paddingRight = options.paddingRight;
    curLayout->paddingTop = options.paddingTop;
    curLayout->paddingBottom = options.paddingBottom;

    ImVec2 contentPosition = curLayout->getPositionPadding();

    curLayout->positionContents = curLayout->position;

    curLayout->content_avail = ImGui::GetContentRegionAvail();

    if (curLayout->isMatchParentX) {
        if (curLayout->parentLayout != NULL && curLayout->parentLayout->orientation == ImOrientation::HORIZONTAL && curLayout->parentLayout->childLayoutCache.Size > 1) {
            curLayout->size.x = ImMax(curLayout->matchTargetSize.x, 4.0f);
        }
        else {
            curLayout->size.x = ImMax(curLayout->content_avail.x, 4.0f);
        }
    }
    else if (!curLayout->isWrapParentX) {
        curLayout->size.x = ImFloor(x2 - x1);
    }

    if (curLayout->isMatchParentY) {
        if (curLayout->parentLayout != NULL && curLayout->parentLayout->orientation == ImOrientation::VERTICAL && curLayout->parentLayout->childLayoutCache.Size > 1) {
            curLayout->size.y = ImMax(curLayout->matchTargetSize.y, 4.0f);
        }
        else {
            curLayout->size.y = ImMax(curLayout->content_avail.y, 4.0f);
        }
    }
    else if (!curLayout->isWrapParentY) {
        curLayout->size.y = ImFloor(y2 - y1);
    }

    if (curLayout->childLayoutCache.Size > 1) {
        int totalWrapSize = 0;
        int matchCount = 0;
        int subtractSize = 0;
        ImVec2 parentMaxSize = curLayout->getAbsoluteSize();
        if (curLayout->orientation == ImOrientation::HORIZONTAL) {
            for (int i = 0; i < curLayout->childLayoutCache.Size; i++) {
                ImGuiLayout* childLayout = curLayout->childLayoutCache[i];
                if (childLayout->isWrapParentX) {
                    totalWrapSize += childLayout->contentSize.x;
                }
                else if (childLayout->isMatchParentX) {
                    matchCount++;
                }
                else {
                    totalWrapSize += childLayout->size.x;
                }
                if (i > 0) {
                    ImGuiLayout* prevChildLayout = curLayout->childLayoutCache[i - 1];
                    int lastPosX = prevChildLayout->getAbsoluteSize().x;
                    int sub = lastPosX - childLayout->position.x;
                    subtractSize = subtractSize + sub;
                }
            }

            if (matchCount > 0) {
                float totalSizeLeft = curLayout->size.x - totalWrapSize + subtractSize;
                int matchSize = totalSizeLeft / matchCount;
                int totalSize = matchSize * matchCount - subtractSize + totalWrapSize;
                float remainingPixels = curLayout->content_avail.x - totalSize;
                for (int i = 0; i < curLayout->childLayoutCache.Size; i++) {
                    ImGuiLayout* childLayout = curLayout->childLayoutCache[i];
                    if (childLayout->isMatchParentX) {
                        float addSize = 0;
                        if (remainingPixels > 0) {
                            remainingPixels--;
                            addSize = 1;
                        }
                        childLayout->matchTargetSize.x = matchSize + addSize;
                    }
                }
            }
        }
        else if (curLayout->orientation == ImOrientation::VERTICAL) {
            for (int i = 0; i < curLayout->childLayoutCache.Size; i++) {
                ImGuiLayout* childLayout = curLayout->childLayoutCache[i];
                if (childLayout->isWrapParentY) {
                    totalWrapSize += childLayout->contentSize.y;
                }
                else if (childLayout->isMatchParentY) {
                    matchCount++;
                }
                else {
                    totalWrapSize += childLayout->size.y;
                }
                if (i > 0) {
                    ImGuiLayout* prevChildLayout = curLayout->childLayoutCache[i - 1];
                    int lastPosX = prevChildLayout->getAbsoluteSize().y;
                    int sub = lastPosX - childLayout->position.y;
                    subtractSize = subtractSize + sub;
                }
            }
            if (matchCount > 0) {
                float totalSizeLeft = curLayout->size.y - totalWrapSize + subtractSize;
                int matchSize = totalSizeLeft / matchCount;
                int totalSize = matchSize * matchCount - subtractSize + totalWrapSize;
                float remainingPixels = curLayout->content_avail.x - totalSize;
                for (int i = 0; i < curLayout->childLayoutCache.Size; i++) {
                    ImGuiLayout* childLayout = curLayout->childLayoutCache[i];
                    if (childLayout->isMatchParentY) {
                        int addSize = 0;
                        if (remainingPixels > 0) {
                            remainingPixels--;
                            addSize = 1;
                        }
                        childLayout->matchTargetSize.y = matchSize + addSize;
                    }
                }
            }
        }
    }

    // ***** End Update Layout

    // Write to window object

    window->Pos.x = contentPosition.x;
    window->Pos.y = contentPosition.y;
    window->DC.Indent.x = 0;

    //window->DC.CursorMaxPos.x = contentPosition.x + curLayout->size.x - curLayout->paddingLeft - curLayout->paddingRight;
    //window->DC.CursorMaxPos.y = contentPosition.y + curLayout->size.y - curLayout->paddingTop - curLayout->paddingBottom;
    window->DC.CursorMaxPos.x = contentPosition.x;
    window->DC.CursorMaxPos.y = contentPosition.y;

    window->DC.CursorStartPos.x = contentPosition.x;
    window->DC.CursorStartPos.y = contentPosition.y;

    window->DC.CurrLineSize.y = 0; // necessary to keep position inside layout
    window->DC.CurrLineTextBaseOffset = 0;

    window->DC.CursorPos.x = contentPosition.x;
    window->DC.CursorPos.y = contentPosition.y;

    window->WorkRect.Min.x = contentPosition.x;
    window->WorkRect.Min.y = contentPosition.y;
    window->WorkRect.Max.x = contentPosition.x + curLayout->size.x - curLayout->paddingLeft - curLayout->paddingRight;
    window->WorkRect.Max.y = contentPosition.y + curLayout->size.y - curLayout->paddingTop - curLayout->paddingBottom;

    window->ContentRegionRect.Min.x = contentPosition.x;
    window->ContentRegionRect.Min.y = contentPosition.y;
    window->ContentRegionRect.Max.x = contentPosition.x + curLayout->size.x - curLayout->paddingLeft - curLayout->paddingRight;
    window->ContentRegionRect.Max.y = contentPosition.y + curLayout->size.y - curLayout->paddingTop - curLayout->paddingBottom;

    // ***** End Write to window object
    curLayout->clippingMin = curLayout->position;
    curLayout->clippingMax = curLayout->getAbsoluteSize();
    curLayout->hide = false;

    if (curLayout->contentSize.x == 0 && curLayout->contentSize.y == 0) {
        curLayout->hide = true;
        ImGui::GetStyle().Alpha = 0;
    }
    else {
        if (curLayout->clipping) {
            ImGui::PushClipRect(curLayout->clippingMin, curLayout->clippingMax, true);
        }
    }

    ImGui::BeginGroup();
}

void ImLayout::BeginLayout_1(ImGuiID id, float sizeX, float sizeY, const ImGuiLayoutOptions& options)
{
    BeginLayout(id, sizeX, sizeY, options);
}

void ImLayout::BeginLayout_2(const char* id, float sizeX, float sizeY, const ImGuiLayoutOptions& options)
{
    BeginLayout(id, sizeX, sizeY, options);
}

void ImLayout::BeginLayout(ImGuiID id, float sizeX, float sizeY, const ImGuiLayoutOptions& options)
{
    BeginLayoutEx(id);
    PrepareLayout(sizeX, sizeY, options);
}

void ImLayout::BeginLayout(const char* strID, float sizeX, float sizeY, const ImGuiLayoutOptions& options)
{
    BeginLayoutEx(strID);
    PrepareLayout(sizeX, sizeY, options);
}

void ImLayout::EndLayout()
{
    ImGui::EndGroup();
    ImGuiContext& g = *GImGui;
    ImGuiWindow* window = g.CurrentWindow;
    ImGuiLayout* curLayout = ImLayout::GetCurrentLayout();
    if (curLayout == NULL)
        return;


    if (curLayout->hide) {
        ImGui::GetStyle().Alpha = 1.0;
    }
    else {
        if (curLayout->clipping) {
            ImGui::PopClipRect();
        }
    }

    float x = window->DC.CursorPos.x;
    float y = window->DC.CursorPos.y;

    curLayout->contentSize.x = window->DC.CursorMaxPos.x - curLayout->positionContents.x;
    curLayout->contentSize.y = y - curLayout->positionContents.y - g.Style.ItemSpacing.y;

    // Restore windows data
    window->DC = curLayout->DC;
    window->WorkRect = curLayout->WorkRect;
    window->SkipItems = curLayout->skipping;
    window->Pos = curLayout->Pos;
    window->ContentRegionRect = curLayout->ContentsRegionRect;
    ImGui::ItemAdd(curLayout->getBoundingBox(), curLayout->id);
    // ********************

    if (curLayout->isMatchParentX) {
        //sizeItem.x = curLayout->size.x;
        if (curLayout->size.x < curLayout->contentSize.x || curLayout->size.x > curLayout->contentSize.x) {
            //special case where dev used MATCH_CONTENT and WRAP_CONTENT wrong. The parent have a WRAP_CONTENT y size and the child have MATCH_CONTENT y size.
            /* curLayout->error = true;
            sizeItem.x = 10;*/
        }
    }
    else if (curLayout->isWrapParentX) {
        int sizeX = curLayout->contentSize.x + curLayout->paddingLeft + curLayout->paddingRight;
        curLayout->size.x = sizeX;
    }

    if (curLayout->isMatchParentY) {
        //sizeItem.y = curLayout->size.y;
        if (curLayout->size.y < curLayout->contentSize.y) {
            //special case where dev used MATCH_CONTENT and WRAP_CONTENT wrong. The parent have a WRAP_CONTENT y size and the child have MATCH_CONTENT y size.
               //curLayout->error = true;
            //sizeItem.y = curLayout->contentSize.y;
        }
    }
    else if (curLayout->isWrapParentY) {
        int sizeY = curLayout->contentSize.y + curLayout->paddingBottom + curLayout->paddingTop;
        curLayout->size.y = sizeY;
    }

    ImGui::ItemSize(curLayout->size);

    if (curLayout->debug) {
        curLayout->drawContentDebug();

        if (curLayout->paddingTop != 0 || curLayout->paddingBottom != 0 || curLayout->paddingLeft != 0 || curLayout->paddingRight != 0) {
            curLayout->drawPaddingAreaDebug();
        }
        curLayout->drawSizeDebug();
        curLayout->debug = false;
    }

    if (curLayout->debugClipping) {
        ImLayout::DrawBoundingBox_2(curLayout->clippingMin, curLayout->clippingMax, 255, 0, 0);
        curLayout->debugClipping = false;
    }

    if (curLayout->error) {
        curLayout->error = false;
        curLayout->drawError();
    }

    curLayout->childLayoutCache.clear();
    for (int i = 0; i < curLayout->childsLayout.Size; i++) {
        ImGuiLayout* childLayout = curLayout->childsLayout[i];
        curLayout->childLayoutCache.push_back(childLayout);
    }

    popLayout();
}

static bool renderFrameArrow(bool* value, int arrowColor, int arrowBackgroundHoveredColor, int arrowBackgroundClickedColor)
{
    ImGuiContext& g = *GImGui;
    ImGuiWindow* window = g.CurrentWindow;

    const ImGuiStyle& style = g.Style;
    const bool display_frame = true;
    int flags = ImGuiTreeNodeFlags_FramePadding;

    const ImVec2 padding = display_frame ? style.FramePadding : ImVec2(style.FramePadding.x, ImMin(window->DC.CurrLineTextBaseOffset, style.FramePadding.y));
    const float text_offset_x = g.FontSize + (display_frame ? padding.x * 3 : padding.x * 2);   // Collapsing arrow width + Spacing
    const float text_offset_y = ImMax(padding.y, window->DC.CurrLineTextBaseOffset);            // Latch before ItemSize changes it
    ImVec2 text_pos(window->DC.CursorPos.x + text_offset_x, window->DC.CursorPos.y + text_offset_y);

    ImDrawList* drawList = ImGui::GetWindowDrawList();
    float getFrameHeight = ImGui::GetFrameHeight();
    float removedSize = 0.0f; // reduce few pixels
    float halfSize = (getFrameHeight) / 2.0f - removedSize;

    ImVec2 vec = ImGui::GetCursorScreenPos();

    float screenPosX = vec.x;
    float screenPosY = vec.y;
    float arrowPaddingLeft = 0;

    float x = screenPosX + halfSize + arrowPaddingLeft;
    float y = screenPosY + getFrameHeight / 2.0f;

    ImVec2 min(x - halfSize - removedSize, y - halfSize - removedSize);
    ImVec2 max(x + halfSize + removedSize, y + halfSize + removedSize);

    //ImLayout::DrawBoundingBox_2(min, max, 0, 100, 255, 255);

    bool hovered = ImGui::IsMouseHoveringRect(min, max);
    ImU32 hoveredColor = arrowBackgroundHoveredColor;
    bool isWindowHovered = ImGui::IsWindowHovered();

    float centerOffsetX = (max.x - min.x);
    float centerOffsetY = (max.y - min.y);
    float half = centerOffsetX / 2.0;
    float centerX = min.x + half;
    float centerY = min.y + half;

    const float h = drawList->_Data->FontSize;
    float scale = ImLayout::GetDPIScale();
    float r = h * 0.40f * scale;
    float r1 = h * 0.20f * scale;

    if (isWindowHovered) {
        if (hovered) {
            if (ImGui::IsMouseDown(0))
                hoveredColor = arrowBackgroundClickedColor;
            if (ImGui::IsMouseReleased(0))
                *value = !*value;
        }

        if (hovered) {
            drawList->AddCircleFilled(ImVec2(centerX, centerY), r + r1, hoveredColor);
        }
    }

    float triA_X = 0;
    float triA_Y = 0;
    float triB_X = 0;
    float triB_Y = 0;
    float triC_X = 0;
    float triC_Y = 0;
    float offsetX = 0;
    float offsetY = 0;

    float halfArrowSize = halfSize - halfSize/2 * scale;

    ImVec2 a, b, c;
    if (*value) {
        // arrow down
        centerY = centerY + (h / 2.0) * 0.1f;
        a = ImVec2(+0.000f, +0.750f) * r;
        b = ImVec2(-0.866f, -0.750f) * r;
        c = ImVec2(+0.866f, -0.750f) * r;
    }
    else {
        // arrow right
        a = ImVec2(+0.750f, +0.000f) * r;
        b = ImVec2(-0.750f, +0.866f) * r;
        c = ImVec2(-0.750f, -0.866f) * r;
    }

    ImVec2 centerVec(centerX, centerY);

    drawList->AddTriangleFilled(a + centerVec, b + centerVec, c + centerVec, arrowColor);

    float bk = g.Style.ItemSpacing.y;
    g.Style.ItemSpacing.y = 0;
    ImGui::ItemSize(ImVec2(halfSize * 2 + 3, getFrameHeight));
    g.Style.ItemSpacing.y = bk;

    return *value;
}

static int OPEN_KEY = 13213;

bool ImLayout::PrepareCollapseLayout(const char* title, float sizeX, float sizeY, ImGuiCollapseLayoutOptions options)
{
    ImGuiContext& g = *GImGui;
    ImGuiWindow* window = g.CurrentWindow;
    ImDrawList* drawList = window->DrawList;
    ImGuiLayout* rootLayout = ImLayout::GetCurrentLayout();

    float frameHeight = ImGui::GetFrameHeight();

    bool flag = rootLayout->map.GetBool(OPEN_KEY, options.openDefault);
    bool* isOpen = &flag;

    sizeY = *isOpen ? sizeY : ImLayout::WRAP_PARENT;

    ImLayout::PrepareLayout(sizeX, sizeY, ImGuiLayoutOptions(1, 1, 1, 1));

    rootLayout->map.SetFloat(120, options.paddingLeft);
    rootLayout->map.SetFloat(121, options.paddingRight);
    rootLayout->map.SetFloat(122, options.paddingTop);
    rootLayout->map.SetFloat(123, options.paddingBottom);

    ImGuiLayoutOptions op(0, 0, 0, 0);
    ImLayout::BeginLayout("frame", ImLayout::MATCH_PARENT, frameHeight, op);
    ImGuiLayout* frameLayout = ImLayout::GetCurrentLayout();

    ImVec2 mousePos = ImGui::GetMousePos();

    drawList->AddRectFilled(rootLayout->position, ImVec2(rootLayout->getAbsoluteSize().x, frameLayout->getAbsoluteSize().y), options.frameColor, options.borderRound, options.roundingCorners);

    renderFrameArrow(isOpen, options.arrowColor, options.arrowBackgroundHoveredColor, options.arrowBackgroundClickedColor);

    rootLayout->map.SetBool(OPEN_KEY, *isOpen);

    ImGui::SameLine(0, 0);

    ImLayout::BeginAlign("align", ImLayout::WRAP_PARENT, ImLayout::MATCH_PARENT,  0.0, 0.5, 0, 0);

    ImGui::Text(title);

    ImLayout::EndAlign();

    ImGui::SameLine();

    return *isOpen;
}

bool ImLayout::BeginCollapseLayoutEx(const char* id, const char* title, float sizeX, float sizeY)
{
    ImGuiCollapseLayoutOptions op;
    return BeginCollapseLayoutEx(id, title, sizeX, sizeY, op);
}

bool ImLayout::BeginCollapseLayoutEx(const char* id, const char* title, float sizeX, float sizeY, ImGuiCollapseLayoutOptions & options)
{
    ImLayout::BeginLayoutEx(id);
    return ImLayout::PrepareCollapseLayout(title, sizeX, sizeY, options);
}

bool ImLayout::BeginCollapseLayoutEx_2(int id, const char* title, float sizeX, float sizeY)
{
    ImGuiCollapseLayoutOptions options;
    return BeginCollapseLayoutEx_2(id, title, sizeX, sizeY, options);
}

bool ImLayout::BeginCollapseLayoutEx_2(int id, const char* title, float sizeX, float sizeY, ImGuiCollapseLayoutOptions & options)
{
    ImLayout::BeginLayoutEx(id);
    return ImLayout::PrepareCollapseLayout(title, sizeX, sizeY, options);
}

void ImLayout::BeginCollapseLayoutEx_3(const char* id, bool* isOpen, const char* title, float sizeX, float sizeY)
{
    ImGuiCollapseLayoutOptions op;
    BeginCollapseLayoutEx_3(id, isOpen, title, sizeX, sizeY, op);
}

void ImLayout::BeginCollapseLayoutEx_3(const char* id, bool* isOpen, const char* title, float sizeX, float sizeY, ImGuiCollapseLayoutOptions & options)
{
    ImLayout::BeginLayoutEx(id);
    ImGuiLayout* rootLayout = ImLayout::GetCurrentLayout();
    rootLayout->map.SetBool(OPEN_KEY, *isOpen);
    bool flag = ImLayout::PrepareCollapseLayout(title, sizeX, sizeY, options);
    *isOpen = flag;
}

bool ImLayout::BeginCollapseLayout(const char* id, const char* title, float sizeX, float sizeY)
{
    ImGuiCollapseLayoutOptions op;
    return BeginCollapseLayout(id, title, sizeX, sizeY, op);
}

bool ImLayout::BeginCollapseLayout(const char* id, const char* title, float sizeX, float sizeY, ImGuiCollapseLayoutOptions & options)
{
    bool flag = ImLayout::BeginCollapseLayoutEx(id, title, sizeX, sizeY, options);
    ImLayout::EndCollapseFrameLayout();
    return flag;
}

void ImLayout::BeginCollapseLayout_2(const char* id, bool* isOpen, const char* title, float sizeX, float sizeY)
{
     ImGuiCollapseLayoutOptions options;
     BeginCollapseLayout_2(id, isOpen, title, sizeX, sizeY, options);
}

void ImLayout::BeginCollapseLayout_2(const char* id, bool* isOpen, const char* title, float sizeX, float sizeY, ImGuiCollapseLayoutOptions & options)
{
    ImLayout::BeginCollapseLayoutEx_3(id, isOpen, title, sizeX, sizeY, options);
    ImLayout::EndCollapseFrameLayout();
}

void ImLayout::EndCollapseFrameLayout()
{
    ImGuiContext& g = *GImGui;
    float bk = g.Style.ItemSpacing.y;

    g.Style.ItemSpacing.y = 0;
    ImLayout::EndLayout(); // end frame
    g.Style.ItemSpacing.y = bk;
    ImGuiLayout* rootLayout = ImLayout::GetCurrentLayout();

    float paddingLeft = rootLayout->map.GetFloat(120, 0.0f);
    float paddingRight = rootLayout->map.GetFloat(121, 0.0f);
    float paddingTop = rootLayout->map.GetFloat(122, 0.0f);
    float paddingBottom = rootLayout->map.GetFloat(123, 0.0f);
    ImGuiLayoutOptions op(paddingLeft, paddingRight, paddingTop, paddingBottom);
    ImLayout::BeginLayout("content", ImLayout::MATCH_PARENT, ImLayout::WRAP_PARENT, op);
    ImGuiLayout* collapseLayout = ImLayout::GetCurrentLayout();
}

void ImLayout::EndCollapseLayout()
{
    ImGuiContext& g = *GImGui;
    ImGuiWindow* window = g.CurrentWindow;
    ImDrawList* drawList = window->DrawList;

    ImU32 borderColor = ImGui::GetColorU32(ImVec4(0x40 / 255.0f, 0x40 / 255.0f, 0x49 / 255.0f, 255 / 255.0f));
    int borderRound = 4;
    int roundingCorners = ImDrawFlags_RoundCornersTopLeft | ImDrawFlags_RoundCornersTopRight;

    ImLayout::EndLayout(); // end content

    ImGuiLayout* rootLayout = ImLayout::GetCurrentLayout();

    ImLayout::EndLayout(); // end root

    ImVec2 borderPosition = rootLayout->position;
    ImVec2 borderSize = rootLayout->getAbsoluteSize();

    drawList->AddRect(borderPosition, borderSize, borderColor, borderRound, 1.0f, roundingCorners);
}

void ImLayout::BeginAlign_1(ImGuiID id, float sizeX, float sizeY, float alignX, float alignY, float offsetX, float offsetY, const ImGuiLayoutOptions& options) {
    ImLayout::BeginAlign(id, sizeX, sizeY, alignX, alignY, offsetX, offsetY, options);
}

void ImLayout::BeginAlign_2(const char* id, float sizeX, float sizeY, float alignX, float alignY, float offsetX, float offsetY, const ImGuiLayoutOptions& options) {
    ImLayout::BeginAlign(id, sizeX, sizeY, alignX, alignY, offsetX, offsetY, options);
}


void ImLayout::BeginAlign(ImGuiID id, float sizeX, float sizeY, float alignX, float alignY, float offsetX, float offsetY, const ImGuiLayoutOptions& options) {
    ImLayout::BeginLayout(id, sizeX, sizeY, options);
    ImLayout::AlignLayout(alignX, alignY, offsetX, offsetY);
}

void ImLayout::BeginAlign(const char* strID, float sizeX, float sizeY, float alignX, float alignY, float offsetX, float offsetY, const ImGuiLayoutOptions& options) {
    ImLayout::BeginLayout(strID, sizeX, sizeY, options);
    ImLayout::AlignLayout(alignX, alignY, offsetX, offsetY);
}

void ImLayout::AlignLayout(float alignX, float alignY, float offsetX, float offsetY) {
    ImGuiContext& g = *GImGui;
    ImGuiWindow* window = g.CurrentWindow;
    ImGuiLayout* curLayout = ImLayout::GetCurrentLayout();
    if (curLayout == NULL)
        return;

    ImVec2 regionAvail = ImGui::GetContentRegionAvail();
    float totalX = regionAvail.x;
    float totalY = regionAvail.y;

    ImVec2 posPad = curLayout->getPositionPadding();
    ImVec2 absSizePad = curLayout->getAbsoluteSizePadding();

    if (alignX >= 0.0f && curLayout->contentSize.x > 0.0) {
        float addX = ImFloor((totalX - curLayout->contentSize.x) * alignX);
        float newX = posPad.x + addX + offsetX;
        if (curLayout->isWrapParentX == false) {
            if (newX < posPad.x) {
                //curLayout->error = true;
                //newX = posPad.x;
            }
            else {
                int sizeX = newX + curLayout->contentSize.x;
                if (sizeX > absSizePad.x) {
                    //curLayout->error = true;
                    //newX = absSizePad.x;
                }
            }
            curLayout->positionContents.x = newX;
        }
        else {
            curLayout->positionContents.x = newX;
        }
    }

    if (alignY >= 0.0f && curLayout->contentSize.y > 0.0) {
        float addY = ImFloor((totalY - curLayout->contentSize.y) * alignY);
        float newY = posPad.y + addY + offsetY;
        if (curLayout->isWrapParentY == false) {
            if (newY < posPad.y) {
                //curLayout->error = true;
                //newY = posPad.y;
            }
            else {
                int sizeY = newY + curLayout->contentSize.y;
                if (sizeY > absSizePad.y) {
                    //curLayout->error = true;
                    //newY = absSizePad.y - curLayout->contentSize.y;
                }
            }
            curLayout->positionContents.y = newY;
        }
        else {
            curLayout->positionContents.y = newY;
        }
    }
    window->DC.CursorMaxPos.x = curLayout->positionContents.x;
    window->DC.CursorMaxPos.y = curLayout->positionContents.y;

    window->DC.CursorStartPos.x = curLayout->positionContents.x;
    window->DC.CursorStartPos.y = curLayout->positionContents.y;

    window->Pos.x = curLayout->positionContents.x;
    window->Pos.y = curLayout->positionContents.y;

    window->DC.CursorPos.x = curLayout->positionContents.x;
    window->DC.CursorPos.y = curLayout->positionContents.y;
}

void ImLayout::EndAlign() {
    ImLayout::EndLayout();
}

void ImLayout::BeginBoundingBox() {
    ImGui::BeginGroup();
    ImGuiContext& g = *GImGui;
    ImGuiWindow* window = g.CurrentWindow;
}

ImRect& ImLayout::EndBoundingBox() {
    ImGuiContext& g = *GImGui;
    ImGuiWindow* window = g.CurrentWindow;
    ImGui::EndGroup();
    return g.LastItemData.Rect;
}

float ImLayout::GetTableContentHeight() {
	ImGuiContext& g = *GImGui;
	ImGuiTable* table = g.CurrentTable;
	if (table != NULL) {
		ImGuiWindow* window = table->InnerWindow;
		float y = window->DC.CursorPos.y;
		float itemSpacingY = g.Style.ItemSpacing.y;

		float height = y - table->RowPosY1 - table->RowCellPaddingY - itemSpacingY;

		return height;
	}
	return 0;
}

// ## TREE
static char* KEY_TREE_BEGIN = "KEY_TREE_BEGIN";
static char* KEY_TREE_ID = "KEY_TREE_ID";
static char* KEY_TREE_IS_OPEN = "KEY_TREE_IS_OPEN";
static char* KEY_TREE_NODE_IS_LEAF = "KEY_TREE_NODE_IS_LEAF";

static bool IS_GLOBAL_TREE = false;
// Tree alternative background
static int TREE_COUNT = 0;

void ImLayout::BeginGlobalTree() {
    IS_GLOBAL_TREE = true;
    TREE_COUNT = 0;
}

void ImLayout::EndGlobalTree() {
    IS_GLOBAL_TREE = false;
}

void ImLayout::BeginTree_1(const char* treeIdStr) {
    ImGuiWindow* window = ImGui::GetCurrentWindow();
    ImGuiID id = window->GetID(treeIdStr);
    BeginTree_2(id);
}

void ImLayout::BeginTree_2(int id) {
    ImGuiWindow* window = ImGui::GetCurrentWindow();
    ImGuiStorage* storage = ImGui::GetStateStorage();
    int treeIdBefore = storage->GetInt(ImGui::GetID(KEY_TREE_ID), -1);
    if (treeIdBefore != -1) {
        ImVector<ImGuiID> stack = window->IDStack;
        int treeId = stack.back();
        if (treeIdBefore == treeId) {
            IM_ASSERT("Cannot push same tree multiple times");
        }
    }
    ImGui::PushID(id);
    ImVector<ImGuiID> stack = window->IDStack;
    int treeId = stack.back();

    storage->SetInt(ImGui::GetID(KEY_TREE_ID), treeId);
}

void ImLayout::EndTree() {
    ImGuiStorage* storage = ImGui::GetStateStorage();

    int isOpenId = ImGui::GetID(KEY_TREE_IS_OPEN);
    bool isOpen = storage->GetBool(isOpenId, false);

    if (isOpen) {
        ImGui::TreePop();
    }

    int treeIdKey = ImGui::GetID(KEY_TREE_ID);
    int treeId = storage->GetInt(treeIdKey, -1);
    if (treeId == -1) {
        IM_ASSERT("Begin must be called first");
    }

    ImGuiWindow* window = ImGui::GetCurrentWindow();
    ImVector<ImGuiID> stack = window->IDStack;
    int curID = stack.back();

    if (treeId != curID) {
        IM_ASSERT("Current tree id is wrong");
    }
    storage->SetInt(treeIdKey, -1);

    ImGui::PopID();
}

void Begin(float height, bool isLeaf, bool isSelected, int isOpen) {
    bool debug = false;

    ImGuiWindow* window = ImGui::GetCurrentWindow();
    ImGuiStorage* storage = ImGui::GetStateStorage();
    int isOpenId = ImGui::GetID(KEY_TREE_IS_OPEN);
    int isLeafId = ImGui::GetID(KEY_TREE_NODE_IS_LEAF);
    ImVec2 pos = window->DC.CursorPos;
    float posX = pos.x;
    float posY = pos.y;
    ImRect bb = ImRect(pos, ImVec2(posX + ImGui::GetContentRegionAvail().x, posY + height));
    float minX = bb.Min.x;
    float minY = bb.Min.y;
    float maxX = bb.Max.x;
    float maxY = bb.Max.y;
    ImVec2 bbSize = bb.GetSize();
    float bbSizeX = bbSize.x;
    float bbSizeY = bbSize.y;

    float windowX = ImGui::GetWindowPos().x;
    float windowY = ImGui::GetWindowPos().y;
    ImDrawList* drawlist = ImGui::GetWindowDrawList();

    ImGuiContext& g = *GImGui;

    if (g.NextItemData.OpenCond & ImGuiCond_Always)
    {
        isOpen = g.NextItemData.OpenVal ? 1 : 0;
    }
    else if (isOpen == -1) {
        bool defaultValue = g.NextItemData.OpenCond != 0 ? g.NextItemData.OpenVal : false;
        bool isOpenVal = storage->GetBool(isOpenId, defaultValue);
        isOpen = isOpenVal ? 1 : 0;
    }
    g.NextItemData.OpenCond = ImGuiCond_None;

    if (isLeaf) {
        isOpen = 0;
    }

    if (debug) {
        drawlist->AddRect(bb.Min, bb.Max, IM_COL32(255, 0, 0, 255));
    }
    storage->SetBool(isLeafId, isLeaf);

    const float h = drawlist->_Data->FontSize;

    float arrowWidth = (h * 0.70) * 2 * ImLayout::GetDPIScale();

    float arrowMaxX = minX + arrowWidth;
    ImRect bbArrow = ImRect(bb.Min, ImVec2(arrowMaxX, maxY));
    if (debug) {
        drawlist->AddRect(bbArrow.Min, bbArrow.Max, IM_COL32(255, 0, 0, 255));
    }
    int arrowButtonId = ImGui::GetID("ArrowButton");
    ImGui::ItemSize(bbArrow);
    ImGui::ItemAdd(bbArrow, arrowButtonId);
    ImGui::SameLine(0, 0);

    int layoutId = ImGui::GetID("FullLayout2");
    float scrollOffsetH = ImGui::GetScrollX();
    ImVec2 clipRectMin = ImVec2(windowX, windowY);
    float xMin = clipRectMin.x + scrollOffsetH + ImGui::GetWindowContentRegionMin().x;
    ImRect fullLayout = ImRect(ImVec2(xMin, minY), bb.Max);
    ImGuiStyle style = ImGui::GetStyle();

    {
        TREE_COUNT++;
        if (!IS_GLOBAL_TREE && window->DC.TreeDepth == 0) {
            TREE_COUNT = 0;
        }

        int treeDepth = TREE_COUNT;
        bool isBGAlt = (treeDepth % 2) == 1;
        if (isBGAlt) {
            ImVec4 color = style.Colors[ImGuiCol_TableRowBgAlt];
            float r = color.x;
            float g = color.y;
            float b = color.z;
            float a = color.w;
            int bgAltColor = IM_COL32((int)(255 * r), (int)(255 * g), (int)(255 * b), (int)(255 * a));
            drawlist->AddRectFilled(fullLayout.Min, fullLayout.Max, bgAltColor);
        }
    }

    {
        // Selected color
        if (isSelected) {
            ImVec4 color = style.Colors[ImGuiCol_HeaderActive];
            float r = color.x;
            float g = color.y;
            float b = color.z;
            float a = color.w;
            int selectedColor = IM_COL32((int)(255 * r), (int)(255 * g), (int)(255 * b), (int)(255 * a));
            drawlist->AddRectFilled(fullLayout.Min, fullLayout.Max, selectedColor);
        }
    }

    if (!isLeaf) {
        // Prevent layout hover in arrow bounding box
        ImGui::ItemHoverable(bbArrow, arrowButtonId, ImGuiItemFlags_None);
    }
    // Selected and hover logic
    bool arrowClicked = ImGui::IsItemClicked();

    {
        bool isLayoutHovered = ImGui::ItemHoverable(fullLayout, layoutId, ImGuiItemFlags_AllowOverlap);

        if (isLayoutHovered) {
            ImVec4 color = style.Colors[ImGuiCol_HeaderHovered];
            float r = color.x;
            float g = color.y;
            float b = color.z;
            float a = color.w;
            int hoveredColor = IM_COL32((int)(255 * r), (int)(255 * g), (int)(255 * b), (int)(255 * a));

            drawlist->AddRectFilled(fullLayout.Min, fullLayout.Max, hoveredColor);
        }
    }


    if (!isLeaf) {
        ImGuiDir dir = isOpen == 1 ? ImGuiDir_Down : ImGuiDir_Right;
        if (arrowClicked) {
            isOpen = isOpen == 1 ? 0 : 1;
        }
        float fontSize = ImGui::GetFontSize();
        int arrowColor = IM_COL32(255, 255, 255, 255);
        ImVec2 size = bbArrow.GetSize();
        float sizeX = size.x;
        float sizeY = size.y;
        float iconPosX = minX;
        float iconPosY = minY;
        iconPosX = iconPosX + (sizeX - fontSize) * 0.5f;
        iconPosY = iconPosY + (sizeY - fontSize) * 0.5f;
        ImGui::RenderArrow(drawlist, ImVec2(iconPosX, iconPosY), arrowColor, dir);
    }

    storage->SetBool(isOpenId, isOpen == 1);

    {
        // Remove Y spacing
        ImVec2 itemSpacing = style.ItemSpacing;
        float x = itemSpacing.x;
        ImGui::PushStyleVar(ImGuiStyleVar_ItemSpacing, ImVec2(x, 0));
    }

    ImLayout::BeginAlign("FullLayout2", ImLayout::MATCH_PARENT, height, 0.0, 0.5);
}

void ImLayout::BeginTreeLayout(float height, bool isLeaf, bool selected) {
    Begin(height, isLeaf, selected , -1);
}

void ImLayout::BeginTreeLayout(float height, bool isLeaf, bool selected, bool isOpen) {
    Begin(height, isLeaf, selected, isOpen ? 1 : 0);
}

bool ImLayout::EndTreeLayout() {

    //ImLayout::ShowLayoutDebug();
    bool debug = false;

    bool selected = false;

    ImGuiStorage* storage = ImGui::GetStateStorage();

    //        ImLayout.ShowLayoutDebug();

    ImGuiLayout* layout = ImLayout::GetCurrentLayout();
    ImVec2 absoluteSize = layout->getAbsoluteSize();
    float maxX = absoluteSize.x;
    float maxY = absoluteSize.y;
    float minY = layout->position.y;
    ImLayout::EndAlign();

    ImGuiWindow* window = ImGui::GetCurrentWindow();
    float windowX = ImGui::GetWindowPos().x;
    float windowY = ImGui::GetWindowPos().y;

    int layoutId = ImGui::GetID("FullLayout");
    float scrollOffsetH = ImGui::GetScrollX();
    ImVec2 clipRectMin = ImVec2(windowX, windowY);
    float minX = clipRectMin.x + scrollOffsetH + ImGui::GetWindowContentRegionMin().x;

    ImRect fullLayout = ImRect(ImVec2(minX, minY), ImVec2(maxX, maxY));

    if (debug) {
        ImGui::GetWindowDrawList()->AddRect(fullLayout.Min, fullLayout.Max, IM_COL32(255, 0, 0, 255));
    }

    {
        // Selected and hover logic
        ImGui::ItemAdd(fullLayout, layoutId);
        if (ImGui::ButtonBehavior(fullLayout, layoutId, NULL, NULL)) {
            selected = true;
        }
    }

    ImGui::PopStyleVar();

    int isOpenId = ImGui::GetID(KEY_TREE_IS_OPEN);
    bool isOpen = storage->GetBool(isOpenId, false);

    if (isOpen) {
        ImGui::TreePush("TreePush");
    }
    // We push it again to update data to treepush ID
    storage->SetBool(ImGui::GetID(KEY_TREE_IS_OPEN), isOpen);

    return selected;
}

bool ImLayout::IsTreeOpen() {
    ImGuiStorage* storage = ImGui::GetStateStorage();
    int isOpenId = ImGui::GetID(KEY_TREE_IS_OPEN);
    bool isOpen = storage->GetBool(isOpenId, false);
    return isOpen;
}

float ImLayout::GetTreeHeight(float padding) {
    float fontSize = ImGui::GetFontSize();
    return fontSize + padding;
}

int ImLayout::ButtonBehavior(int id, const ImRect& bb, bool isSelected, ImU32 selectedColor, ImU32 hoveredColor, ImU32 hoveredStrokeColor, ImGuiButtonFlags ImGuiButtonFlags, float clickDelay) {
    ImGui::PushID(id);
    int clicksId = ImGui::GetID("ButtonBehavior_clicks");
    int clickTimeId = ImGui::GetID("ButtonBehavior_clickTime");
    int retClicks = 0;

    float timeNow = ImGui::GetTime();

    ImGuiStorage* storage = ImGui::GetStateStorage();

    int clicks = storage->GetInt(clicksId, 0);
    float clickTime = storage->GetFloat(clickTimeId, 0);

    bool hovered, held;

    if (ImGui::ButtonBehavior(bb, id, &hovered, &held, ImGuiButtonFlags)) {
        if ((ImGuiButtonFlags & ImGuiButtonFlags_PressedOnDoubleClick) == ImGuiButtonFlags_PressedOnDoubleClick) {
            clicks++;
        }
        if (clicks == 0) {
            clickTime = timeNow;
        }
        clicks++;
    }

    if (clicks > 0) {
        float time = timeNow - clickTime;
        if (time > clickDelay) {
            if (clicks == 1) {
                retClicks = clicks;
            }
            else {
                retClicks = clicks;
            }
            clicks = 0;
            clickTime = 0;
        }
    }

    bool value = hovered;
    ImDrawList* drawList = ImGui::GetWindowDrawList();
    if (selectedColor != 0 && isSelected) {
        drawList->AddRectFilled(bb.Min, bb.Max, selectedColor);
    }
    if (value) {
        if (hoveredColor != 0) {
            drawList->AddRectFilled(bb.Min, bb.Max, hoveredColor);
        }
        if (hoveredStrokeColor != 0) {
            drawList->AddRect(bb.Min, bb.Max, hoveredStrokeColor);
        }
    }

    storage->SetInt(clicksId, clicks);
    storage->SetFloat(clickTimeId, clickTime);

    ImGui::PopID();
    
    return retClicks;
}
