package crazypants.enderio.base.handler.darksteel;

import java.util.UUID;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDarkSteelSFXPacket implements IMessage {

  private ResourceLocation upgradeName;
  public UUID uid;

  public PacketDarkSteelSFXPacket() {
  }

  public PacketDarkSteelSFXPacket(@Nonnull IDarkSteelUpgrade upgrade, @Nonnull EntityPlayer player) {
    this.upgradeName = upgrade.getRegistryName();
    this.uid = player.getUniqueID();
  }

  @Override
  public void toBytes(ByteBuf buffer) {
    ByteBufUtils.writeUTF8String(buffer, upgradeName.toString());
    buffer.writeLong(uid.getLeastSignificantBits());
    buffer.writeLong(uid.getMostSignificantBits());
  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    final String string = ByteBufUtils.readUTF8String(buffer);
    upgradeName = string != null ? new ResourceLocation(string) : null;
    long leastSig = buffer.readLong();
    long mostSig = buffer.readLong();
    uid = new UUID(mostSig, leastSig);
  }

  public static class ServerHandler implements IMessageHandler<PacketDarkSteelSFXPacket, IMessage> {

    @Override
    public IMessage onMessage(PacketDarkSteelSFXPacket message, MessageContext ctx) {
      if (message.upgradeName != null) {
        IDarkSteelUpgrade upgrade = UpgradeRegistry.getUpgrade(message.upgradeName);
        if (upgrade != null) {
          final EntityPlayerMP player = ctx.getServerHandler().player;
          message.uid = player.getUniqueID();
          for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot != null) {
              ItemStack stack = player.getItemStackFromSlot(slot);
              if (upgrade.hasUpgrade(stack)) {

                WorldServer worldServer = (WorldServer) player.world;
                PlayerChunkMap playerManager = worldServer.getPlayerChunkMap();

                int chunkX = ((int) player.posX) >> 4;
                int chunkZ = ((int) player.posZ) >> 4;

                for (Object playerObj : worldServer.playerEntities) {
                  if (playerObj instanceof EntityPlayerMP && playerObj != player) {
                    EntityPlayerMP otherPlayer = (EntityPlayerMP) playerObj;
                    if (playerManager.isPlayerWatchingChunk(otherPlayer, chunkX, chunkZ)) {
                      PacketHandler.INSTANCE.sendTo(message, otherPlayer);
                    }
                  }
                }

                return null;
              }
            }
          }
        }
      }
      return null;
    }
  }

  public static class ClientHandler implements IMessageHandler<PacketDarkSteelSFXPacket, IMessage> {

    @Override
    public IMessage onMessage(PacketDarkSteelSFXPacket message, MessageContext ctx) {
      final UUID uid = message.uid;
      if (message.upgradeName != null && uid != null) {
        IDarkSteelUpgrade upgrade = UpgradeRegistry.getUpgrade(message.upgradeName);
        if (upgrade != null) {
          EntityPlayer otherPlayer = EnderIO.proxy.getClientWorld().getPlayerEntityByUUID(uid);
          if (otherPlayer != null) {
            upgrade.doMultiplayerSFX(otherPlayer);
          }
        }
      }
      return null;
    }
  }
}
