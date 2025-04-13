package org.apidesign.demo.launchjdkviajni;

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

        int res = JvmInit.JNI_CreateJavaVM(javaVM, envPtr, jvmArgs);
        System.err.println("result " + res);

        
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
