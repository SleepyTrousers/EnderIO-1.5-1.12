package crazypants.enderio.machines.machine.obelisk.xp;

import javax.annotation.Nonnull;

import crazypants.enderio.base.xp.PacketExperienceContainer;
import crazypants.enderio.base.xp.XpUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerExperienceObelisk extends Container implements IExperienceObeliskRemoteExec.Container {

  public static final int ADD_XP = 42;
  public static final int DWN_XP = 43;
  public static final int REM_XP = 44;

  private final @Nonnull TileExperienceObelisk inv;

  public ContainerExperienceObelisk(@Nonnull TileExperienceObelisk inv) {
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
  public IMessage doAddXP(@Nonnull EntityPlayer player, int levels) {
    inv.getContainer().givePlayerXp(player, levels);
    return new PacketExperienceContainer(inv);
  }

  @Override
  public IMessage doDrainXP(@Nonnull EntityPlayer player, int level) {
    if (player.capabilities.isCreativeMode) {
      inv.getContainer().addExperience(XpUtil.getExperienceForLevel(level));
    } else {
      inv.getContainer().drainPlayerXpToReachPlayerLevel(player, level);
    }
    return new PacketExperienceContainer(inv);
  }

  @Override
  public IMessage doRemoveXP(@Nonnull EntityPlayer player, int level) {
    if (player.capabilities.isCreativeMode) {
      inv.getContainer().addExperience(XpUtil.getExperienceForLevel(level));
    } else {
      inv.getContainer().drainPlayerXpToReachContainerLevel(player, level);
    }
    return new PacketExperienceContainer(inv);
  }

}
