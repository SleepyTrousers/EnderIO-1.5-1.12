package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class BlockItemCustomFence extends ItemBlock {

  public BlockItemCustomFence(int id) {
    super(id);
    setHasSubtypes(true);
  }

  public int getMetadata(int par1) {
    return par1;
  }
  
}
