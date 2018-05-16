package crazypants.enderio.conduits.conduit.redstone;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;
import crazypants.enderio.base.filter.redstone.IRedstoneSignalFilter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

public interface IRedstoneConduit extends IServerConduit, IClientConduit {

  public static final String KEY_CONDUIT_ICON = "blocks/redstone_conduit";
  public static final String KEY_TRANSMISSION_ICON = "blocks/redstone_conduit_transmission";

  public static final String KEY_INS_CONDUIT_ICON = "blocks/redstone_insulated_conduit";
  public static final String KEY_INS_CORE_OFF_ICON = "blocks/redstone_insulated_conduit_core_off";
  public static final String KEY_INS_CORE_ON_ICON = "blocks/redstone_insulated_conduit_core_on";

  // External redstone interface

  int isProvidingStrongPower(@Nonnull EnumFacing toDirection);

  int isProvidingWeakPower(@Nonnull EnumFacing toDirection);

  Signal getNetworkInput(@Nonnull EnumFacing side);

  CombinedSignal getNetworkOutput(@Nonnull EnumFacing side);

  @Nonnull
  DyeColor getInputSignalColor(@Nonnull EnumFacing dir);

  void updateNetwork();

  // Old insulated interface

  void onInputsChanged(@Nonnull EnumFacing side, int[] inputValues);

  void onInputChanged(@Nonnull EnumFacing side, int inputValue);

  void forceConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode);

  void setInputSignalColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col);

  boolean isOutputStrong(@Nonnull EnumFacing dir);

  void setOutputStrength(@Nonnull EnumFacing dir, boolean isStrong);

  int getRedstoneSignalForColor(@Nonnull DyeColor col);

  @Nonnull
  DyeColor getOutputSignalColor(@Nonnull EnumFacing dir);

  void setOutputSignalColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col);

  @Nonnull
  TextureAtlasSprite getTextureForInputMode();

  @Nonnull
  TextureAtlasSprite getTextureForOutputMode();

  @Nonnull
  TextureAtlasSprite getTextureForInOutMode(boolean b);

  @Nonnull
  TextureAtlasSprite getTextureForInOutBackground();

  @Override
  @Nullable
  RedstoneConduitNetwork getNetwork() throws NullPointerException;

  @Optional.Method(modid = "computercraft")
  @Nonnull
  public Map<DyeColor, Signal> getComputerCraftSignals(@Nonnull EnumFacing dir);

  @Nonnull
  IRedstoneSignalFilter getSignalFilter(@Nonnull EnumFacing dir, boolean isOutput);

  void setSignalIdBase(int id);

}
