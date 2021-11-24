package com.enderio.base.common.enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enderio.base.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.common.util.TooltipUtil;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.EnchantmentBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@SuppressWarnings("unused")
@EventBusSubscriber
public class EIOEnchantments {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    // region enchantments

    public static final RegistryEntry<AutoSmeltEnchantment> AUTO_SMELT = enchantmentBuilder("auto_smelt", new AutoSmeltEnchantment())
        .lang("Auto Smelt")
        .register();

    public static final RegistryEntry<RepellentEnchantment> REPELLENT = enchantmentBuilder("repellent", new RepellentEnchantment())
        .lang("Repellent")
        .register();

    public static final RegistryEntry<ShimmerEnchantment> SHIMMER = enchantmentBuilder("shimmer", new ShimmerEnchantment()).lang("Shimmer").register();

    public static final RegistryEntry<SoulBoundEnchantment> SOULBOUND = enchantmentBuilder("soulbound", new SoulBoundEnchantment())
        .lang("Soulbound")
        .register();

    // TODO: HOUSEKEEPING: Rename these to something better, and split bow and crossbow into separate enchantments.
    public static final RegistryEntry<WitherArrowEnchantment> WITHER_ARROW = enchantmentBuilder("wither_arrow", new WitherArrowEnchantment())
        .lang("Wither Arrows")
        .register();

    public static final RegistryEntry<WitherWeaponEnchantment> WITHER_WEAPON = enchantmentBuilder("wither_weapon", new WitherWeaponEnchantment())
        .lang("Wither Weapon")
        .register();

    public static final RegistryEntry<XPBoostEnchantment> XP_BOOST = enchantmentBuilder("xp_boost", new XPBoostEnchantment()).lang("XP Boost").register();

    // endregion

    // region builders

    private static <T extends EIOBaseEnchantment> EnchantmentBuilder<T, Registrate> enchantmentBuilder(String name, T enchantment) {
        return REGISTRATE.enchantment(name, enchantment.getCategory(), (r, c, s) -> enchantment);
    }
    
    private static void addTooltip(ItemTooltipEvent event, Map<Enchantment, Integer> enchantments, List<Component> toolTip, Enchantment enchantment, Component component) {
        if (enchantments.containsKey(enchantment)) {
            toolTip.stream().forEach(c -> {
                if(c.equals(enchantment.getFullname(enchantments.get(enchantment)))) {
                    event.getToolTip().add(event.getToolTip().indexOf(c)+1, component);
                }
            });
        }
    }

    // endregion
    
    // Renders Enchantment tooltips.
    @SubscribeEvent
    static void tooltip(ItemTooltipEvent event) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(event.getItemStack());
        List<Component> toolTip = new ArrayList<>(event.getToolTip());
        if (!enchantments.isEmpty()) {
            addTooltip(event, enchantments, toolTip, AUTO_SMELT.get(), EIOLang.AUTO_SMELT_DESC);
            addTooltip(event, enchantments, toolTip, REPELLENT.get(), EIOLang.REPELLENT_DESC);
            addTooltip(event, enchantments, toolTip, SHIMMER.get(), EIOLang.SHIMMER_DESC);
            addTooltip(event, enchantments, toolTip, SOULBOUND.get(), EIOLang.SOULBOUND_DESC);
            addTooltip(event, enchantments, toolTip, WITHER_ARROW.get(), EIOLang.WITHER_ARROW_DESC);
            addTooltip(event, enchantments, toolTip, WITHER_WEAPON.get(), EIOLang.WITHER_WEAPON_DESC);
            addTooltip(event, enchantments, toolTip, XP_BOOST.get(), EIOLang.XP_BOOST_DESC);
        }
    }

    public static void register() {
    }
}
