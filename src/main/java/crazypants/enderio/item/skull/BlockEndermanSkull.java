package crazypants.enderio.item.skull;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.crafting.IInfusionStabiliser;
import crazypants.enderio.BlockEio;
import crazypants.enderio.IHaveRenderers;
import crazypants.enderio.ModObject;
import crazypants.util.ClientUtil;

@Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "Thaumcraft")
public class BlockEndermanSkull extends BlockEio<TileEndermanSkull> implements IInfusionStabiliser, IHaveRenderers {

  public enum SkullType implements IStringSerializable {

    BASE("base", false),
    REANIMATED("reanimated", true),
    TORMENTED("tormented", false),
    REANIMATED_TORMENTED("reanimatedTormented", true);

    final String name;
    final boolean showEyes;

    SkullType(String name, boolean showEyes) {
      this.name = name;
      this.showEyes = showEyes;
    }

    @Override
    public String getName() {
      return name;
    }
  }

  public static final PropertyEnum<SkullType> VARIANT = PropertyEnum.<SkullType> create("variant", SkullType.class);

  public static BlockEndermanSkull create() {
    BlockEndermanSkull res = new BlockEndermanSkull();
    res.init();
    return res;
  }

  private BlockEndermanSkull() {
    super(ModObject.blockEndermanSkull.getUnlocalisedName(), TileEndermanSkull.class, ItemEndermanSkull.class, Material.circuits);
    setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
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
    SkullType var = SkullType.values()[meta];
    return getDefaultState().withProperty(VARIANT, var);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    SkullType var = state.getValue(VARIANT);
    return var.ordinal();
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { VARIANT });
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public int getRenderType() {
    return 2;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    int inc = MathHelper.floor_double(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
    float facingYaw = -22.5f * inc;
    TileEndermanSkull te = getTileEntity(world, pos);
    if (te != null) {
      te.setYaw(facingYaw);
    }
    if (world.isRemote) {
      return;
    }
    world.setBlockState(pos, getStateFromMeta(stack.getItemDamage()));
    world.markBlockForUpdate(pos);
  }

  @Override
  public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
    TileEndermanSkull tileEntity = getTileEntity(worldIn, pos);
    if (tileEntity != null) {
      tileEntity.lookingAt = 20;
    }
    return super.getSelectedBoundingBox(worldIn, pos);
  }

  @Override
  @Optional.Method(modid = "Thaumcraft")
  public boolean canStabaliseInfusion(World world, BlockPos pos) {
    return true;
  }
}
