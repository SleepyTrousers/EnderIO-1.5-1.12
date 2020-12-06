package gg.galaxygaming.gasconduits.common.network;

import com.enderio.core.common.network.ThreadedNetworkWrapper;
import gg.galaxygaming.gasconduits.GasConduitsConstants;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    @Nonnull
    private static final ThreadedNetworkWrapper INSTANCE = new ThreadedNetworkWrapper(GasConduitsConstants.MOD_ID);
    private static int ID;

    public static void init(FMLInitializationEvent event) {
        INSTANCE.registerMessage(PacketConduitGasLevel.Handler.class, PacketConduitGasLevel.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(PacketGasFilter.Handler.class, PacketGasFilter.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(PacketEnderGasConduit.Handler.class, PacketEnderGasConduit.class, ID++, Side.SERVER);
    }
}