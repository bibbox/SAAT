/***********************************************
 * StringEncrypter.java
 ***********************************************
 *
 ***********************************************
 * VERSION 1
 *
 * Medical University Graz
 * Institut of Pathology
 * Group of Univ.Prof. Dr.med.univ. Kurt Zatloukal
 * kurt.zatloukal(at)medunigraz.at
 * http://forschung.medunigraz.at/fodok/suchen.person_uebersicht?sprache_in=en&menue_id_in=101&id_in=90075196
 *
 ***********************************************
 * VERSION 2
 * http://sourceforge.net/projects/saat/
 *
 * Medical University Graz
 * Institut of Pathology
 * Group of Univ.Prof. Dr.med.univ. Kurt Zatloukal
 * kurt.zatloukal(at)medunigraz.at
 * http://forschung.medunigraz.at/fodok/suchen.person_uebersicht?sprache_in=en&menue_id_in=101&id_in=90075196
 *
 * Fraunhofer-Gesellschaft
 * Fraunhofer Institute for Biomedical Engineering
 * Central Research Infrastructure for molecular Pathology
 * Dr. Christina Schr�der
 * Christina.Schroeder(at)ibmt.fraunhofer.de
 * http://www.crip.fraunhofer.de/en/about/staff?noCache=776:1304399536
 ***********************************************
 * DESCRIPTION
 *
 * The encryption class based on the BASE64 Decoder and Encoder.
 * Need to search for a other Decoder and Encoder System for the
 * next releace. Decoder and Encoder the String using the MAC Address,
 * (befor the releace using MAC Address and the code from the Windows
 * System info Windows 2000 or XP, dose not worke with Windows 7 so
 * changed to MAC address only)
 ***********************************************
 */
package SAAT.generic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.KeySpec;
import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * The encryption class based on the BASE64 Decoder and Encoder.
 * Need to search for a other Decoder and Encoder System for the
 * next releace. Decoder and Encoder the String using the MAC Address,
 * (befor the releace using MAC Address and the code from the Windows
 * System info Windows 2000 or XP, dose not worke with Windows 7 so 
 * changed to MAC address only)
 *
 * @author  Reihs Robert
 * @author  Sauer Stefan
 * @version 2.0
 * @since   since_version_2_date
 */
public class StringEncrypter {

    /**
     * Description of the variable here.
     */
    public static final String desede_encryption_scheme_ = "DESede";
    private static final String unicode_format_ = "UTF8";
    private static String encryption_key_ = "";
    private KeySpec key_spec_;
    private SecretKeyFactory key_factory_;
    private Cipher cipher_;

    /**
     * Creates a new instance of StringEncrypter
     *
     * Criating the class, using the MAC address to generate the
     * key for the decoder and encoder.
     *
     * @throws EncryptionException
     */
    public StringEncrypter() throws EncryptionException {
        try {
            String Mac = LanAddress.getMacAddress();
            encryption_key_ = Mac.replaceAll("-", "") + Mac.replaceAll("-", "");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            byte[] keyAsBytes = encryption_key_.getBytes(unicode_format_);
            key_spec_ = new DESedeKeySpec(keyAsBytes);

            key_factory_ = SecretKeyFactory.getInstance(desede_encryption_scheme_);
            cipher_ = Cipher.getInstance(desede_encryption_scheme_);
        } catch (InvalidKeyException e) {
            throw new EncryptionException(e);
        } catch (UnsupportedEncodingException e) {
            throw new EncryptionException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptionException(e);
        }

    }

    /**
     * Encrypt the string
     *
     * Encrypt the given string with the generated mac address key and
     * returns the encrypted string.
     *
     * @param unencryptedString The given string
     * @return The encrypte string
     * @throws EncryptionException
     */
    public String encrypt(String unencryptedString) throws EncryptionException {
        if (unencryptedString == null || unencryptedString.trim().length() == 0) {
            throw new IllegalArgumentException("unencrypted string was null or empty");
        }
        try {
            SecretKey key = key_factory_.generateSecret(key_spec_);
            cipher_.init(Cipher.ENCRYPT_MODE, key);
            byte[] cleartext = unencryptedString.getBytes(unicode_format_);
            byte[] ciphertext = cipher_.doFinal(cleartext);

            BASE64Encoder base64encoder = new BASE64Encoder();
            return base64encoder.encode(ciphertext);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    /**
     * Decrypt the string
     *
     * Decrypt the given encrypted string with the generated mac address
     * key and returns the original string.
     *
     * @param encryptedString The encrypted string
     * @return The decrypte original string
     * @throws EncryptionException
     */
    public String decrypt(String encryptedString) throws EncryptionException {
        if (encryptedString == null || encryptedString.trim().length() <= 0) {
            throw new IllegalArgumentException("encrypted string was null or empty");
        }
        try {
            SecretKey key = key_factory_.generateSecret(key_spec_);
            cipher_.init(Cipher.DECRYPT_MODE, key);
            BASE64Decoder base64decoder = new BASE64Decoder();
            byte[] cleartext = base64decoder.decodeBuffer(encryptedString);
            byte[] ciphertext = cipher_.doFinal(cleartext);

            return bytes2String(ciphertext);
        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    /**
     * Function to transfer a byte array to a String
     *
     * Helping function for the transfer a byte array to a
     * String, for the decryption.
     *
     * @param bytes The byte array
     * @return The generated string
     */
    private static String bytes2String(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append((char) bytes[i]);
        }
        return stringBuffer.toString();
    }

    /**
     * Exception Class for the encryption Class
     */
    public static class EncryptionException extends Exception {

        public EncryptionException(Throwable t) {
            super(t);
        }
    }
}
