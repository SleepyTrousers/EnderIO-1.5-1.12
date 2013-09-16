package crazypants.enderio.material;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLLog;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;

public class MachinePartRenderer implements IItemRenderer {

  private ItemRenderer itemRenderer = new ItemRenderer(Minecraft.getMinecraft());
  private RenderItem renderItem = new RenderItem();

  public MachinePartRenderer() {
  }

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    if (item != null && item.getItemDamage() == MachinePart.MACHINE_CHASSI.ordinal()) {
      return type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.INVENTORY
          || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    } else {
      return false;
    }
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    if (type == ItemRenderType.INVENTORY) {
      RenderBlocks renderBlocks = (RenderBlocks) data[0];
      renderToInventory(item, renderBlocks);
    } else if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
      renderEquipped(item, (RenderBlocks) data[0]);
    } else if (type == ItemRenderType.ENTITY) {
      renderEntity(item, (RenderBlocks) data[0]);
    } else {
      FMLLog.warning("MachinePartRenderer.renderItem: Unsupported render type");
    }
  }

  private void renderEntity(ItemStack item, RenderBlocks renderBlocks) {
    GL11.glPushMatrix();    
    GL11.glScalef(0.5f, 0.5f, 0.5f);
    renderToInventory(item, renderBlocks);
    GL11.glPopMatrix();
  }

  private void renderEquipped(ItemStack item, RenderBlocks renderBlocks) {
    GL11.glPushMatrix();
    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    renderToInventory(item, renderBlocks);
    GL11.glPopMatrix();
  }

  private void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {
    renderBlocks.setOverrideBlockTexture(EnderIO.itemMachinePart.getIconFromDamage(item.getItemDamage()));
    renderBlocks.renderBlockAsItem(Block.stone, 0, 1.0F);
    renderBlocks.clearOverrideBlockTexture();
  }
}
