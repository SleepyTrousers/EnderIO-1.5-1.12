package com.enderio.base.common.capability.capacitors;

import com.enderio.core.common.capability.INamedNBTSerializable;
import net.minecraft.nbt.Tag;

import java.util.Map;

/**
 * interface for storing capacitor data
 */
public interface ICapacitorData extends INamedNBTSerializable<Tag> {
    @Override
    default String getSerializedName() {
        return "CapacitorData";
    }

    /**
     * Static strings for specialization types.
     */
    String ALL_ENERGY_CONSUMPSTION = "all_energy_consuption";
    String ALL_PRODUCTION_SPEED = "all_production_speed";

    String ALLOY_ENERGY_CONSUMPSTION = "alloy_energy_consuption";
    String ALLOY_PRODUCTION_SPEED = "alloy_production_speed";

    /**
     * Gets Base value used for non specialization.
     */
    void setBase(float base);

    /**
     * Sets Base value used for non specialization.
     */
    float getBase();

    /**
     * Add a specialization. It contains a String for the type and a float for the value.
     */
    void addSpecialization(String type, float modifier);

    /**
     * Clears old and adds a new specialization;
     */
    void addNewSpecialization(String type, float modifier);

    /**
     * Adds all specializations to the capacitor.
     */
    void addAllSpecialization(Map<String, Float> specializations);

    /**
     * Gets all specializations.
     */
    Map<String, Float> getSpecializations();

    /**
     * Flavor text used by loot capacitor.
     */
    int getFlavor();

}
