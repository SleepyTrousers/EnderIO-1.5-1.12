package crazypants.enderio.machines.integration.jei;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.enchanter.EnchanterRecipe;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.PersonalConfig;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.enchanter.ContainerEnchanter;
import crazypants.enderio.machines.machine.enchanter.GuiEnchanter;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
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

  public static class EnchanterRecipeWrapper implements IRecipeWrapper {

    private static final @Nonnull ResourceLocation XP_ORB_TEXTURE = new ResourceLocation("textures/entity/experience_orb.png");

    private final @Nonnull EnchanterRecipe rec;
    private final @Nonnull NNList<ItemStack> bookInputs, itemInputs, lapisInputs;
    private final int level;

    protected static @Nonnull List<EnchanterRecipeWrapper> create(IMachineRecipe imr) {
      if (!(imr instanceof EnchanterRecipe)) {
        return NNList.emptyList();
      }
      EnchanterRecipe rec = (EnchanterRecipe) imr;
      final @Nonnull List<EnchanterRecipeWrapper> result = new NNList<>();
      Enchantment enchantment = rec.getEnchantment();
      LEVEL: for (int level = 1; level <= enchantment.getMaxLevel(); level++) {
        // Books
        NNList<ItemStack> bookInputs = new NNList<>();
        for (ItemStack book : rec.getBook().getItemStacks()) {
          bookInputs.add(book.copy());
        }
        // Lapis Lazulis
        NNList<ItemStack> lapisInputs = new NNList<>();
        for (ItemStack lapis : rec.getLapis().getItemStacks()) {
          lapis = lapis.copy();
          lapis.setCount(rec.getLapizForLevel(level));
          if (lapis.getCount() <= lapis.getMaxStackSize()) {
            lapisInputs.add(lapis);
          }
        }
        if (lapisInputs.isEmpty()) {
          break LEVEL;
        }
        // Items
        NNList<ItemStack> itemInputs = new NNList<>();
        for (ItemStack item : rec.getInput().getItemStacks()) {
          item = item.copy();
          item.setCount(rec.getItemsPerLevel() * level);
          if (item.getCount() <= item.getMaxStackSize()) {
            itemInputs.add(item);
          }
          if (itemInputs.isEmpty()) {
            break LEVEL;
          }
        }
        // create recipe
        result.add(new EnchanterRecipeWrapper(rec, level, bookInputs, lapisInputs, itemInputs));
      }
      return NullHelper.notnullJ(result, "Eclipse is stupid");
    }

    private EnchanterRecipeWrapper(@Nonnull EnchanterRecipe rec, int level, @Nonnull NNList<ItemStack> bookInputs, @Nonnull NNList<ItemStack> lapisInputs,
        @Nonnull NNList<ItemStack> itemInputs) {
      this.rec = rec;
      this.level = level;
      this.bookInputs = bookInputs;
      this.lapisInputs = lapisInputs;
      this.itemInputs = itemInputs;
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      Enchantment enchantment = rec.getEnchantment();
      String name = enchantment.getTranslatedName(level);
      minecraft.fontRenderer.drawString(name, 147 - minecraft.fontRenderer.getStringWidth(name), 0, 0x8b8b8b);

      int xpCost = rec.getXPCost(getMachineInputs());
      minecraft.getTextureManager().bindTexture(XP_ORB_TEXTURE);
      GlStateManager.color(0x80 / 255f, 0xFF / 255f, 0x20 / 255f);
      Gui.drawScaledCustomSizeModalRect(0, 29, 0, 0, 16, 16, 16, 16, 64, 64);
      minecraft.fontRenderer.drawStringWithShadow("  " + Lang.GUI_VANILLA_REPAIR_COST.get(xpCost), 9, 33, 8453920);
    }

    private @Nonnull NNList<MachineRecipeInput> getMachineInputs() {
      return new NNList<>(new MachineRecipeInput(0, bookInputs.get(0)), new MachineRecipeInput(1, itemInputs.get(0)),
          new MachineRecipeInput(1, lapisInputs.get(0)));
    }
    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setInputLists(VanillaTypes.ITEM, new NNList<List<ItemStack>>(bookInputs, itemInputs, lapisInputs));
      ingredients.setOutput(VanillaTypes.ITEM, rec.getCompletedResult(0, 1F, getMachineInputs())[0].item);
    }

  }

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {
    // Check JEI recipes are enabled
    if (!PersonalConfig.enableEnchanterJEIRecipes.get()) {
      return;
    }

    registry.addRecipeCategories(new EnchanterRecipeCategory(guiHelper));
    registry.addRecipeClickArea(GuiEnchanter.class, 155, 8, 16, 16, EnchanterRecipeCategory.UID);
    registry.addRecipeCatalyst(new ItemStack(block_enchanter.getBlockNN()), EnchanterRecipeCategory.UID);

    registry.addRecipes(NullHelper.notnullJ(MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.ENCHANTER).values().stream()
        .map(EnchanterRecipeWrapper::create).flatMap(Collection::stream).collect(Collectors.toList()), "Stream::collect"), UID);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerEnchanter.class, EnchanterRecipeCategory.UID, FIRST_RECIPE_SLOT, NUM_RECIPE_SLOT,
        FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location correct
  private static final int xOff = 16;
  private static final int yOff = 24;

  private final @Nonnull IDrawable background;

  public EnchanterRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("enchanter");
    background = guiHelper.drawableBuilder(backgroundLocation, xOff - 1, yOff, 146, 48).build();
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

    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

    guiItemStacks.init(0, true, 16 - xOff, 34 - yOff);
    guiItemStacks.init(1, true, 65 - xOff, 34 - yOff);
    guiItemStacks.init(2, true, 85 - xOff, 34 - yOff);
    guiItemStacks.init(3, false, 144 - xOff, 34 - yOff);

    guiItemStacks.set(ingredients);
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
