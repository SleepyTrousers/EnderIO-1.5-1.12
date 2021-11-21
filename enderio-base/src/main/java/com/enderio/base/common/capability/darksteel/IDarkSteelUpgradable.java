package com.enderio.base.common.capability.darksteel;

import com.enderio.core.common.capability.INamedNBTSerializable;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.Optional;

public interface IDarkSteelUpgradable extends INamedNBTSerializable<Tag> {

    @Override
    default String getSerializedName() {
        return "DarkSteelUpgradable";
    }

    /**
     * Note that this will blindly apply the upgrade regardless of validity, call
     * {@link #canApplyUpgrade(IDarkSteelUpgrade)} first
     * @param upgrade the upgrade to add
     */
    void addUpgrade(IDarkSteelUpgrade upgrade);

    void removeUpgrade(String name);

    /**
     * Returns the upgrades currently applied to the upgradable
     * @return the upgrades currently applied to the upgradable
     */
    Collection<IDarkSteelUpgrade> getUpgrades();

    /**
     * Performs validity checks to see if the specified upgrade can be applied based on the current state of the upgradable
     * @param upgrade the upgrade to be checked
     * @return true is can be applied
     */
    boolean canApplyUpgrade(IDarkSteelUpgrade upgrade);

    boolean hasUpgrade(String upgradeName);

    Optional<IDarkSteelUpgrade> getUpgrade(String upgradeName);

    default <T extends IDarkSteelUpgrade> Optional<T> getUpgradeAs(String upgradeName, Class<T> as) {
        return getUpgrade(upgradeName).filter(as::isInstance).map(as::cast);
    }

    /**
     * Returns the list of upgrades that will return true from {@link #canApplyUpgrade(IDarkSteelUpgrade)}
     * @return the upgrades that can be applied
     */
    Collection<IDarkSteelUpgrade> getUpgradesThatCanBeAppliedAtTheMoment();

    /**
     * Returns all upgrades that can ever be applied to this upgradable
     * @return res
     */
    Collection<IDarkSteelUpgrade> getAllPossibleUpgrades();
}
