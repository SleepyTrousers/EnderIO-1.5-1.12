package crazypants.util;

import static crazypants.util.FluidUtil.isValidFluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import cpw.mods.fml.common.Loader;
import crazypants.enderio.Log;
import crazypants.enderio.conduit.IConduitBundle;

public class FluidUtil {

  public static final List<IFluidReceptor> fluidReceptors = new ArrayList<IFluidReceptor>();

  static {
    try {
      Class.forName("crazypants.util.BuildcraftUtil");
    } catch (Exception e) {
      if(Loader.isModLoaded("BuildCraft|Transport")) {
        Log.warn("ItemUtil: Could not register Build Craft pipe handler. Fluid conduits will show connections to all Build Craft pipes.");
      } //Don't log if BC isn't installed, but we still check in case another mod is using their API
    }
  }

  public static Map<ForgeDirection, IFluidHandler> getNeighbouringFluidHandlers(IBlockAccess world, BlockCoord bc) {
    Map<ForgeDirection, IFluidHandler> res = new HashMap<ForgeDirection, IFluidHandler>();
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
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
      IFluidHandler res = (IFluidHandler) te;
      for (IFluidReceptor rec : fluidReceptors) {
        if(!rec.isValidReceptor(res)) {
          return null;
        }
      }
      return res;
    }
    return null;
  }

  public static FluidStack getFluidFromItem(ItemStack stack) {
    if(stack != null) {
      FluidStack fluidStack = null;
      if(stack.getItem() instanceof IFluidContainerItem) {
        fluidStack = ((IFluidContainerItem) stack.getItem()).getFluid(stack);
      }
      if(fluidStack == null) {
        fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
      }
      if(fluidStack == null && Block.getBlockFromItem(stack.getItem()) instanceof IFluidBlock) {
        Fluid fluid = ((IFluidBlock) Block.getBlockFromItem(stack.getItem())).getFluid();
        if(fluid != null) {
          return new FluidStack(fluid, 1000);
        }
      }
      return fluidStack;
    }
    return null;
  }

  public static ItemStack getEmptyContainer(ItemStack stack) {
    if(stack.getItem().hasContainerItem(stack)) {
      return stack.getItem().getContainerItem(stack);
    }
    else if(stack.getItem() instanceof ItemPotion && stack.stackTagCompound == null) {
      return new ItemStack(Items.glass_bottle);
    }
    else {
      return null;
    }
  }

  public static boolean doPull(IFluidHandler into, ForgeDirection fromDir, int maxVolume) {
    TileEntity te = (TileEntity) into;
    BlockCoord loc = new BlockCoord(te).getLocation(fromDir);
    IFluidHandler target = FluidUtil.getFluidHandler(te.getWorldObj(), loc);
    if(target != null) {
      FluidTankInfo[] infos = target.getTankInfo(fromDir.getOpposite());
      if(infos != null) {
        for (FluidTankInfo info : infos) {
          if(info.fluid != null && info.fluid.amount > 0) {
            if(into.canFill(fromDir, info.fluid.getFluid())) {
              FluidStack canPull = info.fluid.copy();
              canPull.amount = Math.min(maxVolume, canPull.amount);
              FluidStack drained = target.drain(fromDir.getOpposite(), canPull, false);
              if(drained != null && drained.amount > 0) {
                int filled = into.fill(fromDir, drained, false);
                if(filled > 0) {
                  drained = target.drain(fromDir.getOpposite(), filled, true);
                  into.fill(fromDir, drained, true);
                  return true;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }

  public static boolean doPush(IFluidHandler from, ForgeDirection fromDir, int maxVolume) {

    TileEntity te = (TileEntity) from;
    BlockCoord loc = new BlockCoord(te).getLocation(fromDir);
    IFluidHandler target = getFluidHandler(te.getWorldObj(), loc);
    if(target == null) {
      return false;
    }
    FluidTankInfo[] infos = from.getTankInfo(fromDir);
    boolean res = false;
    if(infos != null) {
      for (FluidTankInfo info : infos) {
        if(info.fluid != null && info.fluid.amount > 0 && from.canDrain(fromDir, info.fluid.getFluid())) {
          FluidStack maxDrain = new FluidStack(info.fluid.getFluid(), maxVolume);
          FluidStack canDrain = from.drain(fromDir, maxDrain, false);
          if(canDrain != null && canDrain.amount > 0) {
            int filled = target.fill(fromDir.getOpposite(), canDrain, true);
            from.drain(fromDir, new FluidStack(info.fluid.getFluid(), filled), true);
            res |= true;
          }
        }
      }
    }
    return res;
  }

  public static boolean isValidFluid(FluidStack fluidStack) {
    return fluidStack != null && fluidStack.getFluid() != null && fluidStack.getFluid().getName() != null;
  }

  public static ItemStack fillContainerFromInternalTank(ITankAccess tank, ItemStack item, boolean drain) {
    FluidStack available = tank.getOutputTank().getFluid();
    if (isValidFluid(available)) {
      ItemStack res = FluidContainerRegistry.fillFluidContainer(available.copy(), item);
      FluidStack filled = FluidContainerRegistry.getFluidForFilledItem(res);

      /*
       * We had some code here that replicated the two methods above if they did
       * not return a result. As far as *I* know, that is not needed (anymore?).
       * It works without it for vanilla buckets and Forestry cans. In the case
       * of issues like "cannot fill xxx anymore", that code needs to be
       * re-inserted here.
       */

      if (isValidFluid(filled) && (filled.amount <= available.amount)) {
        if (drain) {
          tank.getOutputTank().drain(filled.amount, true);
          tank.setTanksDirty();
        }
        return res;
      }
    }

    return null;
  }

  /**
   * If the currently held item of the given player can be filled with the
   * liquid in the given tank's output tank, do so and put the resultant filled
   * container item where it can go. This will also drain the tank and set it to
   * dirty.
   * 
   * <p>
   * Cases handled for the the filled container:
   * 
   * <ul>
   * <li>If the stacksize of the held item is one, then it will be replaced by
   * the filled container unless the player in in creative.
   * <li>If the filled container is stackable and the player already has a
   * non-maxed stack in the inventory, it is put there.
   * <li>If the player has space in his inventory, it is put there.
   * <li>Otherwise it will be dropped on the ground between the position given
   * as parameter and the player's position.
   * </ul>
   * 
   * @param world
   * @param x
   * @param y
   * @param z
   * @param entityPlayer
   * @param tank
   * @return true if a container was filled, false otherwise
   */
  public static boolean fillPlayerHandItemFromInternalTank(World world, int x, int y, int z, EntityPlayer entityPlayer,
      ITankAccess tank) {
    ItemStack item = entityPlayer.inventory.getCurrentItem();
    if (item == null || item.getItem() == null) {
      return false;
    }
    ItemStack filledContainer = fillContainerFromInternalTank(tank, item, !entityPlayer.capabilities.isCreativeMode);
    if (filledContainer == null) {
      return false;
    }

    if (item.stackSize == 1 && !entityPlayer.capabilities.isCreativeMode) {
      entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, filledContainer);
    } else {
      if (!entityPlayer.capabilities.isCreativeMode) {
        item.stackSize--;
        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, item);
      }
      if (filledContainer.isStackable()) {
        for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
          ItemStack inventoryItem = entityPlayer.inventory.mainInventory[i];
          if (ItemUtil.areStackMergable(inventoryItem, filledContainer)
              && inventoryItem.stackSize < inventoryItem.getMaxStackSize()) {
            filledContainer.stackSize += inventoryItem.stackSize;
            entityPlayer.inventory.setInventorySlotContents(i, filledContainer);
            return true;
          }
        }
      }
      for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
        if (entityPlayer.inventory.mainInventory[i] == null) {
          entityPlayer.inventory.setInventorySlotContents(i, filledContainer);
          return true;
        }
      }
      if (!world.isRemote) {
        double x0 = (x + entityPlayer.posX) / 2.0D;
        double y0 = (y + entityPlayer.posY) / 2.0D + 0.5D;
        double z0 = (z + entityPlayer.posZ) / 2.0D;
        Util.dropItems(world, filledContainer, x0, y0, z0, true);
      }
    }
    return true;
  }

}
