package crazypants.enderio.base.handler.darksteel;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.upgrades.ItemUpgrades;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry.AddCallback;
import net.minecraftforge.registries.IForgeRegistry.ClearCallback;
import net.minecraftforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = EnderIO.MODID)
public class UpgradeRegistry {

  private static IForgeRegistry<IDarkSteelUpgrade> REGISTRY = null;

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerRegistry(@Nonnull RegistryEvent.NewRegistry event) {
    REGISTRY = new RegistryBuilder<IDarkSteelUpgrade>().setName(new ResourceLocation(EnderIO.DOMAIN, "upgrades")).setType(IDarkSteelUpgrade.class)
        .setIDRange(0, Integer.MAX_VALUE - 1).add((AddCallback<IDarkSteelUpgrade>) (owner, stage, id, obj, oldObj) -> sortedList.clear())
        .add((ClearCallback<IDarkSteelUpgrade>) (owner, stage) -> sortedList.clear()).create();
  }

  private static final @Nonnull NNList<IDarkSteelUpgrade> sortedList = new NNList<>();

  public static @Nonnull NNList<IDarkSteelUpgrade> getUpgrades() {
    if (sortedList.isEmpty()) {
      synchronized (sortedList) {
        sortedList.clear();
        sortedList.addAll(REGISTRY.getValuesCollection());
        sortedList.sort(UpgradeRegistry::compare);
        Arrays.stream(DarkSteelConfig.disabledUpgrades.get().split("\\s*[,;]\\s*")).map(s -> s != null ? new ResourceLocation(s) : null)
            .forEach(rl -> sortedList.remove(getUpgrade(rl)));
      }
    }
    return sortedList;
  }

  protected static int compare(@Nonnull IDarkSteelUpgrade o1, @Nonnull IDarkSteelUpgrade o2) {
    final Pair<String, Integer> k1 = o1.getSortKey();
    final Pair<String, Integer> k2 = o2.getSortKey();
    return (k1.getLeft() + k1.getRight()).compareTo((k2.getLeft() + k2.getRight()));
  }

  public static @Nullable IDarkSteelUpgrade getUpgrade(ResourceLocation id) {
    return REGISTRY.getValue(id);
  }

  public static int getId(IDarkSteelUpgrade upgrade) {
    return ((ForgeRegistry<IDarkSteelUpgrade>) REGISTRY).getID(upgrade);
  }

  public static @Nonnull ItemStack getUpgradeItem(@Nonnull IDarkSteelUpgrade upgrade, boolean enabled) {
    return ItemUpgrades.setEnabled(((ItemUpgrades) ModObject.itemDarkSteelUpgrade.getItemNN()).withUpgrade(upgrade), enabled);
  }

  public static @Nonnull ItemStack getUpgradeItem(@Nonnull IDarkSteelUpgrade upgrade) {
    return getUpgradeItem(upgrade, true);
  }

  public static boolean isUpgradeItem(@Nonnull IDarkSteelUpgrade upgrade, @Nonnull ItemStack stack) {
    return stack.getCount() == 1 && stack.getItem() == ModObject.itemDarkSteelUpgrade.getItemNN() && ItemUpgrades.getUpgrade(stack) == upgrade
        && ItemUpgrades.isEnabled(stack);
  }

  public static @Nullable IDarkSteelUpgrade getUpgradeFromItem(@Nonnull ItemStack stack) {
    if (stack.getCount() == 1 && stack.getItem() == ModObject.itemDarkSteelUpgrade.getItemNN() && ItemUpgrades.isEnabled(stack)) {
      return ItemUpgrades.getUpgrade(stack);
    }
    return null;
  }

  @SuppressWarnings("null")
  public static @Nullable IDarkSteelUpgrade read(@Nonnull ByteBuf in) {
    return ByteBufUtils.readRegistryEntry(in, REGISTRY);
  }

}
