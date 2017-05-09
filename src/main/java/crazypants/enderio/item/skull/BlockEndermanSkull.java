package crazypants.enderio.item.skull;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.enderio.render.IHaveTESR;
import crazypants.util.ClientUtil;
import crazypants.util.NullHelper;
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
import net.minecraft.util.IStringSerializable;
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

  public enum SkullType implements IStringSerializable {

    BASE("base", false),
    REANIMATED("reanimated", true),
    TORMENTED("tormented", false),
    REANIMATED_TORMENTED("reanimatedTormented", true);

    final @Nonnull String name;
    final boolean showEyes;

    SkullType(@Nonnull String name, boolean showEyes) {
      this.name = name;
      this.showEyes = showEyes;
    }

    @Override
    public @Nonnull String getName() {
      return NullHelper.notnullJ(name.toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
    }

    public static @Nonnull SkullType getTypeFromMeta(int meta) {
      return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
    }
  }

  public static final @Nonnull PropertyEnum<SkullType> VARIANT = NullHelper.notnullM(PropertyEnum.<SkullType> create("variant", SkullType.class),
      "PropertyEnum.create()");

  public static BlockEndermanSkull create() {
    BlockEndermanSkull res = new BlockEndermanSkull();
    res.init();
    return res;
  }

  public static final @Nonnull AxisAlignedBB AABB = new AxisAlignedBB(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);

  private BlockEndermanSkull() {
    super(ModObject.blockEndermanSkull.getUnlocalisedName(), TileEndermanSkull.class, NullHelper.notnullM(Material.CIRCUITS, "Material.CIRCUITS"));
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
  }

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
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
  public void registerRenderers() {
    Item item = Item.getItemFromBlock(this);
    int num = SkullType.values().length;
    for (int i = 0; i < num; i++) {
      SkullType st = SkullType.values()[i];
      ClientUtil.regRenderer(item, i, ModObject.blockEndermanSkull.getUnlocalisedName() + "_" + st.name);
    }
  }

  @Override
  public int damageDropped(IBlockState state) {
    SkullType var = state.getValue(VARIANT);
    return var.ordinal();
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    @Nonnull
    SkullType var = SkullType.getTypeFromMeta(meta);
    return getDefaultState().withProperty(VARIANT, var);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    SkullType var = state.getValue(VARIANT);
    return var.ordinal();
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { VARIANT });
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  public @Nonnull EnumBlockRenderType getRenderType(IBlockState bs) {
    return EnumBlockRenderType.INVISIBLE;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    if (world != null && pos != null) {
      if (player != null) {
        int inc = MathHelper.floor(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
        float facingYaw = -22.5f * inc;
        TileEndermanSkull te = getTileEntity(world, pos);
        if (te != null) {
          te.setYaw(facingYaw);
        }
      }
      if (world.isRemote) {
        return;
      }
      if (stack != null) {
        world.setBlockState(pos, getStateFromMeta(stack.getItemDamage()));
        world.notifyBlockUpdate(pos, state, state, 3);
      }
    }
  }

  @Deprecated
  @Override
  public AxisAlignedBB getSelectedBoundingBox(IBlockState bs, World worldIn, BlockPos pos) {
    if (worldIn != null && pos != null) {
      TileEndermanSkull tileEntity = getTileEntity(worldIn, pos);
      if (tileEntity != null) {
        tileEntity.lookingAt = 20;
      }
    }
    return super.getSelectedBoundingBox(bs, worldIn, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileEndermanSkull.class, new EndermanSkullRenderer());
  }

  //TODO: Mod Thaumcraft
//  @Override
//  @Optional.Method(modid = "Thaumcraft")
//  public boolean canStabaliseInfusion(World world, BlockPos pos) {
//    return true;
//  }
}
