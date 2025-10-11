
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class Main {
  private static void listS() throws IOException {
      var sw = Path.of("s:\\windows");
      var real = sw.toRealPath();
      System.err.println("real path is : " + real + " on " + System.getProperty("java.vm.version"));
      for (var f : Files.list(real).toList()) {
          System.err.println("  there is " + f.toRealPath());
      }
  }

  public static void main(String... args) throws Exception {
     System.err.println("Running with " + System.getProperty("java.version") + " at " + System.getProperty("java.home"));
     listS();
     System.err.println("test invoked with " + Arrays.toString(args));
  }
}
