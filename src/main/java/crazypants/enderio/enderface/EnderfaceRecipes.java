package crazypants.enderio.enderface;

import crazypants.enderio.EnderIO;
import crazypants.enderio.material.Alloy;
import crazypants.util.RecipeUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EnderfaceRecipes {

    public static void addRecipes() {

        ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);
        String electricalSteel = Alloy.ELECTRICAL_STEEL.getOreIngot();
        RecipeUtil.addShaped(
                new ItemStack(EnderIO.blockEnderIo),
                "sqs",
                "qeq",
                "sqs",
                's',
                electricalSteel,
                'q',
                fusedQuartz,
                'e',
                new ItemStack(Items.ender_eye));
    }
}
