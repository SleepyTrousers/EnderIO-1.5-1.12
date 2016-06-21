package crazypants.enderio.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;

public abstract class GuiContainerBaseEIO extends GuiContainerBase {

  private static final String TEXTURE_PATH = ":textures/gui/23/";
  private static final String TEXTURE_EXT = ".png";

  private final List<ResourceLocation> guiTextures = new ArrayList<ResourceLocation>();

  public GuiContainerBaseEIO(Container par1Container, String... guiTexture) {
    super(par1Container);
    for (String string : guiTexture) {
      guiTextures.add(getGuiTexture(string));
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

  public static ResourceLocation getGuiTexture(String name) {
    return new ResourceLocation(EnderIO.DOMAIN + TEXTURE_PATH + name + TEXTURE_EXT);
  }

}
