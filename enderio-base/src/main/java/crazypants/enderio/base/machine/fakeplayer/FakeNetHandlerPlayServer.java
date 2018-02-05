package crazypants.enderio.base.machine.fakeplayer;

import java.util.Set;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketPlayerPosLook.EnumFlags;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class FakeNetHandlerPlayServer extends NetHandlerPlayServer {

  public FakeNetHandlerPlayServer(EntityPlayerMP p_i1530_3_) {
    super(FMLCommonHandler.instance().getMinecraftServerInstance(), new NetworkManager(EnumPacketDirection.CLIENTBOUND), p_i1530_3_);
  }

  private int warnCount = 0;

  @Override
  public @Nonnull NetworkManager getNetworkManager() {
    if (warnCount++ < 10) {
      Log.warn("Someone is trying to send network packets to a fake player. This may crash and that is NOT Ender IO's fault.");
    }
    return super.netManager;
  }

  @Override
  public void processInput(@Nonnull CPacketInput p_147358_1_) {
  }

  @Override
  public void processPlayer(@Nonnull CPacketPlayer p_147347_1_) {
  }

  @Override
  public void setPlayerLocation(double p_147364_1_, double p_147364_3_, double p_147364_5_, float p_147364_7_, float p_147364_8_) {
  }

  @Override
  public void processPlayerDigging(@Nonnull CPacketPlayerDigging p_147345_1_) {
  }

  @Override
  public void onDisconnect(@Nonnull ITextComponent p_147231_1_) {
  }

  @Override
  public void sendPacket(@Nonnull Packet<?> p_147359_1_) {
  }

  @Override
  public void processHeldItemChange(@Nonnull CPacketHeldItemChange p_147355_1_) {
  }

  @Override
  public void processChatMessage(@Nonnull CPacketChatMessage p_147354_1_) {
  }

  @Override
  public void handleAnimation(@Nonnull CPacketAnimation packetIn) {

  }

  @Override
  public void processEntityAction(@Nonnull CPacketEntityAction p_147357_1_) {
  }

  @Override
  public void processUseEntity(@Nonnull CPacketUseEntity p_147340_1_) {
  }

  @Override
  public void processClientStatus(@Nonnull CPacketClientStatus p_147342_1_) {
  }

  @Override
  public void processCloseWindow(@Nonnull CPacketCloseWindow p_147356_1_) {
  }

  @Override
  public void processClickWindow(@Nonnull CPacketClickWindow p_147351_1_) {
  }

  @Override
  public void processEnchantItem(@Nonnull CPacketEnchantItem p_147338_1_) {
  }

  @Override
  public void processCreativeInventoryAction(@Nonnull CPacketCreativeInventoryAction p_147344_1_) {
  }

  @Override
  public void processConfirmTransaction(@Nonnull CPacketConfirmTransaction p_147339_1_) {
  }

  @Override
  public void processUpdateSign(@Nonnull CPacketUpdateSign p_147343_1_) {
  }

  @Override
  public void processKeepAlive(@Nonnull CPacketKeepAlive p_147353_1_) {
  }

  @Override
  public void processPlayerAbilities(@Nonnull CPacketPlayerAbilities p_147348_1_) {
  }

  @Override
  public void processTabComplete(@Nonnull CPacketTabComplete p_147341_1_) {
  }

  @Override
  public void processClientSettings(@Nonnull CPacketClientSettings p_147352_1_) {
  }

  @Override
  public void handleSpectate(@Nonnull CPacketSpectate packetIn) {
  }

  @Override
  public void handleResourcePackStatus(@Nonnull CPacketResourcePackStatus packetIn) {
  }

  @Override
  public void update() {
  }

  @Override
  public void disconnect(@Nonnull ITextComponent textComponent) {
  }

  @Override
  public void processVehicleMove(@Nonnull CPacketVehicleMove packetIn) {
  }

  @Override
  public void processConfirmTeleport(@Nonnull CPacketConfirmTeleport packetIn) {
  }

  @Override
  public void setPlayerLocation(double x, double y, double z, float yaw, float pitch, @Nonnull Set<EnumFlags> relativeSet) {
  }

  @Override
  public void processTryUseItemOnBlock(@Nonnull CPacketPlayerTryUseItemOnBlock packetIn) {
  }

  @Override
  public void processTryUseItem(@Nonnull CPacketPlayerTryUseItem packetIn) {
  }

  @Override
  public void processSteerBoat(@Nonnull CPacketSteerBoat packetIn) {
  }

  @Override
  public void processCustomPayload(@Nonnull CPacketCustomPayload packetIn) {
  }

}
