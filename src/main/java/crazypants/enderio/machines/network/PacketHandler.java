package crazypants.enderio.machines.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.ThreadedNetworkWrapper;

import crazypants.enderio.base.config.PacketConfigSyncNew;
import crazypants.enderio.base.config.PacketConfigSyncNew.PacketConfigSyncNewHandler;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.Config;
import crazypants.enderio.machines.machine.buffer.PacketBufferIO;
import crazypants.enderio.machines.machine.farm.PacketFarmAction;
import crazypants.enderio.machines.machine.farm.PacketFarmLockedSlot;
import crazypants.enderio.machines.machine.farm.PacketUpdateNotification;
import crazypants.enderio.machines.machine.generator.combustion.PacketCombustionTank;
import crazypants.enderio.machines.machine.generator.stirling.PacketBurnTime;
import crazypants.enderio.machines.machine.generator.zombie.PacketNutrientTank;
import crazypants.enderio.machines.machine.killera.PacketSwing;
import crazypants.enderio.machines.machine.obelisk.PacketObeliskFx;
import crazypants.enderio.machines.machine.obelisk.weather.PacketActivateWeather;
import crazypants.enderio.machines.machine.obelisk.weather.PacketWeatherTank;
import crazypants.enderio.machines.machine.sagmill.PacketGrindingBall;
import crazypants.enderio.machines.machine.spawner.PacketSpawnerUpdateNotification;
import crazypants.enderio.machines.machine.tank.PacketTankFluid;
import crazypants.enderio.machines.machine.tank.PacketTankVoidMode;
import crazypants.enderio.machines.machine.teleport.packet.PacketDrainStaff;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketFluidLevel;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketOpenServerGui;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketSetTarget;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTargetList;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTeleport;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTeleportTrigger;
import crazypants.enderio.machines.machine.transceiver.PacketItemFilter;
import crazypants.enderio.machines.machine.transceiver.PacketSendRecieveChannel;
import crazypants.enderio.machines.machine.transceiver.PacketSendRecieveChannelList;
import crazypants.enderio.machines.machine.vacuum.PacketVaccumChest;
import crazypants.enderio.machines.machine.vat.PacketDumpTank;
import crazypants.enderio.machines.machine.vat.PacketTanks;
import crazypants.enderio.machines.machine.vat.PacketVatProgress;
import crazypants.enderio.machines.machine.wireless.PacketStoredEnergy;
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
    INSTANCE.registerMessage(new PacketConfigSyncNewHandler(Config.F), PacketConfigSyncNew.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketNutrientTank.class, PacketNutrientTank.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketBurnTime.class, PacketBurnTime.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketCombustionTank.class, PacketCombustionTank.class, nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketBufferIO.class, PacketBufferIO.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketFarmAction.class, PacketFarmAction.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketUpdateNotification.class, PacketUpdateNotification.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketFarmLockedSlot.class, PacketFarmLockedSlot.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketNutrientTank.class, PacketNutrientTank.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketSwing.class, PacketSwing.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketObeliskFx.class, PacketObeliskFx.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketGrindingBall.class, PacketGrindingBall.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketStoredEnergy.class, PacketStoredEnergy.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketTankFluid.class, PacketTankFluid.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketTankVoidMode.class, PacketTankVoidMode.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketVaccumChest.Handler.class, PacketVaccumChest.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketTanks.class, PacketTanks.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketVatProgress.class, PacketVatProgress.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketDumpTank.class, PacketDumpTank.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketActivateWeather.class, PacketActivateWeather.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketActivateWeather.class, PacketActivateWeather.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketWeatherTank.class, PacketWeatherTank.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketSpawnerUpdateNotification.class, PacketSpawnerUpdateNotification.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketDrainStaff.class, PacketDrainStaff.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketTargetList.class, PacketTargetList.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketTargetList.class, PacketTargetList.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketOpenServerGui.class, PacketOpenServerGui.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketSetTarget.class, PacketSetTarget.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketSetTarget.class, PacketSetTarget.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketTeleportTrigger.class, PacketTeleportTrigger.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketTeleport.class, PacketTeleport.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketFluidLevel.class, PacketFluidLevel.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketSendRecieveChannel.class, PacketSendRecieveChannel.class, PacketHandler.nextID(), Side.SERVER);
    INSTANCE.registerMessage(PacketSendRecieveChannelList.class, PacketSendRecieveChannelList.class, PacketHandler.nextID(), Side.CLIENT);
    INSTANCE.registerMessage(PacketItemFilter.class, PacketItemFilter.class, PacketHandler.nextID(), Side.SERVER);

  }

}
