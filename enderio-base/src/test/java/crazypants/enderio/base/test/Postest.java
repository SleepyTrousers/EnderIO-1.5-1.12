package crazypants.enderio.base.test;

import org.junit.jupiter.api.Test;

import net.minecraft.util.math.BlockPos;

class Postest {

  /**
   * Not really a test, just dumping out data...
   */
  @Test
  void testBlockPosLong() {
    System.out.println("Origin: " + BlockPos.ORIGIN.toLong());
    System.out.println("(0, -1, 0): " + new BlockPos(0, -1, 0).toLong());
    System.out.println("f(0, -1, 0): " + BlockPos.fromLong(new BlockPos(0, -1, 0).toLong()));
  }

}
