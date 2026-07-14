package imgui.example.renderer.gdx;

public final class GdxExampleOptions {
    private GdxExampleOptions() {
    }

    public static void apply(String[] args) {
        setProperty(args, "--screenshot=", "jimgui.example.screenshot");
        setProperty(args, "--screenshot-after-frames=", "jimgui.example.screenshotAfterFrames");
        setProperty(args, "--exit-after-frames=", "jimgui.example.exitAfterFrames");
    }

    private static void setProperty(String[] args, String prefix, String propertyName) {
        if (args == null) {
            return;
        }
        for (String arg : args) {
            if (arg != null && arg.startsWith(prefix)) {
                System.setProperty(propertyName, arg.substring(prefix.length()));
                return;
            }
        }
    }
}
