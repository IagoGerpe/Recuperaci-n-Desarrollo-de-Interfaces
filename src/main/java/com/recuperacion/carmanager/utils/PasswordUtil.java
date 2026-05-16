package com.recuperacion.carmanager.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil { //Esta clase está reciclada de mi trabajo de programación de servicios y procesos

    //constantes para el algoritmo del hash
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    private PasswordUtil() {}

    public static String hashPassword(String password) { //este metodo es el que recibe la contraseña y aplica el hash tanto el hash como el salt, devolviendo una contraseña segura de guardar en la base de datos
        byte[] salt = generateSalt();
        byte[] hash = generateHash(password, salt);

        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String encodedHash = Base64.getEncoder().encodeToString(hash);

        return encodedSalt + ":" + encodedHash;
    }

    public static boolean checkPassword(String password, String storedPassword) { //método que comprueba que la contraseña aportada coincide con la contraseña del usuario solicitado (para el login vaya)
        String[] parts = storedPassword.split(":");

        if (parts.length != 2) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHash = Base64.getDecoder().decode(parts[1]);
        byte[] passwordHash = generateHash(password, salt);

        return slowEquals(storedHash, passwordHash);
    }

    private static byte[] generateSalt() { //el salt se añade a la contraseña de un usuario para que dos usuarios diferntes no tengan el mismo hash en caso de que usen la misma contraseña
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    private static byte[] generateHash(String password, byte[] salt) { //este método genera el hash con la contraseña dada
        try {
            PBEKeySpec keySpec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH
            );

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return secretKeyFactory.generateSecret(keySpec).getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new RuntimeException("No se pudo emcriptar la contraseña.", exception);
        }
    }

    private static boolean slowEquals(byte[] firstArray, byte[] secondArray) { //compara dos arrays de bytes, sin importar la longitud de las contraseañs para evitar timing attacks
        if (firstArray.length != secondArray.length) {
            return false;
        }

        int result = 0;

        for (int i = 0; i < firstArray.length; i++) {
            result = result | firstArray[i] ^ secondArray[i];
        }

        return result == 0;
    }
}