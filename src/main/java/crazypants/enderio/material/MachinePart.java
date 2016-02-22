package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public enum MachinePart {

  MACHINE_CHASSI("machineChassi"),
  BASIC_GEAR("basicGear");

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
    this.baseName = baseName;
    this.unlocalisedName = "enderio." + baseName;
    this.iconKey = "enderio:" + baseName;
    this.oreDict = "item" + StringUtils.capitalize(baseName);
  }
  
  public static void registerOres(Item item) {
    for (MachinePart m : values()) {
      OreDictionary.registerOre(m.oreDict, new ItemStack(item, 1, m.ordinal()));
    }
  }
}
