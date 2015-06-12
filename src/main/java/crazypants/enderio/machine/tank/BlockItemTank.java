package crazypants.enderio.machine.tank;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.gui.IAdvancedTooltipProvider;

public class BlockItemTank extends ItemBlockWithMetadata implements IAdvancedTooltipProvider, IFluidContainerItem {

  public BlockItemTank() {
    super(EnderIO.blockTank,EnderIO.blockTank);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  public BlockItemTank(Block block) {
    super(block, block);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    String result = super.getUnlocalizedName(par1ItemStack);
    if(meta == 1) {
      result += ".advanced";
    }
    return result;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    ItemStack stack = new ItemStack(this, 1,0);
    par3List.add(stack);
    stack = new ItemStack(this, 1,1);
    par3List.add(stack);
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    EnderIO.blockTank.addCommonEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    EnderIO.blockTank.addBasicEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    EnderIO.blockTank.addDetailedEntries(itemstack, entityplayer, list, flag);
  }

  public TileTank getDetachedTileEntity(ItemStack item) {
    if (item != null && item.getItem() instanceof BlockItemTank) {
      TileTank tt = new TileTank(item.getItemDamage());
      tt.readFromItemStack(item);
      return tt;
    }
    return null;
  }

  public void saveDetachedTileEntity(ItemStack item, TileTank tt) {
    if (item != null && tt != null) {
      tt.writeToItemStack(item);
    }
  }

  @Override
  public FluidStack getFluid(ItemStack container) {
    TileTank tt = getDetachedTileEntity(container);
    if (tt != null) {
      FluidTank[] outputTanks = tt.getOutputTanks();
      if (outputTanks.length >= 1 && outputTanks[0] != null) {
        return outputTanks[0].getFluid();
      }
    }
    return null;
  }

  @Override
  public int getCapacity(ItemStack container) {
    TileTank tt = getDetachedTileEntity(container);
    if (tt != null) {
      FluidTank[] outputTanks = tt.getOutputTanks();
      if (outputTanks.length >= 1 && outputTanks[0] != null) {
        return outputTanks[0].getCapacity();
      }
    }
    return 0;
  }

  @Override
  public int fill(ItemStack container, FluidStack resource, boolean doFill) {
    TileTank tt = getDetachedTileEntity(container);
    if (tt != null) {
      int result = tt.fill(ForgeDirection.UNKNOWN, resource, doFill);
      if (doFill) {
        saveDetachedTileEntity(container, tt);
      }
      return result;
    }
    return 0;
  }

  @Override
  public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
    TileTank tt = getDetachedTileEntity(container);
    if (tt != null) {
      FluidStack result = tt.drain(ForgeDirection.UNKNOWN, maxDrain, doDrain);
      if (doDrain) {
        saveDetachedTileEntity(container, tt);
      }
      return result;
    }
    return null;
  }

}
