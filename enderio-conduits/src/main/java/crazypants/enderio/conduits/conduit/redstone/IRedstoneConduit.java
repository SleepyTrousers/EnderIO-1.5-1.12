package crazypants.enderio.conduits.conduit.redstone;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.conduit.redstone.signals.Signal;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public interface IRedstoneConduit extends IServerConduit, IClientConduit {

  public static final String KEY_CONDUIT_ICON = "blocks/redstone_conduit";
  public static final String KEY_TRANSMISSION_ICON = "blocks/redstone_conduit_transmission";

  public static final String KEY_INS_CONDUIT_ICON = "blocks/redstone_insulated_conduit";
  public static final String KEY_INS_CORE_OFF_ICON = "blocks/redstone_insulated_conduit_core_off";
  public static final String KEY_INS_CORE_ON_ICON = "blocks/redstone_insulated_conduit_core_on";

  // External redstone interface

  int isProvidingStrongPower(@Nonnull EnumFacing toDirection);

  int isProvidingWeakPower(@Nonnull EnumFacing toDirection);

  Set<Signal> getNetworkInputs(@Nonnull EnumFacing side);

  Collection<Signal> getNetworkOutputs(@Nonnull EnumFacing side);

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

  Set<EnumFacing> getInputConnections();

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
}
