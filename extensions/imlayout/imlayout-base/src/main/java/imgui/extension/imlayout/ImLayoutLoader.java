package imgui.extension.imlayout;

import com.github.xpenatan.jParser.loader.JParserLibraryLoader;
import com.github.xpenatan.jParser.loader.JParserLibraryLoaderListener;

/**
 * @author xpenatan
 */
public class ImLayoutLoader {

    /*[-JNI;-NATIVE]
        #include "ImLayoutCustom.h"
    */

    /*[-FFM;-NATIVE]
        #include "ImLayoutCustom.h"
    */

    /*[-TEAVM_C;-NATIVE]
        #include "ImLayoutCustom.h"
    */

    public static void init(JParserLibraryLoaderListener listener) {
        JParserLibraryLoader.load("imlayout", listener);
    }
}
