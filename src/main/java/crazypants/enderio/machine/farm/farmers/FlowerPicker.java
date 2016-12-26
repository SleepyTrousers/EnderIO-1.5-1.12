package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.farm.TileFarmStation.ToolType;
import crazypants.util.Prep;
import crazypants.util.Things;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class FlowerPicker implements IFarmerJoe {

  protected Things flowers = new Things();

  public FlowerPicker(Things flowers) {
    add(flowers);
  }

  public FlowerPicker add(Things newFlowers) {
    this.flowers.add(newFlowers);
    FarmStationContainer.slotItemsProduce.addAll(newFlowers.getItemStacks());
    return this;
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    return false;
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    return flowers.contains(block) || block instanceof IShearable;
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    World worldObj = farm.getWorld();
    List<ItemStack> drops = null;

    if (block instanceof IShearable) {
      if (!farm.hasShears()) {
        farm.setNotification(FarmNotification.NO_SHEARS);
        return null;
      }
      ItemStack shears = farm.getTool(ToolType.SHEARS);
      if (!((IShearable) block).isShearable(shears, worldObj, bc)) {
        return null;
      }
      drops = ((IShearable) block).onSheared(shears, worldObj, bc, farm.getMaxLootingValue());
      farm.damageShears(block, bc);
    } else {
      if (!farm.hasHoe()) {
        farm.setNotification(FarmNotification.NO_HOE);
        return null;
      }
      drops = block.getDrops(worldObj, bc, meta, farm.getMaxLootingValue());
      farm.damageHoe(1, bc);
    }
    farm.actionPerformed(false);

    List<EntityItem> result = new ArrayList<EntityItem>();
    if (drops != null) {
      for (ItemStack stack : drops) {
        if (Prep.isValid(stack)) {
          result.add(new EntityItem(worldObj, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, stack.copy()));
        }
      }
    }

    worldObj.setBlockToAir(bc);

    return new HarvestResult(result, bc);
  }

}
