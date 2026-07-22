package io.github.libfdx.imgui;

import imgui.ImGui;
import imgui.ImGuiContext;
import imgui.ImGuiIO;
import imgui.enums.ImGuiKey;
import imgui.enums.ImGuiMouseSource;
import io.github.libfdx.input.InputAdapter;
import io.github.libfdx.input.Input;
import io.github.libfdx.input.Key;
import io.github.libfdx.input.KeyEvent;
import io.github.libfdx.input.MouseButton;
import io.github.libfdx.input.PointerEvent;
import io.github.libfdx.input.PointerType;
import io.github.libfdx.input.TextInputEvent;
import io.github.libfdx.input.TouchEvent;
import io.github.libfdx.input.TouchPoint;

final class FdxImGuiInputBridge extends InputAdapter {
    private final ImGuiContext context;
    private final Input input;

    FdxImGuiInputBridge(ImGuiContext context, Input input) {
        this.context = context;
        this.input = input;
    }

    @Override
    public boolean keyDown(KeyEvent event) {
        ImGuiKey key = mapKey(event.key());
        if (key == ImGuiKey.None) {
            return false;
        }
        ImGui.SetCurrentContext(context);
        ImGuiIO io = ImGui.GetIO();
        io.AddKeyEvent(key, true);
        return io.get_WantCaptureKeyboard();
    }

    @Override
    public boolean keyUp(KeyEvent event) {
        ImGuiKey key = mapKey(event.key());
        if (key == ImGuiKey.None) {
            return false;
        }
        ImGui.SetCurrentContext(context);
        ImGuiIO io = ImGui.GetIO();
        io.AddKeyEvent(key, false);
        return io.get_WantCaptureKeyboard();
    }

    @Override
    public boolean pointerDown(PointerEvent event) {
        ImGuiIO io = beginPointer(event.type(), event.x(), event.y());
        int button = mapButton(event.button());
        if (button >= 0) {
            io.AddMouseButtonEvent(button, true);
        }
        return io.get_WantCaptureMouse();
    }

    @Override
    public boolean pointerUp(PointerEvent event) {
        ImGuiIO io = beginPointer(event.type(), event.x(), event.y());
        int button = mapButton(event.button());
        if (button >= 0) {
            io.AddMouseButtonEvent(button, false);
        }
        return io.get_WantCaptureMouse();
    }

    @Override
    public boolean pointerMoved(PointerEvent event) {
        ImGuiIO io = beginPointer(event.type(), event.x(), event.y());
        return io.get_WantCaptureMouse();
    }

    @Override
    public boolean scrolled(PointerEvent event) {
        ImGuiIO io = beginPointer(event.type(), event.x(), event.y());
        io.AddMouseWheelEvent(event.scrollX(), -event.scrollY());
        return io.get_WantCaptureMouse();
    }

    @Override
    public boolean touchDown(TouchEvent event) {
        TouchPoint point = event.point();
        if (point == null) {
            return false;
        }
        ImGuiIO io = beginPointer(PointerType.TOUCH, point.x(), point.y());
        io.AddMouseButtonEvent(0, true);
        return io.get_WantCaptureMouse();
    }

    @Override
    public boolean touchUp(TouchEvent event) {
        TouchPoint point = event.point();
        if (point == null) {
            return false;
        }
        ImGuiIO io = beginPointer(PointerType.TOUCH, point.x(), point.y());
        io.AddMouseButtonEvent(0, false);
        return io.get_WantCaptureMouse();
    }

    @Override
    public boolean touchMoved(TouchEvent event) {
        TouchPoint point = event.point();
        if (point == null) {
            return false;
        }
        ImGuiIO io = beginPointer(PointerType.TOUCH, point.x(), point.y());
        return io.get_WantCaptureMouse();
    }

    @Override
    public boolean textInput(TextInputEvent event) {
        if (event.composition() || event.text().isEmpty()) {
            return false;
        }
        ImGui.SetCurrentContext(context);
        ImGuiIO io = ImGui.GetIO();
        io.AddInputCharactersUTF8(event.text());
        return io.get_WantCaptureKeyboard() || io.get_WantTextInput();
    }

    private ImGuiIO beginPointer(PointerType type, int x, int y) {
        ImGui.SetCurrentContext(context);
        ImGuiIO io = ImGui.GetIO();
        io.AddMouseSourceEvent(mapSource(type));
        if ((io.get_ConfigFlags().getValue() & imgui.enums.ImGuiConfigFlags.ViewportsEnable.getValue()) != 0) {
            io.AddMousePosEvent(input.pointerScreenX(), input.pointerScreenY());
        } else {
            io.AddMousePosEvent(x, y);
        }
        return io;
    }

    private ImGuiMouseSource mapSource(PointerType type) {
        if (type == PointerType.TOUCH) {
            return ImGuiMouseSource.TouchScreen;
        }
        if (type == PointerType.PEN) {
            return ImGuiMouseSource.Pen;
        }
        return ImGuiMouseSource.Mouse;
    }

    private ImGuiKey mapKey(Key key) {
        if (key == null) {
            return ImGuiKey.None;
        }
        return switch (key) {
            case TAB -> ImGuiKey.Tab;
            case LEFT -> ImGuiKey.LeftArrow;
            case RIGHT -> ImGuiKey.RightArrow;
            case UP -> ImGuiKey.UpArrow;
            case DOWN -> ImGuiKey.DownArrow;
            case PAGE_UP -> ImGuiKey.PageUp;
            case PAGE_DOWN -> ImGuiKey.PageDown;
            case HOME -> ImGuiKey.Home;
            case END -> ImGuiKey.End;
            case DELETE -> ImGuiKey.Delete;
            case BACKSPACE -> ImGuiKey.Backspace;
            case SPACE -> ImGuiKey.Space;
            case ENTER -> ImGuiKey.Enter;
            case ESCAPE -> ImGuiKey.Escape;
            case CONTROL_LEFT -> ImGuiKey.LeftCtrl;
            case SHIFT_LEFT -> ImGuiKey.LeftShift;
            case ALT_LEFT -> ImGuiKey.LeftAlt;
            case CONTROL_RIGHT -> ImGuiKey.RightCtrl;
            case SHIFT_RIGHT -> ImGuiKey.RightShift;
            case ALT_RIGHT -> ImGuiKey.RightAlt;
            case A -> ImGuiKey.A;
            case B -> ImGuiKey.B;
            case C -> ImGuiKey.C;
            case D -> ImGuiKey.D;
            case E -> ImGuiKey.E;
            case F -> ImGuiKey.F;
            case G -> ImGuiKey.G;
            case H -> ImGuiKey.H;
            case I -> ImGuiKey.I;
            case J -> ImGuiKey.J;
            case K -> ImGuiKey.K;
            case L -> ImGuiKey.L;
            case M -> ImGuiKey.M;
            case N -> ImGuiKey.N;
            case O -> ImGuiKey.O;
            case P -> ImGuiKey.P;
            case Q -> ImGuiKey.Q;
            case R -> ImGuiKey.R;
            case S -> ImGuiKey.S;
            case T -> ImGuiKey.T;
            case U -> ImGuiKey.U;
            case V -> ImGuiKey.V;
            case W -> ImGuiKey.W;
            case X -> ImGuiKey.X;
            case Y -> ImGuiKey.Y;
            case Z -> ImGuiKey.Z;
            case NUM_0 -> ImGuiKey.Num_0;
            case NUM_1 -> ImGuiKey.Num_1;
            case NUM_2 -> ImGuiKey.Num_2;
            case NUM_3 -> ImGuiKey.Num_3;
            case NUM_4 -> ImGuiKey.Num_4;
            case NUM_5 -> ImGuiKey.Num_5;
            case NUM_6 -> ImGuiKey.Num_6;
            case NUM_7 -> ImGuiKey.Num_7;
            case NUM_8 -> ImGuiKey.Num_8;
            case NUM_9 -> ImGuiKey.Num_9;
            case F1 -> ImGuiKey.F1;
            case F2 -> ImGuiKey.F2;
            case F3 -> ImGuiKey.F3;
            case F4 -> ImGuiKey.F4;
            case F5 -> ImGuiKey.F5;
            case F6 -> ImGuiKey.F6;
            case F7 -> ImGuiKey.F7;
            case F8 -> ImGuiKey.F8;
            case F9 -> ImGuiKey.F9;
            case F10 -> ImGuiKey.F10;
            case F11 -> ImGuiKey.F11;
            case F12 -> ImGuiKey.F12;
            case BACK -> ImGuiKey.AppBack;
            default -> ImGuiKey.None;
        };
    }

    private int mapButton(MouseButton button) {
        if (button == MouseButton.LEFT) {
            return 0;
        }
        if (button == MouseButton.RIGHT) {
            return 1;
        }
        if (button == MouseButton.MIDDLE) {
            return 2;
        }
        if (button == MouseButton.BACK) {
            return 3;
        }
        if (button == MouseButton.FORWARD) {
            return 4;
        }
        return -1;
    }
}
