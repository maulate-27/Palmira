package works;

public class Config {
    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    public static final String STEGHIDE_PATH = IS_WINDOWS ? 
        "C:\\Program Files\\steghide\\steghide.exe" : 
        "/usr/bin/steghide";

    public static File getSteghideExecutable() {
        return new File(STEGHIDE_PATH);
    }
}
