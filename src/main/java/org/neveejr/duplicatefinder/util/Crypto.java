package org.neveejr.duplicatefinder.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.apache.log4j.Logger;

public class Crypto {

	private static Logger logger = Logger.getLogger(Crypto.class);
	// private static MessageDigest md;
	//
	// static {
	// try {
	// md = MessageDigest.getInstance("MD5");
	// } catch (NoSuchAlgorithmException e) {
	// md = null;
	// logger.error(e.toString());
	// }
	//
	// }

	public static String getMd5(String path) {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");

			try (InputStream is = Files.newInputStream(Paths.get(path));
					DigestInputStream dis = new DigestInputStream(is, md)) {
				byte[] buffer = new byte[1024];
				int numRead;

				do {
					numRead = is.read(buffer);
					if (numRead > 0) {
						md.update(buffer, 0, numRead);
					}
				} while (numRead != -1);

				byte[] digest = md.digest();
				return Base64.getEncoder().encodeToString(digest);
			} catch (IOException e) {
				logger.error(e.toString());
			}

		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

}
