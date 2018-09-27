package crazypants.enderio.base.farming.registry;

import java.util.Comparator;
import java.util.TreeMap;

import javax.annotation.Nullable;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.farming.farmers.CustomSeedFarmer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;

class RegistryCallbacks implements IForgeRegistry.AddCallback<IFarmerJoe>, IForgeRegistry.ClearCallback<IFarmerJoe>, IForgeRegistry.CreateCallback<IFarmerJoe> {

  @Override
  public void onCreate(IForgeRegistryInternal<IFarmerJoe> owner, RegistryManager stage) {
    owner.setSlaveMap(Registry.PRIOLIST, new PrioMap());
  }

  @Override
  public void onClear(IForgeRegistryInternal<IFarmerJoe> owner, RegistryManager stage) {
    owner.getSlaveMap(Registry.PRIOLIST, PrioMap.class).clear();
  }

  @Override
  public void onAdd(IForgeRegistryInternal<IFarmerJoe> owner, RegistryManager stage, int id, IFarmerJoe joe, @Nullable IFarmerJoe oldObj) {
    owner.getSlaveMap(Registry.PRIOLIST, PrioMap.class).put(joe, joe.getRegistryName());
    if (joe instanceof CustomSeedFarmer) {
      CustomSeedFarmer customSeedFarmer = (CustomSeedFarmer) joe;
      if (customSeedFarmer.doesDisableTreeFarm()) {
        Commune.disableTrees.add(customSeedFarmer.getSeeds());
      }
    }
  }

  public static final class PrioMap extends TreeMap<IFarmerJoe, ResourceLocation> {

    private static final long serialVersionUID = 4505789417181671182L;

    private PrioMap() {
      super(new Comparator<IFarmerJoe>() {
        @Override
        public int compare(IFarmerJoe o1, IFarmerJoe o2) {
          int comp1 = o1.getPriority().compareTo(o2.getPriority());
          if (comp1 != 0) {
            return comp1;
          }
          ResourceLocation rl1 = o1.getRegistryName();
          if (rl1 == null) {
            return Integer.compare(o1.hashCode(), o2.hashCode());
          }
          return rl1.compareTo(o2.getRegistryName());
        }
      });
    }

  }

}
