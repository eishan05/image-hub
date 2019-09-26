package com.eishanlawrence.utils;

public final class Utilities {

  private static final String ALPHA_NUMERIC_STRING =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

  private static final int DEFAULT_RANDOM_STRING_SIZE = 20;

  /** Builds a random string of default size 20 */
  public static String getRandomString() {
    return buildRandomString(DEFAULT_RANDOM_STRING_SIZE);
  }

  /** Builds a random string of the specified size */
  public static String getRandomString(int size) {
    return buildRandomString(size);
  }

  private static String buildRandomString(int size) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < DEFAULT_RANDOM_STRING_SIZE; i++) {
      int index = (int) (ALPHA_NUMERIC_STRING.length() * Math.random());
      stringBuilder.append(ALPHA_NUMERIC_STRING.charAt(index));
    }
    return stringBuilder.toString();
  }

  private Utilities() {}
}
