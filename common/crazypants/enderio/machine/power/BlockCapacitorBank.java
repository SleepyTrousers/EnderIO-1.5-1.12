package crazypants.enderio.machine.power;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import buildcraft.api.tools.IToolWrench;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.machine.painter.TileEntityCustomBlock;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;
import crazypants.vecmath.Vector3d;

public class BlockCapacitorBank extends Block implements ITileEntityProvider, IGuiHandler {

  static final NumberFormat NF = NumberFormat.getIntegerInstance();

  public static BlockCapacitorBank create() {
    BlockCapacitorBank res = new BlockCapacitorBank();
    res.init();
    return res;
  }

  Icon overlayIcon;
  Icon fillBarIcon;

  protected BlockCapacitorBank() {
    super(ModObject.blockCapacitorBank.actualId, new Material(MapColor.ironColor));
    setHardness(2.0F);
    setStepSound(soundMetalFootstep);
    setUnlocalizedName(ModObject.blockCapacitorBank.unlocalisedName);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.blockCapacitorBank.name);
    GameRegistry.registerBlock(this, BlockItemCapacitorBank.class, ModObject.blockCapacitorBank.unlocalisedName);
    GameRegistry.registerTileEntity(TileCapacitorBank.class, ModObject.blockCapacitorBank.unlocalisedName + "TileEntity");
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_CAPACITOR_BANK, this);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {

    if (ConduitUtil.isToolEquipped(entityPlayer) && entityPlayer.isSneaking()) {
      if (entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
        IToolWrench wrench = (IToolWrench) entityPlayer.getCurrentEquippedItem().getItem();
        if (wrench.canWrench(entityPlayer, x, y, z)) {
          removeBlockByPlayer(world, entityPlayer, x, y, z);
          if (entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
            ((IToolWrench) entityPlayer.getCurrentEquippedItem().getItem()).wrenchUsed(entityPlayer, x, y, z);
          }
          return true;
        }
      }
    }

    if (entityPlayer.isSneaking()) {
      return false;
    }
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (!(te instanceof TileCapacitorBank)) {
      return false;
    }
    entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_CAPACITOR_BANK, world, x, y, z);
    return true;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (te instanceof TileCapacitorBank) {
      return new GuiCapacitorBank((TileCapacitorBank) te);
    }
    return null;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:capacitorBank");
    overlayIcon = iconRegister.registerIcon("enderio:capacitorBankOverlays");
    fillBarIcon = iconRegister.registerIcon("enderio:capacitorBankFillBar");
  }

  @Override
  public int getRenderType() {
    return -1;
  }

  @Override
  public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
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
  public TileEntity createNewTileEntity(World world) {
    return null;
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileCapacitorBank();
  }

  @Override
  public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
    int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
    return i1 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
  }

  @Override
  public void onBlockAdded(World world, int x, int y, int z) {
    if (world.isRemote) {
      return;
    }
    TileCapacitorBank tr = (TileCapacitorBank) world.getBlockTileEntity(x, y, z);
    tr.onBlockAdded();
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
    if (world.isRemote) {
      return;
    }
    TileCapacitorBank te = (TileCapacitorBank) world.getBlockTileEntity(x, y, z);
    te.onNeighborBlockChange(blockId);
  }

  public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
    ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
    if(!world.isRemote) {             
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if (te instanceof TileCapacitorBank) {
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
  public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {    
    if (!world.isRemote) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if (te instanceof TileCapacitorBank) {
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
    return super.removeBlockByPlayer(world, player, x, y, z);
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    if (world.isRemote) {
      return;
    }
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (te instanceof TileCapacitorBank) {
      TileCapacitorBank cb = (TileCapacitorBank) te;
      cb.addEnergy(PowerHandlerUtil.getStoredEnergyForItem(stack));
    }
    world.markBlockForUpdate(x, y, z);
  }

  @Override
  public int idDropped(int par1, Random par2Random, int par3) {
    return 0;
  }

  @Override
  public int quantityDropped(Random r) {
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (!(te instanceof TileCapacitorBank)) {
      return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
    TileCapacitorBank tr = (TileCapacitorBank) te;
    if (!tr.isMultiblock()) {
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
