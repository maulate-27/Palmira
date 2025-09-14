package works;

import java.io.*;
import java.util.*;

// Classe para EMBUTIR (criptografar no sentido de esconder)
public class SteghideEncryptor {
    private final File steghideExe;

    public SteghideEncryptor(File steghideExe) {
        this.steghideExe = steghideExe;
    }

    public ProcessResult embed(File coverFile, File secretFile, String passphrase) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(steghideExe.getAbsolutePath());
        cmd.add("embed");
        cmd.add("-cf");
        cmd.add(coverFile.getAbsolutePath());
        cmd.add("-ef");
        cmd.add(secretFile.getAbsolutePath());
        cmd.add("-p");
        cmd.add(passphrase);
        return CommandRunner.runCommand(cmd, null);
    }
}
