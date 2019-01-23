package com.passport.crypto.eth;

import java.security.SecureRandom;

/**
 * Utility class for working with SecureRandom implementation.
 *
 * <p>This is to address issues with SecureRandom on Android. For more information, refer to the
 * following <a href="https://github.com/web3j/web3j/issues/146">issue</a>.
 */
final class SecureRandomUtils {

<<<<<<< HEAD
    private static final SecureRandom SECURE_RANDOM;

    static {
        if (isAndroidRuntime()) {
            new LinuxSecureRandom();
        }
        SECURE_RANDOM = new SecureRandom();
    }

    static SecureRandom secureRandom() {
        return SECURE_RANDOM;
    }

    // Taken from BitcoinJ implementation
    // https://github.com/bitcoinj/bitcoinj/blob/3cb1f6c6c589f84fe6e1fb56bf26d94cccc85429/core/src/main/java/org/bitcoinj/core/Utils.java#L573
    private static int isAndroid = -1;

    static boolean isAndroidRuntime() {
        if (isAndroid == -1) {
            final String runtime = System.getProperty("java.runtime.name");
            isAndroid = (runtime != null && runtime.equals("Android Runtime")) ? 1 : 0;
        }
        return isAndroid == 1;
    }

    private SecureRandomUtils() { }
=======
  private static final SecureRandom SECURE_RANDOM;
  // Taken from BitcoinJ implementation
  // https://github.com/bitcoinj/bitcoinj/blob/3cb1f6c6c589f84fe6e1fb56bf26d94cccc85429/core/src/main/java/org/bitcoinj/core/Utils.java#L573
  private static int isAndroid = -1;

  static {
    if (isAndroidRuntime()) {
      new LinuxSecureRandom();
    }
    SECURE_RANDOM = new SecureRandom();
  }

  private SecureRandomUtils() {
  }

  static SecureRandom secureRandom() {
    return SECURE_RANDOM;
  }

  static boolean isAndroidRuntime() {
    if (isAndroid == -1) {
      final String runtime = System.getProperty("java.runtime.name");
      isAndroid = (runtime != null && runtime.equals("Android Runtime")) ? 1 : 0;
    }
    return isAndroid == 1;
  }
>>>>>>> a1abf2231ceadb16c3538774fc50b7415b1816d4
}
