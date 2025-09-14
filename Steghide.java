import java.io.*;
import java.util.*;

// Classe utilitária que encapsula execução de comandos
class CommandRunner {
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

// Estrutura para resultado de execução
class ProcessResult {
    final int exitCode;
    final String output;

    ProcessResult(int code, String out) {
        this.exitCode = code;
        this.output = out;
    }
}

// Classe para EMBUTIR (criptografar no sentido de esconder)
class SteghideEncryptor {
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

// Classe para EXTRAIR (decriptografar / revelar o que foi escondido)
class SteghideDecryptor {
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

class FileViewer {

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



// Classe principal para testar
public class Steghide {

	File steghide = new File("C:\\Program Files\\steghide\\steghide.exe"); // caminho do steghide
  File cover = new File("P:\\mira.jpg");   // imagem de capa
  File secret = new File("P:\\mensagem.txt"); // arquivo a esconder
  File saida = new File("P:\\saida.txt");
  String senha = "minhaSenhaForte";
  
  
	public void encriptar(){
		try{
		// Criptografar / embutir
        	SteghideEncryptor encryptor = new SteghideEncryptor(steghide);
       		ProcessResult res1 = encryptor.embed(cover, secret, senha);
        	System.out.println("Embed exit=" + res1.exitCode);
        	System.out.println(res1.output);
     }catch(Exception e){
     	System.out.println("Erro " + e.getMessage());
     }
	}
	
	public void decriptar(){
	
	try{
		// Decriptografar / extrair
        	SteghideDecryptor decryptor = new SteghideDecryptor(steghide);
        	ProcessResult res2 = decryptor.extract(cover, senha, saida);
        	System.out.println("Extract exit=" + res2.exitCode);
        	System.out.println(res2.output);
   }catch(Exception e){
   	System.out.println("Erro " + e.getMessage());
   }
	}
	
	public void ver_mensagem(){
	try{
		ProcessResult res3 = FileViewer.viewFile(saida);
		System.out.println("Conteudo do arquivo extraido:");
		System.out.println(res3.output);
	}catch(Exception e){
		System.out.println("Erro" + e.getMessage());
	}
	}
    public static void main(String[] args) throws Exception {
    		Scanner sc = new Scanner(System.in);
    		
    		Steghide st = new Steghide();
        int opcao;
				
				do {
				
				System.out.println("--------------------- Menu --------------------------- \n\t1.Encriptar\n\t2.Decriptar\n\t3.Ver Mensagem\n\t0.sair");
				opcao = sc.nextInt();
				
				switch(opcao){
					case 1:
        				st.encriptar();
        	break;

					case 2:
        		st.decriptar();
        	break;
        	
        	case 3:
        		st.ver_mensagem();
        	
        	default:
        	break;
        }
        }while(opcao!=0);
    }
}
