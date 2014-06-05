package crazypants.enderio.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Created by CrazyPants on 27/02/14.
 */
public class NetworkUtil {

    public static NBTTagCompound readNBTTagCompound(ByteBuf dataIn) {
    try {
      short size = dataIn.readShort();
      if(size < 0) {
        return null;
      } else {
        byte[] buffer = new byte[size];
        dataIn.readBytes(buffer);
        return CompressedStreamTools.decompress(buffer);
      }
    } catch (IOException e) {
      FMLCommonHandler.instance().raiseException(e, "Custom Packet", true);
      return null;
    }
  }

  public static void writeNBTTagCompound(NBTTagCompound compound, ByteBuf dataout) {
    try {
      if(compound == null) {
        dataout.writeShort(-1);
      } else {
        byte[] buf = CompressedStreamTools.compress(compound);
        dataout.writeShort((short) buf.length);
        dataout.writeBytes(buf);
      }
    } catch (IOException e) {
      FMLCommonHandler.instance().raiseException(e, "PacketUtil.readTileEntityPacket.writeNBTTagCompound", true);
    }
  }
}
