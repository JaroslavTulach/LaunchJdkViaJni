package org.apidesign.demo.launchjdkviajni;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.c.CContext;

@Platforms(Platform.WINDOWS.class)
public class WindowsDirectives implements CContext.Directives {

    private static final String[] windowsLibs = new String[]{
                    "<windows.h>",
                    "<winsock.h>",
                    "<process.h>",
                    "<stdio.h>",
                    "<stdlib.h>",
                    "<string.h>",
                    "<io.h>",
                    "<math.h>"
    };

    @Override
    public boolean isInConfiguration() {
        return Platform.includedIn(Platform.WINDOWS.class);
    }

    @Override
    public List<String> getHeaderFiles() {
        if (Platform.includedIn(Platform.WINDOWS.class)) {
            return new ArrayList<>(Arrays.asList(windowsLibs));
        } else {
            throw new IllegalStateException("Unsupported OS");
        }
    }

    @Override
    public List<String> getMacroDefinitions() {
        return Arrays.asList("_WIN64");
    }
}