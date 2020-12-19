package crazypants.enderio.base.capacitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.capacitor.ICapacitorKey.UnconfiguredCapKeyException;
import crazypants.enderio.api.capacitor.Scaler;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.RecipeConfig;
import crazypants.enderio.base.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = EnderIO.MODID)
public class CapacitorKeyRegistry {

  private static IForgeRegistry<ICapacitorKey> REGISTRY = null;

  private static final @Nonnull Map<ICapacitorKey, Triple<Integer, Scaler, String>> BASE = new HashMap<>();

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerRegistry(@Nonnull RegistryEvent.NewRegistry event) {
    REGISTRY = new RegistryBuilder<ICapacitorKey>().setName(new ResourceLocation(EnderIO.DOMAIN, "capacitor")).setType(ICapacitorKey.class)
        .setIDRange(0, Integer.MAX_VALUE - 1).create();
  }

  public static void setValue(@Nonnull ResourceLocation name, int baseValue, @Nullable Scaler scaler, @Nonnull String scalerString) {
    ICapacitorKey key = REGISTRY.getValue(name);
    if (key == null) {
      throw new RuntimeException("Trying to configure non-existant key " + name);
    }
    Scaler theScaler = scaler != null ? scaler : NullHelper.notnull(ScalerFactory.fromString(scalerString), "Invalid scaler '", scalerString, "'");
    key.setBaseValue(baseValue);
    key.setScaler(theScaler);
    BASE.put(key, Triple.of(baseValue, theScaler, scalerString));
  }

  public static void validate() {
    for (ICapacitorKey key : REGISTRY.getValuesCollection()) {
      try {
        key.validate();
      } catch (UnconfiguredCapKeyException e) {
        if (!RecipeConfig.loadCoreRecipes.get()) {
          throw new UnconfiguredCapKeyException(
              "Ender IO core recipe loading has been disabled in the configuration and you have failed to provide a user recipe for: " + key.getRegistryName(),
              e);
        }
        throw e;
      }
    }
  }

  public static void resetOverrides() {
    for (Entry<ICapacitorKey, Triple<Integer, Scaler, String>> entry : BASE.entrySet()) {
      ICapacitorKey key = entry.getKey();
      Triple<Integer, Scaler, String> pair = entry.getValue();
      if (key != null && pair != null) {
        Integer baseValue = pair.getLeft();
        Scaler scaler = pair.getMiddle();
        if (baseValue != null && scaler != null) {
          key.setBaseValue(baseValue);
          key.setScaler(scaler);
        }
      }
    }
  }

  public static void addOverride(@Nonnull ResourceLocation name, int baseValue, @Nullable String scalerString) {
    ICapacitorKey key = REGISTRY.getValue(name);
    if (key == null) {
      throw new RuntimeException("Trying to override non-existant key " + name);
    }
    key.setBaseValue(baseValue);
    key.setScaler(NullHelper.notnull(ScalerFactory.fromString(scalerString), "Invalid scaler '", scalerString, "'"));
  }

  public static boolean contains(@Nonnull ResourceLocation name) {
    return REGISTRY.containsKey(name);
  }

  @SubscribeEvent
  public static void onPlayerLoggon(final PlayerLoggedInEvent evt) {
    PacketHandler.sendTo(new PacketCapacitorSync(BASE), (EntityPlayerMP) evt.player);
    Log.debug("Sending server config overrides for capacitor keys to player " + evt.player.getName());
  }

  @SubscribeEvent
  public static void onPlayerLogout(final ClientDisconnectionFromServerEvent event) {
    resetOverrides();
    Log.debug("Removed server config overrides for capacitor keys");
  }

  public static Iterable<ICapacitorKey> getAllKeys() {
    return REGISTRY;
  }

}
