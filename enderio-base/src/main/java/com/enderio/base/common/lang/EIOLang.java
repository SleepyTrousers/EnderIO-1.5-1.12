package com.enderio.base.common.lang;

import com.enderio.base.EnderIO;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.Component;

public class EIOLang {
    private static Registrate REGISTRATE = EnderIO.registrate();

    public static final Component COORDINATE_SELECTOR_NO_PAPER = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_paper"), "No Paper in Inventory");
    public static final Component COORDINATE_SELECTOR_NO_BLOCK = REGISTRATE.addLang("info", EnderIO.loc("coordinate_selector.no_block"), "No Block in Range");

    public static final Component DS_UPGRADE_ITEM_NO_XP = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.no_xp"), "Not enough XP");
    public static final Component DS_UPGRADE_AVAILABLE = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.available"), "Available Upgrades");
    public static final Component DS_UPGRADE_EMPOWERED = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.empowered"), "Empowered");
    public static final Component DS_UPGRADE_SPOON = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.spoon"), "Spoon");
    public static final Component DS_UPGRADE_FORK = REGISTRATE.addLang("info", EnderIO.loc("darksteel.upgrade.fork"), "Fork");

    public static void register() {}
}
