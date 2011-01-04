package nz.gen.wellington.guardian.android.api.caching;

import java.security.MessageDigest;

public class FileCacheLocalFilenameService {
	
	public static String getLocalFilenameFor(String url) {
		return md5(url);
	}
		
	private static String md5(String s) {
		try {
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (Exception e) {
		}
		return null;
	}
	
}
