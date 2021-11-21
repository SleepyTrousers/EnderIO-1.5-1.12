package com.enderio.base.common.item.darksteel.upgrades;

import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.EnergyStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class EmpoweredUpgrade implements IDarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "empowered";

    private static final Map<Integer,Supplier<EmpoweredUpgrade>> UPGRADES = new HashMap<>();

    //TODO: Config All the things
    public static final Supplier<EmpoweredUpgrade> TIER_0_FACTORY = () -> new EmpoweredUpgrade(0,100000, 0.5f);
    public static final Supplier<EmpoweredUpgrade> TIER_1_FACTORY = () -> new EmpoweredUpgrade(1,150000, 0.6f);
    public static final Supplier<EmpoweredUpgrade> TIER_2_FACTORY = () -> new EmpoweredUpgrade(2,250000, 0.7f);
    public static final Supplier<EmpoweredUpgrade> TIER_3_FACTORY = () -> new EmpoweredUpgrade(3,1000000,0.85f);

    static {
        UPGRADES.put(0, TIER_0_FACTORY);
        UPGRADES.put(1, TIER_1_FACTORY);
        UPGRADES.put(2, TIER_2_FACTORY);
        UPGRADES.put(3, TIER_3_FACTORY);
    }

    private static Optional<EmpoweredUpgrade> getUpgradeForTier(int tier) {
        if(!UPGRADES.containsKey(tier)) {
            return Optional.empty();
        }
        return Optional.of(UPGRADES.get(tier).get());
    }

    private static final Random RANDOM = new Random();

    //TODO: Config
    private int speedBoostWhenPowered = 2;

    //TODO: Config
    private int powerUsePerDamagePoint = 750;

    private int level;

    private int maxStorage;

    private float damageAbsorptionChance;

    private EnergyStorage storage;


    public EmpoweredUpgrade() {
        this(1,100000,0.5f);
    }

    public EmpoweredUpgrade(int level, int  maxStorage, float damageAbsorptionChance) {
        this.level = level;
        this.maxStorage = maxStorage;
        this.damageAbsorptionChance = damageAbsorptionChance;
        storage = new EnergyStorage(maxStorage);
    }

    public float adjustDestroySpeed(float speed, BlockState pState) {
        if (storage.getEnergyStored() > 0) {
            speed += speedBoostWhenPowered;
        }
        return speed;
    }

    public int adjustDamage(int oldDamage, int newDamage) {
        int damageTaken = newDamage - oldDamage;
        if (damageTaken > 0 && storage.getEnergyStored() > 0 && RANDOM.nextDouble() < damageAbsorptionChance) {
            storage.extractEnergy(damageTaken * powerUsePerDamagePoint, false);
            return oldDamage;
        }
        return newDamage;
    }

    @Override
    public boolean isBaseTier() {
        return level == 0;
    }

    @Override
    public Optional<? extends IDarkSteelUpgrade> getNextTier() {
        return getUpgradeForTier(level + 1);
    }

    @Override
    public boolean isValidUpgrade(IDarkSteelUpgrade upgrade) {
        if(upgrade instanceof EmpoweredUpgrade eu) {
            return eu.level == level + 1;
        }
        return false;
    }

    public EnergyStorage getStorage() {
        return storage;
    }

    @Override
    public String getSerializedName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return EIOLang.DS_UPGRADE_EMPOWERED.copy().append(" " + (level + 1));
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("level", level);
        nbt.put("storage", storage.serializeNBT());
        nbt.putInt("maxStorage", maxStorage);
        nbt.putFloat("damageAbsorptionChance", damageAbsorptionChance);
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if(tag instanceof CompoundTag nbt) {
            level = nbt.getInt("level");
            maxStorage = nbt.getInt("maxStorage");
            storage = new EnergyStorage(maxStorage);
            storage.deserializeNBT(nbt.get("storage"));
            damageAbsorptionChance = nbt.getFloat("damageAbsorptionChance");
        }
    }
}
