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
import cofh.api.energy.IEnergyContainerItem;
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
    EnderIO.packetPipeline.registerPacket(PacketDarkSteelPowerPacket.class);
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    EntityPlayer player = event.player;

    //boots step height
    ItemStack boots = player.getEquipmentInSlot(1);
    if(boots != null && boots.getItem() == EnderIO.itemDarkSteelBoots) {
      player.stepHeight = 1.0023F;
    } else if(player.stepHeight == 1.0023F) {
      player.stepHeight = 0.5001F;
    }

    //leggings
    IAttributeInstance moveInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed);
    moveInst.removeModifier(walkModifier);
    moveInst.removeModifier(sprintModifier);

    ItemStack leggings = player.getEquipmentInSlot(2);
    if(leggings != null && leggings.getItem() == EnderIO.itemDarkSteelLeggings) {

      double horzMovement = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
      double costModifier = player.isSprinting() ? (Config.darkSteelSprintPowerCost * 1.25) : Config.darkSteelWalkPowerCost;
      int cost = (int) (horzMovement * costModifier);

      int totalEnergy = getPlayerEnergy(player, EnderIO.itemDarkSteelLeggings);
      if(totalEnergy > 0 && totalEnergy >= cost) {
        usePlayerEnergy(player, EnderIO.itemDarkSteelLeggings, cost);
        if(player.isSprinting()) {
          moveInst.applyModifier(sprintModifier);
        } else {
          moveInst.applyModifier(walkModifier);
        }
      }
    }

  }

  void usePlayerEnergy(EntityPlayer player, ItemDarkSteelArmor armor, int cost) {

    if(cost == 0) {
      return;
    }

    int remaining = cost;
    if(Config.darkSteelDrainPowerFromInventory) {
      for (ItemStack stack : player.inventory.mainInventory) {
        if(stack != null && stack.getItem() instanceof IEnergyContainerItem) {
          IEnergyContainerItem cont = (IEnergyContainerItem) stack.getItem();
          remaining -= cont.extractEnergy(stack, remaining, false);
          if(remaining <= 0) {
            return;
          }
        }
      }
    }

    if(armor != null) {
      ItemStack stack = player.inventory.armorInventory[3 - armor.armorType];
      if(stack != null) {
        armor.extractEnergy(stack, remaining, false);
      }
    }
  }

  private int getPlayerEnergy(EntityPlayer player, ItemDarkSteelArmor armor) {
    int res = 0;

    if(Config.darkSteelDrainPowerFromInventory) {
      for (ItemStack stack : player.inventory.mainInventory) {
        if(stack != null && stack.getItem() instanceof IEnergyContainerItem) {
          IEnergyContainerItem cont = (IEnergyContainerItem) stack.getItem();
          res += cont.extractEnergy(stack, Integer.MAX_VALUE, true);
        }
      }
    }
    if(armor != null) {
      ItemStack stack = player.inventory.armorInventory[3 - armor.armorType];
      res = armor.getEnergyStored(stack);
    }
    return res;
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
        doJump(player);
      } else if(input.jump && jumpCount < 3 && ticksSinceLastJump > 5) {
        doJump(player);
      }

      wasJumping = !player.isCollidedVertically;
      if(!wasJumping) {
        jumpCount = 0;
      }
      ticksSinceLastJump++;
    }
  }

  @SideOnly(Side.CLIENT)
  private void doJump(EntityClientPlayerMP player) {

    ItemStack boots = player.getEquipmentInSlot(1);
    if(boots != null && boots.getItem() == EnderIO.itemDarkSteelBoots) {
      int requiredPower = Config.darkSteelBootsJumpPowerCost * (int) Math.pow(jumpCount + 1, 2.5);
      int availablePower = getPlayerEnergy(player, EnderIO.itemDarkSteelBoots);

      if(availablePower > 0 && requiredPower <= availablePower) {
        jumpCount++;
        player.motionY += 0.15 * Config.darkSteelBootsJumpModifier * jumpCount;
        ticksSinceLastJump = 0;
        usePlayerEnergy(player, EnderIO.itemDarkSteelBoots, requiredPower);
        EnderIO.packetPipeline.sendToServer(new PacketDarkSteelPowerPacket(requiredPower, EnderIO.itemDarkSteelBoots.armorType));
      }
    }

  }

}
