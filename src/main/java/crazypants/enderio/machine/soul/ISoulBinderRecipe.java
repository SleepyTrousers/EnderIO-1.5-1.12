package crazypants.enderio.machine.soul;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface ISoulBinderRecipe {

    ItemStack getInputStack();

    ItemStack getOutputStack();

    List<String> getSupportedSouls();

    int getEnergyRequired();

    int getExperienceLevelsRequired();

    int getExperienceRequired();
}
