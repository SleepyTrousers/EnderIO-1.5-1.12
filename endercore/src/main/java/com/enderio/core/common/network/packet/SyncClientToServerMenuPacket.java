package com.enderio.core.common.network.packet;

import com.enderio.core.common.blockentity.sync.EnderDataSlot;
import com.enderio.core.common.menu.SyncedMenu;
import com.enderio.core.common.network.ClientToServerMenuPacket;
import com.enderio.core.common.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

public class SyncClientToServerMenuPacket extends ClientToServerMenuPacket<SyncedMenu> {

    private final CompoundTag data;

    public SyncClientToServerMenuPacket(int containerID, ListTag list) {
        super(SyncedMenu.class, containerID);
        data = new CompoundTag();
        data.put("list", list);
    }

    public SyncClientToServerMenuPacket(FriendlyByteBuf buf) {
        super(SyncedMenu.class, buf);
        this.data = buf.readNbt();
    }

    @Override
    protected void write(FriendlyByteBuf writeInto) {
        super.write(writeInto);
        writeInto.writeNbt(data);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ListTag list = data.getList("list", Constants.NBT.TAG_COMPOUND);
        List<EnderDataSlot<?>> clientToServerSlots = getMenu(context).getClientToServerSlots();
        List<Pair<Integer, CompoundTag>> dataSlots = new ArrayList<>();
        boolean encounteredError = false;
        for (Tag tag: list) {
            if (tag instanceof CompoundTag compound) {
                Tag indexTag = compound.get("dataSlotIndex");
                if (indexTag instanceof IntTag intTag) {
                    int index = intTag.getAsInt();
                    if (index >= 0 && index < clientToServerSlots.size()) {
                        dataSlots.add(Pair.of(intTag.getAsInt(), compound));
                    } else {
                        encounteredError = true;
                        Packet.logPacketError(context, "Index " + index + " is out of range for " + clientToServerSlots.size() + " dataslots", this);
                    }
                } else {
                    encounteredError = true;
                    if (indexTag == null) {
                        Packet.logPacketError(context, "There is no IndexTag in (" + tag.getAsString() + ")", this);
                    } else {
                        Packet.logPacketError(context, "IndexTag is not an IntTag (" + indexTag.getAsString() + ")", this);
                    }
                }
            }
        }
        if (!encounteredError) {
            for (Pair<Integer, CompoundTag> dataSlot : dataSlots) {
                try {
                    clientToServerSlots.get(dataSlot.getKey()).handleNBT(dataSlot.getRight());
                } catch (Exception e) {
                    Packet.logPacketError(context, "An exception has been caught during handling of dataslot " + dataSlot.getKey()
                        + " with data " + dataSlot.getRight().getAsString()
                        + " in menu " + getMenu(context).getClass(), this);
                    LogManager.getLogger().warn(e);
                    encounteredError = true;
                }
            }
        }
        if (encounteredError) {
            handleWrongPlayer(context);
        }
    }
}
