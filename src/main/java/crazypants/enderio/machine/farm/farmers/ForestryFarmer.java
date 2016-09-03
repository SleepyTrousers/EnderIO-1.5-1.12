package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Log;
import crazypants.enderio.machine.farm.TileFarmStation;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.genetics.AlleleManager;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ForestryFarmer implements IFarmerJoe {
    private ITreeRoot root;
    private Item forestrySapling;

    private ForestryFarmer(ITreeRoot root, Item forestrySapling) {
        this.root = root;
        this.forestrySapling = forestrySapling;
    }

    public static void init() {
        ITreeRoot root = (ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
        Item forestrySapling = GameRegistry.findItem("Forestry", "sapling");
        if (root != null && forestrySapling != null) {
            FarmersCommune.joinCommune(new ForestryFarmer(root, forestrySapling));
            Log.info("ForestryFarmer engaged.");
        } else {
            Log.info("ForestryFarmer borked.");
        }
    }

    @Override
    public boolean canPlant(ItemStack stack) {
        return stack != null && stack.getItem() == forestrySapling && root.getType(stack) == EnumGermlingType.SAPLING;
    }
    
    @Override
    public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        ItemStack sapling = farm.getSeedTypeInSuppliesFor(bc);
        ITree tree = root.getMember(sapling);
        if (tree != null && tree.canStay(farm.getWorldObj(), bc.x, bc.y, bc.z)) {
            farm.takeSeedFromSupplies(sapling, bc, false);
            root.plantSapling(farm.getWorldObj(), tree, farm.getFakePlayer().getGameProfile(), bc.x, bc.y, bc.z);
            return true;
        }
        return false;

    }

    @Override
    public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        return false;
    }
    

    @Override
    public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        return null;
    }





}
