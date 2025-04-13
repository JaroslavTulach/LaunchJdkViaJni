package org.apidesign.demo.launchjdkviajni;

import java.io.File;
import org.apidesign.demo.launchjdkviajni.JvmInit.*;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.UnmanagedMemory;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.Pointer;
import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;

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

        var jvmLib = new File(new File(new File(new File(javaHome), "lib"), "server"), "libjvm.so");
        if (!jvmLib.exists()) {
            System.err.println("Cannot find " + jvmLib + " in JAVA_HOME directory");
            System.exit(3);
        }

        var jvmArgs = StackValue.get(JavaVMInitArgs.class);
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

        var javaVM = StackValue.get(JvmInit.JNIJavaVM.class);
        JNI.JNIEnvPointer envPtr = UnmanagedMemory.calloc(4096);

        try (
            var libPath = CTypeConversion.toCString(jvmLib.getPath());
            var createJvm = CTypeConversion.toCString("JNI_CreateJavaVM")
        ) {
            var jvmSo = Dlfcn.dlopen(libPath.get(), Dlfcn.RTLD_NOW());
            System.err.println("jvm: " + jvmSo.rawValue());
            JNICreateJavaVMPointer cStringCreateJvm = Dlfcn.dlsym(jvmSo, createJvm.get());
            System.err.println("name: " + cStringCreateJvm.rawValue());
            int res = cStringCreateJvm.call(javaVM, envPtr, jvmArgs);
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
}
