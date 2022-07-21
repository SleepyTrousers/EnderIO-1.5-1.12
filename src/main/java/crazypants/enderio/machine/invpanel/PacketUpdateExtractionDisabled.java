package crazypants.enderio.machine.invpanel;

import com.enderio.core.common.network.MessageTileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class PacketUpdateExtractionDisabled extends MessageTileEntity<TileInventoryPanel>
        implements IMessageHandler<PacketUpdateExtractionDisabled, IMessage> {

    private boolean extractionDisabled;

    public PacketUpdateExtractionDisabled() {}

    public PacketUpdateExtractionDisabled(TileInventoryPanel tile, boolean extractionDisabled) {
        super(tile);
        this.extractionDisabled = extractionDisabled;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        extractionDisabled = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeBoolean(extractionDisabled);
    }

    @Override
    public IMessage onMessage(PacketUpdateExtractionDisabled message, MessageContext ctx) {
        EntityPlayer player = EnderIO.proxy.getClientPlayer();
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (te instanceof TileInventoryPanel) {
            TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
            teInvPanel.updateExtractionDisabled(message.extractionDisabled);
        }
        return null;
    }
}
