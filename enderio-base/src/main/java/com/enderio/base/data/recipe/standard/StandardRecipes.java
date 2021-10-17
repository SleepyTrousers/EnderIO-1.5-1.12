package com.enderio.base.data.recipe.standard;

import net.minecraft.data.DataGenerator;

public class StandardRecipes {
    public static void generate(DataGenerator dataGenerator) {
        dataGenerator.addProvider(new MaterialRecipes(dataGenerator));
        dataGenerator.addProvider(new BlockRecipes(dataGenerator));
    }
}
