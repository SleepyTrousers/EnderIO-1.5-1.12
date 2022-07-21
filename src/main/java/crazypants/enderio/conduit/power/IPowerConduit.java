package crazypants.enderio.conduit.power;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IExtractor;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.IInternalPowerHandler;
import crazypants.enderio.power.IPowerInterface;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public interface IPowerConduit extends IConduit, IInternalPowerHandler, IExtractor {

    public static final String ICON_KEY = "enderio:powerConduit";
    public static final String ICON_KEY_INPUT = "enderio:powerConduitInput";
    public static final String ICON_KEY_OUTPUT = "enderio:powerConduitOutput";
    public static final String ICON_CORE_KEY = "enderio:powerConduitCore";
    public static final String ICON_TRANSMISSION_KEY = "enderio:powerConduitTransmission";

    public static final String COLOR_CONTROLLER_ID = "ColorController";

    IPowerInterface getExternalPowerReceptor(ForgeDirection direction);

    ICapacitor getCapacitor();

    int getMaxEnergyExtracted(ForgeDirection dir);

    @Override
    int getMaxEnergyRecieved(ForgeDirection dir);

    IIcon getTextureForInputMode();

    IIcon getTextureForOutputMode();

    // called from NetworkPowerManager
    void onTick();

    boolean getConnectionsDirty();
}
