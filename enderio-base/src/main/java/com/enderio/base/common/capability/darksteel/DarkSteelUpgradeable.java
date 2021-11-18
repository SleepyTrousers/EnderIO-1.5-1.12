package com.enderio.base.common.capability.darksteel;

import com.enderio.base.EnderIO;
import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.base.common.item.darksteel.upgrades.EmpoweredUpgrade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.*;

@SuppressWarnings("unused")
public class DarkSteelUpgradeable implements IDarkSteelUpgradable {

    // region Utils

    public static ItemStack addUpgrade(ItemStack itemStack, IDarkSteelUpgrade upgrade) {
        itemStack.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).ifPresent(upgradable -> upgradable.addUpgrade(upgrade));
        return itemStack;
    }

    public static Collection<IDarkSteelUpgrade> getUpgrades(ItemStack itemStack) {
        return itemStack.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).map(IDarkSteelUpgradable::getUpgrades).orElse(Collections.emptyList());
    }

    public static boolean hasUpgrade(ItemStack itemStack, String name) {
        return itemStack.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).map(upgradable -> upgradable.hasUpgrade(name)).orElse(false);
    }

    public static <T extends IDarkSteelUpgrade> Optional<T> getUpgradeAs(ItemStack itemStack, String upgrade, Class<T> as) {
        Optional<IDarkSteelUpgradable> cap = itemStack.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).resolve();
        return cap.flatMap(upgradeCap -> upgradeCap.getUpgradeAs(upgrade, as));
    }

    public static Collection<IDarkSteelUpgrade> getUpgradesThatCanBeAppliedAtTheMoment(ItemStack itemStack) {
        return itemStack.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).map(IDarkSteelUpgradable::getUpgradesThatCanBeAppliedAtTheMoment).orElse(Collections.emptyList());
    }

    public static Collection<IDarkSteelUpgrade> getAllPossibleUpgrades(ItemStack itemStack) {
        return itemStack.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).map(IDarkSteelUpgradable::getAllPossibleUpgrades).orElse(Collections.emptyList());
    }

    // endregion

    // region Class Impl

    private static final String ON_ITEM_KEY = "onItem";

    private final Map<String, IDarkSteelUpgrade> upgrades = new HashMap<>();

    /** The type of item that is upgradable, used to determine valid upgrades.*/
    private ResourceLocation onItem;

    public DarkSteelUpgradeable() {
        this(EnderIO.loc("empty"));
    }

    public DarkSteelUpgradeable(ResourceLocation onItem) {
        this.onItem = onItem;
    }

    @Override
    public void addUpgrade(IDarkSteelUpgrade upgrade) {
        removeUpgradeInSlot(upgrade.getSlot());
        upgrades.put(upgrade.getSerializedName(), upgrade);
    }

    @Override
    public void removeUpgrade(String name) {
        upgrades.remove(name);
    }

    private void removeUpgradeInSlot(String slot) {
        for (var entry : upgrades.entrySet()) {
            if (entry.getValue().getSlot().equals(slot)) {
                upgrades.remove(entry.getKey());
                break;
            }
        }
    }

    @Override
    public boolean canApplyUpgrade(IDarkSteelUpgrade upgrade) {
        if(upgrades.isEmpty()) {
            return EmpoweredUpgrade.NAME.equals(upgrade.getSerializedName()) && upgrade.isBaseTier();
        }

        Optional<IDarkSteelUpgrade> existing = getUpgrade(upgrade.getSerializedName());
        if(existing.isPresent()) {
            return existing.get().isValidUpgrade(upgrade);
        }
        if(!upgrade.isBaseTier()) {
            return false;
        }
        return DarkSteelUpgradeRegistry.instance().getUpgradesForItem(onItem).contains(upgrade.getSerializedName());
    }

    @Override
    public Optional<IDarkSteelUpgrade> getUpgrade(String upgrade) {
        return Optional.ofNullable(upgrades.get(upgrade));
    }

    @Override
    public Collection<IDarkSteelUpgrade> getUpgrades() {
        return upgrades.values();
    }

    @Override
    public boolean hasUpgrade(String upgrade) {
        return upgrades.containsKey(upgrade);
    }

    @Override
    public Collection<IDarkSteelUpgrade> getUpgradesThatCanBeAppliedAtTheMoment() {
        if(upgrades.isEmpty()) {
            return List.of(EmpoweredUpgrade.TIER_0_FACTORY.get());
        }
        final List<IDarkSteelUpgrade> result = new ArrayList<>();
        upgrades.values().forEach(upgrade -> upgrade.getNextTier().ifPresent(result::add));

        getAllPossibleUpgrades().forEach(upgrade -> {
            if(!hasUpgrade(upgrade.getSerializedName())) {
                result.add(upgrade);
            }
        });
        return result;
    }

    @Override
    public Collection<IDarkSteelUpgrade> getAllPossibleUpgrades() {
        Set<String> upgradeNames = DarkSteelUpgradeRegistry.instance().getUpgradesForItem(onItem);
        final List<IDarkSteelUpgrade> result = new ArrayList<>();
        upgradeNames.forEach(s -> DarkSteelUpgradeRegistry.instance().createUpgrade(s).ifPresent(result::add));
        return result;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (var entry : upgrades.entrySet()) {
            tag.put(entry.getKey(), entry.getValue().serializeNBT());
        }
        tag.putString(ON_ITEM_KEY, onItem.toString());
        return tag;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        upgrades.clear();
        if(tag instanceof CompoundTag nbt) {
            for (String key : nbt.getAllKeys()) {
                DarkSteelUpgradeRegistry.instance().createUpgrade(key).ifPresent(upgrade -> {
                    upgrade.deserializeNBT(Objects.requireNonNull(nbt.get(key)));
                    addUpgrade(upgrade);
                });
            }
            onItem = new ResourceLocation(nbt.getString(ON_ITEM_KEY));
        }
    }

    // endregion
}
