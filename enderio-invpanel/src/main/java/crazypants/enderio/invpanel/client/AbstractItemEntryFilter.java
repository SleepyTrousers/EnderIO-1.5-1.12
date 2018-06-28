package crazypants.enderio.invpanel.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.filter.item.IItemFilter;

public abstract class AbstractItemEntryFilter {

  private static final @Nonnull Pattern SPLIT_PATTERN = Pattern.compile("\\s+");

  public abstract boolean matches(@Nonnull ItemEntry entry);

  @Nullable
  public static AbstractItemEntryFilter parse(@Nonnull String filter, @Nonnull Locale locale, @Nullable IItemFilter filterCard) {
    ArrayList<AbstractItemEntryFilter> list = new ArrayList<AbstractItemEntryFilter>();
    if (filterCard != null) {
      list.add(new CardEntryFilter(filterCard));
    }

    String[] parts = SPLIT_PATTERN.split(filter);
    for (String part : parts) {
      if (part.startsWith("@")) {
        part = part.substring(1);
        if (!part.isEmpty()) {
          list.add(new ModEntryFilter(part, locale));
        }
      } else if (!part.isEmpty()) {
        list.add(new NameEntryFilter(part, locale));
      }
    }

    if (list.isEmpty()) {
      return null;
    }
    if (list.size() == 1) {
      return list.get(0);
    }
    return new AndEntryFilter(list.toArray(new AbstractItemEntryFilter[list.size()]));
  }

  static class AndEntryFilter extends AbstractItemEntryFilter {

    final @Nonnull AbstractItemEntryFilter[] list;

    AndEntryFilter(@Nonnull AbstractItemEntryFilter[] list) {
      this.list = list;
    }

    @Override
    public boolean matches(@Nonnull ItemEntry entry) {
      for (AbstractItemEntryFilter f : list) {
        if (!f.matches(entry)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public String toString() {
      return Arrays.deepToString(list);
    }
  }

  static class ModEntryFilter extends AbstractItemEntryFilter {
    final @Nonnull String text;
    final @Nonnull Locale locale;

    ModEntryFilter(@Nonnull String text, @Nonnull Locale locale) {
      this.text = text.toLowerCase(locale);
      this.locale = locale;
    }

    @Override
    public boolean matches(@Nonnull ItemEntry entry) {
      return entry.getModId().toLowerCase(locale).contains(text);
    }

    @Override
    @Nonnull
    public String toString() {
      return "@" + text;
    }
  }

  static class NameEntryFilter extends AbstractItemEntryFilter {
    final @Nonnull String text;
    final @Nonnull Locale locale;

    NameEntryFilter(@Nonnull String text, @Nonnull Locale locale) {
      this.text = text.toLowerCase(locale);
      this.locale = locale;
    }

    @Override
    public boolean matches(@Nonnull ItemEntry entry) {
      return entry.getLowercaseUnlocName(locale).contains(text);
    }

    @Override
    public String toString() {
      return text;
    }
  }

  static class CardEntryFilter extends AbstractItemEntryFilter {
    final @Nonnull IItemFilter filter;

    CardEntryFilter(@Nonnull IItemFilter filter) {
      this.filter = filter;
    }

    @Override
    public boolean matches(@Nonnull ItemEntry entry) {
      return filter.doesItemPassFilter(null, entry.makeItemStack());
    }
  }
}
