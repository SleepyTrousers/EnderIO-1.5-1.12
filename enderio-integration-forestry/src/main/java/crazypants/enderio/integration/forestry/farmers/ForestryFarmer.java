package crazypants.enderio.integration.forestry.farmers;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.farm.AbstractFarmerJoe;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.integration.forestry.EnderIOIntegrationForestry;
import crazypants.enderio.integration.forestry.ForestryItemStacks;
import crazypants.enderio.util.Prep;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ForestryFarmer extends AbstractFarmerJoe {

  private ITreeRoot root = null;

  public ForestryFarmer() {
    setRegistryName(EnderIOIntegrationForestry.MODID, "treefarmer");
  }

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    return isValid() && stack.getItem() == ForestryItemStacks.FORESTRY_SAPLING.getItem() && getRoot().getType(stack) == EnumGermlingType.SAPLING;
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    if (isValid()) {
      ItemStack sapling = farm.getSeedTypeInSuppliesFor(bc);
      if (sapling.getItem() == ForestryItemStacks.FORESTRY_SAPLING.getItem()) {
        ITree tree = getRoot().getMember(sapling);
        if (tree != null && tree.canStay(farm.getWorld(), bc)) {
          if (Prep.isValid(farm.takeSeedFromSupplies(bc))) {
            getRoot().plantSapling(farm.getWorld(), tree, farm.getFakePlayer().getGameProfile(), bc);
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    return null;
  }

  private ITreeRoot getRoot() {
    if (root == null && NullHelper.untrust(AlleleManager.alleleRegistry) != null) {
      root = (ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
    }
    return root;
  }

  public boolean isValid() {
    return ForestryItemStacks.FORESTRY_SAPLING != null && getRoot() != null;
  }

}
