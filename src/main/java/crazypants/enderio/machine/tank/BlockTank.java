package crazypants.enderio.machine.tank;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.util.FluidUtil;
import crazypants.util.Util;

public class BlockTank extends AbstractMachineBlock<TileTank> {

  public static BlockTank create() {
   
    BlockTank res = new BlockTank();
    res.init();
    return res;
  }

  protected BlockTank() {
    super(ModObject.blockTank, TileTank.class);      
    setStepSound(Block.soundTypeGlass);
  }

  @Override
  protected void init() {
    super.init();
    setLightOpacity(0);
  }
  
  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {

    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileTank)) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    TileTank tank = (TileTank) te;
    ItemStack item = entityPlayer.inventory.getCurrentItem();
    if(item == null) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    //check for filled fluid containers and see if we can empty them into our input tank
    FluidStack fluid = FluidUtil.getFluidFromItem(item);
    if(fluid != null) {
      int filled = tank.fill(ForgeDirection.UP, fluid, false);
      if(filled >= fluid.amount) {
        tank.fill(ForgeDirection.UP, fluid, true);
        if(!entityPlayer.capabilities.isCreativeMode) {
          entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, Util.consumeItem(item));
        }
        return true;
      }
    }

    //now check for empty fluid containers to fill
    FluidStack available = tank.tank.getFluid();
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
        tank.drain(ForgeDirection.DOWN, filled, true);
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
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileTank)) {
      System.out.println("BlockTank.getServerGuiElement: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      return null;
    }
    return new ContainerTank(player.inventory, (TileTank)te);
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileTank)) {
      System.out.println("BlockTank.getClientGuiElement: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      return null;
    }
    return new GuiTank(player.inventory, (TileTank)te);
  }

  //Causes crashes in 1.7 on some machines
  //@Override
  //@SideOnly(Side.CLIENT)
  //public int getRenderBlockPass() {
  //  return 1;
  //}
  //
  //@Override
  //public boolean canRenderInPass(int pass) {
  //  return pass == 1;
  //}

@Override
public boolean isOpaqueCube() {
  return false;
}
  
  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_TANK;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return "enderio:blockTank";
  }
  
  protected String getSideIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }
  
  protected String getBackIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }
  
}
