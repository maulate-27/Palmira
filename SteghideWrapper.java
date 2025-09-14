import java.io.*;
import java.util.*;

public class SteghideWrapper {

    private static void runCommand(List<String> cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) System.out.println(line);
        }
        int rc = p.waitFor();
        if (rc != 0) throw new RuntimeException("Comando falhou com código " + rc + ": " + String.join(" ", cmd));
    }

    public static void embed(String coverImage, String secretFile, String outImage, String password) throws Exception {
        // steghide embed -cf cover.jpg -ef secret.txt -sf out.jpg -p password
        List<String> cmd = new ArrayList<>(Arrays.asList(
            "steghide", "embed", "-cf", coverImage, "-ef", secretFile, "-sf", outImage, "-p", password
        ));
        runCommand(cmd);
    }

    public static void extract(String stegoImage, String password, String outFile) throws Exception {
        // steghide extract -sf out.jpg -p password -xf out.txt
        List<String> cmd = new ArrayList<>(Arrays.asList(
            "steghide", "extract", "-sf", stegoImage, "-p", password, "-xf", outFile
        ));
        runCommand(cmd);
    }

    public static void main(String[] args) throws Exception {
        // Exemplo de uso:
        embed("car.jpg", "mensagem.txt", "cover_stego.jpg", "minhaSenha");
        extract("cover_stego.jpg", "minhaSenha", "mensagem_extraida.txt");
    }
}
