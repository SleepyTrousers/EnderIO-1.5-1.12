package crazypants.enderio.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.enderio.core.client.render.EnderWidget;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.soul.GuiSoulBinder;
import crazypants.enderio.machine.soul.ISoulBinderRecipe;

public class SoulBinderRecipeHandler extends TemplateRecipeHandler {

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("enderio.nei.soulbinder");
    }

    @Override
    public String getGuiTexture() {
        return GuiContainerBaseEIO.getGuiTexture("soulFuser").toString();
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
        transferRects.add(
                new TemplateRecipeHandler.RecipeTransferRect(
                        new Rectangle(149, 32, 16, 16),
                        "EnderIOSoulBinder",
                        new Object[0]));
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result == null || result.getItem() == null) {
            return;
        }

        Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance
                .getRecipesForMachine(ModObject.blockSoulBinder.unlocalisedName);
        if (recipes.isEmpty()) {
            return;
        }
        for (IMachineRecipe recipe : recipes.values()) {
            if (recipe instanceof ISoulBinderRecipe) {
                ISoulBinderRecipe sbr = (ISoulBinderRecipe) recipe;
                if (sbr.getOutputStack().isItemEqual(result)) {
                    arecipes.add(new SoulBinderRecipeNEI((ISoulBinderRecipe) recipe));
                }
            }
        }
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {

        if (outputId.equals("EnderIOSoulBinder") && getClass() == SoulBinderRecipeHandler.class) {
            Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance
                    .getRecipesForMachine(ModObject.blockSoulBinder.unlocalisedName);
            if (recipes.isEmpty()) {
                return;
            }
            for (IMachineRecipe recipe : recipes.values()) {
                if (recipe instanceof ISoulBinderRecipe) {
                    arecipes.add(new SoulBinderRecipeNEI((ISoulBinderRecipe) recipe));
                }
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {

        if (ingredient == null || ingredient.getItem() == null) {
            return;
        }

        Map<String, IMachineRecipe> recipes = MachineRecipeRegistry.instance
                .getRecipesForMachine(ModObject.blockSoulBinder.unlocalisedName);
        if (recipes.isEmpty()) {
            return;
        }
        for (IMachineRecipe recipe : recipes.values()) {
            if (recipe instanceof ISoulBinderRecipe) {
                SoulBinderRecipeNEI sbr = new SoulBinderRecipeNEI((ISoulBinderRecipe) recipe);
                if (sbr.contains(sbr.input, ingredient)) {
                    arecipes.add(sbr);
                }
            }
        }
    }

    @Override
    public void drawBackground(int recipeIndex) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(18, 0, 29, 11, 142, 65);
    }

    @Override
    public void drawExtras(int recipeIndex) {
        drawProgressBar(70, 23, 177, 14, 23, 17, 160, 0);

        SoulBinderRecipeNEI recipe = (SoulBinderRecipeNEI) arecipes.get(recipeIndex);

        String energyString = PowerDisplayUtil.formatPower(recipe.getEnergy()) + " " + PowerDisplayUtil.abrevation();
        GuiDraw.drawStringC(energyString, 83, 45, 0x808080, false);

        int cost = recipe.getExperience();
        if (cost > 0) {
            String s = I18n.format("container.repair.cost", new Object[] { Integer.valueOf(cost) });
            GuiDraw.drawStringC(s, 83, 55, 0x80FF20);
        }

        int x = 149, y = 32;
        EnderWidget.map.render(EnderWidget.BUTTON, x, y, 16, 16, 0, true);
        IconEIO.map.render(IconEIO.RECIPE, x + 1, y + 1, 14, 14, 0, true);
    }

    private static final ArrayList<PositionedStack> EMPTY_VIAL_OUTPUT = new ArrayList<PositionedStack>();

    static {
        EMPTY_VIAL_OUTPUT.add(new PositionedStack(new ItemStack(EnderIO.itemSoulVessel), 101, 23));
    }

    public class SoulBinderRecipeNEI extends TemplateRecipeHandler.CachedRecipe {

        private final ArrayList<PositionedStack> input = new ArrayList<PositionedStack>();
        private final PositionedStack output;
        private int energy;
        private int experience;

        public int getEnergy() {
            return energy;
        }

        public int getExperience() {
            return experience;
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
            this(
                    recipe.getInputStack(),
                    recipe.getOutputStack(),
                    recipe.getEnergyRequired(),
                    recipe.getExperienceLevelsRequired(),
                    recipe.getSupportedSouls());
        }

        public SoulBinderRecipeNEI(ItemStack inputStack, ItemStack result, int energy, int experience,
                List<String> list) {

            int yOff = 11;
            int xOff = 11;

            input.add(new PositionedStack(getSoulVialInputs(list), 38 - xOff, 34 - yOff));
            input.add(new PositionedStack(inputStack, 59 - xOff, 34 - yOff));
            output = new PositionedStack(result, 134 - xOff, 34 - yOff);

            this.energy = energy;
            this.experience = experience;
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
