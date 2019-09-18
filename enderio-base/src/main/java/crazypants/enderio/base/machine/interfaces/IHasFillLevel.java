package crazypants.enderio.base.machine.interfaces;

import com.enderio.core.common.inventory.EnderInventory.Type;

import crazypants.enderio.base.machine.base.te.AbstractCapabilityMachineEntity;

/**
 * Marker interface to tell the TOP integration that a {@link AbstractCapabilityMachineEntity} has {@link Type#INOUT} slots that can be summed up to give a fill
 * level.
 *
 */
public interface IHasFillLevel {

}
