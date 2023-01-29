package crazypants.enderio.machine.invpanel;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.network.CompressedDataOutput;
import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.machine.invpanel.server.InventoryDatabaseServer;
import crazypants.enderio.machine.invpanel.server.ItemEntry;
import crazypants.enderio.network.PacketHandler;
import io.netty.buffer.ByteBuf;

public class PacketRequestMissingItems extends MessageTileEntity<TileInventoryPanel>
        implements IMessageHandler<PacketRequestMissingItems, IMessage> {

    private byte[] compressed;

    public PacketRequestMissingItems() {}

    public PacketRequestMissingItems(TileInventoryPanel tile, int generation, List<Integer> missingIDs) {
        super(tile);
        try {
            CompressedDataOutput cdo = new CompressedDataOutput();
            try {
                cdo.writeVariable(generation);
                cdo.writeVariable(missingIDs.size());
                for (Integer id : missingIDs) {
                    cdo.writeVariable(id - InventoryDatabase.COMPLEX_DBINDEX_START);
                }
                compressed = cdo.getCompressed();
            } finally {
                cdo.close();
            }
        } catch (IOException ex) {
            compressed = new byte[0];
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        compressed = NetworkUtil.readByteArray(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        NetworkUtil.writeByteArray(buf, compressed);
    }

    @Override
    public IMessage onMessage(PacketRequestMissingItems message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (te instanceof TileInventoryPanel) {
            TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
            InventoryDatabaseServer db = teInvPanel.getDatabaseServer();
            if (db != null) {
                try {
                    List<ItemEntry> items = db.decompressMissingItems(message.compressed);
                    if (!items.isEmpty()) {
                        PacketHandler.sendTo(new PacketItemInfo(teInvPanel, db, items), player);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(PacketItemInfo.class.getName())
                            .log(Level.SEVERE, "Exception while reading missing item IDs", ex);
                }
            }
        }
        return null;
    }
}
