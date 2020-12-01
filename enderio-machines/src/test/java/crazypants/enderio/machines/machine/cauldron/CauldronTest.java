package crazypants.enderio.machines.machine.cauldron;

import net.minecraft.util.math.MathHelper;

public class CauldronTest {

  private static class TestMath {
    float in1, in2;
    int water;

    public TestMath(float in1, float in2, int water) {
      super();
      this.in1 = in1;
      this.in2 = in2;
      this.water = water;
    }

    void tick() {
      if (water > 10) {
        water--;
        if (water <= 0) {
          in1 = in2 = water = 0;
        }
      }
    }

    double waterPerInput = 500;

    double yield() {
      double a = 1.45992201759558E+02;
      double b = 7.78379997224069E-05;
      double c = 2.35321095525948E+00;

      // double a = 2.87017287344277E-03;
      // double b = 9.09728756485006E-01;
      // double c = 2.35321061011871E+00;
      double min = Math.min(in1, in2);
      double x = MathHelper.clamp(water / waterPerInput / min, 0, 1);

      return a * Math.pow(b, x) * Math.pow(x, c);
    }

    int yield_mB(double amount) {
      return (int) MathHelper.clamp(yield() * amount * 1000, 0, waterPerInput * amount);
    }

    int usage_mB(double amount) {
      double min = Math.min(in1, in2);
      return (int) MathHelper.clamp(Math.min(Math.floor(water * amount / min), yield_mB(amount)), 0, water);
    }

    void use(double amount) {
      water -= usage_mB(amount);
      in1 -= amount;
      in2 -= amount;
    }

    boolean canUse(double amount) {
      return in1 >= amount && in2 >= amount && water > 1 && usage_mB(amount) < water;
    }
  }

  // @Test
  void test1() {
    float in1 = 1f, in2 = 1;
    int water = 500;
    System.out.println("Input: meat worth " + in1 + " + sugar worth " + in2 + " + " + water + "mB water");
    for (int i = 0; i < water; i++) {
      TestMath a = new TestMath(in1, in2, water);
      for (int j = 0; j < i; j++) {
        a.tick();
      }
      System.out.print("after " + i + " ticks: ");
      while (a.canUse(.1)) {
        System.out.print(a.yield_mB(.1) + "mB");
        a.use(.1);
        if (a.canUse(.1)) {
          System.out.print(" + ");
        }
      }
      System.out.print(" (remaining: meat=" + a.in1 + ", sugar=" + a.in2 + ", water=" + a.water + "mB)");
      System.out.println();
    }
  }

}
