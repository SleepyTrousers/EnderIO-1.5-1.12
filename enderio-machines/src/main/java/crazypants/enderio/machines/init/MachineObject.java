package crazypants.enderio.machines.init;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
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
import crazypants.enderio.machines.machine.generator.lava.BlockLavaGenerator;
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
import crazypants.enderio.machines.machine.reservoir.BlockReservoirBase;
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

  block_simple_furnace(BlockAlloySmelter::create_furnace, MachineTileEntity.TileAlloySmelterFurnace),
  block_simple_alloy_smelter(BlockAlloySmelter::create_simple, MachineTileEntity.TileAlloySmelterSimple),
  block_alloy_smelter(BlockAlloySmelter::create, MachineTileEntity.TileAlloySmelter),
  block_enhanced_alloy_smelter(BlockAlloySmelter::create_enhanced, MachineTileEntity.TileAlloySmelterEnhanced),
  block_enhanced_alloy_smelter_top(BlockAlloySmelter::create_extension),
  block_buffer(BlockBuffer::create, MachineTileEntity.TileBufferAbstract),
  block_enchanter(BlockEnchanter::create, MachineTileEntity.TileEnchanter),
  block_farm_station(BlockFarmStation::create, MachineTileEntity.TileFarmStation),
  block_combustion_generator(BlockCombustionGenerator::create, MachineTileEntity.TileCombustionGenerator),
  block_enhanced_combustion_generator(BlockCombustionGenerator::create_enhanced, MachineTileEntity.TileCombustionGeneratorEnhanced),
  block_enhanced_combustion_generator_top(BlockCombustionGenerator::create_extension),
  block_simple_stirling_generator(BlockStirlingGenerator::create_simple, MachineTileEntity.TileStirlingGeneratorSimple),
  block_stirling_generator(BlockStirlingGenerator::create, MachineTileEntity.TileStirlingGenerator),
  block_zombie_generator(BlockZombieGenerator::create, MachineTileEntity.TileZombieGenerator),
  block_franken_zombie_generator(BlockZombieGenerator::create_franken, MachineTileEntity.TileFrankenZombieGenerator),
  block_ender_generator(BlockZombieGenerator::create_ender, MachineTileEntity.TileEnderGenerator),
  block_lava_generator(BlockLavaGenerator::create, MachineTileEntity.TileLavaGenerator),

  block_killer_joe(BlockKillerJoe::create, MachineTileEntity.TileKillerJoe),
  block_electric_light(BlockElectricLight::create, MachineTileEntity.TileElectricLight),
  block_light_node(BlockLightNode::create, MachineTileEntity.TileLightNode),

  // Obelisks
  block_attractor_obelisk(BlockAttractor::create, MachineTileEntity.TileAttractor),
  block_aversion_obelisk(BlockAversionObelisk::create, MachineTileEntity.TileAversionObelisk),
  block_inhibitor_obelisk(BlockInhibitorObelisk::create, MachineTileEntity.TileInhibitorObelisk),
  block_relocator_obelisk(BlockRelocatorObelisk::create, MachineTileEntity.TileRelocatorObelisk),
  block_weather_obelisk(BlockWeatherObelisk::create, MachineTileEntity.TileWeatherObelisk),
  block_experience_obelisk(BlockExperienceObelisk::create, MachineTileEntity.TileExperienceObelisk),

  block_painter(BlockPainter::create, MachineTileEntity.TileEntityPainter),
  block_reservoir(BlockReservoirBase::create, MachineTileEntity.TileReservoir),
  block_omni_reservoir(BlockReservoirBase::create_omni, MachineTileEntity.TileOmniReservoir),
  block_simple_sag_mill(BlockSagMill::create_simple, MachineTileEntity.TileSagMillSimple),
  block_sag_mill(BlockSagMill::create, MachineTileEntity.TileSagMill),
  block_enhanced_sag_mill(BlockSagMill::create_enhanced, MachineTileEntity.TileSagMillEnhanced),
  block_enhanced_sag_mill_top(BlockSagMill::create_extension),
  block_slice_and_splice(BlockSliceAndSplice::create, MachineTileEntity.TileSliceAndSplice),
  block_solar_panel(BlockSolarPanel::create, MachineTileEntity.TileSolarPanel),
  block_soul_binder(BlockSoulBinder::create, MachineTileEntity.TileSoulBinder),
  block_powered_spawner(BlockPoweredSpawner::create, MachineTileEntity.TilePoweredSpawner),
  block_vat(BlockVat::create, MachineTileEntity.TileVat),
  block_enhanced_vat(BlockVat::create_enhanced, MachineTileEntity.TileVatEnhanced),
  block_enhanced_vat_top(BlockVat::create_extension),
  block_simple_wired_charger(BlockWiredCharger::create_simple, MachineTileEntity.TileWiredChargerSimple),
  block_wired_charger(BlockWiredCharger::create, MachineTileEntity.TileWiredCharger),
  block_enhanced_wired_charger(BlockWiredCharger::create_enhanced, MachineTileEntity.TileWiredChargerEnhanced),
  block_enhanced_wired_charger_top(BlockWiredCharger::create_extension, MachineTileEntity.TileWiredChargerEnhanced),
  block_wireless_charger(BlockWirelessCharger::create, MachineTileEntity.TileWirelessCharger),
  block_normal_wireless_charger(BlockNormalWirelessCharger::create, MachineTileEntity.TileWirelessCharger),
  block_enhanced_wireless_charger(BlockEnhancedWirelessCharger::create, MachineTileEntity.TileWirelessCharger),
  block_wireless_charger_extension(BlockAntenna::create),
  block_tank(BlockTank::create, MachineTileEntity.TileTank),
  block_transceiver(BlockTransceiver::create, MachineTileEntity.TileTransceiver),
  block_vacuum_chest(BlockVacuumChest::create, MachineTileEntity.TileVacuumChest),
  block_xp_vacuum(BlockXPVacuum::create, MachineTileEntity.TileXPVacuum),

  block_travel_anchor(BlockTravelAnchor::create, MachineTileEntity.TileTravelAnchor),
  block_tele_pad(BlockTelePad::create_telepad, MachineTileEntity.TileTelePad),
  block_dialing_device(BlockDialingDevice::create, MachineTileEntity.TileDialingDevice),

  block_impulse_hopper(BlockImpulseHopper::create, MachineTileEntity.TileImpulseHopper),
  block_simple_crafter(BlockCrafter::create_simple, MachineTileEntity.TileSimpleCrafter),
  block_crafter(BlockCrafter::create, MachineTileEntity.TileCrafter),

  block_creative_spawner(BlockCreativeSpawner::create, MachineTileEntity.TileCreativeSpawner),

  ;

  @SubscribeEvent
  public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
    event.register(MachineObject.class);
  }

  final @Nonnull String unlocalisedName;

  protected @Nullable Block block;
  protected @Nullable Item item;

  protected final @Nullable IModTileEntity modTileEntity;

  protected final @Nullable Function<IModObject, Block> blockMaker;
  protected final @Nullable BiFunction<IModObject, Block, Item> itemMaker;

  private MachineObject(@Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
    this(null, itemMaker, null);
  }

  private MachineObject(@Nonnull Function<IModObject, Block> blockMaker) {
    this(blockMaker, null, null);
  }

  private MachineObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull BiFunction<IModObject, Block, Item> itemMaker) {
    this(blockMaker, itemMaker, null);
  }

  private MachineObject(@Nonnull Function<IModObject, Block> blockMaker, @Nonnull IModTileEntity modTileEntity) {
    this(blockMaker, null, modTileEntity);
  }

  private MachineObject(@Nullable Function<IModObject, Block> blockMaker, @Nullable BiFunction<IModObject, Block, Item> itemMaker,
      @Nullable IModTileEntity modTileEntity) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.blockMaker = blockMaker;
    this.itemMaker = itemMaker;
    if (blockMaker == null && itemMaker == null) {
      throw new RuntimeException(this + " unexpectedly is neither a Block nor an Item.");
    }
    this.modTileEntity = modTileEntity;
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
  public final @Nullable Class<?> getClazz() {
    return null;
  }

  @Override
  public final String getBlockMethodName() {
    return null;
  }

  @Override
  public final String getItemMethodName() {
    return null;
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

  @Override
  public @Nonnull Function<IModObject, Block> getBlockCreator() {
    return blockMaker != null ? blockMaker : mo -> null;
  }

  @Override
  public @Nonnull BiFunction<IModObject, Block, Item> getItemCreator() {
    return NullHelper.first(itemMaker, IModObject.WithBlockItem.itemCreator);
  }

  @Override
  @Nonnull
  public <B extends Block> B apply(@Nonnull B blockIn) {
    blockIn.setCreativeTab(EnderIOTab.tabEnderIOMachines);
    return IModObjectBase.super.apply(blockIn);
  }

}
