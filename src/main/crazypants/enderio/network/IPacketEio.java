package crazypants.enderio.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by brad on 27/02/14.
 */
public interface IPacketEio {

  void encode(ChannelHandlerContext ctx, ByteBuf buffer);


  void decode(ChannelHandlerContext ctx, ByteBuf buffer);

  void handleClientSide(EntityPlayer player);

  void handleServerSide(EntityPlayer player);

}
