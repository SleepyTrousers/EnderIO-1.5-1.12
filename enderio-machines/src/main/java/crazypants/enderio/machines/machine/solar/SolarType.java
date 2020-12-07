package crazypants.enderio.machines.machine.solar;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.base.init.RegisterModObject;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.SolarConfig;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public class SolarType implements ISolarType {
  // Note: This is not an Enum because Enums implement Comparable, which conflicts with the
  // Comparable already on the interface forced onto us by Property

  @SubscribeEvent
  public static void registerRegistry(@Nonnull RegisterModObject event) {
    // we need to classload early so ISolarType.KIND is filled before it is used in the
    // Block registration
  }

  public static final @Nonnull SolarType SIMPLE = new SolarType("SIMPLE", ".simple");
  public static final @Nonnull SolarType NORMAL = new SolarType("NORMAL", "");
  public static final @Nonnull SolarType ADVANCED = new SolarType("ADVANCED", ".advanced");
  public static final @Nonnull SolarType VIBRANT = new SolarType("VIBRANT", ".vibrant") {
    @Override
    public boolean hasParticles() {
      return true;
    }

    @Override
    public @Nonnull Vector3d getParticleColor() {
      return new Vector3d(0x47 / 255d, 0x9f / 255d, 0xa3 / 255d);
    }
  };
  public static final @Nonnull SolarType COMPRESSED = new SolarType("COMPRESSED", ".compressed") {
    @Override
    public boolean hasParticles() {
      return true;
    }

    @Override
    public @Nonnull Vector3d getParticleColor() {
      return new Vector3d(0xd5 / 255d, 0x5a / 255d, 0x5a / 255d);
    }
  };
  public static final @Nonnull SolarType CONCENTRATED = new SolarType("CONCENTRATED", ".concentrated") {
    @Override
    public boolean hasParticles() {
      return true;
    }

    @Override
    public @Nonnull Vector3d getParticleColor() {
      return new Vector3d(0x01 / 255d, 0x01 / 255d, 0x01 / 255d);
    }
  };
  public static final @Nonnull SolarType ULTIMATE = new SolarType("ULTIMATE", ".ultimate") {
    @Override
    public boolean hasParticles() {
      return true;
    }

    @Override
    public @Nonnull Vector3d getParticleColor() {
      return new Vector3d(0xff / 255d, 0xff / 255d, 0xff / 255d);
    }
  };

  static {
    KIND.addValue(SIMPLE);
    KIND.addValue(NORMAL);
    KIND.addValue(ADVANCED);
    KIND.addValue(VIBRANT);
    if (Loader.isModLoaded("enderioendergy")) {
      KIND.addValue(COMPRESSED);
      KIND.addValue(CONCENTRATED);
      KIND.addValue(ULTIMATE);
    }
  }

  private final @Nonnull String name, unlocalisedName;

  private SolarType(@Nonnull String name, @Nonnull String unlocalisedName) {
    this.name = NullHelper.notnullJ(name.toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
    this.unlocalisedName = unlocalisedName;
  }

  @Override
  public @Nonnull String getName() {
    return name;
  }

  @Override
  public @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  @Override
  public int getRfperTick() {
    return SolarConfig.blockGen.get(ISolarType.getMetaFromType(this)).get();
  }

  @Override
  public int getRfperSecond() {
    return SolarConfig.upgradeGen.get(ISolarType.getMetaFromType(this)).get();
  }

  @Override
  public int getUpgradeLevelCost() {
    return SolarConfig.upgradeCost.get(ISolarType.getMetaFromType(this)).get();
  }

}