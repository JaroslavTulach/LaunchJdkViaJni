package org.apidesign.demo.launchjdkviajni;

import java.io.File;

import org.apidesign.demo.launchjdkviajni.JvmInit.JNICreateJavaVMPointer;
import org.apidesign.demo.launchjdkviajni.JvmInit.JavaVMInitArgs;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.c.type.CTypeConversion;

public final class LaunchJdkViaJni {
    private LaunchJdkViaJni() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: <name_of_class_to_load>");
            System.exit(1);
        }

        var javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null) {
            System.err.println("Specify JAVA_HOME environment variable");
            System.exit(2);
        }

        var jvmLib = findDynamicLibrary(javaHome);
        if (!jvmLib.exists()) {
            System.err.println("Cannot find " + jvmLib + " in JAVA_HOME directory");
            System.exit(3);
        }

        var jvmArgs = StackValue.get(JavaVMInitArgs.class);
        jvmArgs.nOptions(0);
        var options = StackValue.get(10, JNI.JNIJavaVMOption.class);
        jvmArgs.options(options);
        System.err.println("empty: " + jvmArgs.version());
        jvmArgs.version(JNI.JNI_VERSION_1_1());
        System.err.println("version is " + jvmArgs.version());

        int resInitArgs = JvmInit.JNI_GetDefaultJavaVMInitArgs(jvmArgs);
        if (resInitArgs != 0) {
            System.err.println("result: " + resInitArgs);
            System.err.println("JVM wants to support version " + jvmArgs.version());
        }

        jvmArgs.nOptions(0);
        jvmArgs.ignoreUnrecognized(false);

        var jvmPtr = StackValue.get(JvmInit.JNIJavaVMPointer.class);
        var envPtr = StackValue.get(JNI.JNIEnvPointer.class);

        try (
            var libPath = CTypeConversion.toCString(jvmLib.getPath());
            var createJvm = CTypeConversion.toCString("JNI_CreateJavaVM")
        ) {
            var jvmSo = Dll.LoadLibraryA(libPath.get());
            System.err.println("jvmdll: " + jvmSo.rawValue());
            System.err.println("    nn: " + jvmSo.isNonNull());
            JNICreateJavaVMPointer cStringCreateJvm = Dll.GetProcAddress(jvmSo, createJvm.get());
            System.err.println("symbol name: " + cStringCreateJvm.rawValue());
            System.err.println("    nn: " + cStringCreateJvm.isNonNull());
            System.err.println("jvmPtr: " + jvmPtr.rawValue());
            System.err.println("envPtr: " + envPtr.rawValue());
            System.err.println("jvmArg: " + jvmArgs.rawValue());
            int res = cStringCreateJvm.call(jvmPtr, envPtr, jvmArgs);
            System.err.println("result " + res);
        }

        System.err.println("       ignore  " + jvmArgs.ignoreUnrecognized());
        System.err.println("       nOption:" + jvmArgs.nOptions());
        System.err.println("       options:" + jvmArgs.options().rawValue());

        var env = envPtr.readJNIEnv();
        System.err.println("env: " + env.rawValue());

        var findClassFn = env.getFunctions().getFindClass();

        try (var nameHolder = CTypeConversion.toCString(args[0])) {
            var clazz = findClassFn.call(env, nameHolder.get());

            System.err.println("clazz: " + clazz.rawValue());
        }

        /*
        var envPtr = StackValue.get(JNI.JNIEnvPointer.class);
        var opt = StackValue.get(JNI.JNIJavaVMOption.class);
        JNI.JNIJavaVMPointer jvmPtr = WordFactory.pointer(jvm.rawValue());
        Pointer envPtr2 = WordFactory.pointer(envPtr.rawValue());
        JNI.JNIJavaVMOptionPointer optPtr = WordFactory.pointer(opt.rawValue());

        try (var optHolder = CTypeConversion.toCString("-Djava.class.path=/home/devel/NetBeansProjects/enso/LaunchJdkViaJni/")) {
            opt.setOptionString(optHolder.get());

            // var jvmPointer = WordFactory.<JavaVM.JNIJavaVMPointer>pointer(jvm.rawValue());
            System.err.println("Before create");
            JNI.JNI_CreateJavaVM(jvmPtr, envPtr2, optPtr);
            System.err.println("After create");

            var fn = jvm.getFunctions();
            System.out.println("opt: " + opt.rawValue());
            System.out.println("jvm: " + fn.getGetEnv().rawValue());
            System.out.println("env: " + envPtr.rawValue());
        }
        */
    }

    private static File findDynamicLibrary(String javaHome) {
        var libName = "libjvm.so";
        if (System.getProperty("os.name").contains("Mac")) {
            libName = "libjvm.dylib";
        }
        if (System.getProperty("os.name").contains("Windows")) {
            return new File(new File(new File(new File(javaHome), "bin"), "server"), "jvm.dll");
        }

        return new File(new File(new File(new File(javaHome), "lib"), "server"), libName);
    }
}
