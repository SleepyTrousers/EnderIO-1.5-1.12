package crazypants.enderio.invpanel.integration.jei;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.invpanel.init.InvpanelObject;
import crazypants.enderio.invpanel.invpanel.BlockInventoryPanel;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;

public class InvPanelSubtypeInterpreter implements ISubtypeInterpreter {

  public static void registerSubtypes(ISubtypeRegistry subtypeRegistry) {
    subtypeRegistry.registerSubtypeInterpreter(InvpanelObject.blockInventoryPanel.getItemNN(), new InvPanelSubtypeInterpreter());
  }

  @Override
  public @Nonnull String apply(@Nullable ItemStack itemStack) {
    if (itemStack == null) {
      throw new NullPointerException("You want me to return something Nonnull for a null ItemStack? F.U.");
    }
    int tint = ((BlockInventoryPanel) InvpanelObject.blockInventoryPanel.getBlockNN()).getItemTint(itemStack, 1);
    return tint >= 0 ? "c" + tint : "";
  }

}
