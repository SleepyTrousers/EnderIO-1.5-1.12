package crazypants.enderio.base.farming.farmers;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.FarmingAction;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.FarmingTool;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;

public class FlowerPicker extends Impl<IFarmerJoe> implements IFarmerJoe {

  protected Things flowers = new Things();

  public FlowerPicker(@Nonnull Things flowers) {
    add(flowers);
  }

  public FlowerPicker add(@Nonnull Things newFlowers) {
    this.flowers.add(newFlowers);
    FarmersRegistry.slotItemsProduce.add(newFlowers);
    return this;
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    return false;
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    return flowers.contains(block) || block instanceof IShearable;
  }

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull final IFarmer farm, @Nonnull final BlockPos pos, @Nonnull Block block, @Nonnull IBlockState meta) {
    final World world = farm.getWorld();
    List<ItemStack> drops = null;

    if (block instanceof IShearable) {
      if (!farm.hasTool(FarmingTool.SHEARS)) {
        farm.setNotification(FarmNotification.NO_SHEARS);
        return null;
      }
      ItemStack shears = farm.getTool(FarmingTool.SHEARS);
      if (!((IShearable) block).isShearable(shears, world, pos)) {
        return null;
      }
      drops = ((IShearable) block).onSheared(shears, world, pos, farm.getLootingValue(FarmingTool.SHEARS));
      farm.registerAction(FarmingAction.HARVEST, FarmingTool.SHEARS, meta, pos);
    } else {
      if (!farm.hasTool(FarmingTool.HOE)) {
        farm.setNotification(FarmNotification.NO_HOE);
        return null;
      }
      drops = block.getDrops(world, pos, meta, farm.getLootingValue(FarmingTool.HOE));
      farm.registerAction(FarmingAction.HARVEST, FarmingTool.HOE, meta, pos);
    }

    final NNList<EntityItem> result = new NNList<EntityItem>();

    NNList.wrap(drops).apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack drop) {
        result.add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop.copy()));
      }
    });

    world.setBlockToAir(pos);

    return new HarvestResult(result, pos);
  }

}
