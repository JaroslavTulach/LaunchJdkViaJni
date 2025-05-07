package org.apidesign.demo.launchjdkviajni;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.constant.CConstant;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.function.CFunction.Transition;
import org.graalvm.nativeimage.c.function.CLibrary;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.word.PointerBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CContext(Dlfcn.Direct.class)
@CLibrary("dl")
public class Dlfcn {

    @CConstant
    public static native int RTLD_LAZY();

    @CConstant
    public static native int RTLD_NOW();

    @CConstant
    public static native int RTLD_GLOBAL();

    @CConstant
    public static native int RTLD_LOCAL();

    @CConstant
    public static native PointerBase RTLD_DEFAULT();

    @CFunction
    public static native PointerBase dlopen(CCharPointer file, int mode);

    @CFunction
    public static native int dlclose(PointerBase handle);

    @CFunction(transition = Transition.NO_TRANSITION)
    public static native <T extends PointerBase> T dlsym(PointerBase handle, CCharPointer name);

    @CFunction
    public static native CCharPointer dlerror();

    public static final class Direct implements CContext.Directives {

        private static final String[] commonLibs = new String[]{
            "<dlfcn.h>",};

        @Override
        public boolean isInConfiguration() {
            return Platform.includedIn(Platform.LINUX.class) || Platform.includedIn(Platform.DARWIN.class);
        }

        @Override
        public List<String> getHeaderFiles() {
            List<String> result = new ArrayList<>(Arrays.asList(commonLibs));
            return result;
        }
    }

}
