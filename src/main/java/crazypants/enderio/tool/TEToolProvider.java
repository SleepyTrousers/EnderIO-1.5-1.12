package crazypants.enderio.tool;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import crazypants.enderio.api.tool.ITool;

public class TEToolProvider implements IToolProvider, IToolImpl {

  private TEHammer wrench = new TEHammer();

  @Override
  public ITool getTool(ItemStack stack) {
    if(stack.getItem() instanceof IToolHammer) {
      return wrench;
    }
    return null;
  }

  public static class TEHammer implements ITool {

    @Override
    public boolean canUse(ItemStack stack, EntityPlayer player, int x, int y, int z) {
      return ((IToolHammer) stack.getItem()).isUsable(stack, player, x, y, z);
    }

    @Override
    public void used(ItemStack stack, EntityPlayer player, int x, int y, int z) {
      ((IToolHammer) stack.getItem()).toolUsed(stack, player, x, y, z);
    }
    
    @Override
    public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
      return true;
    }
  }

  @Override
  public Class<?> getInterface() {
    return IToolHammer.class;
  }

  @Override
  public Object handleMethod(ITool yetaWrench, Method method, Object[] args) {
    if("isUsable".equals(method.getName())) {
      return true;
    } else if("toolUsed".equals(method.getName())) {
      toolUsed((ItemStack) args[0], (EntityLivingBase) args[1], (Integer) args[2], (Integer) args[3], (Integer) args[4]);
      return null;
    }
    return null;
  }

  public void toolUsed(ItemStack item, EntityLivingBase user, int x, int y, int z) {
    Block block = user.worldObj.getBlock(x, y, z);
    if(user.isSneaking() && block instanceof IDismantleable && user instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) user;
      IDismantleable machine = (IDismantleable) block;
      if(machine.canDismantle(player, player.worldObj, x, y, z) && !player.worldObj.isRemote) {
        machine.dismantleBlock(player, player.worldObj, x, y, z, false);
      }
    }
  }

}
