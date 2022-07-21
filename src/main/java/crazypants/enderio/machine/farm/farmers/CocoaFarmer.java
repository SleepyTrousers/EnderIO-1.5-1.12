package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class CocoaFarmer extends CustomSeedFarmer {
    public CocoaFarmer() {
        super(Blocks.cocoa, new ItemStack(Items.dye, 1, 3));
        this.requiresFarmland = false;
        if (!Config.farmHarvestJungleWhenCocoa) {
            this.disableTreeFarm = true;
        }
    }

    @Override
    public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
        return block == getPlantedBlock() && (meta & 12) >> 2 >= 2;
    }

    @Override
    protected boolean plant(TileFarmStation farm, World worldObj, BlockCoord bc) {
        worldObj.setBlock(bc.x, bc.y, bc.z, Blocks.air, 0, 1 | 2);
        int dir = getPlantDirection(worldObj, bc);
        if (dir < 0) return false;
        worldObj.setBlock(bc.x, bc.y, bc.z, getPlantedBlock(), Direction.facingToDirection[dir], 1 | 2);
        farm.actionPerformed(false);
        return true;
    }

    @Override
    protected boolean canPlant(World worldObj, BlockCoord bc) {
        return getPlantDirection(worldObj, bc) > 0;
    }

    private int getPlantDirection(World worldObj, BlockCoord bc) {
        if (!worldObj.isAirBlock(bc.x, bc.y, bc.z)) return -1;
        for (int i = 2; i < 6; i++) {
            ForgeDirection dir = ForgeDirection.getOrientation(i);
            int x = bc.x + dir.offsetX;
            int y = bc.y + dir.offsetY;
            int z = bc.z + dir.offsetZ;
            if (validBlock(worldObj.getBlock(x, y, z), worldObj.getBlockMetadata(x, y, z))) return i;
        }
        return -1;
    }

    private boolean validBlock(Block block, int metadata) {
        return block == Blocks.log && BlockLog.func_150165_c(metadata) == 3;
    }
}
