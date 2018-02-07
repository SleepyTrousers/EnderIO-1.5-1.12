package crazypants.enderio.base.integration.jei.energy;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.material.material.Material;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;

public class EnergyIngredientRenderer implements IIngredientRenderer<EnergyIngredient> {

  public static final @Nonnull EnergyIngredientRenderer INSTANCE = new EnergyIngredientRenderer();

  @Override
  public void render(@Nonnull Minecraft minecraft, int xPosition, int yPosition, @Nullable EnergyIngredient ingredient) {
    if (ingredient != null) {
      String energyString = asString(ingredient);
      if (ingredient.hasAmount()) {
        // Render into recipe
        minecraft.fontRenderer.drawString(energyString, xPosition + 1, yPosition + 1, 0x808080, false);
        GlStateManager.color(1, 1, 1, 1);
      } else {
        // Render into item list
        RenderHelper.enableGUIStandardItemLighting();
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(null, Material.POWDER_INFINITY.getStack(), xPosition, yPosition);
        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
        minecraft.fontRenderer.drawString(energyString, xPosition + 5, yPosition + 5, 0xFFFFFF, false);
        GlStateManager.enableDepth();
        GlStateManager.color(1, 1, 1, 1);
      }
    }
  }

  public @Nonnull String asString(@Nonnull EnergyIngredient ingredient) {
    return !ingredient.hasAmount() ? LangPower.RF() : ingredient.isPerTick() ? LangPower.RFt(ingredient.getAmount()) : LangPower.RF(ingredient.getAmount());
  }

  @Override
  public @Nonnull List<String> getTooltip(@Nonnull Minecraft minecraft, @Nonnull EnergyIngredient ingredient, @Nonnull ITooltipFlag tooltipFlag) {
    return Collections.singletonList(asString(ingredient));
  }

  @Override
  public @Nonnull FontRenderer getFontRenderer(@Nonnull Minecraft minecraft, @Nonnull EnergyIngredient ingredient) {
    return minecraft.fontRenderer;
  }

}
