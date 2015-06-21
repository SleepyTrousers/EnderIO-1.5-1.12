package crazypants.enderio.material;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.StringUtils;

public enum MachinePart {

  MACHINE_CHASSI("machineChassi"),
  BASIC_GEAR("basicGear");

  public final String unlocalisedName;
  public final String iconKey;
  public final String oreDict;

  private MachinePart(String unlocalisedName) {
    this.unlocalisedName = "enderio." + unlocalisedName;
    this.iconKey = "enderio:" + unlocalisedName;
    this.oreDict = "item" + StringUtils.capitalize(unlocalisedName);
  }
  
  public static void registerOres(Item item) {
    for (MachinePart m : values()) {
      OreDictionary.registerOre(m.oreDict, new ItemStack(item, 1, m.ordinal()));
    }
  }
}
