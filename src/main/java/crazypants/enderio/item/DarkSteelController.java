package crazypants.enderio.item;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import crazypants.enderio.EnderIO;

public class DarkSteelController {

  public static final DarkSteelController instance = new DarkSteelController();

  private AttributeModifier walkModifier = new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", 0.3, 1);
  private AttributeModifier sprintModifier = new AttributeModifier(new UUID(6, 320981923), "generic.movementSpeed", 0.6, 1);

  private DarkSteelController() {
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    EntityPlayer player = event.player;

    //boots
    ItemStack boots = player.getEquipmentInSlot(1);
    if(boots != null && boots.getItem() == EnderIO.itemDarkSteelBoots) {
      player.stepHeight = 1.001F;
    } else if(player.stepHeight == 1.001F) {
      player.stepHeight = 0.5001F;
    }

    //leggings
    IAttributeInstance moveInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed);
    moveInst.removeModifier(walkModifier);
    moveInst.removeModifier(sprintModifier);

    walkModifier = new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed", 0.3, 1);
    sprintModifier = new AttributeModifier(new UUID(6, 320981923), "generic.movementSpeed", 0.5, 1);

    ItemStack leggings = player.getEquipmentInSlot(2);
    if(leggings != null && leggings.getItem() == EnderIO.itemDarkSteelLeggings) {
      if(player.isSprinting()) {
        moveInst.applyModifier(sprintModifier);
      } else {
        moveInst.applyModifier(walkModifier);
      }
    }

  }

}
