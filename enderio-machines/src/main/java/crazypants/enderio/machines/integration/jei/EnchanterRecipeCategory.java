package crazypants.enderio.machines.integration.jei;

import static crazypants.enderio.machines.init.MachineObject.block_enchanter;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.enchanter.ContainerEnchanter.NUM_RECIPE_SLOT;

import java.util.ArrayList;
import java.util.Arrays;
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
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.enchanter.ContainerEnchanter;
import crazypants.enderio.machines.machine.enchanter.GuiEnchanter;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocus.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

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
    
    private int currentCost;

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
        final boolean cycling = in0.getAllIngredients().size() > 1;
        if (slot3 != null) {
          Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(slot3);
          if (enchants.size() == 1) {
            Entry<Enchantment, Integer> ench = enchants.entrySet().iterator().next();
            enchLvl = ench.getValue();
            String name = ench.getKey().getTranslatedName(enchLvl);
            String referenceName = getTranslatedName(ench.getKey(), enchLvl);
            if (referenceName.equals(name)) {
              name = getTranslatedNameBase(ench.getKey(), enchLvl);
              int lvlWidth = minecraft.fontRenderer.getStringWidth(getTranslatedNameLevel(ench.getKey(), cycling ? Math.min(3, ench.getKey().getMaxLevel()) : enchLvl));
              int maxWidth = minecraft.fontRenderer.getStringWidth(name) + lvlWidth;
              minecraft.fontRenderer.drawString(name, 146 - maxWidth, 0, 0x8b8b8b);
              name = getTranslatedNameLevel(ench.getKey(), enchLvl);
              minecraft.fontRenderer.drawString(name, 146 - lvlWidth, 0, 0x8b8b8b);
            } else {
              // modded enchantment that overrides getTranslatedName()
              minecraft.fontRenderer.drawString(name, 146 - minecraft.fontRenderer.getStringWidth(name), 0, 0x8b8b8b);
            }
          }
        }
      }
      if (in0 != null && in1 != null && in2 != null) {
        final ItemStack slot0 = in0.getDisplayedIngredient();
        final ItemStack slot1 = in1.getDisplayedIngredient();
        final ItemStack slot2 = in2.getDisplayedIngredient();
        if (slot0 != null && slot1 != null && slot2 != null) {
          currentCost = rec.getXPCost(new NNList<>(new MachineRecipeInput(0, slot0), new MachineRecipeInput(1, slot1), new MachineRecipeInput(1, slot2)));
          if (currentCost != 0) {

            minecraft.getTextureManager().bindTexture(XP_ORB_TEXTURE);
            GlStateManager.color(0x80 / 255f, 0xFF / 255f, 0x20 / 255f);
            Gui.drawScaledCustomSizeModalRect(-4, 27, 0, 0, 16, 16, 16, 16, 64, 64);

            minecraft.fontRenderer.drawString("" + currentCost, 9, 32, 0x404040);
          }
        }
      }
    }
    
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
      if (currentCost > 0 && mouseX < 21 && mouseY > 30 && mouseY < 40) {
        return Collections.singletonList(Lang.JEI_ENCHANTER_LEVELS.get(TextFormatting.GREEN, currentCost, TextFormatting.WHITE));
      }
      return Collections.emptyList();
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
      List<List<ItemStack>> inputs = Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
      List<ItemStack> itemOutputs = new ArrayList<>();

      NNList<NNList<MachineRecipeInput>> variants = rec.getVariants(-1);
      for (NNList<MachineRecipeInput> variant : variants) {
        for (MachineRecipeInput input : variant) {
          List<ItemStack> slot = inputs.get(input.slotNumber);
          slot.add(input.item);
        }
        itemOutputs.add(rec.getCompletedResult(0, 1, NullHelper.notnull(variant, "NNList iterated to null"))[0].item);
      }
      ingredients.setInputLists(ItemStack.class, inputs);
      ingredients.setOutputLists(ItemStack.class, Collections.singletonList(itemOutputs));
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
  static final int xOff = 15;
  static final int yOff = 24;

  @Nonnull
  private final IDrawable background;

  private EnchanterRecipeWrapper currentRecipe;

  public EnchanterRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("enchanter");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 146, 40);
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

    IGuiIngredientGroup<ItemStack> guiItemStacks = recipeLayout.getItemStacks();

    Map<Integer, ? extends IGuiIngredient<ItemStack>> ings = guiItemStacks.getGuiIngredients();
    currentRecipe.setInfoData(ings);

    guiItemStacks.init(0, true, 16 - xOff - 1, 34 - yOff);
    guiItemStacks.init(1, true, 65 - xOff - 1, 34 - yOff);
    guiItemStacks.init(2, true, 85 - xOff - 1, 34 - yOff);
    guiItemStacks.init(3, false, 144 - xOff - 1, 34 - yOff);
    
    IFocus<?> focus = recipeLayout.getFocus();
    ItemStack focusStack = focus == null ? null : (ItemStack) focus.getValue();
    
    List<ItemStack> bookInputs = new ArrayList<>();
    List<ItemStack> itemInputs = new ArrayList<>();
    List<ItemStack> lapizInputs = new ArrayList<>();
    List<ItemStack> itemOutputs = new ArrayList<>();
    
    EnchanterRecipe rec = recipeWrapper.rec;
    int level = focus == null || focusStack == null || focus.getMode() != Mode.OUTPUT ? -1 : 
      EnchantmentHelper.getEnchantments(focusStack).get(recipeWrapper.rec.getEnchantment());

    NNList<NNList<MachineRecipeInput>> variants = rec.getVariants(level);
    variants_loop: 
    for (NNList<MachineRecipeInput> variant : variants) {
      ItemStack[] inputs = new ItemStack[3];
      for (MachineRecipeInput machineRecipeInput : variant) {
        inputs[machineRecipeInput.slotNumber] = machineRecipeInput.item;
      }
      
      // Player heads are blacklisted in JEI and screw up our list sizes, so blacklist them
      for (ItemStack input : inputs) {
        if (input.getItem() == Items.SKULL && input.getItemDamage() == 3) {
          continue variants_loop;
        }
      }
      
      // Filter out recipes that don't have the focused input, if it exists ----- Apparently focusing the machine isn't a special case, so ignore those
      if (focus != null && focusStack != null && focus.getMode() == Mode.INPUT && focusStack.getItem() != MachineObject.block_enchanter.getItem()) {
        // If no inputs to this recipe match the focus, skip it
        if (Arrays.stream(inputs).filter(s -> s.isItemEqual(focusStack)).count() == 0) {
          continue variants_loop;
        }
      }
      
      bookInputs.add(inputs[0]);
      itemInputs.add(inputs[1]);
      lapizInputs.add(inputs[2]);
      
      ResultStack[] completedResult = rec.getCompletedResult(0, 1F, NullHelper.notnullM(variant, "NNList iterated to null"));
      itemOutputs.add(completedResult[0].item);
    }

    guiItemStacks.set(0, bookInputs);
    guiItemStacks.set(1, itemInputs);
    guiItemStacks.set(2, lapizInputs);
    guiItemStacks.set(3, itemOutputs);
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
