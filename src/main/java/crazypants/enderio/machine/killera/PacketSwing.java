package crazypants.enderio.machine.killera;

import net.minecraft.entity.player.EntityPlayer;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketSwing extends MessageTileEntity<TileKillerJoe> implements IMessageHandler<PacketSwing, IMessage> {

    public PacketSwing() {}

    public PacketSwing(TileKillerJoe tile) {
        super(tile);
    }

    @Override
    public IMessage onMessage(PacketSwing message, MessageContext ctx) {
        EntityPlayer player = EnderIO.proxy.getClientPlayer();
        TileKillerJoe tile = message.getTileEntity(player.worldObj);
        if (tile != null) {
            tile.swingWeapon();
        }
        return null;
    }
}
