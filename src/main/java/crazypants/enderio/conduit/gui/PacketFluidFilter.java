package crazypants.enderio.conduit.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.liquid.AbstractEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.FluidFilter;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.packet.AbstractConduitPacket;
import crazypants.enderio.conduit.packet.ConTypeEnum;
import io.netty.buffer.ByteBuf;

public class PacketFluidFilter extends AbstractConduitPacket<ILiquidConduit>
        implements IMessageHandler<PacketFluidFilter, IMessage> {

    private ForgeDirection dir;
    private boolean isInput;
    private FluidFilter filter;

    public PacketFluidFilter() {}

    public PacketFluidFilter(AbstractEnderLiquidConduit eConduit, ForgeDirection dir, FluidFilter filter,
            boolean isInput) {
        super(eConduit.getBundle().getEntity(), ConTypeEnum.FLUID);
        this.dir = dir;
        this.filter = filter;
        this.isInput = isInput;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort(dir.ordinal());
        buf.writeBoolean(isInput);
        NBTTagCompound tag = new NBTTagCompound();
        filter.writeToNBT(tag);
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        dir = ForgeDirection.values()[buf.readShort()];
        isInput = buf.readBoolean();
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        filter = new FluidFilter();
        filter.readFromNBT(tag);
    }

    @Override
    public IMessage onMessage(PacketFluidFilter message, MessageContext ctx) {
        ILiquidConduit conduit = message.getTileCasted(ctx);
        if (!(conduit instanceof AbstractEnderLiquidConduit)) {
            return null;
        }
        AbstractEnderLiquidConduit eCon = (AbstractEnderLiquidConduit) conduit;
        eCon.setFilter(message.dir, message.filter, message.isInput);
        message.getWorld(ctx).markBlockForUpdate(message.x, message.y, message.z);
        return null;
    }
}
