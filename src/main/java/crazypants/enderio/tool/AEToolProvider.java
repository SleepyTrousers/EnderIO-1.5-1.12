package crazypants.enderio.tool;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import appeng.api.implementations.items.IAEWrench;
import crazypants.enderio.api.tool.ITool;

public class AEToolProvider implements IToolProvider, IToolImpl {

  private AETool aeTool = new AETool();

  public AEToolProvider() throws Exception {
    //Do a check for so we throw an exception in the constructor if we dont have the
    // wrench class
    Class.forName("appeng.api.implementations.items.IAEWrench");
  }

  @Override
  public Class<?> getInterface() {
    return IAEWrench.class;
  }

  @Override
  public Object handleMethod(ITool yetaWrench, Method method, Object[] args) {
    return true;
  }

  @Override
  public ITool getTool(ItemStack stack) {
    if(!(stack.getItem() instanceof IAEWrench)) {
      return null;
    }
    return aeTool;
  }

  public static class AETool implements ITool {

    @Override
    public boolean canUse(ItemStack stack, EntityPlayer player, int x, int y, int z) {
      if(!(stack.getItem() instanceof IAEWrench)) {
        return false;
      }
      IAEWrench wrench = (IAEWrench) stack.getItem();
      return wrench.canWrench(stack, player, x, y, z);
    }

    @Override
    public void used(ItemStack stack, EntityPlayer player, int x, int y, int z) {
    }
    
    @Override
    public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
      return true;
    }
  }
}
