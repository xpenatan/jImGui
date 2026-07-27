#pragma once

#include <algorithm>
#include <cstdint>
#include <string>
#include <vector>

#include "TextDiff.h"
#include "TextEditor.h"
#include "imgui.h"
#include "RuntimeHelper.h"

enum class TextEditorLanguage {
    None,
    C,
    Cpp,
    Cs,
    AngelScript,
    Lua,
    Python,
    Glsl,
    Hlsl,
    Json,
    Markdown,
    Sql
};

enum class TextEditorPalette {
    Dark,
    Light
};

enum class TextEditorColor {
    Text = static_cast<int>(::TextEditor::Color::text),
    Keyword = static_cast<int>(::TextEditor::Color::keyword),
    Declaration = static_cast<int>(::TextEditor::Color::declaration),
    Number = static_cast<int>(::TextEditor::Color::number),
    String = static_cast<int>(::TextEditor::Color::string),
    Punctuation = static_cast<int>(::TextEditor::Color::punctuation),
    Preprocessor = static_cast<int>(::TextEditor::Color::preprocessor),
    Identifier = static_cast<int>(::TextEditor::Color::identifier),
    KnownIdentifier = static_cast<int>(::TextEditor::Color::knownIdentifier),
    Comment = static_cast<int>(::TextEditor::Color::comment),
    Background = static_cast<int>(::TextEditor::Color::background),
    Cursor = static_cast<int>(::TextEditor::Color::cursor),
    Selection = static_cast<int>(::TextEditor::Color::selection),
    Whitespace = static_cast<int>(::TextEditor::Color::whitespace),
    MatchingBracketBackground = static_cast<int>(::TextEditor::Color::matchingBracketBackground),
    MatchingBracketActive = static_cast<int>(::TextEditor::Color::matchingBracketActive),
    MatchingBracketLevel1 = static_cast<int>(::TextEditor::Color::matchingBracketLevel1),
    MatchingBracketLevel2 = static_cast<int>(::TextEditor::Color::matchingBracketLevel2),
    MatchingBracketLevel3 = static_cast<int>(::TextEditor::Color::matchingBracketLevel3),
    MatchingBracketError = static_cast<int>(::TextEditor::Color::matchingBracketError),
    LineNumber = static_cast<int>(::TextEditor::Color::lineNumber),
    CurrentLineNumber = static_cast<int>(::TextEditor::Color::currentLineNumber)
};

enum class TextEditorScroll {
    AlignTop = static_cast<int>(::TextEditor::Scroll::alignTop),
    AlignMiddle = static_cast<int>(::TextEditor::Scroll::alignMiddle),
    AlignBottom = static_cast<int>(::TextEditor::Scroll::alignBottom)
};

enum class TextEditorLineBreakRule {
    LB2,
    LB3,
    LB4,
    LB5,
    LB6,
    LB7,
    LB8,
    LB8A,
    LB9,
    LB10,
    LB11,
    LB12,
    LB12A,
    LB13,
    LB14,
    LB15A,
    LB15B,
    LB15C,
    LB15D,
    LB16,
    LB17,
    LB18,
    LB19,
    LB19A,
    LB20,
    LB20A,
    LB21,
    LB21A,
    LB21B,
    LB22,
    LB23,
    LB23A,
    LB24,
    LB25,
    LB26,
    LB27,
    LB28,
    LB28A,
    LB29,
    LB30,
    LB30A,
    LB30B
};

namespace TextEditorWrapper {

using Editor = ::TextEditor;
using DiffViewer = ::TextDiff;

inline std::size_t ToSize(long long value) {
    return value < 0 ? 0 : static_cast<std::size_t>(value);
}

inline Editor::DocPos ToDocPos(long long line, long long index) {
    return Editor::DocPos(ToSize(line), ToSize(index));
}

inline void FromDocPos(const Editor::DocPos& position, long long* line, long long* index) {
    *line = static_cast<long long>(position.line);
    *index = static_cast<long long>(position.index);
}

inline Editor::Color ToColor(TextEditorColor color) {
    return static_cast<Editor::Color>(static_cast<int>(color));
}

inline Editor::Scroll ToScroll(TextEditorScroll scroll) {
    return static_cast<Editor::Scroll>(static_cast<int>(scroll));
}

inline const Editor::Language* ToLanguage(TextEditorLanguage language) {
    switch (language) {
        case TextEditorLanguage::C: return Editor::Language::C();
        case TextEditorLanguage::Cpp: return Editor::Language::Cpp();
        case TextEditorLanguage::Cs: return Editor::Language::Cs();
        case TextEditorLanguage::AngelScript: return Editor::Language::AngelScript();
        case TextEditorLanguage::Lua: return Editor::Language::Lua();
        case TextEditorLanguage::Python: return Editor::Language::Python();
        case TextEditorLanguage::Glsl: return Editor::Language::Glsl();
        case TextEditorLanguage::Hlsl: return Editor::Language::Hlsl();
        case TextEditorLanguage::Json: return Editor::Language::Json();
        case TextEditorLanguage::Markdown: return Editor::Language::Markdown();
        case TextEditorLanguage::Sql: return Editor::Language::Sql();
        case TextEditorLanguage::None:
        default:
            return nullptr;
    }
}

inline TextEditorLanguage FromLanguage(const Editor::Language* language) {
    if (language == Editor::Language::C()) return TextEditorLanguage::C;
    if (language == Editor::Language::Cpp()) return TextEditorLanguage::Cpp;
    if (language == Editor::Language::Cs()) return TextEditorLanguage::Cs;
    if (language == Editor::Language::AngelScript()) return TextEditorLanguage::AngelScript;
    if (language == Editor::Language::Lua()) return TextEditorLanguage::Lua;
    if (language == Editor::Language::Python()) return TextEditorLanguage::Python;
    if (language == Editor::Language::Glsl()) return TextEditorLanguage::Glsl;
    if (language == Editor::Language::Hlsl()) return TextEditorLanguage::Hlsl;
    if (language == Editor::Language::Json()) return TextEditorLanguage::Json;
    if (language == Editor::Language::Markdown()) return TextEditorLanguage::Markdown;
    if (language == Editor::Language::Sql()) return TextEditorLanguage::Sql;
    return TextEditorLanguage::None;
}

inline const Editor::Palette& ToPalette(TextEditorPalette palette) {
    switch (palette) {
        case TextEditorPalette::Light: return Editor::GetLightPalette();
        case TextEditorPalette::Dark:
        default:
            return Editor::GetDarkPalette();
    }
}

class TextEditorLineBreakConfig {
private:
    Editor::LineBreakConfig config;

    bool* GetRule(TextEditorLineBreakRule rule) {
        switch (rule) {
            case TextEditorLineBreakRule::LB2: return &config.lb2;
            case TextEditorLineBreakRule::LB3: return &config.lb3;
            case TextEditorLineBreakRule::LB4: return &config.lb4;
            case TextEditorLineBreakRule::LB5: return &config.lb5;
            case TextEditorLineBreakRule::LB6: return &config.lb6;
            case TextEditorLineBreakRule::LB7: return &config.lb7;
            case TextEditorLineBreakRule::LB8: return &config.lb8;
            case TextEditorLineBreakRule::LB8A: return &config.lb8a;
            case TextEditorLineBreakRule::LB9: return &config.lb9;
            case TextEditorLineBreakRule::LB10: return &config.lb10;
            case TextEditorLineBreakRule::LB11: return &config.lb11;
            case TextEditorLineBreakRule::LB12: return &config.lb12;
            case TextEditorLineBreakRule::LB12A: return &config.lb12a;
            case TextEditorLineBreakRule::LB13: return &config.lb13;
            case TextEditorLineBreakRule::LB14: return &config.lb14;
            case TextEditorLineBreakRule::LB15A: return &config.lb15a;
            case TextEditorLineBreakRule::LB15B: return &config.lb15b;
            case TextEditorLineBreakRule::LB15C: return &config.lb15c;
            case TextEditorLineBreakRule::LB15D: return &config.lb15d;
            case TextEditorLineBreakRule::LB16: return &config.lb16;
            case TextEditorLineBreakRule::LB17: return &config.lb17;
            case TextEditorLineBreakRule::LB18: return &config.lb18;
            case TextEditorLineBreakRule::LB19: return &config.lb19;
            case TextEditorLineBreakRule::LB19A: return &config.lb19a;
            case TextEditorLineBreakRule::LB20: return &config.lb20;
            case TextEditorLineBreakRule::LB20A: return &config.lb20a;
            case TextEditorLineBreakRule::LB21: return &config.lb21;
            case TextEditorLineBreakRule::LB21A: return &config.lb21a;
            case TextEditorLineBreakRule::LB21B: return &config.lb21b;
            case TextEditorLineBreakRule::LB22: return &config.lb22;
            case TextEditorLineBreakRule::LB23: return &config.lb23;
            case TextEditorLineBreakRule::LB23A: return &config.lb23a;
            case TextEditorLineBreakRule::LB24: return &config.lb24;
            case TextEditorLineBreakRule::LB25: return &config.lb25;
            case TextEditorLineBreakRule::LB26: return &config.lb26;
            case TextEditorLineBreakRule::LB27: return &config.lb27;
            case TextEditorLineBreakRule::LB28: return &config.lb28;
            case TextEditorLineBreakRule::LB28A: return &config.lb28a;
            case TextEditorLineBreakRule::LB29: return &config.lb29;
            case TextEditorLineBreakRule::LB30: return &config.lb30;
            case TextEditorLineBreakRule::LB30A: return &config.lb30a;
            case TextEditorLineBreakRule::LB30B: return &config.lb30b;
            default: return &config.lb2;
        }
    }

public:
    void SetUseUnicodeAnnex14(bool value) { config.useUnicodeAnnex14 = value; }
    bool IsUsingUnicodeAnnex14() const { return config.useUnicodeAnnex14; }
    void SetBreakAfter(const char* value) { config.breakAfter = value ? value : ""; }
    std::string GetBreakAfter() const { return config.breakAfter; }
    void SetBreakBefore(const char* value) { config.breakBefore = value ? value : ""; }
    std::string GetBreakBefore() const { return config.breakBefore; }
    void SetRuleEnabled(TextEditorLineBreakRule rule, bool value) { *GetRule(rule) = value; }
    bool IsRuleEnabled(TextEditorLineBreakRule rule) { return *GetRule(rule); }
    Editor::LineBreakConfig GetConfig() const { return config; }
};

class TextEditor {
private:
    Editor editor;

    Editor::AutoCompleteConfig autoCompleteConfig;
    Editor::AutoCompleteState autoCompleteState{};
    std::vector<std::string> autoCompleteSuggestions;
    bool autoCompleteEnabled = false;
    bool autoCompleteRequestPending = false;

    bool changePending = false;
    std::vector<Editor::Change> transactionChanges;
    bool languageChangePending = false;
    std::vector<std::string> identifiers;

    void ApplyAutoCompleteConfig() {
        if (!autoCompleteEnabled) {
            editor.SetAutoCompleteConfig(nullptr);
            return;
        }

        autoCompleteConfig.callback = [this](Editor::AutoCompleteState& state) {
            autoCompleteState = state;
            autoCompleteRequestPending = true;
            state.suggestionsPromise = true;
        };
        editor.SetAutoCompleteConfig(&autoCompleteConfig);
    }

    const Editor::Change* GetTransactionChange(long long index) const {
        const auto safeIndex = ToSize(index);
        return safeIndex < transactionChanges.size() ? &transactionChanges[safeIndex] : nullptr;
    }

public:
    // Configuration
    void SetTabSize(long long value) { editor.SetTabSize(ToSize(value)); }
    long long GetTabSize() const { return static_cast<long long>(editor.GetTabSize()); }
    void SetInsertSpacesOnTabs(bool value) { editor.SetInsertSpacesOnTabs(value); }
    bool IsInsertSpacesOnTabs() const { return editor.IsInsertSpacesOnTabs(); }
    void SetLineSpacing(float value) { editor.SetLineSpacing(value); }
    float GetLineSpacing() const { return editor.GetLineSpacing(); }
    void SetWordWrapEnabled(bool value) { editor.SetWordWrapEnabled(value); }
    bool IsWordWrapEnabled() const { return editor.IsWordWrapEnabled(); }
    void SetReadOnlyEnabled(bool value) { editor.SetReadOnlyEnabled(value); }
    bool IsReadOnlyEnabled() const { return editor.IsReadOnlyEnabled(); }
    void SetCaretsVisible(bool value) { editor.SetCaretsVisible(value); }
    bool IsCaretsVisible() const { return editor.IsCaretsVisible(); }
    void SetAutoIndentEnabled(bool value) { editor.SetAutoIndentEnabled(value); }
    bool IsAutoIndentEnabled() const { return editor.IsAutoIndentEnabled(); }
    void SetShowWhitespacesEnabled(bool value) { editor.SetShowWhitespacesEnabled(value); }
    bool IsShowWhitespacesEnabled() const { return editor.IsShowWhitespacesEnabled(); }
    void SetShowSpacesEnabled(bool value) { editor.SetShowSpacesEnabled(value); }
    bool IsShowSpacesEnabled() const { return editor.IsShowSpacesEnabled(); }
    void SetShowTabsEnabled(bool value) { editor.SetShowTabsEnabled(value); }
    bool IsShowTabsEnabled() const { return editor.IsShowTabsEnabled(); }
    void SetShowLineNumbersEnabled(bool value) { editor.SetShowLineNumbersEnabled(value); }
    bool IsShowLineNumbersEnabled() const { return editor.IsShowLineNumbersEnabled(); }
    void SetShowMiniMapEnabled(bool value) { editor.SetShowMiniMapEnabled(value); }
    bool IsShowMiniMapEnabled() const { return editor.IsShowMiniMapEnabled(); }
    void SetMiniMapColumns(long long value) { editor.SetMiniMapColumns(ToSize(value)); }
    long long GetMiniMapColumns() const { return static_cast<long long>(editor.GetMiniMapColumns()); }
    void SetShowScrollbarMiniMapEnabled(bool value) { editor.SetShowScrollbarMiniMapEnabled(value); }
    bool IsShowScrollbarMiniMapEnabled() const { return editor.IsShowScrollbarMiniMapEnabled(); }
    void SetShowPanScrollIndicatorEnabled(bool value) { editor.SetShowPanScrollIndicatorEnabled(value); }
    bool IsShowPanScrollIndicatorEnabled() const { return editor.IsShowPanScrollIndicatorEnabled(); }
    void SetShowMatchingBrackets(bool value) { editor.SetShowMatchingBrackets(value); }
    bool IsShowingMatchingBrackets() const { return editor.IsShowingMatchingBrackets(); }
    void SetCompletePairedGlyphs(bool value) { editor.SetCompletePairedGlyphs(value); }
    bool IsCompletingPairedGlyphs() const { return editor.IsCompletingPairedGlyphs(); }
    void SetLineFoldingEnabled(bool value) { editor.SetLineFoldingEnabled(value); }
    bool IsLineFoldingEnabled() const { return editor.IsLineFoldingEnabled(); }
    void SetOverwriteEnabled(bool value) { editor.SetOverwriteEnabled(value); }
    bool IsOverwriteEnabled() const { return editor.IsOverwriteEnabled(); }
    void SetMiddleMousePanMode() { editor.SetMiddleMousePanMode(); }
    void SetMiddleMouseScrollMode() { editor.SetMiddleMouseScrollMode(); }
    bool IsMiddleMousePanMode() const { return editor.IsMiddleMousePanMode(); }
    void SetLineNumberLeftMargin(long long value) { editor.SetLineNumberLeftMargin(ToSize(value)); }
    long long GetLineNumberLeftMargin() const { return static_cast<long long>(editor.GetLineNumberLeftMargin()); }
    void SetDecorationLeftMargin(long long value) { editor.SetDecorationLeftMargin(ToSize(value)); }
    long long GetDecorationLeftMargin() const { return static_cast<long long>(editor.GetDecorationLeftMargin()); }
    void SetTextLeftMargin(long long value) { editor.SetTextLeftMargin(ToSize(value)); }
    long long GetTextLeftMargin() const { return static_cast<long long>(editor.GetTextLeftMargin()); }

    // Text
    void SetText(const char* text) { editor.SetText(text ? text : ""); }
    std::string GetText() const { return editor.GetText(); }
    std::string GetCursorText(long long cursor) const { return editor.GetCursorText(ToSize(cursor)); }
    std::string GetLineText(long long line) const { return editor.GetLineText(ToSize(line)); }
    std::string GetSectionText(long long startLine, long long startIndex, long long endLine, long long endIndex) const {
        return editor.GetSectionText(ToDocPos(startLine, startIndex), ToDocPos(endLine, endIndex));
    }
    void ReplaceSectionText(long long startLine, long long startIndex, long long endLine, long long endIndex, const char* text) {
        editor.ReplaceSectionText(ToDocPos(startLine, startIndex), ToDocPos(endLine, endIndex), text ? text : "");
    }
    void ClearText() { editor.ClearText(); }
    bool IsEmpty() const { return editor.IsEmpty(); }
    long long GetLineCount() const { return static_cast<long long>(editor.GetLineCount()); }

    // Rendering and focus
    void Render(
        const char* title,
        const ImVec2& size = ImVec2(),
        int childFlags = 0,
        int windowFlags = ImGuiWindowFlags_NoNavInputs | ImGuiWindowFlags_NoMove | ImGuiWindowFlags_HorizontalScrollbar) {
        editor.Render(title, size, childFlags, windowFlags);
    }
    void SetFocus() { editor.SetFocus(); }

    // Clipboard and history
    void Cut() { editor.Cut(); }
    void Copy() const { editor.Copy(); }
    void Paste() { editor.Paste(); }
    void Undo() { editor.Undo(); }
    void Redo() { editor.Redo(); }
    bool CanUndo() const { return editor.CanUndo(); }
    bool CanRedo() const { return editor.CanRedo(); }
    long long GetUndoIndex() const { return static_cast<long long>(editor.GetUndoIndex()); }

    // Cursors and selections
    void SelectAll() { editor.SelectAll(); }
    void SelectLine(long long line) { editor.SelectLine(ToSize(line)); }
    void SelectLines(long long start, long long end) { editor.SelectLines(ToSize(start), ToSize(end)); }
    void SelectRegion(long long startLine, long long startIndex, long long endLine, long long endIndex) {
        editor.SelectRegion(ToDocPos(startLine, startIndex), ToDocPos(endLine, endIndex));
    }
    void SelectToBrackets(bool includeBrackets = true) { editor.SelectToBrackets(includeBrackets); }
    void GrowSelections() { editor.GrowSelections(); }
    void ShrinkSelections() { editor.ShrinkSelections(); }
    void AddNextOccurrence() { editor.AddNextOccurrence(); }
    void SelectAllOccurrences() { editor.SelectAllOccurrences(); }
    bool AnyCursorHasSelection() const { return editor.AnyCursorHasSelection(); }
    bool AllCursorsHaveSelection() const { return editor.AllCursorsHaveSelection(); }
    bool CurrentCursorHasSelection() const { return editor.CurrentCursorHasSelection(); }
    void ClearCursors() { editor.ClearCursors(); }
    long long GetNumberOfCursors() const { return static_cast<long long>(editor.GetNumberOfCursors()); }

    void GetCursorPosition(long long cursor, long long* outLine, long long* outIndex) const {
        FromDocPos(editor.GetCursorPosition(ToSize(cursor)), outLine, outIndex);
    }
    void GetMainCursorPosition(long long* outLine, long long* outIndex) const {
        FromDocPos(editor.GetMainCursorPosition(), outLine, outIndex);
    }
    void GetCurrentCursorPosition(long long* outLine, long long* outIndex) const {
        FromDocPos(editor.GetCurrentCursorPosition(), outLine, outIndex);
    }
    void GetCursorSelection(
        long long cursor,
        long long* outStartLine,
        long long* outStartIndex,
        long long* outEndLine,
        long long* outEndIndex) const {
        const auto selection = editor.GetCursorSelection(ToSize(cursor));
        FromDocPos(selection.start, outStartLine, outStartIndex);
        FromDocPos(selection.end, outEndLine, outEndIndex);
    }
    void GetMainCursorSelection(
        long long* outStartLine,
        long long* outStartIndex,
        long long* outEndLine,
        long long* outEndIndex) const {
        const auto selection = editor.GetMainCursorSelection();
        FromDocPos(selection.start, outStartLine, outStartIndex);
        FromDocPos(selection.end, outEndLine, outEndIndex);
    }
    void GetCurrentCursorSelection(
        long long* outStartLine,
        long long* outStartIndex,
        long long* outEndLine,
        long long* outEndIndex) const {
        const auto selection = editor.GetCurrentCursorSelection();
        FromDocPos(selection.start, outStartLine, outStartIndex);
        FromDocPos(selection.end, outEndLine, outEndIndex);
    }

    // Mouse position
    bool IsMousePosOverGlyph(const ImVec2& mousePos) const { return editor.IsMousePosOverGlyph(mousePos); }
    void GetDocPosAtMousePos(const ImVec2& mousePos, long long* outLine, long long* outIndex) const {
        FromDocPos(editor.GetDocPosAtMousePos(mousePos), outLine, outIndex);
    }
    std::string GetWordAtMousePos(const ImVec2& mousePos) const { return editor.GetWordAtMousePos(mousePos); }

    // Scrolling and coordinates
    void ScrollToLine(long long line, TextEditorScroll alignment) { editor.ScrollToLine(ToSize(line), ToScroll(alignment)); }
    long long GetFirstVisibleRow() const { return static_cast<long long>(editor.GetFirstVisibleRow()); }
    long long GetLastVisibleRow() const { return static_cast<long long>(editor.GetLastVisibleRow()); }
    long long GetFirstVisibleColumn() const { return static_cast<long long>(editor.GetFirstVisibleColumn()); }
    long long GetLastVisibleColumn() const { return static_cast<long long>(editor.GetLastVisibleColumn()); }
    void SetCursor(long long line, long long index) { editor.SetCursor(ToDocPos(line, index)); }
    float GetLineHeight() const { return editor.GetLineHeight(); }
    float GetGlyphWidth() const { return editor.GetGlyphWidth(); }
    void DocPos2VisPos(long long line, long long index, long long* outRow, long long* outColumn) const {
        const auto position = editor.DocPos2VisPos(ToDocPos(line, index));
        *outRow = static_cast<long long>(position.row);
        *outColumn = static_cast<long long>(position.column);
    }
    void VisPos2DocPos(long long row, long long column, long long* outLine, long long* outIndex) const {
        FromDocPos(editor.VisPos2DocPos(Editor::VisPos(ToSize(row), ToSize(column))), outLine, outIndex);
    }
    bool IsDocPosVisible(long long line, long long index) const { return editor.IsDocPosVisible(ToDocPos(line, index)); }
    bool IsVisPosOverGlyph(long long row, long long column) const {
        return editor.IsVisPosOverGlyph(Editor::VisPos(ToSize(row), ToSize(column)));
    }

    // Find and replace
    void SelectFirstOccurrenceOf(const char* text, bool caseSensitive = true, bool wholeWord = false) {
        editor.SelectFirstOccurrenceOf(text ? text : "", caseSensitive, wholeWord);
    }
    void SelectNextOccurrenceOf(const char* text, bool caseSensitive = true, bool wholeWord = false) {
        editor.SelectNextOccurrenceOf(text ? text : "", caseSensitive, wholeWord);
    }
    void SelectAllOccurrencesOf(const char* text, bool caseSensitive = true, bool wholeWord = false) {
        editor.SelectAllOccurrencesOf(text ? text : "", caseSensitive, wholeWord);
    }
    void ReplaceTextInCurrentCursor(const char* text) { editor.ReplaceTextInCurrentCursor(text ? text : ""); }
    void ReplaceTextInAllCursors(const char* text) { editor.ReplaceTextInAllCursors(text ? text : ""); }
    void OpenFindReplaceWindow() { editor.OpenFindReplaceWindow(); }
    void CloseFindReplaceWindow() { editor.CloseFindReplaceWindow(); }
    void SetFindButtonLabel(const char* label) { editor.SetFindButtonLabel(label ? label : ""); }
    void SetFindAllButtonLabel(const char* label) { editor.SetFindAllButtonLabel(label ? label : ""); }
    void SetReplaceButtonLabel(const char* label) { editor.SetReplaceButtonLabel(label ? label : ""); }
    void SetReplaceAllButtonLabel(const char* label) { editor.SetReplaceAllButtonLabel(label ? label : ""); }
    bool HasFindString() const { return editor.HasFindString(); }
    void FindNext() { editor.FindNext(); }
    void FindAll() { editor.FindAll(); }

    // Markers
    void AddMarker(
        long long line,
        ImU32 lineNumberColor,
        ImU32 textColor,
        const char* lineNumberTooltip,
        const char* textTooltip) {
        editor.AddMarker(
            ToSize(line),
            lineNumberColor,
            textColor,
            lineNumberTooltip ? lineNumberTooltip : "",
            textTooltip ? textTooltip : "");
    }
    void ClearMarkers() { editor.ClearMarkers(); }
    bool HasMarkers() const { return editor.HasMarkers(); }

    // Change reporting
    void SetChangeTrackingEnabled(bool enabled, int delay = 0) {
        changePending = false;
        if (enabled) {
            editor.SetChangeCallback([this]() { changePending = true; }, delay);
        } else {
            editor.SetChangeCallback(nullptr);
        }
    }
    bool HasPendingChange() const { return changePending; }
    void ClearPendingChange() { changePending = false; }

    void SetTransactionTrackingEnabled(bool enabled) {
        transactionChanges.clear();
        if (enabled) {
            editor.SetTransactionCallback([this](const std::vector<Editor::Change>& changes) {
                transactionChanges = changes;
            });
        } else {
            editor.SetTransactionCallback(nullptr);
        }
    }
    long long GetTransactionChangeCount() const { return static_cast<long long>(transactionChanges.size()); }
    void ClearTransactionChanges() { transactionChanges.clear(); }
    bool IsTransactionChangeInsert(long long index) const {
        const auto* change = GetTransactionChange(index);
        return change ? change->insert : false;
    }
    void GetTransactionChangeStart(long long index, long long* outLine, long long* outGlyphIndex) const {
        const auto* change = GetTransactionChange(index);
        FromDocPos(change ? change->start : Editor::DocPos(), outLine, outGlyphIndex);
    }
    void GetTransactionChangeEnd(long long index, long long* outLine, long long* outGlyphIndex) const {
        const auto* change = GetTransactionChange(index);
        FromDocPos(change ? change->end : Editor::DocPos(), outLine, outGlyphIndex);
    }
    std::string GetTransactionChangeText(long long index) const {
        const auto* change = GetTransactionChange(index);
        return change ? change->text : "";
    }

    // Opaque per-line user data
    void SetUserData(long long line, long long data) {
        editor.SetUserData(ToSize(line), reinterpret_cast<void*>(static_cast<std::uintptr_t>(data)));
    }
    long long GetUserData(long long line) const {
        return static_cast<long long>(reinterpret_cast<std::uintptr_t>(editor.GetUserData(ToSize(line))));
    }

    // Folding
    void FoldAroundLine(long long line) { editor.FoldAroundLine(ToSize(line)); }
    void UnfoldAroundLine(long long line) { editor.UnfoldAroundLine(ToSize(line)); }
    void ToggleAtLine(long long line) { editor.ToggleAtLine(ToSize(line)); }
    void UnfoldAll() { editor.UnfoldAll(); }
    bool IsLineFoldable(long long line) const { return editor.IsLineFoldable(ToSize(line)); }
    bool IsLineFolded(long long line) const { return editor.IsLineFolded(ToSize(line)); }
    bool IsLineVisible(long long line) const { return editor.IsLineVisible(ToSize(line)); }
    bool IsLineHidden(long long line) const { return editor.IsLineHidden(ToSize(line)); }

    // Built-in text transformations
    void IndentLines() { editor.IndentLines(); }
    void DeindentLines() { editor.DeindentLines(); }
    void MoveUpLines() { editor.MoveUpLines(); }
    void MoveDownLines() { editor.MoveDownLines(); }
    void ToggleComments() { editor.ToggleComments(); }
    void SelectionToLowerCase() { editor.SelectionToLowerCase(); }
    void SelectionToUpperCase() { editor.SelectionToUpperCase(); }
    void StripTrailingWhitespaces() { editor.StripTrailingWhitespaces(); }
    void TabsToSpaces() { editor.TabsToSpaces(); }
    void SpacesToTabs() { editor.SpacesToTabs(); }

    // Palettes
    void SetPalette(TextEditorPalette palette) { editor.SetPalette(ToPalette(palette)); }
    void SetPaletteColor(TextEditorColor color, ImU32 value) {
        auto palette = editor.GetPalette();
        palette[static_cast<std::size_t>(ToColor(color))] = value;
        editor.SetPalette(palette);
    }
    ImU32 GetPaletteColor(TextEditorColor color) const { return editor.GetPalette().get(ToColor(color)); }
    static void SetDefaultPalette(TextEditorPalette palette) { Editor::SetDefaultPalette(ToPalette(palette)); }
    static void SetDefaultPaletteColor(TextEditorColor color, ImU32 value) {
        auto palette = Editor::GetDefaultPalette();
        palette[static_cast<std::size_t>(ToColor(color))] = value;
        Editor::SetDefaultPalette(palette);
    }
    static ImU32 GetDefaultPaletteColor(TextEditorColor color) {
        return Editor::GetDefaultPalette().get(ToColor(color));
    }

    // Languages
    void SetLanguage(TextEditorLanguage language) { editor.SetLanguage(ToLanguage(language)); }
    TextEditorLanguage GetLanguage() const { return FromLanguage(editor.GetLanguage()); }
    bool HasLanguage() const { return editor.HasLanguage(); }
    std::string GetLanguageName() const { return editor.GetLanguageName(); }
    void SetLanguageChangeTrackingEnabled(bool enabled) {
        languageChangePending = false;
        if (enabled) {
            editor.SetLanguageChangeCallback([this]() { languageChangePending = true; });
        } else {
            editor.SetLanguageChangeCallback(nullptr);
        }
    }
    bool HasPendingLanguageChange() const { return languageChangePending; }
    void ClearPendingLanguageChange() { languageChangePending = false; }
    void CollectIdentifiers() {
        identifiers.clear();
        editor.IterateIdentifiers([this](const std::string& identifier) { identifiers.push_back(identifier); });
        std::sort(identifiers.begin(), identifiers.end());
    }
    long long GetIdentifierCount() const { return static_cast<long long>(identifiers.size()); }
    std::string GetIdentifier(long long index) const {
        const auto safeIndex = ToSize(index);
        return safeIndex < identifiers.size() ? identifiers[safeIndex] : "";
    }

    // Autocomplete
    void SetAutoCompleteEnabled(bool enabled) {
        autoCompleteEnabled = enabled;
        autoCompleteRequestPending = false;
        ApplyAutoCompleteConfig();
    }
    bool IsAutoCompleteEnabled() const { return autoCompleteEnabled; }
    void SetAutoCompleteTriggerOnTyping(bool value) { autoCompleteConfig.triggerOnTyping = value; ApplyAutoCompleteConfig(); }
    bool IsAutoCompleteTriggerOnTyping() const { return autoCompleteConfig.triggerOnTyping; }
    void SetAutoCompleteTriggerOnShortcut(bool value) { autoCompleteConfig.triggerOnShortcut = value; ApplyAutoCompleteConfig(); }
    bool IsAutoCompleteTriggerOnShortcut() const { return autoCompleteConfig.triggerOnShortcut; }
    void SetAutoCompleteTriggerInComments(bool value) { autoCompleteConfig.triggerInComments = value; ApplyAutoCompleteConfig(); }
    bool IsAutoCompleteTriggerInComments() const { return autoCompleteConfig.triggerInComments; }
    void SetAutoCompleteTriggerInStrings(bool value) { autoCompleteConfig.triggerInStrings = value; ApplyAutoCompleteConfig(); }
    bool IsAutoCompleteTriggerInStrings() const { return autoCompleteConfig.triggerInStrings; }
    void SetAutoCompleteTriggerShortcut(int value) { autoCompleteConfig.triggerShortcut = value; ApplyAutoCompleteConfig(); }
    int GetAutoCompleteTriggerShortcut() const { return autoCompleteConfig.triggerShortcut; }
    void SetAutoInsertSingleSuggestions(bool value) { autoCompleteConfig.autoInsertSingleSuggestions = value; ApplyAutoCompleteConfig(); }
    bool IsAutoInsertSingleSuggestions() const { return autoCompleteConfig.autoInsertSingleSuggestions; }
    void SetAutoCompleteTriggerDelay(int milliseconds) {
        autoCompleteConfig.triggerDelay = std::chrono::milliseconds(std::max(0, milliseconds));
        ApplyAutoCompleteConfig();
    }
    int GetAutoCompleteTriggerDelay() const {
        return static_cast<int>(autoCompleteConfig.triggerDelay.count());
    }
    void SetNoSuggestionsLabel(const char* label) {
        autoCompleteConfig.noSuggestionsLabel = label ? label : "";
        ApplyAutoCompleteConfig();
    }
    std::string GetNoSuggestionsLabel() const { return autoCompleteConfig.noSuggestionsLabel; }
    void SetSuggestionWidth(long long value) { autoCompleteConfig.suggestionWidth = ToSize(value); ApplyAutoCompleteConfig(); }
    long long GetSuggestionWidth() const { return static_cast<long long>(autoCompleteConfig.suggestionWidth); }
    bool HasPendingAutoCompleteRequest() const { return autoCompleteRequestPending; }
    void ClearPendingAutoCompleteRequest() { autoCompleteRequestPending = false; }
    std::string GetAutoCompleteSearchTerm() const { return autoCompleteState.searchTerm; }
    void GetAutoCompleteSearchTermStart(long long* outLine, long long* outIndex) const {
        FromDocPos(autoCompleteState.searchTermStart, outLine, outIndex);
    }
    void GetAutoCompleteSearchTermEnd(long long* outLine, long long* outIndex) const {
        FromDocPos(autoCompleteState.searchTermEnd, outLine, outIndex);
    }
    bool IsAutoCompleteInIdentifier() const { return autoCompleteState.inIdentifier; }
    bool IsAutoCompleteInNumber() const { return autoCompleteState.inNumber; }
    bool IsAutoCompleteInComment() const { return autoCompleteState.inComment; }
    bool IsAutoCompleteInString() const { return autoCompleteState.inString; }
    TextEditorLanguage GetAutoCompleteLanguage() const { return FromLanguage(autoCompleteState.language); }
    void ClearAutoCompleteSuggestions() { autoCompleteSuggestions.clear(); }
    void AddAutoCompleteSuggestion(const char* suggestion) {
        autoCompleteSuggestions.emplace_back(suggestion ? suggestion : "");
    }
    void SubmitAutoCompleteSuggestions() {
        editor.SetAutoCompleteSuggestions(autoCompleteSuggestions);
        autoCompleteRequestPending = false;
    }

    // Unicode line breaking
    void SetLineBreakConfig(const TextEditorLineBreakConfig* config) {
        if (config) {
            auto value = config->GetConfig();
            editor.SetLineBreakConfig(value);
        }
    }
};

class TextDiff {
private:
    DiffViewer diff;

public:
    void SetSideBySideMode(bool value) { diff.SetSideBySideMode(value); }
    bool GetSideBySideMode() const { return diff.GetSideBySideMode(); }
    void SetTabSize(long long value) { diff.SetTabSize(ToSize(value)); }
    long long GetTabSize() const { return static_cast<long long>(diff.GetTabSize()); }
    void SetLineSpacing(float value) { diff.SetLineSpacing(value); }
    float GetLineSpacing() const { return diff.GetLineSpacing(); }
    void SetWordWrapEnabled(bool value) { diff.SetWordWrapEnabled(value); }
    bool IsWordWrapEnabled() const { return diff.IsWordWrapEnabled(); }
    void SetShowWhitespacesEnabled(bool value) { diff.SetShowWhitespacesEnabled(value); }
    bool IsShowWhitespacesEnabled() const { return diff.IsShowWhitespacesEnabled(); }
    void SetShowSpacesEnabled(bool value) { diff.SetShowSpacesEnabled(value); }
    bool IsShowSpacesEnabled() const { return diff.IsShowSpacesEnabled(); }
    void SetShowTabsEnabled(bool value) { diff.SetShowTabsEnabled(value); }
    bool IsShowTabsEnabled() const { return diff.IsShowTabsEnabled(); }
    void SetShowScrollbarMiniMapEnabled(bool value) { diff.SetShowScrollbarMiniMapEnabled(value); }
    bool IsShowScrollbarMiniMapEnabled() const { return diff.IsShowScrollbarMiniMapEnabled(); }
    void SetLanguage(TextEditorLanguage language) { diff.SetLanguage(ToLanguage(language)); }
    TextEditorLanguage GetLanguage() const { return FromLanguage(diff.GetLanguage()); }
    void SetColors(ImU32 addedColor, ImU32 deletedColor) { diff.SetColors(addedColor, deletedColor); }
    void SetPalette(TextEditorPalette palette) { diff.SetPalette(ToPalette(palette)); }
    void SetPaletteColor(TextEditorColor color, ImU32 value) {
        auto palette = diff.GetPalette();
        palette[static_cast<std::size_t>(ToColor(color))] = value;
        diff.SetPalette(palette);
    }
    ImU32 GetPaletteColor(TextEditorColor color) const { return diff.GetPalette().get(ToColor(color)); }
    void SetFocus() { diff.SetFocus(); }
    void SetText(const char* left, const char* right) { diff.SetText(left ? left : "", right ? right : ""); }
    void Render(
        const char* title,
        const ImVec2& size = ImVec2(),
        int childFlags = 0,
        int windowFlags = ImGuiWindowFlags_NoNavInputs | ImGuiWindowFlags_NoMove) {
        diff.Render(title, size, childFlags, windowFlags);
    }
};

}
