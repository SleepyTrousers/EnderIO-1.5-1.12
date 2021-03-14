package crazypants.enderio.base.config.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.registry.Registry;
import info.loenwind.autoconfig.factory.FactoryManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.SERVER)
public class CommandConfig extends CommandBase {

  private final @Nonnull Map<String, Configuration> configurations;
  private final @Nonnull TreeMap<String, ConfigCategory> sections = new TreeMap<>();

  @SubscribeEvent
  public static void onStarting(EnderIOLifecycleEvent.ServerStarting.Dedicated event) {
    event.getEvent().registerServerCommand(new CommandConfig());
  }

  private CommandConfig() {
    configurations = Registry.getConfigurations();
    for (Entry<String, Configuration> entry : configurations.entrySet()) {
      for (String name : entry.getValue().getCategoryNames()) {
        sections.put(entry.getKey() + "." + name, entry.getValue().getCategory(name));
      }
    }
  }

  @Override
  public @Nonnull String getName() {
    return "enderio";
  }

  @Override
  public @Nonnull String getUsage(@Nonnull ICommandSender sender) {
    return "/" + getName() + " config set <section> <key> <value>|config get <section> <key>|config list [<section>]|config save|config help";
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
    } else if (match(args, "config", "help", null)) {
      doHelp(server, sender, args[2]);
    } else if (match(args, "config", "help")) {
      doHelp(server, sender, "0");
    } else
      throw new WrongUsageException(getUsage(sender));
  }

  private void doHelp(MinecraftServer server, ICommandSender sender, String page) {
    Integer no = getPageNumber(page);
    sender.sendMessage(new TextComponentTranslation("enderio.command.config.help." + no));
    if (no < 5) {
      sender.sendMessage(new TextComponentTranslation("enderio.command.config.help.more", no + 1));
    }
  }

  protected Integer getPageNumber(String page) {
    try {
      return MathHelper.clamp(Integer.valueOf(page), 0, 4);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private void doSave(MinecraftServer server, ICommandSender sender) {
    MinecraftForge.EVENT_BUS.post(new OnConfigChangedEvent(EnderIO.MODID, null, true, false));
    sender.sendMessage(new TextComponentTranslation("enderio.command.config.saved"));
  }

  @SuppressWarnings("null")
  private void doList(MinecraftServer server, ICommandSender sender, String section) {
    if (sections.containsKey(section) && !sections.get(section).isEmpty()) {
      // (1) exact section name. List keys
      sender.sendMessage(
          new TextComponentString(sections.get(section).getOrderedValues().stream().map(property -> property.getName()).collect(Collectors.joining(", "))));
    } else {
      String list = sections.keySet().stream().filter(name -> name.startsWith(section)).collect(Collectors.joining(", "));
      if (list != null && !list.isEmpty()) {
        // (2) partial section name, matches the start of names. List sections
        sender.sendMessage(new TextComponentString(list));
      } else {
        list = sections.keySet().stream().filter(name -> name.contains(section)).collect(Collectors.joining(", "));
        if (list != null && !list.isEmpty()) {
          // (2) partial section name, matches any substring. List sections
          sender.sendMessage(new TextComponentString(list));
        } else {
          sender.sendMessage(new TextComponentTranslation("enderio.command.config.error.section"));
        }
      }
    }
  }

  @SuppressWarnings("null")
  private void doList(MinecraftServer server, ICommandSender sender) {
    // (4) no section name. List all sections
    sender.sendMessage(new TextComponentString(sections.keySet().stream().collect(Collectors.joining(", "))));
  }

  private void doGet(MinecraftServer server, ICommandSender sender, String section, String key) {
    if (sections.containsKey(section) && !sections.get(section).isEmpty()) {
      Property property = sections.get(section).get(key);
      if (property != null) {
        sendKeyComments(sender, property, section, key, false, false);
      } else {
        sender.sendMessage(new TextComponentTranslation("enderio.command.config.error.key"));
      }
    } else {
      sender.sendMessage(new TextComponentTranslation("enderio.command.config.error.section"));
    }
  }

  private void sendKeyComments(ICommandSender sender, Property property, String section, String key, boolean isSet, boolean isValidSet) {
    String comment = property.getComment();
    if (comment != null && !comment.isEmpty()) {
      if (!isSet) {
        sender.sendMessage(new TextComponentString(comment));
      }
    } else {
      comment = "";
    }
    sender.sendMessage(new TextComponentString(section + " " + key + " = " + property.getString()));
    if (!comment.contains(FactoryManager.SERVER_OVERRIDE)) {
      sender.sendMessage(new TextComponentTranslation("enderio.command.config.warn.clientvalue"));
    } else if (isSet) {
      sender.sendMessage(new TextComponentTranslation("enderio.command.config.warn.reconnect"));
    }
    if (comment.contains(FactoryManager.SERVER_SYNC)) {
      sender.sendMessage(new TextComponentTranslation("enderio.command.config.warn.notsynced"));
    }
    if (isSet) {
      if (!isValidSet) {
        sender.sendMessage(new TextComponentTranslation("enderio.command.config.warn.unchecked"));
      }
      sender.sendMessage(new TextComponentTranslation("enderio.command.config.warn.save"));
    }
  }

  private void doSet(MinecraftServer server, ICommandSender sender, String section, String key, String value) {
    if (sections.containsKey(section) && !sections.get(section).isEmpty()) {
      Property property = sections.get(section).get(key);
      if (property != null) {

        String[] validValues = property.getValidValues();
        if (validValues != null && validValues.length > 0) {
          for (String validValue : validValues) {
            if (value.equals(validValue)) {
              property.set(value);
              sendKeyComments(sender, property, section, key, true, true);
              return;
            }
          }
          sender.sendMessage(new TextComponentTranslation("enderio.command.config.error.key", joinNiceString(validValues)));
        } else {
          property.set(value);
          sendKeyComments(sender, property, section, key, true, false);
        }
      } else {
        sender.sendMessage(new TextComponentTranslation("enderio.command.config.error.key"));
      }
    } else {
      sender.sendMessage(new TextComponentTranslation("enderio.command.config.error.section"));
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
        return getListOfStringsMatchingLastWord(args, "set", "get", "list", "save", "help");
      } else if ("set".equals(args[1]) || "get".equals(args[1]) || "list".equals(args[1])) {
        if (args.length == 3) {
          return getListOfStringsMatchingLastWord(args, sections.keySet().toArray(new String[0]));
        } else if ("set".equals(args[1]) || "get".equals(args[1])) {
          if (sections.containsKey(args[2]) && !sections.get(args[2]).isEmpty()) {
            if (args.length == 4) {
              return getListOfStringsMatchingLastWord(args,
                  sections.get(args[2]).getOrderedValues().stream().map(property -> property.getName()).collect(Collectors.toList()).toArray(new String[0]));
            } else if ("set".equals(args[1])) {
              Property property = sections.get(args[2]).get(args[3]);
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
