package crazypants.enderio;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.tool.ToolUtil;

public abstract class BlockEio extends Block {

  protected final Class<? extends TileEntity> teClass;
  protected final String name;

  protected BlockEio(String name, Class<? extends TileEntity> teClass) {
    this(name, teClass, new Material(MapColor.ironColor));
  }

  protected BlockEio(String name, Class<? extends TileEntity> teClass, Material mat) {
    super(mat);
    this.teClass = teClass;
    this.name = name;
    setHardness(0.5F);
    setBlockName(name);
    setStepSound(Block.soundTypeMetal);
    setHarvestLevel("pickaxe", 0);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  protected void init() {
    GameRegistry.registerBlock(this, name);
    if(teClass != null) {
      GameRegistry.registerTileEntity(teClass, name + "TileEntity");
    }
  }

  @Override
  public boolean hasTileEntity(int metadata) {
    return teClass != null;
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    if(teClass != null) {
      try {
        return teClass.newInstance();
      } catch (Exception e) {
        Log.error("Could not create tile entity for block " + name + " for class " + teClass);
      }
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcon = iIconRegister.registerIcon("enderio:" + name);
  }

  @Override
  public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
    return doNormalDrops(world, x, y, z) ? super.getDrops(world, x, y, z, metadata, fortune) : new ArrayList<ItemStack>();
  }

  /* Subclass Helpers */
  
  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {

    if(shouldWrench(world, x, y, z, entityPlayer, side) && ToolUtil.breakBlockWithTool(this, world, x, y, z, entityPlayer)) {
      return true;
    }

    ITool tool = ToolUtil.getEquippedTool(entityPlayer);
    if(tool != null && !entityPlayer.isSneaking()) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof AbstractMachineEntity) {
        ((AbstractMachineEntity) te).toggleIoModeForFace(ForgeDirection.getOrientation(side));
        world.markBlockForUpdate(x, y, z);
        return true;
      }
    }

    if(entityPlayer.isSneaking()) {
      return false;
    }
    
    return openGui(world, x, y, z, entityPlayer, side);
  }

  protected boolean shouldWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side) {
    return true;
  }

  protected boolean openGui(World world, int x, int y, int z, EntityPlayer entityPlayer, int side) {
    return false;
  }

  public boolean doNormalDrops(World world, int x, int y, int z) {
    return true;
  }

  @Override
  public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean doHarvest) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityEio && ((TileEntityEio) te).shouldDrop()) {
      if(!world.isRemote && !player.capabilities.isCreativeMode && !doNormalDrops(world, x, y, z)) {
        dropAsItem(world, x, y, z, (TileEntityEio) te);
        ((TileEntityEio) te).preventDrops();
      } else if(player.capabilities.isCreativeMode) {
        ((TileEntityEio) te).preventDrops();
      }
    }
    return super.removedByPlayer(world, player, x, y, z, false);
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileEntityEio && ((TileEntityEio) te).shouldDrop() && !doNormalDrops(world, x, y, z)) {
      dropAsItem(world, x, y, z, (TileEntityEio) te);
      ((TileEntityEio)te).preventDrops();
    }
    super.breakBlock(world, x, y, z, block, meta);
  }

  public void dropAsItem(World world, int x, int y, int z, TileEntityEio te) {
    int meta = damageDropped(te.getBlockMetadata());
    ItemStack itemStack = new ItemStack(this, 1, meta);
    processDrop(world, x, y, z, te, itemStack);
    dropBlockAsItem(world, x, y, z, itemStack);
  }

  protected void processDrop(World world, int x, int y, int z, @Nullable TileEntityEio te, ItemStack drop) {
  }
}
