package imgui;

import com.github.xpenatan.jParser.loader.JParserLibraryLoader;
import com.github.xpenatan.jParser.loader.JParserLibraryLoaderListener;
import com.github.xpenatan.jparser.runtime.RuntimeLoader;

/**
 * @author xpenatan
 */
public class ImGuiLoader {

    /*[-JNI;-NATIVE]
        #include "ImGuiCustom.h"
    */

    /*[-FFM;-NATIVE]
        #include "ImGuiCustom.h"
    */

    /*[-TEAVM_C;-NATIVE]
        #include "ImGuiCustom.h"
    */

    public static void init(JParserLibraryLoaderListener listener) {
        RuntimeLoader.init(new JParserLibraryLoaderListener() {
            @Override
            public void onLoad(boolean idl_isSuccess, Throwable idl_t) {
                if(idl_isSuccess) {
                    JParserLibraryLoader.load("imgui", listener);
                }
                else {
                    listener.onLoad(false, idl_t);
                }
            }
        });
    }
}
