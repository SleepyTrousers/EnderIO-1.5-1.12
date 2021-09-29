package com.enderio.base.common.item;

import com.enderio.base.EnderIO;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Locale;
import java.util.function.Supplier;

public class EIOCreativeTabs extends CreativeModeTab {
    public static final EIOCreativeTabs MAIN = new EIOCreativeTabs("main", EIOItems.CREATIVE_ICON_NONE::get);
    public static final EIOCreativeTabs GEAR = new EIOCreativeTabs("gear", EIOItems.CREATIVE_ICON_ITEMS::get);
    public static final EIOCreativeTabs BLOCKS = new EIOCreativeTabs("blocks", EIOItems.CREATIVE_ICON_MATERIALS::get);
    public static final EIOCreativeTabs MACHINES = new EIOCreativeTabs("machines", EIOItems.CREATIVE_ICON_MACHINES::get);
    public static final EIOCreativeTabs SOULS = new EIOCreativeTabs("souls", EIOItems.CREATIVE_ICON_MOBS::get);

    private final Supplier<Item> itemIcon;

    public EIOCreativeTabs(String name, Supplier<Item> itemIcon) {
        super("enderio." + name);
        this.itemIcon = itemIcon;
        EnderIO
            .registrate()
            .addLang("itemGroup", new ResourceLocation(EnderIO.DOMAIN, name), getEnglish(name));
    }

    protected String getEnglish(String name) {
        if (name.equals("main")) return "EnderIO";
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
