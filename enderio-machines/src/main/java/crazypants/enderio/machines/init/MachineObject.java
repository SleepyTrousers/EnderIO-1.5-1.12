package crazypants.enderio.machines.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModTileEntity;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObjectBase;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.init.RegisterModObject;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machines.machine.buffer.BlockBuffer;
import crazypants.enderio.machines.machine.crafter.BlockCrafter;
import crazypants.enderio.machines.machine.enchanter.BlockEnchanter;
import crazypants.enderio.machines.machine.farm.BlockFarmStation;
import crazypants.enderio.machines.machine.generator.combustion.BlockCombustionGenerator;
import crazypants.enderio.machines.machine.generator.stirling.BlockStirlingGenerator;
import crazypants.enderio.machines.machine.generator.zombie.BlockZombieGenerator;
import crazypants.enderio.machines.machine.ihopper.BlockImpulseHopper;
import crazypants.enderio.machines.machine.killera.BlockKillerJoe;
import crazypants.enderio.machines.machine.light.BlockElectricLight;
import crazypants.enderio.machines.machine.light.BlockLightNode;
import crazypants.enderio.machines.machine.obelisk.attractor.BlockAttractor;
import crazypants.enderio.machines.machine.obelisk.aversion.BlockAversionObelisk;
import crazypants.enderio.machines.machine.obelisk.inhibitor.BlockInhibitorObelisk;
import crazypants.enderio.machines.machine.obelisk.relocator.BlockRelocatorObelisk;
import crazypants.enderio.machines.machine.obelisk.weather.BlockWeatherObelisk;
import crazypants.enderio.machines.machine.obelisk.xp.BlockExperienceObelisk;
import crazypants.enderio.machines.machine.painter.BlockPainter;
import crazypants.enderio.machines.machine.reservoir.BlockReservoir;
import crazypants.enderio.machines.machine.sagmill.BlockSagMill;
import crazypants.enderio.machines.machine.slicensplice.BlockSliceAndSplice;
import crazypants.enderio.machines.machine.solar.BlockSolarPanel;
import crazypants.enderio.machines.machine.soul.BlockSoulBinder;
import crazypants.enderio.machines.machine.spawner.BlockPoweredSpawner;
import crazypants.enderio.machines.machine.spawner.creative.BlockCreativeSpawner;
import crazypants.enderio.machines.machine.tank.BlockTank;
import crazypants.enderio.machines.machine.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.machines.machine.teleport.telepad.BlockDialingDevice;
import crazypants.enderio.machines.machine.teleport.telepad.BlockTelePad;
import crazypants.enderio.machines.machine.transceiver.BlockTransceiver;
import crazypants.enderio.machines.machine.vacuum.chest.BlockVacuumChest;
import crazypants.enderio.machines.machine.vacuum.xp.BlockXPVacuum;
import crazypants.enderio.machines.machine.vat.BlockVat;
import crazypants.enderio.machines.machine.wired.BlockWiredCharger;
import crazypants.enderio.machines.machine.wireless.BlockAntenna;
import crazypants.enderio.machines.machine.wireless.BlockEnhancedWirelessCharger;
import crazypants.enderio.machines.machine.wireless.BlockNormalWirelessCharger;
import crazypants.enderio.machines.machine.wireless.BlockWirelessCharger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public enum MachineObject implements IModObjectBase {

  block_simple_furnace(BlockAlloySmelter.class, "create_furnace", MachineTileEntity.TileAlloySmelterFurnace),
  block_simple_alloy_smelter(BlockAlloySmelter.class, "create_simple", MachineTileEntity.TileAlloySmelterSimple),
  block_alloy_smelter(BlockAlloySmelter.class, MachineTileEntity.TileAlloySmelter),
  block_enhanced_alloy_smelter(BlockAlloySmelter.class, "create_enhanced", MachineTileEntity.TileAlloySmelterEnhanced),
  block_enhanced_alloy_smelter_top(BlockAlloySmelter.class, "create_extension"),
  block_buffer(BlockBuffer.class, MachineTileEntity.TileBufferAbstract),
  block_enchanter(BlockEnchanter.class, MachineTileEntity.TileEnchanter),
  block_farm_station(BlockFarmStation.class, MachineTileEntity.TileFarmStation),
  block_combustion_generator(BlockCombustionGenerator.class, MachineTileEntity.TileCombustionGenerator),
  block_enhanced_combustion_generator(BlockCombustionGenerator.class, "create_enhanced", MachineTileEntity.TileCombustionGeneratorEnhanced),
  block_enhanced_combustion_generator_top(BlockCombustionGenerator.class, "create_extension"),
  block_simple_stirling_generator(BlockStirlingGenerator.class, "create_simple", MachineTileEntity.TileStirlingGeneratorSimple),
  block_stirling_generator(BlockStirlingGenerator.class, MachineTileEntity.TileStirlingGenerator),
  block_zombie_generator(BlockZombieGenerator.class, MachineTileEntity.TileZombieGenerator),
  block_franken_zombie_generator(BlockZombieGenerator.class, "create_franken", MachineTileEntity.TileFrankenZombieGenerator),
  block_ender_generator(BlockZombieGenerator.class, "create_ender", MachineTileEntity.TileEnderGenerator),

  block_killer_joe(BlockKillerJoe.class, MachineTileEntity.TileKillerJoe),
  block_electric_light(BlockElectricLight.class, MachineTileEntity.TileElectricLight),
  block_light_node(BlockLightNode.class, MachineTileEntity.TileLightNode),

  // Obelisks
  block_attractor_obelisk(BlockAttractor.class, MachineTileEntity.TileAttractor),
  block_aversion_obelisk(BlockAversionObelisk.class, MachineTileEntity.TileAversionObelisk),
  block_inhibitor_obelisk(BlockInhibitorObelisk.class, MachineTileEntity.TileInhibitorObelisk),
  block_relocator_obelisk(BlockRelocatorObelisk.class, MachineTileEntity.TileRelocatorObelisk),
  block_weather_obelisk(BlockWeatherObelisk.class, MachineTileEntity.TileWeatherObelisk),
  block_experience_obelisk(BlockExperienceObelisk.class, MachineTileEntity.TileExperienceObelisk),

  block_painter(BlockPainter.class, MachineTileEntity.TileEntityPainter),
  block_reservoir(BlockReservoir.class, MachineTileEntity.TileReservoir),
  block_omni_reservoir(BlockReservoir.class, "create_omni", MachineTileEntity.TileOmniReservoir),
  block_simple_sag_mill(BlockSagMill.class, "create_simple", MachineTileEntity.TileSagMillSimple),
  block_sag_mill(BlockSagMill.class, MachineTileEntity.TileSagMill),
  block_enhanced_sag_mill(BlockSagMill.class, "create_enhanced", MachineTileEntity.TileSagMillEnhanced),
  block_enhanced_sag_mill_top(BlockSagMill.class, "create_extension"),
  block_slice_and_splice(BlockSliceAndSplice.class, MachineTileEntity.TileSliceAndSplice),
  block_solar_panel(BlockSolarPanel.class, MachineTileEntity.TileSolarPanel),
  block_soul_binder(BlockSoulBinder.class, MachineTileEntity.TileSoulBinder),
  block_powered_spawner(BlockPoweredSpawner.class, MachineTileEntity.TilePoweredSpawner),
  block_vat(BlockVat.class, MachineTileEntity.TileVat),
  block_enhanced_vat(BlockVat.class, "create_enhanced", MachineTileEntity.TileVatEnhanced),
  block_enhanced_vat_top(BlockVat.class, "create_extension"),
  block_wired_charger(BlockWiredCharger.class, MachineTileEntity.TileWiredCharger),
  block_enhanced_wired_charger(BlockWiredCharger.class, "create_enhanced", MachineTileEntity.TileWiredChargerEnhanced),
  block_simple_wired_charger(BlockWiredCharger.class, "create_simple", MachineTileEntity.TileWiredChargerSimple),
  block_enhanced_wired_charger_top(BlockWiredCharger.class, "create_extension", MachineTileEntity.TileWiredChargerEnhanced),
  block_wireless_charger(BlockWirelessCharger.class, MachineTileEntity.TileWirelessCharger),
  block_normal_wireless_charger(BlockNormalWirelessCharger.class, MachineTileEntity.TileWirelessCharger),
  block_enhanced_wireless_charger(BlockEnhancedWirelessCharger.class, MachineTileEntity.TileWirelessCharger),
  block_wireless_charger_extension(BlockAntenna.class),
  block_tank(BlockTank.class, MachineTileEntity.TileTank),
  block_transceiver(BlockTransceiver.class, MachineTileEntity.TileTransceiver),
  block_vacuum_chest(BlockVacuumChest.class, MachineTileEntity.TileVacuumChest),
  block_xp_vacuum(BlockXPVacuum.class, MachineTileEntity.TileXPVacuum),

  block_travel_anchor(BlockTravelAnchor.class, MachineTileEntity.TileTravelAnchor),
  block_tele_pad(BlockTelePad.class, "create_telepad", MachineTileEntity.TileTelePad),
  block_dialing_device(BlockDialingDevice.class, MachineTileEntity.TileDialingDevice),

  block_impulse_hopper(BlockImpulseHopper.class, MachineTileEntity.TileImpulseHopper),
  block_crafter(BlockCrafter.class, MachineTileEntity.TileCrafter),
  block_simple_crafter(BlockCrafter.class, "create_simple", MachineTileEntity.TileSimpleCrafter),

  block_creative_spawner(BlockCreativeSpawner.class, MachineTileEntity.TileCreativeSpawner),

  ;

  @SubscribeEvent
  public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
    event.register(MachineObject.class);
  }

  final @Nonnull String unlocalisedName;

  protected @Nullable Block block;
  protected @Nullable Item item;

  protected final @Nonnull Class<?> clazz;
  protected final @Nullable String blockMethodName, itemMethodName;
  protected final @Nullable IModTileEntity modTileEntity;

  private MachineObject(@Nonnull Class<?> clazz) {
    this(clazz, "create", null);
  }

  private MachineObject(@Nonnull Class<?> clazz, @Nullable IModTileEntity modTileEntity) {
    this(clazz, "create", modTileEntity);
  }

  private MachineObject(@Nonnull Class<?> clazz, @Nonnull String methodName) {
    this(clazz, Block.class.isAssignableFrom(clazz) ? methodName : null, Item.class.isAssignableFrom(clazz) ? methodName : null, null);
  }

  private MachineObject(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nullable IModTileEntity modTileEntity) {
    this(clazz, Block.class.isAssignableFrom(clazz) ? methodName : null, Item.class.isAssignableFrom(clazz) ? methodName : null, modTileEntity);
  }

  private MachineObject(@Nonnull Class<?> clazz, @Nullable String blockMethodName, @Nullable String itemMethodName, @Nullable IModTileEntity modTileEntity) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.clazz = clazz;
    this.blockMethodName = blockMethodName == null || blockMethodName.isEmpty() ? null : blockMethodName;
    this.itemMethodName = itemMethodName == null || itemMethodName.isEmpty() ? null : itemMethodName;
    if (blockMethodName == null && itemMethodName == null) {
      throw new RuntimeException("Clazz " + clazz + " unexpectedly is neither a Block nor an Item.");
    }
    this.modTileEntity = modTileEntity;
  }

  @Override
  @Nonnull
  public <B extends Block> B apply(@Nonnull B block) {
    block.setCreativeTab(EnderIOTab.tabEnderIOMachines);
    return IModObjectBase.super.apply(block);
  }

  @Override
  public final @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  @Override
  public final @Nullable Block getBlock() {
    return block;
  }

  @Override
  public final @Nullable Item getItem() {
    return item;
  }

  @Override
  public final @Nonnull Class<?> getClazz() {
    return clazz;
  }

  @Override
  public final String getBlockMethodName() {
    return blockMethodName;
  }

  @Override
  public final String getItemMethodName() {
    return itemMethodName;
  }

  @Override
  public final void setItem(@Nullable Item obj) {
    item = obj;
  }

  @Override
  public final void setBlock(@Nullable Block obj) {
    block = obj;
  }

  @Override
  @Nullable
  public IModTileEntity getTileEntity() {
    return modTileEntity;
  }

}
