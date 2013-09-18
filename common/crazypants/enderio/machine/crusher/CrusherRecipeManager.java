package crazypants.enderio.machine.crusher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLLog;
import crazypants.enderio.ModObject;
import crazypants.enderio.material.PowderIngot;

public class CrusherRecipeManager {

  static final float ORE_ENERGY_COST = 400;

  static final float INGOT_ENERGY_COST = 240;

  static CrusherRecipeManager instance;

  public static void addRecipes() {
    instance = new CrusherRecipeManager();
    instance.createRecipes();
    MinecraftForge.EVENT_BUS.register(instance);
  }

  static enum Type {
    ORE(ORE_ENERGY_COST),
    INGOT(INGOT_ENERGY_COST);

    final float energyCost;

    private Type(float energyCost) {
      this.energyCost = energyCost;
    }

  }

  private static String[] CANDIDATE_ORES = {
      "Obsidian", "EnderPearl",
      "Coal", "Iron",
      "Gold", "Invar",
      "Aluminium", "Electrum",
      "Charcoal", "Copper",
      "Tin", "Silver",
      "Lead", "Bronze",
      "Brass", "Platinum",
      "Nickel", "CertusQuartz",
      "NetherQuartz" };

  private static String[] ONE_TO_ONE_ORES = {
      "Obsidian", "EnderPearl", "Charcoal", "Coal", "Quartz"
  };

  private final Map<String, List<ItemStack>> ores = new HashMap<String, List<ItemStack>>();
  private final Map<String, List<ItemStack>> ingots = new HashMap<String, List<ItemStack>>();
  private final Map<String, List<ItemStack>> dusts = new HashMap<String, List<ItemStack>>();
  private final List<CrusherRecipe> recipes = new ArrayList<CrusherRecipe>();

  // Different handling for AE stuff, there must be a better way but this will
  // do for now
  private ItemStack crystalCertusQuartz;

  @ForgeSubscribe
  public void onOreDictionaryRegister(OreDictionary.OreRegisterEvent event) {
    if(event != null) {
      oreRegistered(event.Name, event.Ore);
    }
  }

  public void createRecipes() {
    addVanillaOres();
    addDefaultRecipes();
    for (String name : OreDictionary.getOreNames()) {
      for (ItemStack item : OreDictionary.getOres(name)) {
        oreRegistered(name, item);
      }
    }
  }

  CrusherRecipe getRecipeForInput(ItemStack input) {
    if(input == null) {
      return null;
    }
    for (CrusherRecipe recipe : recipes) {
      if(recipe.isInput(input)) {
        return recipe;
      }
    }
    return null;
  }

  private void addDefaultRecipes() {
    addRecipe(new ItemStack(Block.stone), Type.INGOT, new ItemStack(Block.cobblestone));
    addRecipe(new ItemStack(Block.cobblestone), Type.INGOT, new ItemStack(Block.sand));
    addRecipe(new ItemStack(Block.sandStone), Type.INGOT, new ItemStack(Block.sand));
    addRecipe(new ItemStack(Block.glass), Type.INGOT, new ItemStack(Block.sand));
    addRecipe(new ItemStack(Item.blazeRod), Type.INGOT, new ItemStack(Item.blazePowder, 4));
    addRecipe(new ItemStack(Block.glowStone), Type.INGOT, new ItemStack(Item.glowstone, 4));
    addRecipe(new ItemStack(Block.gravel), Type.INGOT, new ItemStack(Item.flint));
    addRecipe(new ItemStack(Item.bone), Type.INGOT, new ItemStack(Item.dyePowder, 6, 15));

    addRecipe(new ItemStack(Item.coal), Type.INGOT, new ItemStack(ModObject.itemPowderIngot.actualId, 1, PowderIngot.POWDER_COAL.ordinal()));
    addRecipe(new ItemStack(Block.oreCoal), Type.ORE, new ItemStack(ModObject.itemPowderIngot.actualId, 2, PowderIngot.POWDER_COAL.ordinal()));

    ItemStack ironDust = new ItemStack(ModObject.itemPowderIngot.actualId, 2, PowderIngot.POWDER_IRON.ordinal());
    ItemStack goldDust = new ItemStack(ModObject.itemPowderIngot.actualId, 1, PowderIngot.POWDER_GOLD.ordinal());
    addRecipe(new ItemStack(Block.oreIron), Type.ORE.energyCost, new CrusherOutput(ironDust), new CrusherOutput(goldDust, 0.2f));

    goldDust.stackSize = 2;
    addRecipe(new ItemStack(Block.oreGold), Type.ORE.energyCost, new CrusherOutput(goldDust));
  }

  private void addRecipe(ItemStack input, Type type, ItemStack output) {
    addRecipe(input, type.energyCost, new CrusherOutput(output, 1));
  }

  private void addRecipe(ItemStack input, float energyCost, CrusherOutput... output) {
    if(input == null || output == null) {
      return;
    }
    CrusherRecipe rec = getRecipeForInput(input);
    if(rec != null) {
      FMLLog.warning("Not adding supplied recipe as a recipe already exists for the input stack ItemID=" + input.itemID + " damage=" + input.getItemDamage());
      return;
    }
    recipes.add(new CrusherRecipe(input, energyCost, output));
  }

  private void mergeRecipe(ItemStack input, ItemStack output, Type type) {
    if(input == null || output == null || type == null) {
      return;
    }
    CrusherRecipe rec = getRecipeForInput(input);
    if(rec == null) {
      recipes.add(new CrusherRecipe(input, type.energyCost, new CrusherOutput(output, 1)));
    } else if(rec != null && isCloser(input, output, rec.getOutput()[0].getOutput())) {
      recipes.remove(rec);
      recipes.add(new CrusherRecipe(input, type.energyCost, new CrusherOutput(output, 1)));
    }

  }

  private void addVanillaOres() {
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
      if(s.equals(name)) {
        return 1;
      }
    }
    return 2;
  }

  private void addOre(String name, ItemStack item) {
    add(name, item, ores);

    if("CertusQuartz".equals(name)) {
      if(crystalCertusQuartz != null) {
        mergeRecipe(item.copy(), crystalCertusQuartz.copy(), Type.INGOT);
      }
    } else if(dusts.containsKey(name)) {
      ItemStack output = getBestMatch(name, item, dusts);
      if(output != null) {
        output = output.copy();
        output.stackSize = getDustToOreRatio(name);
        mergeRecipe(item.copy(), output, Type.ORE);
      }
    }
  }

  private void addIngot(String name, ItemStack item) {
    add(name, item, ingots);
    if(dusts.containsKey(name)) {
      ItemStack input = item.copy();
      ItemStack output = getBestMatch(name, input, dusts);
      if(output != null) {
        mergeRecipe(input, output.copy(), Type.INGOT);
      }
    }
  }

  private void addDust(String name, ItemStack item) {

    add(name, item, dusts);
    if("CertusQuartz".equals(name) && crystalCertusQuartz != null) {
      ItemStack input = crystalCertusQuartz.copy();
      ItemStack output = item.copy();
      mergeRecipe(input, output, Type.ORE);

    } else if(ores.containsKey(name)) {

      List<ItemStack> matchingOres = ores.get(name);
      if(matchingOres != null) {
        for (ItemStack input : matchingOres) {
          if(input != null) {
            ItemStack output = item.copy();
            output.stackSize = getDustToOreRatio(name);
            mergeRecipe(input.copy(), output, Type.ORE);
          }
        }
      }
    }
    if(ingots.containsKey(name)) {
      List<ItemStack> matchingIngots = ingots.get(name);
      if(matchingIngots != null) {
        for (ItemStack input : matchingIngots) {
          if(input != null) {
            mergeRecipe(input.copy(), item.copy(), Type.INGOT);
          }
        }
      }
    }
  }

  public void oreRegistered(String name, ItemStack item) {
    if(name == null || item == null) {
      return;
    }
    if(name.equals("crystalCertusQuartz")) {
      crystalCertusQuartz = item;
      if(dusts.containsKey("CertusQuartz")) {
        ItemStack output = getBestMatch(name, crystalCertusQuartz, dusts);
        if(output != null) {
          mergeRecipe(crystalCertusQuartz.copy(), output.copy(), Type.INGOT);
        }
      }
      if(ores.containsKey("CertusQuartz")) {
        ItemStack input = getBestMatch("CertusQuartz", crystalCertusQuartz, ores);
        if(input != null) {
          mergeRecipe(input.copy(), crystalCertusQuartz.copy(), Type.ORE);
        }
      }
    }

    for (String ore : CANDIDATE_ORES) {
      if(name.equals("ore" + ore)) {
        addOre(ore, item);
      } else if(name.equals("ingot" + ore)) {
        addIngot(ore, item);
      } else if(name.equals("dust" + ore)) {
        addDust(ore, item);
      }
    }
  }

  private void add(String name, ItemStack item, Map<String, List<ItemStack>> map) {
    List<ItemStack> list = map.get(name);
    if(list == null) {
      list = new ArrayList<ItemStack>();
      map.put(name, list);
    }
    list.add(item);
  }

  private ItemStack getBestMatch(String name, ItemStack from, Map<String, List<ItemStack>> map) {
    ItemStack result = null;
    int fromID = from.itemID;
    int minDiff = Integer.MAX_VALUE;
    List<ItemStack> list = map.get(name);
    if(list == null) {
      return null;
    }
    for (ItemStack item : list) {
      int diff = Math.abs(fromID - item.itemID);
      if(diff < minDiff) {
        minDiff = diff;
        result = item;
      }
    }
    return result;
  }

  private boolean isCloser(ItemStack input, ItemStack output1, ItemStack output2) {
    int diff1 = Math.abs(input.itemID - output1.itemID);
    int diff2 = Math.abs(input.itemID - output2.itemID);
    return diff1 < diff2;
  }

}
