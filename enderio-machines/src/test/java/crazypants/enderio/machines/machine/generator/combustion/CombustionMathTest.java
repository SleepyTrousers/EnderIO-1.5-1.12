/**
 * 
 */
package crazypants.enderio.machines.machine.generator.combustion;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.fluid.IFluidCoolant;
import crazypants.enderio.base.fluid.IFluidFuel;
import net.minecraftforge.fluids.Fluid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Henry Loenwind
 *
 */
class CombustionMathTest {

  private static class MockCoolant implements IFluidCoolant {

    private final float degreesCoolingPerMB, startTemp;

    MockCoolant(float degreesCoolingPerMB, float startTemp) {
      this.degreesCoolingPerMB = degreesCoolingPerMB;
      this.startTemp = startTemp;
    }

    @SuppressWarnings("null")
    @Override
    @Nonnull
    public Fluid getFluid() {
      return null;
    }

    @Override
    public double getDegreesCoolingPerMB() {
      return (273.25 + 100.0 - startTemp) * getDegreesCoolingPerMBPerK();
    }

    @Override
    public double getDegreesCoolingPerMBPerK() {
      return degreesCoolingPerMB;
    }

  }

  private static class MockFuel implements IFluidFuel {

    private final int totalBurningTime, powerPerCycle;

    MockFuel(int powerPerCycle, int totalBurningTime) {
      this.totalBurningTime = totalBurningTime;
      this.powerPerCycle = powerPerCycle;
    }

    @SuppressWarnings("null")
    @Override
    @Nonnull
    public Fluid getFluid() {
      return null;
    }

    @Override
    public int getTotalBurningTime() {
      return totalBurningTime;
    }

    @Override
    public int getPowerPerCycle() {
      return powerPerCycle;
    }

  }

  private final MockCoolant WATER = new MockCoolant(0.0023f, 300);
  private final MockCoolant CRYO = new MockCoolant(0.0276f, 50);
  private final MockCoolant VAPOR = new MockCoolant(0.0314f, 5);

  private final MockFuel HOOTCH = new MockFuel(Config.hootchPowerPerCycleRF, Config.hootchPowerTotalBurnTime);

  @BeforeEach
  void testSetup() {
    assertNotNull(WATER);
    assertNotNull(CRYO);
    assertNotNull(HOOTCH);
  }

  // WATER

  @Test
  void testWater_cap1_n() {
    CombustionMath cm = new CombustionMath(WATER, HOOTCH, 1f, 1f);
    assertEquals(60, cm.getEnergyPerTick(), "WATER+HOOTCH getEnergyPerTick");
    assertEquals(24, cm.getTicksPerCoolant(), "WATER+HOOTCH getTicksPerCoolant");
    assertEquals(6, cm.getTicksPerFuel(), "WATER+HOOTCH getTicksPerFuel");
  }

  @Test
  void testWater_cap1_e() {
    CombustionMath cm = new CombustionMath(WATER, HOOTCH, 1f, 1.5f);
    assertEquals(90, cm.getEnergyPerTick(), "WATER+HOOTCH getEnergyPerTick");
    assertEquals(11, cm.getTicksPerCoolant(), "WATER+HOOTCH getTicksPerCoolant");
    assertEquals(6, cm.getTicksPerFuel(), "WATER+HOOTCH getTicksPerFuel");
  }

  @Test
  void testWater_cap2_n() {
    CombustionMath cm = new CombustionMath(WATER, HOOTCH, 2f, 1f);
    assertEquals(120, cm.getEnergyPerTick(), "WATER+HOOTCH getEnergyPerTick");
    assertEquals(12, cm.getTicksPerCoolant(), "WATER+HOOTCH getTicksPerCoolant");
    assertEquals(3, cm.getTicksPerFuel(), "WATER+HOOTCH getTicksPerFuel");
  }

  @Test
  void testWater_cap2_e() {
    CombustionMath cm = new CombustionMath(WATER, HOOTCH, 2f, 1.5f);
    assertEquals(180, cm.getEnergyPerTick(), "WATER+HOOTCH getEnergyPerTick");
    assertEquals(5, cm.getTicksPerCoolant(), "WATER+HOOTCH getTicksPerCoolant");
    assertEquals(3, cm.getTicksPerFuel(), "WATER+HOOTCH getTicksPerFuel");
  }

  @Test
  void testWater_cap3_n() {
    CombustionMath cm = new CombustionMath(WATER, HOOTCH, 3f, 1f);
    assertEquals(180, cm.getEnergyPerTick(), "WATER+HOOTCH getEnergyPerTick");
    assertEquals(8, cm.getTicksPerCoolant(), "WATER+HOOTCH getTicksPerCoolant");
    assertEquals(2, cm.getTicksPerFuel(), "WATER+HOOTCH getTicksPerFuel");
  }

  @Test
  void testWater_cap3_e() {
    CombustionMath cm = new CombustionMath(WATER, HOOTCH, 3f, 1.5f);
    assertEquals(270, cm.getEnergyPerTick(), "WATER+HOOTCH getEnergyPerTick");
    assertEquals(4, cm.getTicksPerCoolant(), "WATER+HOOTCH getTicksPerCoolant");
    assertEquals(2, cm.getTicksPerFuel(), "WATER+HOOTCH getTicksPerFuel");
  }

  @Test
  void testWater_cap5_n() {
    CombustionMath cm = new CombustionMath(WATER, HOOTCH, 5f, 1f);
    assertEquals(300, cm.getEnergyPerTick(), "WATER+HOOTCH getEnergyPerTick");
    assertEquals(5, cm.getTicksPerCoolant(), "WATER+HOOTCH getTicksPerCoolant");
    assertEquals(1, cm.getTicksPerFuel(), "WATER+HOOTCH getTicksPerFuel");
  }

  @Test
  void testWater_cap5_e() {
    CombustionMath cm = new CombustionMath(WATER, HOOTCH, 5f, 1.5f);
    assertEquals(450, cm.getEnergyPerTick(), "WATER+HOOTCH getEnergyPerTick");
    assertEquals(2, cm.getTicksPerCoolant(), "WATER+HOOTCH getTicksPerCoolant");
    assertEquals(1, cm.getTicksPerFuel(), "WATER+HOOTCH getTicksPerFuel");
  }

  // Cryo

  @Test
  void testCryo_cap1_n() {
    CombustionMath cm = new CombustionMath(CRYO, HOOTCH, 1f, 1f);
    assertEquals(60, cm.getEnergyPerTick(), "CRYO+HOOTCH getEnergyPerTick");
    assertEquals(1293, cm.getTicksPerCoolant(), "CRYO+HOOTCH getTicksPerCoolant");
    assertEquals(6, cm.getTicksPerFuel(), "CRYO+HOOTCH getTicksPerFuel");
  }

  @Test
  void testCryo_cap1_e() {
    CombustionMath cm = new CombustionMath(CRYO, HOOTCH, 1f, 1.5f);
    assertEquals(90, cm.getEnergyPerTick(), "CRYO+HOOTCH getEnergyPerTick");
    assertEquals(575, cm.getTicksPerCoolant(), "CRYO+HOOTCH getTicksPerCoolant");
    assertEquals(6, cm.getTicksPerFuel(), "CRYO+HOOTCH getTicksPerFuel");
  }

  @Test
  void testCryo_cap2_n() {
    CombustionMath cm = new CombustionMath(CRYO, HOOTCH, 2f, 1f);
    assertEquals(120, cm.getEnergyPerTick(), "CRYO+HOOTCH getEnergyPerTick");
    assertEquals(646, cm.getTicksPerCoolant(), "CRYO+HOOTCH getTicksPerCoolant");
    assertEquals(3, cm.getTicksPerFuel(), "CRYO+HOOTCH getTicksPerFuel");
  }

  @Test
  void testCryo_cap2_e() {
    CombustionMath cm = new CombustionMath(CRYO, HOOTCH, 2f, 1.5f);
    assertEquals(180, cm.getEnergyPerTick(), "CRYO+HOOTCH getEnergyPerTick");
    assertEquals(287, cm.getTicksPerCoolant(), "CRYO+HOOTCH getTicksPerCoolant");
    assertEquals(3, cm.getTicksPerFuel(), "CRYO+HOOTCH getTicksPerFuel");
  }

  @Test
  void testCryo_cap3_n() {
    CombustionMath cm = new CombustionMath(CRYO, HOOTCH, 3f, 1f);
    assertEquals(180, cm.getEnergyPerTick(), "CRYO+HOOTCH getEnergyPerTick");
    assertEquals(431, cm.getTicksPerCoolant(), "CRYO+HOOTCH getTicksPerCoolant");
    assertEquals(2, cm.getTicksPerFuel(), "CRYO+HOOTCH getTicksPerFuel");
  }

  @Test
  void testCryo_cap3_e() {
    CombustionMath cm = new CombustionMath(CRYO, HOOTCH, 3f, 1.5f);
    assertEquals(270, cm.getEnergyPerTick(), "CRYO+HOOTCH getEnergyPerTick");
    assertEquals(192, cm.getTicksPerCoolant(), "CRYO+HOOTCH getTicksPerCoolant");
    assertEquals(2, cm.getTicksPerFuel(), "CRYO+HOOTCH getTicksPerFuel");
  }

  @Test
  void testCryo_cap5_n() {
    CombustionMath cm = new CombustionMath(CRYO, HOOTCH, 5f, 1f);
    assertEquals(300, cm.getEnergyPerTick(), "CRYO+HOOTCH getEnergyPerTick");
    assertEquals(259, cm.getTicksPerCoolant(), "CRYO+HOOTCH getTicksPerCoolant");
    assertEquals(1, cm.getTicksPerFuel(), "CRYO+HOOTCH getTicksPerFuel");
  }

  @Test
  void testCryo_cap5_e() {
    CombustionMath cm = new CombustionMath(CRYO, HOOTCH, 5f, 1.5f);
    assertEquals(450, cm.getEnergyPerTick(), "CRYO+HOOTCH getEnergyPerTick");
    assertEquals(115, cm.getTicksPerCoolant(), "CRYO+HOOTCH getTicksPerCoolant");
    assertEquals(1, cm.getTicksPerFuel(), "CRYO+HOOTCH getTicksPerFuel");
  }

  // Comparison

  @Test
  void testCryoRelative() {
    CombustionMath cm = new CombustionMath(WATER, HOOTCH, 1f, 1f);
    CombustionMath cc = new CombustionMath(CRYO, HOOTCH, 1f, 1f);
    assertTrue(cc.getEnergyPerTick() >= cm.getEnergyPerTick(), "CRYO+HOOTCH >= WATER+HOOTCH getEnergyPerTick");
    assertTrue(cc.getTicksPerCoolant() >= cm.getTicksPerCoolant(), "CRYO+HOOTCH >= WATER+HOOTCH getTicksPerCoolant");
    assertTrue(cc.getTicksPerFuel() >= cm.getTicksPerFuel(), "CRYO+HOOTCH >= WATER+HOOTCH getTicksPerFuel");
  }

  @Test
  void testVaporRelative() {
    CombustionMath cm = new CombustionMath(CRYO, HOOTCH, 1f, 1f);
    CombustionMath cc = new CombustionMath(VAPOR, HOOTCH, 1f, 1f);
    assertTrue(cc.getEnergyPerTick() >= cm.getEnergyPerTick(), "VAPOR+HOOTCH >= CRYO+HOOTCH getEnergyPerTick");
    assertTrue(cc.getTicksPerCoolant() >= cm.getTicksPerCoolant(), "VAPOR+HOOTCH >= CRYO+HOOTCH getTicksPerCoolant");
    assertTrue(cc.getTicksPerFuel() >= cm.getTicksPerFuel(), "VAPOR+HOOTCH >= CRYO+HOOTCH getTicksPerFuel");
  }

  // Vapor of Levity

  @Test
  void testVapor_cap1_n() {
    CombustionMath cm = new CombustionMath(VAPOR, HOOTCH, 1f, 1f);
    assertEquals(60, cm.getEnergyPerTick(), "VAPOR+HOOTCH getEnergyPerTick");
    assertEquals(1676, cm.getTicksPerCoolant(), "VAPOR+HOOTCH getTicksPerCoolant");
    assertEquals(6, cm.getTicksPerFuel(), "VAPOR+HOOTCH getTicksPerFuel");
  }

  @Test
  void testVapor_cap1_e() {
    CombustionMath cm = new CombustionMath(VAPOR, HOOTCH, 1f, 1.5f);
    assertEquals(90, cm.getEnergyPerTick(), "VAPOR+HOOTCH getEnergyPerTick");
    assertEquals(745, cm.getTicksPerCoolant(), "VAPOR+HOOTCH getTicksPerCoolant");
    assertEquals(6, cm.getTicksPerFuel(), "VAPOR+HOOTCH getTicksPerFuel");
  }

  @Test
  void testVapor_cap2_n() {
    CombustionMath cm = new CombustionMath(VAPOR, HOOTCH, 2f, 1f);
    assertEquals(120, cm.getEnergyPerTick(), "VAPOR+HOOTCH getEnergyPerTick");
    assertEquals(838, cm.getTicksPerCoolant(), "VAPOR+HOOTCH getTicksPerCoolant");
    assertEquals(3, cm.getTicksPerFuel(), "VAPOR+HOOTCH getTicksPerFuel");
  }

  @Test
  void testVapor_cap2_e() {
    CombustionMath cm = new CombustionMath(VAPOR, HOOTCH, 2f, 1.5f);
    assertEquals(180, cm.getEnergyPerTick(), "VAPOR+HOOTCH getEnergyPerTick");
    assertEquals(372, cm.getTicksPerCoolant(), "VAPOR+HOOTCH getTicksPerCoolant");
    assertEquals(3, cm.getTicksPerFuel(), "VAPOR+HOOTCH getTicksPerFuel");
  }

  @Test
  void testVapor_cap3_n() {
    CombustionMath cm = new CombustionMath(VAPOR, HOOTCH, 3f, 1f);
    assertEquals(180, cm.getEnergyPerTick(), "VAPOR+HOOTCH getEnergyPerTick");
    assertEquals(559, cm.getTicksPerCoolant(), "VAPOR+HOOTCH getTicksPerCoolant");
    assertEquals(2, cm.getTicksPerFuel(), "VAPOR+HOOTCH getTicksPerFuel");
  }

  @Test
  void testVapor_cap3_e() {
    CombustionMath cm = new CombustionMath(VAPOR, HOOTCH, 3f, 1.5f);
    assertEquals(270, cm.getEnergyPerTick(), "VAPOR+HOOTCH getEnergyPerTick");
    assertEquals(248, cm.getTicksPerCoolant(), "VAPOR+HOOTCH getTicksPerCoolant");
    assertEquals(2, cm.getTicksPerFuel(), "VAPOR+HOOTCH getTicksPerFuel");
  }

  @Test
  void testVapor_cap5_n() {
    CombustionMath cm = new CombustionMath(VAPOR, HOOTCH, 5f, 1f);
    assertEquals(300, cm.getEnergyPerTick(), "VAPOR+HOOTCH getEnergyPerTick");
    assertEquals(335, cm.getTicksPerCoolant(), "VAPOR+HOOTCH getTicksPerCoolant");
    assertEquals(1, cm.getTicksPerFuel(), "VAPOR+HOOTCH getTicksPerFuel");
  }

  @Test
  void testVapor_cap5_e() {
    CombustionMath cm = new CombustionMath(VAPOR, HOOTCH, 5f, 1.5f);
    assertEquals(450, cm.getEnergyPerTick(), "VAPOR+HOOTCH getEnergyPerTick");
    assertEquals(149, cm.getTicksPerCoolant(), "VAPOR+HOOTCH getTicksPerCoolant");
    assertEquals(1, cm.getTicksPerFuel(), "VAPOR+HOOTCH getTicksPerFuel");
  }

}
