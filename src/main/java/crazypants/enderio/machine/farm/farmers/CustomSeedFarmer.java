package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class CustomSeedFarmer implements IFarmerJoe {

    protected Block plantedBlock;
    protected int plantedBlockMeta;
    protected int grownBlockMeta;
    protected ItemStack seeds;
    protected boolean requiresFarmland = true;
    protected List<Block> tilledBlocks = new ArrayList<Block>();
    protected boolean ignoreSustainCheck = false;
    protected boolean disableTreeFarm;

    public CustomSeedFarmer(Block plantedBlock, ItemStack seeds) {
        this(plantedBlock, 0, 7, seeds);
    }

    public CustomSeedFarmer(Block plantedBlock, int grownBlockMeta, ItemStack seeds) {
        this(plantedBlock, 0, grownBlockMeta, seeds);
    }

    public CustomSeedFarmer(Block plantedBlock, int plantedBlockMeta, int grownBlockMeta, ItemStack seeds) {
        this.plantedBlock = plantedBlock;
        this.plantedBlockMeta = plantedBlockMeta;
        this.grownBlockMeta = grownBlockMeta;
        this.seeds = seeds;
        FarmStationContainer.slotItemsSeeds.add(seeds);
        addTilledBlock(Blocks.farmland);
        Block ztonesSoil = GameRegistry.findBlock("Ztones", "cleanDirt");
        if (ztonesSoil != null) {
            addTilledBlock(ztonesSoil);
        }
    }

    public void addTilledBlock(Block block) {
        tilledBlocks.add(block);
    }

    public boolean isIgnoreGroundCanSustainCheck() {
        return ignoreSustainCheck;
    }

    public void setIgnoreGroundCanSustainCheck(boolean ignoreSustainCheck) {
        this.ignoreSustainCheck = ignoreSustainCheck;
    }

    public int getPlantedBlockMeta() {
        return plantedBlockMeta;
    }

    public Block getPlantedBlock() {
        return plantedBlock;
    }

    public ItemStack getSeeds() {
        return seeds;
    }

    public int getFullyGrownBlockMeta() {
        return grownBlockMeta;
    }

    @Override
    public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        return block == getPlantedBlock() && getFullyGrownBlockMeta() == meta;
    }

    @Override
    public boolean canPlant(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return stack.isItemEqual(getSeeds());
    }

    @Override
    public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        if (!farm.isOpen(bc)) {
            return false;
        }
        if (requiresFarmland()) {
            World worldObj = farm.getWorldObj();
            if (isGroundTilled(farm, bc)) {
                return plantFromInventory(farm, bc);
            }
            if (farm.hasSeed(getSeeds(), bc)) {
                boolean tilled = tillBlock(farm, bc);
                if (!tilled) {
                    return false;
                }
            }
        }
        return plantFromInventory(farm, bc);
    }

    public boolean requiresFarmland() {
        return requiresFarmland;
    }

    public void setRequiresFarmland(boolean requiresFarmland) {
        this.requiresFarmland = requiresFarmland;
    }

    protected boolean plantFromInventory(TileFarmStation farm, BlockCoord bc) {
        World worldObj = farm.getWorldObj();
        if (canPlant(worldObj, bc) && farm.takeSeedFromSupplies(getSeeds(), bc) != null) {
            return plant(farm, worldObj, bc);
        }
        return false;
    }

    @Override
    public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {

        if (!canHarvest(farm, bc, block, meta)) {
            return null;
        }
        if (!farm.hasHoe()) {
            farm.setNotification(TileFarmStation.NOTIFICATION_NO_HOE);
            return null;
        }

        World worldObj = farm.getWorldObj();
        List<EntityItem> result = new ArrayList<EntityItem>();

        ArrayList<ItemStack> drops = block.getDrops(worldObj, bc.x, bc.y, bc.z, meta, farm.getMaxLootingValue());
        farm.damageHoe(1, bc);
        farm.actionPerformed(false);
        boolean removed = false;
        if (drops != null) {
            for (ItemStack stack : drops) {
                if (!removed && stack.isItemEqual(getSeeds())) {
                    stack.stackSize--;
                    removed = true;
                    if (stack.stackSize > 0) {
                        result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, stack.copy()));
                    }
                } else {
                    result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, stack.copy()));
                }
            }
        }

        if (removed) {
            if (!plant(farm, worldObj, bc)) {
                result.add(new EntityItem(
                        worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, getSeeds().copy()));
                worldObj.setBlock(bc.x, bc.y, bc.z, Blocks.air, 0, 1 | 2);
            }
        } else {
            worldObj.setBlock(bc.x, bc.y, bc.z, Blocks.air, 0, 1 | 2);
        }

        return new HarvestResult(result, bc);
    }

    protected boolean tillBlock(TileFarmStation farm, BlockCoord plantingLocation) {
        World worldObj = farm.getWorldObj();
        BlockCoord dirtLoc = plantingLocation.getLocation(ForgeDirection.DOWN);
        Block dirtBlock = farm.getBlock(dirtLoc);
        if ((dirtBlock == Blocks.dirt || dirtBlock == Blocks.grass) && farm.hasHoe()) {
            farm.damageHoe(1, dirtLoc);
            worldObj.setBlock(dirtLoc.x, dirtLoc.y, dirtLoc.z, Blocks.farmland);
            worldObj.playSoundEffect(
                    dirtLoc.x + 0.5F,
                    dirtLoc.y + 0.5F,
                    dirtLoc.z + 0.5F,
                    Blocks.farmland.stepSound.getStepResourcePath(),
                    (Blocks.farmland.stepSound.getVolume() + 1.0F) / 2.0F,
                    Blocks.farmland.stepSound.getPitch() * 0.8F);
            farm.actionPerformed(false);
            return true;
        }
        return false;
    }

    protected boolean isGroundTilled(TileFarmStation farm, BlockCoord plantingLocation) {
        Block target = farm.getBlock(plantingLocation.getLocation(ForgeDirection.DOWN));
        for (Block tst : tilledBlocks) {
            if (tst == target) {
                return true;
            }
        }
        return false;
    }

    protected boolean canPlant(World worldObj, BlockCoord bc) {
        Block target = getPlantedBlock();
        Block ground = worldObj.getBlock(bc.x, bc.y - 1, bc.z);
        IPlantable plantable = (IPlantable) getPlantedBlock();
        if (target.canPlaceBlockAt(worldObj, bc.x, bc.y, bc.z)
                && target.canBlockStay(worldObj, bc.x, bc.y, bc.z)
                && (ground.canSustainPlant(worldObj, bc.x, bc.y - 1, bc.z, ForgeDirection.UP, plantable)
                        || ignoreSustainCheck)) {
            return true;
        }
        return false;
    }

    protected boolean plant(TileFarmStation farm, World worldObj, BlockCoord bc) {
        worldObj.setBlock(bc.x, bc.y, bc.z, Blocks.air, 0, 1 | 2);
        if (canPlant(worldObj, bc)) {
            worldObj.setBlock(bc.x, bc.y, bc.z, getPlantedBlock(), getPlantedBlockMeta(), 1 | 2);
            farm.actionPerformed(false);
            return true;
        }
        return false;
    }

    public boolean doesDisableTreeFarm() {
        return disableTreeFarm;
    }
}
