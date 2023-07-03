package crazypants.enderio.machine.invpanel;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.network.PacketUtil;
import io.netty.buffer.ByteBuf;

public class PacketSetExtractionDisabled extends MessageTileEntity<TileInventoryPanel>
        implements IMessageHandler<PacketSetExtractionDisabled, IMessage> {

    private boolean extractionDisabled;

    public PacketSetExtractionDisabled() {}

    public PacketSetExtractionDisabled(TileInventoryPanel tile, boolean extractionDisabled) {
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
    public IMessage onMessage(PacketSetExtractionDisabled message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (PacketUtil.isInvalidPacketForGui(ctx, te, getClass())) return null;
        if (te instanceof TileInventoryPanel) {
            TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
            teInvPanel.setExtractionDisabled(message.extractionDisabled);
        }
        return null;
    }
}
