package crazypants.enderio.base.material.food;

import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SpecialFont extends FontRenderer {

  private final @Nonnull FontRenderer wrapped;
  private final @Nonnull ItemStack stack = EnderFood.ENDERIOS.getStack();
  private int inARow = 0;

  public SpecialFont(@Nonnull FontRenderer wrapped) {
    super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
    this.wrapped = wrapped;
  }

  @Override
  public int drawString(@Nonnull String string, float x, float y, int color, boolean dropShadow) {
    boolean pop = false;
    if (isSmallText(string)) {
      GL11.glPushMatrix();
      GL11.glScalef(0.5f, 0.5f, 0.5f);
      GL11.glTranslated(x, y, 0);
      GL11.glTranslated(0, -this.FONT_HEIGHT * inARow + FONT_HEIGHT, 0);
      inARow++;
      pop = true;
    } else {
      inARow = 0;
    }
    int ret = wrapped.drawString(string, x, y, color, dropShadow);
    if (pop) {
      GL11.glPopMatrix();
    }
    return ret;
  }

  @Override
  public int getStringWidth(@Nonnull String p_78256_1_) {
    int ret = wrapped.getStringWidth(p_78256_1_);
    if (isSmallText(p_78256_1_)) {
      ret /= 2;
    }
    return ret;
  }

  private boolean isSmallText(String string) {
    List<String> lines = Lists.newArrayList();
    SpecialTooltipHandler.addDetailedTooltipFromResources(lines, stack.getUnlocalizedName());
    return lines.contains(TextFormatting.getTextWithoutFormattingCodes(string));
  }

  @Override
  public int getCharWidth(char p_78263_1_) {
    return wrapped.getCharWidth(p_78263_1_);
  }
}