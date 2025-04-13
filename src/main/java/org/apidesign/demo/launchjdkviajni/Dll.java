package org.apidesign.demo.launchjdkviajni;

import org.apidesign.demo.launchjdkviajni.WinBase.HMODULE;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunction;
import static org.graalvm.nativeimage.c.function.CFunction.Transition.NO_TRANSITION;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.word.PointerBase;

@CContext(WindowsDirectives.class)
final class Dll {
    /** Loads the specified module into the address space of the calling process. */
    @CFunction(transition = NO_TRANSITION)
    public static native HMODULE LoadLibraryA(CCharPointer lpLibFileName);

    @CFunction(transition = NO_TRANSITION)
    public static native <T extends PointerBase> T GetProcAddress(HMODULE hModule, CCharPointer lpProcName);

}
