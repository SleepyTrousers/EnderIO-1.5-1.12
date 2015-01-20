package crazypants;

import java.io.BufferedReader;
import java.io.FileReader;

public class Crap {

  public static void main(String[] args) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader("C:\\MyData\\Dev\\Minecraft\\Workspace17\\PamsSeeds.txt"));
    String line = br.readLine();
    System.out.println("private static final String[] PAMS_SEEDS = new String[] {");
    while(line != null) {
      System.out.println("\""  + line.trim() + "\",");
      line = br.readLine();
    }
    System.out.println("};");
  }
  
}
