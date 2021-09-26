package com.enderio.base;

import com.enderio.base.common.enchantments.AutoSmeltEnchantment;
import com.enderio.base.common.enchantments.EIOBaseEnchantment;
import com.enderio.base.common.enchantments.RepellentEnchantment;
import com.enderio.base.common.enchantments.ShimmerEnchantment;
import com.enderio.base.common.enchantments.SoulBoundEnchantment;
import com.enderio.base.common.enchantments.WitherArrowEnchantment;
import com.enderio.base.common.enchantments.WitherWeaponEnchantment;
import com.enderio.base.common.enchantments.XPBoostEnchantment;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.EnchantmentBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class EIOEnchantments {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    // region enchantments

    public static final RegistryEntry<AutoSmeltEnchantment> AUTO_SMELT = enchantmentBuilder("auto_smelt", new AutoSmeltEnchantment())
        .lang("Auto Smelt")
        .register();

    public static final RegistryEntry<RepellentEnchantment> REPELLENT = enchantmentBuilder("repellent", new RepellentEnchantment())
        .lang("Repellent")
        .register();

    public static final RegistryEntry<ShimmerEnchantment> SHIMMER = enchantmentBuilder("shimmer", new ShimmerEnchantment())
        .lang("Shimmer")
        .register();

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

    public static final RegistryEntry<XPBoostEnchantment> XP_BOOST = enchantmentBuilder("xp_boost", new XPBoostEnchantment())
        .lang("XP Boost")
        .register();

    // endregion

    // region description

    public static final TranslatableComponent AUTO_SMELT_DESC = descriptionBuilder("auto_smelt", "Automatically smeltes whatever is mined");
    public static final TranslatableComponent REPELLENT_DESC = descriptionBuilder("repellent",
        "Chance to teleport attackers away\\nHigher levels teleport more often and farther");
    public static final TranslatableComponent SHIMMER_DESC = descriptionBuilder("shimmer",
        "Makes the item shimmer as if it was enchanted.\\nThat's all.\\nReally.\\nNothing more.\\nYes, it is useless.\\nI know.");
    public static final TranslatableComponent SOULBOUND_DESC = descriptionBuilder("soulbound",
        "Prevents item from being lost on death.\\nNote: Most gravestone mods are stupid and prevent this from working!");
    public static final TranslatableComponent WITHER_ARROW_DESC = descriptionBuilder("wither_bow",
        "Applies withering to the target\\nApplies to ranged weapons");
    public static final TranslatableComponent WITHER_WEAPON_DESC = descriptionBuilder("wither_weapon",
        "Applies withering to the target\\nApplies to melee weapons");
    public static final TranslatableComponent XP_BOOST_DESC = descriptionBuilder("xp_boost", "Extra XP from mobs and blocks");

    // endregion

    // region builders

    private static <T extends EIOBaseEnchantment> EnchantmentBuilder<T, Registrate> enchantmentBuilder(String name, T enchantment) {
        return REGISTRATE.enchantment(name, enchantment.getCategory(), (r, c, s) -> enchantment);
    }

    private static TranslatableComponent descriptionBuilder(String enchantmentname, String description) {
        return REGISTRATE.addLang("description.enchantment", new ResourceLocation(EnderIO.DOMAIN, enchantmentname), description);
    }

    // endregion

    public static void register() {
    }
}
