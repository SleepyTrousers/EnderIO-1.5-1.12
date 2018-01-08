package crazypants.enderio.base.integration.forestry;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.util.Prep;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;

public class ForestryFarmer extends Impl<IFarmerJoe> implements IFarmerJoe {
  private final @Nonnull ITreeRoot root;
  private final @Nonnull Item forestrySapling;

  private ForestryFarmer(@Nonnull ITreeRoot root, @Nonnull Item forestrySapling) {
    this.root = root;
    this.forestrySapling = forestrySapling;
  }

  public static void init(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    ITreeRoot root = (ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
    Item forestrySapling = Item.REGISTRY.getObject(new ResourceLocation("forestry", "sapling"));
    if (root != null && forestrySapling != null)
      event.getRegistry().register(new ForestryFarmer(root, forestrySapling).setRegistryName("forestry", "trees"));
  }

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    return stack.getItem() == forestrySapling && root.getType(stack) == EnumGermlingType.SAPLING;
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState state) {
    ItemStack sapling = farm.getSeedTypeInSuppliesFor(bc);
    if (sapling.getItem() == forestrySapling) {
      ITree tree = root.getMember(sapling);
      if (tree != null && tree.canStay(farm.getWorld(), bc)) {
        if (Prep.isValid(farm.takeSeedFromSupplies(bc))) {
          root.plantSapling(farm.getWorld(), tree, farm.getFakePlayer().getGameProfile(), bc);
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState state) {
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState state) {
    return null;
  }

}
