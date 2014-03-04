package crazypants.enderio.conduit.power;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IExtractor;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.IPowerInterface;

public interface IPowerConduit extends IConduit, IInternalPowerReceptor, IExtractor {

  public static final String ICON_KEY = "enderio:powerConduit";
  public static final String ICON_KEY_INPUT = "enderio:powerConduitInput";
  public static final String ICON_KEY_OUTPUT = "enderio:powerConduitOutput";
  public static final String ICON_CORE_KEY = "enderio:powerConduitCore";
  public static final String ICON_TRANSMISSION_KEY = "enderio:powerConduitTransmission";

  public static final String COLOR_CONTROLLER_ID = "ColorController";

  IPowerInterface getExternalPowerReceptor(ForgeDirection direction);

  ICapacitor getCapacitor();

  float getMaxEnergyExtracted(ForgeDirection dir);

  float getMaxEnergyRecieved(ForgeDirection dir);

  IIcon getTextureForInputMode();

  IIcon getTextureForOutputMode();

  //called from NetworkPowerManager 
  void onTick();

  //mj
  float getEnergyStored();

  void setEnergyStored(float give);

  boolean getConnectionsDirty();

}
