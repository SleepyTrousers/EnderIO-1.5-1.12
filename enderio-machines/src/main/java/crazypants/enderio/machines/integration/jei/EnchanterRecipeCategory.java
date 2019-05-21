package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.enchanter.EnchanterRecipe;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.PersonalConfig;
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
import mezz.jei.api.recipe.IFocus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import static crazypants.enderio.machines.init.MachineObject.block_enchanter;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.NUM_RECIPE_SLOT;

public class EnchanterRecipeCategory extends BlankRecipeCategory<EnchanterRecipeCategory.EnchanterRecipeWrapper> {

  public static final @Nonnull String UID = "Enchanter";

  // ------------ Recipes

  public static class EnchanterRecipeWrapper extends BlankRecipeWrapper {

    private static final @Nonnull ResourceLocation XP_ORB_TEXTURE = new ResourceLocation("textures/entity/experience_orb.png");

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
      final IGuiIngredient<ItemStack> out = currentIngredients.get(3);
      int enchLvl = 0;
      if (out != null) {
        final ItemStack slot3 = out.getDisplayedIngredient();
        if (slot3 != null) {
          Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(slot3);
          if (enchants.size() == 1) {
            Entry<Enchantment, Integer> ench = enchants.entrySet().iterator().next();
            enchLvl = ench.getValue();
            String name = ench.getKey().getTranslatedName(enchLvl);
            String referenceName = getTranslatedName(ench.getKey(), enchLvl);
            if (referenceName.equals(name)) {
              name = getTranslatedNameBase(ench.getKey(), enchLvl);
              minecraft.fontRenderer.drawString(name, 147 - minecraft.fontRenderer.getStringWidth(name), 0, 0x8b8b8b);
              name = getTranslatedNameLevel(ench.getKey(), enchLvl);
              minecraft.fontRenderer.drawString(name, 147, 0, 0x8b8b8b);
            } else {
              // modded enchantment that overrides getTranslatedName()
              minecraft.fontRenderer.drawString(name, 147 - minecraft.fontRenderer.getStringWidth(name), 0, 0x8b8b8b);
            }
          }
        }
      }
      if (in0 != null && in1 != null && in2 != null) {
        final ItemStack slot0 = in0.getDisplayedIngredient();
        final ItemStack slot1 = in1.getDisplayedIngredient();
        final ItemStack slot2 = in2.getDisplayedIngredient();
        if (slot0 != null && slot1 != null && slot2 != null) {
          int xpCost = rec.getXPCost(new NNList<>(new MachineRecipeInput(0, slot0), new MachineRecipeInput(1, slot1), new MachineRecipeInput(1, slot2)));
          if (xpCost != 0) {

            minecraft.getTextureManager().bindTexture(XP_ORB_TEXTURE);
            GlStateManager.color(0x80 / 255f, 0xFF / 255f, 0x20 / 255f);
            Gui.drawScaledCustomSizeModalRect(-4, 26, 0, 0, 16, 16, 16, 16, 64, 64);

            minecraft.fontRenderer.drawString("" + xpCost, 9, 31, 0x404040);
          }
        }
      }
    }

    // copy of Enchantment.getTranslatedName()
    public static @Nonnull String getTranslatedName(Enchantment e, int level) {
      String s = I18n.translateToLocal(e.getName());

      if (e.isCurse()) {
        s = TextFormatting.RED + s;
      }

      return level == 1 && e.getMaxLevel() == 1 ? s : s + " " + I18n.translateToLocal("enchantment.level." + level);
    }

    public static @Nonnull String getTranslatedNameBase(Enchantment e, int level) {
      String s = I18n.translateToLocal(e.getName());

      if (e.isCurse()) {
        s = TextFormatting.RED + s;
      }

      return s;
    }

    public static @Nonnull String getTranslatedNameLevel(Enchantment e, int level) {
      return level == 1 && e.getMaxLevel() == 1 ? "" : " " + I18n.translateToLocal("enchantment.level." + level);
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
        ResultStack[] completedResult = rec.getCompletedResult(0, 1F, NullHelper.notnullM(variant, "NNList iterated to null"));
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
    // Check JEI recipes are enabled
    if (!PersonalConfig.enableEnchanterJEIRecipes.get()) {
      return;
    }

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
    IFocus<?> focus = recipeLayout.getFocus();
    if (focus != null) {
      // we cycle through different variants of the same output items here, keeping one of them in focus
      // would trash our recipe.
      guiItemStacks.setOverrideDisplayFocus(null);
      // TODO: Re-build the ingredient lists to satisfy the focus.
    }

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
