package crazypants.enderio.machine.farm;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

public class FakeNetHandlerPlayServer extends NetHandlerPlayServer {

  public FakeNetHandlerPlayServer(EntityPlayerMP p_i1530_3_) {
    super(MinecraftServer.getServer(), new net.minecraft.network.NetworkManager(false), p_i1530_3_);
  }

  @Override
  public void onNetworkTick() {
  }

  @Override
  public NetworkManager func_147362_b() {
    return null;
  }

  @Override
  public void kickPlayerFromServer(String p_147360_1_) {
  }

  @Override
  public void processInput(C0CPacketInput p_147358_1_) {
  }

  @Override
  public void processPlayer(C03PacketPlayer p_147347_1_) {
  }

  @Override
  public void setPlayerLocation(double p_147364_1_, double p_147364_3_, double p_147364_5_, float p_147364_7_, float p_147364_8_) {
  }

  @Override
  public void processPlayerDigging(C07PacketPlayerDigging p_147345_1_) {
  }

  @Override
  public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement p_147346_1_) {
  }

  @Override
  public void onDisconnect(IChatComponent p_147231_1_) {
  }

  @Override
  public void sendPacket(Packet p_147359_1_) {
  }

  @Override
  public void processHeldItemChange(C09PacketHeldItemChange p_147355_1_) {
  }

  @Override
  public void processChatMessage(C01PacketChatMessage p_147354_1_) {
  }

  @Override
  public void processAnimation(C0APacketAnimation p_147350_1_) {
  }

  @Override
  public void processEntityAction(C0BPacketEntityAction p_147357_1_) {
  }

  @Override
  public void processUseEntity(C02PacketUseEntity p_147340_1_) {
  }

  @Override
  public void processClientStatus(C16PacketClientStatus p_147342_1_) {
  }

  @Override
  public void processCloseWindow(C0DPacketCloseWindow p_147356_1_) {
  }

  @Override
  public void processClickWindow(C0EPacketClickWindow p_147351_1_) {
  }

  @Override
  public void processEnchantItem(C11PacketEnchantItem p_147338_1_) {
  }

  @Override
  public void processCreativeInventoryAction(C10PacketCreativeInventoryAction p_147344_1_) {
  }

  @Override
  public void processConfirmTransaction(C0FPacketConfirmTransaction p_147339_1_) {
  }

  @Override
  public void processUpdateSign(C12PacketUpdateSign p_147343_1_) {
  }

  @Override
  public void processKeepAlive(C00PacketKeepAlive p_147353_1_) {
  }

  @Override
  public void processPlayerAbilities(C13PacketPlayerAbilities p_147348_1_) {
  }

  @Override
  public void processTabComplete(C14PacketTabComplete p_147341_1_) {
  }

  @Override
  public void processClientSettings(C15PacketClientSettings p_147352_1_) {
  }

  @Override
  public void processVanilla250Packet(C17PacketCustomPayload p_147349_1_) {
  }

  @Override
  public void onConnectionStateTransition(EnumConnectionState p_147232_1_, EnumConnectionState p_147232_2_) {
  }

}
