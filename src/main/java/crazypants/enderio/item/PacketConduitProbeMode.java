package crazypants.enderio.item;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;

public class PacketConduitProbeMode implements IMessage, IMessageHandler<PacketConduitProbeMode, IMessage> {

    public PacketConduitProbeMode() {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(PacketConduitProbeMode message, MessageContext ctx) {
        ItemStack stack = ctx.getServerHandler().playerEntity.inventory.getCurrentItem();
        if (stack != null && stack.getItem() == EnderIO.itemConduitProbe) {
            int newMeta = stack.getItemDamage() == 0 ? 1 : 0;
            stack.setItemDamage(newMeta);
        }
        return null;
    }
}
