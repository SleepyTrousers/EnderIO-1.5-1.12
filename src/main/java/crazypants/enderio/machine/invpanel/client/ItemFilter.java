package crazypants.enderio.machine.invpanel.client;

import crazypants.enderio.conduit.item.filter.IItemFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

public abstract class ItemFilter {

    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s+");

    public abstract boolean matches(ItemEntry entry);

    public static ItemFilter parse(String filter, Locale locale, IItemFilter filterCard) {
        ArrayList<ItemFilter> list = new ArrayList<ItemFilter>();
        if (filterCard != null) {
            list.add(new CardFilter(filterCard));
        }

        String[] parts = SPLIT_PATTERN.split(filter);
        for (String part : parts) {
            if (part.startsWith("@")) {
                part = part.substring(1);
                if (!part.isEmpty()) {
                    list.add(new ModFilter(part, locale));
                }
            } else if (!part.isEmpty()) {
                list.add(new NameFilter(part, locale));
            }
        }

        if (list.isEmpty()) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        return new AndFilter(list.toArray(new ItemFilter[list.size()]));
    }

    static class AndFilter extends ItemFilter {
        final ItemFilter[] list;

        AndFilter(ItemFilter[] list) {
            this.list = list;
        }

        @Override
        public boolean matches(ItemEntry entry) {
            for (ItemFilter f : list) {
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

    static class ModFilter extends ItemFilter {
        final String text;
        final Locale locale;

        ModFilter(String text, Locale locale) {
            this.text = text.toLowerCase(locale);
            this.locale = locale;
        }

        @Override
        public boolean matches(ItemEntry entry) {
            return entry.getModId().toLowerCase(locale).contains(text);
        }

        @Override
        public String toString() {
            return "@" + text;
        }
    }

    static class NameFilter extends ItemFilter {
        final String text;
        final Locale locale;

        NameFilter(String text, Locale locale) {
            this.text = text.toLowerCase(locale);
            this.locale = locale;
        }

        @Override
        public boolean matches(ItemEntry entry) {
            return entry.getLowercaseUnlocName(locale).contains(text);
        }

        @Override
        public String toString() {
            return text;
        }
    }

    static class CardFilter extends ItemFilter {
        final IItemFilter filter;

        CardFilter(IItemFilter filter) {
            this.filter = filter;
        }

        @Override
        public boolean matches(ItemEntry entry) {
            return filter.doesItemPassFilter(null, entry.makeItemStack());
        }
    }
}
