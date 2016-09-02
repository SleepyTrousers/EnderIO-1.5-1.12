package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import net.minecraft.util.ResourceLocation;

public enum FrankenSkull {

  ZOMBIE_ELECTRODE("skullZombieElectrode", false),
  ZOMBIE_CONTROLLER("skullZombieController", false),
  FRANKEN_ZOMBIE("skullZombieFrankenstien", "enderio:skullZombieController", true),
  ENDER_RESONATOR("skullEnderResonator", "enderio:skullEnderResonator", false),
  SENTIENT_ENDER("skullSentientEnder", "enderio:skullEnderResonator", true),
  SKELETAL_CONTRACTOR("skullSkeletalContractor", "enderio:skullSkeletalContractor", false);
  
  public final @Nonnull String baseName;
  public final @Nonnull String unlocalisedName;
  public final @Nonnull String iconKey;
  public final boolean isAnimated;
  
  public static @Nonnull List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for(FrankenSkull c : values()) {
      res.add(new ResourceLocation(EnderIO.MODID, c.baseName));
    }
    return res;
  }
  
  private FrankenSkull(@Nonnull String baseName, @Nonnull String iconKey, boolean isAnimated) {
    this.baseName = baseName;
    this.unlocalisedName = baseName;
    this.iconKey = iconKey;
    this.isAnimated = isAnimated;
  }
  
  private FrankenSkull(@Nonnull String unlocalisedName, boolean isAnimated) {
    this(unlocalisedName, "enderio:" + unlocalisedName, isAnimated);
  }

  public @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  public @Nonnull String getIconKey() {
    return iconKey;
  }
  
}
