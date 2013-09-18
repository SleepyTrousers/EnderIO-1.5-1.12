package crazypants.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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

}
