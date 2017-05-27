package crazypants.enderio.integration.forestry;

import javax.annotation.Nonnull;

import crazypants.enderio.farming.IFarmer;
import crazypants.enderio.farming.farmers.FarmersCommune;
import crazypants.enderio.farming.farmers.IFarmerJoe;
import crazypants.enderio.farming.farmers.IHarvestResult;
import crazypants.util.Prep;
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

public class ForestryFarmer implements IFarmerJoe {
  private final @Nonnull ITreeRoot root;
  private final @Nonnull Item forestrySapling;

  private ForestryFarmer(@Nonnull ITreeRoot root, @Nonnull Item forestrySapling) {
    this.root = root;
    this.forestrySapling = forestrySapling;
  }

  public static void init() {
    ITreeRoot root = (ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
    Item forestrySapling = Item.REGISTRY.getObject(new ResourceLocation("forestry", "sapling"));
    if (root != null && forestrySapling != null)
      FarmersCommune.joinCommune(new ForestryFarmer(root, forestrySapling));
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return stack.getItem() == forestrySapling && root.getType(stack) == EnumGermlingType.SAPLING;
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState state) {
    ItemStack sapling = farm.getSeedTypeInSuppliesFor(bc);
    if (sapling.getItem() == forestrySapling) {
      ITree tree = root.getMember(sapling);
      if (tree != null && tree.canStay(farm.getWorld(), bc)) {
        if (Prep.isValid(farm.takeSeedFromSupplies(sapling, bc, false))) {
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
