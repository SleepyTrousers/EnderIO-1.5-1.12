package crazypants.enderio.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.FluidUtil;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.vat.GuiVat;
import crazypants.enderio.machine.vat.VatRecipeManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class VatRecipeHandler extends TemplateRecipeHandler {

    private Rectangle inTankBounds = new Rectangle(25, 1, 15, 47);
    private Rectangle outTankBounds = new Rectangle(127, 1, 15, 47);

    public VatRecipeHandler() {}

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("enderio.nei.vat");
    }

    @Override
    public String getGuiTexture() {
        return GuiContainerBaseEIO.getGuiTexture("vat").toString();
    }

    public PositionedStack getResult() {
        return null;
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiVat.class;
    }

    @Override
    public String getOverlayIdentifier() {
        return "EnderIOVat";
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(
                new Rectangle(149, 32, 16, 16), "EnderIOVat", new Object[0]));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("liquid")) {
            loadCraftingRecipes((FluidStack) results[0]);
        } else if (outputId.equals("EnderIOVat") && getClass() == VatRecipeHandler.class) {
            List<IRecipe> recipes = VatRecipeManager.getInstance().getRecipes();
            for (IRecipe recipe : recipes) {
                FluidStack output = recipe.getOutputs()[0].getFluidOutput();
                InnerVatRecipe res = new InnerVatRecipe(recipe.getEnergyRequired(), recipe.getInputs(), output);
                arecipes.add(res);
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        FluidStack fluid = FluidUtil.getFluidFromItem(result);
        if (fluid != null) {
            loadCraftingRecipes(fluid);
        }
    }

    public void loadCraftingRecipes(FluidStack result) {
        List<IRecipe> recipes = VatRecipeManager.getInstance().getRecipes();
        for (IRecipe recipe : recipes) {
            FluidStack output = recipe.getOutputs()[0].getFluidOutput();
            if (output.isFluidEqual(result)) {
                InnerVatRecipe res = new InnerVatRecipe(recipe.getEnergyRequired(), recipe.getInputs(), output);
                arecipes.add(res);
            }
        }
    }

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("liquid")) {
            loadUsageRecipes((FluidStack) ingredients[0]);
        } else {
            super.loadUsageRecipes(inputId, ingredients);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        FluidStack fluid = FluidUtil.getFluidFromItem(ingredient);
        if (fluid != null) {
            loadUsageRecipes(fluid);
        }

        List<IRecipe> recipes = VatRecipeManager.getInstance().getRecipes();
        for (IRecipe recipe : recipes) {
            if (recipe.isValidInput(0, ingredient) || recipe.isValidInput(1, ingredient)) {
                FluidStack output = recipe.getOutputs()[0].getFluidOutput();
                InnerVatRecipe res = new InnerVatRecipe(recipe.getEnergyRequired(), recipe.getInputs(), output);
                res.setIngredientPermutation(res.inputs, ingredient);
                arecipes.add(res);
            }
        }
    }

    public void loadUsageRecipes(FluidStack ingredient) {
        List<IRecipe> recipes = VatRecipeManager.getInstance().getRecipes();
        for (IRecipe recipe : recipes) {
            if (recipe.isValidInput(ingredient)) {
                FluidStack output = recipe.getOutputs()[0].getFluidOutput();
                InnerVatRecipe res = new InnerVatRecipe(recipe.getEnergyRequired(), recipe.getInputs(), output);
                arecipes.add(res);
            }
        }
    }

    @Override
    public void drawBackground(int recipeIndex) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(22, 0, 27, 11, 123, 52);
    }

    @Override
    public void drawExtras(int recipeIndex) {
        InnerVatRecipe rec = (InnerVatRecipe) arecipes.get(recipeIndex);
        if (rec.inFluid != null && rec.inFluid.getFluid() != null) {
            RenderUtil.renderGuiTank(
                    rec.inFluid,
                    rec.inFluid.amount,
                    rec.inFluid.amount,
                    inTankBounds.x,
                    inTankBounds.y,
                    0,
                    inTankBounds.width,
                    inTankBounds.height);
        }

        if (rec.result != null && rec.result.getFluid() != null) {
            RenderUtil.renderGuiTank(
                    rec.result,
                    rec.result.amount,
                    rec.result.amount,
                    outTankBounds.x,
                    outTankBounds.y,
                    0,
                    outTankBounds.width,
                    outTankBounds.height);
        }

        String energyString = PowerDisplayUtil.formatPower(rec.energy) + " " + PowerDisplayUtil.abrevation();
        GuiDraw.drawStringC(energyString, 86, 54, 0x808080, false);

        Fluid outputFluid = rec.result.getFluid();
        List<PositionedStack> stacks = rec.getIngredients();
        for (PositionedStack ps : stacks) {
            float mult =
                    VatRecipeManager.getInstance().getMultiplierForInput(rec.inFluid.getFluid(), ps.item, outputFluid);
            String str = "x" + mult;
            GuiDraw.drawStringC(str, ps.relx + 8, ps.rely + 19, 0x808080, false);
        }

        int x = 149, y = 32;
        EnderWidget.map.render(EnderWidget.BUTTON, x, y, 16, 16, 0, true);
        IconEIO.map.render(IconEIO.RECIPE, x + 1, y + 1, 14, 14, 0, true);
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe<?> gui, ItemStack stack, List<String> currenttip, int recipeIndex) {
        return currenttip;
    }

    @Override
    public List<String> handleTooltip(GuiRecipe<?> gui, List<String> currenttip, int recipeIndex) {
        InnerVatRecipe rec = (InnerVatRecipe) arecipes.get(recipeIndex);
        Point pos = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipeIndex);
        Point relMouse =
                new Point(pos.x - ((gui.width - 176) / 2) - offset.x, pos.y - ((gui.height - 166) / 2) - offset.y);

        if (inTankBounds.contains(relMouse) || outTankBounds.contains(relMouse)) {
            if (inTankBounds.contains(relMouse)) {
                if (rec.inFluid != null && rec.inFluid.getFluid() != null) {
                    currenttip.add(rec.inFluid.getFluid().getLocalizedName(rec.inFluid));
                }
            } else {
                if (rec.result != null && rec.result.getFluid() != null) {
                    currenttip.add(rec.result.getFluid().getLocalizedName(rec.result));
                }
            }
        }
        return super.handleTooltip(gui, currenttip, recipeIndex);
    }

    @Override
    public boolean mouseClicked(GuiRecipe gui, int button, int recipeIndex) {
        if (button == 0) {
            if (this.transferFluidTanks(gui, recipeIndex, false)) {
                return true;
            }
        } else if (button == 1) {
            if (this.transferFluidTanks(gui, recipeIndex, true)) {
                return true;
            }
        }
        return super.mouseClicked(gui, button, recipeIndex);
    }

    private boolean transferFluidTanks(GuiRecipe gui, int recipeIndex, boolean usage) {
        InnerVatRecipe rec = (InnerVatRecipe) arecipes.get(recipeIndex);
        Point pos = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipeIndex);
        Point relMouse =
                new Point(pos.x - ((gui.width - 176) / 2) - offset.x, pos.y - ((gui.height - 166) / 2) - offset.y);

        if (inTankBounds.contains(relMouse)) {
            transferFluidTank(rec.inFluid, usage);
        } else if (outTankBounds.contains(relMouse)) {
            transferFluidTank(rec.result, usage);
        }
        return false;
    }

    private boolean transferFluidTank(FluidStack tank, boolean usage) {
        if (tank != null && tank.amount > 0) {
            if (usage) {
                if (!GuiUsageRecipe.openRecipeGui("liquid", new Object[] {tank.copy()})) {
                    return false;
                }
            } else {
                if (!GuiCraftingRecipe.openRecipeGui("liquid", new Object[] {tank.copy()})) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public List<ItemStack> getInputs(RecipeInput input) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        result.add(input.getInput());
        ItemStack[] eq = input.getEquivelentInputs();
        if (eq != null) {
            for (ItemStack st : eq) {
                result.add(st);
            }
        }
        return result;
    }

    public class InnerVatRecipe extends TemplateRecipeHandler.CachedRecipe {

        private ArrayList<PositionedStack> inputs;
        private int energy;
        private FluidStack result;
        private FluidStack inFluid;

        public int getEnergy() {
            return energy;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 30, inputs);
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        public InnerVatRecipe(int energy, RecipeInput[] ingredients, FluidStack result) {
            ArrayList<ItemStack> inputsOne = new ArrayList<ItemStack>();
            ArrayList<ItemStack> inputsTwo = new ArrayList<ItemStack>();
            for (RecipeInput input : ingredients) {
                if (input.getInput() != null) {
                    List<ItemStack> equivs = getInputs(input);
                    if (input.getSlotNumber() == 0) {
                        inputsOne.addAll(equivs);
                    } else if (input.getSlotNumber() == 1) {
                        inputsTwo.addAll(equivs);
                    }
                } else if (input.getFluidInput() != null) {
                    inFluid = input.getFluidInput();
                }
            }

            inputs = new ArrayList<PositionedStack>();

            if (!inputsOne.isEmpty()) {
                inputs.add(new PositionedStack(inputsOne, 51, 1));
            }
            if (!inputsTwo.isEmpty()) {
                inputs.add(new PositionedStack(inputsTwo, 100, 1));
            }

            this.energy = energy;
            this.result = result;
        }
    }
}
