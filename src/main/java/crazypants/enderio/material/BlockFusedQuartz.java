package crazypants.enderio.material;

import java.util.List;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.painter.TileEntityPaintedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFusedQuartz extends BlockEio<TileEntityPaintedBlock> {
  
  private final static Type[] metaMapping = new Type[16];
  public enum Type {

    FUSED_QUARTZ("fusedQuartz", "enderio:fusedQuartz", "enderio:fusedQuartzFrame", "enderio:fusedQuartzItem", false, true, 0),
    GLASS("fusedGlass", "enderio:fusedGlass", Config.clearGlassSameTexture ? "enderio:fusedQuartzFrame" : "enderio:fusedGlassFrame", "enderio:fusedGlassItem", false, false, 0),
    ENLIGHTENED_FUSED_QUARTZ("enlightenedFusedQuartz", "enderio:fusedQuartz", "enderio:fusedQuartzFrame", "enderio:fusedQuartzItem", true, true, 0),
    ENLIGHTENED_GLASS("enlightenedFusedGlass", "enderio:fusedGlass", Config.clearGlassSameTexture ? "enderio:fusedQuartzFrame" : "enderio:fusedGlassFrame",
        "enderio:fusedGlassItem", true, false, 0),
    DARK_FUSED_QUARTZ("darkFusedQuartz", "enderio:fusedQuartz", "enderio:fusedQuartzFrame", "enderio:fusedQuartzItem", false, true, 255),
    DARK_GLASS("darkFusedGlass", "enderio:fusedGlass", Config.clearGlassSameTexture ? "enderio:fusedQuartzFrame" : "enderio:fusedGlassFrame", "enderio:fusedGlassItem", false, false, 255);

    final String unlocalisedName;
    final String blockIcon;
    final String frameIcon;
    final String itemIcon;
    final boolean enlightened;
    final boolean blastResistance;
    final int lightOpacity;
    int connectedTextureMask;

    private Type(String unlocalisedName, String blockIcon, String frameIcon, String itemIcon, boolean enlightened, boolean blastResistance, int lightOpacity) {
      this.unlocalisedName = unlocalisedName;
      this.frameIcon = frameIcon;
      this.blockIcon = blockIcon;
      this.itemIcon = itemIcon;
      this.enlightened = enlightened;
      this.blastResistance = blastResistance;
      this.lightOpacity = lightOpacity;
      connectedTextureMask = Config.clearGlassConnectToFusedQuartz ? ~0 : (1 << ordinal());
      metaMapping[this.ordinal()] = this;
    }

    public boolean connectTo(int otherMeta) {
      return (connectedTextureMask & (1 << otherMeta)) != 0;
    }

    void setConnectedTexture(Type other) {
      connectedTextureMask |= 1 << other.ordinal();
    }

    public static Type byMeta(int meta) {
      return metaMapping[meta] != null ? metaMapping[meta] : GLASS;
    }
  }

  static {
    Type.GLASS.setConnectedTexture(Type.ENLIGHTENED_GLASS);
    Type.GLASS.setConnectedTexture(Type.DARK_GLASS);
    Type.ENLIGHTENED_GLASS.setConnectedTexture(Type.GLASS);
    Type.ENLIGHTENED_GLASS.setConnectedTexture(Type.DARK_GLASS);
    Type.DARK_GLASS.setConnectedTexture(Type.GLASS);
    Type.DARK_GLASS.setConnectedTexture(Type.ENLIGHTENED_GLASS);
    Type.FUSED_QUARTZ.setConnectedTexture(Type.ENLIGHTENED_FUSED_QUARTZ);
    Type.FUSED_QUARTZ.setConnectedTexture(Type.DARK_FUSED_QUARTZ);
    Type.ENLIGHTENED_FUSED_QUARTZ.setConnectedTexture(Type.FUSED_QUARTZ);
    Type.ENLIGHTENED_FUSED_QUARTZ.setConnectedTexture(Type.DARK_FUSED_QUARTZ);
    Type.DARK_FUSED_QUARTZ.setConnectedTexture(Type.FUSED_QUARTZ);
    Type.DARK_FUSED_QUARTZ.setConnectedTexture(Type.ENLIGHTENED_FUSED_QUARTZ);
  }

  public static BlockFusedQuartz create() {
    BlockFusedQuartz result = new BlockFusedQuartz();
    result.init();
    return result;
  }

//  IIcon[] blockIcon;
//  IIcon[] itemsIcons;
//  IIcon[] frameIcons;

  private BlockFusedQuartz() {
    super(ModObject.blockFusedQuartz.unlocalisedName, TileEntityPaintedBlock.class, Material.glass);
    setStepSound(Block.soundTypeGlass);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, ItemFusedQuartz.class, name);
    if(teClass != null) {
      GameRegistry.registerTileEntity(teClass, name + "TileEntity");
    }
  }
  

  @Override
  public float getExplosionResistance(World world, BlockPos pos, Entity par1Entity, Explosion explosion) {   
    IBlockState bs = world.getBlockState(pos);
    int meta = bs.getBlock().getMetaFromState(bs);
    Type type = Type.byMeta(meta);
    if (type.blastResistance) {
      return 2000;
    } else {
      return super.getExplosionResistance(par1Entity);
    }
  }
  
  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public int getLightOpacity(IBlockAccess world, BlockPos pos) {
    IBlockState bs = world.getBlockState(pos);
    int meta = bs.getBlock().getMetaFromState(bs);
    Type type = Type.byMeta(meta);
    return type.lightOpacity;
  }
  
  @Override
  public int getLightValue(IBlockAccess world, BlockPos pos) {
    IBlockState bs = world.getBlockState(pos);
    Block block = bs.getBlock();
    if(block != this) {
      return super.getLightValue(world, pos);
    }
    int meta = block.getMetaFromState(bs);
    Type type = Type.byMeta(meta);
    return type.enlightened ? 15 : super.getLightValue(world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (int j = 0; j < Type.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
      
    IBlockState bs = world.getBlockState(pos);
    Block block = bs.getBlock();
    int meta = block.getMetaFromState(bs);
    if(block == this) {
      BlockPos here = pos.offset(side.getOpposite());
      bs = world.getBlockState(here);
      block = bs.getBlock();      
      int myMeta = block.getMetaFromState(bs);
      return !Type.byMeta(myMeta).connectTo(meta);
    }
    return true;
  }

  @Override
  public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {  
    return true;
  }

  
  @Override
  public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {  
    if(side == EnumFacing.UP) { //stop drips
      return false;
    }
    return true;
  }

  @Override
  public boolean canPlaceTorchOnTop(IBlockAccess world, BlockPos pos) {
    return true;
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iIconRegister) {
//    //This little oddity is so the standard rendering used for items and breaking effects
//    //uses the item texture, while the custom renderer uses 'realBlockIcon' to render the 'non-frame' part of the block.
//    Type[] ts = Type.values();
//    blockIcon = new IIcon[ts.length];
//    itemsIcons = new IIcon[ts.length];
//    frameIcons = new IIcon[ts.length];
//
//    for (int i = 0; i < ts.length; i++) {
//      blockIcon[i] = iIconRegister.registerIcon(ts[i].blockIcon);
//      itemsIcons[i] = iIconRegister.registerIcon(ts[i].itemIcon);
//      frameIcons[i] = iIconRegister.registerIcon(ts[i].frameIcon);
//    }
//  }

  @Override
  protected boolean shouldWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    return false;
  }

}
