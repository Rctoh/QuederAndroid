package com.example.queder;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

public class encryptDecypt {

    // Constants for AES-256 encryption
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";

    // Constants for PBKDF2 key derivation
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int PBKDF2_ITERATIONS = 10000;
    private static final int AES_KEY_LENGTH = 256; // In bits

    SharedPreferences sp;
    static SharedPreferences encryption_sp;

    ///////////////////////////BELOW IS ENCRYPT FOR USER ACCOUNT//////////////////////

    public static void generateAndStoreUserSymmetricKey(String password, String NRIC, Context context) {
        try {

            //GENERATE SYMMETRIC KEY
            byte[] NRIC_byte = NRIC.getBytes(StandardCharsets.UTF_8);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            KeySpec symmetricKeySpec = new PBEKeySpec(password.toCharArray(), NRIC_byte, PBKDF2_ITERATIONS, AES_KEY_LENGTH);
            SecretKey symmetricKey = secretKeyFactory.generateSecret(symmetricKeySpec);

            //GENERATE SECOND LAYER ENCRYPTION KEY (SLEK) (To encrypt symmetric key)
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            int keyDetails = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
            KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder("PBKDF2_SLEK", keyDetails)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(false)
                    .build();

            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(keySpec);

            //ENCRYPT THE SYMMETRIC KEY WITH SLEK

            byte[] ivByte = generateRandomIV();
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keyGenerator.generateKey(), new GCMParameterSpec(128, ivByte));
            byte[] encryptedSymmetricKey = cipher.doFinal(symmetricKey.getEncoded());

            //STORE encryptedSymmetricKey and IV in encrypted sharedpreferences

            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            encryption_sp = EncryptedSharedPreferences.create(
                    "userEncryptionDetails",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            encryption_sp.edit()
                    .putString("encryptedSymmetricKeyString", convertBytetoString(encryptedSymmetricKey))
                    .putString("ivString", convertBytetoString(ivByte))
                    .apply();

        } catch (Exception e) {
            Log.e("Keystore Error", "Error in generating/storing key", e);
        }

    }

    private static SecretKey retrieveUserSymmetricKey(Context context) throws Exception {

        //Instantiate SP to get encryptedSymmetricKey and IV
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        encryption_sp = EncryptedSharedPreferences.create(
                "userEncryptionDetails",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        String encryptedSymmetricKeyString = encryption_sp.getString("encryptedSymmetricKeyString", "");
        String ivString = encryption_sp.getString("ivString", "");

        //Get SECOND LAYER ENCRYPTION KEY (SLEK) from android keystore
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        SecretKey SLEK = (SecretKey) keyStore.getKey("PBKDF2_SLEK", null);

        //Decrypt the encryptedSymmetricKey
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        byte[] ivByte = convertStringtoByte(ivString);
        cipher.init(Cipher.DECRYPT_MODE, SLEK, new GCMParameterSpec(128, ivByte));
        byte[] decryptedSymmetricKeyByte = cipher.doFinal(convertStringtoByte(encryptedSymmetricKeyString));

        return convertByteToSecretKey(decryptedSymmetricKeyByte);
    }

    public static String Encrypt(String toBeEncrypted, Context context) {
        try {
            byte[] ivByte = generateRandomIV();
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, retrieveUserSymmetricKey(context), new GCMParameterSpec(128, ivByte));
            byte[] encryptedBytes = cipher.doFinal(toBeEncrypted.getBytes(StandardCharsets.UTF_8));

            JSONObject encryptedObject = new JSONObject();
            encryptedObject.put("encryptedString", convertBytetoString(encryptedBytes));
            encryptedObject.put("iv", convertBytetoString(ivByte));

            return encryptedObject.toString();
        } catch (Exception e) {
            Log.e("Encryption Error", "Error encrypting data", e);
            return null; // Return null or handle the error as appropriate
        }
    }

    public static String Decrypt(String toBeDecrypted, Context context) {
        try {
            JSONObject toBeDecryptedObject = new JSONObject(toBeDecrypted);
            String encryptedString = toBeDecryptedObject.getString("encryptedString");
            byte[] ivByte = convertStringtoByte(toBeDecryptedObject.getString("iv"));

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, retrieveUserSymmetricKey(context), new GCMParameterSpec(128, ivByte));
            byte[] decryptedByte = cipher.doFinal(convertStringtoByte(encryptedString));

            return new String(decryptedByte, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e("Decryption Error", "Error decrypting data", e);
            return null; // Return null or handle the error as appropriate
        }
    }

    public static byte[] generateFixedIV(String email) {
        byte[] iv = email.getBytes(StandardCharsets.UTF_8);

        // Ensure IV is exactly 16 bytes
        if (iv.length < 16) {
            byte[] paddedIV = new byte[16];
            System.arraycopy(iv, 0, paddedIV, 0, iv.length);
            return paddedIV;
        } else if (iv.length > 16) {
            byte[] truncatedIV = new byte[16];
            System.arraycopy(iv, 0, truncatedIV, 0, truncatedIV.length);
            return truncatedIV;
        } else {
            return iv;
        }
    }


    ///////////////////////////BELOW IS ENCRYPT TO SEND TO DOCTOR//////////////////////

    public static String encryptDataForDoctor(String patientData, String doctorPublicKey) throws Exception {

        SecretKey secretKey = generateRandomSecretKey();
        byte[] iv = generateRandomIV();

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] encryptedBytes = cipher.doFinal(patientData.getBytes(StandardCharsets.UTF_8));

        String asymmetricEncryptedSecretKey = encryptSecretKey(convertSecretKeyToString(secretKey), convertStringToPublicKey(doctorPublicKey));

        JSONObject encryptionObject = new JSONObject();
        encryptionObject.put("asymmetricEncryptedSecretKey", asymmetricEncryptedSecretKey);
        encryptionObject.put("iv", convertBytetoString(iv));
        encryptionObject.put("encryptedData", convertBytetoString(encryptedBytes));

        return encryptionObject.toString();
    }

    public static KeyPair generateAsymmetricKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

        // Create a KeyGenParameterSpec for the key pair generation
        KeyGenParameterSpec keyGenParameterSpec = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                    "QUEDER_ASYMMETRIC_KEY_PAIR",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .setIsStrongBoxBacked(false)
                    .build();
        }

        keyPairGenerator.initialize(keyGenParameterSpec);

        return keyPairGenerator.generateKeyPair();
    }



    public static PublicKey getAsymmetricPublicKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        return keyStore.getCertificate("QUEDER_ASYMMETRIC_KEY_PAIR").getPublicKey();
    }

    public static PrivateKey getAsymmetricPrivateKey() throws Exception {

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        return (PrivateKey) keyStore.getKey("QUEDER_ASYMMETRIC_KEY_PAIR", null);
    }

    public static String asymmetricEncryption(String toEncrypt) throws Exception {
        OAEPParameterSpec OPS = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getAsymmetricPublicKey(), OPS);
        byte[] encryptedBytes = cipher.doFinal(toEncrypt.getBytes());
        return convertBytetoString(encryptedBytes);
    }

    public static String asymmetricDecryption(String toDecrypt) throws Exception {
        OAEPParameterSpec OPS = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, getAsymmetricPrivateKey(), OPS);
        byte[] encryptedBytes = convertStringtoByte(toDecrypt);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }



    public static String convertPublicKeyToString(PublicKey publicKey) throws Exception {
        if (publicKey != null) {
            byte[] publicKeyBytes = publicKey.getEncoded();
            if (publicKeyBytes != null) {
                return Base64.encodeToString(publicKeyBytes, Base64.DEFAULT);
            } else {
                throw new IllegalArgumentException("Public key encoding is null");
            }
        } else {
            throw new IllegalArgumentException("Public key is null");
        }
    }

    public static PublicKey convertStringToPublicKey(String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    // INTERNAL PRIVATE LOWER-LEVEL FUNCTION TO BE UTILISED

    private static byte[] generateRandomIV() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[12]; // 16 bytes for AES
        secureRandom.nextBytes(iv);
        return iv;
    }

    private static SecretKey generateRandomSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);

            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String encryptSecretKey (String secretKeyToBeEncrypted, PublicKey doctorPublicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, doctorPublicKey);
        byte[] encryptedBytes = cipher.doFinal(secretKeyToBeEncrypted.getBytes());
        return convertBytetoString(encryptedBytes);
    }

    private static String convertSecretKeyToString(SecretKey secretKey) {
        byte[] keyBytes = secretKey.getEncoded();
        return Base64.encodeToString(keyBytes, Base64.DEFAULT);
    }

    private static SecretKey convertStringToSecretKey(String keyString) {
        byte[] keyBytes = Base64.decode(keyString, Base64.DEFAULT);
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    private static String convertBytetoString(byte[] inputByte) {
        return Base64.encodeToString(inputByte, Base64.DEFAULT);

    }

    private static byte[] convertStringtoByte(String inputString) {
        return Base64.decode(inputString, Base64.DEFAULT);
    }


    public static SecretKey convertByteToSecretKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Convert SecretKey to byte array
    public static byte[] convertSecretKeyToByte(SecretKey secretKey) {
        return secretKey.getEncoded();
    }

}
