package crazypants.enderio.base.handler.darksteel;

import java.util.Arrays;
import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry.AddCallback;
import net.minecraftforge.registries.IForgeRegistry.ClearCallback;
import net.minecraftforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = EnderIO.MODID)
public class UpgradeRegistry {

  private static final Comparator<IDarkSteelUpgrade> darkSteelUpgradeComperator = new Comparator<IDarkSteelUpgrade>() {
    @Override
    public int compare(IDarkSteelUpgrade o1, IDarkSteelUpgrade o2) {
      return o1.getUnlocalizedName().compareTo(o2.getUnlocalizedName());
    }
  };

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
        sortedList.sort(darkSteelUpgradeComperator);
        Arrays.stream(DarkSteelConfig.disabledUpgrades.get().split("\\s*[,;]\\s*")).map(s -> s != null ? new ResourceLocation(s) : null)
            .forEach(rl -> sortedList.remove(getUpgrade(rl)));
      }
    }
    return sortedList;
  }

  public static @Nullable IDarkSteelUpgrade getUpgrade(ResourceLocation id) {
    return REGISTRY.getValue(id);
  }

}
