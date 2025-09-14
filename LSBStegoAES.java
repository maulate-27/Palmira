import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

public class LSBStegoAES {

    // AES-GCM params
    private static final int GCM_TAG_LEN = 128; // bits
    private static final int IV_LEN = 12; // bytes

    // converte bytes para bits
    private static byte[] bytesFromBits(int[] bits) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int value = 0, count = 0;
        for (int b : bits) {
            value = (value << 1) | (b & 1);
            count++;
            if (count == 8) {
                out.write(value);
                value = 0; count = 0;
            }
        }
        if (count > 0) {
            value <<= (8 - count);
            out.write(value);
        }
        return out.toByteArray();
    }

    private static int[] bitsFromBytes(byte[] data) {
        int[] bits = new int[data.length * 8];
        int idx = 0;
        for (byte b : data) {
            for (int i = 7; i >= 0; i--) {
                bits[idx++] = (b >> i) & 1;
            }
        }
        return bits;
    }

    // AES-GCM encrypt
    public static byte[] encryptAESGCM(byte[] plain, byte[] key) throws Exception {
        SecureRandom rnd = new SecureRandom();
        byte[] iv = new byte[IV_LEN];
        rnd.nextBytes(iv);
        SecretKeySpec sk = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LEN, iv);
        cipher.init(Cipher.ENCRYPT_MODE, sk, spec);
        byte[] ct = cipher.doFinal(plain);
        ByteBuffer buf = ByteBuffer.allocate(iv.length + ct.length);
        buf.put(iv);
        buf.put(ct);
        return buf.array();
    }

    // AES-GCM decrypt
    public static byte[] decryptAESGCM(byte[] blob, byte[] key) throws Exception {
        byte[] iv = Arrays.copyOfRange(blob, 0, IV_LEN);
        byte[] ct = Arrays.copyOfRange(blob, IV_LEN, blob.length);
        SecretKeySpec sk = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LEN, iv);
        cipher.init(Cipher.DECRYPT_MODE, sk, spec);
        return cipher.doFinal(ct);
    }

    // derive key from password simples (PBKDF2 seria melhor; aqui didático)
    public static byte[] deriveKey(String password) throws Exception {
        // recomendado: PBKDF2WithHmacSHA256; este exemplo usa SHA-256 direto (não ideal para produção)
        byte[] p = password.getBytes(StandardCharsets.UTF_8);
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        return md.digest(p);
    }

    // Embutir: coverPNG -> outPNG ; secretBytes já cifrados
    public static void embedLSB(String coverPath, String outPath, byte[] secretBytes) throws Exception {
        BufferedImage img = ImageIO.read(new File(coverPath));
        int w = img.getWidth(), h = img.getHeight();
        int capacity = w * h * 3; // 3 bits por pixel (1 bit por canal R,G,B)
        int[] bits = bitsFromBytes(secretBytes);
        // acrescenta cabeçalho com tamanho em 32-bit (número de bytes do payload) -> para facilitar extração
        ByteBuffer header = ByteBuffer.allocate(4);
        header.putInt(secretBytes.length);
        byte[] hdrPlus = concat(header.array(), secretBytes);
        bits = bitsFromBytes(hdrPlus);

        if (bits.length > capacity) throw new IllegalArgumentException("Imagem pequena demais para os dados.");

        int bitIndex = 0;
        outer: for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int a = (rgb >> 24) & 0xFF;
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                if (bitIndex < bits.length) {
                    r = (r & ~1) | bits[bitIndex++]; // LSB do R
                }
                if (bitIndex < bits.length) {
                    g = (g & ~1) | bits[bitIndex++]; // LSB do G
                }
                if (bitIndex < bits.length) {
                    b = (b & ~1) | bits[bitIndex++]; // LSB do B
                }
                int nrgb = (a << 24) | (r << 16) | (g << 8) | b;
                img.setRGB(x, y, nrgb);
                if (bitIndex >= bits.length) break outer;
            }
        }
        ImageIO.write(img, "PNG", new File(outPath));
    }

    // Extrair: lê cabeçalho e recupera bytes (cifrados), depois decifra
    public static byte[] extractLSB(String stegoPath) throws Exception {
        BufferedImage img = ImageIO.read(new File(stegoPath));
        int w = img.getWidth(), h = img.getHeight();
        int capacity = w * h * 3;
        int[] bits = new int[capacity];
        int idx = 0;
        outer: for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                bits[idx++] = r & 1;
                bits[idx++] = g & 1;
                bits[idx++] = b & 1;
            }
        }
        byte[] allBytes = bytesFromBits(bits);
        // primeiro 4 bytes = tamanho (big-endian)
        if (allBytes.length < 4) throw new IllegalArgumentException("Dados insuficientes.");
        ByteBuffer bb = ByteBuffer.wrap(allBytes);
        int size = bb.getInt();
        if (size < 0 || size > allBytes.length - 4) throw new IllegalArgumentException("Tamanho inválido ou stego vazio.");
        byte[] payload = Arrays.copyOfRange(allBytes, 4, 4 + size);
        return payload;
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] r = new byte[a.length + b.length];
        System.arraycopy(a,0,r,0,a.length);
        System.arraycopy(b,0,r,a.length,b.length);
        return r;
    }

    // exemplo de uso
    public static void main(String[] args) throws Exception {
        String cover = "cover.png";        // PNG sem perdas
        String out = "cover_stego.png";
        String senha = "minhaSenhaSegura";

        // mensagem -> cifrar -> embutir
        String mensagem = "Segredo importante!";
        byte[] key = deriveKey(senha); // 256-bit key (SHA-256)
        byte[] ct = encryptAESGCM(mensagem.getBytes(StandardCharsets.UTF_8), key);

        embedLSB(cover, out, ct);
        System.out.println("Embed OK: " + out);

        // extrair -> decifrar
        byte[] extracted = extractLSB(out);
        byte[] pt = decryptAESGCM(extracted, key);
        System.out.println("Recuperado: " + new String(pt, StandardCharsets.UTF_8));
    }
}
