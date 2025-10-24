package org.apidesign.demo.launchjdkviajni;

import java.io.File;
import org.enso.jvm.channel.JVM;

public final class LaunchJdkViaJni {
    private LaunchJdkViaJni() {
    }
    public static void main(String[] args) throws Exception {
        DumpDisk.dumpC();

        if (args.length == 0) {
            System.err.println("Usage: <name_of_class_to_load> additional args");
            var mainClass = "org/apidesign/demo/launchjdkviajni/DumpDisk";
            System.err.println("Defaulting to " + mainClass + ". Press Enter to go on!");
            System.in.read();
            args = new String[] { mainClass };
        }

        var javaHomeEnv = System.getenv("JAVA_HOME");
        if (javaHomeEnv == null) {
            System.err.println("Specify JAVA_HOME environment variable");
            System.exit(2);
        }
        var javaHome = new File(javaHomeEnv);

        var where = LaunchJdkViaJni.class.getProtectionDomain().getCodeSource().getLocation();
        System.err.println("where: " + where);
        var target = new File(where.toURI()).getParentFile();
        var classes = new File(target, "classes");
        assert classes.isDirectory() : "Found " + classes;
        System.err.println(" file: " + classes);
        
        var jvm = JVM.create(javaHome, "-Djava.class.path=" + classes);
        jvm.executeMain(args[0], args);
        System.err.println("Back in NI");
        DumpDisk.dumpC();
    }
}
