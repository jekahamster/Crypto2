package crypto;

// KKS

public class Handler {
	public static void main(String[] args) {
		String pass1 = "Kirnis";
		String pass2 = "Kirnis";
		System.out.println("Primary Number: " + Crypter.createNewPrimaryNumber(pass1));
		System.out.println("Secondary Number: " + Crypter.createNewSecondaryNumber(pass2));
		CryptoFileController.createShuffledKeysFile();
		
		
		String message = "Hello, everybody!";
		String encryptedMessage = Crypter.encryptMessage(message);
		String decryptedMessage = Crypter.decryptMessage(encryptedMessage);
		System.out.println("Encrypted: " + encryptedMessage);
		System.out.println("Decrypted: " + decryptedMessage);
	}
}

// KKS