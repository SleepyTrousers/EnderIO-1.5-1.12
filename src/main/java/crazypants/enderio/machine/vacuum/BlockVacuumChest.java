package crazypants.enderio.machine.vacuum;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.tool.ToolUtil;

public class BlockVacuumChest extends BlockEio implements IGuiHandler, IResourceTooltipProvider {

  public static BlockVacuumChest create() {
    BlockVacuumChest res = new BlockVacuumChest();
    res.init();
    return res;
  }

  public static int renderId;

  protected BlockVacuumChest() {
    super(ModObject.blockVacuumChest.unlocalisedName, TileVacuumChest.class);
    setBlockTextureName("enderio:blockVacuumChest");
  }

  @Override
  protected void init() {
    super.init();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_VACUUM_CHEST, this);
  }

  @Override
  protected boolean openGui(World world, int x, int y, int z, EntityPlayer entityPlayer, int side) {
    if(!world.isRemote) {
      entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_VACUUM_CHEST, world, x, y, z);
    }    return super.openGui(world, x, y, z, entityPlayer, side);
  }

  @Override
  public boolean doNormalDrops(World world, int x, int y, int z) {
    return false;
  }

  @Override
  protected void processDrop(World world, int x, int y, int z, TileEntityEio te, ItemStack drop) {
    drop.stackTagCompound = new NBTTagCompound();
    if(te != null) {
      ((TileVacuumChest) te).writeContentsToNBT(drop.stackTagCompound);
    }
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placedBy, ItemStack stack) {
    if(!world.isRemote) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(stack != null && stack.stackTagCompound != null && te instanceof TileVacuumChest) {
        ((TileVacuumChest) te).readContentsFromNBT(stack.stackTagCompound);
        world.markBlockForUpdate(x, y, z);
      }
    }
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
    if(te instanceof TileVacuumChest) {
      return new ContainerVacuumChest(player, player.inventory, (TileVacuumChest) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileVacuumChest) {
      return new GuiVacuumChest(player, player.inventory, (TileVacuumChest) te);
    }
    return null;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
