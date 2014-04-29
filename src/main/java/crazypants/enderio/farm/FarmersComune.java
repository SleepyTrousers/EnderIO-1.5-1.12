package crazypants.enderio.farm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import crazypants.util.BlockCoord;

public class FarmersComune implements IFarmerJoe {

  public static FarmersComune instance = new FarmersComune();

  public static void joinComune(IFarmerJoe joe) {
    instance.farmers.add(joe);
  }

  public static void leaveComune(IFarmerJoe joe) {
    //instance.farmers.remove(joe);
    throw new UnsupportedOperationException("As if this would be implemented.");
  }

  static {
    joinComune(new PotatoeFarmerPaddy());
  }

  private List<IFarmerJoe> farmers = new ArrayList<IFarmerJoe>();

  @Override
  public boolean isFarmerForTheJob(World worldObj, BlockCoord bc, Block block, int meta, EntityPlayer player) {
    for (IFarmerJoe joe : farmers) {
      if(joe.isFarmerForTheJob(worldObj, bc, block, meta, player)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<EntityItem> harvestBlock(World worldObj, BlockCoord bc, Block block, int meta, EntityPlayer player) {
    if(!block.canHarvestBlock(player, meta)) {
      return null;
    }
    for (IFarmerJoe joe : farmers) {
      if(joe.isFarmerForTheJob(worldObj, bc, block, meta, player)) {
        return joe.harvestBlock(worldObj, bc, block, meta, player);
      }
    }
    return null;
  }

  @Override
  public boolean prepareBlock(World worldObj, BlockCoord bc, Block block, int meta, EntityPlayer player) {
    for (IFarmerJoe joe : farmers) {
      if(joe.prepareBlock(worldObj, bc, block, meta, player)) {
        return true;
      }
    }
    return false;
  }



}
