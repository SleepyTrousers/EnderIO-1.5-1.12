package crazypants.enderio.gui;

import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.Config;

public class TooltipAddera {


  public static TooltipAddera instance = new TooltipAddera();

  static {
    MinecraftForge.EVENT_BUS.register(instance);
  }

  @SubscribeEvent
  public void addTooltip(ItemTooltipEvent evt) {
    if(evt.itemStack == null) {
      return;
    }
    if(Config.addFurnaceFuelTootip) {
      int time = TileEntityFurnace.getItemBurnTime(evt.itemStack);
      if(time > 0) {
        evt.toolTip.add("Burn time " + time);
      }
    }
  }


}
