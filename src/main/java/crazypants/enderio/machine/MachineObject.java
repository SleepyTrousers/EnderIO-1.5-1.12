package crazypants.enderio.machine;

import com.enderio.core.common.util.NullHelper;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.init.ModObjectRegistry;
import crazypants.enderio.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machine.capbank.BlockCapBank;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum MachineObject implements IModObject.Registerable {

    blockAlloySmelter(BlockAlloySmelter.class),
    blockCapBank(BlockCapBank.class);

    static {
        ModObjectRegistry.INSTANCE.addModObjects(MachineObject.class);
    }


    final @Nonnull String unlocalisedName;

    private @Nullable Block block;
    private @Nullable Item item;

    private final @Nullable Class<?> clazz;
    private final @Nonnull String methodName;
    private final @Nullable Class<? extends TileEntity> teClazz;

    private MachineObject(@Nullable Class<?> clazz) {
        this(clazz, "create", null);
    }

    private MachineObject(@Nullable Class<?> clazz, Class<? extends TileEntity> teClazz) {
        this(clazz, "create", teClazz);
    }

    private MachineObject(@Nullable Class<?> clazz, @Nonnull String methodName, Class<? extends TileEntity> teClazz) {
        this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
        this.clazz = clazz;
        this.methodName = methodName;
        this.teClazz = teClazz;
    }

    @Override
    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public void setItem(Item obj) {
        this.item = obj;
    }

    @Override
    public void setBlock(Block obj) {
        this.block = obj;
    }

    @Nonnull
    @Override
    public String getUnlocalisedName() {
        return unlocalisedName;
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(EnderIOMachines.MOD_ID, getUnlocalisedName());
    }

    @Override
    public @Nonnull <B extends Block> B apply(@Nonnull B blockIn) {
        blockIn.setUnlocalizedName(getUnlocalisedName());
        blockIn.setRegistryName(getRegistryName());
        return blockIn;
    }

    @Override
    public @Nonnull <I extends Item> I apply(@Nonnull I itemIn) {
        itemIn.setUnlocalizedName(getUnlocalisedName());
        itemIn.setRegistryName(getRegistryName());
        return itemIn;
    }

    @Nullable
    @Override
    public Block getBlock() {
        return null;
    }

    @Nullable
    @Override
    public Item getItem() {
        return null;
    }

    @Nullable
    @Override
    public Class<? extends TileEntity> getTileClass() {
        return null;
    }

    @Override
    public void preInit(@Nonnull FMLPreInitializationEvent event) {

    }

    @Override
    public void init(@Nonnull FMLInitializationEvent event) {

    }

}
