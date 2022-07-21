package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ManaBeanFarmer extends CustomSeedFarmer {
    public ManaBeanFarmer(Block block, ItemStack stack) {
        super(block, stack);
        this.requiresFarmland = false;
    }

    protected boolean canPlant(World worldObj, BlockCoord bc) {
        return getPlantedBlock().canPlaceBlockOnSide(worldObj, bc.x, bc.y, bc.z, 0);
    }
}
