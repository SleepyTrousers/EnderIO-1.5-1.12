package crazypants.enderio.machines.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.ThreadedNetworkWrapper;

import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.machine.buffer.PacketBufferIO;
import crazypants.enderio.machines.machine.crafter.PacketCrafter;
import crazypants.enderio.machines.machine.farm.PacketFarmAction;
import crazypants.enderio.machines.machine.farm.PacketFarmLockedSlot;
import crazypants.enderio.machines.machine.farm.PacketUpdateNotification;
import crazypants.enderio.machines.machine.generator.combustion.PacketCombustionTank;
import crazypants.enderio.machines.machine.generator.stirling.PacketBurnTime;
import crazypants.enderio.machines.machine.generator.zombie.PacketNutrientTank;
import crazypants.enderio.machines.machine.killera.PacketSwing;
import crazypants.enderio.machines.machine.obelisk.weather.PacketActivateWeather;
import crazypants.enderio.machines.machine.obelisk.weather.PacketWeatherTank;
import crazypants.enderio.machines.machine.sagmill.PacketGrindingBall;
import crazypants.enderio.machines.machine.spawner.PacketSpawnerUpdateNotification;
import crazypants.enderio.machines.machine.tank.PacketTankFluid;
import crazypants.enderio.machines.machine.tank.PacketTankVoidMode;
import crazypants.enderio.machines.machine.teleport.packet.PacketDrainStaff;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketOpenServerGui;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketSetTarget;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTargetList;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTelePadFluidLevel;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTeleport;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTeleportTrigger;
import crazypants.enderio.machines.machine.transceiver.PacketSendRecieveChannel;
import crazypants.enderio.machines.machine.transceiver.PacketSendRecieveChannelList;
import crazypants.enderio.machines.machine.vacuum.chest.PacketVaccumChest;
import crazypants.enderio.machines.machine.vat.PacketDumpTank;
import crazypants.enderio.machines.machine.vat.PacketTanks;
import crazypants.enderio.machines.machine.vat.PacketVatProgress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

  public static final @Nonnull ThreadedNetworkWrapper INSTANCE = new ThreadedNetworkWrapper(EnderIOMachines.MODID); // sic! not DOMAIN!

  private static int ID = 0;

  public static int nextID() {
    return ID++;
  }

  public static void sendToAllAround(IMessage message, TileEntity te) {
    INSTANCE.sendToAllAround(message, te);
  }

  public static void sendTo(@Nonnull IMessage message, EntityPlayerMP player) {
    INSTANCE.sendTo(message, player);
  }

  public static void sendToServer(@Nonnull IMessage message) {
    INSTANCE.sendToServer(message);
  }

  public static void init(FMLInitializationEvent event) {
    INSTANCE.registerMessage(PacketNutrientTank.Handler.class, PacketNutrientTank.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketBurnTime.Handler.class, PacketBurnTime.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketCombustionTank.Handler.class, PacketCombustionTank.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketBufferIO.Handler.class, PacketBufferIO.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketFarmAction.Handler.class, PacketFarmAction.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketUpdateNotification.Handler.class, PacketUpdateNotification.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketFarmLockedSlot.Handler.class, PacketFarmLockedSlot.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketGrindingBall.Handler.class, PacketGrindingBall.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketTankFluid.Handler.class, PacketTankFluid.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketTankVoidMode.Handler.class, PacketTankVoidMode.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketVaccumChest.Handler.class, PacketVaccumChest.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketTanks.Handler.class, PacketTanks.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketVatProgress.Handler.class, PacketVatProgress.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketDumpTank.Handler.class, PacketDumpTank.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketActivateWeather.Handler.class, PacketActivateWeather.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketActivateWeather.Handler.class, PacketActivateWeather.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketWeatherTank.Handler.class, PacketWeatherTank.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketSpawnerUpdateNotification.Handler.class, PacketSpawnerUpdateNotification.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketDrainStaff.Handler.class, PacketDrainStaff.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketTargetList.Handler.class, PacketTargetList.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketTargetList.Handler.class, PacketTargetList.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketOpenServerGui.Handler.class, PacketOpenServerGui.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketSetTarget.HandlerServer.class, PacketSetTarget.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketSetTarget.HandlerClient.class, PacketSetTarget.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketTeleportTrigger.Handler.class, PacketTeleportTrigger.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketTeleport.Handler.class, PacketTeleport.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketTelePadFluidLevel.Handler.class, PacketTelePadFluidLevel.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketSendRecieveChannel.Handler.class, PacketSendRecieveChannel.class, nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketSendRecieveChannelList.Handler.class, PacketSendRecieveChannelList.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketSwing.Handler.class, PacketSwing.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketCrafter.Handler.class, PacketCrafter.class, nextID(), Side.SERVER);
  }

}
