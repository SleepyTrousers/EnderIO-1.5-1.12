package crazypants.enderio.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

/**
 *
 */
public interface IPacketEio {

  void encode(ChannelHandlerContext ctx, ByteBuf buffer);


  void decode(ChannelHandlerContext ctx, ByteBuf buffer);

  void handleClientSide(EntityPlayer player);

  void handleServerSide(EntityPlayer player);

}
