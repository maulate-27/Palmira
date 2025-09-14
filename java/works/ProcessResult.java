package works;

// Estrutura para resultado de execução
public class ProcessResult {
    final int exitCode;
    final String output;

    ProcessResult(int code, String out) {
        this.exitCode = code;
        this.output = out;
    }
    
    public int getExitCode(){
    	return this.exitCode;
    }
    
    public String getOutput(){
    	return this.output;
    }
}
