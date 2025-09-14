package works;

import java.io.*;
import java.util.*;

// Classe para EXTRAIR (decriptografar / revelar o que foi escondido)
public class SteghideDecryptor {
    private final File steghideExe;

    public SteghideDecryptor(File steghideExe) {
        this.steghideExe = steghideExe;
    }

    public ProcessResult extract(File stegoFile, String passphrase, File outputFile) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(steghideExe.getAbsolutePath());
        cmd.add("extract");
        cmd.add("-sf");
        cmd.add(stegoFile.getAbsolutePath());
        cmd.add("-p");
        cmd.add(passphrase);
        cmd.add("-xf");
        cmd.add(outputFile.getAbsolutePath());
        return CommandRunner.runCommand(cmd, null);
    }
}
