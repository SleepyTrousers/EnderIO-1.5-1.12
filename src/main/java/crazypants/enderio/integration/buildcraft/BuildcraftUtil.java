package crazypants.enderio.integration.buildcraft;

import com.enderio.core.api.common.util.IFluidReceptor;
import com.enderio.core.api.common.util.IItemReceptor;
import com.enderio.core.common.util.ItemUtil;

import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.IFluidHandler;

public class BuildcraftUtil implements IItemReceptor, IFluidReceptor {

  static {
    //Only register if we can actually load the IPipeTile class
    try {
      Class.forName("buildcraft.api.transport.IPipeTile");
      BuildcraftUtil instance = new BuildcraftUtil();
      ItemUtil.receptors.add(instance);
      //TODO: Mod BC might not be needed if BC supports capabilities in 1.10
//      FluidUtil.fluidReceptors.add(instance);
    } catch (Exception e) {

    }
  }

  @Override
  public int doInsertItem(Object into, ItemStack item, EnumFacing side) {
    if(into instanceof IPipeTile) {
      return ((IPipeTile) into).injectItem(item, true, side, null);
    }
    return 0;
  }

  @Override
  public boolean canInsertIntoObject(Object into, EnumFacing side) {
    if(into instanceof IPipeTile) {
      IPipeTile pipe = (IPipeTile) into;
      return pipe.getPipeType() == PipeType.ITEM;
    }
    return false;
  }

  @Override
  public boolean isValidReceptor(IFluidHandler handler) {
    if(handler instanceof IPipeTile) {
      IPipeTile pipe = (IPipeTile) handler;
      return pipe.getPipeType() == PipeType.FLUID;
    }
    return true;
  }

}
