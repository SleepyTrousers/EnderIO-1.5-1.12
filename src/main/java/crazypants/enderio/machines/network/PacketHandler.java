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
import crazypants.enderio.machines.machine.sagmill.PacketGrindingBall;
import crazypants.enderio.machines.machine.tank.PacketTankFluid;
import crazypants.enderio.machines.machine.tank.PacketTankVoidMode;
import crazypants.enderio.machines.machine.vacuum.PacketVaccumChest;
import crazypants.enderio.machines.machine.wireless.PacketStoredEnergy;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

  public static final @Nonnull ThreadedNetworkWrapper INSTANCE = new ThreadedNetworkWrapper(EnderIOMachines.MODID); // sic! not DOMAIN!

  private static int ID = 0;

  public static int nextID() {
    return ID++;
  }

  public static void sendToAllAround(@Nonnull IMessage message, @Nonnull TileEntity te, int range) {
    BlockPos p = te.getPos();
    INSTANCE.sendToAllAround(message, new TargetPoint(te.getWorld().provider.getDimension(), p.getX(), p.getY(), p.getZ(), range));
  }

  public static void sendToAllAround(@Nonnull IMessage message, @Nonnull TileEntity te) {
    sendToAllAround(message, te, 64);
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

    PacketHandler.INSTANCE.registerMessage(PacketBufferIO.class, PacketBufferIO.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketFarmAction.class, PacketFarmAction.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketUpdateNotification.class, PacketUpdateNotification.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketFarmLockedSlot.class, PacketFarmLockedSlot.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketNutrientTank.class, PacketNutrientTank.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketSwing.class, PacketSwing.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketObeliskFx.class, PacketObeliskFx.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketGrindingBall.class, PacketGrindingBall.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketStoredEnergy.class, PacketStoredEnergy.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketTankFluid.class, PacketTankFluid.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketTankVoidMode.class, PacketTankVoidMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketVaccumChest.Handler.class, PacketVaccumChest.class, PacketHandler.nextID(), Side.SERVER);

  }

}
