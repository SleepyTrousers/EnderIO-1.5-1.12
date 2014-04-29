package crazypants.enderio.farm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.util.BlockCoord;
import crazypants.util.Util;

public class PotatoeFarmerPaddy implements IFarmerJoe {

  @Override
  public boolean isFarmerForTheJob(World worldObj, BlockCoord bc, Block block, int meta, EntityPlayer player) {
    return block == Blocks.potatoes;
  }

  @Override
  public boolean prepareBlock(World worldObj, BlockCoord bc, Block block, int meta, EntityPlayer player) {
    if(block != Blocks.air || !Util.isEquipped(player, ItemHoe.class)) {
      return false;
    }
    BlockCoord loc = bc.getLocation(ForgeDirection.DOWN);
    Block blk = worldObj.getBlock(loc.x, loc.y, loc.z);
    if(blk == Blocks.dirt || blk == Blocks.grass) {
      worldObj.setBlock(loc.x, loc.y, loc.z, Blocks.farmland);
      worldObj.playSoundEffect(loc.x + 0.5F, loc.y+ 0.5F, loc.z+ 0.5F, Blocks.farmland.stepSound.getStepResourcePath(), (Blocks.farmland.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.farmland.stepSound.getPitch() * 0.8F);
      player.getCurrentEquippedItem().damageItem(1, player);
      return true;
    }
    return false;
  }

  @Override
  public List<EntityItem> harvestBlock(World worldObj, BlockCoord bc, Block block, int meta, EntityPlayer player) {

    if(meta < 7 || !isFarmerForTheJob(worldObj, bc, block, meta, player)) {
      return null;
    }

    List<EntityItem> result = new ArrayList<EntityItem>();
    //todo: looting
    ArrayList<ItemStack> drops = block.getDrops(worldObj, bc.x, bc.y, bc.z, meta, 0);
    boolean removed = false;
    if(drops != null) {
      for (ItemStack stack : drops) {
        if(!removed && stack.getItem() == Items.potato) {
          stack.stackSize--;
          removed = true;
          if(stack.stackSize > 0) {
            result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, stack.copy()));
          }
        } else {
          result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, stack.copy()));
        }
      }
    }

    if(removed) {
      replant(worldObj, bc, result);
    }

    return result;
  }

  private void replant(World worldObj, BlockCoord bc, List<EntityItem> result) {

    worldObj.setBlock(bc.x, bc.y, bc.z, Blocks.air, 0, 1 | 2);

    Block target = Blocks.potatoes;
    Block ground = worldObj.getBlock(bc.x, bc.y - 1, bc.z);
    IPlantable plantable = (IPlantable) Blocks.potatoes;
    if(target.canPlaceBlockAt(worldObj, bc.x, bc.y, bc.z) &&
        target.canBlockStay(worldObj, bc.x, bc.y, bc.z) &&
        ground.canSustainPlant(worldObj, bc.x, bc.y, bc.z, ForgeDirection.UP, plantable)) {

      worldObj.setBlock(bc.x, bc.y, bc.z, Blocks.potatoes, 0, 1 | 2);
    } else {

      result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, new ItemStack(Blocks.potatoes)));
    }
  }

}
