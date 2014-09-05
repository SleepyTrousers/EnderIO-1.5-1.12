package crazypants.enderio.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.enchanter.EnchanterRecipe;
import crazypants.enderio.machine.enchanter.EnchanterRecipeManager;
import crazypants.enderio.machine.enchanter.GuiEnchanter;
import crazypants.enderio.machine.enchanter.TileEnchanter;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.soul.GuiSoulBinder;
import crazypants.enderio.machine.soul.ISoulBinderRecipe;
import crazypants.enderio.nei.EnchanterRecipeHandler.EnchanterRecipeNEI;

public class SoulBinderRecipeHandler extends TemplateRecipeHandler {

  @Override
  public String getRecipeName() {
    return "Soul Binder";
  }

  @Override
  public String getGuiTexture() {
    return "enderio:textures/gui/soulFuser.png";
  }

  @Override
  public Class<? extends GuiContainer> getGuiClass() {
    return GuiSoulBinder.class;
  }

  @Override
  public String getOverlayIdentifier() {
    return "EnderIOSoulBinder";
  }

  @Override
  public void loadTransferRects() {
    transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(149, 32, 16, 16), "EnderIOSoulBinder", new Object[0]));
  }

  @Override
  public void loadCraftingRecipes(ItemStack result) {
    if(result == null || result.getItem() == null) {
      return;
    }

    Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForMachine(ModObject.blockSoulBinder.unlocalisedName);
    if(recipes.isEmpty()) {
      return;
    }
    for (IMachineRecipe recipe : recipes.values()) {
      if(recipe instanceof ISoulBinderRecipe) {
        ISoulBinderRecipe sbr = (ISoulBinderRecipe) recipe;
        if(sbr.getOutputStack().isItemEqual(result)) {
          arecipes.add(new SoulBinderRecipeNEI((ISoulBinderRecipe) recipe));
        }
      }
    }
  }

  @Override
  public void loadCraftingRecipes(String outputId, Object... results) {

    if(outputId.equals("EnderIOSoulBinder") && getClass() == SoulBinderRecipeHandler.class) {
      Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForMachine(ModObject.blockSoulBinder.unlocalisedName);
      if(recipes.isEmpty()) {
        return;
      }
      for (IMachineRecipe recipe : recipes.values()) {
        if(recipe instanceof ISoulBinderRecipe) {
          arecipes.add(new SoulBinderRecipeNEI((ISoulBinderRecipe) recipe));
        }
      }
    } else {
      super.loadCraftingRecipes(outputId, results);
    }
  }

  @Override
  public void loadUsageRecipes(ItemStack ingredient) {

    if(ingredient == null || ingredient.getItem() == null) {
      return;
    }

    Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForMachine(ModObject.blockSoulBinder.unlocalisedName);
    if(recipes.isEmpty()) {
      return;
    }
    for (IMachineRecipe recipe : recipes.values()) {
      if(recipe instanceof ISoulBinderRecipe) {
        ISoulBinderRecipe sbr = (ISoulBinderRecipe) recipe;
        if(sbr.getInputStack().isItemEqual(ingredient)) {
          arecipes.add(new SoulBinderRecipeNEI((ISoulBinderRecipe) recipe));
        }
      }
    }

  }

  @Override
  public void drawExtras(int recipeIndex) {
  }

  //  public List<ItemStack> getInputs(RecipeInput input) {
  //    List<ItemStack> result = new ArrayList<ItemStack>();
  //    result.add(input.getInput());
  //    ItemStack[] equivs = input.getEquivelentInputs();
  //    if(equivs != null && equivs.length > 0) {
  //      result.addAll(Arrays.asList(equivs));
  //    }
  //    return result;
  //  }

  private static final ArrayList<PositionedStack> EMPTY_VIAL_OUTPUT = new ArrayList<PositionedStack>();
  static {
    EMPTY_VIAL_OUTPUT.add(new PositionedStack(new ItemStack(EnderIO.itemSoulVessel), 107, 23));
  }

  public class SoulBinderRecipeNEI extends TemplateRecipeHandler.CachedRecipe {

    private final ArrayList<PositionedStack> input = new ArrayList<PositionedStack>();

    private final PositionedStack output;

    private int energy;

    public int getEnergy() {
      return energy;
    }

    @Override
    public List<PositionedStack> getIngredients() {
      return getCycledIngredients(cycleticks / 20, input);
    }

    @Override
    public PositionedStack getResult() {
      return output;
    }

    @Override
    public List<PositionedStack> getOtherStacks() {
      return EMPTY_VIAL_OUTPUT;
    }

    public SoulBinderRecipeNEI(ISoulBinderRecipe recipe) {
      this(recipe.getInputStack(), recipe.getOutputStack(), recipe.getEnergyRequired(), recipe.getSupportedSouls());
    }

    public SoulBinderRecipeNEI(ItemStack inputStack, ItemStack result, int energy, List<String> list) {

      int yOff = 11;
      int xOff = 6;

      input.add(new PositionedStack(getSoulVialInputs(list), 38 - xOff, 34 - yOff));
      input.add(new PositionedStack(inputStack, 60 - xOff, 34 - yOff));
      output = new PositionedStack(result, 134 - xOff, 34 - yOff);

    }

    private List<ItemStack> getSoulVialInputs(List<String> mobs) {
      List<ItemStack> result = new ArrayList<ItemStack>(mobs.size());
      for (String mobName : mobs) {
        ItemStack sv = new ItemStack(EnderIO.itemSoulVessel);
        sv.stackTagCompound = new NBTTagCompound();
        sv.stackTagCompound.setString("id", mobName);
        result.add(sv);
      }
      return result;
    }
  }

}
