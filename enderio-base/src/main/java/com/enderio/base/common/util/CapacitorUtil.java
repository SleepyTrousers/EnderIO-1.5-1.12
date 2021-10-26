package com.enderio.base.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.capability.capacitors.ICapacitorData;
import com.enderio.base.common.recipe.CapacitorDataRecipe;
import com.enderio.base.common.recipe.EIORecipes;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Helper class for Capacitors
 */
public class CapacitorUtil {
    /**
     * Static maps with specializations for the "basic"
     */
    private static ArrayList<String> types = new ArrayList<>();

    static {
        types.add(ICapacitorData.ALL_ENERGY_CONSUMPSTION);
        types.add(ICapacitorData.ALL_PRODUCTION_SPEED);
        types.add(ICapacitorData.ALLOY_ENERGY_CONSUMPSTION);
        types.add(ICapacitorData.ALLOY_ENERGY_CONSUMPSTION);
    }

    public static void addType(String type) {
        types.add(type);
    }

    public static Optional<ICapacitorData> getCapacitorData(ItemStack itemStack, Level level) {
        LazyOptional<ICapacitorData> capacitorDataCap = itemStack.getCapability(EIOCapabilities.CAPACITOR);
        if (capacitorDataCap.isPresent())
            return Optional.of(capacitorDataCap.orElseThrow(NullPointerException::new));
        Optional<CapacitorDataRecipe> recipe = level
            .getRecipeManager()
            .getAllRecipesFor(EIORecipes.Types.CAPACITOR_DATA)
            .stream()
            .filter(r -> r.matches(itemStack))
            .findFirst();
        return recipe.map(CapacitorDataRecipe::getCapacitorData);
    }

    /**
     * Returns a random type from the list for loot capacitors.
     *
     * @return
     */
    public static String getRandomType() {
        return types.get(new Random().nextInt(types.size()));
    }

    /**
     * Adds a tooltip for loot capacitors based on it's stats.
     *
     * @param stack
     * @param tooltipComponents
     */
    public static void getTooltip(ItemStack stack, List<Component> tooltipComponents) {
        stack.getCapability(EIOCapabilities.CAPACITOR).ifPresent(cap -> {
            TranslatableComponent t = new TranslatableComponent(getFlavor(cap.getFlavor()),
                getGradeText(cap.getSpecializations().values().stream().findFirst().get()),
                getTypeText(cap.getSpecializations().keySet().stream().findFirst().get()), getBaseText(cap.getBase()));
            tooltipComponents.add(t);
        });
    }

    //TODO depending on direction
    private static String getFlavor(int flavor) {
        return "description.enderio.capacitor.flavor." + flavor;
    }

    //TODO depending on direction
    private static TranslatableComponent getBaseText(float base) {
        TranslatableComponent t = new TranslatableComponent("description.enderio.capacitor.base." + (int) Math.ceil(base));
        t.withStyle(ChatFormatting.ITALIC);
        return t;
    }

    //TODO depending on direction
    private static TranslatableComponent getTypeText(String type) {
        TranslatableComponent t = new TranslatableComponent("description.enderio.capacitor.type." + type);
        t.withStyle(ChatFormatting.ITALIC);
        return t;
    }

    //TODO depending on direction
    private static TranslatableComponent getGradeText(float grade) {
        TranslatableComponent t = new TranslatableComponent("description.enderio.capacitor.grade." + (int) Math.ceil(grade));
        t.withStyle(ChatFormatting.ITALIC);
        return t;
    }
}
