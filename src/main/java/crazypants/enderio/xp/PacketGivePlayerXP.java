package crazypants.enderio.xp;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketGivePlayerXP extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketGivePlayerXP, IMessage> {

  private static boolean isRegistered = false;
  
  public static void register() {
    if(!isRegistered) {
      PacketHandler.INSTANCE.registerMessage(PacketGivePlayerXP.class, PacketGivePlayerXP.class, PacketHandler.nextID(), Side.SERVER);
      isRegistered = true;
    }
  }
  
  
  int levels;
  
  public PacketGivePlayerXP() {
  }

  public PacketGivePlayerXP(TileEntity tile, int levels) {
    super(tile);
    this.levels = levels;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort((short)levels);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    levels = buf.readShort();
  }

  @Override
  public IMessage onMessage(PacketGivePlayerXP message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity tile = message.getTileEntity(player.world);
    if (tile instanceof IHaveExperience) {      
      IHaveExperience xpTile = (IHaveExperience)tile;
      xpTile.getContainer().givePlayerXp(player, message.levels);
      return new PacketExperianceContainer(tile);
    }
    return null;
  }

}
