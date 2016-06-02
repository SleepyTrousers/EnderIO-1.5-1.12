package crazypants.enderio.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiContainerBaseEIO extends GuiContainerBase {

  private final List<ResourceLocation> guiTextures = new ArrayList<ResourceLocation>();

  public GuiContainerBaseEIO(Container par1Container, String... guiTexture) {
    super(par1Container);
    for (String string : guiTexture) {
      guiTextures.add(EnderIO.proxy.getGuiTexture(string));
    }
  }

  public void bindGuiTexture() {
    bindGuiTexture(0);
  }

  public void bindGuiTexture(int id) {
    RenderUtil.bindTexture(getGuiTexture(id));
  }

  protected ResourceLocation getGuiTexture(int id) {
    return guiTextures.size() > id ? guiTextures.get(id) : null;
  }

  public List<Rectangle> getBlockingAreas() {
    return Collections.<Rectangle> emptyList();
  }

}
