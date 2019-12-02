package crazypants.enderio.base.handler.darksteel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class StateController {

  private static final @Nonnull String ENDERIO_FLAGS = EnderIO.DOMAIN + ":flags";

  private static NBTTagCompound getActiveSetNBT(@Nonnull EntityPlayer player) {
    NBTTagCompound entityData = player.getEntityData();
    if (entityData.hasKey(StateController.ENDERIO_FLAGS, Constants.NBT.TAG_COMPOUND)) {
      return entityData.getCompoundTag(StateController.ENDERIO_FLAGS);
    } else {
      NBTTagCompound set = new NBTTagCompound();
      entityData.setTag(StateController.ENDERIO_FLAGS, set);
      return set;
    }
  }

  /**
   * When a player starts tracking another player, sync a complete set of flags of the tracked player to the tracking player.
   * <p>
   * That way you'll see another player's glider wings when you log on and the other player has them active already.
   */
  @SubscribeEvent
  public static void onTracking(PlayerEvent.StartTracking event) {
    final EntityPlayer toUpdate = event.getEntityPlayer();
    if (toUpdate instanceof EntityPlayerMP) {
      final Entity target = event.getTarget();
      if (target instanceof EntityPlayer) {
        StateController.updateFlags((EntityPlayerMP) toUpdate, (EntityPlayer) target);
      }
    }
  }

  @SubscribeEvent
  public static void onLogin(PlayerLoggedInEvent event) {
    final EntityPlayer player = event.player;
    if (player instanceof EntityPlayerMP) {
      StateController.updateFlags((EntityPlayerMP) player, player);
    }
  }

  private static void updateFlags(@Nonnull EntityPlayerMP toUpdate, @Nonnull EntityPlayer target) {
    final NBTTagCompound activeSet = getActiveSetNBT(target);
    final PacketUpgradeState packet = new PacketUpgradeState(target.getEntityId());
    UpgradeRegistry.getUpgrades().apply((Callback<IDarkSteelUpgrade>) type -> {
      if (activeSet.hasKey(type.getKeybindingID())) {
        packet.add(type.getKeybindingID(), activeSet.getBoolean(type.getKeybindingID()));
      }
    });
    PacketHandler.sendTo(packet, toUpdate);
  }

  public static boolean isActive(@Nonnull EntityPlayer player, @Nullable IDarkSteelUpgrade type) {
    final NBTTagCompound activeSet = getActiveSetNBT(player);
    return type != null && (activeSet.hasKey(type.getKeybindingID()) ? activeSet.getBoolean(type.getKeybindingID()) : type.keybindingDefault());
  }

  public static void setActive(@Nonnull EntityPlayer player, @Nonnull IDarkSteelUpgrade type, boolean isActive) {
    StateController.setActive(player, type.getKeybindingID(), isActive);
  }

  public static void setActive(@Nonnull EntityPlayer player, @Nonnull String type, boolean isActive) {
    if (player.world.isRemote) {
      PacketHandler.INSTANCE.sendToServer(new PacketUpgradeState(type, isActive));
    } else {
      StateController.syncActive(player, type, isActive);
      PacketHandler.INSTANCE.sendToDimension(new PacketUpgradeState(type, isActive, player.getEntityId()), player.world.provider.getDimension());
    }
  }

  public static void syncActive(@Nonnull EntityPlayer player, @Nonnull String type, boolean isActive) {
    getActiveSetNBT(player).setBoolean(type, isActive);
  }

}
