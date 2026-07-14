package imgui.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.BufferUtils;
import com.github.xpenatan.jParser.api.NativeObject;
import com.github.xpenatan.jparser.runtime.helper.NativeString;
import com.github.xpenatan.jparser.runtime.helper.NativeUtils;
import imgui.ImDrawCmd;
import imgui.ImDrawData;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiPlatformIO;
import imgui.ImTemp;
import imgui.ImTextureData;
import imgui.ImTextureIDRef;
import imgui.ImTextureRect;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.ImVectorImDrawCmd;
import imgui.ImVectorImDrawIdx;
import imgui.ImVectorImDrawListPtr;
import imgui.ImVectorImDrawVert;
import imgui.ImVectorImTextureDataPtr;
import imgui.ImVectorImTextureRect;
import imgui.enums.ImGuiBackendFlags;
import imgui.enums.ImTextureFormat;
import imgui.enums.ImTextureStatus;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * @author xpenatan
 */
public class ImGuiGdxGLImpl extends ImGuiGdxImpl {

    private final static int VTX_BUFFER_SIZE = 8 + 8 + 4;// = ImVec2 + ImVec2 + ImU32;
    private final static int IDX_BUFFER_SIZE = 2; // short

    private boolean optionA = true; // A copy to a contiguous buffer; B upload line by line

    private VertexAttributes vertexAttributes;

    private int VboHandle;
    private int ElementsHandle;
    private Matrix4 matrix = new Matrix4();

    private ShaderProgram shader;

    final static IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);
    int vaoHandle = -1;

    int glVersion;
    boolean isGL30 = false;

    private FileHandle imgui;

    private ByteBuffer vertexByteBuffer;
    private ByteBuffer indexByteBuffer;

    private ByteBuffer tempBuffer;
    private IntBuffer tempTextureBuffer;

    public ImGuiGdxGLImpl() {
        this(Gdx.files.local("imgui.ini"));
    }

    public ImGuiGdxGLImpl(FileHandle imgui) {
        this.imgui = imgui;
        tempBuffer = BufferUtils.newUnsafeByteBuffer(4);
        tempTextureBuffer = tempBuffer.asIntBuffer();

        ImGuiIO io = ImGui.GetIO();
        io.set_BackendFlags(ImGuiBackendFlags.RendererHasVtxOffset.or(ImGuiBackendFlags.RendererHasTextures).or(ImGuiBackendFlags.HasMouseCursors));

        vertexAttributes = new VertexAttributes(
                new VertexAttribute(Usage.Position, 2, GL20.GL_FLOAT, false, "Position"),
                new VertexAttribute(Usage.TextureCoordinates, 2, GL20.GL_FLOAT, false, "UV"),
                new VertexAttribute(Usage.ColorPacked, 4, GL20.GL_UNSIGNED_BYTE, true, "Color")
        );

        GLVersion glVersionObj = Gdx.graphics.getGLVersion();
        glVersion = glVersionObj.getMajorVersion() * 100 + glVersionObj.getMinorVersion() * 10;
        isGL30 = Gdx.graphics.isGL30Available();

        String vertex = getVertexShaderGlsl130();
        String fragment = getFragmentShaderGlsl130();
        if(isGL30) {
            GLVersion.Type glType = glVersionObj.getType();
            boolean isGles = glType == GLVersion.Type.GLES || glType == GLVersion.Type.WebGL;
            if(isGles) {
                vertex = getVertexShaderGlsl300es();
                fragment = getFragmentShaderGlsl300es();
            } else {
                vertex = getVertexShaderGlsl330();
                fragment = getFragmentShaderGlsl330();
            }
        }

        shader = new ShaderProgram(vertex, fragment);
        if(shader.isCompiled() == false) {
            Gdx.app.log("ShaderTest", shader.getLog());
            Gdx.app.exit();
        }

        createBufferObject();

        if(imgui != null) {
            boolean exists = imgui.exists();
            if(exists) {
                String iniData = imgui.readString();
                if(!iniData.isEmpty()) {
                    ImGui.LoadIniSettingsFromMemory(iniData);
                }
            }
        }
    }

    private void updateTexture(ImTextureData tex) {
        ImTextureStatus status = tex.get_Status();
        ImTextureFormat format = tex.get_Format();
        int sizeBytes = tex.GetSizeInBytes();

        if(status == ImTextureStatus.WantCreate || status == ImTextureStatus.WantUpdates) {
            Gdx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
        }

        if (status == ImTextureStatus.WantCreate) {
            int bytesPerPixel = tex.get_BytesPerPixel();
            ByteBuffer pixels = BufferUtils.newUnsafeByteBuffer(sizeBytes);
            pixels.order(ByteOrder.LITTLE_ENDIAN);
            NativeObject pixelsBuff = tex.GetPixels();
            NativeUtils.copyToByteBuffer(pixelsBuff, pixels,0 , sizeBytes);
            int width = tex.get_Width();
            int height = tex.get_Height();
            tempTextureBuffer.put(0, 0);
            Gdx.gl.glGetIntegerv(GL20.GL_ACTIVE_TEXTURE, tempTextureBuffer);
            int last_texture = tempTextureBuffer.get(0);
            int g_Texture = Gdx.gl.glGenTexture();
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, g_Texture);
            Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR);
            Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
            Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
            Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);
            Gdx.gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, width, height, 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);
            BufferUtils.disposeUnsafeByteBuffer(pixels);
            tex.SetTexID(ImTemp.ImTextureIDRef_1(g_Texture));
            tex.SetStatus(ImTextureStatus.OK);
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, last_texture);
        }
        else if(status == ImTextureStatus.WantUpdates) {
            int bytesPerPixel = tex.get_BytesPerPixel();
            tempTextureBuffer.put(0, 0);
            Gdx.gl.glGetIntegerv(GL20.GL_ACTIVE_TEXTURE, tempTextureBuffer);
            int last_texture = tempTextureBuffer.get(0);
            ImTextureIDRef imTextureIDRef = tex.GetTexID();
            int texId = (int)imTextureIDRef.Get();
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, texId);
            ImVectorImTextureRect updates = tex.get_Updates();
            int size = updates.size();
            int width = tex.get_Width();
            int height = tex.get_Height();
            if(Gdx.gl30 != null) {
                Gdx.gl.glPixelStorei(GL30.GL_UNPACK_ROW_LENGTH, width);
                for(int i = 0; i < size; i++) {
                    ImTextureRect r = updates.getData(i);
                    short r_x = r.get_x();
                    short r_y = r.get_y();
                    short r_w = r.get_w();
                    short r_h = r.get_h();
                    int bufferSize = width * r_h * bytesPerPixel;
                    NativeObject pixels = tex.GetPixelsAt(r_x, r_y);
                    ByteBuffer byteBuffer = BufferUtils.newUnsafeByteBuffer(bufferSize);
                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    NativeUtils.copyToByteBuffer(pixels, byteBuffer, 0, bufferSize);
                    Gdx.gl.glTexSubImage2D(GL20.GL_TEXTURE_2D, 0, r_x, r_y, r_w, r_h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, byteBuffer);
                    BufferUtils.disposeUnsafeByteBuffer(byteBuffer);
                }
                Gdx.gl.glPixelStorei(GL30.GL_UNPACK_ROW_LENGTH, 0);
            }
            else {
                for(int i = 0; i < size; i++) {
                    ImTextureRect r = updates.getData(i);
                    short r_x = r.get_x();
                    short r_y = r.get_y();
                    short r_w = r.get_w();
                    short r_h = r.get_h();
                    if(optionA) {
                        int src_pitch = r_w * bytesPerPixel;
                        int bufferSize = r_h * src_pitch;
                        ByteBuffer byteBuffer = BufferUtils.newUnsafeByteBuffer(bufferSize);

                        int offset = 0;
                        for (int y = 0; y < r_h; y++) {
                            NativeObject src_row = tex.GetPixelsAt(r_x, r_y + y);
                            NativeUtils.copyToByteBuffer(src_row, byteBuffer, offset, src_pitch);
                            offset += src_pitch;
                        }
                        Gdx.gl.glTexSubImage2D(GL20.GL_TEXTURE_2D, 0, r_x, r_y, r_w, r_h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, byteBuffer);
                        BufferUtils.disposeUnsafeByteBuffer(byteBuffer);
                    }
                    else {
                        int bufferSize = r_w * bytesPerPixel;
                        ByteBuffer byteBuffer = BufferUtils.newUnsafeByteBuffer(bufferSize);
                        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                        for (int y = 0; y < r_h; y++) {
                            NativeObject row_pixels = tex.GetPixelsAt(r_x, r_y + y);
                            NativeUtils.copyToByteBuffer(row_pixels, byteBuffer, 0, bufferSize);
                            Gdx.gl.glTexSubImage2D(GL20.GL_TEXTURE_2D, 0, r_x, r_y + y, r_w, r_h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, byteBuffer);
                        }
                        BufferUtils.disposeUnsafeByteBuffer(byteBuffer);
                    }
                }
            }
            tex.SetStatus(ImTextureStatus.OK);
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, last_texture);
        }
        else if (status == ImTextureStatus.WantDestroy && tex.get_UnusedFrames() > 0) {
            destroyTexture(tex);
        }
    }

    private void destroyTexture(ImTextureData tex) {
        ImTextureIDRef imTextureIDRef = tex.GetTexID();
        long texId = imTextureIDRef.Get();
        Gdx.gl.glDeleteTexture((int)texId);
        tex.SetTexID(ImTemp.ImTextureIDRef_1(0));
        tex.SetStatus(ImTextureStatus.Destroyed);
    }

    private void createBufferObject() {
        VboHandle = Gdx.gl20.glGenBuffer();
        ElementsHandle = Gdx.gl20.glGenBuffer();
    }

    public void newFrame() {
        syncViewportPlatform();
        float deltaTime = Gdx.graphics.getDeltaTime();
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int backBufferWidth = Gdx.graphics.getBackBufferWidth();
        int backBufferHeight = Gdx.graphics.getBackBufferHeight();

        ImGuiIO io = ImGui.GetIO();
        ImVec2 displaySize = io.get_DisplaySize(); // TODO check if this updates the struct vec2
        displaySize.set_x(width);
        displaySize.set_y(height);
        if (width > 0 && height > 0) {
            ImVec2 displayFramebufferScale = io.get_DisplayFramebufferScale();
            displayFramebufferScale.set_x((float)backBufferWidth / width);
            displayFramebufferScale.set_y((float)backBufferHeight / height);
        }
        io.set_DeltaTime(deltaTime);
        ImGui.NewFrame();

        if(imgui != null) {
            ImGuiIO imGuiIO = ImGui.GetIO();
            if(imGuiIO.get_WantSaveIniSettings()) {
                imGuiIO.set_WantSaveIniSettings(false);
                saveImGuiData(imgui);
            }
        }
    }

    @Override
    public void render(ImDrawData drawData) {
        renderDrawData(drawData);
        updatePlatformWindows();
    }

    void renderViewport(ImDrawData drawData) {
        renderDrawData(drawData);
    }

    private void renderDrawData(ImDrawData drawData) {
        int cmdListsCount = drawData.get_CmdListsCount();
        if(cmdListsCount <= 0) {
            return;
        }

        ImVec2 displaySize = drawData.get_DisplaySize();
        ImVec2 framebufferScale = drawData.get_FramebufferScale();
        float displaySizeX = displaySize.get_x();
        float displaySizeY = displaySize.get_y();
        float frameScaleX = framebufferScale.get_x();
        float frameScaleY = framebufferScale.get_y();
        int fb_width = (int)(displaySizeX * frameScaleX);
        int fb_height = (int)(displaySizeY * frameScaleY);
        if (fb_width <= 0 || fb_height <= 0) {
            return;
        }

        ImVectorImTextureDataPtr textures = drawData.get_Textures();
        if(textures != ImVectorImTextureDataPtr.NULL) {
            int size = textures.size();
            for(int i = 0; i < size; i++) {
                ImTextureData tex = textures.getData(i);
                if(tex.get_Status() != ImTextureStatus.OK) {
                    updateTexture(tex);
                }
            }
        }

        int totalVtxCount = drawData.get_TotalVtxCount();
        int totalIdxCount = drawData.get_TotalIdxCount();

        boolean last_enable_blend = Gdx.gl.glIsEnabled(GL20.GL_BLEND);
        boolean last_enable_cull_face = Gdx.gl.glIsEnabled(GL20.GL_CULL_FACE);
        boolean last_enable_depth_test = Gdx.gl.glIsEnabled(GL20.GL_DEPTH_TEST);
        boolean last_enable_stencil_test = Gdx.gl.glIsEnabled(GL20.GL_STENCIL_TEST);
        boolean last_enable_scissor_test = Gdx.gl.glIsEnabled(GL20.GL_SCISSOR_TEST);

        ImVec2 displayPos = drawData.get_DisplayPos();

        bind(drawData, fb_width, fb_height);

        float clip_offX = displayPos.get_x(); // (0,0) unless using multi-viewports
        float clip_offY = displayPos.get_y();
        float clip_scaleX = frameScaleX; // (1,1) unless using retina display which are often (2,2)
        float clip_scaleY = frameScaleY;

        ImVectorImDrawListPtr cmdLists = drawData.get_CmdLists();
        for(int i = 0; i < cmdListsCount; i++) {
            ImDrawList drawList = cmdLists.getData(i);
            ImVectorImDrawCmd cmdBuffer = drawList.get_CmdBuffer();
            ImVectorImDrawVert vtxBuffer = drawList.get_VtxBuffer();
            ImVectorImDrawIdx idxBuffer = drawList.get_IdxBuffer();

            int vtxByteSize = vtxBuffer.size() * VTX_BUFFER_SIZE;
            int idxByteSize = idxBuffer.size() * IDX_BUFFER_SIZE;

            int vtxBufferSize = 0;
            if(vertexByteBuffer != null) {
                vtxBufferSize = vertexByteBuffer.capacity();
            }
            if(vtxByteSize > vtxBufferSize) {
                if(vertexByteBuffer != null) {
                    BufferUtils.disposeUnsafeByteBuffer(vertexByteBuffer);
                }
                vertexByteBuffer = BufferUtils.newUnsafeByteBuffer(vtxByteSize);
                vertexByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            }
            int idxBufferSize = 0;
            if(indexByteBuffer != null) {
                idxBufferSize = indexByteBuffer.capacity();
            }
            if(idxByteSize > idxBufferSize) {
                if(indexByteBuffer != null) {
                    BufferUtils.disposeUnsafeByteBuffer(indexByteBuffer);
                }
                indexByteBuffer = BufferUtils.newUnsafeByteBuffer(idxByteSize);
                indexByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            }

            NativeObject vtxData = vtxBuffer.get_Data();
            NativeObject idxData = idxBuffer.get_Data();
            NativeUtils.copyToByteBuffer(vtxData, vertexByteBuffer, 0, vtxByteSize);
            NativeUtils.copyToByteBuffer(idxData, indexByteBuffer, 0, idxByteSize);

            Gdx.gl.glBufferData(GL20.GL_ARRAY_BUFFER, vtxByteSize, vertexByteBuffer, GL20.GL_STREAM_DRAW);
            Gdx.gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, idxByteSize, indexByteBuffer, GL20.GL_STREAM_DRAW);

            int cmdBufferSize = cmdBuffer.size();
            for(int j = 0; j < cmdBufferSize; j++) {
                ImDrawCmd drawCmd = cmdBuffer.getData(j);
                ImVec4 clipRect = drawCmd.get_ClipRect();
                float clipRectX = clipRect.get_x();
                float clipRectY = clipRect.get_y();
                float clipRectZ = clipRect.get_z();
                float clipRectW = clipRect.get_w();
                float clip_minX = (clipRectX - clip_offX) * clip_scaleX;
                float clip_minY = (clipRectY - clip_offY) * clip_scaleY;
                float clip_maxX = (clipRectZ - clip_offX) * clip_scaleX;
                float clip_maxY = (clipRectW - clip_offY) * clip_scaleY;
                if(clip_maxX < clip_minX || clip_maxY < clip_minY)
                    continue;

                Gdx.gl.glScissor((int)clip_minX, (int)(fb_height - clip_maxY), (int)(clip_maxX - clip_minX), (int)(clip_maxY - clip_minY));

                int idxOffset = drawCmd.get_IdxOffset();
                int vtxOffset = drawCmd.get_VtxOffset();
                int elemCount = drawCmd.get_ElemCount();
                int indices = idxOffset * 2;
                ImTextureIDRef imTextureIDRef = drawCmd.GetTexID();
                int texId = (int)imTextureIDRef.Get();
                Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, texId);
                Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, elemCount, GL20.GL_UNSIGNED_SHORT, indices);
            }
        }

        unbind();

        if(last_enable_blend) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
        }
        else {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
        if(last_enable_cull_face) {
            Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        }
        else {
            Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        }
        if(last_enable_depth_test) {
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        }
        else {
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        }
        if(last_enable_stencil_test) {
            Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
        }
        else {
            Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
        }
        if(last_enable_scissor_test) {
            Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        }
        else {
            Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        }
    }

    private void bind(ImDrawData drawData, int fb_width, int fb_height) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        ImVec2 displayPos = drawData.get_DisplayPos();
        ImVec2 displaySize = drawData.get_DisplaySize();

        float displayX = displayPos.get_x();
        float displayY = displayPos.get_y();

        float L = displayX;
        float R = displayX + displaySize.get_x();
        float T = displayY;
        float B = displayY + displaySize.get_y();

        matrix.val[0] = 2.0f / (R - L);
        matrix.val[1] = 0.0f;
        matrix.val[2] = 0.0f;
        matrix.val[3] = 0.0f;
        matrix.val[4] = 0.0f;
        matrix.val[5] = 2.0f / (T - B);
        matrix.val[6] = 0;
        matrix.val[7] = 0;
        matrix.val[8] = 0;
        matrix.val[9] = 0;
        matrix.val[10] = -1.0f;
        matrix.val[11] = 0f;
        matrix.val[12] = (R + L) / (L - R);
        matrix.val[13] = (T + B) / (B - T);
        matrix.val[14] = 0;
        matrix.val[15] = 1.0f;

        shader.bind();
        shader.setUniformi("Texture", 0);
        shader.setUniformMatrix("ProjMtx", matrix);

        if(isGL30) {
            Gdx.gl30.glGenVertexArrays(1, tmpHandle);
            vaoHandle = tmpHandle.get();
            Gdx.gl30.glBindVertexArray(vaoHandle);
        }

        Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, VboHandle);
        Gdx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, ElementsHandle);
        // bind vertices/indices
        final int numAttributes = vertexAttributes.size();
        for(int i = 0; i < numAttributes; i++) {
            final VertexAttribute attribute = vertexAttributes.get(i);
            final int location = shader.getAttributeLocation(attribute.alias);
            if(location < 0) continue;
            shader.enableVertexAttribute(location);
            shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized,
                    vertexAttributes.vertexSize, attribute.offset);
        }
    }

    public void unbind() {
        // unbind vertice
        Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        final int numAttributes = vertexAttributes.size();
        for(int i = 0; i < numAttributes; i++) {
            shader.disableVertexAttribute(vertexAttributes.get(i).alias);
        }

        // unbind index
        Gdx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
        Gdx.gl.glFlush();

        if(isGL30) {
            tmpHandle.clear();
            tmpHandle.put(vaoHandle);
            tmpHandle.flip();
            Gdx.gl30.glDeleteVertexArrays(1, tmpHandle);
        }
    }

    private void deleteTexture() {
        // Destroy all textures

        ImGuiPlatformIO platformIO = ImGui.GetPlatformIO();
        ImVectorImTextureDataPtr textures = platformIO.get_Textures();
        int size = textures.size();
        for(int i = 0; i < size; i++) {
            ImTextureData data = textures.getData(i);
            short refCount = data.get_RefCount();
            if(refCount == 1) {
                destroyTexture(data);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        deleteTexture();
        BufferUtils.disposeUnsafeByteBuffer(tempBuffer);

        if(vertexByteBuffer != null) {
            BufferUtils.disposeUnsafeByteBuffer(vertexByteBuffer);
        }
        if(indexByteBuffer != null) {
            BufferUtils.disposeUnsafeByteBuffer(indexByteBuffer);
        }

        // TODO fix exception
//		ImGui.DestroyPlatformWindows();
        Gdx.gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
        Gdx.gl.glDeleteBuffer(ElementsHandle);
        ElementsHandle = 0;
    }

    public void saveImGuiData(FileHandle path) {
        if(path != null) {
            NativeString idlString = ImGui.SaveIniSettingsToMemory();
            String s = idlString.c_str();
            path.writeString(s, false);
        }
    }

    private String getVertexShaderGlsl130() {
        return "uniform mat4 ProjMtx;\n"
                + "attribute vec2 Position;\n"
                + "attribute vec2 UV;\n"
                + "attribute vec4 Color;\n"
                + "varying vec2 Frag_UV;\n"
                + "varying vec4 Frag_Color;\n"
                + "void main()\n"
                + "{\n"
                + "    Frag_UV = UV;\n"
                + "    Frag_Color = Color;\n"
                + "    gl_Position = ProjMtx * vec4(Position.xy, 0.0, 1.0);\n"
                + "}\n";
    }

    private String getFragmentShaderGlsl130() {
        return "#ifdef GL_ES\n"
                + "precision mediump float;\n"
                + "#endif\n"
                + "uniform sampler2D Texture;\n"
                + "varying vec2 Frag_UV;\n"
                + "varying vec4 Frag_Color;\n"
                + "void main()\n"
                + "{\n"
                + "    gl_FragColor = Frag_Color * texture2D(Texture, Frag_UV.st);\n"
                + "}\n";
    }
    private String getVertexShaderGlsl300es() {
        return "#version 300 es\n"
                + "precision highp float;\n"
                + "layout(location = 0) in vec2 Position;\n"
                + "layout(location = 1) in vec2 UV;\n"
                + "layout(location = 2) in vec4 Color;\n"
                + "uniform mat4 ProjMtx;\n"
                + "out vec2 Frag_UV;\n"
                + "out vec4 Frag_Color;\n"
                + "void main()\n"
                + "{\n"
                + "    Frag_UV = UV;\n"
                + "    Frag_Color = Color;\n"
                + "    gl_Position = ProjMtx * vec4(Position.xy, 0.0, 1.0);\n"
                + "}\n";
    }

    private String getFragmentShaderGlsl300es() {
        return "#version 300 es\n"
                + "precision mediump float;\n"
                + "in vec2 Frag_UV;\n"
                + "in vec4 Frag_Color;\n"
                + "uniform sampler2D Texture;\n"
                + "layout(location = 0) out vec4 Out_Color;\n"
                + "void main()\n"
                + "{\n"
                + "    Out_Color = Frag_Color * texture(Texture, Frag_UV.st);\n"
                + "}\n";
    }

    private String getVertexShaderGlsl330() {
        return "#version 330 core\n"
                + "layout(location = 0) in vec2 Position;\n"
                + "layout(location = 1) in vec2 UV;\n"
                + "layout(location = 2) in vec4 Color;\n"
                + "uniform mat4 ProjMtx;\n"
                + "out vec2 Frag_UV;\n"
                + "out vec4 Frag_Color;\n"
                + "void main()\n"
                + "{\n"
                + "    Frag_UV = UV;\n"
                + "    Frag_Color = Color;\n"
                + "    gl_Position = ProjMtx * vec4(Position.xy, 0.0, 1.0);\n"
                + "}\n";
    }

    private String getFragmentShaderGlsl330() {
        return "#version 330 core\n"
                + "in vec2 Frag_UV;\n"
                + "in vec4 Frag_Color;\n"
                + "uniform sampler2D Texture;\n"
                + "layout(location = 0) out vec4 Out_Color;\n"
                + "void main()\n"
                + "{\n"
                + "    Out_Color = Frag_Color * texture(Texture, Frag_UV.st);\n"
                + "}\n";
    }
}
