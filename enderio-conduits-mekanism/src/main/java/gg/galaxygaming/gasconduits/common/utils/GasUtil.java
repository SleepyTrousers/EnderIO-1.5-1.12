package gg.galaxygaming.gasconduits.common.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class GasUtil {

    public static boolean areGasesTheSame(@Nullable Gas gas, @Nullable Gas gas2) {
        if (gas == null) {
            return gas2 == null;
        }
        if (gas2 == null) {
            return false;
        }
        return gas == gas2 || gas.getName().equals(gas2.getName());
    }

    public static GasStack getGasTypeFromItem(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        if (stack.getItem() instanceof IGasItem) {
            return ((IGasItem) stack.getItem()).getGas(stack);
        }
        return null;
    }

    public static GasStack getGasStack(IGasHandler handler, EnumFacing side) {
        if (handler == null) {
            return null;
        }
        GasTankInfo[] tankInfo = handler.getTankInfo();
        int stored = 0;
        Gas type = null;
        for (GasTankInfo info : tankInfo) {
            GasStack gasStack = info.getGas();
            //Require current found type to be null or the same as this tank's type
            // and this gas type to be accessible from the side accessed
            if (gasStack != null && (type == null || areGasesTheSame(type, gasStack.getGas())) && handler.canDrawGas(side, gasStack.getGas())) {
                stored += info.getStored();
                type = gasStack.getGas();
            }
        }

        if (type == null || stored <= 0) {
            return null;
        }
        return new GasStack(type, stored);
    }
}