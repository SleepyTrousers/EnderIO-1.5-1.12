package crazypants.enderio.base.handler;

import java.util.Iterator;

import javax.annotation.Nonnull;

import org.lwjgl.input.Keyboard;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.integration.jei.JeiAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
@SideOnly(Side.CLIENT)
public class RecipeButtonHandler {

  @SubscribeEvent
  public static void onGuiInit(InitGuiEvent.Post event) {
    GuiScreen gui = event.getGui();
    if (gui != null && (gui instanceof GuiInventory || gui instanceof GuiCrafting) && (PersonalConfig.recipeButtonDisableAlways.get()
        || ((PersonalConfig.recipeButtonReplaceWithJei.get() || PersonalConfig.recipeButtonDisableWithJei.get()) && JeiAccessor.isJeiRuntimeAvailable()))) {
      Iterator<GuiButton> iterator = event.getButtonList().iterator();
      while (iterator.hasNext()) {
        GuiButton button = iterator.next();
        if (button instanceof GuiButtonImage && button.id == 10) {
          iterator.remove();
          if (PersonalConfig.recipeButtonReplaceWithJei.get() && JeiAccessor.isJeiRuntimeAvailable()) {
            event.getButtonList().add(new WrapperGuiButton((GuiButtonImage) button, (IRecipeShownListener) gui));
          }
          return;
        }
      }
    }
  }

  private static class WrapperGuiButton extends GuiButton {

    private final @Nonnull GuiButtonImage wrapped;
    private final @Nonnull IRecipeShownListener gui;

    public WrapperGuiButton(@Nonnull GuiButtonImage wrapped, @Nonnull IRecipeShownListener gui) {
      super(-1, 0, 0, "");
      this.wrapped = wrapped;
      this.gui = gui;
      sync();
    }

    private void sync() {
      if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || gui.func_194310_f().isVisible()) {
        id = 10;
      } else {
        id = -1;
      }
      width = wrapped.width;
      height = wrapped.height;
      x = wrapped.x;
      y = wrapped.y;
      displayString = wrapped.displayString;
      enabled = wrapped.enabled;
      visible = wrapped.visible;
      packedFGColour = wrapped.packedFGColour;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      sync();
      wrapped.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
      if (id != 10 && mouseX >= x && mouseY >= y && mouseX <= (x + width) && mouseY <= (y + height)) {
        JeiAccessor.showCraftingRecipes();
        return;
      }
      wrapped.mouseReleased(mouseX, mouseY);
    }

    @Override
    public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
      return wrapped.mousePressed(mc, mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver() {
      return wrapped.isMouseOver();
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
      sync();
      wrapped.drawButtonForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void playPressSound(@Nonnull SoundHandler soundHandlerIn) {
      wrapped.playPressSound(soundHandlerIn);
    }

    @Override
    public int getButtonWidth() {
      sync();
      return wrapped.getButtonWidth();
    }

    @Override
    public void setWidth(int width) {
      wrapped.setWidth(width);
      sync();
    }

  }

}
