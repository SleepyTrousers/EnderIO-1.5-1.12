package com.enderio.base.common.enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enderio.base.EnderIO;
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

    //TODO should crossbow and bow be handled as different enchant classes for the sake of levels/costs?
    public static final RegistryEntry<WitherArrowEnchantment> WITHER_ARROW = enchantmentBuilder("wither_arrow", new WitherArrowEnchantment())
        .lang("Wither Arrows")
        .register();

    public static final RegistryEntry<WitherWeaponEnchantment> WITHER_WEAPON = enchantmentBuilder("wither_weapon", new WitherWeaponEnchantment())
        .lang("Wither Weapon")
        .register();

    public static final RegistryEntry<XPBoostEnchantment> XP_BOOST = enchantmentBuilder("xp_boost", new XPBoostEnchantment()).lang("XP Boost").register();

    // endregion

    // region description

    public static final Component AUTO_SMELT_DESC = descriptionBuilder("auto_smelt", "Automatically smeltes whatever is mined");
    public static final Component REPELLENT_DESC = descriptionBuilder("repellent",
        "Chance to teleport attackers away\nHigher levels teleport more often and farther");
    public static final Component SHIMMER_DESC = descriptionBuilder("shimmer",
        "Makes the item shimmer as if it was enchanted.\nThat's all.\nReally.\nNothing more.\nYes, it is useless.\nI know.");
    public static final Component SOULBOUND_DESC = descriptionBuilder("soulbound",
        "Prevents item from being lost on death.\nNote: Most gravestone mods are stupid and prevent this from working!");
    public static final Component WITHER_ARROW_DESC = descriptionBuilder("wither_bow",
        "Applies withering to the target\nApplies to ranged weapons");
    public static final Component WITHER_WEAPON_DESC = descriptionBuilder("wither_weapon",
        "Applies withering to the target\nApplies to melee weapons");
    public static final Component XP_BOOST_DESC = descriptionBuilder("xp_boost", "Extra XP from mobs and blocks");

    // endregion

    // region builders

    private static <T extends EIOBaseEnchantment> EnchantmentBuilder<T, Registrate> enchantmentBuilder(String name, T enchantment) {
        return REGISTRATE.enchantment(name, enchantment.getCategory(), (r, c, s) -> enchantment);
    }

    private static Component descriptionBuilder(String enchantmentname, String description) {
        return TooltipUtil.style(REGISTRATE.addLang("description", new ResourceLocation(EnderIO.DOMAIN, "enchantment." + enchantmentname), description));
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
            addTooltip(event, enchantments, toolTip, AUTO_SMELT.get(), AUTO_SMELT_DESC);
            addTooltip(event, enchantments, toolTip, REPELLENT.get(), REPELLENT_DESC);
            addTooltip(event, enchantments, toolTip, SHIMMER.get(), SHIMMER_DESC);
            addTooltip(event, enchantments, toolTip, SOULBOUND.get(), SOULBOUND_DESC);
            addTooltip(event, enchantments, toolTip, WITHER_ARROW.get(), WITHER_ARROW_DESC);
            addTooltip(event, enchantments, toolTip, WITHER_WEAPON.get(), WITHER_WEAPON_DESC);
            addTooltip(event, enchantments, toolTip, XP_BOOST.get(), XP_BOOST_DESC);
        }
    }

    public static void register() {
    }
}
