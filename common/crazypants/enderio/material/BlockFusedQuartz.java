package crazypants.enderio.material;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
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
import crazypants.util.Lang;

public class BlockFusedQuartz extends Block implements ITileEntityProvider {

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

  Icon[] blockIcon;
  Icon[] itemsIcons;
  Icon[] frameIcons;

  private BlockFusedQuartz() {
    super(ModObject.blockFusedQuartz.id, Material.glass);
    setHardness(0.5F);
    setStepSound(Block.soundGlassFootstep);
    setUnlocalizedName(ModObject.blockFusedQuartz.unlocalisedName);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  private void init() {

    GameRegistry.registerBlock(this, ItemFusedQuartz.class, ModObject.blockFusedQuartz.unlocalisedName);
    LanguageRegistry.addName(this, ModObject.blockFusedQuartz.unlocalisedName);
    for (Type subtype : Type.values()) {
      String unlocalisedName = "blockFusedQuartz." + subtype.unlocalisedName;
      LanguageRegistry.instance().addStringLocalization(unlocalisedName, Lang.localize(unlocalisedName));
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
  public int damageDropped(int par1) {
    return par1;
  }

  @Override
  public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < Type.values().length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
    int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
    return i1 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    //This little oddity is so the standard rendering used for items and breaking effects
    //uses the item texture, while the custom renderer uses 'realBlockIcon' to render the 'non-frame' part of the block.
    Type[] ts = Type.values();
    blockIcon = new Icon[ts.length];
    itemsIcons = new Icon[ts.length];
    frameIcons = new Icon[ts.length];

    for (int i = 0; i < ts.length; i++) {
      blockIcon[i] = iconRegister.registerIcon(ts[i].blockIcon);
      itemsIcons[i] = iconRegister.registerIcon(ts[i].itemIcon);
      frameIcons[i] = iconRegister.registerIcon(ts[i].frameIcon);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Icon getIcon(int par1, int meta) {
    meta = MathHelper.clamp_int(meta, 0, Type.values().length - 1);
    return blockIcon[meta];
  }

  public Icon getItemIcon(int meta) {
    meta = MathHelper.clamp_int(meta, 0, Type.values().length - 1);
    return itemsIcons[meta];
  }

  public Icon getDefaultFrameIcon(int meta) {
    meta = MathHelper.clamp_int(meta, 0, Type.values().length - 1);
    return frameIcons[meta];
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

    if(!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
      TileEntity te = world.getBlockTileEntity(x, y, z);

      if(te instanceof TileEntityCustomBlock) {
        TileEntityCustomBlock tef = (TileEntityCustomBlock) te;

        ItemStack itemStack = createItemStackForSourceBlock(world.getBlockMetadata(x, y, z), tef.getSourceBlockId(), tef.getSourceBlockMetadata());
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

  private ItemStack createItemStackForSourceBlock(int quartzBlockMeta, int sourceBlockId, int sourceBlockMetadata) {
    if(sourceBlockId <= 0) {
      return null;
    }
    ItemStack result = new ItemStack(ModObject.itemFusedQuartzFrame.actualId, 1, quartzBlockMeta);
    PainterUtil.setSourceBlock(result, sourceBlockId, sourceBlockMetadata);
    return result;
  }

}
