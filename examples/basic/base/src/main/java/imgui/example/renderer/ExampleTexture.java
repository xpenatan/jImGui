package imgui.example.renderer;

public final class ExampleTexture implements AutoCloseable {
    private final long id;
    private final int width;
    private final int height;
    private final Runnable disposer;
    private boolean disposed;

    public ExampleTexture(long id, int width, int height, Runnable disposer) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.disposer = disposer;
    }

    public long id() {
        return id;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    @Override
    public void close() {
        if (!disposed) {
            disposed = true;
            disposer.run();
        }
    }
}
