package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.machine.farm.TileFarmStation;

public class PlantableFarmer implements IFarmerJoe {

    private Set<Block> harvestExcludes = new HashSet<Block>();

    public void addHarvestExlude(Block block) {
        harvestExcludes.add(block);
    }

    @Override
    public boolean canPlant(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return stack.getItem() instanceof IPlantable;
    }

    @Override
    public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        if (block == null) {
            return false;
        }

        ItemStack seedStack = farm.getSeedTypeInSuppliesFor(bc);
        if (seedStack == null) {
            farm.setNotification(TileFarmStation.NOTIFICATION_NO_SEEDS);
            return false;
        }

        if (!(seedStack.getItem() instanceof IPlantable)) {
            return false;
        }

        IPlantable plantable = (IPlantable) seedStack.getItem();
        EnumPlantType type = plantable.getPlantType(farm.getWorldObj(), bc.x, bc.y, bc.z);
        if (type == null) {
            return false;
        }
        Block ground = farm.getBlock(bc.getLocation(ForgeDirection.DOWN));
        if (type == EnumPlantType.Nether) {
            if (ground != Blocks.soul_sand) {
                return false;
            }
            return plantFromInventory(farm, bc, plantable);
        }

        if (type == EnumPlantType.Crop) {
            farm.tillBlock(bc);
            return plantFromInventory(farm, bc, plantable);
        }

        if (type == EnumPlantType.Water) {
            return plantFromInventory(farm, bc, plantable);
        }

        return false;
    }

    // From BlockBush, as a reference
    // @Override
    // public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z)
    // {
    // if (this == Blocks.wheat) return Crop;
    // if (this == Blocks.carrots) return Crop;
    // if (this == Blocks.potatoes) return Crop;
    // if (this == Blocks.melon_stem) return Crop;
    // if (this == Blocks.pumpkin_stem) return Crop;
    // if (this == Blocks.deadbush) return Desert;
    // if (this == Blocks.waterlily) return Water;
    // if (this == Blocks.red_mushroom) return Cave;
    // if (this == Blocks.brown_mushroom) return Cave;
    // if (this == Blocks.nether_wart) return Nether;
    // if (this == Blocks.sapling) return Plains;
    // if (this == Blocks.tallgrass) return Plains;
    // if (this == Blocks.double_plant) return Plains;
    // if (this == Blocks.red_flower) return Plains;
    // if (this == Blocks.yellow_flower) return Plains;
    // return Plains;
    // }

    protected boolean plantFromInventory(TileFarmStation farm, BlockCoord bc, IPlantable plantable) {
        World worldObj = farm.getWorldObj();
        if (canPlant(worldObj, bc, plantable) && farm.takeSeedFromSupplies(bc) != null) {
            return plant(farm, worldObj, bc, plantable);
        }
        return false;
    }

    protected boolean plant(TileFarmStation farm, World worldObj, BlockCoord bc, IPlantable plantable) {
        worldObj.setBlock(bc.x, bc.y, bc.z, Blocks.air, 0, 1 | 2);
        Block target = plantable.getPlant(null, 0, 0, 0);
        int meta = plantable.getPlantMetadata(null, 0, 0, 0);
        worldObj.setBlock(bc.x, bc.y, bc.z, target, meta, 1 | 2);
        farm.actionPerformed(false);
        return true;
    }

    protected boolean canPlant(World worldObj, BlockCoord bc, IPlantable plantable) {
        Block target = plantable.getPlant(null, 0, 0, 0);
        Block ground = worldObj.getBlock(bc.x, bc.y - 1, bc.z);
        if (target != null && target.canPlaceBlockAt(worldObj, bc.x, bc.y, bc.z)
                && target.canBlockStay(worldObj, bc.x, bc.y, bc.z)
                && ground.canSustainPlant(worldObj, bc.x, bc.y - 1, bc.z, ForgeDirection.UP, plantable)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        if (!harvestExcludes.contains(block) && block instanceof IGrowable && !(block instanceof BlockStem)) {
            return !((IGrowable) block).func_149851_a(farm.getWorldObj(), bc.x, bc.y, bc.z, true);
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

        ItemStack removedPlantable = null;

        ArrayList<ItemStack> drops = block.getDrops(worldObj, bc.x, bc.y, bc.z, meta, farm.getMaxLootingValue());
        farm.damageHoe(1, bc);
        farm.actionPerformed(false);
        boolean removed = false;
        if (drops != null) {
            for (ItemStack stack : drops) {
                if (stack != null && !removed && isPlantableForBlock(stack, block)) {
                    stack.stackSize--;
                    removed = true;
                    removedPlantable = stack.copy();
                    if (stack.stackSize > 0) {
                        result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, stack.copy()));
                    }
                } else {
                    result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, stack.copy()));
                }
            }
        }

        if (removed) {
            if (!plant(farm, worldObj, bc, (IPlantable) removedPlantable.getItem())) {
                result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, removedPlantable.copy()));
                worldObj.setBlock(bc.x, bc.y, bc.z, Blocks.air, 0, 1 | 2);
            }
        } else {
            worldObj.setBlock(bc.x, bc.y, bc.z, Blocks.air, 0, 1 | 2);
        }

        return new HarvestResult(result, bc);
    }

    private boolean isPlantableForBlock(ItemStack stack, Block block) {
        if (!(stack.getItem() instanceof IPlantable)) {
            return false;
        }
        IPlantable plantable = (IPlantable) stack.getItem();
        return plantable.getPlant(null, 0, 0, 0) == block;
    }
}
