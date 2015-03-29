package crazypants.enderio.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import cpw.mods.fml.common.Optional;

// this cannot be an inner class as fml/Optional does not support them
class BaublesToolwithBaubles extends BaublesTool {
    @Optional.Method(modid = "Baubles")
    public boolean hasBaubles() {
      return true;
    } 
    @Optional.Method(modid = "Baubles")
    public IInventory getBaubles(EntityPlayer player) {
      return baubles.api.BaublesApi.getBaubles(player);
    }
  }