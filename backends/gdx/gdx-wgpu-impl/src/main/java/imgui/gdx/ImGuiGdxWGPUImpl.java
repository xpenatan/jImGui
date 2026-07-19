package imgui.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.IntMap;
import com.github.xpenatan.jParser.api.NativeObject;
import com.github.xpenatan.jparser.runtime.helper.NativeString;
import com.github.xpenatan.jparser.runtime.helper.NativeTemp;
import com.github.xpenatan.jparser.runtime.helper.NativeUtils;
import com.github.xpenatan.webgpu.WGPUAddressMode;
import com.github.xpenatan.webgpu.WGPUBindGroup;
import com.github.xpenatan.webgpu.WGPUBindGroupDescriptor;
import com.github.xpenatan.webgpu.WGPUBindGroupEntry;
import com.github.xpenatan.webgpu.WGPUBindGroupLayout;
import com.github.xpenatan.webgpu.WGPUBindGroupLayoutDescriptor;
import com.github.xpenatan.webgpu.WGPUBindGroupLayoutEntry;
import com.github.xpenatan.webgpu.WGPUBlendComponent;
import com.github.xpenatan.webgpu.WGPUBlendFactor;
import com.github.xpenatan.webgpu.WGPUBlendOperation;
import com.github.xpenatan.webgpu.WGPUBlendState;
import com.github.xpenatan.webgpu.WGPUBuffer;
import com.github.xpenatan.webgpu.WGPUBufferBindingType;
import com.github.xpenatan.webgpu.WGPUBufferDescriptor;
import com.github.xpenatan.webgpu.WGPUBufferUsage;
import com.github.xpenatan.webgpu.WGPUChainedStruct;
import com.github.xpenatan.webgpu.WGPUColor;
import com.github.xpenatan.webgpu.WGPUColorTargetState;
import com.github.xpenatan.webgpu.WGPUColorWriteMask;
import com.github.xpenatan.webgpu.WGPUCommandEncoder;
import com.github.xpenatan.webgpu.WGPUCullMode;
import com.github.xpenatan.webgpu.WGPUDepthStencilState;
import com.github.xpenatan.webgpu.WGPUDevice;
import com.github.xpenatan.webgpu.WGPUExtent3D;
import com.github.xpenatan.webgpu.WGPUFilterMode;
import com.github.xpenatan.webgpu.WGPUFragmentState;
import com.github.xpenatan.webgpu.WGPUFrontFace;
import com.github.xpenatan.webgpu.WGPUIndexFormat;
import com.github.xpenatan.webgpu.WGPULoadOp;
import com.github.xpenatan.webgpu.WGPUMipmapFilterMode;
import com.github.xpenatan.webgpu.WGPUMultisampleState;
import com.github.xpenatan.webgpu.WGPUOrigin3D;
import com.github.xpenatan.webgpu.WGPUPipelineLayout;
import com.github.xpenatan.webgpu.WGPUPipelineLayoutDescriptor;
import com.github.xpenatan.webgpu.WGPUPrimitiveState;
import com.github.xpenatan.webgpu.WGPUPrimitiveTopology;
import com.github.xpenatan.webgpu.WGPURenderPassColorAttachment;
import com.github.xpenatan.webgpu.WGPURenderPassDepthStencilAttachment;
import com.github.xpenatan.webgpu.WGPURenderPassDescriptor;
import com.github.xpenatan.webgpu.WGPURenderPassEncoder;
import com.github.xpenatan.webgpu.WGPURenderPipeline;
import com.github.xpenatan.webgpu.WGPURenderPipelineDescriptor;
import com.github.xpenatan.webgpu.WGPUSType;
import com.github.xpenatan.webgpu.WGPUSampler;
import com.github.xpenatan.webgpu.WGPUSamplerBindingType;
import com.github.xpenatan.webgpu.WGPUSamplerDescriptor;
import com.github.xpenatan.webgpu.WGPUShaderModule;
import com.github.xpenatan.webgpu.WGPUShaderModuleDescriptor;
import com.github.xpenatan.webgpu.WGPUShaderSourceWGSL;
import com.github.xpenatan.webgpu.WGPUShaderStage;
import com.github.xpenatan.webgpu.WGPUStoreOp;
import com.github.xpenatan.webgpu.WGPUTexelCopyBufferLayout;
import com.github.xpenatan.webgpu.WGPUTexelCopyTextureInfo;
import com.github.xpenatan.webgpu.WGPUTexture;
import com.github.xpenatan.webgpu.WGPUTextureAspect;
import com.github.xpenatan.webgpu.WGPUTextureDescriptor;
import com.github.xpenatan.webgpu.WGPUTextureDimension;
import com.github.xpenatan.webgpu.WGPUTextureFormat;
import com.github.xpenatan.webgpu.WGPUTextureSampleType;
import com.github.xpenatan.webgpu.WGPUTextureUsage;
import com.github.xpenatan.webgpu.WGPUTextureView;
import com.github.xpenatan.webgpu.WGPUTextureViewDescriptor;
import com.github.xpenatan.webgpu.WGPUTextureViewDimension;
import com.github.xpenatan.webgpu.WGPUVectorBindGroupEntry;
import com.github.xpenatan.webgpu.WGPUVectorBindGroupLayout;
import com.github.xpenatan.webgpu.WGPUVectorBindGroupLayoutEntry;
import com.github.xpenatan.webgpu.WGPUVectorColorTargetState;
import com.github.xpenatan.webgpu.WGPUVectorRenderPassColorAttachment;
import com.github.xpenatan.webgpu.WGPUVectorVertexAttribute;
import com.github.xpenatan.webgpu.WGPUVectorVertexBufferLayout;
import com.github.xpenatan.webgpu.WGPUVertexAttribute;
import com.github.xpenatan.webgpu.WGPUVertexBufferLayout;
import com.github.xpenatan.webgpu.WGPUVertexFormat;
import com.github.xpenatan.webgpu.WGPUVertexState;
import com.github.xpenatan.webgpu.WGPUVertexStepMode;
import com.monstrous.gdx.webgpu.application.WebGPUContext;
import com.monstrous.gdx.webgpu.application.WgGraphics;
import imgui.ImGuiInternal;
import imgui.ImTemp;
import imgui.ImTextureIDRef;
import imgui.enums.ImGuiBackendFlags;
import imgui.ImDrawCmd;
import imgui.ImDrawData;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImGuiDrawCallbacks;
import imgui.ImGuiIO;
import imgui.ImGuiPlatformIO;
import imgui.ImTextureData;
import imgui.ImTextureRect;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.ImVectorImDrawCmd;
import imgui.ImVectorImDrawIdx;
import imgui.ImVectorImDrawListPtr;
import imgui.ImVectorImDrawVert;
import imgui.ImVectorImTextureDataPtr;
import imgui.enums.ImGuiDrawCallbackType;
import imgui.enums.ImTextureStatus;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class ImGuiGdxWGPUImpl extends ImGuiGdxImpl {

    private static final int VTX_BUFFER_SIZE = 8 + 8 + 4;// = ImVec2 + ImVec2 + ImU32;
    private final static int IDX_BUFFER_SIZE = 2; // short

    private final WGPUTexture tmp_texture = WGPUTexture.native_new();
    private final WGPUTextureView tmp_textureView = WGPUTextureView.native_new();

    private WGPUDevice device;
    private WGPURenderPassEncoder renderPass;
    private WGPUTextureFormat renderFormat;
    private WGPURenderPipeline pipeline;
    private WGPUShaderModule shaderModule;
    private WGPUPipelineLayout pipelineLayout;
    private WGPUBindGroupLayout commonBindGroupLayout;
    private WGPUBindGroupLayout imageBindGroupLayout;
    private WGPUSampler linearSampler;
    private WGPUSampler nearestSampler;
    private WGPUBuffer uniformBuffer;
    private WGPUBindGroup linearCommonBindGroup;
    private WGPUBindGroup nearestCommonBindGroup;
    private WGPUBuffer vertexBuffer;
    private WGPUBuffer indexBuffer;
    private int vertexBufferSizeBytes = 0;
    private int indexBufferSizeBytes = 0;
    private final int prjSizeBytes = 64;
    private final int gamaSizeBytes = 8;
    private ByteBuffer projBuffer;
    private FloatBuffer projFloatView;
    private ByteBuffer gammaBuffer;
    private FloatBuffer gammaFloatBuffer;
    private boolean init = true;
    private final NativeObject empty_01 = NativeObject.native_new();
    private ByteBuffer vertexByteBuffer;
    private ByteBuffer indexByteBuffer;
    private FileHandle imgui;
    private IntMap<WGPUBindGroup> textureBindGroups = new IntMap<>();
    private ArrayList<WGPUBindGroup> poolList = new ArrayList<>();

    public ImGuiGdxWGPUImpl() {
        WgGraphics gfx = (WgGraphics) Gdx.graphics;
        WebGPUContext webgpu = gfx.getContext();
        setup(Gdx.files.local("imgui.ini"), webgpu.device, webgpu.surfaceFormats);
    }

    public ImGuiGdxWGPUImpl(WGPUDevice device, WGPUTextureFormat renderFormat) {
        setup(Gdx.files.local("imgui.ini"), device, (renderFormat != null) ? new WGPUTextureFormat[] { renderFormat } : null);
    }

    public ImGuiGdxWGPUImpl(WGPUDevice device, WGPUTextureFormat[] renderFormats) {
        setup(Gdx.files.local("imgui.ini"), device, renderFormats);
    }

    public ImGuiGdxWGPUImpl(FileHandle imgui, WGPUDevice device, WGPUTextureFormat renderFormat) {
        setup(imgui, device, (renderFormat != null) ? new WGPUTextureFormat[] { renderFormat } : null);
    }

    public ImGuiGdxWGPUImpl(FileHandle imgui, WGPUDevice device, WGPUTextureFormat[] renderFormats) {
        setup(imgui, device, renderFormats);
    }

    private void setup(FileHandle imgui, WGPUDevice device, WGPUTextureFormat[] renderFormats) {
        this.imgui = imgui;
        this.device = device;
        this.renderFormat = renderFormats[0];

        ImGuiIO io = ImGui.GetIO();
        io.set_BackendFlags(ImGuiBackendFlags.RendererHasTextures.or(ImGuiBackendFlags.HasMouseCursors));
        ImGuiDrawCallbacks.InstallStandardCallbacks(ImGui.GetPlatformIO());

        if(imgui != null) {
            boolean exists = imgui.exists();
            if(exists) {
                String iniData = imgui.readString();
                if(!iniData.isEmpty()) {
                    ImGui.LoadIniSettingsFromMemory(iniData);
                }
            }
        }

        projBuffer = BufferUtils.newUnsafeByteBuffer(prjSizeBytes)
                .order(ByteOrder.LITTLE_ENDIAN);
        projFloatView = projBuffer.asFloatBuffer();
        gammaBuffer = BufferUtils.newUnsafeByteBuffer(prjSizeBytes)
                .order(ByteOrder.LITTLE_ENDIAN);
        gammaFloatBuffer = gammaBuffer.asFloatBuffer();
    }

    @Override
    public void newFrame() {
        syncViewportPlatform();
        if (init) {
            init = false;
            createDeviceObjects();
        }

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
        WgGraphics gfx = (WgGraphics) Gdx.graphics;
        WebGPUContext webgpu = gfx.getContext();
        render(drawData, webgpu.encoder, webgpu.getTargetView());
    }

    private void createDeviceObjects() {
        WGPUShaderModuleDescriptor shaderDesc = WGPUShaderModuleDescriptor.obtain();
        WGPUShaderSourceWGSL wgslDesc = WGPUShaderSourceWGSL.obtain();
        wgslDesc.getChain().setNext(WGPUChainedStruct.NULL);
        wgslDesc.getChain().setSType(WGPUSType.ShaderSourceWGSL);

        String shader = getShader();
        wgslDesc.setCode(shader);
        shaderDesc.setNextInChain(wgslDesc.getChain());

        shaderModule = new WGPUShaderModule();
        device.createShaderModule(shaderDesc, shaderModule);
//        shaderModule.setLabel("Shader Module");

        // Bind group layouts
        createBindGroupLayout();

        // Pipeline layout
        WGPUPipelineLayoutDescriptor layoutDesc = WGPUPipelineLayoutDescriptor.obtain();

        WGPUVectorBindGroupLayout array = WGPUVectorBindGroupLayout.obtain();
        array.push_back(commonBindGroupLayout);
        array.push_back(imageBindGroupLayout);
        layoutDesc.setBindGroupLayouts(array);
        pipelineLayout = new WGPUPipelineLayout();
        device.createPipelineLayout(layoutDesc, pipelineLayout);

        // Vertex layout
        WGPUVectorVertexAttribute attribute_desc = WGPUVectorVertexAttribute.obtain();
        WGPUVertexAttribute attr01 = WGPUVertexAttribute.obtain();
        attr01.setFormat(WGPUVertexFormat.Float32x2);
        attr01.setOffset(0);
        attr01.setShaderLocation(0);
        attribute_desc.push_back(attr01);

        WGPUVertexAttribute attr02 = WGPUVertexAttribute.obtain();
        attr02.setFormat(WGPUVertexFormat.Float32x2);
        attr02.setOffset(8);
        attr02.setShaderLocation(1);
        attribute_desc.push_back(attr02);

        WGPUVertexAttribute attr03 = WGPUVertexAttribute.obtain();
        attr03.setFormat(WGPUVertexFormat.Unorm8x4);
        attr03.setOffset(16);
        attr03.setShaderLocation(2);
        attribute_desc.push_back(attr03);

        WGPUVertexBufferLayout vertexLayout = WGPUVertexBufferLayout.obtain();
        vertexLayout.setArrayStride(VTX_BUFFER_SIZE);
        vertexLayout.setAttributes(attribute_desc);
        vertexLayout.setStepMode(WGPUVertexStepMode.Vertex);

        WGPURenderPipelineDescriptor graphics_pipeline_desc = WGPURenderPipelineDescriptor.obtain();

        WGPUPrimitiveState primitive = graphics_pipeline_desc.getPrimitive();
        primitive.setTopology(WGPUPrimitiveTopology.TriangleList);
        primitive.setStripIndexFormat(WGPUIndexFormat.Undefined);
        primitive.setFrontFace(WGPUFrontFace.CW);
        primitive.setCullMode(WGPUCullMode.None);
        WGPUMultisampleState multisample = graphics_pipeline_desc.getMultisample();
        multisample.setCount(1);
        multisample.setMask(-1);
        multisample.setAlphaToCoverageEnabled(false);

        // Vertex state
        WGPUVertexState vertexState = graphics_pipeline_desc.getVertex();
        vertexState.setModule(shaderModule);
        vertexState.setEntryPoint("vs_main");

        WGPUVectorVertexBufferLayout buffer_layouts = WGPUVectorVertexBufferLayout.obtain();
        buffer_layouts.push_back(vertexLayout);
        vertexState.setBuffers(buffer_layouts);

        WGPUColorTargetState color_state = WGPUColorTargetState.obtain();
        color_state.setFormat(renderFormat);
        color_state.setBlend(createBlendState());
        color_state.setWriteMask(WGPUColorWriteMask.All);
        WGPUVectorColorTargetState targets = WGPUVectorColorTargetState.obtain();
        // Currently we only attach a single color view in the render pass (targetView).
        // To remain compatible (no-end-to-end MRT support yet), create the pipeline
        // with a single color target using `renderFormat` (first format) so it matches
        // the single attachment used at render time.
        WGPUColorTargetState cs = WGPUColorTargetState.obtain();
        cs.setFormat(renderFormat);
        cs.setBlend(createBlendState());
        cs.setWriteMask(WGPUColorWriteMask.All);
        targets.push_back(cs);

        // Fragment state
        WGPUFragmentState fragmentState = WGPUFragmentState.obtain();
        fragmentState.setModule(shaderModule);
        fragmentState.setEntryPoint("fs_main");
        fragmentState.setTargets(targets);

        // Render pipeline descriptor
        graphics_pipeline_desc.setLayout(pipelineLayout);
        graphics_pipeline_desc.setFragment(fragmentState);
        graphics_pipeline_desc.setDepthStencil(WGPUDepthStencilState.NULL);
        pipeline = new WGPURenderPipeline();
        device.createRenderPipeline(graphics_pipeline_desc, pipeline);
//        pipeline.setLabel("Pipeline");

        linearSampler = createSampler(WGPUFilterMode.Linear, WGPUMipmapFilterMode.Linear);
        nearestSampler = createSampler(WGPUFilterMode.Nearest, WGPUMipmapFilterMode.Nearest);

        int gammaSize = 2;
        int orthoProjSize = 16;
        int uniformSize = orthoProjSize * Float.BYTES + gammaSize * Float.BYTES;
        int uniformAlign = (int)memAlign(uniformSize, 16);

        // Uniform buffer
        WGPUBufferDescriptor uniformDesc = WGPUBufferDescriptor.obtain();
        uniformDesc.setSize(uniformAlign);
        uniformDesc.setUsage(WGPUBufferUsage.Uniform.or(WGPUBufferUsage.CopyDst));
        uniformBuffer = device.createBuffer(uniformDesc);
//        uniformBuffer.setLabel("Uniform Buffer");

        linearCommonBindGroup = createCommonBindGroup(linearSampler, uniformAlign);
        nearestCommonBindGroup = createCommonBindGroup(nearestSampler, uniformAlign);

        renderPass = new WGPURenderPassEncoder();
    }

    private WGPUSampler createSampler(WGPUFilterMode filter, WGPUMipmapFilterMode mipmapFilter) {
        WGPUSamplerDescriptor samplerDesc = WGPUSamplerDescriptor.obtain();
        samplerDesc.setMinFilter(filter);
        samplerDesc.setMagFilter(filter);
        samplerDesc.setMipmapFilter(mipmapFilter);
        samplerDesc.setAddressModeU(WGPUAddressMode.ClampToEdge);
        samplerDesc.setAddressModeV(WGPUAddressMode.ClampToEdge);
        samplerDesc.setAddressModeW(WGPUAddressMode.ClampToEdge);
        samplerDesc.setMaxAnisotropy(1);
        WGPUSampler result = new WGPUSampler();
        device.createSampler(samplerDesc, result);
        return result;
    }

    private WGPUBindGroup createCommonBindGroup(WGPUSampler sampler, int uniformAlign) {
        WGPUVectorBindGroupEntry common_bg_entries = WGPUVectorBindGroupEntry.obtain();

        WGPUBindGroupEntry uniformEntry = WGPUBindGroupEntry.obtain();
        uniformEntry.setBinding(0);
        uniformEntry.setBuffer(uniformBuffer);
        uniformEntry.setSize(uniformAlign);
        uniformEntry.setSampler(WGPUSampler.NULL);
        uniformEntry.setTextureView(WGPUTextureView.NULL);
        common_bg_entries.push_back(uniformEntry);

        WGPUBindGroupEntry sampleEntry = WGPUBindGroupEntry.obtain();
        sampleEntry.setBinding(1);
        sampleEntry.setSize(0);
        sampleEntry.setSampler(sampler);
        sampleEntry.setBuffer(WGPUBuffer.NULL);
        sampleEntry.setTextureView(WGPUTextureView.NULL);
        common_bg_entries.push_back(sampleEntry);

        WGPUBindGroupDescriptor common_bg_descriptor = WGPUBindGroupDescriptor.obtain();
        common_bg_descriptor.setLabel("common_bg_descriptor");
        common_bg_descriptor.setLayout(commonBindGroupLayout);
        common_bg_descriptor.setEntries(common_bg_entries);
        WGPUBindGroup result = new WGPUBindGroup();
        device.createBindGroup(common_bg_descriptor, result);
        return result;
    }

    private void createBindGroupLayout() {

        {
            //Uniform
            WGPUVectorBindGroupLayoutEntry entries = WGPUVectorBindGroupLayoutEntry.obtain();
            WGPUBindGroupLayoutEntry entry = WGPUBindGroupLayoutEntry.obtain();
            entry.setBinding(0);
            entry.setVisibility(WGPUShaderStage.Vertex.or(WGPUShaderStage.Fragment));
            entry.getBuffer().setType(WGPUBufferBindingType.Uniform);
            entries.push_back(entry);

            WGPUBindGroupLayoutEntry samplerEntry = WGPUBindGroupLayoutEntry.obtain();
            samplerEntry.setBinding(1);
            samplerEntry.setVisibility(WGPUShaderStage.Fragment);
            samplerEntry.getSampler().setType(WGPUSamplerBindingType.Filtering);
            entries.push_back(samplerEntry);

            WGPUBindGroupLayoutDescriptor common_bg_layout_desc = WGPUBindGroupLayoutDescriptor.obtain();
            common_bg_layout_desc.setEntries(entries);
            commonBindGroupLayout = new WGPUBindGroupLayout();
            device.createBindGroupLayout(common_bg_layout_desc, commonBindGroupLayout);
//            commonBindGroupLayout.setLabel("Uniform Bind Group Layout");
        }

        {
            // texture/sampler
            WGPUVectorBindGroupLayoutEntry entries = WGPUVectorBindGroupLayoutEntry.obtain();

            WGPUBindGroupLayoutEntry textureEntry = WGPUBindGroupLayoutEntry.obtain();
            textureEntry.setBinding(0);
            textureEntry.setVisibility(WGPUShaderStage.Fragment);
            textureEntry.getTexture().setSampleType(WGPUTextureSampleType.Float);
            textureEntry.getTexture().setViewDimension(WGPUTextureViewDimension._2D);
            entries.push_back(textureEntry);

            WGPUBindGroupLayoutDescriptor image_bg_layout_desc = WGPUBindGroupLayoutDescriptor.obtain();
            image_bg_layout_desc.setEntries(entries);
            imageBindGroupLayout = new WGPUBindGroupLayout();
            device.createBindGroupLayout(image_bg_layout_desc, imageBindGroupLayout);
//            imageBindGroupLayout.setLabel("Texture Bind Group Layout");
        }
    }

    private WGPUBlendState createBlendState() {
        WGPUBlendState blend = WGPUBlendState.obtain();
        WGPUBlendComponent alpha = blend.getAlpha();
        alpha.setOperation(WGPUBlendOperation.Add);
        alpha.setSrcFactor(WGPUBlendFactor.One);
        alpha.setDstFactor(WGPUBlendFactor.OneMinusSrcAlpha);
        WGPUBlendComponent color = blend.getColor();
        color.setOperation(WGPUBlendOperation.Add);
        color.setSrcFactor(WGPUBlendFactor.SrcAlpha);
        color.setDstFactor(WGPUBlendFactor.OneMinusSrcAlpha);
        return blend;
    }

    private void createImageBindGroup(WGPUBindGroupLayout layout, WGPUTextureView texture, WGPUBindGroup bindGroup) {
        boolean valid = texture.isValid();
        WGPUVectorBindGroupEntry entries = WGPUVectorBindGroupEntry.obtain();
        {
            WGPUBindGroupEntry texEntry = WGPUBindGroupEntry.obtain();
            texEntry.setBinding(0);
            texEntry.setTextureView(texture);
            entries.push_back(texEntry);
        }
        WGPUBindGroupDescriptor bgDesc = WGPUBindGroupDescriptor.obtain();
        bgDesc.setLabel("font WGPUBindGroupDescriptor");
        bgDesc.setLayout(layout);
        bgDesc.setEntries(entries);

        device.createBindGroup(bgDesc, bindGroup);
//        bindGroup.setLabel("fontBindGroup");
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
        ImGuiDrawCallbacks.ClearStandardCallbacks(ImGui.GetPlatformIO());
        super.dispose();
        deleteTexture();
        if(vertexByteBuffer != null) {
            BufferUtils.disposeUnsafeByteBuffer(vertexByteBuffer);
        }
        if(indexByteBuffer != null) {
            BufferUtils.disposeUnsafeByteBuffer(indexByteBuffer);
        }
        if (renderPass != null) {
            renderPass.dispose();
        }
        pipeline.release();
        pipeline.dispose();
        shaderModule.release();
        shaderModule.dispose();
        pipelineLayout.release();
        pipelineLayout.dispose();
        commonBindGroupLayout.release();
        commonBindGroupLayout.dispose();
        imageBindGroupLayout.release();
        imageBindGroupLayout.dispose();
        linearSampler.release();
        linearSampler.dispose();
        nearestSampler.release();
        nearestSampler.dispose();
        uniformBuffer.release();
        uniformBuffer.dispose();
        linearCommonBindGroup.release();
        linearCommonBindGroup.dispose();
        nearestCommonBindGroup.release();
        nearestCommonBindGroup.dispose();
        if (vertexBuffer != null) {
            vertexBuffer.release();
            vertexBuffer.dispose();
        }
        if (indexBuffer != null) {
            indexBuffer.release();
            indexBuffer.dispose();
        }

        BufferUtils.disposeUnsafeByteBuffer(projBuffer);
        BufferUtils.disposeUnsafeByteBuffer(gammaBuffer);

        for(int i = 0; i < poolList.size(); i++) {
            WGPUBindGroup bindGroup = poolList.get(i);
            bindGroup.dispose();
        }
    }

    public void saveImGuiData(FileHandle path) {
        if(path != null) {
            NativeString idlString = ImGui.SaveIniSettingsToMemory();
            String s = idlString.c_str();
            path.writeString(s, false);
        }
    }

    public void render(ImDrawData drawData, WGPUCommandEncoder encoder, WGPUTextureView targetView) {
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
        float fb_width = displaySizeX * frameScaleX;
        float fb_height = displaySizeY * frameScaleY;
        if (fb_width <= 0 || fb_height <= 0 || cmdListsCount == 0) {
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

        ImVec2 displayPos = drawData.get_DisplayPos();
        float displayX = displayPos.get_x();
        float displayY = displayPos.get_y();

        // Calculate total buffer sizes
        int totalVtxCountBytes = drawData.get_TotalVtxCount() * VTX_BUFFER_SIZE;
        int totalIdxCountBytes = drawData.get_TotalIdxCount() * IDX_BUFFER_SIZE;
        setupBuffers(totalVtxCountBytes, totalIdxCountBytes);

        WGPURenderPassColorAttachment colorAttach = WGPURenderPassColorAttachment.obtain();
        colorAttach.setDepthSlice(-1);
        colorAttach.setView(targetView);
        colorAttach.setLoadOp(WGPULoadOp.Load);
        colorAttach.setStoreOp(WGPUStoreOp.Store);

        WGPURenderPassDescriptor passDesc = WGPURenderPassDescriptor.obtain();
        WGPUVectorRenderPassColorAttachment attachments = WGPUVectorRenderPassColorAttachment.obtain();
        attachments.push_back(colorAttach);
        passDesc.setDepthStencilAttachment(WGPURenderPassDepthStencilAttachment.NULL);
        passDesc.setColorAttachments(attachments);

        encoder.beginRenderPass(passDesc, renderPass);

        // Upload all vertex and index data
        int vtxOffset = 0;
        int idxOffset = 0;

        boolean fullBuffer = true;

        if(fullBuffer) {
            int vb_write_align_size = (int)memAlign(totalVtxCountBytes, 4);
            int ib_write_align_size = (int)memAlign(totalIdxCountBytes, 4);

            int vtxBufferSize = 0;
            if(vertexByteBuffer != null) {
                vtxBufferSize = vertexByteBuffer.capacity();
            }
            if(vb_write_align_size > vtxBufferSize) {
                if(vertexByteBuffer != null) {
                    BufferUtils.disposeUnsafeByteBuffer(vertexByteBuffer);
                }
                vertexByteBuffer = BufferUtils.newUnsafeByteBuffer(vb_write_align_size);
            }

            int idxBufferSize = 0;
            if(indexByteBuffer != null) {
                idxBufferSize = indexByteBuffer.capacity();
            }
            if(ib_write_align_size > idxBufferSize) {
                if(indexByteBuffer != null) {
                    BufferUtils.disposeUnsafeByteBuffer(indexByteBuffer);
                }
                indexByteBuffer = BufferUtils.newUnsafeByteBuffer(ib_write_align_size);
            }

            ImVectorImDrawListPtr cmdLists = drawData.get_CmdLists();
            for (int n = 0; n < cmdListsCount; n++) {
                ImDrawList drawList = cmdLists.getData(n);
                ImVectorImDrawVert vtxBuffer = drawList.get_VtxBuffer();
                ImVectorImDrawIdx idxBuffer = drawList.get_IdxBuffer();

                int vtxByteSize = vtxBuffer.size() * VTX_BUFFER_SIZE;
                int idxByteSize = idxBuffer.size() * IDX_BUFFER_SIZE;

                NativeObject vtxData = vtxBuffer.get_Data();
                NativeObject idxData = idxBuffer.get_Data();
                NativeUtils.copyToByteBuffer(vtxData, vertexByteBuffer, vtxOffset, vtxByteSize);
                NativeUtils.copyToByteBuffer(idxData, indexByteBuffer, idxOffset, idxByteSize);

                vtxOffset += vtxByteSize;
                idxOffset += idxByteSize;
            }

            device.getQueue().writeBuffer(vertexBuffer, 0, vertexByteBuffer, vb_write_align_size);
            device.getQueue().writeBuffer(indexBuffer, 0, indexByteBuffer, ib_write_align_size);
        }
        else {
//            for (int n = 0; n < cmdListsCount; n++) {
//                ImDrawList cmdList = drawData.get_CmdLists(n);
//                VecVtxBuffer vtxBuffer = cmdList.get_VtxBuffer();
//                VecIdxBuffer idxBuffer = cmdList.get_IdxBuffer();
//
//                vtxOffset = (int)memAlign(vtxOffset, 4);
//                idxOffset = (int)memAlign(idxOffset, 4);
//
//                int vtxByteSize = vtxBuffer.size() * VTX_BUFFER_SIZE;
//                int idxByteSize = idxBuffer.size() * IDX_BUFFER_SIZE;
//                int vb_write_align_size = (int)memAlign(vtxByteSize, 4);
//                int ib_write_align_size = (int)memAlign(idxByteSize, 4);
//
//                boolean useByteBuffer = true;
//
//                if(useByteBuffer) {
//                    int vtxBufferSize = 0;
//                    if(vertexByteBuffer != null) {
//                        vtxBufferSize = vertexByteBuffer.capacity();
//                    }
//                    if(vb_write_align_size > vtxBufferSize) {
//                        if(vertexByteBuffer != null) {
//                            BufferUtils.disposeUnsafeByteBuffer(vertexByteBuffer);
//                        }
//                        vertexByteBuffer = BufferUtils.newUnsafeByteBuffer(vb_write_align_size);
//                    }
//
//                    int idxBufferSize = 0;
//                    if(indexByteBuffer != null) {
//                        idxBufferSize = indexByteBuffer.capacity();
//                    }
//                    if(ib_write_align_size > idxBufferSize) {
//                        if(indexByteBuffer != null) {
//                            BufferUtils.disposeUnsafeByteBuffer(indexByteBuffer);
//                        }
//                        indexByteBuffer = BufferUtils.newUnsafeByteBuffer(ib_write_align_size);
//                    }
//
//                    vtxBuffer.getByteBuffer(vtxByteSize, 0, vertexByteBuffer);
//                    idxBuffer.getByteBuffer(idxByteSize, 0, indexByteBuffer);
//
//                    device.getQueue().writeBuffer(vertexBuffer, vtxOffset, vertexByteBuffer, vb_write_align_size);
//                    device.getQueue().writeBuffer(indexBuffer, idxOffset, indexByteBuffer, ib_write_align_size);
//                }
//                else {
//                    empty_01.native_copy(vtxBuffer.get_Data());
//                    device.getQueue().writeBuffer(vertexBuffer, vtxOffset, empty_01, vb_write_align_size);
//                    empty_01.native_copy(idxBuffer.get_Data());
//                    device.getQueue().writeBuffer(indexBuffer, idxOffset, empty_01, ib_write_align_size);
//                }
//
//                vtxOffset += vb_write_align_size;
//                idxOffset += ib_write_align_size;
//            }
        }

        setupRenderState(displayX, displayY, displaySizeX, displaySizeY, frameScaleX, frameScaleY);

        int global_vtx_offset = 0;
        int global_idx_offset = 0;

        float clip_offX = displayX;
        float clip_offY = displayY;
        float clip_scaleX = frameScaleX;
        float clip_scaleY = frameScaleY;
        ImVectorImDrawListPtr cmdLists = drawData.get_CmdLists();
        for (int n = 0; n < cmdListsCount; n++) {
            ImDrawList draw_list = cmdLists.getData(n);
            ImVectorImDrawCmd cmdBuffer = draw_list.get_CmdBuffer();

            ImVectorImDrawVert vtxBuffer = draw_list.get_VtxBuffer();
            ImVectorImDrawIdx idxBuffer = draw_list.get_IdxBuffer();

            int vtxSize = vtxBuffer.size();
            int idxSize = idxBuffer.size();

            int cmdBufferSize = cmdBuffer.size();
            for (int cmd_i = 0; cmd_i < cmdBufferSize; cmd_i++) {
                ImDrawCmd pcmd = cmdBuffer.getData(cmd_i);
                ImGuiDrawCallbackType callbackType = ImGuiDrawCallbacks.GetType(pcmd);
                if(callbackType != ImGuiDrawCallbackType.None) {
                    if(callbackType == ImGuiDrawCallbackType.ResetRenderState) {
                        setupRenderState(displayX, displayY, displaySizeX, displaySizeY, frameScaleX, frameScaleY);
                    }
                    else if(callbackType == ImGuiDrawCallbackType.SetSamplerLinear) {
                        renderPass.setBindGroup(0, linearCommonBindGroup);
                    }
                    else if(callbackType == ImGuiDrawCallbackType.SetSamplerNearest) {
                        renderPass.setBindGroup(0, nearestCommonBindGroup);
                    }
                    else if(callbackType == ImGuiDrawCallbackType.User) {
                        ImGuiDrawCallbacks.InvokeUserCallback(draw_list, pcmd);
                    }
                    continue;
                }
                ImVec4 clipRect = pcmd.get_ClipRect();

                ImTextureIDRef imTextureIDRef = pcmd.GetTexID();
                long get = imTextureIDRef.Get();
                if(get == 0) {
                    continue;
                }
                int tex_id_hash = ImGuiInternal.ImHashData(NativeTemp.Long_1(get), Long.BYTES, 0); // Pass ImGui long memory to create hash
                WGPUBindGroup bindGroup = textureBindGroups.get(tex_id_hash);
                if(bindGroup == null) {
                    tmp_textureView.native_setAddress(get);
                    bindGroup = obtainBindGroup();
                    createImageBindGroup(imageBindGroupLayout, tmp_textureView, bindGroup);
                    textureBindGroups.put(tex_id_hash, bindGroup);
                }
                renderPass.setBindGroup(1, bindGroup);

                float clipRectX = clipRect.get_x();
                float clipRectY = clipRect.get_y();
                float clipRectZ = clipRect.get_z();
                float clipRectW = clipRect.get_w();
                float clip_minX = (clipRectX - clip_offX) * clip_scaleX;
                float clip_minY = (clipRectY - clip_offY) * clip_scaleY;
                float clip_maxX = (clipRectZ - clip_offX) * clip_scaleX;
                float clip_maxY = (clipRectW - clip_offY) * clip_scaleY;

                // Clamp to framebuffer size
                if(clip_minX < 0) clip_minX = 0;
                if(clip_minY < 0) clip_minY = 0;
                if(clip_maxX > fb_width) { clip_maxX = fb_width; }
                if(clip_maxY > fb_height) { clip_maxY = fb_height; }
                if(clip_maxX <= clip_minX || clip_maxY <= clip_minY) {
                    continue;
                }
                renderPass.setScissorRect((int) clip_minX, (int) clip_minY, (int) (clip_maxX - clip_minX), (int) (clip_maxY - clip_minY));
                renderPass.drawIndexed(pcmd.get_ElemCount(), 1, pcmd.get_IdxOffset() + global_idx_offset, pcmd.get_VtxOffset() + global_vtx_offset, 0);
            }

            global_idx_offset += idxSize;
            global_vtx_offset += vtxSize;
        }

        IntMap.Entries<WGPUBindGroup> entries = textureBindGroups.entries();
        while(entries.hasNext()) {
            IntMap.Entry<WGPUBindGroup> entry = entries.next();
            WGPUBindGroup bindGroup = entry.value;
            bindGroup.release();
            poolList.add(bindGroup);
        }
        textureBindGroups.clear();
        renderPass.end();
        renderPass.release();
    }

    private void updateTexture(ImTextureData tex) {
        ImTextureStatus status = tex.get_Status();

        if (status.isEqual(ImTextureStatus.WantCreate)) {
            WGPUTexture texture = new WGPUTexture();
            WGPUTextureView textureView = new WGPUTextureView();

            int width = tex.get_Width();
            int height = tex.get_Height();
            WGPUTextureDescriptor tex_desc = WGPUTextureDescriptor.obtain();
//            tex_desc.setLabel("Dear ImGui Texture");
            tex_desc.setDimension(WGPUTextureDimension._2D);
            WGPUExtent3D size = tex_desc.getSize();
            size.setWidth(width);
            size.setHeight(height);
            size.setDepthOrArrayLayers(1);
            tex_desc.setSampleCount(1);
            // ImTextureData pixels are RGBA8 regardless of the swap-chain format.
            tex_desc.setFormat(WGPUTextureFormat.RGBA8Unorm);
            tex_desc.setMipLevelCount(1);
            tex_desc.setUsage(WGPUTextureUsage.CopyDst.or(WGPUTextureUsage.TextureBinding));
            device.createTexture(tex_desc, texture);

            WGPUTextureViewDescriptor tex_view_desc = WGPUTextureViewDescriptor.obtain();
            tex_view_desc.setFormat(WGPUTextureFormat.RGBA8Unorm);
            tex_view_desc.setDimension(WGPUTextureViewDimension._2D);
            tex_view_desc.setBaseMipLevel(0);
            tex_view_desc.setMipLevelCount(1);
            tex_view_desc.setBaseArrayLayer(0);
            tex_view_desc.setArrayLayerCount(1);
            tex_view_desc.setAspect(WGPUTextureAspect.All);
            texture.createView(tex_view_desc, textureView);

            long texViewAddress = textureView.native_getAddressLong();
            tex.SetTexID(ImTemp.ImTextureIDRef_1(texViewAddress));
            tex.set_BackendUserData(texture);
        }

        if (status == ImTextureStatus.WantCreate || status == ImTextureStatus.WantUpdates) {
            int width = tex.get_Width();
            int height = tex.get_Height();
            int bytesPerPixel = tex.get_BytesPerPixel();
            NativeObject textureIdl = tex.get_BackendUserData();
            tmp_texture.native_copy(textureIdl);

            int upload_x;
            int upload_y;
            int upload_w;
            int upload_h;

            if(status == ImTextureStatus.WantCreate) {
                upload_x = 0;
                upload_y = 0;
                upload_w = width;
                upload_h = height;
            }
            else {
                ImTextureRect updateRect = tex.get_UpdateRect();
                upload_x = updateRect.get_x();
                upload_y = updateRect.get_y();
                upload_w = updateRect.get_w();
                upload_h = updateRect.get_h();
            }

            WGPUTexelCopyTextureInfo dst_view = WGPUTexelCopyTextureInfo.obtain();
            dst_view.setTexture(tmp_texture);
            dst_view.setMipLevel(0);
            WGPUOrigin3D origin = dst_view.getOrigin();
            origin.set(upload_x, upload_y, 0);
            dst_view.setAspect(WGPUTextureAspect.All);

            WGPUTexelCopyBufferLayout layout = WGPUTexelCopyBufferLayout.obtain();
            layout.setOffset(0);
            layout.setBytesPerRow(width * bytesPerPixel);
            layout.setRowsPerImage(upload_h);
            WGPUExtent3D write_size = WGPUExtent3D.obtain();
            write_size.setWidth(upload_w);
            write_size.setHeight(upload_h);
            write_size.setDepthOrArrayLayers(1);
            int bufferSize = width * upload_h * bytesPerPixel;
            ByteBuffer buffer = BufferUtils.newUnsafeByteBuffer(bufferSize);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            NativeObject pixels = tex.GetPixelsAt(upload_x, upload_y);
            NativeUtils.copyToByteBuffer(pixels, buffer,0 , bufferSize);
            device.getQueue().writeTexture(dst_view, buffer, bufferSize, layout, write_size);
            tex.SetStatus(ImTextureStatus.OK);
        }
        else if (status == ImTextureStatus.WantDestroy && tex.get_UnusedFrames() > 0) {
            destroyTexture(tex);
        }
    }

    private void destroyTexture(ImTextureData tex) {
        ImTextureIDRef imTextureIDRef = tex.GetTexID();
        long texId = imTextureIDRef.Get();
        tmp_textureView.native_setAddress(texId);
        if(tmp_textureView.isValid()) {
            tmp_textureView.release();
        }
        tmp_textureView.native_takeOwnership();
        tmp_textureView.dispose();
        NativeObject backendUserData = tex.get_BackendUserData();
        tmp_texture.native_copy(backendUserData);
        tmp_texture.release();
        tmp_texture.native_takeOwnership();
        tmp_texture.dispose();
        tex.SetTexID(ImTemp.ImTextureIDRef_1(0));
        tex.SetStatus(ImTextureStatus.Destroyed);
        tex.set_BackendUserData(NativeObject.NULL);
    }

    private void setupRenderState(float displayX, float displayY, float displaySizeX, float displaySizeY, float frameScaleX, float frameScaleY) {
        {
            float L = displayX;
            float R = displayX + displaySizeX;
            float T = displayY;
            float B = displayY + displaySizeY;

            projFloatView.put(0, 2.0f / (R - L));
            projFloatView.put(1, 0.0f);
            projFloatView.put(2, 0.0f);
            projFloatView.put(3, 0.0f);
            projFloatView.put(4, 0.0f);
            projFloatView.put(5, 2.0f / (T - B));
            projFloatView.put(6, 0.0f);
            projFloatView.put(7, 0.0f);
            projFloatView.put(8, 0.0f);
            projFloatView.put(9, 0.0f);
            projFloatView.put(10, 0.5f);
            projFloatView.put(11, 0.0f);
            projFloatView.put(12, (R + L) / (L - R));
            projFloatView.put(13, (T + B) / (B - T));
            projFloatView.put(14, 0.5f);
            projFloatView.put(15, 1.0f);

            float gamma = getGamma(renderFormat);
            float contrast = getContrast(renderFormat);
            gammaFloatBuffer.put(0, gamma);
            gammaFloatBuffer.put(1, contrast);
            device.getQueue().writeBuffer(uniformBuffer, 0, projBuffer, prjSizeBytes);
            device.getQueue().writeBuffer(uniformBuffer, prjSizeBytes, gammaBuffer, gamaSizeBytes);
        }

        // Setup viewport
        float fbWidth = displaySizeX * frameScaleX;
        float fbHeight = displaySizeY * frameScaleY;
        renderPass.setViewport(0, 0, fbWidth, fbHeight, 0, 1);

        // Bind shader and vertex buffers
        renderPass.setVertexBuffer(0, vertexBuffer, 0, vertexBufferSizeBytes);
        renderPass.setIndexBuffer(indexBuffer, WGPUIndexFormat.Uint16, 0, indexBufferSizeBytes);
        renderPass.setPipeline(pipeline);
        renderPass.setBindGroup(0, linearCommonBindGroup);

        // Setup blend factor
        WGPUColor color = WGPUColor.obtain();
        color.setColor(0.f, 0.f, 0.f, 0.f);
        renderPass.setBlendConstant(color);
    }

    private void setupBuffers(int totalVtxCountBytes, int totalIdxCountBytes) {
        if (totalVtxCountBytes > vertexBufferSizeBytes) {
            if (vertexBuffer != null) {
                vertexBuffer.destroy();
                vertexBuffer.release();
                vertexBuffer.dispose();
            }
            vertexBufferSizeBytes = totalVtxCountBytes;
            int vertAligned = (int)memAlign(vertexBufferSizeBytes, 4);

            WGPUBufferDescriptor desc = WGPUBufferDescriptor.obtain();
            desc.setSize(vertAligned);
            desc.setUsage(WGPUBufferUsage.Vertex.or(WGPUBufferUsage.CopyDst));
            vertexBuffer = device.createBuffer(desc);
//            vertexBuffer.setLabel("Vertex Buffer");
        }
        if (totalIdxCountBytes > indexBufferSizeBytes) {
            if (indexBuffer != null) {
                indexBuffer.destroy();
                indexBuffer.release();
                indexBuffer.dispose();
            }
            indexBufferSizeBytes = totalIdxCountBytes;
            int vertAligned = (int)memAlign(indexBufferSizeBytes, 4);

            WGPUBufferDescriptor desc = WGPUBufferDescriptor.obtain();
            desc.setSize(vertAligned);
            desc.setUsage(WGPUBufferUsage.Index.or(WGPUBufferUsage.CopyDst));
            indexBuffer = device.createBuffer(desc);
//            indexBuffer.setLabel("Index Buffer");
        }
    }

    private static long memAlign(long size, long align) {
        return ((size + (align - 1)) & ~(align - 1));
    }

    public float getGamma(WGPUTextureFormat surfaceFormat) {
        switch(surfaceFormat) {
            case ASTC10x10UnormSrgb:
            case ASTC10x5UnormSrgb:
            case ASTC10x6UnormSrgb:
            case ASTC10x8UnormSrgb:
            case ASTC12x10UnormSrgb:
            case ASTC12x12UnormSrgb:
            case ASTC4x4UnormSrgb:
            case ASTC5x5UnormSrgb:
            case ASTC6x5UnormSrgb:
            case ASTC6x6UnormSrgb:
            case ASTC8x5UnormSrgb:
            case ASTC8x6UnormSrgb:
            case ASTC8x8UnormSrgb:
            case BC1RGBAUnormSrgb:
            case BC2RGBAUnormSrgb:
            case BC3RGBAUnormSrgb:
            case BC7RGBAUnormSrgb:
            case BGRA8UnormSrgb:
            case ETC2RGB8A1UnormSrgb:
            case ETC2RGB8UnormSrgb:
            case ETC2RGBA8UnormSrgb:
            case RGBA8UnormSrgb:
                return 1.0f;
        }
        return 2.2f;
    }

    public float getContrast(WGPUTextureFormat surfaceFormat) {
        switch(surfaceFormat) {
            case ASTC10x10UnormSrgb:
            case ASTC10x5UnormSrgb:
            case ASTC10x6UnormSrgb:
            case ASTC10x8UnormSrgb:
            case ASTC12x10UnormSrgb:
            case ASTC12x12UnormSrgb:
            case ASTC4x4UnormSrgb:
            case ASTC5x5UnormSrgb:
            case ASTC6x5UnormSrgb:
            case ASTC6x6UnormSrgb:
            case ASTC8x5UnormSrgb:
            case ASTC8x6UnormSrgb:
            case ASTC8x8UnormSrgb:
            case BC1RGBAUnormSrgb:
            case BC2RGBAUnormSrgb:
            case BC3RGBAUnormSrgb:
            case BC7RGBAUnormSrgb:
            case BGRA8UnormSrgb:
            case ETC2RGB8A1UnormSrgb:
            case ETC2RGB8UnormSrgb:
            case ETC2RGBA8UnormSrgb:
            case RGBA8UnormSrgb:
                return 1.0f;
        }
        return 1.03f;
    }

    private WGPUBindGroup obtainBindGroup() {
        if(poolList.size() > 0) {
            return poolList.remove(0);
        } else {
            return new WGPUBindGroup();
        }
    }

    private String getShader() {
        String wgslCode = "struct VertexInput {\n" +
                "    @location(0) position: vec2<f32>,\n" +
                "    @location(1) uv: vec2<f32>,\n" +
                "    @location(2) color: vec4<f32>,\n" +
                "};\n" +
                "\n" +
                "struct VertexOutput {\n" +
                "    @builtin(position) position: vec4<f32>,\n" +
                "    @location(0) color: vec4<f32>,\n" +
                "    @location(1) uv: vec2<f32>,\n" +
                "};\n" +
                "\n" +
                "struct Uniforms {\n" +
                "    mvp: mat4x4<f32>,\n" +
                "    gamma: f32,\n" +
                "    contrast: f32\n" +
                "};\n" +
                "\n" +
                "@group(0) @binding(0) var<uniform> uniforms: Uniforms;\n" +
                "@group(0) @binding(1) var s: sampler;\n" +
                "@group(1) @binding(0) var t: texture_2d<f32>;\n" +
                "\n" +
                "@vertex\n" +
                "fn vs_main(in: VertexInput) -> VertexOutput {\n" +
                "    var out: VertexOutput;\n" +
                "    out.position = uniforms.mvp * vec4<f32>(in.position, 0.0, 1.0);\n" +
                "    out.color = pow(in.color, vec4<f32>(2.2));\n" +
                "    out.uv = in.uv;\n" +
                "    return out;\n" +
                "}\n" +
                "\n" +
                "@fragment\n" +
                "fn fs_main(in: VertexOutput) -> @location(0) vec4<f32> {\n" +
                "    let color = in.color * textureSample(t, s, in.uv);\n" +
                "    let corrected_color = pow(color.rgb, vec3<f32>(1.0 / uniforms.gamma));\n" +
                "    let contrasted = (corrected_color - 0.5) * uniforms.contrast + 0.5;\n" +
                "    return vec4<f32>(contrasted, color.a);\n" +
                "}\n";
        return wgslCode;
    }
}
