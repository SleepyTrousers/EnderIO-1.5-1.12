package crazypants.enderio.machines.machine.obelisk.xp;

import javax.annotation.Nonnull;

import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.xp.PacketExperienceContainer;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.network.PacketHandler;
import info.loenwind.processor.RemoteCall;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.MathHelper;

@RemoteCall
public class ContainerExperienceObelisk extends Container {

  public static final int ADD_XP = 42;
  public static final int DWN_XP = 43;
  public static final int REM_XP = 44;

  private final @Nonnull TileExperienceObelisk inv;
  private final @Nonnull EntityPlayer player;

  public ContainerExperienceObelisk(@Nonnull EntityPlayer player, @Nonnull TileExperienceObelisk inv) {
    this.inv = inv;
    this.player = player;
  }

  @Override
  public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
    return inv.isUseableByPlayer(playerIn);
  }

  /*
   * Note that creative adds/removed levels relative to the obelisk while survival works relative to the player's level.
   */

  @RemoteCall
  public void doAddXP(int levels) {
    try {
      if (player.capabilities.isCreativeMode) {
        int containerLevel = inv.getContainer().getExperienceLevel();
        int targetLevel = Math.max(0, containerLevel - levels);
        long diffxp = XpUtil.getExperienceForLevelL(containerLevel) - XpUtil.getExperienceForLevelL(targetLevel);
        inv.getContainer().removeExperience(diffxp);
        XpUtil.addPlayerXP(player, diffxp);
      } else {
        inv.getContainer().givePlayerXp(player, Math.max(levels, 0));
      }
    } catch (XpUtil.TooManyXPLevelsException e) {
      player.sendStatusMessage(Lang.GUI_TOO_MANY_LEVELS.toChatServer(), true);
    }
    PacketHandler.sendToAllAround(new PacketExperienceContainer(inv), inv);
  }

  @RemoteCall
  public void doDrainXP(int levels) {
    if (player.capabilities.isCreativeMode) {
      int containerLevel = inv.getContainer().getExperienceLevel();
      int targetLevel = Math.min(containerLevel + levels, XpUtil.getLevelForExperience(inv.getContainer().getMaximumExperience()));
      long diffxp = XpUtil.getExperienceForLevelL(targetLevel) - XpUtil.getExperienceForLevelL(containerLevel);
      inv.getContainer().addExperience(diffxp);
    } else {
      try {
        int level = MathHelper.clamp(Minecraft.getMinecraft().player.experienceLevel - levels, 0, Minecraft.getMinecraft().player.experienceLevel);
        inv.getContainer().drainPlayerXpToReachPlayerLevel(player, level);
      } catch (XpUtil.TooManyXPLevelsException e) {
        player.sendStatusMessage(Lang.GUI_TOO_MANY_LEVELS.toChatServer(), true);
      }
    }
    PacketHandler.sendToAllAround(new PacketExperienceContainer(inv), inv);
  }

  // TODO: HL: Find out why this is unused before deleting it completely. I do not like finding unused code like this, it smells like a bug that it's not
  // called...

  // @RemoteCall
  // public void doRemoveXP(int level) {
  // if (player.capabilities.isCreativeMode) {
  // inv.getContainer().addExperience(XpUtil.getExperienceForLevel(level));
  // } else {
  // inv.getContainer().drainPlayerXpToReachContainerLevel(player, level);
  // }
  // PacketHandler.sendToAllAround(new PacketExperienceContainer(inv), inv);
  // }

}
