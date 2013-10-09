package crazypants.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.FileChannel;

public class IOUtils {

  private static final long FILE_COPY_BUFFER_SIZE = 1024 * 1024;

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

  public static void closeQuietly(FileChannel output) {
    try {
      output.close();
    } catch (IOException e) {
    }
  }

  public static void moveFile(File srcFile, File destFile) throws IOException {
    if(srcFile == null) {
      throw new NullPointerException("Source must not be null");
    }
    if(destFile == null) {
      throw new NullPointerException("Destination must not be null");
    }
    if(!srcFile.exists()) {
      throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
    }
    if(srcFile.isDirectory()) {
      throw new IOException("Source '" + srcFile + "' is a directory");
    }
    if(destFile.exists()) {
      throw new IOException("Destination '" + destFile + "' already exists");
    }
    if(destFile.isDirectory()) {
      throw new IOException("Destination '" + destFile + "' is a directory");
    }
    boolean rename = srcFile.renameTo(destFile);
    if(!rename) {
      copyFile(srcFile, destFile, true);
      if(!srcFile.delete()) {
        throw new IOException("Failed to delete original file '" + srcFile +
            "' after copy to '" + destFile + "'");
      }
    }
  }

  public static void copyFile(File srcFile, File destFile,
      boolean preserveFileDate) throws IOException {
    if(srcFile == null) {
      throw new NullPointerException("Source must not be null");
    }
    if(destFile == null) {
      throw new NullPointerException("Destination must not be null");
    }
    if(srcFile.exists() == false) {
      throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
    }
    if(srcFile.isDirectory()) {
      throw new IOException("Source '" + srcFile + "' exists but is a directory");
    }
    if(srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
      throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
    }
    File parentFile = destFile.getParentFile();
    if(parentFile != null) {
      if(!parentFile.mkdirs() && !parentFile.isDirectory()) {
        throw new IOException("Destination '" + parentFile + "' directory cannot be created");
      }
    }
    if(destFile.exists() && destFile.canWrite() == false) {
      throw new IOException("Destination '" + destFile + "' exists but is read-only");
    }
    doCopyFile(srcFile, destFile, preserveFileDate);
  }

  private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
    if(destFile.exists() && destFile.isDirectory()) {
      throw new IOException("Destination '" + destFile + "' exists but is a directory");
    }

    FileInputStream fis = null;
    FileOutputStream fos = null;
    FileChannel input = null;
    FileChannel output = null;
    try {
      fis = new FileInputStream(srcFile);
      fos = new FileOutputStream(destFile);
      input = fis.getChannel();
      output = fos.getChannel();
      long size = input.size();
      long pos = 0;
      long count = 0;
      while (pos < size) {
        count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
        pos += output.transferFrom(input, pos, count);
      }
    } finally {
      closeQuietly(output);
      closeQuietly(fos);
      closeQuietly(input);
      closeQuietly(fis);
    }

    if(srcFile.length() != destFile.length()) {
      throw new IOException("Failed to copy full contents from '" +
          srcFile + "' to '" + destFile + "'");
    }
    if(preserveFileDate) {
      destFile.setLastModified(srcFile.lastModified());
    }
  }

}
