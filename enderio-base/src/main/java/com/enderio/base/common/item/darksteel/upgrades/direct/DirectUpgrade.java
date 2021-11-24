package com.enderio.base.common.item.darksteel.upgrades.direct;

import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.enderio.base.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.network.chat.Component;

public class DirectUpgrade implements IDarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "direct";

    public static DirectUpgrade create() {
        return new DirectUpgrade();
    }

    @Override
    public String getSerializedName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return EIOLang.DS_UPGRADE_DIRECT;
    }
}
