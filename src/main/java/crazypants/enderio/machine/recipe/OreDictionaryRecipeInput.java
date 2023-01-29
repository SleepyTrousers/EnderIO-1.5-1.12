package crazypants.enderio.machine.recipe;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryRecipeInput extends RecipeInput {

    private int oreId;

    public OreDictionaryRecipeInput(ItemStack itemStack, int oreId, int slot) {
        this(itemStack, oreId, 1, slot);
    }

    public OreDictionaryRecipeInput(ItemStack stack, int oreId, float multiplier, int slot) {
        super(stack, true, multiplier, slot);
        this.oreId = oreId;
    }

    public OreDictionaryRecipeInput(OreDictionaryRecipeInput copy) {
        super(copy.getInput(), true, copy.getMulitplier(), copy.getSlotNumber());
        oreId = copy.oreId;
    }

    public RecipeInput copy() {
        return new OreDictionaryRecipeInput(this);
    }

    @Override
    public boolean isInput(ItemStack test) {
        if (test == null || oreId < 0) {
            return false;
        }
        try {
            int[] ids = OreDictionary.getOreIDs(test);
            if (ids == null) {
                return false;
            }
            for (int id : ids) {
                if (id == oreId) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ItemStack[] getEquivelentInputs() {
        ArrayList<ItemStack> res = OreDictionary.getOres(oreId);
        if (res == null || res.isEmpty()) {
            return null;
        }
        ItemStack[] res2 = res.toArray(new ItemStack[res.size()]);
        for (int i = 0; i < res.size(); ++i) {
            res2[i] = res2[i].copy();
            res2[i].stackSize = getInput().stackSize;
        }
        return res2;
    }

    @Override
    public String toString() {
        return "OreDictionaryRecipeInput [oreId=" + oreId
                + " name="
                + OreDictionary.getOreName(oreId)
                + " amount="
                + getInput().stackSize
                + "]";
    }
}
