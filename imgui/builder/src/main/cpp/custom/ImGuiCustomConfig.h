#pragma once

#ifdef _WIN32
    #ifdef IMGUI_EXPORTS
        #define IMGUI_API __declspec(dllexport)
    #else
        #define IMGUI_API __declspec(dllimport)
    #endif
#else
    #define IMGUI_API __attribute__((visibility("default")))
#endif