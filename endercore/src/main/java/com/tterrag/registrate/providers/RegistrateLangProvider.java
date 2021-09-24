package com.tterrag.registrate.providers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistrateLangProvider extends LanguageProvider implements RegistrateProvider {
    
    private static class AccessibleLanguageProvider extends LanguageProvider {

        public AccessibleLanguageProvider(DataGenerator gen, String modid, String locale) {
            super(gen, modid, locale);
        }

        @Override
        public void add(@Nullable String key, @Nullable String value) {
            super.add(key, value);
        }

        @Override
        protected void addTranslations() {}
    }
    
    private final AbstractRegistrate<?> owner;
    
    private final AccessibleLanguageProvider upsideDown;

    public RegistrateLangProvider(AbstractRegistrate<?> owner, DataGenerator gen) {
        super(gen, owner.getModid(), "en_us");
        this.owner = owner;
        this.upsideDown = new AccessibleLanguageProvider(gen, owner.getModid(), "en_ud");
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.CLIENT;
    }
    
    @Override
    public String getName() {
        return "Lang (en_us/en_ud)";
    }

    @Override
    protected void addTranslations() {
        owner.genData(ProviderType.LANG, this);
    }
    
    public static final String toEnglishName(String internalName) {
        return Arrays.stream(internalName.toLowerCase(Locale.ROOT).split("_"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }
    
    public String getAutomaticName(NonNullSupplier<? extends IForgeRegistryEntry<?>> sup) {
        return toEnglishName(sup.get().getRegistryName().getPath());
    }
    
    public void addBlock(NonNullSupplier<? extends Block> block) {
        addBlock(block, getAutomaticName(block));
    }
    
    public void addBlockWithTooltip(NonNullSupplier<? extends Block> block, String tooltip) {
        addBlock(block);
        addTooltip(block, tooltip);
    }
    
    public void addBlockWithTooltip(NonNullSupplier<? extends Block> block, String name, String tooltip) {
        addBlock(block, name);
        addTooltip(block, tooltip);
    }
    
    public void addItem(NonNullSupplier<? extends Item> item) {
        addItem(item, getAutomaticName(item));
    }
    
    public void addItemWithTooltip(NonNullSupplier<? extends Item> block, String name, List<@NonnullType String> tooltip) {
        addItem(block, name);
        addTooltip(block, tooltip);
    }
    
    public void addTooltip(NonNullSupplier<? extends ItemLike> item, String tooltip) {
        add(item.get().asItem().getDescriptionId() + ".desc", tooltip);
    }
    
    public void addTooltip(NonNullSupplier<? extends ItemLike> item, List<@NonnullType String> tooltip) {
        for (int i = 0; i < tooltip.size(); i++) {
            add(item.get().asItem().getDescriptionId() + ".desc." + i, tooltip.get(i));
        }
    }
    
    public void add(CreativeModeTab group, String name) {
        add(((TranslatableComponent)group.getDisplayName()).getKey(), name);
    }
    
    public void addEntityType(NonNullSupplier<? extends EntityType<?>> entity) {
        addEntityType(entity, getAutomaticName(entity));
    }
    
    // Automatic en_ud generation

    private static final String NORMAL_CHARS = 
            /* lowercase */ "abcdefghijklmn\u00F1opqrstuvwxyz" +
            /* uppercase */ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            /*  numbers  */ "0123456789" +
            /*  special  */ "_,;.?!/\\'";
    private static final String UPSIDE_DOWN_CHARS = 
            /* lowercase */ "\u0250q\u0254p\u01DD\u025Fb\u0265\u0131\u0638\u029E\u05DF\u026Fuuodb\u0279s\u0287n\u028C\u028Dx\u028Ez" +
            /* uppercase */ "\u2C6F\u15FA\u0186\u15E1\u018E\u2132\u2141HI\u017F\u029E\uA780WNO\u0500\u1F49\u1D1AS\u27D8\u2229\u039BMX\u028EZ" +
            /*  numbers  */ "0\u0196\u1105\u0190\u3123\u03DB9\u312586" +
            /*  special  */ "\u203E'\u061B\u02D9\u00BF\u00A1/\\,";
    
    static {
        if (NORMAL_CHARS.length() != UPSIDE_DOWN_CHARS.length()) {
            throw new AssertionError("Char maps do not match in length!");
        }
    }

    private String toUpsideDown(String normal) {
        char[] ud = new char[normal.length()];
        for (int i = 0; i < normal.length(); i++) {
            char c = normal.charAt(i);
            if (c == '%') {
                String fmtArg = "";
                while (Character.isDigit(c) || c == '%' || c == '$' || c == 's' || c == 'd') { // TODO this is a bit lazy
                    fmtArg += c;
                    i++;
                    c = i == normal.length() ? 0 : normal.charAt(i);
                }
                i--;
                for (int j = 0; j < fmtArg.length(); j++) {
                    ud[normal.length() - 1 - i + j] = fmtArg.charAt(j);
                }
                continue;
            }
            int lookup = NORMAL_CHARS.indexOf(c);
            if (lookup >= 0) {
                c = UPSIDE_DOWN_CHARS.charAt(lookup);
            }
            ud[normal.length() - 1 - i] = c;
        }
        return new String(ud);
    }

    @Override
    public void add(String key, String value) {
        super.add(key, value);
        upsideDown.add(key, toUpsideDown(value));
    }

    @Override
    public void run(HashCache cache) throws IOException {
        super.run(cache);
        upsideDown.run(cache);
    }
}
