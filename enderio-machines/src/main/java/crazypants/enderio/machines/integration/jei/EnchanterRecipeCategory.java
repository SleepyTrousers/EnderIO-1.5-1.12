package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.enchanter.EnchanterRecipe;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.enchanter.ContainerEnchanter;
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
      return rec != null;
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      if (currentIngredients == null) {
        return;
      }

      final IGuiIngredient<ItemStack> in0 = currentIngredients.get(0);
      final IGuiIngredient<ItemStack> in1 = currentIngredients.get(1);
      final IGuiIngredient<ItemStack> in2 = currentIngredients.get(2);
      if (in0 != null && in1 != null && in2 != null) {
        final ItemStack slot0 = in0.getDisplayedIngredient();
        final ItemStack slot1 = in1.getDisplayedIngredient();
        final ItemStack slot2 = in2.getDisplayedIngredient();
        if (slot0 != null && slot1 != null && slot2 != null) {
          int xpCost = rec.getXPCost(new NNList<>(new MachineRecipeInput(0, slot0), new MachineRecipeInput(1, slot1), new MachineRecipeInput(1, slot2)));

          if (xpCost != 0) {
            String str = Lang.GUI_VANILLA_REPAIR_COST.get(xpCost);
            minecraft.fontRenderer.drawStringWithShadow(str, 6, 36, 0x80FF20);
          }
        }
      }
    }

    public void setInfoData(Map<Integer, ? extends IGuiIngredient<ItemStack>> ings) {
      currentIngredients = ings;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      List<ItemStack> bookInputs = new ArrayList<ItemStack>();
      List<ItemStack> itemInputs = new ArrayList<ItemStack>();
      List<ItemStack> lapizInputs = new ArrayList<ItemStack>();
      List<ItemStack> itemOutputs = new ArrayList<ItemStack>();

      NNList<NNList<MachineRecipeInput>> variants = rec.getVariants();
      for (NNList<MachineRecipeInput> variant : variants) {
        for (MachineRecipeInput machineRecipeInput : variant) {
          if (machineRecipeInput.slotNumber == 0) {
            bookInputs.add(machineRecipeInput.item);
          } else if (machineRecipeInput.slotNumber == 1) {
            itemInputs.add(machineRecipeInput.item);
          } else if (machineRecipeInput.slotNumber == 2) {
            lapizInputs.add(machineRecipeInput.item);
          }
        }
        ResultStack[] completedResult = rec.getCompletedResult(0, NullHelper.notnullM(variant, "NNList iterated to null"));
        itemOutputs.add(completedResult[0].item);
      }

      List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>();
      inputs.add(bookInputs);
      inputs.add(itemInputs);
      inputs.add(lapizInputs);

      ingredients.setInputLists(ItemStack.class, inputs);
      ingredients.setOutputLists(ItemStack.class, NullHelper.notnullJ(Collections.singletonList(itemOutputs), "Collections.singletonList()"));
    }

  }

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {

    registry.addRecipeCategories(new EnchanterRecipeCategory(guiHelper));
    registry.handleRecipes(EnchanterRecipe.class, EnchanterRecipeWrapper::new, EnchanterRecipeCategory.UID);
    registry.addRecipeClickArea(GuiEnchanter.class, 155, 8, 16, 16, EnchanterRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_enchanter.getBlockNN()), EnchanterRecipeCategory.UID);

    registry.addRecipes(NullHelper.notnullJ(MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.ENCHANTER).values(), "Map.values()"),
        UID);

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

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
