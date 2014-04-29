package crazypants.enderio.farm;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import crazypants.util.BlockCoord;

public interface IFarmerJoe {

  boolean prepareBlock(World worldObj, BlockCoord bc, Block block, int meta, EntityPlayer player);

  boolean isFarmerForTheJob(World worldObj, BlockCoord bc, Block block, int meta, EntityPlayer player);

  List<EntityItem> harvestBlock(World worldObj, BlockCoord bc, Block block, int meta, EntityPlayer player);

}
