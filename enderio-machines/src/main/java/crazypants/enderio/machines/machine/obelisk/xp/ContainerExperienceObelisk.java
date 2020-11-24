package crazypants.enderio.machines.machine.obelisk.xp;

import javax.annotation.Nonnull;

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

  @RemoteCall
  public void doAddXP(int levels) {
    inv.getContainer().givePlayerXp(player, MathHelper.clamp(levels, 0, 10000 /* Random value higher than max levels player can have */));
    PacketHandler.sendToAllAround(new PacketExperienceContainer(inv), inv);
  }

  @RemoteCall
  public void doDrainXP(int levels) {
    int level = MathHelper.clamp(Minecraft.getMinecraft().player.experienceLevel - levels, 0, Minecraft.getMinecraft().player.experienceLevel);
    if (player.capabilities.isCreativeMode) {
      inv.getContainer().addExperience(XpUtil.getExperienceForLevel(level));
    } else {
      inv.getContainer().drainPlayerXpToReachPlayerLevel(player, level);
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
