package crazypants.enderio.conduit.gas;

import cpw.mods.fml.common.Optional.Interface;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IExtractor;
import mekanism.api.gas.IGasHandler;
import net.minecraftforge.common.util.ForgeDirection;

@Interface(iface = "mekanism.api.gas.IGasHandler", modid = GasUtil.API_NAME)
public interface IGasConduit extends IConduit, IGasHandler, IExtractor {

    boolean canOutputToDir(ForgeDirection dir);

    boolean isExtractingFromDir(ForgeDirection dir);
}
