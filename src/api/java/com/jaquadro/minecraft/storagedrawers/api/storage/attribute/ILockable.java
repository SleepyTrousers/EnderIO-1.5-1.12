package com.jaquadro.minecraft.storagedrawers.api.storage.attribute;

public interface ILockable
{
    /**
     * Gets whether or not a drawer or group is locked for the given lock attribute.
     */
    boolean isLocked (LockAttribute attr);

    /**
     * Gets whether or not the lock state can be changed for the given lock attribute.
     * If this method returns false, isLocked may still return true.
     */
    boolean canLock (LockAttribute attr);

    /**
     * Sets the lock state of a drawer or group for the given lock attribute.
     * If canLock returns false, this is a no-op.
     */
    void setLocked (LockAttribute attr, boolean isLocked);
}
