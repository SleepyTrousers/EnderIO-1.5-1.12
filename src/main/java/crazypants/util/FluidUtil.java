package crazypants.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import crazypants.enderio.conduit.IConduitBundle;

public class FluidUtil {

  public static Map<ForgeDirection, IFluidHandler> getNeighbouringFluidHandlers(IBlockAccess world, BlockCoord bc) {
    Map<ForgeDirection, IFluidHandler> res = new HashMap<ForgeDirection, IFluidHandler>();
    for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      IFluidHandler fh = getFluidHandler(world, bc.getLocation(dir));
      if(fh != null) {
        res.put(dir, fh);
      }
    }
    return res;
  }
  
  public static IFluidHandler getExternalFluidHandler(IBlockAccess world, BlockCoord bc) {
    IFluidHandler con = getFluidHandler(world, bc);
    return (con != null && !(con instanceof IConduitBundle)) ? con : null;
  }  
    
  public static IFluidHandler getFluidHandler(IBlockAccess world, BlockCoord bc) {
    return getFluidHandler(world, bc.x, bc.y, bc.z);
  }

  public static IFluidHandler getFluidHandler(IBlockAccess world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    return getFluidHandler(te);
  }

  public static IFluidHandler getFluidHandler(TileEntity te) {
    if(te instanceof IFluidHandler) {
      if(te instanceof IPipeTile) {
        if(((IPipeTile) te).getPipeType() != PipeType.FLUID) {
          return null;
        }
      }
      return (IFluidHandler) te;
    }
    return null;
  }
  
  public static FluidStack getFluidFromItem(ItemStack item) {
    FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(item);
    if(fluid == null) {
      if(item.getItem() == Items.water_bucket) {
        fluid = new FluidStack(FluidRegistry.WATER, 1000);
      } else if(item.getItem() == Items.lava_bucket) {
        fluid = new FluidStack(FluidRegistry.LAVA, 1000);
      }
    }
    return fluid;
  }
  
  public static ItemStack getEmptyContainer(ItemStack stack) {
    if(stack.getItem().hasContainerItem(stack)) {
      return stack.getItem().getContainerItem(stack);
    } 
    else if (stack.getItem() instanceof ItemPotion && stack.stackTagCompound == null) {
        return new ItemStack(Items.glass_bottle);
    }
    else {
      return null;
    }
  }



}
