package crazypants.util;

import static crazypants.enderio.material.Material.BINDER_COMPOSITE;
import static crazypants.enderio.material.Material.CONDUIT_BINDER;
import static crazypants.enderio.material.Material.PHASED_IRON_NUGGET;
import static crazypants.enderio.material.Material.SILICON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy;
import com.pahimar.ee3.api.exchange.RecipeRegistryProxy;

import cpw.mods.fml.common.Loader;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.ItemEnderFood.EnderFood;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.material.MachinePart;
import crazypants.enderio.material.PowderIngot;

public class EE3Util {

    public static final String EE3_ID = "EE3";
    public static final boolean IS_EE3_LOADED = Loader.isModLoaded(EE3_ID);

    public static void registerItemStack(ItemStack itemStack, int emv) {
        if (IS_EE3_LOADED) {
            EnergyValueRegistryProxy.addPreAssignedEnergyValue(itemStack, emv);
        }
    }

    public static void registerItemStackLike(ItemStack itemStack, ItemStack copyFrom) {
        if (IS_EE3_LOADED) {
            RecipeRegistryProxy.addRecipe(itemStack, Collections.singletonList(copyFrom));
        }
    }

    public static void registerFluidStack(FluidStack fluidStack, int emv) {
        if (IS_EE3_LOADED) {
            EnergyValueRegistryProxy.addPreAssignedEnergyValue(fluidStack, emv);
        }
    }

    public static void registerRecipe(ItemStack itemStack, List<?> recipeInputList) {
        if (IS_EE3_LOADED) {
            RecipeRegistryProxy.addRecipe(itemStack, recipeInputList);
        }
    }

    public static void registerRecipe(FluidStack fluidStack, List<?> recipeInputList) {
        if (IS_EE3_LOADED) {
            RecipeRegistryProxy.addRecipe(fluidStack, recipeInputList);
        }
    }

    public static void registerRecipe(ItemStack itemStack, Object... recipeInputList) {
        if (IS_EE3_LOADED) {
            RecipeRegistryProxy.addRecipe(itemStack, Arrays.asList(recipeInputList));
        }
    }

    public static void registerRecipe(FluidStack fluidStack, Object... recipeInputList) {
        if (IS_EE3_LOADED) {
            RecipeRegistryProxy.addRecipe(fluidStack, Arrays.asList(recipeInputList));
        }
    }

    //

    /**
     * Registers various recipes with EE3.
     * <p>
     * Some of them are ManyToMany recipes (e.g. SAG Mill), others are vanilla crafting recipes that fail in EE3 because
     * of too much oreDicting. Those ones could be removed after EE3 fixes that.
     * <p>
     * This does not take into account changes the user may have done in the recipe xml files. Those are ManyToMany,
     * they contain chances, and they don't usually sum up nicely (e.g. coal ore (emc 32) => 8*coal (emc 32) plus a
     * chance of coal and coal dust). It is not unreasonable to require the user to also change emc values after
     * changing the recipes for our core items.
     * <p>
     * This also ignores OreDict, as it is only about the emc values. For those, the base vanilla items are the best
     * source.
     */
    public static void registerMiscRecipes() {
        if (IS_EE3_LOADED) {
            ItemStack basicGear = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.BASIC_GEAR.ordinal());
            ItemStack stick4 = new ItemStack(Items.stick, 4);
            ItemStack cobble4 = new ItemStack(Blocks.cobblestone, 4);
            registerRecipe(basicGear, stick4, cobble4);

            ItemStack flour10 = new ItemStack(EnderIO.itemPowderIngot, 10, PowderIngot.FLOUR.ordinal());
            ItemStack wheat5 = new ItemStack(Items.wheat, 5);
            ItemStack seeds1 = new ItemStack(Items.wheat_seeds, 1);
            registerRecipe(flour10, wheat5, seeds1);

            ItemStack enderios = EnderFood.ENDERIOS.getStack();
            ItemStack bowl = new ItemStack(Items.bowl, 1);
            ItemStack wheat = new ItemStack(Items.wheat, 1);
            ItemStack milkb = new ItemStack(Items.milk_bucket, 1);
            FluidStack milk = FluidContainerRegistry.getFluidForFilledItem(milkb);
            ItemStack dustEnderPearl = new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_ENDER.ordinal());
            if (milk != null) {
                registerRecipe(enderios, bowl, milk, wheat, dustEnderPearl);
            } else {
                registerRecipe(enderios, bowl, milkb, wheat, dustEnderPearl);
            }

            ItemStack dustCoal = new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_COAL.ordinal());
            ItemStack coal = new ItemStack(Items.coal, 1);
            registerRecipe(dustCoal, coal);

            ItemStack dustIron = new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_IRON.ordinal());
            ItemStack iron = new ItemStack(Items.iron_ingot, 1);
            registerRecipe(dustIron, iron);

            ItemStack dustGold = new ItemStack(EnderIO.itemPowderIngot, 1, PowderIngot.POWDER_GOLD.ordinal());
            ItemStack gold = new ItemStack(Items.gold_ingot, 1);
            registerRecipe(dustGold, gold);

            ItemStack dustObsidian4 = new ItemStack(EnderIO.itemPowderIngot, 4, PowderIngot.POWDER_OBSIDIAN.ordinal());
            ItemStack obsidian = new ItemStack(Blocks.obsidian, 1);
            registerRecipe(dustObsidian4, obsidian);

            ItemStack enderDust9 = new ItemStack(EnderIO.itemPowderIngot, 9, PowderIngot.POWDER_ENDER.ordinal());
            ItemStack enderPearl = new ItemStack(Items.ender_pearl);
            registerRecipe(enderDust9, enderPearl);

            ItemStack cbc8 = BINDER_COMPOSITE.getStack(8);
            ItemStack sand2 = new ItemStack(Blocks.sand, 2);
            ItemStack gravel5 = new ItemStack(Blocks.gravel, 5);
            ItemStack gravel6 = new ItemStack(Blocks.gravel, 6);
            ItemStack clay_ball2 = new ItemStack(Items.clay_ball, 2);
            ItemStack clay_ball1 = new ItemStack(Items.clay_ball, 1);
            if (Config.useAlternateBinderRecipe) {
                registerRecipe(cbc8, gravel5, clay_ball2, sand2);
            } else {
                registerRecipe(cbc8, gravel6, clay_ball1, sand2);
            }
            registerRecipe(cbc8, gravel6, clay_ball1, sand2);
            ItemStack cbc = BINDER_COMPOSITE.getStack();
            ItemStack binder4 = CONDUIT_BINDER.getStack(4);
            registerRecipe(binder4, cbc);

            ItemStack silicon = SILICON.getStack(1);
            registerRecipe(silicon, sand2);

            int numConduits = Config.numConduitsPerRecipe;
            ItemStack phasedIronNugget3 = PHASED_IRON_NUGGET.getStack(3);
            ItemStack binder6 = CONDUIT_BINDER.getStack(6);
            ItemStack itemConduit = new ItemStack(EnderIO.itemItemConduit, numConduits, 0);
            registerRecipe(itemConduit, phasedIronNugget3, binder6);
        }
    }

    public static void registerBasicToManyRecipe(Recipe recipe) {
        if (IS_EE3_LOADED) {
            List<ItemStack> in = new ArrayList<ItemStack>();
            for (RecipeInput r0 : recipe.getInputs()) {
                in.add(r0.getInput().copy());
            }
            registerRecipe(recipe.getOutputs()[0].getOutput().copy(), in);
        }
    }
}
