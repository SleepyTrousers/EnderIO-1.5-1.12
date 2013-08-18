package crazypants.enderio.machine.solar;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.enderface.BlockEnderIO;
import crazypants.enderio.enderface.TileEnderIO;
import crazypants.enderio.machine.AbstractMachineBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockSolarPanel extends Block implements ITileEntityProvider {

  public static BlockSolarPanel create() {
    BlockSolarPanel result = new BlockSolarPanel();
    result.init();
    return result;
  }

  private static final float BLOCK_HEIGHT = 0.15f;
  
  Icon sideIcon;

  private BlockSolarPanel() {
    super(ModObject.blockSolarPanel.id, Material.ground);
    setHardness(0.5F);
    setStepSound(Block.soundStoneFootstep);
    setUnlocalizedName(ModObject.blockSolarPanel.unlocalisedName);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  private void init() {
    LanguageRegistry.addName(this, ModObject.blockSolarPanel.name);
    GameRegistry.registerBlock(this, ModObject.blockSolarPanel.unlocalisedName);
    GameRegistry.registerTileEntity(TileEntitySolarPanel.class, ModObject.blockSolarPanel.unlocalisedName + "TileEntity");
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return new TileEntitySolarPanel();
  }
  
  public Icon getIcon(int side, int meta) {
    if(side == ForgeDirection.UP.ordinal()) {
      return blockIcon;
    }
    return sideIcon;
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TileEntitySolarPanel) {
      ((TileEntitySolarPanel)te).onNeighborBlockChange();
    }    
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:solarPanelTop");
    sideIcon = iconRegister.registerIcon("enderio:solarPanelSide");
  }

  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  @Override
  public void setBlockBoundsForItemRender() {
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {
    setBlockBoundsBasedOnState(par1World, par2, par3, par4);
    super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
  }

}
