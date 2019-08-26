package crazypants.enderio.machines.machine.obelisk.xp;

import javax.annotation.Nonnull;

import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public interface IExperienceObeliskRemoteExec {

  public static final int ADD_XP = 0;
  public static final int DWN_XP = 1;
  public static final int REM_XP = 2;

  public interface GUI extends IRemoteExec.IGui {

    default void doAddXP(int levels) {
      GuiPacket.send(this, ADD_XP, levels);
    }

    default void doDrainXP(int levels) {
      GuiPacket.send(this, DWN_XP, Minecraft.getMinecraft().player.experienceLevel - levels);
    }

    default void doRemoveXP(int levels) {
      GuiPacket.send(this, REM_XP, levels);
    }

  }

  public interface Container extends IRemoteExec.IContainer {

    IMessage doAddXP(@Nonnull EntityPlayer player, int levels);

    IMessage doDrainXP(@Nonnull EntityPlayer player, int level);

    IMessage doRemoveXP(@Nonnull EntityPlayer player, int level);

    @Override
    default IMessage networkExec(int id, @Nonnull GuiPacket message) {
      switch (id) {
      case ADD_XP:
        return doAddXP(message.getPlayer(), message.getInt(0));
      case DWN_XP:
        return doDrainXP(message.getPlayer(), message.getInt(0));
      case REM_XP:
        return doRemoveXP(message.getPlayer(), message.getInt(0));
      }
      return null;
    }

  }

}
