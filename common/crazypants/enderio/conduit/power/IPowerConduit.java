package crazypants.enderio.conduit.power;

import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;

public interface IPowerConduit extends IConduit, IInternalPowerReceptor {

  public static final String ICON_KEY = "enderio:powerConduit";
  public static final String ICON_KEY_INPUT = "enderio:powerConduitInput";
  public static final String ICON_KEY_OUTPUT = "enderio:powerConduitOutput";
  public static final String ICON_CORE_KEY = "enderio:powerConduitCore";
  public static final String ICON_TRANSMISSION_KEY = "enderio:powerConduitTransmission";

  IPowerReceptor getExternalPowerReceptor(ForgeDirection direction);

  ICapacitor getCapacitor();
  
  float getMaxEnergyExtracted(ForgeDirection dir);  
  
  float getMaxEnergyRecieved(ForgeDirection dir);
  
  Icon getTextureForInputMode();
  
  Icon getTextureForOutputMode();

}
