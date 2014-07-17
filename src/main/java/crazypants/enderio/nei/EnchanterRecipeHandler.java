package crazypants.enderio.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.IRecipeInput;
import crazypants.enderio.crafting.IRecipeOutput;
import crazypants.enderio.crafting.RecipeReigistry;
import crazypants.enderio.machine.alloy.GuiAlloySmelter;
import crazypants.enderio.machine.enchanter.EnchanterRecipe;
import crazypants.enderio.machine.enchanter.EnchanterRecipeManager;
import crazypants.enderio.machine.enchanter.GuiEnchanter;
import crazypants.enderio.machine.enchanter.TileEnchanter;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.nei.AlloySmelterRecipeHandler.AlloySmelterRecipe;

public class EnchanterRecipeHandler extends TemplateRecipeHandler {

  @Override
  public String getRecipeName() {
    return "Enchanter";
  }

  @Override
  public String getGuiTexture() {
    return "enderio:textures/gui/enchanter.png";
  }

  @Override
  public Class<? extends GuiContainer> getGuiClass() {
    return GuiEnchanter.class;
  }

  @Override
  public String getOverlayIdentifier() {
    return "EIOEnchanter";
  }

  @Override
  public void loadTransferRects() {
    transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(149, -3, 16, 16), "EIOEnchanter", new Object[0]));
  }

  @Override
  public void loadCraftingRecipes(ItemStack result) {
    if(result == null || result.getItem() != Items.enchanted_book) {
      return;
    }
    Map<Short, Short> enchants = EnchantmentHelper.getEnchantments(result);
    List<EnchanterRecipe> recipes = EnchanterRecipeManager.getInstance().getRecipes();
    
    for(Short id : enchants.keySet()) {
      if(id != null && id >= 0 && id < Enchantment.enchantmentsList.length) {
        Enchantment ench = Enchantment.enchantmentsList[id];
        if(ench != null && ench.getName() != null) {
    
          for (EnchanterRecipe recipe : recipes) {
            if(recipe.isValid() && recipe.getEnchantment().getName().equals(ench.getName())) {
              EnchantmentData enchantment = new EnchantmentData(recipe.getEnchantment(), 1);
              ItemStack output = new ItemStack(Items.enchanted_book);
              Items.enchanted_book.addEnchantment(output, enchantment);
              EnchanterRecipeNEI rec = new EnchanterRecipeNEI(recipe.getInput(), output, enchantment);
              arecipes.add(rec);
            }
          }          
          
        }        
      }
    }
    

  }

  @Override
  public void loadCraftingRecipes(String outputId, Object... results) {
    if(outputId.equals("EIOEnchanter") && getClass() == EnchanterRecipeHandler.class) {      
      List<EnchanterRecipe> recipes = EnchanterRecipeManager.getInstance().getRecipes();
      for (EnchanterRecipe recipe : recipes) {
        if(recipe.isValid()) {
          EnchantmentData enchantment = new EnchantmentData(recipe.getEnchantment(), 1);
          ItemStack output = new ItemStack(Items.enchanted_book);
          Items.enchanted_book.addEnchantment(output, enchantment);
          EnchanterRecipeNEI rec = new EnchanterRecipeNEI(recipe.getInput(), output, enchantment);
          arecipes.add(rec);
        }
      }
    } else {
      super.loadCraftingRecipes(outputId, results);
    }
  }

  @Override
  public void loadUsageRecipes(ItemStack ingredient) {
    List<EnchanterRecipe> recipes = EnchanterRecipeManager.getInstance().getRecipes();
    for (EnchanterRecipe recipe : recipes) {
      if(recipe.isValid() && recipe.isInput(ingredient)) {
        EnchantmentData enchantment = new EnchantmentData(recipe.getEnchantment(), 1);
        ItemStack output = new ItemStack(Items.enchanted_book);
        Items.enchanted_book.addEnchantment(output, enchantment);
        EnchanterRecipeNEI rec = new EnchanterRecipeNEI(recipe.getInput(), output, enchantment);
        arecipes.add(rec);
      }
    }
  }

  @Override
  public void drawExtras(int recipeIndex) {    
    EnchanterRecipeNEI recipe = (EnchanterRecipeNEI) arecipes.get(recipeIndex);
    int cost = TileEnchanter.getEnchantmentCost(recipe.enchData);
    if(cost > 0) {
      String s = I18n.format("container.repair.cost", new Object[] {Integer.valueOf(cost)});      
      FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
      int sw = fr.getStringWidth(s);
      fr.drawStringWithShadow(s, 90 - (sw/2), 48, 8453920);
    }
  }

  public List<ItemStack> getInputs(RecipeInput input) {
    List<ItemStack> result = new ArrayList<ItemStack>();
    result.add(input.getInput());
    ItemStack[] equivs = input.getEquivelentInputs();
    if(equivs != null && equivs.length > 0) {
      result.addAll(Arrays.asList(equivs));
    }
    return result;
  }

  public class EnchanterRecipeNEI extends TemplateRecipeHandler.CachedRecipe {

    private ArrayList<PositionedStack> input;
    private PositionedStack output;
    private EnchantmentData enchData;

    @Override
    public List<PositionedStack> getIngredients() {
      return getCycledIngredients(cycleticks / 20, input);
    }

    @Override
    public PositionedStack getResult() {
      return output;
    }

    public EnchanterRecipeNEI(RecipeInput ingredient, ItemStack result, EnchantmentData enchData) {
      input = new ArrayList<PositionedStack>();
      input.add(new PositionedStack(new ItemStack(Items.writable_book), 22, 24));
      input.add(new PositionedStack(getInputs(ingredient), 71, 24));
      output = new PositionedStack(result, 129, 24);
      this.enchData = enchData;
    }
  }
}
