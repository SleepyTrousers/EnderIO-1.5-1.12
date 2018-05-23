package crazypants.enderio.api.farm;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class AbstractFarmerJoe extends IForgeRegistryEntry.Impl<IFarmerJoe> implements IFarmerJoe {

  private @Nonnull EventPriority priority = EventPriority.NORMAL;

  public AbstractFarmerJoe setPriority(@Nonnull EventPriority priority) {
    this.priority = priority;
    return this;
  }

  @Override
  @Nonnull
  public EventPriority getPriority() {
    return priority;
  }

}
