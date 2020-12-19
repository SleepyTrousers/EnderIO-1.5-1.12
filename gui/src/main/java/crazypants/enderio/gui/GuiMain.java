package crazypants.enderio.gui;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import crazypants.enderio.gui.forms.MainWindow;

public class GuiMain {

  public static void main(String[] args) {
    System.out.println("Helly World!");
    if (args.length > 0) {
      System.out.println("Automatic start detected");
      System.out.println("arg0=" + args[0]);
      if (args[0].startsWith("-")) {
        MainWindow.main(null);
      } else {
        MainWindow.main(args[0]);
      }
      // ValueRepository.setFile(new File(args[0]));
      // ValueRepository.read();
      // System.out.println(ValueRepository.OREDICTS.getDescription("ballDarkSteel").get(0));
    } else {
      System.out.println("Manual jar start detected");
      run("-"); // "/Users/micaja/git/EnderIO/run/config/enderio");
    }
  }

  public static boolean run(String extraParam) {
    String binPath = System.getProperty("java.home") + "/bin/java";
    String jar = whereFrom(GuiMain.class);
    String cp = System.getProperty("java.class.path");
    ProcessBuilder pb = jar == null ? new ProcessBuilder(binPath, "-cp", cp, GuiMain.class.getCanonicalName(), extraParam)
        : new ProcessBuilder(binPath, "-jar", jar, extraParam);
    pb.inheritIO();
    try {
      pb.start();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static String whereFrom(Class<?> c) {
    if (c == null) {
      return null;
    }
    try {
      ClassLoader loader = c.getClassLoader();
      if (loader == null) {
        // Try the bootstrap classloader - obtained from the ultimate parent of the System Class Loader.
        loader = ClassLoader.getSystemClassLoader();
        while (loader != null && loader.getParent() != null) {
          loader = loader.getParent();
        }
      }
      if (loader != null) {
        String name = c.getCanonicalName();
        URL resource = loader.getResource(name.replace(".", "/") + ".class");
        if (resource != null && resource.getProtocol().equals("jar")) {
          JarURLConnection connection = (JarURLConnection) resource.openConnection();
          File file = new File(connection.getJarFileURL().toURI());
          return file.getCanonicalPath();
        }
        return null;
      }
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    }
    return null; // this should be an error
  }

}
