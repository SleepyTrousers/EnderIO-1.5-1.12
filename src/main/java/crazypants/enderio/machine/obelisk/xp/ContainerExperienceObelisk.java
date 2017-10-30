package crazypants.enderio.machine.obelisk.xp;

import crazypants.enderio.network.GuiPacket;
import crazypants.enderio.network.IRemoteExec;
import crazypants.enderio.xp.PacketExperienceContainer;
import crazypants.enderio.xp.XpUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import javax.annotation.Nonnull;

public class ContainerExperienceObelisk extends Container implements IRemoteExec.IContainer {

  public static final int ADD_XP = 42;
  public static final int DWN_XP = 43;
  public static final int REM_XP = 44;

  private TileExperienceObelisk inv;
  
  public ContainerExperienceObelisk(TileExperienceObelisk inv) {
    this.inv = inv;
  }

  @Override
  public boolean canInteractWith(@Nonnull EntityPlayer player) {
    return inv.isUseableByPlayer(player);
  }

  private int guiid = -1;

  @Override
  public void setGuiID(int id) {
    guiid = id;
  }

  @Override
  public int getGuiID() {
    return guiid;
  }

  @Override
  public PacketExperienceContainer networkExec(int id, GuiPacket message) {
    switch (id) {
    case ADD_XP:
      inv.getContainer().givePlayerXp(message.getPlayer(), message.getInt(0));
      return new PacketExperienceContainer(inv);
    case DWN_XP:
      if (message.getPlayer().capabilities.isCreativeMode) {
        inv.getContainer().addExperience(XpUtil.getExperienceForLevel(message.getInt(0)));
      } else {
        inv.getContainer().drainPlayerXpToReachPlayerLevel(message.getPlayer(), message.getInt(0));
      }
      break;
    case REM_XP:
      if (message.getPlayer().capabilities.isCreativeMode) {
        inv.getContainer().addExperience(XpUtil.getExperienceForLevel(message.getInt(0)));
      } else {
        inv.getContainer().drainPlayerXpToReachContainerLevel(message.getPlayer(), message.getInt(0));
      }
      break;
    default:
      break;
    }
    return null;
  }

}
