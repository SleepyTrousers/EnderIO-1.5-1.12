package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.AbstractPainterTemplate;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.ClientConfig;
import crazypants.enderio.machines.machine.painter.ContainerPainter;
import crazypants.enderio.machines.machine.painter.GuiPainter;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.machines.init.MachineObject.block_painter;
import static crazypants.enderio.machines.machine.painter.ContainerPainter.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.painter.ContainerPainter.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machines.machine.painter.ContainerPainter.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.painter.ContainerPainter.NUM_RECIPE_SLOT;

public class PainterRecipeCategory extends BlankRecipeCategory<PainterRecipeCategory.PainterRecipeWrapper> {

  public static final @Nonnull String UID = "Painter";

  // ------------ Recipes

  @SuppressWarnings("null")
  @Nonnull
  private static List<PainterRecipeWrapper> splitRecipes(@Nonnull Collection<IMachineRecipe> recipes, List<ItemStack> validItems) {
    long start = System.nanoTime();
    List<AbstractPainterTemplate<?>> basicPainterTemplates = new ArrayList<AbstractPainterTemplate<?>>();
    for (IMachineRecipe recipe : recipes) {
      if (recipe instanceof AbstractPainterTemplate<?>) {
        basicPainterTemplates.add((AbstractPainterTemplate<?>) recipe);
      }
    }

    List<PainterRecipeWrapper> recipesWrappers = new ArrayList<PainterRecipeWrapper>();
    for (ItemStack target : validItems) {
      for (AbstractPainterTemplate<?> basicPainterTemplate : basicPainterTemplates) {
        if (basicPainterTemplate.isValidTarget(target)) {
          recipesWrappers.add(new PainterRecipeWrapper(basicPainterTemplate, target, new ArrayList<ItemStack>(), new ArrayList<ItemStack>()));
        }
      }
    }

    List<ItemStack> paints = ClientConfig.jeiUseShortenedPainterRecipes.get() ? getLimitedItems(validItems) : validItems;

    int count = 0;
    for (ItemStack paint : paints) {
      try {
        for (PainterRecipeWrapper painterRecipeWrapper : recipesWrappers) {
          if (painterRecipeWrapper.recipe.isRecipe(paint, painterRecipeWrapper.target)) {
            for (ResultStack result : painterRecipeWrapper.recipe.getCompletedResult(paint, painterRecipeWrapper.target)) {
              painterRecipeWrapper.results.add(result.item);
              painterRecipeWrapper.paints.add(paint);
              count++;
            }
          }
        }
      } catch (Exception e) {
        Log.warn("PainterRecipeCategory: Error while accessing item '" + paint + "': " + e);
        e.printStackTrace();
      }
    }
    long end = System.nanoTime();

    for (PainterRecipeWrapper painterRecipeWrapper : recipesWrappers) {
      if (painterRecipeWrapper.results.isEmpty()) {
        Log.warn("PainterRecipeCategory: Empty recipe group: " + painterRecipeWrapper.recipe + " for " + painterRecipeWrapper.target);
      }
    }

    Log.info(String.format("PainterRecipeCategory: Added %d painter recipes in %d groups to JEI in %.3f seconds.", count, recipesWrappers.size(),
        (end - start) / 1000000000d));

    return recipesWrappers;
  }

  public static class PainterRecipeWrapper extends BlankRecipeWrapper {

    final AbstractPainterTemplate<?> recipe;
    final @Nonnull ItemStack target;
    final @Nonnull List<ItemStack> paints;
    final @Nonnull List<ItemStack> results;

    public PainterRecipeWrapper(@Nonnull AbstractPainterTemplate<?> recipe, @Nonnull ItemStack target, @Nonnull List<ItemStack> paints,
        @Nonnull List<ItemStack> results) {
      this.recipe = recipe;
      this.target = target;
      this.paints = paints;
      this.results = results;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      List<List<ItemStack>> list = new ArrayList<>();
      list.add(Collections.singletonList(target));
      list.add(paints);
      ingredients.setInputLists(ItemStack.class, list);
      ingredients.setOutputs(ItemStack.class, results);
      ingredients.setInput(EnergyIngredient.class, new EnergyIngredient(recipe.getEnergyRequired(NNList.emptyList())));
    }
  }

  @SuppressWarnings("null")
  public static void register(IModRegistry registry, IJeiHelpers jeiHelpers) {
    registry.addRecipeCategories(new PainterRecipeCategory(jeiHelpers));
    registry.addRecipeClickArea(GuiPainter.class, 155, 42, 16, 16, PainterRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_painter.getBlock()), PainterRecipeCategory.UID);

    List<ItemStack> validItems = registry.getIngredientRegistry().getIngredients(ItemStack.class);
    registry.addRecipes(splitRecipes(MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.PAINTER).values(), validItems), UID);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerPainter.class, PainterRecipeCategory.UID, FIRST_RECIPE_SLOT, NUM_RECIPE_SLOT,
        FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location correct
  private final static int xOff = 34;
  private final static int yOff = 28;

  private final @Nonnull IStackHelper stackHelper;
  private final @Nonnull IDrawable background;
  private final @Nonnull IDrawableAnimated arrow;

  public PainterRecipeCategory(IJeiHelpers jeiHelpers) {
    stackHelper = jeiHelpers.getStackHelper();
    IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("painter");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 120, 50);

    IDrawableStatic flameDrawable = guiHelper.createDrawable(backgroundLocation, 176, 14, 24, 16);
    arrow = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @Override
  public @Nonnull String getTitle() {
    return block_painter.getBlockNN().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    arrow.draw(minecraft, 88 - xOff, 34 - yOff);
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull PainterRecipeCategory.PainterRecipeWrapper currentRecipe,
      @Nonnull IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);

    guiItemStacks.init(0, true, 67 - xOff - 1, 34 - yOff - 1);
    guiItemStacks.init(1, true, 38 - xOff - 1, 34 - yOff - 1);
    guiItemStacks.init(2, false, 121 - xOff - 1, 34 - yOff - 1);
    group.init(3, true, EnergyIngredientRenderer.INSTANCE, 108 - xOff, 60 - yOff, 50, 10, 0, 0);

    IFocus<?> focus = recipeLayout.getFocus();
    if (focus != null) {
      Object focusValue = focus.getValue();
      if (focusValue instanceof ItemStack) {
        ItemStack focused = (ItemStack) focusValue;

        List<ItemStack> paints = new ArrayList<ItemStack>();
        List<ItemStack> results = new ArrayList<ItemStack>();

        if (focus.getMode() == IFocus.Mode.OUTPUT) {
          // JEI is focusing on the output item. Limit the recipe to the fixed input item, the focused output item and the matching paint.
          IBlockState paint = PaintUtil.getSourceBlock(focused);
          ItemStack paintAsStack = PaintUtil.getPaintAsStack(paint);
          paints.add(paintAsStack);
          results.add(focused);
        } else if (stackHelper.isEquivalent(focused, currentRecipe.target)) {
          // JEI is focusing on a paintable item. If that item also can be used as a paint source, it will display "item+item=anything", which is somewhere
          // between weird and wrong. So remove the recipe "item+item" from the list to get "anything+item=anything".
          for (int i = 0; i < currentRecipe.paints.size(); i++) {
            ItemStack resultStack = currentRecipe.results.get(i);
            ItemStack paintStack = currentRecipe.paints.get(i);
            if (!stackHelper.isEquivalent(focused, paintStack)) {
              paints.add(paintStack);
              results.add(resultStack);
            }
          }
        } else {
          // JEI is focusing on the paint. Limit the output items to things that are painted with this paint.
          for (ResultStack result : currentRecipe.recipe.getCompletedResult(focused, currentRecipe.target)) {
            paints.add(focused);
            results.add(result.item);
          }
        }
        if (!paints.isEmpty()) {
          guiItemStacks.set(1, paints);
          guiItemStacks.set(2, results);
          return;
        }
      }
    }

    guiItemStacks.set(0, currentRecipe.target);
    guiItemStacks.set(1, currentRecipe.paints);
    guiItemStacks.set(2, currentRecipe.results);
    group.set(ingredients);
  }

  private static @Nonnull List<ItemStack> getLimitedItems(List<ItemStack> validItems) {
    Things paints = new Things().add(Blocks.STONE).add(Blocks.COBBLESTONE).add(Blocks.GRASS).add(Blocks.DIRT).add(Blocks.PLANKS).add(Blocks.GLASS)
        .add(Blocks.STONE_STAIRS).add(Blocks.RED_FLOWER).add(Blocks.SLIME_BLOCK).add(Blocks.TNT);
    Random rand = new Random();
    for (ItemStack stack : validItems) {
      if (rand.nextFloat() < 0.05f && !paints.contains(stack)) {
        paints.add(stack);
      }
    }
    NNList<ItemStack> paintStacks = paints.getItemStacks();
    while (paintStacks.size() > 50) {
      paintStacks.remove(paintStacks.size() - 1);
    }
    return paintStacks;
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
