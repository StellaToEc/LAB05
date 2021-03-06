import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class lab5 {

    static void saveText(String text,String fileName, int opc) {
        String dirActual = "Text";
        Path path = Paths.get("");
        String directoryName = path.toAbsolutePath().toString();

        directoryName+="\\"+dirActual+"\\"+fileName+"Edited.txt";

        try {
            File fileText = new File(directoryName);
            FileWriter pw = new FileWriter(fileText, true);

            if(opc==1){
                pw.write(Base64.getEncoder().encodeToString(text.getBytes()));
                System.out.println("text stored correctly");
            }else{
                pw.write(text);
                System.out.println("text stored correctly");
            }

            pw.close();
        }
        catch (Exception e) {
            System.out.println("Error writing text");
        }
    }

    static String readText(String fileName, int opc) throws IOException {
        String dirActual = "Text";
        Path path = Paths.get("");
        String directoryName = path.toAbsolutePath().toString();

        directoryName+="\\"+dirActual+"\\"+fileName+".txt";

        byte[] source = Files.readAllBytes(Path.of(directoryName));
        String message = new String(source);

        if(opc==1){
            return message;
        }else {
            byte[] decodedBytes = Base64.getDecoder().decode(message);
            String decodedString = new String(decodedBytes);
            return decodedString;
        }
    }

    static void createKey (int size, String fileName) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(size);
            SecretKey key = keyGen.generateKey();
            saveText(Base64.getEncoder().encodeToString(key.getEncoded()), fileName, 2);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR");
        }
    }

    static IvParameterSpec createIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    static void encrypt(String keyfile, String filename, int mode, int elec) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        String instance = "AES/CBC/PKCS5Padding";
        String plainText;
        String cypherText;
        byte[] ct;
        SecretKey key= new SecretKeySpec(Base64.getDecoder().decode(readText(keyfile, 1)), "AES" );
        IvParameterSpec iv;

        if(elec==1){
            plainText = readText(filename,1);
            iv=createIv();
        }else{
            plainText = readText(filename,2);
            iv = new IvParameterSpec(plainText.substring(0,15).getBytes(StandardCharsets.UTF_8)); //default
            int keysize = key.toString().length();
            if(keysize==23)
                iv = new IvParameterSpec(plainText.substring(0,23).getBytes(StandardCharsets.UTF_8));
            if(keysize==31)
                iv = new IvParameterSpec(plainText.substring(0,31).getBytes(StandardCharsets.UTF_8));
        }
        switch(mode){
            case 1 -> instance="AES/CBC/PKCS5Padding";
            case 2 -> instance="AES/CTR/PKCS5Padding";
            case 3 -> instance="AES/CFB/PKCS5Padding";
        }

        Cipher cipher= Cipher.getInstance(instance);
        cipher.init(elec,key,iv);
        ct= cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        if(elec==1){
            cypherText = new String(iv.toString())+ new String(ct);
            saveText(cypherText,filename,1);
        }else {
            cypherText = new String(ct);
            saveText(cypherText,filename,2);
        }
    }


    public static void main (String[]args) throws IOException, NoSuchPaddingException,NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidAlgorithmParameterException{
        Scanner reader = new Scanner(System.in);
        String keyfile;
        String namefile;
        int mode;

        System.out.println("to encrypt press 1 to decrypt 2");
        int elec = reader.nextInt();
        reader.nextLine();

        if(elec==1){
            System.out.println("Write the name file for save the key:");
            keyfile= reader.nextLine();
            System.out.println("Choose key size(128, 192 or 256):");
            int size = reader.nextInt();

            createKey(size,keyfile);

            System.out.println("Write the name file to encrypt:");
            namefile= reader.nextLine();
            System.out.println("Choose a mode 1-CBC 2-CTR 3-CFB:");
            mode = reader.nextInt();

            encrypt(keyfile,namefile,mode,elec);
        }else{
            System.out.println("Write the name file for read the key:");
            keyfile= reader.nextLine();
            System.out.println("Write the name file to decrypt:");
            namefile= reader.nextLine();
            System.out.println("Choose a mode 1-CBC 2-CTR 3-CFB:");
            mode = reader.nextInt();
            encrypt(keyfile,namefile,mode,elec);
        }
    }
}
