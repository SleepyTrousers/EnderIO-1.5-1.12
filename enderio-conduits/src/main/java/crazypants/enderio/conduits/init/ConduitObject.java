package crazypants.enderio.conduits.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.IModTileEntity;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.registry.Registry;
import crazypants.enderio.conduits.EnderIOConduits;
import crazypants.enderio.conduits.conduit.BlockConduitBundle;
import crazypants.enderio.conduits.conduit.item.ItemItemConduit;
import crazypants.enderio.conduits.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduits.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduits.conduit.redstone.ItemRedstoneConduit;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODID)
public enum ConduitObject implements IModObject.Registerable {

  // Conduits
  block_conduit_bundle(BlockConduitBundle.class, ConduitTileEntity.TileConduitBundle),

  item_item_conduit(ItemItemConduit.class),
  item_liquid_conduit(ItemLiquidConduit.class),
  item_power_conduit(ItemPowerConduit.class),
  item_redstone_conduit(ItemRedstoneConduit.class),;

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void registerBlocksEarly(@Nonnull RegistryEvent.Register<Block> event) {
    Registry.registerConduitBlock(block_conduit_bundle);
    ModObjectRegistry.addModObjects(ConduitObject.class);
  }

  final @Nonnull String unlocalisedName;

  protected @Nullable Block block;
  protected @Nullable Item item;

  protected final @Nonnull Class<?> clazz;
  protected final @Nullable String blockMethodName, itemMethodName;
  protected final @Nullable IModTileEntity modTileEntity;

  private ConduitObject(@Nonnull Class<?> clazz) {
    this(clazz, (IModTileEntity) null);
  }

  private ConduitObject(@Nonnull Class<?> clazz, @Nullable IModTileEntity modTileEntity) {
    this(clazz, "create", modTileEntity);
  }

  private ConduitObject(@Nonnull Class<?> clazz, @Nonnull String methodName) {
    this(clazz, Block.class.isAssignableFrom(clazz) ? methodName : null, Item.class.isAssignableFrom(clazz) ? methodName : null, null);
  }

  private ConduitObject(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nullable IModTileEntity modTileEntity) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.clazz = clazz;
    if (Block.class.isAssignableFrom(clazz)) {
      this.blockMethodName = methodName;
      this.itemMethodName = null;
    } else if (Item.class.isAssignableFrom(clazz)) {
      this.blockMethodName = null;
      this.itemMethodName = methodName;
    } else {
      throw new RuntimeException("Clazz " + clazz + " unexpectedly is neither a Block nor an Item.");
    }
    this.modTileEntity = modTileEntity;
  }

  private ConduitObject(@Nonnull Class<?> clazz, @Nullable String blockMethodName, @Nullable String itemMethodName, @Nullable IModTileEntity modTileEntity) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.clazz = clazz;
    this.blockMethodName = blockMethodName == null || blockMethodName.isEmpty() ? null : blockMethodName;
    this.itemMethodName = itemMethodName == null || itemMethodName.isEmpty() ? null : itemMethodName;
    this.modTileEntity = modTileEntity;
  }

  @Override
  public @Nonnull Class<?> getClazz() {
    return clazz;
  }

  @Override
  public void setItem(@Nullable Item obj) {
    this.item = obj;
  }

  @Override
  public void setBlock(@Nullable Block obj) {
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
    return new ResourceLocation(EnderIO.DOMAIN, getUnlocalisedName());
  }

  @Nullable
  @Override
  public Block getBlock() {
    return block;
  }

  @Nullable
  @Override
  public Item getItem() {
    return item;
  }

  @Override
  @Nullable
  public IModTileEntity getTileEntity() {
    return modTileEntity;
  }

  @Override
  public final @Nonnull <B extends Block> B apply(@Nonnull B blockIn) {
    blockIn.setUnlocalizedName(getUnlocalisedName());
    blockIn.setRegistryName(getRegistryName());
    return blockIn;
  }

  @Override
  public final @Nonnull <I extends Item> I apply(@Nonnull I itemIn) {
    itemIn.setUnlocalizedName(getUnlocalisedName());
    itemIn.setRegistryName(getRegistryName());
    return itemIn;
  }

  @Override
  @Nullable
  public String getBlockMethodName() {
    return blockMethodName;
  }

  @Override
  @Nullable
  public String getItemMethodName() {
    return itemMethodName;
  }

}
