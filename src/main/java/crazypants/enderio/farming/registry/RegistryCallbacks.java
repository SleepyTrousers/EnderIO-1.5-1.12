package crazypants.enderio.farming.registry;

import java.util.Map;

import com.enderio.core.common.util.NNList;
import com.google.common.collect.BiMap;

import crazypants.enderio.farming.farmers.CustomSeedFarmer;
import crazypants.enderio.farming.farmers.IFarmerJoe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

class RegistryCallbacks implements IForgeRegistry.AddCallback<IFarmerJoe>, IForgeRegistry.ClearCallback<IFarmerJoe>, IForgeRegistry.CreateCallback<IFarmerJoe> {

  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(Map<ResourceLocation, ?> slaveset, BiMap<ResourceLocation, ? extends IForgeRegistry<?>> doNotTouch) {
    ((Map<ResourceLocation, NNList<ResourceLocation>>) slaveset).put(Registry.PRIOLIST, new NNList<ResourceLocation>());
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onClear(IForgeRegistry<IFarmerJoe> joe, Map<ResourceLocation, ?> slaveset) {
    ((Map<ResourceLocation, NNList<ResourceLocation>>) slaveset).get(Registry.PRIOLIST).clear();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onAdd(IFarmerJoe joe, int id, Map<ResourceLocation, ?> slaveset) {
    ((Map<ResourceLocation, NNList<ResourceLocation>>) slaveset).get(Registry.PRIOLIST).add(joe.getRegistryName());
    if (joe instanceof CustomSeedFarmer) {
      CustomSeedFarmer customSeedFarmer = (CustomSeedFarmer) joe;
      if (customSeedFarmer.doesDisableTreeFarm())
        Commune.disableTrees.add(customSeedFarmer.getSeeds());
    }
  }

}