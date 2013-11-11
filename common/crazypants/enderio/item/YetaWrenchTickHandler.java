package crazypants.enderio.item;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import crazypants.enderio.conduit.ConduitDisplayMode;

public class YetaWrenchTickHandler implements ITickHandler {
  protected int slotSelected = -1;
  public static int dWheel;

  @Override
  public void tickStart(EnumSet<TickType> type, Object... tickData) {

    dWheel = Mouse.getDWheel() / 120;
    EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
    if(player != null && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemYetaWrench
        && player.isSneaking()) {
      slotSelected = player.inventory.currentItem;
    } else {
      slotSelected = -1;
    }

  }

  @Override
  public void tickEnd(EnumSet<TickType> type, Object... tickData) {
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
        if(dif > 0) {
          newMode = curMode.next();
          ConduitDisplayMode.setDisplayMode(stack, newMode);
        } else if(dif < 0) {
          newMode = curMode.previous();
          ConduitDisplayMode.setDisplayMode(stack, newMode);
        }
        if(newMode != null) {
          player.sendQueue.addToSendQueue(YetaWrenchPacketProcessor.getSmeltingModePacket(slotSelected, newMode));
        }
      }
      slotSelected = -1;

    }
  }

  @Override
  public EnumSet<TickType> ticks() {
    return EnumSet.of(TickType.CLIENT);
  }

  @Override
  public String getLabel() {
    return "YetaWrenchTickHandler: Client Tick";
  }

}
