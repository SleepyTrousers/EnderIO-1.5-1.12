package crazypants.enderio.base.gui.tooltip;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler.ITooltipCallback;

import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.recipe.sagmill.IGrindingMultiplier;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TooltipHandlerGrinding implements ITooltipCallback {

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {

  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    IGrindingMultiplier ball = SagMillRecipeManager.getInstance().getGrindballFromStack(itemstack);
    list.add(Lang.GRINDING_BALL_1.get(TextFormatting.BLUE));
    list.add(Lang.GRINDING_BALL_2.get(TextFormatting.GRAY, toPercent(ball.getGrindingMultiplier())));
    list.add(Lang.GRINDING_BALL_3.get(TextFormatting.GRAY, toPercent(ball.getChanceMultiplier())));
    list.add(Lang.GRINDING_BALL_4.get(TextFormatting.GRAY, toPercent(1 - ball.getPowerMultiplier())));
  }

  private String toPercent(float fl) {
    fl = fl * 100;
    int per = Math.round(fl);
    return " " + per + "%";
  }

  @Override
  public boolean shouldHandleItem(@Nonnull ItemStack item) {
    return SagMillRecipeManager.getInstance().getGrindballFromStack(item) != null;
  }

}