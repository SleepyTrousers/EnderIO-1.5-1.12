package crazypants.enderio.conduit.init;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.registry.Registry;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.EnderIOConduits;
import crazypants.enderio.conduit.item.ItemExtractSpeedUpgrade;
import crazypants.enderio.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.conduit.item.ItemItemConduit;
import crazypants.enderio.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduit.redstone.ItemRedstoneConduit;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@EventBusSubscriber(modid = EnderIOConduits.MODID)
public enum ConduitObject implements IModObject.Registerable {

  // Conduits
  block_conduit_bundle(BlockConduitBundle.class),

  item_item_conduit(ItemItemConduit.class),
  item_liquid_conduit(ItemLiquidConduit.class),
  item_power_conduit(ItemPowerConduit.class),
  item_redstone_conduit(ItemRedstoneConduit.class),

  item_extract_speed_upgrade(ItemExtractSpeedUpgrade.class),
  item_function_upgrade(ItemFunctionUpgrade.class),

  ;

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
  protected final @Nullable List<Class<? extends TileEntity>> teClazzes;

  private ConduitObject(@Nonnull Class<?> clazz, Class<? extends TileEntity>... teClazz) {
    this(clazz, "create", teClazz);
  }

  @SafeVarargs
  private ConduitObject(@Nonnull Class<?> clazz, @Nonnull String methodName, Class<? extends TileEntity>... teClazz) {
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
    this.teClazzes = teClazz.length > 0 ? new NNList<>(teClazz) : null;
  }

  @SafeVarargs
  private ConduitObject(@Nonnull Class<?> clazz, @Nullable String blockMethodName, @Nullable String itemMethodName, Class<? extends TileEntity>... teClazz) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.clazz = clazz;
    this.blockMethodName = blockMethodName == null || blockMethodName.isEmpty() ? null : blockMethodName;
    this.itemMethodName = itemMethodName == null || itemMethodName.isEmpty() ? null : itemMethodName;
    this.teClazzes = teClazz.length > 0 ? new NNList<>(teClazz) : null;
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

  @Nullable
  @Override
  public final List<Class<? extends TileEntity>> getTileClass() {
    return teClazzes;
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
