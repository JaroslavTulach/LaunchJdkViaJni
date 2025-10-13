package org.apidesign.demo.launchjdkviajni;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.graalvm.nativeimage.ImageInfo;

public final class DumpDisk {
  private DumpDisk() {
  }

  public static void dumpC() throws IOException {
      var aot = ImageInfo.inImageRuntimeCode();
      System.err.println("dumpC, AOT: " + aot);
      var sw = Path.of("c:\\");
      var real = sw.toRealPath();
      System.err.println("real path is : " + real + " on " + System.getProperty("java.vm.version") +  " at " + System.getProperty("java.home"));
      for (var f : Files.list(real).toList()) {
          System.err.println("  there is " + f.toRealPath());
      }
  }

  public static void main(String... args) throws Exception {
     dumpC();
     System.err.println("test invoked with " + Arrays.toString(args));
  }
}
