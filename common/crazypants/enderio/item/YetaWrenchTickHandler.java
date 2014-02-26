package crazypants.enderio.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import crazypants.enderio.conduit.ConduitDisplayMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

public class YetaWrenchTickHandler {
  protected int slotSelected = -1;
  public static int dWheel;


  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if(event.phase == TickEvent.Phase.START) {
      tickStart();
    } else {
      tickEnd();
    }
  }

  public void tickStart() {
    dWheel = Mouse.getDWheel() / 120;
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    if(player != null && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemYetaWrench
        && player.isSneaking()) {
      slotSelected = player.inventory.currentItem;
    } else {
      slotSelected = -1;
    }

  }

  public void tickEnd() {
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    if(player != null) {
      if(slotSelected > -1 && dWheel != Mouse.getDWheel()) {
        player.inventory.currentItem = slotSelected;
        Minecraft.getMinecraft().playerController.updateController();

        ItemStack stack = player.inventory.getStackInSlot(slotSelected);
        ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(stack);
        if(curMode == null) {
          curMode = ConduitDisplayMode.ALL;
        }

        int dif = dWheel - Mouse.getDWheel();
        ConduitDisplayMode newMode = null;
        if(dif < 0) {
          newMode = curMode.next();
          ConduitDisplayMode.setDisplayMode(stack, newMode);
        } else if(dif > 0) {
          newMode = curMode.previous();
          ConduitDisplayMode.setDisplayMode(stack, newMode);
        }
        if(newMode != null) {
          //TODO:1.7
          //player.sendQueue.addToSendQueue(YetaWrenchPacketProcessor.getWrenchModePacket(slotSelected, newMode));
        }
      }
      slotSelected = -1;

    }
  }

//  @Override
//  public EnumSet<TickType> ticks() {
//    return EnumSet.of(TickType.CLIENT);
//  }
//
//  @Override
//  public String getLabel() {
//    return "YetaWrenchTickHandler: Client Tick";
//  }

}
