package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.Things;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FlowerPicker implements IFarmerJoe {

  protected Things flowers = new Things();
  
  public FlowerPicker(Things flowers) {
    add(flowers);
  }

  public FlowerPicker add(Things flowers) {
    this.flowers.add(flowers);
    FarmStationContainer.slotItemsProduce.addAll(flowers.getItemStacks());
    return this;
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    return false;
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    return flowers.contains(block);
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {

    if(!farm.hasHoe()) {
      farm.setNotification(FarmNotification.NO_HOE);
      return null;
    }

    World worldObj = farm.getWorld();
    List<EntityItem> result = new ArrayList<EntityItem>();

    List<ItemStack> drops = block.getDrops(worldObj, bc.getBlockPos(), meta, farm.getMaxLootingValue());
    farm.damageHoe(1, bc);
    farm.actionPerformed(false);
    if(drops != null) {
      for (ItemStack stack : drops) {
        result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, stack.copy()));
      }
    }

    worldObj.setBlockToAir(bc.getBlockPos());

    return new HarvestResult(result, bc.getBlockPos());
  }

}
