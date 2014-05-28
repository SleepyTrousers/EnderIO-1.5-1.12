package crazypants.enderio.machine.tank;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.ClientProxy;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.power.BlockItemCapacitorBank;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.power.TileCapacitorBank;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.FluidUtil;
import crazypants.util.Lang;
import crazypants.util.Util;

public class BlockTank extends AbstractMachineBlock<TileTank> implements IAdvancedTooltipProvider {

  public static BlockTank create() {
   EnderIO.packetPipeline.registerPacket(PacketTank.class);
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
    GameRegistry.registerBlock(this, BlockItemTank.class, ModObject.blockTank.unlocalisedName);
    if(teClass != null) {
      GameRegistry.registerTileEntity(teClass, ModObject.blockTank.unlocalisedName + "TileEntity");
    }
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TANK, this);
    setLightOpacity(0);
  }
  
  @Override
  public int damageDropped(int par1) {
    return par1;
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
  public TileEntity createNewTileEntity(World var1, int var2) {
    return new TileTank(var2);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileTank)) {
      return null;
    }
    return new ContainerTank(player.inventory, (TileTank)te);
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileTank)) {
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
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {

    // used to render the block in the world
    TileEntity te = world.getTileEntity(x, y, z);
    int facing = 0;
    if(te instanceof AbstractMachineEntity) {
      AbstractMachineEntity me = (AbstractMachineEntity) te;
      facing = me.facing;
    }
    int meta = world.getBlockMetadata(x, y, z);
    meta = MathHelper.clamp_int(meta, 0, 1);
    if(meta == 1) {
      return iconBuffer[0][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing] + 6];
    } else {
      return iconBuffer[0][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing]];
    }
  }

  @Override
  public IIcon getIcon(int blockSide, int blockMeta) {
    int offset = MathHelper.clamp_int(blockMeta, 0, 1) == 0 ? 0 : 6;
    return iconBuffer[0][blockSide + offset];
  }
  

  @Override
  protected String getMachineFrontIconKey(boolean pressurized) {
    if(pressurized) {
      return "enderio:blockTankAdvanced";
    }
    return "enderio:blockTank";
  }
  
  protected String getSideIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }
  
  protected String getBackIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }
    
  @Override
  protected String getTopIconKey(boolean pressurized) {
    if(pressurized) { 
      return "enderio:blockTankTopAdvanced";
    }
    return "enderio:machineTop";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }

  @Override
  public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
    int meta = world.getBlockMetadata(x, y, z);
    meta = MathHelper.clamp_int(meta, 0, 1);
    if(meta == 1) {
      return 2000;
    } else {
      return super.getExplosionResistance(par1Entity);
    }
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {    
    if(itemstack.stackTagCompound != null && itemstack.stackTagCompound.hasKey("tankContents")) {    
      FluidStack fl = FluidStack.loadFluidStackFromNBT((NBTTagCompound) itemstack.stackTagCompound.getTag("tankContents"));
      if(fl != null && fl.getFluid() != null) {
        String str = fl.amount + " " + Lang.localize("fluid.millibucket.abr") + " " + PowerDisplayUtil.ofStr() + " " + fl.getFluid().getLocalizedName();
        list.add(str);        
      } 
    }  
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    TooltipAddera.addDetailedTooltipFromResources(list, itemstack);
    if(itemstack.getItemDamage() == 1) {
      list.add(EnumChatFormatting.ITALIC + Lang.localize("blastResistant"));
    }
  }
  
  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    System.out.println("BlockTank.getUnlocalizedNameForTooltip: ");
    return stack.getUnlocalizedName();
  }
  
}
