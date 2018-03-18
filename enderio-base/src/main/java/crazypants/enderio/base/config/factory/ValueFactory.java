package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import net.minecraftforge.common.config.Configuration;

public class ValueFactory implements IRootFactory {

  private final @Nonnull String modid;
  private Configuration config = null;
  private boolean inInit = false;
  private int generation = 0;
  final private @Nonnull NNList<AbstractValue<?>> preloadValues = new NNList<>();

  public ValueFactory(@Nonnull String modid) {
    this.modid = modid;
  }

  @Override
  public @Nonnull IValueFactory section(@SuppressWarnings("hiding") @Nonnull String section) {
    return new SlaveFactory(this, section);
  }

  @Override
  public @Nonnull String getModid() {
    return modid;
  }

  @Override
  public @Nonnull String getSection() {
    return "";
  }

  public void setConfig(Configuration config) {
    this.config = config;
    generation++;
    inInit = true;
    for (AbstractValue<?> value : preloadValues) {
      value.get();
    }
    inInit = false;
    if (config.hasChanged()) {
      config.save();
    }
    // Note: Forge trashes the config when loading it from disk, so we need to re-configure all values every time that happens
    // preloadValues.clear();
  }

  @Override
  public Configuration getConfig() {
    return config;
  }

  @Override
  public boolean isInInit() {
    return inInit;
  }

  @Override
  public int getGeneration() {
    return generation;
  }

  @Override
  public void addPreloadValue(@Nonnull AbstractValue<?> value) {
    preloadValues.add(value);
  }

}
