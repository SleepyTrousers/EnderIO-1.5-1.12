package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.machines.machine.enchanter.ContainerEnchanter;
import crazypants.enderio.machines.machine.enchanter.EnchanterRecipe;
import crazypants.enderio.machines.machine.enchanter.EnchanterRecipeManager;
import crazypants.enderio.machines.machine.enchanter.GuiEnchanter;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.machines.init.MachineObject.block_enchanter;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.NUM_RECIPE_SLOT;

public class EnchanterRecipeCategory extends BlankRecipeCategory<EnchanterRecipeCategory.EnchanterRecipeWrapper> {

  public static final @Nonnull String UID = "Enchanter";

  // ------------ Recipes

  public static class EnchanterRecipeWrapper extends BlankRecipeWrapper {

    private final EnchanterRecipe rec;

    Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients;

    public EnchanterRecipeWrapper(EnchanterRecipe rec) {
      this.rec = rec;
    }

    public boolean isValid() {
      return rec != null && rec.isValid();
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      if (currentIngredients == null) {
        return;
      }

      ItemStack stack = null;
      IGuiIngredient<ItemStack> ging = currentIngredients.get(1);
      if (ging != null) {
        stack = ging.getDisplayedIngredient();
      }
      if (stack == null) {
        return;
      }
      int level = rec.getLevelForStackSize(stack.getCount());
      int cost = rec.getCostForLevel(level);
      String str = I18n.format("container.repair.cost", new Object[] { cost });
      minecraft.fontRenderer.drawString(str, 6, 36, 0x80FF20);
    }

    public void setInfoData(Map<Integer, ? extends IGuiIngredient<ItemStack>> ings) {
      currentIngredients = ings;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      List<ItemStack> itemInputs = new ArrayList<ItemStack>();
      List<ItemStack> lapizInputs = new ArrayList<ItemStack>();
      List<ItemStack> itemOutputs = new ArrayList<ItemStack>();
      getItemStacks(rec, itemInputs, lapizInputs, itemOutputs);

      List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>();
      inputs.add(Collections.singletonList(new ItemStack(Items.WRITABLE_BOOK)));
      inputs.add(itemInputs);
      inputs.add(lapizInputs);

      ingredients.setInputLists(ItemStack.class, inputs);
      ingredients.setOutputs(ItemStack.class, itemOutputs);
    }

  }

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {

    registry.addRecipeCategories(new EnchanterRecipeCategory(guiHelper));
    registry.handleRecipes(EnchanterRecipe.class, EnchanterRecipeWrapper::new, EnchanterRecipeCategory.UID);
    // TODO what was this for?
    // @Override
    // public boolean isRecipeValid(@Nonnull EnchanterRecipeWrapper recipe) {
    // return recipe.isValid();
    // }
    //
    // });
    registry.addRecipeClickArea(GuiEnchanter.class, 155, 8, 16, 16, EnchanterRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_enchanter.getBlockNN()), EnchanterRecipeCategory.UID);

    registry.addRecipes(EnchanterRecipeManager.getInstance().getRecipes(), UID);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerEnchanter.class, EnchanterRecipeCategory.UID, FIRST_RECIPE_SLOT, NUM_RECIPE_SLOT,
        FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location
  // correct
  private int xOff = 15;
  private int yOff = 24;

  @Nonnull
  private final IDrawable background;

  private EnchanterRecipeWrapper currentRecipe;

  public EnchanterRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("enchanter");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 146, 48);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getTitle() {
    return block_enchanter.getBlock().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull EnchanterRecipeCategory.EnchanterRecipeWrapper recipeWrapper,
      @Nonnull IIngredients ingredients) {

    currentRecipe = recipeWrapper;

    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

    Map<Integer, ? extends IGuiIngredient<ItemStack>> ings = guiItemStacks.getGuiIngredients();
    currentRecipe.setInfoData(ings);

    guiItemStacks.init(0, true, 16 - xOff - 1, 34 - yOff);
    guiItemStacks.init(1, true, 65 - xOff - 1, 34 - yOff);
    guiItemStacks.init(2, true, 85 - xOff - 1, 34 - yOff);
    guiItemStacks.init(3, false, 144 - xOff - 1, 34 - yOff);

    guiItemStacks.set(ingredients);
  }

  private static void getItemStacks(EnchanterRecipe rec, List<ItemStack> itemInputs, List<ItemStack> lapizInputs, List<ItemStack> itemOutputs) {
    ItemStack item = rec.getInput().getInput();
    for (int level = 1; level <= rec.getEnchantment().getMaxLevel(); level++) {
      itemInputs.add(new ItemStack(item.getItem(), level * rec.getItemsPerLevel(), item.getMetadata()));
      lapizInputs.add(new ItemStack(Items.DYE, rec.getLapizForStackSize(level * rec.getItemsPerLevel()), 4));
      EnchantmentData enchantment = new EnchantmentData(rec.getEnchantment(), level);
      ItemStack output = new ItemStack(Items.ENCHANTED_BOOK);
      Items.ENCHANTED_BOOK.addEnchantment(output, enchantment);
      itemOutputs.add(output);
    }
  }

}
