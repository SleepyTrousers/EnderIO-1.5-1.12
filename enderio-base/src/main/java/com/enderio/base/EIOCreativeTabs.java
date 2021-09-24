package com.enderio.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.Supplier;

public class EIOCreativeTabs extends CreativeModeTab {
    public static final @Nonnull EIOCreativeTabs MAIN, ITEMS, MATERIALS, MACHINES, SOULS;

    static {
        MAIN = new EIOCreativeTabs("blocks", EIOItems.CREATIVE_ICON_NONE::get);
        ITEMS = new EIOCreativeTabs("items", EIOItems.CREATIVE_ICON_ITEMS::get);
        MATERIALS = new EIOCreativeTabs("materials", EIOItems.CREATIVE_ICON_MATERIALS::get);
        MACHINES = new EIOCreativeTabs("machines", EIOItems.CREATIVE_ICON_MACHINES::get);
        SOULS = new EIOCreativeTabs("souls", EIOItems.CREATIVE_ICON_MOBS::get);
    }

    private final Supplier<Item> itemIcon;

    public EIOCreativeTabs(String name, Supplier<Item> itemIcon) {
        super("enderio." + name);
        this.itemIcon = itemIcon;
        EnderIO.registrate()
            .addLang("itemGroup", new ResourceLocation(EnderIO.DOMAIN, name), getEnglish(name));
    }

    protected String getEnglish(String name) {
        return "EnderIO " + name.substring(0, 1)
            .toUpperCase(Locale.ENGLISH) + name.substring(1);
    }

    @Override
    public ItemStack makeIcon() {
        if (itemIcon.get() == null)
            return new ItemStack(Items.BEDROCK);
        return new ItemStack(itemIcon.get());
    }
}
