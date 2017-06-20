package crazypants.enderio.machine.capbank;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.machine.capbank.InfoDisplayType;
import info.loenwind.autosave.Registry;
import com.enderio.core.common.NBTAction;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class HandleDisplayMode implements IHandler<Map<EnumFacing, InfoDisplayType>> {

    public HandleDisplayMode() {
    }

    @Override
    public boolean canHandle(Class<?> clazz) {
        // This handler is named specifically in the @Store annotation
        return false;
    }

    @Override
    public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
                         @Nonnull Map<EnumFacing, InfoDisplayType> object) throws IllegalArgumentException, IllegalAccessException, InstantiationException,
            NoHandlerFoundException {
        long value = 0;
        for (EnumFacing face : EnumFacing.values()) {
            long subvalue = 0xFF;
            if (object.containsKey(face)) {
                subvalue = object.get(face).ordinal();
            }
            value = value | (subvalue << (face.ordinal() * 8));
        }
        nbt.setLong(name, value);
        return true;
    }

    @Override
    public Map<EnumFacing, InfoDisplayType> read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field,
                                                 @Nonnull String name, @Nullable Map<EnumFacing, InfoDisplayType> object)
            throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
        if (nbt.hasKey(name)) {
            if (object == null) {
                object = new EnumMap<EnumFacing, InfoDisplayType>(EnumFacing.class);
            }
            long value = nbt.getLong(name);
            for (EnumFacing face : EnumFacing.values()) {
                long subvalue = (value >>> (face.ordinal() * 8)) & 0xFF;
                if (subvalue > 0 && subvalue < InfoDisplayType.values().length) {
                    object.put(face, InfoDisplayType.values()[(int) subvalue]);
                } else {
                    object.remove(face);
                }
            }
        }
        return object;
    }

}