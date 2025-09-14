package works;

import java.io.*;
import java.util.*;

// Classe utilitária que encapsula execução de comandos
public class CommandRunner {
    static ProcessResult runCommand(List<String> command, File workingDir) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        if (workingDir != null) pb.directory(workingDir);
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
}
