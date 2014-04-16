package crazypants.enderio.machine.power;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.enderface.BlockEio;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;
import crazypants.util.Util;
import crazypants.vecmath.Vector3d;

public class BlockCapacitorBank extends BlockEio implements IGuiHandler {

  public static int renderId = -1;

  public static BlockCapacitorBank create() {
    EnderIO.packetPipeline.registerPacket(PacketClientState.class);

    BlockCapacitorBank res = new BlockCapacitorBank();
    res.init();
    return res;
  }

  IIcon overlayIcon;
  IIcon fillBarIcon;

  private IIcon blockIconInput;
  private IIcon blockIconOutput;
  private IIcon blockIconLocked;

  protected BlockCapacitorBank() {
    super(ModObject.blockCapacitorBank.unlocalisedName, TileCapacitorBank.class);
    setHardness(2.0F);
  }

  @Override
  protected void init() {
    super.init();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_CAPACITOR_BANK, this);
    setLightOpacity(255);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List list) {

    ItemStack is = BlockItemCapacitorBank.createItemStackWithPower(0);
    list.add(is);
    is = BlockItemCapacitorBank.createItemStackWithPower(TileCapacitorBank.BASE_CAP.getMaxEnergyStored());
    list.add(is);

  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {

    if(ConduitUtil.isToolEquipped(entityPlayer) && entityPlayer.isSneaking()) {
      if(entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
        IToolWrench wrench = (IToolWrench) entityPlayer.getCurrentEquippedItem().getItem();
        if(wrench.canWrench(entityPlayer, x, y, z)) {
          removedByPlayer(world, entityPlayer, x, y, z);
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
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileCapacitorBank)) {
      return false;
    }

    if(ConduitUtil.isToolEquipped(entityPlayer)) {

      ForgeDirection faceHit = ForgeDirection.getOrientation(side);
      TileCapacitorBank tcb = (TileCapacitorBank) te;
      tcb.toggleModeForFace(faceHit);
      if(world.isRemote) {
        world.markBlockForUpdate(x, y, z);
      } else {
        world.notifyBlocksOfNeighborChange(x, y, z, EnderIO.blockCapacitorBank);
        world.markBlockForUpdate(x, y, z);
      }

      return true;
    }

    entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_CAPACITOR_BANK, world, x, y, z);
    return true;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileCapacitorBank) {
      return new ContainerCapacitorBank(player.inventory, ((TileCapacitorBank) te).getController());
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileCapacitorBank) {
      return new GuiCapacitorBank(player.inventory, ((TileCapacitorBank) te).getController());
    }
    return null;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void registerBlockIcons(IIconRegister IIconRegister) {
    blockIcon = IIconRegister.registerIcon("enderio:capacitorBank");
    blockIconInput = IIconRegister.registerIcon("enderio:capacitorBankInput");
    blockIconOutput = IIconRegister.registerIcon("enderio:capacitorBankOutput");
    blockIconLocked = IIconRegister.registerIcon("enderio:capacitorBankLocked");
    overlayIcon = IIconRegister.registerIcon("enderio:capacitorBankOverlays");
    fillBarIcon = IIconRegister.registerIcon("enderio:capacitorBankFillBar");
  }

  @Override
  public int getRenderType() {
    return renderId;
  }

  @Override
  public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    return true;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
    Block i1 = par1IBlockAccess.getBlock(par2, par3, par4);
    return i1 == this ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess ba, int x, int y, int z, int side) {
    TileEntity te = ba.getTileEntity(x, y, z);
    if(!(te instanceof TileCapacitorBank)) {
      return blockIcon;
    }
    TileCapacitorBank cb = (TileCapacitorBank) te;
    IoMode mode = cb.getFaceModeForFace(ForgeDirection.values()[side]);
    if(mode == null || mode == IoMode.NONE) {
      return blockIcon;
    }
    if(mode == IoMode.PULL) {
      return blockIconInput;
    }
    if(mode == IoMode.PUSH) {
      return blockIconOutput;
    }
    return blockIconLocked;
  }

  @Override
  public void onBlockAdded(World world, int x, int y, int z) {
    if(world.isRemote) {
      return;
    }
    TileCapacitorBank tr = (TileCapacitorBank) world.getTileEntity(x, y, z);
    tr.onBlockAdded();
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId) {
    if(world.isRemote) {
      return;
    }
    TileCapacitorBank te = (TileCapacitorBank) world.getTileEntity(x, y, z);
    te.onNeighborBlockChange(blockId);
  }

  @Override
  public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
    ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
    if(!world.isRemote) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileCapacitorBank) {
        TileCapacitorBank cb = (TileCapacitorBank) te;
        cb.onBreakBlock();

        ItemStack itemStack =
            BlockItemCapacitorBank.createItemStackWithPower(cb.doGetEnergyStored());
        ret.add(itemStack);
      }
    }
    return ret;
  }

  @Override
  public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
    if(!world.isRemote && (!player.capabilities.isCreativeMode || "true".equalsIgnoreCase(System.getProperty("blockCapBankAllwaysDrop")))) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileCapacitorBank) {
        TileCapacitorBank cb = (TileCapacitorBank) te;
        cb.onBreakBlock();

        ItemStack itemStack =
            BlockItemCapacitorBank.createItemStackWithPower(cb.doGetEnergyStored());
        float f = 0.7F;
        double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
      }
    }
    return super.removedByPlayer(world, player, x, y, z);
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    if(world.isRemote) {
      return;
    }
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileCapacitorBank) {
      TileCapacitorBank cb = (TileCapacitorBank) te;
      cb.addEnergy(PowerHandlerUtil.getStoredEnergyForItem(stack));
    }
    world.markBlockForUpdate(x, y, z);
  }

  @Override
  public int quantityDropped(Random r) {
    return 0;
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
    if(!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(!(te instanceof TileCapacitorBank)) {
        super.breakBlock(world, x, y, z, par5, par6);
        return;
      }
      TileCapacitorBank cb = (TileCapacitorBank) te;
      Util.dropItems(world, cb, x, y, z, true);
    }
    world.removeTileEntity(x, y, z);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileCapacitorBank)) {
      return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
    TileCapacitorBank tr = (TileCapacitorBank) te;
    if(!tr.isMultiblock()) {
      return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    Vector3d min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    Vector3d max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
    for (BlockCoord bc : tr.multiblock) {
      min.x = Math.min(min.x, bc.x);
      max.x = Math.max(max.x, bc.x + 1);
      min.y = Math.min(min.y, bc.y);
      max.y = Math.max(max.y, bc.y + 1);
      min.z = Math.min(min.z, bc.z);
      max.z = Math.max(max.z, bc.z + 1);
    }
    return AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x, max.y, max.z);
  }

}
