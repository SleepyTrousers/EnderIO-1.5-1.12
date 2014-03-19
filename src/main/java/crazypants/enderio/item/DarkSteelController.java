package crazypants.enderio.item;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;

public class DarkSteelController {

  public static final DarkSteelController instance = new DarkSteelController();

  private AttributeModifier walkModifier = new AttributeModifier(new UUID(12879874982l, 320981923), "generic.movementSpeed",
      Config.darkSteelLeggingWalkModifier, 1);
  private AttributeModifier sprintModifier = new AttributeModifier(new UUID(6, 320981923), "generic.movementSpeed",
      Config.darkSteelLeggingSprintModifier, 1);

  private boolean wasJumping;
  private int jumpCount;
  private int ticksSinceLastJump;

  private DarkSteelController() {
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    EntityPlayer player = event.player;

    //boots step height
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

    ItemStack leggings = player.getEquipmentInSlot(2);
    if(leggings != null && leggings.getItem() == EnderIO.itemDarkSteelLeggings) {
      if(player.isSprinting()) {
        moveInst.applyModifier(sprintModifier);
      } else {
        moveInst.applyModifier(walkModifier);
      }
    }

  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if(event.phase == TickEvent.Phase.END) {
      EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
      if(player == null) {
        return;
      }
      MovementInput input = player.movementInput;
      if(input.jump && !wasJumping) {
        jumpCount = 1;
        player.motionY += 0.15 * Config.darkSteelBootsJumpModifier;
        ticksSinceLastJump = 0;
      } else if(input.jump && jumpCount < 2 && ticksSinceLastJump > 5) {
        player.motionY += 0.15 * Config.darkSteelBootsJumpModifier * 2;
        jumpCount = 2;
        ticksSinceLastJump = 0;
      }

      wasJumping = !player.isCollidedVertically;
      if(!wasJumping) {
        jumpCount = 0;
      }
      ticksSinceLastJump++;
    }
  }

}
