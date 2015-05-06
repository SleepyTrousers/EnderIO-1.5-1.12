package crazypants.enderio.item.darksteel.upgrade;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

public interface IRenderUpgrade {

  void render(RenderPlayerEvent event, ItemStack stack, boolean head);

}
