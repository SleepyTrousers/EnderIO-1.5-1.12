package crazypants.enderio.base.farming.registry;

import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.farming.farmers.CustomSeedFarmer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;

class RegistryCallbacks implements IForgeRegistry.AddCallback<IFarmerJoe>, IForgeRegistry.ClearCallback<IFarmerJoe>, IForgeRegistry.CreateCallback<IFarmerJoe> {

  @Override
  public void onCreate(IForgeRegistryInternal<IFarmerJoe> owner, RegistryManager stage) {
    owner.setSlaveMap(Registry.PRIOLIST, new NNList<ResourceLocation>());
  }

  @Override
  public void onClear(IForgeRegistryInternal<IFarmerJoe> owner, RegistryManager stage) {
    owner.getSlaveMap(Registry.PRIOLIST, NNList.class).clear();
  }

  @Override
  public void onAdd(IForgeRegistryInternal<IFarmerJoe> owner, RegistryManager stage, int id, IFarmerJoe joe, @Nullable IFarmerJoe oldObj) {
    owner.getSlaveMap(Registry.PRIOLIST, NNList.class).add(joe.getRegistryName());
    if (joe instanceof CustomSeedFarmer) {
      CustomSeedFarmer customSeedFarmer = (CustomSeedFarmer) joe;
      if (customSeedFarmer.doesDisableTreeFarm())
        Commune.disableTrees.add(customSeedFarmer.getSeeds());
    }
  }

}