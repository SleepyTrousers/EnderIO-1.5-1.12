package crazypants.util;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.liquid.PacketFluidLevel;

/**
 * To avoid integrated server crashes when calling client-only methods
 * @author Garrett Spicer-Davis
 */
public class ClientUtil
{
    public static void doFluidLevelUpdate(int x, int y, int z, PacketFluidLevel pkt)
    {
        TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
        
        if(pkt.tc == null || !(tile instanceof IConduitBundle)) {
          return;
        }
        IConduitBundle bundle = (IConduitBundle) tile;
        ILiquidConduit con = bundle.getConduit(ILiquidConduit.class);
        if(con == null) {
          return;
        }
        con.readFromNBT(pkt.tc, TileConduitBundle.NBT_VERSION);
    }
}
