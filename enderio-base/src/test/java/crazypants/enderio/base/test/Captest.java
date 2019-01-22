package crazypants.enderio.base.test;

import org.junit.jupiter.api.Test;

import crazypants.enderio.base.capacitor.ScalerFactory;

class Captest {

  /**
   * Not really a test, just dumping out the scalers as csv...
   */
  @Test
  void testScaleValue() {
    System.out.print("\"Scaler\"");
    for (int i = 0; i < 100; i++) {
      float f = i / 10f;
      System.out.print(";" + f);
    }
    System.out.println();
    for (ScalerFactory scaler : ScalerFactory.values()) {
      System.out.print("\"" + scaler + "\"");
      for (int i = 0; i < 100; i++) {
        float f = i / 10f;
        System.out.print(";" + scaler.scaleValue(f));
      }
      System.out.println();
    }
  }

}
