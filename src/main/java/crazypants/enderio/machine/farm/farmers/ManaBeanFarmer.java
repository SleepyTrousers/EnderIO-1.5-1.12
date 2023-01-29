package crazypants.enderio.machine.farm.farmers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.enderio.core.common.util.BlockCoord;

public class ManaBeanFarmer extends CustomSeedFarmer {

    public ManaBeanFarmer(Block block, ItemStack stack) {
        super(block, stack);
        this.requiresFarmland = false;
    }

    protected boolean canPlant(World worldObj, BlockCoord bc) {
        return getPlantedBlock().canPlaceBlockOnSide(worldObj, bc.x, bc.y, bc.z, 0);
    }
}
