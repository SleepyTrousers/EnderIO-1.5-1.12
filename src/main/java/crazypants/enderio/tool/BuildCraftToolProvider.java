package crazypants.enderio.tool;

import buildcraft.api.tools.IToolWrench;
import crazypants.enderio.api.tool.ITool;
import java.lang.reflect.Method;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BuildCraftToolProvider implements IToolProvider, IToolImpl {

    private BCWrench wrench = new BCWrench();

    public BuildCraftToolProvider() throws Exception {
        // Do a check for so we throw an exception in the constructor if we dont have the
        // wrench class
        Class.forName("buildcraft.api.tools.IToolWrench");
    }

    @Override
    public ITool getTool(ItemStack stack) {
        if (MpsUtil.instance.isPowerFistEquiped(stack) && !MpsUtil.instance.isOmniToolActive(stack)) {
            return null;
        }
        if (stack.getItem() instanceof IToolWrench) {
            return wrench;
        }
        return null;
    }

    @Override
    public Class<?> getInterface() {
        return IToolWrench.class;
    }

    @Override
    public Object handleMethod(ITool yetaWrench, Method method, Object[] args) {
        if ("canWrench".equals(method.getName())) {
            return true;
        } else if ("wrenchUsed".equals(method.getName())) {
            return null;
        }
        return null;
    }

    public static class BCWrench implements ITool {

        @Override
        public boolean canUse(ItemStack stack, EntityPlayer player, int x, int y, int z) {
            return ((IToolWrench) stack.getItem()).canWrench(player, x, y, z);
        }

        @Override
        public void used(ItemStack stack, EntityPlayer player, int x, int y, int z) {
            ((IToolWrench) stack.getItem()).wrenchUsed(player, x, y, z);
        }

        @Override
        public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
            return true;
        }
    }
}
