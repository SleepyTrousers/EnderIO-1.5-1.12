package crazypants.enderio.machines.machine.obelisk.xp;

import javax.annotation.Nonnull;

import crazypants.enderio.base.xp.PacketExperienceContainer;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.network.PacketHandler;
import info.loenwind.processor.RemoteCall;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

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

  @RemoteCall
  public void doAddXP(int levels) {
    inv.getContainer().givePlayerXp(player, levels);
    PacketHandler.sendToAllAround(new PacketExperienceContainer(inv), inv);
  }

  @RemoteCall
  public void doDrainXP(int levels) {
    int level = Minecraft.getMinecraft().player.experienceLevel - levels;
    if (player.capabilities.isCreativeMode) {
      inv.getContainer().addExperience(XpUtil.getExperienceForLevel(level));
    } else {
      inv.getContainer().drainPlayerXpToReachPlayerLevel(player, level);
    }
    PacketHandler.sendToAllAround(new PacketExperienceContainer(inv), inv);
  }

  @RemoteCall
  public void doRemoveXP(int level) {
    if (player.capabilities.isCreativeMode) {
      inv.getContainer().addExperience(XpUtil.getExperienceForLevel(level));
    } else {
      inv.getContainer().drainPlayerXpToReachContainerLevel(player, level);
    }
    PacketHandler.sendToAllAround(new PacketExperienceContainer(inv), inv);
  }

}
