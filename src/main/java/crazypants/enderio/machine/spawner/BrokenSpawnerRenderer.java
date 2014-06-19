package crazypants.enderio.machine.spawner;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import crazypants.enderio.EnderIO;

public class BrokenSpawnerRenderer implements IItemRenderer {

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {    
    RenderBlocks rb = (RenderBlocks)data[0];
    rb.setOverrideBlockTexture(EnderIO.itemBrokenSpawner.getIconFromDamage(0));
    rb.renderBlockAsItem(Blocks.stone, 0, 1);
    rb.setOverrideBlockTexture(null);    
  }

}
