package crazypants.enderio.machine.vacuum;

import com.enderio.core.common.network.MessageTileEntity;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

public class PacketVaccumChest extends MessageTileEntity<TileVacuumChest>
        implements IMessageHandler<PacketVaccumChest, IMessage> {

    public static final int CMD_SET_RANGE = 0;
    public static final int CMD_SET_SLOT = 1;
    public static final int CMD_SET_BLACKLIST = 2;
    public static final int CMD_SET_MATCHMETA = 3;

    private int cmd;
    private int value;
    private ItemStack stack;

    public PacketVaccumChest() {}

    private PacketVaccumChest(TileVacuumChest tile, int cmd) {
        super(tile);
        this.cmd = cmd;
    }

    public static PacketVaccumChest setRange(TileVacuumChest tile, int range) {
        PacketVaccumChest msg = new PacketVaccumChest(tile, CMD_SET_RANGE);
        msg.value = range;
        tile.setRange(range);
        return msg;
    }

    public static PacketVaccumChest setFilterSlot(TileVacuumChest tile, int slot, ItemStack stack) {
        PacketVaccumChest msg = new PacketVaccumChest(tile, CMD_SET_SLOT);
        msg.value = slot;
        msg.stack = stack;
        tile.setItemFilterSlot(slot, stack);
        return msg;
    }

    public static PacketVaccumChest setFilterBlacklist(TileVacuumChest tile, boolean isBlacklist) {
        PacketVaccumChest msg = new PacketVaccumChest(tile, CMD_SET_BLACKLIST);
        msg.value = isBlacklist ? 1 : 0;
        tile.setFilterBlacklist(isBlacklist);
        return msg;
    }

    public static PacketVaccumChest setFilterMatchMeta(TileVacuumChest tile, boolean matchMeta) {
        PacketVaccumChest msg = new PacketVaccumChest(tile, CMD_SET_MATCHMETA);
        msg.value = matchMeta ? 1 : 0;
        tile.setFilterMatchMeta(matchMeta);
        return msg;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        cmd = buf.readByte() & 255;
        value = buf.readInt();
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeByte(cmd);
        buf.writeInt(value);
        ByteBufUtils.writeItemStack(buf, stack);
    }

    @Override
    public IMessage onMessage(PacketVaccumChest msg, MessageContext ctx) {
        TileVacuumChest te = msg.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
        if (te != null) {
            switch (msg.cmd) {
                case CMD_SET_RANGE:
                    te.setRange(msg.value);
                    break;
                case CMD_SET_SLOT:
                    te.setItemFilterSlot(msg.value, msg.stack);
                    break;
                case CMD_SET_BLACKLIST:
                    te.setFilterBlacklist(msg.value != 0);
                    break;
                case CMD_SET_MATCHMETA:
                    te.setFilterMatchMeta(msg.value != 0);
                    break;
            }
        }
        return null;
    }
}
