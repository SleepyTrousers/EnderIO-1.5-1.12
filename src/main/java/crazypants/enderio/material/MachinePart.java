package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.StringUtils;

public enum MachinePart {

  MACHINE_CHASSI("machineChassi"),
  BASIC_GEAR("basicGear", "gearStone");

  public final String baseName;
  public final String unlocalisedName;
  public final String iconKey;
  public final String oreDict;

  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for(MachinePart c : values()) {
      res.add(new ResourceLocation(c.iconKey));
    }
    return res;
  }
  
  private MachinePart(String baseName) {
    this(baseName, "item" + StringUtils.capitalize(baseName));
  }

  private MachinePart(String baseName, String oreDict) {
    this.baseName = baseName;
    this.unlocalisedName = "enderio." + baseName;
    this.iconKey = "enderio:" + baseName;
    this.oreDict = oreDict;
  }
  
  public static void registerOres(Item item) {
    for (MachinePart m : values()) {
      OreDictionary.registerOre(m.oreDict, new ItemStack(item, 1, m.ordinal()));
    }
  }
}
