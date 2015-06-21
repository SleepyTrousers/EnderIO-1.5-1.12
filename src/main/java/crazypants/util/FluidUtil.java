package crazypants.util;

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
import net.minecraftforge.fluids.IFluidTank;
import cpw.mods.fml.common.Loader;
import crazypants.enderio.Log;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.config.Config;

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

  public static FluidAndStackResult tryFillContainer(ItemStack target, FluidStack source) {
    if (target != null && target.getItem() != null && source != null && source.getFluid() != null && source.amount > 0) {

      if (target.getItem() instanceof IFluidContainerItem) {
        ItemStack resultStack = target.copy();
        resultStack.stackSize = 1;
        int amount = ((IFluidContainerItem) target.getItem()).fill(resultStack, source, true);
        if (amount <= 0) {
          return new FluidAndStackResult(null, null, target, source);
        }
        FluidStack resultFluid = source.copy();
        resultFluid.amount = amount;
        ItemStack remainderStack = target.copy();
        remainderStack.stackSize--;
        if (remainderStack.stackSize <= 0) {
          remainderStack = null;
        }
        FluidStack remainderFluid = source.copy();
        remainderFluid.amount -= amount;
        if (remainderFluid.amount <= 0) {
          remainderFluid = null;
        }
        return new FluidAndStackResult(resultStack, resultFluid, remainderStack, remainderFluid);
      }

      ItemStack resultStack = FluidContainerRegistry.fillFluidContainer(source.copy(), target);
      if (resultStack != null) {
        FluidStack resultFluid = FluidContainerRegistry.getFluidForFilledItem(resultStack);
        if (resultFluid != null) {
          ItemStack remainderStack = target.copy();
          remainderStack.stackSize--;
          if (remainderStack.stackSize <= 0) {
            remainderStack = null;
          }
          FluidStack remainderFluid = source.copy();
          remainderFluid.amount -= resultFluid.amount;
          if (remainderFluid.amount <= 0) {
            remainderFluid = null;
          }
          return new FluidAndStackResult(resultStack, resultFluid, remainderStack, remainderFluid);
        }
      }

    }
    return new FluidAndStackResult(null, null, target, source);
  }

  public static FluidAndStackResult tryDrainContainer(ItemStack source, FluidStack target, int capacity) {
    if (source != null && source.getItem() != null) {

      int maxDrain = capacity - (target != null ? target.amount : 0);
      if (source.getItem() instanceof IFluidContainerItem) {
        ItemStack resultStack = source.copy();
        resultStack.stackSize = 1;
        FluidStack resultFluid = ((IFluidContainerItem) source.getItem()).drain(resultStack, maxDrain, true);
        if (resultFluid == null || resultFluid.amount <= 0 || (target != null && resultFluid.getFluid() != target.getFluid())) {
          return new FluidAndStackResult(null, null, source, target);
        }
        ItemStack remainderStack = source.copy();
        remainderStack.stackSize--;
        if (remainderStack.stackSize <= 0) {
          remainderStack = null;
        }
        FluidStack remainderFluid = target != null ? target.copy() : new FluidStack(resultFluid.getFluid(), 0);
        remainderFluid.amount += resultFluid.amount;
        return new FluidAndStackResult(resultStack, resultFluid, remainderStack, remainderFluid);
      }

      FluidStack resultFluid = FluidContainerRegistry.getFluidForFilledItem(source);
      if (resultFluid != null && resultFluid.amount > 0 && resultFluid.amount <= maxDrain
          && (target == null || resultFluid.getFluid() == target.getFluid())) {
        ItemStack resultStack = source.getItem().getContainerItem(source);
        if (resultStack == null && Config.enableWaterFromBottles && source.getItem() instanceof ItemPotion
            && source.stackTagCompound == null) {
          resultStack = new ItemStack(Items.glass_bottle);
        }
        ItemStack remainderStack = source.copy();
        remainderStack.stackSize--;
        if (remainderStack.stackSize <= 0) {
          remainderStack = null;
        }
        FluidStack remainderFluid = target != null ? target.copy() : new FluidStack(resultFluid.getFluid(), 0);
        remainderFluid.amount += resultFluid.amount;
        return new FluidAndStackResult(resultStack, resultFluid, remainderStack, remainderFluid);
      }


    }
    return new FluidAndStackResult(null, null, source, target);
  }

  public static FluidAndStackResult tryDrainContainer(ItemStack source, ITankAccess tank) {
    if (source != null && source.getItem() != null) {

      if (source.getItem() instanceof IFluidContainerItem) {
        FluidStack fluid = ((IFluidContainerItem) source.getItem()).getFluid(source);
        IFluidTank targetTank = fluid != null ? tank.getInputTank(fluid) : null;
        if (targetTank != null) {
          FluidStack target = targetTank.getFluid();
          int maxDrain = targetTank.getCapacity() - (target != null ? target.amount : 0);
          ItemStack resultStack = source.copy();
          resultStack.stackSize = 1;
          FluidStack resultFluid = ((IFluidContainerItem) source.getItem()).drain(resultStack, maxDrain, true);
          if (resultFluid == null || resultFluid.amount <= 0 || (target != null && resultFluid.getFluid() != target.getFluid())) {
            return new FluidAndStackResult(null, null, source, target);
          }
          ItemStack remainderStack = source.copy();
          remainderStack.stackSize--;
          if (remainderStack.stackSize <= 0) {
            remainderStack = null;
          }
          FluidStack remainderFluid = target != null ? target.copy() : new FluidStack(resultFluid.getFluid(), 0);
          remainderFluid.amount += resultFluid.amount;
          return new FluidAndStackResult(resultStack, resultFluid, remainderStack, remainderFluid);
        }
      }

      FluidStack resultFluid = FluidContainerRegistry.getFluidForFilledItem(source);
      if (resultFluid != null && resultFluid.amount > 0) {
        IFluidTank targetTank = tank.getInputTank(resultFluid);
        if (targetTank != null) {
          FluidStack target = targetTank.getFluid();
          int maxDrain = targetTank.getCapacity() - (target != null ? target.amount : 0);
          if (resultFluid.amount <= maxDrain && (target == null || resultFluid.getFluid() == target.getFluid())) {
            ItemStack resultStack = source.getItem().getContainerItem(source);
            if (resultStack == null && Config.enableWaterFromBottles && source.getItem() instanceof ItemPotion
                && source.stackTagCompound == null) {
              resultStack = new ItemStack(Items.glass_bottle);
            }
            ItemStack remainderStack = source.copy();
            remainderStack.stackSize--;
            if (remainderStack.stackSize <= 0) {
              remainderStack = null;
            }
            FluidStack remainderFluid = target != null ? target.copy() : new FluidStack(resultFluid.getFluid(), 0);
            remainderFluid.amount += resultFluid.amount;
            return new FluidAndStackResult(resultStack, resultFluid, remainderStack, remainderFluid);
          }
        }
      }

    }
    return new FluidAndStackResult(null, null, source, null);
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

    for (FluidTank subTank : tank.getOutputTanks()) {
      FluidAndStackResult fill = tryFillContainer(entityPlayer.inventory.getCurrentItem(), subTank.getFluid());
      if(fill.result.fluidStack != null) {

        subTank.setFluid(fill.remainder.fluidStack);
        tank.setTanksDirty();
        if(!entityPlayer.capabilities.isCreativeMode) {
          if(fill.remainder.itemStack == null) {
            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, fill.result.itemStack);
            return true;
          } else {
            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, fill.remainder.itemStack);
          }

          if(fill.result.itemStack.isStackable()) {
            for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
              ItemStack inventoryItem = entityPlayer.inventory.mainInventory[i];
              if(ItemUtil.areStackMergable(inventoryItem, fill.result.itemStack) && inventoryItem.stackSize < inventoryItem.getMaxStackSize()) {
                fill.result.itemStack.stackSize += inventoryItem.stackSize;
                entityPlayer.inventory.setInventorySlotContents(i, fill.result.itemStack);
                return true;
              }
            }
          }

          for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
            if(entityPlayer.inventory.mainInventory[i] == null) {
              entityPlayer.inventory.setInventorySlotContents(i, fill.result.itemStack);
              return true;
            }
          }

          if(!world.isRemote) {
            double x0 = (x + entityPlayer.posX) / 2.0D;
            double y0 = (y + entityPlayer.posY) / 2.0D + 0.5D;
            double z0 = (z + entityPlayer.posZ) / 2.0D;
            Util.dropItems(world, fill.result.itemStack, x0, y0, z0, true);
          }
        }

        return true;
      }
    }
    return false;
  }

  public static boolean fillInternalTankFromPlayerHandItem(World world, int x, int y, int z, EntityPlayer entityPlayer,
      ITankAccess tank) {
    FluidAndStackResult fill = tryDrainContainer(entityPlayer.inventory.getCurrentItem(), tank);
    if(fill.result.fluidStack == null) {
      return false;
    }

    tank.getInputTank(fill.result.fluidStack).setFluid(fill.remainder.fluidStack);
    tank.setTanksDirty();

    if(!entityPlayer.capabilities.isCreativeMode) {
      if(fill.remainder.itemStack == null) {
        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, fill.result.itemStack);
        return true;
      } else {
        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, fill.remainder.itemStack);
      }

      if(fill.result.itemStack.isStackable()) {
        for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
          ItemStack inventoryItem = entityPlayer.inventory.mainInventory[i];
          if(ItemUtil.areStackMergable(inventoryItem, fill.result.itemStack) && inventoryItem.stackSize < inventoryItem.getMaxStackSize()) {
            fill.result.itemStack.stackSize += inventoryItem.stackSize;
            entityPlayer.inventory.setInventorySlotContents(i, fill.result.itemStack);
            return true;
          }
        }
      }

      for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
        if(entityPlayer.inventory.mainInventory[i] == null) {
          entityPlayer.inventory.setInventorySlotContents(i, fill.result.itemStack);
          return true;
        }
      }

      if(!world.isRemote) {
        double x0 = (x + entityPlayer.posX) / 2.0D;
        double y0 = (y + entityPlayer.posY) / 2.0D + 0.5D;
        double z0 = (z + entityPlayer.posZ) / 2.0D;
        Util.dropItems(world, fill.result.itemStack, x0, y0, z0, true);
      }
    }

    return true;
  }

  public static class FluidAndStack {
    public final FluidStack fluidStack;
    public final ItemStack itemStack;

    public FluidAndStack(FluidStack fluidStack, ItemStack itemStack) {
      this.fluidStack = fluidStack;
      this.itemStack = itemStack;
    }

    public FluidAndStack(ItemStack itemStack, FluidStack fluidStack) {
      this.fluidStack = fluidStack;
      this.itemStack = itemStack;
    }
  }

  public static class FluidAndStackResult {
    public final FluidAndStack result;
    public final FluidAndStack remainder;

    public FluidAndStackResult(FluidAndStack result, FluidAndStack remainder) {
      this.result = result;
      this.remainder = remainder;
    }

    public FluidAndStackResult(FluidStack fluidStackResult, ItemStack itemStackResult, FluidStack fluidStackRemainder,
        ItemStack itemStackRemainder) {
      this.result = new FluidAndStack(fluidStackResult, itemStackResult);
      this.remainder = new FluidAndStack(fluidStackRemainder, itemStackRemainder);
    }

    public FluidAndStackResult(ItemStack itemStackResult, FluidStack fluidStackResult, ItemStack itemStackRemainder,
        FluidStack fluidStackRemainder) {
      this.result = new FluidAndStack(fluidStackResult, itemStackResult);
      this.remainder = new FluidAndStack(fluidStackRemainder, itemStackRemainder);
    }
  }

}
