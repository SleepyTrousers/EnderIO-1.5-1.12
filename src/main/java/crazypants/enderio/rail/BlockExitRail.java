package crazypants.enderio.rail;

import crazypants.enderio.ModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecart.Type;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraft.block.BlockRailDetector.SHAPE;

public class BlockExitRail extends BlockRailBase implements IHaveRenderers {

  public static BlockExitRail create() {
    BlockExitRail result = new BlockExitRail();
    result.init();
    return result;
  }

  public BlockExitRail() {
    super(false);
    this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
    setCreativeTab(CreativeTabs.TRANSPORTATION);
    setUnlocalizedName(ModObject.blockExitRail.getUnlocalisedName());
    setRegistryName(ModObject.blockExitRail.getUnlocalisedName());
  }

  private void init() {
    GameRegistry.register(this);
    GameRegistry.register(new ItemBlock(this).setRegistryName(ModObject.blockExitRail.getUnlocalisedName()));
  }

  @Override
  public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
    return SHAPE;
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return state.getValue(SHAPE).getMetadata();
  }

  @Override
  public IBlockState withRotation(IBlockState state, Rotation rot) {
    return Blocks.DETECTOR_RAIL.withRotation(state, rot);
  }

  @Override
  public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
    return Blocks.DETECTOR_RAIL.withMirror(state, mirrorIn);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { SHAPE });
  }

  @Override
  public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
    return false;
  }

  @Override
  public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
    return 0.05f;
  }

  @Override
  public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
    if (cart.getType() == Type.RIDEABLE) {
      if (cart.isBeingRidden()) {
        cart.removePassengers();
      }
      cart.killMinecart(DamageSource.generic);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    ClientUtil.registerDefaultItemRenderer(ModObject.blockExitRail);
  }

}
