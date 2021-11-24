package com.enderio.base.common.item.darksteel.upgrades;

import com.enderio.base.EnderIO;
import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.enderio.base.common.item.darksteel.upgrades.direct.DirectUpgrade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Supplier;

public final class DarkSteelUpgradeRegistry {

    public static final String UPGRADE_PREFIX = EnderIO.DOMAIN + ".darksteel.upgrade.";

    private static final DarkSteelUpgradeRegistry INST = new DarkSteelUpgradeRegistry();
    private static final String UPGRADE_IN_STACK_KEY = "dark_steel_upgrade";

    static {
        INST.registerUpgrade(EmpoweredUpgrade::new);
        INST.registerUpgrade(SpoonUpgrade::new);
        INST.registerUpgrade(ForkUpgrade::new);
        INST.registerUpgrade(DirectUpgrade::new);
    }

    public static DarkSteelUpgradeRegistry instance() {return INST; }

    private final Map<String, Supplier<IDarkSteelUpgrade>> registeredUpgrades = new HashMap<>();

    private final Map<ResourceLocation, Set<String>> possibleUpgrades = new HashMap<>();

    private DarkSteelUpgradeRegistry() {}

    // region Upgrade register

    public void registerUpgrade(Supplier<IDarkSteelUpgrade> upgrade) {
        registeredUpgrades.put(upgrade.get().getSerializedName(), upgrade);
    }

    public Optional<IDarkSteelUpgrade> createUpgrade(String name) {
        Supplier<IDarkSteelUpgrade> val = registeredUpgrades.get(name);
        if (val == null) {
            return Optional.empty();
        }
        return Optional.of(val.get());
    }
    // endregion

    // region Read / Write of Upgrades to ItemStacks

    public void writeUpgradeToItemStack(ItemStack stack, IDarkSteelUpgrade upgrade) {
        CompoundTag rootTag = new CompoundTag();
        rootTag.putString("name", upgrade.getSerializedName());
        rootTag.put("data", upgrade.serializeNBT());
        stack.getOrCreateTag().put(UPGRADE_IN_STACK_KEY, rootTag);
    }

    public boolean hasUpgrade(ItemStack stack) {
        if(stack.isEmpty() || !stack.hasTag()) {
            return false;
        }
        return stack.getOrCreateTag().contains(UPGRADE_IN_STACK_KEY);
    }

    public Optional<IDarkSteelUpgrade> readUpgradeFromStack(ItemStack stack) {
        if(stack.isEmpty() || !stack.hasTag()) {
            return Optional.empty();
        }
        Tag upTag = stack.getOrCreateTag().get(UPGRADE_IN_STACK_KEY);
        if(upTag instanceof CompoundTag rootTag) {
            String serName = rootTag.getString("name");
            final Optional<IDarkSteelUpgrade> upgrade = createUpgrade(serName);
            return upgrade.map(up -> {
                up.deserializeNBT(Objects.requireNonNull(rootTag.get("data")));
                return upgrade;
            }).orElse(Optional.empty());
        }
        return Optional.empty();
    }

    // endregion

    // region Upgrade Sets (the set of upgrades that can be applied to an upgradable item

    public void addUpgradesForItem(ResourceLocation forItem, String... upgrades) {
        Set<String> currentValues = possibleUpgrades.getOrDefault(forItem, new HashSet<>());
        Collections.addAll(currentValues, upgrades);
        possibleUpgrades.put(forItem, currentValues);
    }

    public Set<String> getUpgradesForItem(ResourceLocation forItem) {
        return Collections.unmodifiableSet(possibleUpgrades.getOrDefault(forItem, Collections.emptySet()));
    }

    // endregion
}
