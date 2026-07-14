package io.github.libfdx.imgui;

import io.github.libfdx.core.FdxException;
import io.github.libfdx.graphics.Texture;

public final class FdxImGuiTextureRegistry {
    private long nextId = 1L;
    private long[] ids = new long[8];
    private Texture[] textures = new Texture[8];
    private boolean[] owned = new boolean[8];
    private int size;
    private boolean disposed;

    public long register(Texture texture) {
        return register(texture, false);
    }

    long registerOwned(Texture texture) {
        return register(texture, true);
    }

    private long register(Texture texture, boolean own) {
        ensureNotDisposed();
        if (texture == null) {
            throw new FdxException("ImGui texture cannot be null");
        }
        ensureCapacity(size + 1);
        long id = nextId++;
        if (id == 0L) {
            id = nextId++;
        }
        ids[size] = id;
        textures[size] = texture;
        owned[size] = own;
        size++;
        return id;
    }

    public Texture texture(long id) {
        ensureNotDisposed();
        int index = indexOf(id);
        if (index < 0) {
            throw new FdxException("Unknown ImGui texture id: " + id);
        }
        return textures[index];
    }

    public boolean contains(long id) {
        return indexOf(id) >= 0;
    }

    public Texture remove(long id) {
        ensureNotDisposed();
        int index = indexOf(id);
        if (index < 0) {
            return null;
        }
        Texture removed = textures[index];
        removeAt(index);
        return removed;
    }

    void removeAndDisposeOwned(long id) {
        int index = indexOf(id);
        if (index < 0) {
            return;
        }
        Texture removed = textures[index];
        boolean dispose = owned[index];
        removeAt(index);
        if (dispose && removed != null && !removed.isDisposed()) {
            removed.dispose();
        }
    }

    public void disposeOwned() {
        if (disposed) {
            return;
        }
        for (int i = 0; i < size; i++) {
            Texture texture = textures[i];
            if (owned[i] && texture != null && !texture.isDisposed()) {
                texture.dispose();
            }
            ids[i] = 0L;
            textures[i] = null;
            owned[i] = false;
        }
        size = 0;
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }

    private void removeAt(int index) {
        int last = size - 1;
        if (index < last) {
            System.arraycopy(ids, index + 1, ids, index, last - index);
            System.arraycopy(textures, index + 1, textures, index, last - index);
            System.arraycopy(owned, index + 1, owned, index, last - index);
        }
        ids[last] = 0L;
        textures[last] = null;
        owned[last] = false;
        size--;
    }

    private int indexOf(long id) {
        for (int i = 0; i < size; i++) {
            if (ids[i] == id) {
                return i;
            }
        }
        return -1;
    }

    private void ensureCapacity(int required) {
        if (required <= ids.length) {
            return;
        }
        int next = ids.length;
        while (next < required) {
            next *= 2;
        }
        long[] nextIds = new long[next];
        Texture[] nextTextures = new Texture[next];
        boolean[] nextOwned = new boolean[next];
        System.arraycopy(ids, 0, nextIds, 0, size);
        System.arraycopy(textures, 0, nextTextures, 0, size);
        System.arraycopy(owned, 0, nextOwned, 0, size);
        ids = nextIds;
        textures = nextTextures;
        owned = nextOwned;
    }

    private void ensureNotDisposed() {
        if (disposed) {
            throw new FdxException("ImGui texture registry has been disposed");
        }
    }
}
