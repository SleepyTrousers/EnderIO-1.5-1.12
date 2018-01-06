package crazypants.enderio.base.machine.baselegacy;

import crazypants.enderio.base.power.ILegacyPoweredTile;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLegacyPowerStorage implements IMessage {

  private long pos;
  private int storedEnergy;

  public PacketLegacyPowerStorage() {
  }

  public PacketLegacyPowerStorage(ILegacyPoweredTile ent) {
    pos = ent.getLocation().toLong();
    storedEnergy = ent.getEnergyStored();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeLong(pos);
    buf.writeInt(storedEnergy);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    pos = buf.readLong();
    storedEnergy = buf.readInt();
  }

  public static class Handler implements IMessageHandler<PacketLegacyPowerStorage, IMessage> {

    @Override
    public IMessage onMessage(PacketLegacyPowerStorage message, MessageContext ctx) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      TileEntity te = player.world.getTileEntity(BlockPos.fromLong(message.pos));
      if (te instanceof ILegacyPoweredTile) {
        ILegacyPoweredTile me = (ILegacyPoweredTile) te;
        me.setEnergyStored(message.storedEnergy);
      }
      return null;
    }

  }

}
