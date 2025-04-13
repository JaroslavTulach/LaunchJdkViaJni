package org.apidesign.demo.launchjdkviajni;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.constant.CConstant;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.function.CFunction.Transition;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CPointerTo;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.nativeimage.c.type.CLongPointer;
import org.graalvm.nativeimage.c.type.VoidPointer;
import org.graalvm.word.PointerBase;

@CContext(WindowsDirectives.class)
public class WinBase {

    public static final int MAX_PATH = 260;
    public static final int UNLEN = 256;

    /**
     * Windows opaque Handle type
     */
    public interface HANDLE extends PointerBase {
    }

    @CConstant
    public static native HANDLE INVALID_HANDLE_VALUE();

    @CPointerTo(nameOfCType = "HANDLE")
    public interface LPHANDLE extends PointerBase {
        HANDLE read();
    }

    /**
     * Windows Module Handle type
     */
    public interface HMODULE extends PointerBase {
    }

    @CPointerTo(nameOfCType = "HMODULE")
    public interface HMODULEPointer extends PointerBase {
        public HMODULE read();

        public void write(HMODULE value);
    }

    /**
     * Contains a 64-bit value representing the number of 100-nanosecond intervals since January 1,
     * 1601 (UTC).
     */
    @CStruct
    public interface FILETIME extends PointerBase {
        @CField
        int dwLowDateTime();

        @CField
        int dwHighDateTime();
    }

    /**
     * GetLastError - Return additional error information
     */
    @CFunction(transition = Transition.NO_TRANSITION)
    public static native int GetLastError();

    @CConstant
    public static native int ERROR_TIMEOUT();

    @CConstant
    public static native int ERROR_SUCCESS();

    /**
     * QueryPerformance Counter - used for elapsed time
     */

    @CFunction(transition = Transition.NO_TRANSITION)
    public static native void QueryPerformanceCounter(CLongPointer counter);

    /**
     * QueryPerformance Frequency - used for elapsed time
     */
    @CFunction(transition = Transition.NO_TRANSITION)
    public static native void QueryPerformanceFrequency(CLongPointer counter);

    /**
     * CloseHandle
     */
    @CFunction(transition = Transition.NO_TRANSITION)
    public static native int CloseHandle(HANDLE hFile);

    @CFunction(transition = Transition.NO_TRANSITION)
    public static native int DuplicateHandle(HANDLE hSourceProcessHandle, HANDLE hSourceHandle, HANDLE hTargetProcessHandle, LPHANDLE lpTargetHandle, int dwDesiredAccess, boolean bInheritHandle,
                    int dwOptions);

    @CFunction(transition = Transition.NO_TRANSITION)
    public static native WinBase.HANDLE CreateSemaphoreA(VoidPointer lpSemaphoreAttributes, int lInitialCount, int lMaximumCount, VoidPointer lpName);

}