package crazypants.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import com.enderio.core.api.common.util.IFluidReceptor;
import com.enderio.core.api.common.util.IItemReceptor;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.ItemUtil;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;

public class BuildcraftUtil implements IItemReceptor, IFluidReceptor {

    static {
        // Only register if we can actually load the IPipeTile class
        try {
            Class.forName("buildcraft.api.transport.IPipeTile");
            BuildcraftUtil instance = new BuildcraftUtil();
            ItemUtil.receptors.add(instance);
            FluidUtil.fluidReceptors.add(instance);
        } catch (Exception e) {

        }
    }

    @Override
    public int doInsertItem(Object into, ItemStack item, ForgeDirection side) {
        if (into instanceof IPipeTile) {
            return ((IPipeTile) into).injectItem(item, true, side);
        }
        return 0;
    }

    @Override
    public boolean canInsertIntoObject(Object into, ForgeDirection side) {
        if (into instanceof IPipeTile) {
            IPipeTile pipe = (IPipeTile) into;
            return pipe.getPipeType() == PipeType.ITEM;
        }
        return false;
    }

    @Override
    public boolean isValidReceptor(IFluidHandler handler) {
        if (handler instanceof IPipeTile) {
            IPipeTile pipe = (IPipeTile) handler;
            return pipe.getPipeType() == PipeType.FLUID;
        }
        return true;
    }
}
