package crazypants.enderio.diagnostics;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;

public class DebugCommand extends CommandBase {

  public static final @Nonnull DebugCommand SERVER = new DebugCommand(Side.SERVER);
  public static final @Nonnull DebugCommand CLIENT = new DebugCommand(Side.CLIENT);

  private final Side side;

  public static ICommandSender debugger = null;

  private DebugCommand(Side side) {
    this.side = side;
  }

  @Override
  public String getCommandName() {
    return side == Side.SERVER ? "eioToggleDebugServer" : "eioToggleDebug";
  }

  @Override
  public String getCommandUsage(ICommandSender p_71518_1_) {
    return "/" + getCommandName();
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  @Override
  public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
    return null;
  }

  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return true;
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    if (side == Side.CLIENT == sender.getEntityWorld().isRemote) {
      if (debugger == sender) {
        debugger = null;
        sendResult(sender, "Debugging disabled on " + side);
      } else if (debugger != null) {
        sendResult(debugger, "Debugging disabled on " + side + " by " + sender);
        debugger = null;
      } else {
        debugger = sender;
        sendResult(sender, "Debugging enabled on " + side);
      }
    }
  }

  private void sendResult(ICommandSender player, String result) {
    player.addChatMessage(new TextComponentString(result));
  }

  public boolean isEnabled() {
    return isEnabled(null);
  }

  public boolean isEnabled(ICommandSender player) {
    if (debugger == null || debugger.getCommandSenderEntity() == null || debugger.getCommandSenderEntity().isDead) {
      debugger = null;
    }
    return debugger != null && (player == null || playerEquals(player, debugger));
  }

  private boolean playerEquals(ICommandSender a, ICommandSender b) {
    if (a instanceof EntityPlayer && b instanceof EntityPlayer) {
      if (!((EntityPlayer) a).isDead && !((EntityPlayer) b).isDead) {
        return ((EntityPlayer) a).getUniqueID().equals(((EntityPlayer) b).getUniqueID());
      }
    }
    return false;
  }

  public void debug(String... strings) {
    if (isEnabled()) {
      for (String string : strings) {
        sendResult(debugger, string);
      }
    }
  }
}
