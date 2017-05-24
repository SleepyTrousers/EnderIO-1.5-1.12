package crazypants.enderio.item.skull;

import javax.annotation.Nonnull;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.enderio.render.IHaveTESR;
import crazypants.util.ClientUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "Thaumcraft")
public class BlockEndermanSkull extends BlockEio<TileEndermanSkull> implements IHaveRenderers, IHaveTESR { // TODO: Mod Thaumcraft IInfusionStabiliser

  public static final @Nonnull PropertyEnum<SkullType> VARIANT = PropertyEnum.<SkullType> create("variant", SkullType.class);

  public static BlockEndermanSkull create(@Nonnull IModObject modObject) {
    BlockEndermanSkull res = new BlockEndermanSkull(modObject);
    res.init();
    return res;
  }

  public static final @Nonnull AxisAlignedBB AABB = new AxisAlignedBB(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);

  private BlockEndermanSkull(@Nonnull IModObject modObject) {
    super(modObject.getUnlocalisedName(), TileEndermanSkull.class, Material.CIRCUITS);
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
  }

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
    return AABB;
  }

  @Override
  protected ItemBlock createItemBlock() {
    return new ItemEndermanSkull(this, name);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    Item item = Item.getItemFromBlock(this);
    int num = SkullType.values().length;
    for (int i = 0; i < num; i++) {
      SkullType st = SkullType.values()[i];
      ClientUtil.regRenderer(item, i, name + "_" + st.getName());
    }
  }

  @Override
  public int damageDropped(@Nonnull IBlockState state) {
    return state.getValue(VARIANT).ordinal();
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(VARIANT, SkullType.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(VARIANT).ordinal();
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { VARIANT });
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public @Nonnull EnumBlockRenderType getRenderType(@Nonnull IBlockState bs) {
    return EnumBlockRenderType.INVISIBLE;
  }

  @Override
  public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ,
      int meta, @Nonnull EntityLivingBase placer, @Nonnull EnumHand hand) {
    return getStateFromMeta(placer.getHeldItem(hand).getItemDamage());
  }

  @Override
  public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player,
      @Nonnull ItemStack stack) {
    int inc = MathHelper.floor(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
    float facingYaw = -22.5f * inc;
    TileEndermanSkull te = getTileEntity(world, pos);
    if (te != null) {
      te.setYaw(facingYaw);
    }
  }

  @Deprecated
  @Override
  public @Nonnull AxisAlignedBB getSelectedBoundingBox(@Nonnull IBlockState bs, @Nonnull World worldIn, @Nonnull BlockPos pos) {
    TileEndermanSkull tileEntity = getTileEntity(worldIn, pos);
    if (tileEntity != null) {
      tileEntity.lookingAt = 20;
    }
    return super.getSelectedBoundingBox(bs, worldIn, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileEndermanSkull.class, new EndermanSkullRenderer());
  }

  // TODO: Mod Thaumcraft
  // @Override
  // @Optional.Method(modid = "Thaumcraft")
  // public boolean canStabaliseInfusion(World world, BlockPos pos) {
  // return true;
  // }
}
