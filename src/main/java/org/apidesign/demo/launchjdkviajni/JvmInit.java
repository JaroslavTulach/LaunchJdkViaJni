package org.apidesign.demo.launchjdkviajni;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CPointerTo;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.word.Pointer;
import org.graalvm.word.PointerBase;

@CContext(JvmDirectives.class)
final class JvmInit {
    private JvmInit() {
    }

    /*
typedef struct JavaVMInitArgs {
    jint version;

    jint nOptions;
    JavaVMOption *options;
    jboolean ignoreUnrecognized;
} JavaVMInitArgs;    
    */
    @CStruct("JavaVMInitArgs")
    interface JavaVMInitArgs extends PointerBase {
        @CField
        int version();
        @CField
        void version(int v);

        @CField
        int nOptions();
        @CField
        void nOptions(int n);

        @CField
        Pointer options();

        @CField
        boolean ignoreUnrecognized();
        @CField
        void ignoreUnrecognized(boolean v);
    }

    @CPointerTo(JavaVMInitArgs.class)
    interface JavaVMInitArgsPointer extends PointerBase {
        JavaVMInitArgs read();
    }

    /*
    _JNI_IMPORT_OR_EXPORT_ jint JNICALL
    JNI_GetDefaultJavaVMInitArgs(void *args);
    */
    @CFunction
    static native int JNI_GetDefaultJavaVMInitArgs(JavaVMInitArgs vmargs);
    

    /*
    _JNI_IMPORT_OR_EXPORT_ jint JNICALL
    JNI_CreateJavaVM(JavaVM **pvm, void **penv, void *args);
    */
//    @CFunction
//    static native int JNI_CreateJavaVM(JNIJavaVM jvm, JNI.JNIEnvPointer env, JavaVMInitArgs vmArgs);

    public interface JNICreateJavaVMPointer extends CFunctionPointer {
        @InvokeCFunctionPointer
        int call(JNIJavaVM jvmptr, JNI.JNIEnvPointer env, JavaVMInitArgs args);
    }

    @CStruct(value = "JavaVM_", addStructKeyword = true)
    public interface JNIJavaVM extends PointerBase {

        @CField(value = "functions")
        public JNI.JNIInvokeInterface getFunctions();

        @CField(value = "functions")
        public void setFunctions(JNI.JNIInvokeInterface fns);
    }
    /*
    @CPointerTo(JNIJavaVM.class)
    public interface JNIJavaVMPointer extends PointerBase {

        JNIJavaVM read();

        JNIJavaVM read(int index);

        void write(JNIJavaVM value);

        void write(int index, JNIJavaVM value);
    }
    */
}
