package crazypants.enderio.base.item.darksteel.upgrade.speed;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.Log;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.item.darksteel.attributes.DarkSteelAttributeModifiers;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class SpeedController {

  private static boolean ignoreFovEvent = false;

  @SideOnly(Side.CLIENT)
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void handleFovUpdate(@Nonnull FOVUpdateEvent evt) {

    if (ignoreFovEvent) {
      return;
    }

    EntityPlayer player = NullHelper.notnullF(evt.getEntity(), "FOVUpdateEvent has no player");
    if (!(player instanceof AbstractClientPlayer)) {
      Log.warn("invalid player type when adjusting FOV " + player);
      return;
    }

    IAttributeInstance moveInst = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
    AttributeModifier modifier = moveInst.getModifier(DarkSteelAttributeModifiers.getWalkSpeed(1, 0).getID());
    if (modifier != null) {
      try {
        moveInst.removeModifier(modifier);
        ignoreFovEvent = true;
        evt.setNewfov(((AbstractClientPlayer) player).getFovModifier());
      } finally {
        ignoreFovEvent = false;
        player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(modifier);
      }
    }
  }

}
