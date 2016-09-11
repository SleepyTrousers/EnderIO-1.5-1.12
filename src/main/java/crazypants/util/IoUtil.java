package crazypants.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import crazypants.enderio.config.Config;

public class IoUtil {

  public static String copyConfigFromJar(String fileName, boolean replaceIfExists) throws IOException {    
    
    if(replaceIfExists || !getConfigFile(fileName).exists()) {
      IOUtils.copy(IoUtil.class.getResourceAsStream(getConfigResourcePath(fileName)), new FileOutputStream(getConfigFile(fileName)));
    }            
    return IOUtils.toString(new FileReader(getConfigFile(fileName)));    
  }
  
  public static String readConfigFile(String fileName) throws IOException {
    return IOUtils.toString(new FileReader(getConfigFile(fileName)));
  }
  
  public static String readConfigFileFromClassPath(String fileName) throws IOException {
    return readFileFromClassPath(getConfigResourcePath(fileName));
  }
  
  public static String getConfigResourcePath(String name) {
    return "/assets/enderio/config/" + name;
  }
  
  public static File getConfigFile(String name) {
    return new File(Config.configDirectory, name);
  }
  
  public static String readFileFromClassPath(String fileName) throws IOException {
    InputStream in = IoUtil.class.getResourceAsStream(fileName);
    if(in == null) {
      throw new IOException("Could find resource " + fileName + " in classpath. ");
    }    
    return IOUtils.toString(in);
  }
  
  
//  public static void writeTextFileToConfig(String fileName, String contents) throws IOException {
//    writeTextFile(prependConfigPath(fileName), contents);
//  }
//  
//  public static void writeTextFile(String fileName, String contents) throws IOException {
//    BufferedWriter writer = null;
//    try {
//      writer = new BufferedWriter(new FileWriter(fileName, false));
//      writer.write(contents);
//    } finally {
//      IOUtils.closeQuietly(writer);
//    }    
//  }
  
}
