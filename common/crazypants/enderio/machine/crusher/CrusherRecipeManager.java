package crazypants.enderio.machine.crusher;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.oredict.OreDictionary;

public class CrusherRecipeManager {

  private static CrusherRecipeManager instance;
  
  public static void addRecipes() {
    instance = new CrusherRecipeManager();
    instance.createRecipes();
    MinecraftForge.EVENT_BUS.register(instance);
  }
  
  static enum Type {
    ORE,
    INGOT
  }

  private Map<String, ItemStack> ores = new HashMap<String, ItemStack>();
  private Map<String, ItemStack> ingots = new HashMap<String, ItemStack>();
  private Map<String, ItemStack> dusts = new HashMap<String, ItemStack>();

  private static String[] CANDIDATE_ORES = {
      "Obsidian", "EnderPearl",
      "Coal", "Iron",
      "Gold", "Invar",
      "Aluminium", "Electrum",
      "Charcoal", "Copper",
      "Tin", "Silver",
      "Lead", "Bronze",
      "Brass", "Platinum",
      "Nickel","CertusQuartz",
      "NetherQuartz" };

  private static String[] ONE_TO_ONE_ORES = {
      "Obsidian", "EnderPearl", "Charcoal", "Coal", "Quartz"
  };

  @ForgeSubscribe
  public void onOreDictionaryRegister(OreDictionary.OreRegisterEvent event) {
    oreRegistered(event.Name, event.Ore);
  }

  public void createRecipes() {
    addVanillaEntires();
    for (String name : OreDictionary.getOreNames()) {
      for (ItemStack item : OreDictionary.getOres(name)) {
        oreRegistered(name, item);
      }
    }
  }

  private void addRecipe(ItemStack input, ItemStack output, Type type) {
    System.out.println("CrusherRecipeManager.addRecipe: Recipe added conveting:");    
    System.out.println("CrusherRecipeManager.addRecipe:      " + input.getItemName() + " to " + output.stackSize + " " + output.getItemName());
  }

  private void addVanillaEntires() {
    addOre("Gold", new ItemStack(Block.oreGold));
    addOre("Iron", new ItemStack(Block.oreIron));
    addIngot("Gold", new ItemStack(Item.ingotGold));
    addIngot("Iron", new ItemStack(Item.ingotIron));
    addOre("Obsidian", new ItemStack(Block.obsidian));
    addOre("EnderPearl", new ItemStack(Item.enderPearl));
    addOre("Coal", new ItemStack(Item.coal));
    addOre("Charcoal", new ItemStack(Item.coal, 1, 1));
    addOre("NetherQuartz", new ItemStack(Item.netherQuartz));
  }

  private int getDustToOreRatio(String name) {
    for (String s : ONE_TO_ONE_ORES) {
      if (s.equals(name)) {
        return 1;
      }
    }
    return 2;
  }

  private void addOre(String name, ItemStack item) {
    if (ores.containsKey(name)) {
      return;
    }
    ores.put(name, item);
    if (dusts.containsKey(name)) {
      ItemStack input = item.copy();
      ItemStack output = dusts.get(name).copy();
      output.stackSize = getDustToOreRatio(name);
      addRecipe(input, output, Type.ORE);
    }
  }

  private void addIngot(String name, ItemStack item) {
    if (ingots.containsKey(name)) {
      return;
    }
    ingots.put(name, item);
    if (dusts.containsKey(name)) {
      ItemStack input = item.copy();
      ItemStack output = dusts.get(name).copy();
      addRecipe(input, output, Type.INGOT);
    }
  }

  private void addDust(String name, ItemStack item) {

    if (dusts.containsKey(name)) {
      return;
    }
    dusts.put(name, item);

    if (ores.containsKey(name)) {
      ItemStack input = ores.get(name).copy();
      ItemStack output = item.copy();
      output.stackSize = getDustToOreRatio(name);
      addRecipe(input, output, Type.ORE);
    }
    if (ingots.containsKey(name)) {
      ItemStack input = ingots.get(name).copy();
      ItemStack output = item.copy();
      addRecipe(input, output, Type.INGOT);
    }
  }

  public void oreRegistered(String name, ItemStack item) {
    if (name == null || item == null) {
      return;
    }
    for (String ore : CANDIDATE_ORES) {
      if (name.equals("ore" + ore)) {
        addOre(ore, item);
      } else if (name.equals("ingot" + ore)) {
        addIngot(ore, item);
      } else if (name.equals("dust" + ore)) {
        addDust(ore, item);
      }
    }
  }

}
