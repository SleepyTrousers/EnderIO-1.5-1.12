package crazypants.enderio.base.config.command;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.SERVER)
public class CommandConfig extends CommandBase {

  @SubscribeEvent
  public static void onStarting(EnderIOLifecycleEvent.ServerStarting.Dedicated event) {
    event.getEvent().registerServerCommand(new CommandConfig());
  }

  @Override
  public @Nonnull String getName() {
    return "enderio";
  }

  @Override
  public @Nonnull String getUsage(@Nonnull ICommandSender sender) {
    return "/" + getName() + " config set <section> <key> <value>|config get <section> <key>|config list [<section>]|config save";
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
    if (match(args, "config", "set", null, null, null)) {
      doSet(server, sender, args[2], args[3], args[4]);
    } else if (match(args, "config", "get", null, null)) {
      doGet(server, sender, args[2], args[3]);
    } else if (match(args, "config", "list", null)) {
      doList(server, sender, args[2]);
    } else if (match(args, "config", "list")) {
      doList(server, sender);
    } else if (match(args, "config", "save")) {
      doSave(server, sender);
    } else
      throw new WrongUsageException(getUsage(sender));
  }

  @SuppressWarnings("null")
  private void doList(MinecraftServer server, ICommandSender sender) {
    sender.sendMessage(new TextComponentString(EnderIO.getInstance().getConfiguration().getCategoryNames().stream().collect(Collectors.joining(", "))));
  }

  private void doSave(MinecraftServer server, ICommandSender sender) {
    MinecraftForge.EVENT_BUS.post(new OnConfigChangedEvent(EnderIO.MODID, null, true, false));
    sender.sendMessage(new TextComponentString("Ender IO Configuration saved"));
  }

  @SuppressWarnings("null")
  private void doList(MinecraftServer server, ICommandSender sender, String section) {
    Configuration configuration = EnderIO.getInstance().getConfiguration();
    if (configuration.hasCategory(section) && !configuration.getCategory(section).isEmpty()) {
      sender.sendMessage(new TextComponentString(
          configuration.getCategory(section).getOrderedValues().stream().map(property -> property.getName()).collect(Collectors.joining(", "))));
    } else {
      sender.sendMessage(new TextComponentString("No such section"));
    }
  }

  private void doGet(MinecraftServer server, ICommandSender sender, String section, String key) {
    Configuration configuration = EnderIO.getInstance().getConfiguration();
    if (configuration.hasCategory(section) && !configuration.getCategory(section).isEmpty()) {
      Property property = configuration.getCategory(section).get(key);
      if (property != null) {
        String comment = property.getComment();
        if (comment != null && !comment.isEmpty()) {
          sender.sendMessage(new TextComponentString(comment));
        }
        sender.sendMessage(new TextComponentString(section + " " + key + " = " + property.getString()));
      } else {
        sender.sendMessage(new TextComponentString("No such key"));
      }
    } else {
      sender.sendMessage(new TextComponentString("No such section"));
    }
  }

  private void doSet(MinecraftServer server, ICommandSender sender, String section, String key, String value) {
    Configuration configuration = EnderIO.getInstance().getConfiguration();
    if (configuration.hasCategory(section) && !configuration.getCategory(section).isEmpty()) {
      Property property = configuration.getCategory(section).get(key);
      if (property != null) {

        String[] validValues = property.getValidValues();
        if (validValues != null && validValues.length > 0) {
          for (String validValue : validValues) {
            if (value.equals(validValue)) {
              property.set(value);
              sender.sendMessage(new TextComponentString(section + " " + key + " = " + property.getString()));
              sender.sendMessage(new TextComponentString("Players may need to reconnect to experience the change"));
              sender.sendMessage(new TextComponentString("You need to save the config for this change to persist"));
              return;
            }
          }
          sender.sendMessage(new TextComponentString("Invalid value. Possible: " + joinNiceString(validValues)));
        } else {
          property.set(value);
          sender.sendMessage(new TextComponentString(section + " " + key + " = " + property.getString()));
          sender.sendMessage(new TextComponentString("Restrictions on possible values may apply"));
          sender.sendMessage(new TextComponentString("Players may need to reconnect to experience the change"));
          sender.sendMessage(new TextComponentString("You need to save the config for this change to persist"));
        }
      } else {
        sender.sendMessage(new TextComponentString("No such key"));
      }
    } else {
      sender.sendMessage(new TextComponentString("No such section"));
    }
  }

  private boolean match(@Nonnull String[] args, String... pattern) {
    if (args.length != pattern.length) {
      return false;
    }
    for (int i = 0; i < pattern.length; i++) {
      if (pattern[i] != null && !pattern[i].equals(args[i])) {
        return false;
      }
    }
    return true;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args,
      @Nullable BlockPos targetPos) {
    if (args.length <= 1) {
      return getListOfStringsMatchingLastWord(args, "config");
    }
    if ("config".equals(args[0])) {
      if (args.length == 2) {
        return getListOfStringsMatchingLastWord(args, "set", "get", "list", "save");
      }
      if ("set".equals(args[1]) || "get".equals(args[1]) || "list".equals(args[1])) {
        if (args.length == 3) {
          return getListOfStringsMatchingLastWord(args, EnderIO.getInstance().getConfiguration().getCategoryNames().toArray(new String[0]));
        }
        if ("set".equals(args[1]) || "get".equals(args[1])) {
          if (args.length == 4) {
            return getListOfStringsMatchingLastWord(args, EnderIO.getInstance().getConfiguration().getCategory(args[2]).getOrderedValues().stream()
                .map(property -> property.getName()).collect(Collectors.toList()).toArray(new String[0]));
          }
          if ("set".equals(args[1])) {
            Configuration configuration = EnderIO.getInstance().getConfiguration();
            if (configuration.hasCategory(args[2]) && !configuration.getCategory(args[2]).isEmpty()) {
              Property property = configuration.getCategory(args[2]).get(args[3]);
              if (property != null) {
                String[] validValues = property.getValidValues();
                if (validValues != null && validValues.length > 0) {
                  return getListOfStringsMatchingLastWord(args, validValues);
                }
              }
            }
          }
        }
      }
    }
    return Collections.emptyList();
  }

}
