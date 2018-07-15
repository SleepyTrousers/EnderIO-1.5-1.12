package crazypants.enderio.machines.machine.wireless;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.machines.config.ChargerConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockNormalWirelessCharger extends BlockEio<TileWirelessCharger> implements IResourceTooltipProvider, IHaveRenderers {

  public static BlockNormalWirelessCharger create(@Nonnull IModObject modObject) {
    BlockNormalWirelessCharger res = new BlockNormalWirelessCharger(modObject);
    res.init();
    return res;
  }

  protected BlockNormalWirelessCharger(@Nonnull IModObject modObject) {
    super(modObject);
    setLightOpacity(1);
    setDefaultState(getBlockState().getBaseState());
    setShape(mkShape(BlockFaceShape.BOWL, BlockFaceShape.CENTER_BIG, BlockFaceShape.BOWL));
  }

  @Override
  protected void init() {
  }

  @Override
  @Nonnull
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] {});
  }

  @Override
  @Nonnull
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return 0;
  }

  @Override
  @Nonnull
  public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return getDefaultState();
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public void registerRenderers(@Nonnull IModObject modObject) {
    ClientUtil.registerDefaultItemRenderer(MachineObject.block_normal_wireless_charger);
  }

  protected @Nonnull BoundingBox getChargingStrength(@Nonnull IBlockState state, @Nonnull BlockPos pos) {
    return new BoundingBox(pos).expand(ChargerConfig.wirelessRangeAntenna.get());
  }

  protected boolean isAntenna() {
    return true;
  }

}
