package crazypants.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

public class IOUtils {

  public static void closeQuietly(Reader reader) {
    try {
      reader.close();
    } catch (Exception e) {
    }
  }

  public static void closeQuietly(Writer writer) {
    try {
      writer.close();
    } catch (Exception e) {
    }
  }

  public static void closeQuietly(InputStream is) {
    try {
      is.close();
    } catch (Exception e) {
    }
  }

  public static void closeQuietly(FileOutputStream fos) {
    try {
      fos.close();
    } catch (Exception e) {
    }

  }

}
