package crazypants.enderio.base.filter.gui;

import java.awt.Color;
import java.awt.Rectangle;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.filter.filters.ModItemFilter;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

public class ModItemFilterGui implements IItemFilterGui {

  private static final int MOD_NAME_COLOR = ColorUtil.getRGB(Color.white);

  private final IItemFilterContainer filterContainer;
  private final GuiContainerBaseEIO gui;

  boolean isInput;

  private final ModItemFilter filter;

  private final Rectangle[] inputBounds;

  private final IconButton[] deleteButs;

  private final IconButton whiteListB;

  private final int inputOffsetX;
  private final int tfWidth;
  private final int tfTextureX;
  private final int tfTextureY;

  // TODO Remove isInput
  public ModItemFilterGui(@Nonnull GuiContainerBaseEIO gui, @Nonnull IItemFilterContainer filterContainer, boolean isInput) {
    this.gui = gui;
    this.isInput = isInput;
    this.filterContainer = filterContainer;

    if (isInput) {
      filter = (ModItemFilter) filterContainer.getItemFilter();
      inputOffsetX = 50;
      tfWidth = 96;
      tfTextureX = 120;
      tfTextureY = 214;
    } else {
      filter = (ModItemFilter) filterContainer.getItemFilter();
      inputOffsetX = 32;
      tfWidth = 114;
      tfTextureX = 120;
      tfTextureY = 238;
    }

    inputBounds = new Rectangle[] { new Rectangle(inputOffsetX, 47, 16, 16), new Rectangle(inputOffsetX, 68, 16, 16), new Rectangle(inputOffsetX, 89, 16, 16) };

    deleteButs = new IconButton[inputBounds.length];
    for (int i = 0; i < deleteButs.length; i++) {
      Rectangle r = inputBounds[i];
      IconButton but = new IconButton(gui, FilterGuiUtil.nextButtonId(), r.x + 19, r.y, IconEIO.MINUS);
      deleteButs[i] = but;
    }

    whiteListB = new IconButton(gui, -1, inputOffsetX - 19, 89, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));
  }

  @Override
  public void deactivate() {
    for (IconButton but : deleteButs) {
      but.detach();
    }
    whiteListB.detach();
  }

  @Override
  public void updateButtons() {
    for (IconButton but : deleteButs) {
      but.onGuiInit();
    }

    whiteListB.onGuiInit();
    if (filter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.blacklist"));
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.item.whitelist"));
    }
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    for (int i = 0; i < deleteButs.length; i++) {
      IconButton but = deleteButs[i];
      if (but.id == guiButton.id) {
        setMod(i, null);
        return;
      }
    }
    if (guiButton == whiteListB) {
      toggleBlacklist();
    }
  }

  @Override
  public void renderCustomOptions(int top, float par1, int par2, int par3) {
    GL11.glColor3f(1, 1, 1);
    gui.bindGuiTexture();
    for (Rectangle r : inputBounds) {
      // slot
      gui.drawTexturedModalRect(gui.getGuiLeft() + r.x - 1, gui.getGuiTop() + r.y - 1, 24, 214, 18, 18);
      // text box
      gui.drawTexturedModalRect(gui.getGuiLeft() + r.x + 38, gui.getGuiTop() + r.y - 1, tfTextureX, tfTextureY, tfWidth, 18);
    }

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    for (int i = 0; i < inputBounds.length; i++) {
      String mod = filter.getModAt(i);
      if (mod != null) {
        Rectangle r = inputBounds[i];
        mod = fr.trimStringToWidth(mod, tfWidth - 6);
        fr.drawStringWithShadow(mod, gui.getGuiLeft() + r.x + 41, gui.getGuiTop() + r.y + 4, MOD_NAME_COLOR);
      }
    }
  }

  @Override
  public void mouseClicked(int x, int y, int par3) {
    ItemStack st = Minecraft.getMinecraft().player.inventory.getItemStack();
    if (st.isEmpty()) {
      return;
    }

    for (int i = 0; i < inputBounds.length; i++) {
      Rectangle bound = inputBounds[i];
      if (bound.contains(x, y)) {
        setMod(i, st);
      }
    }
  }

  // TODO Decouple from conduits?
  private void setMod(int i, ItemStack st) {
    String mod = filter.setMod(i, st);
    // PacketHandler.INSTANCE.sendToServer(new PacketModItemFilter(itemConduit, gui.getDir(), isInput, i, mod));

  }

  private void toggleBlacklist() {
    filter.setBlacklist(!filter.isBlacklist());
    // PacketHandler.INSTANCE.sendToServer(new PacketModItemFilter(itemConduit, gui.getDir(), isInput, -1, filter.isBlacklist() ? "1"
    // : "0"));
  }

}
