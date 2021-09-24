package com.tterrag.registrate.util.entry;

import com.tterrag.registrate.AbstractRegistrate;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.RegistryObject;

public class BlockEntry<T extends Block> extends ItemProviderEntry<T> {

    public BlockEntry(AbstractRegistrate<?> owner, RegistryObject<T> delegate) {
        super(owner, delegate);
    }

    public BlockState getDefaultState() {
        return get().defaultBlockState();
    }

    public boolean has(BlockState state) {
        return is(state.getBlock());
    }
    
    public static <T extends Block> BlockEntry<T> cast(RegistryEntry<T> entry) {
        return RegistryEntry.cast(BlockEntry.class, entry);
    }
}
