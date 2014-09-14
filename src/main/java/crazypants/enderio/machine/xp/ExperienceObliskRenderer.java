package crazypants.enderio.machine.xp;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.attractor.AttractorRenderer;
import crazypants.enderio.machine.spawnguard.TileSpawnGuard;

public class ExperienceObliskRenderer extends AttractorRenderer {
  
  private ItemStack stack = new ItemStack(EnderIO.itemXpTransfer);
  
  @Override
  protected ItemStack getFloatingStack(TileEntity te, double x, double y, double z, float tick) {    
    return stack;
  }

}
