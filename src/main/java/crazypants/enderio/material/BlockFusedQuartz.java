package crazypants.enderio.material;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.machine.painter.TileEntityPaintedBlock;

public class BlockFusedQuartz extends BlockEio {

  public static int renderId;

  public enum Type {

    FUSED_QUARTZ("fusedQuartz", "enderio:fusedQuartz", "enderio:fusedQuartzFrame", "enderio:fusedQuartzItem"),
    GLASS("fusedGlass", "enderio:fusedGlass", "enderio:fusedGlassFrame", "enderio:fusedGlassItem");

    final String unlocalisedName;
    final String blockIcon;
    final String frameIcon;
    final String itemIcon;

    private Type(String unlocalisedName, String blockIcon, String frameIcon, String itemIcon) {
      this.unlocalisedName = unlocalisedName;
      this.frameIcon = frameIcon;
      this.blockIcon = blockIcon;
      this.itemIcon = itemIcon;
    }

  }

  public static BlockFusedQuartz create() {
    BlockFusedQuartz result = new BlockFusedQuartz();
    result.init();
    return result;
  }

  IIcon[] blockIcon;
  IIcon[] itemsIcons;
  IIcon[] frameIcons;

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
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public int getRenderType() {
    return renderId;
  }

  //TODO:1.7 this makes it go splat
  //  @Override
  //  @SideOnly(Side.CLIENT)
  //  public int getRenderBlockPass() {
  //    return 1;
  //  }
  //
  //  @Override
  //  public boolean canRenderInPass(int pass) {
  //    FusedQuartzRenderer.renderPass = pass;
  //    return true;
  //  }

  @Override
  public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
    return 0;
  }

  @Override
  public int damageDropped(int par1) {
    return par1;
  }

  @Override
  public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < Type.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
    Block i1 = par1IBlockAccess.getBlock(par2, par3, par4);
    return i1 == this ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
  }

  @Override
  public boolean isBlockSolid(IBlockAccess p_149747_1_, int p_149747_2_, int p_149747_3_, int p_149747_4_, int p_149747_5_) {
    return true;
  }

  @Override
  public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    return true;
  }

  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    //This little oddity is so the standard rendering used for items and breaking effects
    //uses the item texture, while the custom renderer uses 'realBlockIcon' to render the 'non-frame' part of the block.
    Type[] ts = Type.values();
    blockIcon = new IIcon[ts.length];
    itemsIcons = new IIcon[ts.length];
    frameIcons = new IIcon[ts.length];

    for (int i = 0; i < ts.length; i++) {
      blockIcon[i] = iIconRegister.registerIcon(ts[i].blockIcon);
      itemsIcons[i] = iIconRegister.registerIcon(ts[i].itemIcon);
      frameIcons[i] = iIconRegister.registerIcon(ts[i].frameIcon);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int par1, int meta) {
    meta = MathHelper.clamp_int(meta, 0, Type.values().length - 1);
    return blockIcon[meta];
  }

  public IIcon getItemIcon(int meta) {
    meta = MathHelper.clamp_int(meta, 0, Type.values().length - 1);
    return itemsIcons[meta];
  }

  public IIcon getDefaultFrameIcon(int meta) {
    meta = MathHelper.clamp_int(meta, 0, Type.values().length - 1);
    return frameIcons[meta];
  }

  /**
   * Remove the tile entity too.
   */
  @Override
  public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {

    if(!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
      TileEntity te = world.getTileEntity(x, y, z);

      if(te instanceof TileEntityPaintedBlock) {
        TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;

        ItemStack itemStack = createItemStackForSourceBlock(world.getBlockMetadata(x, y, z), tef.getSourceBlock(), tef.getSourceBlockMetadata());
        if(itemStack != null) {
          float f = 0.7F;
          double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
          double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
          double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
          EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
          entityitem.delayBeforeCanPickup = 10;
          world.spawnEntityInWorld(entityitem);
        }
      }

    }

    super.breakBlock(world, x, y, z, par5, par6);
  }

  private ItemStack createItemStackForSourceBlock(int quartzBlockMeta, Block sourceBlock, int sourceBlockMetadata) {
    if(sourceBlock == null) {
      return null;
    }
    ItemStack result = new ItemStack(EnderIO.instance.itemFusedQuartzFrame, 1, quartzBlockMeta);
    PainterUtil.setSourceBlock(result, sourceBlock, sourceBlockMetadata);
    return result;
  }

}
