package crazypants.enderio.conduit.power;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IExtractor;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.IInternalPowerHandler;
import crazypants.enderio.power.IPowerInterface;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public interface IPowerConduit extends IConduit, IInternalPowerHandler, IExtractor {

  public static final String ICON_KEY = "enderio:powerConduit";
  public static final String ICON_KEY_INPUT = "enderio:powerConduitInput";
  public static final String ICON_KEY_OUTPUT = "enderio:powerConduitOutput";
  public static final String ICON_CORE_KEY = "enderio:powerConduitCore";
  public static final String ICON_TRANSMISSION_KEY = "enderio:powerConduitTransmission";

  public static final String COLOR_CONTROLLER_ID = "ColorController";

  IPowerInterface getExternalPowerReceptor(EnumFacing direction);

  ICapacitor getCapacitor();

  int getMaxEnergyExtracted(EnumFacing dir);

  @Override
  int getMaxEnergyRecieved(EnumFacing dir);

  TextureAtlasSprite getTextureForInputMode();

  TextureAtlasSprite getTextureForOutputMode();

  //called from NetworkPowerManager
  void onTick();
  
  boolean getConnectionsDirty();


}
