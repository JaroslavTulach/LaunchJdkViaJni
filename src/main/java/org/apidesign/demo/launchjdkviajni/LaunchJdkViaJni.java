package org.apidesign.demo.launchjdkviajni;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apidesign.demo.launchjdkviajni.JvmInit.JNICreateJavaVMPointer;
import org.apidesign.demo.launchjdkviajni.JvmInit.JavaVMInitArgs;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.WordFactory;

public final class LaunchJdkViaJni {
    private LaunchJdkViaJni() {
    }
  public static void listS() throws IOException {
      var sw = Path.of("s:\\windows");
      var real = sw.toRealPath();
      System.err.println("real path is : " + real + " on " + System.getProperty("java.vm.version"));
      for (var f : Files.list(real).toList()) {
          System.err.println("  there is " + f.toRealPath());
      }
  }
    public static void main(String[] args) throws Exception {
        listS();

        if (args.length == 0) {
            System.err.println("Usage: <name_of_class_to_load> additional args");
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

        executeMain(env, args[0], args);

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

    private static void executeMain(JNI.JNIEnv e, String classNameWithSlashes, String... args) {
        try (
            var className = CTypeConversion.toCString(classNameWithSlashes);
            var mainName = CTypeConversion.toCString("main");
            var stringName = CTypeConversion.toCString("java/lang/String");
            var mainSig = CTypeConversion.toCString("([Ljava/lang/String;)V");
        ) {
            var fn = e.getFunctions();
            var mainClazz = fn.getFindClass().call(e, className.get());
            assert mainClazz.isNonNull() : "Class not found " + classNameWithSlashes;
            var mainMethod = fn.getGetStaticMethodID().call(e, mainClazz, mainName.get(), mainSig.get());
            assert mainMethod.isNonNull() : "main method found in " + classNameWithSlashes;
            var stringClazz = fn.getFindClass().call(e, stringName.get());
            var argsCopy
                    = fn.getNewObjectArray().call(e, args.length, stringClazz, WordFactory.nullPointer());

            for (var i = 0; i < args.length; i++) {
                try (var ithArg = CTypeConversion.toCString(args[i]);) {
                    var str = fn.getNewStringUTF().call(e, ithArg.get());
                    fn.getSetObjectArrayElement().call(e, argsCopy, i, str);
                }
            }
            var arg = StackValue.get(JNI.JValue.class);
            arg.setJObject(argsCopy);
            fn.getCallStaticVoidMethodA().call(e, mainClazz, mainMethod, arg);
        }
    }
}
