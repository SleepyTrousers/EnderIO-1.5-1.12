package com.enderio.machines.data.recipe;

import com.enderio.machines.data.recipe.enchanter.EnchanterRecipeGenerator;

import net.minecraft.data.DataGenerator;

public class MachineRecipeGenerator {

    public static void generate(DataGenerator dataGenerator) {
        dataGenerator.addProvider(new EnchanterRecipeGenerator(dataGenerator));
    }
}
