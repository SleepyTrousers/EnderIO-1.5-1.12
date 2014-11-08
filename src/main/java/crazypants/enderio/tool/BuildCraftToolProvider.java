package crazypants.enderio.tool;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import buildcraft.api.tools.IToolWrench;

public class BuildCraftToolProvider implements IToolProvider, IToolImpl {

  private BCWrench wrench = new BCWrench();

  @Override
  public ITool getTool(ItemStack stack) {
    if(MpsUtil.instance.isPowerFistEquiped(stack) && !MpsUtil.instance.isOmniToolActive(stack)) {
      return null;
    }
    if(stack.getItem() instanceof IToolWrench) {
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
    if("canWrench".equals(method.getName())) {
      return true;
    } else if("wrenchUsed".equals(method.getName())) {
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

  }

}
