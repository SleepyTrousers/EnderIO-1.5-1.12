package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

public enum FrankenSkull {

  ZOMBIE_ELECTRODE("skullZombieElectrode", false),
  ZOMBIE_CONTROLLER("skullZombieController", false),
  FRANKEN_ZOMBIE("skullZombieFrankenstien", "enderio:skullZombieController", true),
  ENDER_RESONATOR("skullEnderResonator", "enderio:skullEnderResonator", false),
  SENTIENT_ENDER("skullSentientEnder", "enderio:skullEnderResonator", true);
  
  public final String baseName;
  public final String unlocalisedName;
  public final String iconKey;
  public final boolean isAnimated;
  
  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for(FrankenSkull c : values()) {
      res.add(new ResourceLocation(c.iconKey));
    }
    return res;
  }
  
  private FrankenSkull(String baseName, String iconKey, boolean isAnimated) {
    this.baseName = baseName;
    this.unlocalisedName = baseName;
    this.iconKey = iconKey;
    this.isAnimated = isAnimated;
  }
  
  private FrankenSkull(String unlocalisedName, boolean isAnimated) {
    this(unlocalisedName, "enderio:" + unlocalisedName, isAnimated);    
  }

  public String getUnlocalisedName() {
    return unlocalisedName;
  }

  public String getIconKey() {
    return iconKey;
  }
  
}
