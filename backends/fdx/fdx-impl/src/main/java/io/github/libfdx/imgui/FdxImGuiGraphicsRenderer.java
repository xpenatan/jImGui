package io.github.libfdx.imgui;

import com.github.xpenatan.jParser.api.NativeObject;
import com.github.xpenatan.jparser.runtime.helper.NativeUtils;
import imgui.ImDrawCmd;
import imgui.ImDrawData;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImGuiDrawCallbacks;
import imgui.ImGuiIO;
import imgui.ImTemp;
import imgui.ImTextureData;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.ImVectorImDrawCmd;
import imgui.ImVectorImDrawIdx;
import imgui.ImVectorImDrawListPtr;
import imgui.ImVectorImDrawVert;
import imgui.ImVectorImTextureDataPtr;
import imgui.enums.ImGuiBackendFlags;
import imgui.enums.ImGuiDrawCallbackType;
import imgui.enums.ImTextureFormat;
import imgui.enums.ImTextureStatus;
import io.github.libfdx.core.FdxException;
import io.github.libfdx.graphics.Buffer;
import io.github.libfdx.graphics.BufferDescriptor;
import io.github.libfdx.graphics.GraphicsContext;
import io.github.libfdx.graphics.GraphicsFrame;
import io.github.libfdx.graphics.LoadOp;
import io.github.libfdx.graphics.PrimitiveTopology;
import io.github.libfdx.graphics.RenderPass;
import io.github.libfdx.graphics.RenderPassDescriptor;
import io.github.libfdx.graphics.RenderPipeline;
import io.github.libfdx.graphics.RenderPipelineDescriptor;
import io.github.libfdx.graphics.ShaderAttribute;
import io.github.libfdx.graphics.ShaderBinding;
import io.github.libfdx.graphics.ShaderBindingType;
import io.github.libfdx.graphics.ShaderBundle;
import io.github.libfdx.graphics.ShaderModule;
import io.github.libfdx.graphics.ShaderProfile;
import io.github.libfdx.graphics.ShaderReflection;
import io.github.libfdx.graphics.ShaderTarget;
import io.github.libfdx.graphics.StoreOp;
import io.github.libfdx.graphics.Texture;
import io.github.libfdx.graphics.TextureDescriptor;
import io.github.libfdx.graphics.VertexAttribute;
import io.github.libfdx.graphics.VertexFormat;
import io.github.libfdx.graphics.VertexLayout;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FdxImGuiGraphicsRenderer implements FdxImGuiRenderer, FdxImGuiViewportRendererFactory {
    private static final int IM_DRAW_VERT_SIZE = 20;
    private static final int IM_DRAW_IDX_SIZE = 2;
    private static final int DEFAULT_VERTEX_BYTES = 8192 * IM_DRAW_VERT_SIZE;
    private static final int DEFAULT_INDEX_BYTES = 16384 * IM_DRAW_IDX_SIZE;
    private static final VertexLayout IMGUI_VERTEX_LAYOUT = VertexLayout.of(
            IM_DRAW_VERT_SIZE,
            VertexAttribute.of(0, VertexFormat.FLOAT32X2, 0),
            VertexAttribute.of(1, VertexFormat.FLOAT32X2, 8),
            VertexAttribute.of(2, VertexFormat.UNORM8X4, 16));
    private static final String IMGUI_WGSL_PREFIX = """
            struct VertexInput {
                @location(0) position : vec2f,
                @location(1) texCoord : vec2f,
                @location(2) color : vec4f,
            };
            struct VertexOutput {
                @builtin(position) position : vec4f,
                @location(0) texCoord : vec2f,
                @location(1) color : vec4f,
            };
            @group(0) @binding(0) var u_texture : texture_2d<f32>;
            @group(0) @binding(1) var u_sampler : sampler;
            @vertex
            fn vertexMain(input : VertexInput) -> VertexOutput {
                var output : VertexOutput;
                output.position = vec4f(input.position, 0.0, 1.0);
                output.texCoord = input.texCoord;
                output.color = input.color;
                return output;
            }
            """;
    private static final String IMGUI_WGSL_LINEAR = IMGUI_WGSL_PREFIX + """
            fn clampTexel(texel : vec2i, dimensions : vec2i) -> vec2i {
                return clamp(texel, vec2i(0), dimensions - vec2i(1));
            }
            @fragment
            fn fragmentMain(input : VertexOutput) -> @location(0) vec4f {
                let dimensions = vec2i(textureDimensions(u_texture));
                let position = input.texCoord * vec2f(dimensions) - vec2f(0.5);
                let base = vec2i(floor(position));
                let weight = fract(position);
                let c00 = textureLoad(u_texture, clampTexel(base, dimensions), 0);
                let c10 = textureLoad(u_texture, clampTexel(base + vec2i(1, 0), dimensions), 0);
                let c01 = textureLoad(u_texture, clampTexel(base + vec2i(0, 1), dimensions), 0);
                let c11 = textureLoad(u_texture, clampTexel(base + vec2i(1, 1), dimensions), 0);
                return mix(mix(c00, c10, weight.x), mix(c01, c11, weight.x), weight.y) * input.color;
            }
            """;
    private static final String IMGUI_WGSL_NEAREST = IMGUI_WGSL_PREFIX + """
            @fragment
            fn fragmentMain(input : VertexOutput) -> @location(0) vec4f {
                let dimensions = vec2i(textureDimensions(u_texture));
                let texel = clamp(vec2i(floor(input.texCoord * vec2f(dimensions))), vec2i(0), dimensions - vec2i(1));
                return textureLoad(u_texture, texel, 0) * input.color;
            }
            """;
    private static final String IMGUI_VERTEX_GLSL = """
            #version 330 core
            layout(location = 0) in vec2 a_position;
            layout(location = 1) in vec2 a_texCoord;
            layout(location = 2) in vec4 a_color;
            out vec2 v_texCoord;
            out vec4 v_color;
            void main() {
                v_texCoord = a_texCoord;
                v_color = a_color;
                gl_Position = vec4(a_position, 0.0, 1.0);
            }
            """;
    private static final String IMGUI_FRAGMENT_LINEAR_GLSL = """
            #version 330 core
            in vec2 v_texCoord;
            in vec4 v_color;
            uniform sampler2D u_texture;
            out vec4 fragColor;
            void main() {
                ivec2 dimensions = textureSize(u_texture, 0);
                vec2 position = v_texCoord * vec2(dimensions) - vec2(0.5);
                ivec2 base = ivec2(floor(position));
                vec2 weight = fract(position);
                ivec2 maxTexel = dimensions - ivec2(1);
                vec4 c00 = texelFetch(u_texture, clamp(base, ivec2(0), maxTexel), 0);
                vec4 c10 = texelFetch(u_texture, clamp(base + ivec2(1, 0), ivec2(0), maxTexel), 0);
                vec4 c01 = texelFetch(u_texture, clamp(base + ivec2(0, 1), ivec2(0), maxTexel), 0);
                vec4 c11 = texelFetch(u_texture, clamp(base + ivec2(1, 1), ivec2(0), maxTexel), 0);
                fragColor = mix(mix(c00, c10, weight.x), mix(c01, c11, weight.x), weight.y) * v_color;
            }
            """;
    private static final String IMGUI_FRAGMENT_NEAREST_GLSL = """
            #version 330 core
            in vec2 v_texCoord;
            in vec4 v_color;
            uniform sampler2D u_texture;
            out vec4 fragColor;
            void main() {
                ivec2 dimensions = textureSize(u_texture, 0);
                ivec2 texel = clamp(ivec2(floor(v_texCoord * vec2(dimensions))), ivec2(0), dimensions - ivec2(1));
                fragColor = texelFetch(u_texture, texel, 0) * v_color;
            }
            """;
    private static final String IMGUI_VERTEX_GLSL_ES = """
            #version 300 es
            precision highp float;
            layout(location = 0) in vec2 a_position;
            layout(location = 1) in vec2 a_texCoord;
            layout(location = 2) in vec4 a_color;
            out vec2 v_texCoord;
            out vec4 v_color;
            void main() {
                v_texCoord = a_texCoord;
                v_color = a_color;
                gl_Position = vec4(a_position, 0.0, 1.0);
            }
            """;
    private static final String IMGUI_FRAGMENT_LINEAR_GLSL_ES = """
            #version 300 es
            precision highp float;
            precision highp int;
            in vec2 v_texCoord;
            in vec4 v_color;
            uniform sampler2D u_texture;
            out vec4 fragColor;
            void main() {
                ivec2 dimensions = textureSize(u_texture, 0);
                vec2 position = v_texCoord * vec2(dimensions) - vec2(0.5);
                ivec2 base = ivec2(floor(position));
                vec2 weight = fract(position);
                ivec2 maxTexel = dimensions - ivec2(1);
                vec4 c00 = texelFetch(u_texture, clamp(base, ivec2(0), maxTexel), 0);
                vec4 c10 = texelFetch(u_texture, clamp(base + ivec2(1, 0), ivec2(0), maxTexel), 0);
                vec4 c01 = texelFetch(u_texture, clamp(base + ivec2(0, 1), ivec2(0), maxTexel), 0);
                vec4 c11 = texelFetch(u_texture, clamp(base + ivec2(1, 1), ivec2(0), maxTexel), 0);
                fragColor = mix(mix(c00, c10, weight.x), mix(c01, c11, weight.x), weight.y) * v_color;
            }
            """;
    private static final String IMGUI_FRAGMENT_NEAREST_GLSL_ES = """
            #version 300 es
            precision highp float;
            precision highp int;
            in vec2 v_texCoord;
            in vec4 v_color;
            uniform sampler2D u_texture;
            out vec4 fragColor;
            void main() {
                ivec2 dimensions = textureSize(u_texture, 0);
                ivec2 texel = clamp(ivec2(floor(v_texCoord * vec2(dimensions))), ivec2(0), dimensions - ivec2(1));
                fragColor = texelFetch(u_texture, texel, 0) * v_color;
            }
            """;
    private static final String IMGUI_VULKAN_VERTEX_GLSL = """
            #version 450
            layout(location = 0) in vec2 a_position;
            layout(location = 1) in vec2 a_texCoord;
            layout(location = 2) in vec4 a_color;
            layout(location = 0) out vec2 v_texCoord;
            layout(location = 1) out vec4 v_color;
            void vertexMain() {
                v_texCoord = a_texCoord;
                v_color = a_color;
                gl_Position = vec4(a_position, 0.0, 1.0);
            }
            """;
    private static final String IMGUI_VULKAN_FRAGMENT_LINEAR_GLSL = """
            #version 450
            layout(set = 0, binding = 0) uniform sampler2D u_texture;
            layout(location = 0) in vec2 v_texCoord;
            layout(location = 1) in vec4 v_color;
            layout(location = 0) out vec4 fragColor;
            void fragmentMain() {
                ivec2 dimensions = textureSize(u_texture, 0);
                vec2 position = v_texCoord * vec2(dimensions) - vec2(0.5);
                ivec2 base = ivec2(floor(position));
                vec2 weight = fract(position);
                ivec2 maxTexel = dimensions - ivec2(1);
                vec4 c00 = texelFetch(u_texture, clamp(base, ivec2(0), maxTexel), 0);
                vec4 c10 = texelFetch(u_texture, clamp(base + ivec2(1, 0), ivec2(0), maxTexel), 0);
                vec4 c01 = texelFetch(u_texture, clamp(base + ivec2(0, 1), ivec2(0), maxTexel), 0);
                vec4 c11 = texelFetch(u_texture, clamp(base + ivec2(1, 1), ivec2(0), maxTexel), 0);
                fragColor = mix(mix(c00, c10, weight.x), mix(c01, c11, weight.x), weight.y) * v_color;
            }
            """;
    private static final String IMGUI_VULKAN_FRAGMENT_NEAREST_GLSL = """
            #version 450
            layout(set = 0, binding = 0) uniform sampler2D u_texture;
            layout(location = 0) in vec2 v_texCoord;
            layout(location = 1) in vec4 v_color;
            layout(location = 0) out vec4 fragColor;
            void fragmentMain() {
                ivec2 dimensions = textureSize(u_texture, 0);
                ivec2 texel = clamp(ivec2(floor(v_texCoord * vec2(dimensions))), ivec2(0), dimensions - ivec2(1));
                fragColor = texelFetch(u_texture, texel, 0) * v_color;
            }
            """;
    // Vulkan consumes SPIR-V at runtime; keep shader edits in the readable GLSL above.
    private static final int[] IMGUI_VULKAN_VERTEX_SPIRV = {
            0x07230203,0x00010600,0x00070000,0x00000023,0x00000000,0x00020011,0x00000001,0x0006000b,
            0x00000001,0x4c534c47,0x6474732e,0x3035342e,0x00000000,0x0003000e,0x00000000,0x00000001,
            0x000c000f,0x00000000,0x00000002,0x74726576,0x614d7865,0x00006e69,0x00000003,0x00000004,
            0x00000005,0x00000006,0x00000007,0x00000008,0x00030003,0x00000002,0x000001c2,0x000a0004,
            0x475f4c47,0x4c474f4f,0x70635f45,0x74735f70,0x5f656c79,0x656e696c,0x7269645f,0x69746365,
            0x00006576,0x00080004,0x475f4c47,0x4c474f4f,0x6e695f45,0x64756c63,0x69645f65,0x74636572,
            0x00657669,0x00050005,0x00000002,0x74726576,0x614d7865,0x00006e69,0x00050005,0x00000003,
            0x65745f76,0x6f6f4378,0x00006472,0x00050005,0x00000004,0x65745f61,0x6f6f4378,0x00006472,
            0x00040005,0x00000005,0x6f635f76,0x00726f6c,0x00040005,0x00000006,0x6f635f61,0x00726f6c,
            0x00060005,0x00000009,0x505f6c67,0x65567265,0x78657472,0x00000000,0x00060006,0x00000009,
            0x00000000,0x505f6c67,0x7469736f,0x006e6f69,0x00070006,0x00000009,0x00000001,0x505f6c67,
            0x746e696f,0x657a6953,0x00000000,0x00070006,0x00000009,0x00000002,0x435f6c67,0x4470696c,
            0x61747369,0x0065636e,0x00070006,0x00000009,0x00000003,0x435f6c67,0x446c6c75,0x61747369,
            0x0065636e,0x00030005,0x00000007,0x00000000,0x00050005,0x00000008,0x6f705f61,0x69746973,
            0x00006e6f,0x00040047,0x00000003,0x0000001e,0x00000000,0x00040047,0x00000004,0x0000001e,
            0x00000001,0x00040047,0x00000005,0x0000001e,0x00000001,0x00040047,0x00000006,0x0000001e,
            0x00000002,0x00050048,0x00000009,0x00000000,0x0000000b,0x00000000,0x00050048,0x00000009,
            0x00000001,0x0000000b,0x00000001,0x00050048,0x00000009,0x00000002,0x0000000b,0x00000003,
            0x00050048,0x00000009,0x00000003,0x0000000b,0x00000004,0x00030047,0x00000009,0x00000002,
            0x00040047,0x00000008,0x0000001e,0x00000000,0x00020013,0x0000000a,0x00030021,0x0000000b,
            0x0000000a,0x00030016,0x0000000c,0x00000020,0x00040017,0x0000000d,0x0000000c,0x00000002,
            0x00040020,0x0000000e,0x00000003,0x0000000d,0x0004003b,0x0000000e,0x00000003,0x00000003,
            0x00040020,0x0000000f,0x00000001,0x0000000d,0x0004003b,0x0000000f,0x00000004,0x00000001,
            0x00040017,0x00000010,0x0000000c,0x00000004,0x00040020,0x00000011,0x00000003,0x00000010,
            0x0004003b,0x00000011,0x00000005,0x00000003,0x00040020,0x00000012,0x00000001,0x00000010,
            0x0004003b,0x00000012,0x00000006,0x00000001,0x00040015,0x00000013,0x00000020,0x00000000,
            0x0004002b,0x00000013,0x00000014,0x00000001,0x0004001c,0x00000015,0x0000000c,0x00000014,
            0x0006001e,0x00000009,0x00000010,0x0000000c,0x00000015,0x00000015,0x00040020,0x00000016,
            0x00000003,0x00000009,0x0004003b,0x00000016,0x00000007,0x00000003,0x00040015,0x00000017,
            0x00000020,0x00000001,0x0004002b,0x00000017,0x00000018,0x00000000,0x0004003b,0x0000000f,
            0x00000008,0x00000001,0x0004002b,0x0000000c,0x00000019,0x00000000,0x0004002b,0x0000000c,
            0x0000001a,0x3f800000,0x00050036,0x0000000a,0x00000002,0x00000000,0x0000000b,0x000200f8,
            0x0000001b,0x0004003d,0x0000000d,0x0000001c,0x00000004,0x0003003e,0x00000003,0x0000001c,
            0x0004003d,0x00000010,0x0000001d,0x00000006,0x0003003e,0x00000005,0x0000001d,0x0004003d,
            0x0000000d,0x0000001e,0x00000008,0x00050051,0x0000000c,0x0000001f,0x0000001e,0x00000000,
            0x00050051,0x0000000c,0x00000020,0x0000001e,0x00000001,0x00070050,0x00000010,0x00000021,
            0x0000001f,0x00000020,0x00000019,0x0000001a,0x00050041,0x00000011,0x00000022,0x00000007,
            0x00000018,0x0003003e,0x00000022,0x00000021,0x000100fd,0x00010038
    };
    private static final int[] IMGUI_VULKAN_FRAGMENT_LINEAR_SPIRV = {
            0x07230203,0x00010000,0x0008000b,0x00000069,0x00000000,0x00020011,0x00000001,0x00020011,
            0x00000032,0x0006000b,0x00000001,0x4c534c47,0x6474732e,0x3035342e,0x00000000,0x0003000e,
            0x00000000,0x00000001,0x000a000f,0x00000004,0x00000004,0x67617266,0x746e656d,0x6e69614d,
            0x00000000,0x00000017,0x00000050,0x00000066,0x00030010,0x00000004,0x00000007,0x00030003,
            0x00000002,0x000001c2,0x00060005,0x00000004,0x67617266,0x746e656d,0x6e69614d,0x00000000,
            0x00050005,0x0000000e,0x65745f75,0x72757478,0x00000065,0x00050005,0x00000017,0x65745f76,
            0x6f6f4378,0x00006472,0x00050005,0x00000050,0x67617266,0x6f6c6f43,0x00000072,0x00040005,
            0x00000066,0x6f635f76,0x00726f6c,0x00040047,0x0000000e,0x00000022,0x00000000,0x00040047,
            0x0000000e,0x00000021,0x00000000,0x00040047,0x00000017,0x0000001e,0x00000000,0x00040047,
            0x00000050,0x0000001e,0x00000000,0x00040047,0x00000066,0x0000001e,0x00000001,0x00020013,
            0x00000002,0x00030021,0x00000003,0x00000002,0x00040015,0x00000006,0x00000020,0x00000001,
            0x00040017,0x00000007,0x00000006,0x00000002,0x00030016,0x0000000a,0x00000020,0x00090019,
            0x0000000b,0x0000000a,0x00000001,0x00000000,0x00000000,0x00000000,0x00000001,0x00000000,
            0x0003001b,0x0000000c,0x0000000b,0x00040020,0x0000000d,0x00000000,0x0000000c,0x0004003b,
            0x0000000d,0x0000000e,0x00000000,0x0004002b,0x00000006,0x00000010,0x00000000,0x00040017,
            0x00000013,0x0000000a,0x00000002,0x00040020,0x00000016,0x00000001,0x00000013,0x0004003b,
            0x00000016,0x00000017,0x00000001,0x0004002b,0x0000000a,0x0000001c,0x3f000000,0x0005002c,
            0x00000013,0x0000001d,0x0000001c,0x0000001c,0x0004002b,0x00000006,0x00000028,0x00000001,
            0x0005002c,0x00000007,0x00000029,0x00000028,0x00000028,0x00040017,0x0000002b,0x0000000a,
            0x00000004,0x0005002c,0x00000007,0x00000030,0x00000010,0x00000010,0x0005002c,0x00000007,
            0x00000038,0x00000028,0x00000010,0x0005002c,0x00000007,0x00000041,0x00000010,0x00000028,
            0x00040020,0x0000004f,0x00000003,0x0000002b,0x0004003b,0x0000004f,0x00000050,0x00000003,
            0x00040020,0x00000065,0x00000001,0x0000002b,0x0004003b,0x00000065,0x00000066,0x00000001,
            0x00050036,0x00000002,0x00000004,0x00000000,0x00000003,0x000200f8,0x00000005,0x0004003d,
            0x0000000c,0x0000000f,0x0000000e,0x00040064,0x0000000b,0x00000011,0x0000000f,0x00050067,
            0x00000007,0x00000012,0x00000011,0x00000010,0x0004003d,0x00000013,0x00000018,0x00000017,
            0x0004006f,0x00000013,0x0000001a,0x00000012,0x00050085,0x00000013,0x0000001b,0x00000018,
            0x0000001a,0x00050083,0x00000013,0x0000001e,0x0000001b,0x0000001d,0x0006000c,0x00000013,
            0x00000021,0x00000001,0x00000008,0x0000001e,0x0004006e,0x00000007,0x00000022,0x00000021,
            0x0006000c,0x00000013,0x00000025,0x00000001,0x0000000a,0x0000001e,0x00050082,0x00000007,
            0x0000002a,0x00000012,0x00000029,0x0008000c,0x00000007,0x00000032,0x00000001,0x0000002d,
            0x00000022,0x00000030,0x0000002a,0x00040064,0x0000000b,0x00000033,0x0000000f,0x0007005f,
            0x0000002b,0x00000034,0x00000033,0x00000032,0x00000002,0x00000010,0x00050080,0x00000007,
            0x00000039,0x00000022,0x00000038,0x0008000c,0x00000007,0x0000003b,0x00000001,0x0000002d,
            0x00000039,0x00000030,0x0000002a,0x00040064,0x0000000b,0x0000003c,0x0000000f,0x0007005f,
            0x0000002b,0x0000003d,0x0000003c,0x0000003b,0x00000002,0x00000010,0x00050080,0x00000007,
            0x00000042,0x00000022,0x00000041,0x0008000c,0x00000007,0x00000044,0x00000001,0x0000002d,
            0x00000042,0x00000030,0x0000002a,0x00040064,0x0000000b,0x00000045,0x0000000f,0x0007005f,
            0x0000002b,0x00000046,0x00000045,0x00000044,0x00000002,0x00000010,0x00050080,0x00000007,
            0x0000004a,0x00000022,0x00000029,0x0008000c,0x00000007,0x0000004c,0x00000001,0x0000002d,
            0x0000004a,0x00000030,0x0000002a,0x00040064,0x0000000b,0x0000004d,0x0000000f,0x0007005f,
            0x0000002b,0x0000004e,0x0000004d,0x0000004c,0x00000002,0x00000010,0x00050051,0x0000000a,
            0x00000057,0x00000025,0x00000000,0x00070050,0x0000002b,0x00000058,0x00000057,0x00000057,
            0x00000057,0x00000057,0x0008000c,0x0000002b,0x00000059,0x00000001,0x0000002e,0x00000034,
            0x0000003d,0x00000058,0x0008000c,0x0000002b,0x0000005f,0x00000001,0x0000002e,0x00000046,
            0x0000004e,0x00000058,0x00050051,0x0000000a,0x00000062,0x00000025,0x00000001,0x00070050,
            0x0000002b,0x00000063,0x00000062,0x00000062,0x00000062,0x00000062,0x0008000c,0x0000002b,
            0x00000064,0x00000001,0x0000002e,0x00000059,0x0000005f,0x00000063,0x0004003d,0x0000002b,
            0x00000067,0x00000066,0x00050085,0x0000002b,0x00000068,0x00000064,0x00000067,0x0003003e,
            0x00000050,0x00000068,0x000100fd,0x00010038
    };
    private static final int[] IMGUI_VULKAN_FRAGMENT_NEAREST_SPIRV = {
            0x07230203,0x00010000,0x0008000b,0x0000002e,0x00000000,0x00020011,0x00000001,0x00020011,
            0x00000032,0x0006000b,0x00000001,0x4c534c47,0x6474732e,0x3035342e,0x00000000,0x0003000e,
            0x00000000,0x00000001,0x000a000f,0x00000004,0x00000004,0x67617266,0x746e656d,0x6e69614d,
            0x00000000,0x00000016,0x00000025,0x0000002b,0x00030010,0x00000004,0x00000007,0x00030003,
            0x00000002,0x000001c2,0x00060005,0x00000004,0x67617266,0x746e656d,0x6e69614d,0x00000000,
            0x00050005,0x0000000e,0x65745f75,0x72757478,0x00000065,0x00050005,0x00000016,0x65745f76,
            0x6f6f4378,0x00006472,0x00050005,0x00000025,0x67617266,0x6f6c6f43,0x00000072,0x00040005,
            0x0000002b,0x6f635f76,0x00726f6c,0x00040047,0x0000000e,0x00000022,0x00000000,0x00040047,
            0x0000000e,0x00000021,0x00000000,0x00040047,0x00000016,0x0000001e,0x00000000,0x00040047,
            0x00000025,0x0000001e,0x00000000,0x00040047,0x0000002b,0x0000001e,0x00000001,0x00020013,
            0x00000002,0x00030021,0x00000003,0x00000002,0x00040015,0x00000006,0x00000020,0x00000001,
            0x00040017,0x00000007,0x00000006,0x00000002,0x00030016,0x0000000a,0x00000020,0x00090019,
            0x0000000b,0x0000000a,0x00000001,0x00000000,0x00000000,0x00000000,0x00000001,0x00000000,
            0x0003001b,0x0000000c,0x0000000b,0x00040020,0x0000000d,0x00000000,0x0000000c,0x0004003b,
            0x0000000d,0x0000000e,0x00000000,0x0004002b,0x00000006,0x00000010,0x00000000,0x00040017,
            0x00000014,0x0000000a,0x00000002,0x00040020,0x00000015,0x00000001,0x00000014,0x0004003b,
            0x00000015,0x00000016,0x00000001,0x0005002c,0x00000007,0x0000001d,0x00000010,0x00000010,
            0x0004002b,0x00000006,0x0000001f,0x00000001,0x0005002c,0x00000007,0x00000020,0x0000001f,
            0x0000001f,0x00040017,0x00000023,0x0000000a,0x00000004,0x00040020,0x00000024,0x00000003,
            0x00000023,0x0004003b,0x00000024,0x00000025,0x00000003,0x00040020,0x0000002a,0x00000001,
            0x00000023,0x0004003b,0x0000002a,0x0000002b,0x00000001,0x00050036,0x00000002,0x00000004,
            0x00000000,0x00000003,0x000200f8,0x00000005,0x0004003d,0x0000000c,0x0000000f,0x0000000e,
            0x00040064,0x0000000b,0x00000011,0x0000000f,0x00050067,0x00000007,0x00000012,0x00000011,
            0x00000010,0x0004003d,0x00000014,0x00000017,0x00000016,0x0004006f,0x00000014,0x00000019,
            0x00000012,0x00050085,0x00000014,0x0000001a,0x00000017,0x00000019,0x0006000c,0x00000014,
            0x0000001b,0x00000001,0x00000008,0x0000001a,0x0004006e,0x00000007,0x0000001c,0x0000001b,
            0x00050082,0x00000007,0x00000021,0x00000012,0x00000020,0x0008000c,0x00000007,0x00000022,
            0x00000001,0x0000002d,0x0000001c,0x0000001d,0x00000021,0x00040064,0x0000000b,0x00000028,
            0x0000000f,0x0007005f,0x00000023,0x00000029,0x00000028,0x00000022,0x00000002,0x00000010,
            0x0004003d,0x00000023,0x0000002c,0x0000002b,0x00050085,0x00000023,0x0000002d,0x00000029,
            0x0000002c,0x0003003e,0x00000025,0x0000002d,0x000100fd,0x00010038
    };
    private static final ShaderBundle IMGUI_LINEAR_SHADER = createShaderBundle(
            "imgui-linear",
            IMGUI_WGSL_LINEAR,
            IMGUI_FRAGMENT_LINEAR_GLSL,
            IMGUI_FRAGMENT_LINEAR_GLSL_ES,
            IMGUI_VULKAN_FRAGMENT_LINEAR_SPIRV);
    private static final ShaderBundle IMGUI_NEAREST_SHADER = createShaderBundle(
            "imgui-nearest",
            IMGUI_WGSL_NEAREST,
            IMGUI_FRAGMENT_NEAREST_GLSL,
            IMGUI_FRAGMENT_NEAREST_GLSL_ES,
            IMGUI_VULKAN_FRAGMENT_NEAREST_SPIRV);

    private static ShaderBundle createShaderBundle(String label, String wgsl, String fragmentGlsl,
            String fragmentGlslEs, int[] fragmentSpirv) {
        return ShaderBundle.builder(label)
            .profile(ShaderProfile.PORTABLE_WEBGPU)
            .wgsl(wgsl)
            .generatedGlsl(ShaderTarget.OPENGL_GLSL, IMGUI_VERTEX_GLSL, fragmentGlsl)
            .generatedGlsl(ShaderTarget.GLES_GLSL_ES, IMGUI_VERTEX_GLSL_ES, fragmentGlslEs)
            .generatedGlsl(ShaderTarget.WEBGL_GLSL_ES, IMGUI_VERTEX_GLSL_ES, fragmentGlslEs)
            .generatedSpirv(IMGUI_VULKAN_VERTEX_SPIRV, fragmentSpirv)
            .reflection(ShaderReflection.of(
                    new ShaderBinding[] {
                            ShaderBinding.of(0, 0, "u_texture", ShaderBindingType.TEXTURE),
                            ShaderBinding.of(0, 1, "u_sampler", ShaderBindingType.SAMPLER)
                    },
                    new ShaderAttribute[] {
                            ShaderAttribute.of(0, "position", VertexFormat.FLOAT32X2),
                            ShaderAttribute.of(1, "texCoord", VertexFormat.FLOAT32X2),
                            ShaderAttribute.of(2, "color", VertexFormat.UNORM8X4)
                    }))
            .build();
    }

    private final String rendererLabel;
    private final boolean ownsPlatformCallbacks;
    private GraphicsContext graphics;
    private FdxImGuiTextureRegistry textures;
    private RenderPassDescriptor renderPassDescriptor;
    private ShaderModule linearShader;
    private ShaderModule nearestShader;
    private RenderPipeline linearPipeline;
    private RenderPipeline nearestPipeline;
    private Buffer vertexBuffer;
    private Buffer indexBuffer;
    private ByteBuffer vertexBytes;
    private ByteBuffer indexBytes;
    private ByteBuffer textureUploadBytes;
    private ByteBuffer alphaSourceBytes;
    private final int[] clipRect = new int[4];
    private boolean supportsBaseVertex;
    private boolean initialized;
    private boolean disposed;

    public FdxImGuiGraphicsRenderer() {
        this("imgui", true);
    }

    private FdxImGuiGraphicsRenderer(String rendererLabel, boolean ownsPlatformCallbacks) {
        this.rendererLabel = rendererLabel != null ? rendererLabel : "imgui";
        this.ownsPlatformCallbacks = ownsPlatformCallbacks;
    }

    @Override
    public final void initialize(FdxImGuiRendererContext context) {
        if (context == null) {
            throw new FdxException("FdxImGuiRendererContext cannot be null");
        }
        graphics = context.graphics();
        textures = context.textures();
        String providerId = graphics.providerId().value();
        if (!supportsProvider(providerId)) {
            throw new FdxException(rendererLabel + " does not support graphics provider: " + providerId);
        }
        supportsBaseVertex = supportsBaseVertex(providerId);
        linearShader = graphics.device().createShaderModule(IMGUI_LINEAR_SHADER.descriptorForProvider(providerId));
        nearestShader = graphics.device().createShaderModule(IMGUI_NEAREST_SHADER.descriptorForProvider(providerId));
        linearPipeline = createPipeline(linearShader, rendererLabel + " linear");
        nearestPipeline = createPipeline(nearestShader, rendererLabel + " nearest");
        renderPassDescriptor = new RenderPassDescriptor().label(rendererLabel + " pass");
        setBackendFlags();
        ensureBuffers(DEFAULT_VERTEX_BYTES, DEFAULT_INDEX_BYTES);
        if (ownsPlatformCallbacks) {
            ImGuiDrawCallbacks.InstallStandardCallbacks(ImGui.GetPlatformIO());
        }
        initialized = true;
    }

    private RenderPipeline createPipeline(ShaderModule shader, String label) {
        return graphics.device().createRenderPipeline(RenderPipelineDescriptor
                .shader(shader, graphics.surfaceFormat())
                .label(label)
                .primitiveTopology(PrimitiveTopology.TRIANGLE_LIST)
                .vertexEntryPoint("vertexMain")
                .fragmentEntryPoint("fragmentMain")
                .vertexLayout(IMGUI_VERTEX_LAYOUT)
                .sampledTextureCount(1)
                .depthTestEnabled(false)
                .depthWriteEnabled(false));
    }

    @Override
    public final void render(ImDrawData drawData) {
        ensureReady();
        if (drawData == null || drawData.native_isNULL()) {
            return;
        }
        updateTextures(drawData.get_Textures());
        if (!drawData.get_Valid() || drawData.get_CmdListsCount() <= 0 || drawData.get_TotalVtxCount() <= 0
                || drawData.get_TotalIdxCount() <= 0) {
            return;
        }
        ImVec2 displaySize = drawData.get_DisplaySize();
        ImVec2 framebufferScale = drawData.get_FramebufferScale();
        int framebufferWidth = (int) (displaySize.get_x() * framebufferScale.get_x());
        int framebufferHeight = (int) (displaySize.get_y() * framebufferScale.get_y());
        if (framebufferWidth <= 0 || framebufferHeight <= 0) {
            return;
        }
        ensureBuffers(drawData.get_TotalVtxCount() * IM_DRAW_VERT_SIZE,
                drawData.get_TotalIdxCount() * IM_DRAW_IDX_SIZE);
        uploadBuffers(drawData);

        GraphicsFrame frame = graphics.currentFrame();
        RenderPass pass = frame.commandEncoder().beginRenderPass(renderPassDescriptor
                .colorAttachment(frame.colorAttachment())
                .colorLoadOp(LoadOp.load())
                .colorStoreOp(StoreOp.store()));
        setupRenderState(pass, linearPipeline, framebufferWidth, framebufferHeight);
        renderDrawLists(drawData, pass, framebufferWidth, framebufferHeight);
        pass.end();
    }

    private static boolean supportsProvider(String providerId) {
        return "gl".equals(providerId)
                || "gles".equals(providerId)
                || "webgl".equals(providerId)
                || "wgpu".equals(providerId)
                || "vulkan".equals(providerId);
    }

    private static boolean supportsBaseVertex(String providerId) {
        return "gl".equals(providerId)
                || "wgpu".equals(providerId)
                || "vulkan".equals(providerId);
    }

    @Override
    public final FdxImGuiRenderer createViewportRenderer() {
        return new FdxImGuiGraphicsRenderer(rendererLabel, false);
    }

    private void renderDrawLists(ImDrawData drawData, RenderPass pass, int framebufferWidth, int framebufferHeight) {
        ImVec2 displayPos = drawData.get_DisplayPos();
        ImVec2 clipScale = drawData.get_FramebufferScale();
        ImVectorImDrawListPtr commandLists = drawData.get_CmdLists();
        int vertexStart = 0;
        int indexStart = 0;
        for (int listIndex = 0; listIndex < drawData.get_CmdListsCount(); listIndex++) {
            ImDrawList commandList = commandLists.getData(listIndex);
            ImVectorImDrawCmd commands = commandList.get_CmdBuffer();
            for (int commandIndex = 0; commandIndex < commands.size(); commandIndex++) {
                ImDrawCmd command = commands.getData(commandIndex);
                ImGuiDrawCallbackType callbackType = ImGuiDrawCallbacks.GetType(command);
                if (callbackType != ImGuiDrawCallbackType.None) {
                    if (callbackType == ImGuiDrawCallbackType.ResetRenderState) {
                        setupRenderState(pass, linearPipeline, framebufferWidth, framebufferHeight);
                    }
                    else if (callbackType == ImGuiDrawCallbackType.SetSamplerLinear) {
                        setupPipelineState(pass, linearPipeline);
                    }
                    else if (callbackType == ImGuiDrawCallbackType.SetSamplerNearest) {
                        setupPipelineState(pass, nearestPipeline);
                    }
                    else if (callbackType == ImGuiDrawCallbackType.User) {
                        ImGuiDrawCallbacks.InvokeUserCallback(commandList, command);
                    }
                    continue;
                }
                if (command.get_ElemCount() <= 0) {
                    continue;
                }
                setClipRect(command.get_ClipRect(), displayPos, clipScale, framebufferWidth, framebufferHeight);
                if (clipRect[2] <= 0 || clipRect[3] <= 0) {
                    continue;
                }
                pass.setScissor(clipRect[0], clipRect[1], clipRect[2], clipRect[3]);
                long textureId = command.GetTexID().Get();
                pass.setTexture(0, textures.texture(textureId));
                int baseVertex = supportsBaseVertex ? vertexStart + command.get_VtxOffset() : 0;
                if (!supportsBaseVertex && command.get_VtxOffset() != 0) {
                    throw new FdxException(rendererLabel + " provider does not support ImGui vertex offsets");
                }
                pass.drawIndexed(command.get_ElemCount(), 1, indexStart + command.get_IdxOffset(), baseVertex, 0);
            }
            vertexStart += commandList.get_VtxBuffer().size();
            indexStart += commandList.get_IdxBuffer().size();
        }
    }

    private void setupRenderState(RenderPass pass, RenderPipeline pipeline, int framebufferWidth,
            int framebufferHeight) {
        setupPipelineState(pass, pipeline);
        pass.setViewport(0, 0, framebufferWidth, framebufferHeight);
    }

    private void setupPipelineState(RenderPass pass, RenderPipeline pipeline) {
        pass.setPipeline(pipeline);
        pass.setVertexBuffer(vertexBuffer);
        pass.setIndexBuffer(indexBuffer);
    }

    private void setClipRect(ImVec4 clip, ImVec2 displayPos, ImVec2 clipScale, int framebufferWidth,
            int framebufferHeight) {
        int clipX = Math.max(0, (int) ((clip.get_x() - displayPos.get_x()) * clipScale.get_x()));
        int clipY = Math.max(0, (int) ((clip.get_y() - displayPos.get_y()) * clipScale.get_y()));
        int clipZ = Math.min(framebufferWidth, (int) ((clip.get_z() - displayPos.get_x()) * clipScale.get_x()));
        int clipW = Math.min(framebufferHeight, (int) ((clip.get_w() - displayPos.get_y()) * clipScale.get_y()));
        int width = clipZ - clipX;
        int height = clipW - clipY;
        clipY = framebufferHeight - clipW;
        clipRect[0] = clipX;
        clipRect[1] = clipY;
        clipRect[2] = width;
        clipRect[3] = height;
    }

    private void uploadBuffers(ImDrawData drawData) {
        ImVec2 displayPos = drawData.get_DisplayPos();
        ImVec2 displaySize = drawData.get_DisplaySize();
        int vertexSize = drawData.get_TotalVtxCount() * IM_DRAW_VERT_SIZE;
        int indexSize = drawData.get_TotalIdxCount() * IM_DRAW_IDX_SIZE;
        vertexBytes.clear();
        indexBytes.clear();
        ImVectorImDrawListPtr commandLists = drawData.get_CmdLists();
        int vertexOffset = 0;
        int indexOffset = 0;
        int vertexStart = 0;
        for (int listIndex = 0; listIndex < drawData.get_CmdListsCount(); listIndex++) {
            ImDrawList commandList = commandLists.getData(listIndex);
            ImVectorImDrawVert vertices = commandList.get_VtxBuffer();
            ImVectorImDrawIdx indices = commandList.get_IdxBuffer();
            int listVertexSize = vertices.size() * IM_DRAW_VERT_SIZE;
            int listIndexSize = indices.size() * IM_DRAW_IDX_SIZE;
            NativeUtils.copyToByteBuffer(vertices.get_Data(), vertexBytes, vertexOffset, listVertexSize);
            NativeUtils.copyToByteBuffer(indices.get_Data(), indexBytes, indexOffset, listIndexSize);
            transformVertexPositions(vertexBytes, vertexOffset, vertices.size(), displayPos, displaySize);
            if (!supportsBaseVertex && vertexStart != 0) {
                adjustIndices(indexBytes, indexOffset, indices.size(), vertexStart);
            }
            vertexOffset += listVertexSize;
            indexOffset += listIndexSize;
            vertexStart += vertices.size();
        }
        vertexBytes.position(0).limit(vertexSize);
        indexBytes.position(0).limit(indexSize);
        graphics.device().writeBuffer(vertexBuffer, vertexBytes);
        graphics.device().writeBuffer(indexBuffer, indexBytes);
    }

    private void transformVertexPositions(ByteBuffer bytes, int byteOffset, int vertexCount, ImVec2 displayPos,
            ImVec2 displaySize) {
        float scaleX = displaySize.get_x() != 0.0f ? 2.0f / displaySize.get_x() : 0.0f;
        float scaleY = displaySize.get_y() != 0.0f ? 2.0f / displaySize.get_y() : 0.0f;
        float offsetX = displayPos.get_x();
        float offsetY = displayPos.get_y();
        for (int i = 0; i < vertexCount; i++) {
            int offset = byteOffset + i * IM_DRAW_VERT_SIZE;
            float x = bytes.getFloat(offset);
            float y = bytes.getFloat(offset + 4);
            bytes.putFloat(offset, (x - offsetX) * scaleX - 1.0f);
            bytes.putFloat(offset + 4, 1.0f - (y - offsetY) * scaleY);
        }
    }

    private void adjustIndices(ByteBuffer bytes, int byteOffset, int indexCount, int vertexOffset) {
        for (int i = 0; i < indexCount; i++) {
            int offset = byteOffset + i * IM_DRAW_IDX_SIZE;
            int index = bytes.getShort(offset) & 0xffff;
            int adjusted = index + vertexOffset;
            if (adjusted > 0xffff) {
                throw new FdxException(rendererLabel
                        + " cannot draw more than 65535 vertices without base-vertex support");
            }
            bytes.putShort(offset, (short) adjusted);
        }
    }

    private void updateTextures(ImVectorImTextureDataPtr textureData) {
        if (textureData == null || textureData.native_isNULL()) {
            return;
        }
        for (int i = 0; i < textureData.size(); i++) {
            ImTextureData texture = textureData.getData(i);
            ImTextureStatus status = texture.get_Status();
            if (status == ImTextureStatus.WantCreate) {
                createTexture(texture);
            }
            else if (status == ImTextureStatus.WantUpdates) {
                updateTexture(texture);
            }
            else if (status == ImTextureStatus.WantDestroy && texture.get_UnusedFrames() > 0) {
                destroyTexture(texture);
            }
        }
    }

    private void createTexture(ImTextureData textureData) {
        Texture texture = graphics.device().createTexture(TextureDescriptor
                .rgba8("imgui texture " + textureData.get_UniqueID(), textureData.get_Width(),
                        textureData.get_Height()));
        ByteBuffer pixels = texturePixels(textureData);
        graphics.device().writeTexture(texture, pixels);
        long textureId = textures.registerOwned(texture);
        textureData.SetTexID(ImTemp.ImTextureIDRef_1(textureId));
        textureData.SetStatus(ImTextureStatus.OK);
    }

    private void updateTexture(ImTextureData textureData) {
        long textureId = textureData.GetTexID().Get();
        Texture texture = textures.texture(textureId);
        ByteBuffer pixels = texturePixels(textureData);
        graphics.device().writeTexture(texture, pixels);
        textureData.SetStatus(ImTextureStatus.OK);
    }

    private void destroyTexture(ImTextureData textureData) {
        long textureId = textureData.GetTexID().Get();
        if (textureId != 0L) {
            textures.removeAndDisposeOwned(textureId);
        }
        textureData.SetTexID(ImTemp.ImTextureIDRef_1(0L));
        textureData.SetStatus(ImTextureStatus.Destroyed);
    }

    private ByteBuffer texturePixels(ImTextureData textureData) {
        if (textureData.get_Format() == ImTextureFormat.RGBA32) {
            int size = textureData.GetSizeInBytes();
            textureUploadBytes = ensureBuffer(textureUploadBytes, size);
            textureUploadBytes.clear();
            NativeUtils.copyToByteBuffer(textureData.GetPixels(), textureUploadBytes, 0, size);
            textureUploadBytes.position(0).limit(size);
            return textureUploadBytes;
        }
        if (textureData.get_Format() == ImTextureFormat.Alpha8) {
            return alpha8ToRgba(textureData.GetPixels(), textureData.GetSizeInBytes());
        }
        throw new FdxException("Unsupported ImGui texture format: " + textureData.get_Format());
    }

    private ByteBuffer alpha8ToRgba(NativeObject pixels, int size) {
        alphaSourceBytes = ensureBuffer(alphaSourceBytes, size);
        alphaSourceBytes.clear();
        NativeUtils.copyToByteBuffer(pixels, alphaSourceBytes, 0, size);
        alphaSourceBytes.position(0).limit(size);
        textureUploadBytes = ensureBuffer(textureUploadBytes, size * 4);
        textureUploadBytes.clear();
        for (int i = 0; i < size; i++) {
            byte alpha = alphaSourceBytes.get(i);
            textureUploadBytes.put((byte) 0xff);
            textureUploadBytes.put((byte) 0xff);
            textureUploadBytes.put((byte) 0xff);
            textureUploadBytes.put(alpha);
        }
        textureUploadBytes.flip();
        return textureUploadBytes;
    }

    private void setBackendFlags() {
        ImGuiIO io = ImGui.GetIO();
        int flags = io.get_BackendFlags().getValue() | ImGuiBackendFlags.RendererHasTextures.getValue();
        if (supportsBaseVertex) {
            flags |= ImGuiBackendFlags.RendererHasVtxOffset.getValue();
        }
        io.set_BackendFlags(ImGuiBackendFlags.CUSTOM.setValue(flags));
    }

    private void ensureBuffers(int vertexByteCount, int indexByteCount) {
        int actualVertexBytes = Math.max(vertexByteCount, DEFAULT_VERTEX_BYTES);
        int actualIndexBytes = Math.max(indexByteCount, DEFAULT_INDEX_BYTES);
        if (vertexBuffer == null || vertexBuffer.size() < actualVertexBytes) {
            if (vertexBuffer != null && !vertexBuffer.isDisposed()) {
                vertexBuffer.dispose();
            }
            vertexBuffer = graphics.device().createBuffer(BufferDescriptor.vertex(rendererLabel + " vertices",
                    actualVertexBytes));
            vertexBytes = null;
        }
        if (indexBuffer == null || indexBuffer.size() < actualIndexBytes) {
            if (indexBuffer != null && !indexBuffer.isDisposed()) {
                indexBuffer.dispose();
            }
            indexBuffer = graphics.device().createBuffer(BufferDescriptor.index(rendererLabel + " indices",
                    actualIndexBytes));
            indexBytes = null;
        }
        vertexBytes = ensureBuffer(vertexBytes, actualVertexBytes);
        indexBytes = ensureBuffer(indexBytes, actualIndexBytes);
    }

    private ByteBuffer ensureBuffer(ByteBuffer buffer, int byteCount) {
        if (buffer != null && buffer.capacity() >= byteCount) {
            return buffer.clear().order(ByteOrder.LITTLE_ENDIAN);
        }
        return ByteBuffer.allocateDirect(byteCount).order(ByteOrder.LITTLE_ENDIAN);
    }

    private void ensureReady() {
        if (disposed) {
            throw new FdxException(rendererLabel + " has been disposed");
        }
        if (!initialized) {
            throw new FdxException(rendererLabel + " has not been initialized");
        }
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        if (ownsPlatformCallbacks && initialized) {
            ImGuiDrawCallbacks.ClearStandardCallbacks(ImGui.GetPlatformIO());
        }
        if (vertexBuffer != null && !vertexBuffer.isDisposed()) {
            vertexBuffer.dispose();
        }
        if (indexBuffer != null && !indexBuffer.isDisposed()) {
            indexBuffer.dispose();
        }
        if (linearPipeline != null && !linearPipeline.isDisposed()) {
            linearPipeline.dispose();
        }
        if (nearestPipeline != null && !nearestPipeline.isDisposed()) {
            nearestPipeline.dispose();
        }
        if (linearShader != null && !linearShader.isDisposed()) {
            linearShader.dispose();
        }
        if (nearestShader != null && !nearestShader.isDisposed()) {
            nearestShader.dispose();
        }
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }
}
