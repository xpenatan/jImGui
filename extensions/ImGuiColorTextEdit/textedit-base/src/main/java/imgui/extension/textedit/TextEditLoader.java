package imgui.extension.textedit;

import com.github.xpenatan.jParser.loader.JParserLibraryLoader;
import com.github.xpenatan.jParser.loader.JParserLibraryLoaderListener;

/**
 * @author xpenatan
 */
public class TextEditLoader {

    /*[-JNI;-NATIVE]
        #include "TextEditCustom.h"
    */

    /*[-FFM;-NATIVE]
        #include "TextEditCustom.h"
    */

    /*[-TEAVM_C;-NATIVE]
        #include "TextEditCustom.h"
    */

    public static void init(JParserLibraryLoaderListener listener) {
        JParserLibraryLoader.load("textedit", listener);
    }
}
