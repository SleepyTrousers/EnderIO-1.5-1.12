package com.enderio.base.common.capability.darksteel;

import com.enderio.core.common.capability.INamedNBTSerializable;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public interface IDarkSteelUpgrade extends INamedNBTSerializable<Tag> {

    Component getDisplayName();

    /**
     * Only one upgrade can be added for a slot. For example, if you had a jetpack and glider upgrade, both upgrades returning the same
     * slot would prevent them from both being applied to the same item.
     * @return the slot
     */
    default String getSlot() {
        return getSerializedName();
    }

    /**
     * For non-tiered upgrades, always true. For tiered upgrades return true for tier 0.
     * @return true if base tier
     */
    default boolean isBaseTier() {
        return true;
    }

    /**
     * If this upgrade has multiple tiers (eg, level 1, 2, 3 etc) return the next tier.
     * @return the next tier
     */
    default Optional<? extends IDarkSteelUpgrade> getNextTier() {
        return Optional.empty();
    }

    /**
     * If an upgrade has multiple tiers, checks that the current upgrade can be replaced by the supplied upgrade
     * @param upgrade the upgrade to be checked
     * @return res
     */
    default boolean isValidUpgrade(IDarkSteelUpgrade upgrade) {
        return false;
    }

    @Override
    default Tag serializeNBT() {
        return StringTag.valueOf(getSerializedName());
    }

    @Override
    default void deserializeNBT(Tag nbt) {
    }

}
