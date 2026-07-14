package imgui.gdx;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.IntArray;

/**
 * This class will process ImGui inputs and other inputs added to the list			<br>
 * If inputs are triggered inside a imgui window it will pass to its children.		<br>
 * It will also block input that is not for imgui.								<br>
 *
 * @author xpenatan
 */
public class ImGuiGdxInputMultiplexer extends InputMultiplexer {

    private ImGuiGdxInput inputProcessor;

    private IntArray keyDown = new IntArray();

    public ImGuiGdxInputMultiplexer() {
        this.inputProcessor = new ImGuiGdxInput();
    }

    public ImGuiGdxInputMultiplexer(ImGuiGdxInput inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(inputProcessor.touchDown(screenX, screenY, pointer, button)) {
            super.touchDown(screenX, screenY, pointer, button);
            return true;
        }
        else {
            // release keys when mouse click outside of ImGui window
            if(keyDown.size > 0)
                releaseKeyDown();
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(inputProcessor.touchDragged(screenX, screenY, pointer)) {
            super.touchDragged(screenX, screenY, pointer);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //TODO child should get touchUP only if touchDown was triggered inside window
        inputProcessor.touchUp(screenX, screenY, pointer, button);
        super.touchUp(screenX, screenY, pointer, button);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if(inputProcessor.keyTyped(character)) {
            super.keyTyped(character);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(inputProcessor.keyDown(keycode)) {
            keyDown.add(keycode);
            super.keyDown(keycode);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(inputProcessor.keyUp(keycode)) {
            keyDown.removeValue(keycode);
            super.keyUp(keycode);
            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if(inputProcessor.scrolled(amountX, amountY)) {
            super.scrolled(amountX, amountY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        inputProcessor.mouseMoved(screenX, screenY);
        super.mouseMoved(screenX, screenY);
        return false;
    }

    private void releaseKeyDown() {
        for(int i = 0; i < keyDown.size; i++)
            super.keyUp(keyDown.get(i));
        keyDown.clear();
    }
}
