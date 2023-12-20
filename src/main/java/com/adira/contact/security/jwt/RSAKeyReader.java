package com.adira.contact.security.jwt;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class RSAKeyReader {

    public static PrivateKey getPrivateKeyFromFile(String filePath) throws Exception {

        // Baca seluruh konten file ke dalam string
        // System.out.println("Membaca file...");
        String privateKeyPEM = new String(Files.readAllBytes(Paths.get(filePath)));

        // Hapus label "BEGIN" dan "END" serta baris-baris tambahan
        System.out.println("Menghapus label...");
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN ENCRYPTED PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("-----END ENCRYPTED PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replaceAll("\\s+", "");

        // Decode base64 string
        System.out.println("Mendecode Base64...");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        // Spesifikasi kunci privat PKCS#8
        System.out.println("Mengonversi ke PKCS8EncodedKeySpec...");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Generate kunci privat dari spesifikasi
        System.out.println("Generasi kunci privat...");
        return keyFactory.generatePrivate(keySpec);

    }

    public static PublicKey getPublicKeyFromFile(String filePath) throws Exception {
        // Baca seluruh konten file ke dalam string
        String publicKeyPEM = new String(Files.readAllBytes(Paths.get(filePath)));

        // Hapus label "BEGIN" dan "END" serta baris-baris tambahan
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replaceAll("\\s+", "");

        // Decode base64 string
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        // Spesifikasi kunci publik
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Generate kunci publik dari spesifikasi
        return keyFactory.generatePublic(keySpec);
    }
}