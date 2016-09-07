package crazypants.enderio.machine.soul;

import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SoulBinderRecipeManager {

  private static final SoulBinderRecipeManager instance = new SoulBinderRecipeManager();

  public static final String KEY_RECIPE_UID = "recipeUID";
  public static final String KEY_INPUT_STACK = "inputStack";
  public static final String KEY_OUTPUT_STACK = "outputStack";
  public static final String KEY_REQUIRED_ENERGY = "requiredEnergyRF";
  public static final String KEY_REQUIRED_XP = "requiredXP";
  public static final String KEY_SOUL_TYPES = "entityTypes";

  public static SoulBinderRecipeManager getInstance() {
    return instance;
  }

  public void addDefaultRecipes() {
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulBinder.getUnlocalisedName(), SoulBinderSpawnerRecipe.instance);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulBinder.getUnlocalisedName(), SoulBinderTunedPressurePlateRecipe.instance1);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulBinder.getUnlocalisedName(), SoulBinderTunedPressurePlateRecipe.instance2);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulBinder.getUnlocalisedName(), SoulBinderReanimationRecipe.instance);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulBinder.getUnlocalisedName(), SoulBinderSentientRecipe.instance);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulBinder.getUnlocalisedName(), SoulBinderEnderCystalRecipe.instance);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulBinder.getUnlocalisedName(), SoulBinderAttractorCystalRecipe.instance);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulBinder.getUnlocalisedName(), SoulBinderPrecientCystalRecipe.instance);

    //Ender Rail
//    if(Config.transceiverEnabled && Config.enderRailEnabled) {
//      BasicSoulBinderRecipe err = new BasicSoulBinderRecipe(new ItemStack(Blocks.detector_rail), new ItemStack(EnderIO.blockEnderRail),
//          Config.soulBinderEnderRailRF, Config.soulBinderEnderRailLevels, "EnderRail", "SpecialMobs.SpecialEnderman", "Enderman");
//      MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulBinder.unlocalisedName, err);
//    }
  }

  // Example of how to add a recipe:
  //
  //  NBTTagCompound root = new NBTTagCompound();
  //  root.setString(SoulBinderRecipeManager.KEY_RECIPE_UID, "diamondToWood");
  //  root.setInteger(SoulBinderRecipeManager.KEY_REQUIRED_ENERGY, 50000);
  //  root.setInteger(SoulBinderRecipeManager.KEY_REQUIRED_XP, 7);
  //  root.setString(SoulBinderRecipeManager.KEY_SOUL_TYPES, "Zombie|SpecialMobs.SpecialZombie|Villager");
  //  ItemStack is = new ItemStack(Items.diamond);
  //  NBTTagCompound stackRoot = new NBTTagCompound();
  //  is.writeToNBT(stackRoot);
  //  root.setTag(SoulBinderRecipeManager.KEY_INPUT_STACK, stackRoot);
  //  is = new ItemStack(Blocks.planks);
  //  stackRoot = new NBTTagCompound();
  //  is.writeToNBT(stackRoot);
  //  root.setTag(SoulBinderRecipeManager.KEY_OUTPUT_STACK, stackRoot);
  //
  //  SoulBinderRecipeManager.getInstance().addRecipeFromNBT(root);
  //  FMLInterModComms.sendMessage("EnderIO",  "recipe:soulbinder", root);

  //@formatter:off
  /**
   * Example of how to add a recipe:
   * 
   * NBTTagCompound root = new NBTTagCompound();
   * root.setString(SoulBinderRecipeManager.KEY_RECIPE_UID, "diamondToWood");
   * root.setInteger(SoulBinderRecipeManager.KEY_REQUIRED_ENERGY, 50000);
   * root.setInteger(SoulBinderRecipeManager.KEY_REQUIRED_XP, 7);
   * root.setString(SoulBinderRecipeManager.KEY_SOUL_TYPES, "Zombie|SpecialMobs.SpecialZombie|Villager");
   * ItemStack is = new ItemStack(Items.diamond);
   * NBTTagCompound stackRoot = new NBTTagCompound();
   * is.writeToNBT(stackRoot);
   * root.setTag(SoulBinderRecipeManager.KEY_INPUT_STACK, stackRoot);
   * is = new ItemStack(Blocks.planks);
   * stackRoot = new NBTTagCompound();
   * is.writeToNBT(stackRoot);
   * root.setTag(SoulBinderRecipeManager.KEY_OUTPUT_STACK, stackRoot);
   * 
   * SoulBinderRecipeManager.getInstance().addRecipeFromNBT(root);
   * FMLInterModComms.sendMessage("EnderIO",  "recipe:soulbinder", root);
   * 
   * @param root
   * @return
   */
  //@formatter:on
  public boolean addRecipeFromNBT(NBTTagCompound root) {
    try {
      String recipeUid = root.getString(KEY_RECIPE_UID);
      if(recipeUid == null || recipeUid.trim().length() == 0) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as recipe UID not set: " + root);
        return false;
      }
      ItemStack inputStack = getStackFromRoot(root, KEY_INPUT_STACK);
      if(inputStack == null) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as no input stack defined: " + root);
        return false;
      }
      ItemStack outputStack = getStackFromRoot(root, KEY_OUTPUT_STACK);
      if(outputStack == null) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as no output stack defined: " + root);
        return false;
      }

      int energyRequired = root.getInteger(KEY_REQUIRED_ENERGY);
      if(energyRequired <= 0) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as energy required was <= 0: " + root);
        return false;
      }
      int xpLevelsRequired = root.getInteger(KEY_REQUIRED_XP);
      if(xpLevelsRequired <= 0) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as energy required was <= 0: " + root);
        return false;
      }

      String str = root.getString(KEY_SOUL_TYPES);
      if(str == null || str.trim().length() == 0) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as no soul types defined: " + root);
        return false;
      }
      String[] entityNames = str.split("\\|");

      BasicSoulBinderRecipe recipe = new BasicSoulBinderRecipe(inputStack, outputStack, energyRequired, xpLevelsRequired, recipeUid, entityNames);

      MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulBinder.getUnlocalisedName(), recipe);

      return true;
    } catch (Exception e) {
      Log.error("SoulBinderRecipeManager: Could not add custom soul binder exception thrown when parsing message: " + e);
      return false;
    }
  }

  private ItemStack getStackFromRoot(NBTTagCompound root, String string) {
    NBTTagCompound stackRoot = root.getCompoundTag(string);
    if(stackRoot == null) {
      return null;
    }
    return ItemStack.loadItemStackFromNBT(stackRoot);
  }

}
