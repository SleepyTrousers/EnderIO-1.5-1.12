package crazypants.enderio;

import javax.annotation.Nonnull;

import net.minecraft.util.text.TextComponentString;

public enum Lang {

  PRINTOUT_ADDTARGET("item.itemLocationPrintout.chat.addTarget"),
  PRINTOUT_SETTARGET("item.itemLocationPrintout.chat.setTarget"),
  PRINTOUT_PRIVATE("gui.travelAccessable.privateBlock"),
  PRINTOUT_NOPAPER("item.itemLocationPrintout.chat.noPaper"),
  MACHINE_CONFIGURED("machine.tooltip.configured"),
  XXXXXX4(""),
  XXXXXX5(""),
  XXXXXX6(""),
  XXXXXX7(""),
  XXXXXX8(""),
  XXXXXX9(""),
  XXXXXX0(""),
  XXXXXXq(""),
  XXXXXXw(""),

  ;

  private final @Nonnull String key;

  private Lang(@Nonnull String key) {
    this.key = key;
  }

  public @Nonnull String get() {
    return EnderIO.lang.localize(key);
  }

  public @Nonnull String get(@Nonnull Object... params) {
    return EnderIO.lang.localize(key, params);
  }

  public @Nonnull TextComponentString toChat() {
    return new TextComponentString(EnderIO.lang.localize(key));
  }

  public @Nonnull TextComponentString toChat(@Nonnull Object... params) {
    return new TextComponentString(EnderIO.lang.localize(key, params));
  }

  static {
    for (Lang lang : values()) {
      if (!EnderIO.lang.canLocalize(lang.key)) {
        Log.error("Missing translation for '" + lang + "': " + lang.get());
      }
    }
  }

}
