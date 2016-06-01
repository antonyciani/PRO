package utils;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Cette classe fournit des fonctions encapsulant les appels aux foncitons des classes java.security
 * et javax.crypto.
 *
 * @author STEINER Lucie
 *
 */
public class Cryptography {

	/**
	 * Permet la génération d'une paire de clés RSA.
	 *
	 * @return la paire de clés générée
	 */
	public static KeyPair generateRSAKeyPair() {
		KeyPairGenerator keyPairGenerator;
		KeyPair keyPair = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return keyPair;
	}

	/**
	 * Permet la génération d'une clé secrète AES
	 *
	 * @return la clé secrète générée
	 */
	public static SecretKey generateAESSecretKey() {
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return keyGenerator.generateKey();
	}

	/**
	 * Permet le chiffrement de données avec RSA
	 *
	 * @param plaintext, les données à chiffrer
	 * @param publicKey, la clé utilisée pour le chiffrement
	 * @return les données chiffrées
	 */
	public static byte[] RSAEncrypt(byte[] plaintext, RSAPublicKey publicKey) {
		byte[] ciphertext = null;

		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			ciphertext = cipher.doFinal(plaintext);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}

		return ciphertext;

	}

	/**
	 * Permet le déchiffrement des données avec RSA
	 *
	 * @param ciphertext, les données à déchiffrer
	 * @param privateKey, la clé utilisée pour le déchiffrement
	 * @return les données déchiffrées
	 */
	public static byte[] RSADecrypt(byte[] ciphertext, RSAPrivateKey privateKey) {
		byte[] plaintext = null;

		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			plaintext = cipher.doFinal(ciphertext);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}

		return plaintext;
	}

	/**
	 * Permet le chiffrement des données avec AES
	 *
	 * @param plaintext, les données à chiffrer
	 * @param secretKey, la clé utilisée pour le chiffrement
	 * @return les données chiffrées
	 */
	public static byte[] AESEncrypt(byte[] plaintext, SecretKey secretKey) {
		byte[] ciphertext = null;

		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			ciphertext = cipher.doFinal(plaintext);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}

		return ciphertext;
	}

	/**
	 * Permet le déchiffrement des données avec AES
	 * @param ciphertext, les données à déchiffrer
	 * @param secretKey, la clé utilisée pour le déchiffrement
	 * @return les données déchiffrées
	 */
	public static byte[] AESDecrypt(byte[] ciphertext, SecretKey secretKey) {
		byte[] plaintext = null;

		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			plaintext = cipher.doFinal(ciphertext);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}

		return plaintext;
	}
}
