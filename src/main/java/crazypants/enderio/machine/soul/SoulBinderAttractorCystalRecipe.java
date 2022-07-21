package crazypants.enderio.machine.soul;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.material.Material;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SoulBinderAttractorCystalRecipe extends AbstractSoulBinderRecipe {

    public static SoulBinderAttractorCystalRecipe instance = new SoulBinderAttractorCystalRecipe();

    private SoulBinderAttractorCystalRecipe() {
        super(
                Config.soulBinderAttractorCystalRF,
                Config.soulBinderAttractorCystalLevels,
                "SoulBinderAttractorCystalRecipe",
                EntityVillager.class);
    }

    @Override
    public ItemStack getInputStack() {
        return new ItemStack(Items.emerald);
    }

    @Override
    public ItemStack getOutputStack() {
        return new ItemStack(EnderIO.itemMaterial, 1, Material.ATTRACTOR_CRYSTAL.ordinal());
    }
}
