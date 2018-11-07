package crazypants.enderio.base.recipe.soul;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class SoulBinderRecipeManager {

  private static final @Nonnull SoulBinderRecipeManager instance = new SoulBinderRecipeManager();

  public static final @Nonnull String KEY_RECIPE_UID = "recipeUID";
  public static final @Nonnull String KEY_INPUT_STACK = "inputStack";
  public static final @Nonnull String KEY_OUTPUT_STACK = "outputStack";
  public static final @Nonnull String KEY_REQUIRED_ENERGY = "requiredEnergyRF";
  public static final @Nonnull String KEY_REQUIRED_XP = "requiredXP";
  public static final @Nonnull String KEY_SOUL_TYPES = "entityTypes";

  public static SoulBinderRecipeManager getInstance() {
    return instance;
  }

  //@formatter:off
  /**
   * Example of how to add a recipe:
   * 
   * <pre> NBTTagCompound root = new NBTTagCompound();
   * root.setString(SoulBinderRecipeManager.KEY_RECIPE_UID, "diamondToWood");
   * root.setInteger(SoulBinderRecipeManager.KEY_REQUIRED_ENERGY, 50000);
   * root.setInteger(SoulBinderRecipeManager.KEY_REQUIRED_XP, 7);
   * root.setString(SoulBinderRecipeManager.KEY_SOUL_TYPES, "minecraft:zombie|specialmobs:specialzombie|minecraft:villager");
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
   * FMLInterModComms.sendMessage("EnderIO",  "recipe:soulbinder", root);</pre>
   * 
   * @param root
   * @return
   */
  //@formatter:on
  public boolean addRecipeFromNBT(@Nonnull NBTTagCompound root) {
    try {
      String recipeUid = root.getString(KEY_RECIPE_UID);
      if (recipeUid.trim().length() == 0) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as recipe UID not set: " + root);
        return false;
      }
      ItemStack inputStack = getStackFromRoot(root, KEY_INPUT_STACK);
      if (Prep.isInvalid(inputStack)) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as no input stack defined: " + root);
        return false;
      }
      ItemStack outputStack = getStackFromRoot(root, KEY_OUTPUT_STACK);
      if (Prep.isInvalid(outputStack)) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as no output stack defined: " + root);
        return false;
      }

      int energyRequired = root.getInteger(KEY_REQUIRED_ENERGY);
      if (energyRequired <= 0) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as energy required was <= 0: " + root);
        return false;
      }
      int xpLevelsRequired = root.getInteger(KEY_REQUIRED_XP);
      if (xpLevelsRequired <= 0) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as XP required was <= 0: " + root);
        return false;
      }

      String str = root.getString(KEY_SOUL_TYPES);
      if (str.trim().length() == 0) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as no soul types defined: " + root);
        return false;
      }
      String[] entityNames = str.split("\\|");
      NNList<ResourceLocation> entityRLs = new NNList<>();
      for (String string : entityNames) {
        if (string == null || string.trim().isEmpty()) {
          Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as no soul types contains emtpty entry: " + root);
          return false;
        }
        entityRLs.add(new ResourceLocation(string));
      }
      if (entityRLs.isEmpty()) {
        Log.error("SoulBinderRecipeManager: Could not add custom soul binder recipe from IMC as no soul types defined: " + root);
        return false;
      }

      BasicSoulBinderRecipe recipe = new BasicSoulBinderRecipe(inputStack, outputStack, energyRequired, xpLevelsRequired, recipeUid,
          entityRLs.toArray(new ResourceLocation[0]));

      MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.SOULBINDER, recipe);

      return true;
    } catch (Exception e) {
      Log.error("SoulBinderRecipeManager: Could not add custom soul binder exception thrown when parsing message: " + e);
      return false;
    }
  }

  private @Nonnull ItemStack getStackFromRoot(@Nonnull NBTTagCompound root, @Nonnull String string) {
    return new ItemStack(root.getCompoundTag(string));
  }

}
