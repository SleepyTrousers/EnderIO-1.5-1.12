package crazypants.enderio.machine.still;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.util.ItemUtil;
import crazypants.util.Util;

public class BlockVat extends AbstractMachineBlock<TileVat> {

  public static BlockVat create() {
    BlockVat res = new BlockVat();
    res.init();
    return res;
  }

  public BlockVat() {
    super(ModObject.blockVat, TileVat.class);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {

    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileVat)) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    TileVat vat = (TileVat) te;
    ItemStack item = entityPlayer.inventory.getCurrentItem();
    if(item == null) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    //check for filled fluid containers and see if we can empty them into our input tank
    FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(item);
    if(fluid == null) {
      if(item.getItem() == Items.water_bucket) {
        fluid = new FluidStack(FluidRegistry.WATER, 1000);
      } else if(item.getItem() == Items.lava_bucket) {
        fluid = new FluidStack(FluidRegistry.LAVA, 1000);
      }
    }

    if(fluid != null) {
      int filled = vat.fill(ForgeDirection.UP, fluid, false);
      if(filled >= fluid.amount) {
        vat.fill(ForgeDirection.UP, fluid, true);
        if(!entityPlayer.capabilities.isCreativeMode) {
          entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, Util.consumeItem(item));
        }
        return true;
      }
    }

    //now check for empty fluid containers to fill
    FluidStack available = vat.outputTank.getFluid();
    if(available != null) {
      ItemStack res = FluidContainerRegistry.fillFluidContainer(available.copy(), item);
      FluidStack filled = FluidContainerRegistry.getFluidForFilledItem(res);

      if(filled == null) { //this shouldn't be necessary but it appears to be a bug as the above method doesnt work
        FluidContainerData[] datas = FluidContainerRegistry.getRegisteredFluidContainerData();
        for (FluidContainerData data : datas) {
          if(data.fluid.getFluid().getName().equals(available.getFluid().getName()) && data.emptyContainer.isItemEqual(item)) {
            res = data.filledContainer.copy();
            filled = FluidContainerRegistry.getFluidForFilledItem(res);
          }
        }
      }

      if(filled != null) {
        vat.drain(ForgeDirection.DOWN, filled, true);
        if(item.stackSize > 1) {
          item.stackSize--;
          entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, item);
          for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
            if(entityPlayer.inventory.mainInventory[i] == null) {
              entityPlayer.inventory.setInventorySlotContents(i, res);              
              return true;
            }            
          }
          if(!world.isRemote) {
            Util.dropItems(world, res, x, y, z, true);
          }

        } else {
          entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, res);
        }

        return true;
      }
    }

    return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
  }
  

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileVat) {
      return new ContainerVat(player.inventory, (TileVat) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileVat) {
      return new GuiVat(player.inventory, (TileVat) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_STILL;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:stillFrontOn";
    }
    return "enderio:stillFront";
  }

}
