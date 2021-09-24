package com.tterrag.registrate.util.entry;

import java.util.Optional;

import javax.annotation.Nullable;

import com.tterrag.registrate.AbstractRegistrate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.RegistryObject;

public class TileEntityEntry<T extends BlockEntity> extends RegistryEntry<BlockEntityType<T>> {

    public TileEntityEntry(AbstractRegistrate<?> owner, RegistryObject<BlockEntityType<T>> delegate) {
        super(owner, delegate);
    }

    /**
     * Create a "default" instance of this {@link BlockEntity} via the {@link BlockEntityType}.
     * 
     * @return The instance
     */
    public T create(BlockPos pos, BlockState state) {
        return get().create(pos, state);
    }

    /**
     * Check that the given {@link BlockEntity} is an instance of this type.
     * 
     * @param t
     *            The {@link BlockEntity} instance
     * @return {@code true} if the type matches, {@code false} otherwise.
     */
    public boolean is(@Nullable BlockEntity t) {
        return t != null && t.getType() == get();
    }

    /**
     * Get an instance of this {@link BlockEntity} from the world.
     * 
     * @param world
     *            The world to look for the instance in
     * @param pos
     *            The position of the instance
     * @return An {@link Optional} containing the instance, if it exists and matches this type. Otherwise, {@link Optional#empty()}.
     */
    public Optional<T> get(BlockGetter world, BlockPos pos) {
        return Optional.ofNullable(getNullable(world, pos));
    }

    /**
     * Get an instance of this {@link BlockEntity} from the world.
     * 
     * @param world
     *            The world to look for the instance in
     * @param pos
     *            The position of the instance
     * @return The instance, if it exists and matches this type. Otherwise, {@code null}.
     */
    @SuppressWarnings("unchecked")
    public @Nullable T getNullable(BlockGetter world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        return is(te) ? (T) te : null;
    }

    public static <T extends BlockEntity> TileEntityEntry<T> cast(RegistryEntry<BlockEntityType<T>> entry) {
        return RegistryEntry.cast(TileEntityEntry.class, entry);
    }
}
