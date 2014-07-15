package crazypants.enderio.machine.enchanter;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.network.IGuiHandler;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.machine.vacuum.BlockVacuumChest;
import crazypants.enderio.machine.vacuum.ContainerVacuumChest;
import crazypants.enderio.machine.vacuum.GuiVacuumChest;
import crazypants.enderio.machine.vacuum.TileVacuumChest;
import crazypants.util.Util;

public class BlockEnchanter extends BlockEio implements IGuiHandler, IResourceTooltipProvider {

  public static BlockEnchanter create() {
    BlockEnchanter res = new BlockEnchanter();
    res.init();
    return res;
  }

  public static int renderId;

  protected BlockEnchanter() {
    super(ModObject.blockEnchanter.unlocalisedName, TileEnchanter.class);
    setBlockTextureName("enderio:blockEnchanter");
  }
  
  @Override
  protected void init() {  
    super.init();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_ENCHANTER, this);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {

    if(ConduitUtil.isToolEquipped(entityPlayer)) {
      if(entityPlayer.isSneaking() && entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
        IToolWrench wrench = (IToolWrench) entityPlayer.getCurrentEquippedItem().getItem();
        if(wrench.canWrench(entityPlayer, x, y, z)) {
          removedByPlayer(world, entityPlayer, x, y, z, false);
          if(entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
            ((IToolWrench) entityPlayer.getCurrentEquippedItem().getItem()).wrenchUsed(entityPlayer, x, y, z);
          }
          return true;
        }
      } 
    }
    if(entityPlayer.isSneaking()) {
      return false;
    }
    entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_ENCHANTER, world, x, y, z);
    return true;
  }
  
  @Override
  public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvested) {
    if(!world.isRemote) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileVacuumChest) {
        TileEnchanter cb = (TileEnchanter) te;
        if(!player.capabilities.isCreativeMode) {
          ItemStack itemStack = new ItemStack(this);          
          float f = 0.7F;
          double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
          double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
          double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
          EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
          entityitem.delayBeforeCanPickup = 10;
          world.spawnEntityInWorld(entityitem);
          
          Util.dropItems(world, cb, x, y, z, true);
        }
      }
    }
    return super.removedByPlayer(world, player, x, y, z, harvested);
  }

  @Override
  public int quantityDropped(Random p_149745_1_) {    
    return 0;
  }

  @Override
  public int getRenderType() {    
    return renderId;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEnchanter) {
      return new ContainerEnchanter(player, player.inventory, (TileEnchanter) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEnchanter) {
      return new GuiEnchanter(player, player.inventory, (TileEnchanter) te);
    }
    return null;
  }

  public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_) {
    super.breakBlock(world, x, y, z, block, p_149749_6_);
    world.removeTileEntity(x, y, z);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
