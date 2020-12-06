package gg.galaxygaming.gasconduits.common.conduit;

import com.enderio.core.common.util.NullHelper;
import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.IModTileEntity;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObjectBase;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.init.RegisterModObject;
import gg.galaxygaming.gasconduits.GasConduitsConstants;
import gg.galaxygaming.gasconduits.common.filter.ItemGasFilter;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public enum GasConduitObject implements IModObjectBase {
    itemGasConduit(ItemGasConduit::create),
    itemGasFilter(ItemGasFilter::create);

    public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
        event.register(GasConduitObject.class);
    }

    @Nonnull
    protected final String unlocalisedName;

    @Nullable
    protected final Function<IModObject, Block> blockMaker;

    @Nullable
    protected final BiFunction<IModObject, Block, Item> itemMaker;

    @Nullable
    protected final IModTileEntity modTileEntity;

    GasConduitObject(@Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
        this(null, itemMaker, null);
    }

    GasConduitObject(@Nonnull Function<IModObject, Block> blockMaker) {
        this(blockMaker, null, null);
    }

    GasConduitObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
        this(blockMaker, itemMaker, null);
    }

    GasConduitObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull IModTileEntity modTileEntity) {
        this(blockMaker, null, modTileEntity);
    }

    GasConduitObject(@Nullable Function<IModObject, Block> blockMaker, @Nullable BiFunction<IModObject, Block, Item> itemMaker, @Nullable IModTileEntity modTileEntity) {
        this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
        this.blockMaker = blockMaker;
        this.itemMaker = itemMaker;
        if (blockMaker == null && itemMaker == null) {
            throw new RuntimeException(this + " unexpectedly is neither a Block nor an Item.");
        }
        this.modTileEntity = null;
    }

    @Nonnull
    @Override
    public final String getUnlocalisedName() {
        return unlocalisedName;
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(GasConduitsConstants.MOD_ID, getUnlocalisedName());
    }

    @Override
    @Nullable
    public IModTileEntity getTileEntity() {
        return modTileEntity;
    }

    @Nonnull
    @Override
    public final <B extends Block> B apply(@Nonnull B blockIn) {
        blockIn.setCreativeTab(EnderIOTab.tabEnderIOConduits);
        return IModObjectBase.super.apply(blockIn);
    }

    @Nonnull
    @Override
    public Function<IModObject, Block> getBlockCreator() {
        return blockMaker != null ? blockMaker : mo -> null;
    }

    @Nonnull
    @Override
    public BiFunction<IModObject, Block, Item> getItemCreator() {
        return NullHelper.first(itemMaker, WithBlockItem.itemCreator);
    }
}