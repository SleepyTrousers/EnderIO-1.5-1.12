package crazypants.enderio.xp;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.network.MessageTileEntity;
import crazypants.enderio.network.PacketHandler;

public class PacketGiveXP extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketGiveXP, IMessage> {

  private static boolean isRegistered = false;
  
  public static void register() {
    if(!isRegistered) {
      PacketHandler.INSTANCE.registerMessage(PacketGiveXP.class, PacketGiveXP.class, PacketHandler.nextID(), Side.SERVER);
      isRegistered = true;
    }
  }
  
  
  int levels;
  
  public PacketGiveXP() {
  }

  public PacketGiveXP(TileEntity tile, int levels) {
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
  public IMessage onMessage(PacketGiveXP message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity tile = message.getTileEntity(player.worldObj);
    if (tile instanceof IHaveExperience) {      
      IHaveExperience xpTile = (IHaveExperience)tile;
      xpTile.getContainer().givePlayerXp(player, message.levels);
    }
    return null;
  }

}
