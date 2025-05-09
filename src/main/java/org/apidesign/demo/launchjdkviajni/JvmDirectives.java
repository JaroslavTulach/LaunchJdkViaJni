package org.apidesign.demo.launchjdkviajni;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.graalvm.nativeimage.c.CContext;

final class JvmDirectives implements CContext.Directives {

    private static final String[] INCLUDES = {"jni.h", "jni_md.h"};

    @Override
    public List<String> getOptions() {
        return Arrays.stream(findJNIHeaders()).map(p -> "-I" + p.getParent()).collect(Collectors.toList());
    }

    @Override
    public List<String> getHeaderFiles() {
        return Arrays.stream(findJNIHeaders()).map(p -> '<' + p.toString() + '>').collect(Collectors.toList());
    }

    @Override
    public List<String> getLibraryPaths() {
        var jdk = new File(System.getProperty("java.home"));
        var lib = new File(jdk, "bin");
        var server = new File(lib, "server");
        return List.of(server.getPath());
    }


    private static Path[] findJNIHeaders() {
        Path javaHome = Paths.get(System.getProperty("java.home"));
        Path includeFolder = javaHome.resolve("include");
        if (!Files.exists(includeFolder)) {
            Path parent = javaHome.getParent();
            if (parent != null) {
                javaHome = parent;
            }
        }
        includeFolder = javaHome.resolve("include");
        if (!Files.exists(includeFolder)) {
            throw new IllegalStateException("Cannot find 'include' folder in JDK.");
        }
        Path[] res = new Path[INCLUDES.length];
        try {
            for (int i = 0; i < INCLUDES.length; i++) {
                String include = INCLUDES[i];
                Optional<Path> includeFile = Files.find(includeFolder, 2, (p, attrs) -> include.equals(p.getFileName().toString())).findFirst();
                if (!includeFile.isPresent()) {
                    throw new IllegalStateException("Include: " + res[i] + " does not exist.");
                }
                res[i] = includeFile.get();
            }
            return res;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
