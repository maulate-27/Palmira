package works;

import java.io.*;
import java.util.*;

public class FileViewer {

    private static ProcessResult runCommand(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process proc = pb.start();

        StringBuilder out = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                out.append(line).append(System.lineSeparator());
            }
        }

        int exit = proc.waitFor();
        return new ProcessResult(exit, out.toString());
    }

    public static ProcessResult viewFile(File file) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add("cmd.exe");
        cmd.add("/c");  // /c -> executa e fecha
        cmd.add("type");
        cmd.add(file.getAbsolutePath());
        return runCommand(cmd);
    }
}
