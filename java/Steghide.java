import java.io.*;
import java.util.*;
import works.*;


// Classe principal para testar
public class Steghide {
	private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
	
	File steghide = IS_WINDOWS ? 
		new File("C:\\Program Files\\steghide\\steghide.exe") : // caminho do steghide
		new File("/usr/bin/steghide");
  	File cover = new File("shadai.jpg");   // imagem de capa
  	File secret = new File("camilo.txt"); // arquivo a esconder
  	File saida = new File("saida.txt");
  	String senha = "minhaSenhaForte";//senha
  
  
	public void encriptar(){
		try{
		// Criptografar / embutir
        	SteghideEncryptor encryptor = new SteghideEncryptor(steghide);
       		ProcessResult res1 = encryptor.embed(cover, secret, senha);
        	System.out.println("Embed exit=" + res1.getExitCode());
        	System.out.println(res1.getOutput());
     }catch(Exception e){
     	System.out.println("Erro " + e.getMessage());
     }
	}
	
	public void decriptar(){
	
	try{
	
		//Decriptografar / extrair
        	SteghideDecryptor decryptor = new SteghideDecryptor(steghide);
        	ProcessResult res2 = decryptor.extract(cover, senha, saida);
        	System.out.println("Extract exit=" + res2.getExitCode());
        	System.out.println(res2.getOutput());
   }catch(Exception e){
   	System.out.println("Erro " + e.getMessage());
   }
	}
	
	public void ver_mensagem(){
	try{
		ProcessResult res3 = FileViewer.viewFile(saida);
		System.out.println("Conteudo do arquivo extraido:");
		System.out.println(res3.getOutput());
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
