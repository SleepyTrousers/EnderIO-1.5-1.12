package crazypants.enderio.material;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.machine.painter.TileEntityCustomBlock;

public class BlockFusedQuartz extends Block implements ITileEntityProvider {

  public static int renderId;

  public static BlockFusedQuartz create() {
    BlockFusedQuartz result = new BlockFusedQuartz();
    result.init();
    return result;
  }

  Icon itemIcon;

  private BlockFusedQuartz() {
    super(ModObject.blockFusedQuartz.id, Material.glass);
    setHardness(0.5F);
    setStepSound(Block.soundGlassFootstep);
    setUnlocalizedName(ModObject.blockFusedQuartz.unlocalisedName);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  private void init() {
    LanguageRegistry.addName(this, ModObject.blockFusedQuartz.name);
    GameRegistry.registerBlock(this, ModObject.blockFusedQuartz.unlocalisedName);
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

  @Override
  @SideOnly(Side.CLIENT)
  public int getRenderBlockPass() {
    return 1;
  }

  @Override
  public boolean canRenderInPass(int pass) {
    FusedQuartzRenderer.renderPass = pass;
    return true;
  }

  @Override
  public int getLightOpacity(World world, int x, int y, int z) {
    return 0;
  }

  @Override
  public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
    int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
    return i1 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:fusedQuartz");
    itemIcon = iconRegister.registerIcon("enderio:fusedQuartzItem");
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return null;
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileEntityCustomBlock();
  }

  /**
   * Remove the tile entity too.
   */
  @Override
  public void breakBlock(World world, int x, int y, int z, int par5, int par6) {

    if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
      TileEntity te = world.getBlockTileEntity(x, y, z);

      if (te instanceof TileEntityCustomBlock) {
        TileEntityCustomBlock tef = (TileEntityCustomBlock) te;

        ItemStack itemStack = createItemStackForSourceBlock(tef.getSourceBlockId(), tef.getSourceBlockMetadata());
        if (itemStack != null) {
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

  private ItemStack createItemStackForSourceBlock(int sourceBlockId, int sourceBlockMetadata) {
    if (sourceBlockId <= 0) {
      return null;
    }
    ItemStack result = new ItemStack(ModObject.itemFusedQuartzFrame.actualId, 1, 0);
    PainterUtil.setSourceBlock(result, sourceBlockId, sourceBlockMetadata);
    return result;
  }

}
