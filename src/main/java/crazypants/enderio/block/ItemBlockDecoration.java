package crazypants.enderio.block;

import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemBlockDecoration extends ItemBlock implements IOverlayRenderAware {
  public ItemBlockDecoration(Block block, String name) {
    super(block);
    setHasSubtypes(true);
    setRegistryName(name);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    doItemOverlayIntoGUI(stack, xPosition, yPosition);
  }

  @SideOnly(Side.CLIENT)
  public static void doItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {

    FontRenderer fr = Minecraft.getMinecraft().getRenderManager().getFontRenderer();

    if (fr == null) {
      // The hotbar can be rendered before the render manager is initialized...
      return;
    }

    GlStateManager.disableLighting();
    GlStateManager.disableDepth();
    GlStateManager.disableBlend();
    fr.drawStringWithShadow("\"", xPosition, yPosition, 16777215);
    fr.drawStringWithShadow("\"", xPosition + 19 - 2 - fr.getStringWidth("\""), yPosition, 16777215);
    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    GlStateManager.enableBlend();
  }

}