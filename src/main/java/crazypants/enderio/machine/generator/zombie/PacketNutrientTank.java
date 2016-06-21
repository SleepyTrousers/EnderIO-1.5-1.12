package crazypants.enderio.machine.generator.zombie;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketNutrientTank extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketNutrientTank, IMessage> {

  private int amount;

  public PacketNutrientTank() {
  }

  public<T extends TileEntity & IHasNutrientTank> PacketNutrientTank(T tile) {
    super(tile);
    amount = tile.getNutrientTank().getFluidAmount();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(amount);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    amount = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketNutrientTank message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity tile = message.getTileEntity(player.worldObj);
    if (tile instanceof IHasNutrientTank) {
      ((IHasNutrientTank) tile).getNutrientTank().setFluidAmount(message.amount);
    }
    return null;
  }
}