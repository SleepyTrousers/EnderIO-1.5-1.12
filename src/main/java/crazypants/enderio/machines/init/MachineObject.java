package crazypants.enderio.machines.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.machine.alloy.BlockAlloySmelter;
import crazypants.enderio.machines.machine.buffer.BlockBuffer;
import crazypants.enderio.machines.machine.enchanter.BlockEnchanter;
import crazypants.enderio.machines.machine.farm.BlockFarmStation;
import crazypants.enderio.machines.machine.generator.combustion.BlockCombustionGenerator;
import crazypants.enderio.machines.machine.generator.stirling.BlockStirlingGenerator;
import crazypants.enderio.machines.machine.generator.zombie.BlockZombieGenerator;
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
import crazypants.enderio.machines.machine.tank.BlockTank;
import crazypants.enderio.machines.machine.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.machines.machine.teleport.telepad.BlockDialingDevice;
import crazypants.enderio.machines.machine.teleport.telepad.BlockTelePad;
import crazypants.enderio.machines.machine.transceiver.BlockTransceiver;
import crazypants.enderio.machines.machine.vacuum.BlockVacuumChest;
import crazypants.enderio.machines.machine.vacuum.BlockXPVacuum;
import crazypants.enderio.machines.machine.vat.BlockVat;
import crazypants.enderio.machines.machine.wireless.BlockWirelessCharger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public enum MachineObject implements IModObject.Registerable {

  block_alloy_smelter(BlockAlloySmelter.class),
  block_buffer(BlockBuffer.class),
//  blockCapBank(BlockCapBank.class),
  block_enchanter(BlockEnchanter.class),
  block_farm_station(BlockFarmStation.class),
  block_combustion_generator(BlockCombustionGenerator.class),
  block_stirling_generator(BlockStirlingGenerator.class),
  block_zombie_generator(BlockZombieGenerator.class),

  block_killer_joe(BlockKillerJoe.class),
  block_electric_light(BlockElectricLight.class),
  block_light_node(BlockLightNode.class),
  
  //Obelisks
  block_attractor_obelisk(BlockAttractor.class),
  block_aversion_obelisk(BlockAversionObelisk.class),
  block_inhibitor_obelisk(BlockInhibitorObelisk.class),
  block_relocator_obelisk(BlockRelocatorObelisk.class),
  block_weather_obelisk(BlockWeatherObelisk.class),
  block_experience_obelisk(BlockExperienceObelisk.class),
  
  block_painter(BlockPainter.class),
  block_reservoir(BlockReservoir.class),
  block_sag_mill(BlockSagMill.class),
  block_slice_and_splice(BlockSliceAndSplice.class),
  block_solar_panel(BlockSolarPanel.class),
  block_soul_binder(BlockSoulBinder.class),
  block_powered_spawner(BlockPoweredSpawner.class),
  block_vat(BlockVat.class),
  block_wireless_charger(BlockWirelessCharger.class),
  block_tank(BlockTank.class),
  block_transceiver(BlockTransceiver.class),
  block_vacuum_chest(BlockVacuumChest.class),
  block_xp_vacuum(BlockXPVacuum.class),
  
  block_travel_anchor(BlockTravelAnchor.class),
  block_tele_pad(BlockTelePad.class),
  block_dialing_device(BlockDialingDevice.class),
  ;

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void registerBlocksEarly(@Nonnull RegistryEvent.Register<Block> event) {
    ModObjectRegistry.addModObjects(MachineObject.class);
  }

  final @Nonnull String unlocalisedName;

  protected @Nullable Block block;
  protected @Nullable Item item;

  protected final @Nonnull Class<?> clazz;
  protected final @Nullable String blockMethodName, itemMethodName;
  protected final @Nullable Class<? extends TileEntity> teClazz;

  private MachineObject(@Nonnull Class<?> clazz) {
    this(clazz, "create", (Class<? extends TileEntity>) null);
  }

  private MachineObject(@Nonnull Class<?> clazz, Class<? extends TileEntity> teClazz) {
    this(clazz, "create", teClazz);
  }

  private MachineObject(@Nonnull Class<?> clazz, @Nonnull String methodName) {
    this(clazz, methodName, (Class<? extends TileEntity>) null);
  }

  private MachineObject(@Nonnull Class<?> clazz, @Nonnull String blockMethodName, @Nonnull String itemMethodName) {
    this(clazz, blockMethodName, itemMethodName, null);
  }

  private MachineObject(@Nonnull Class<?> clazz, @Nonnull String methodName, Class<? extends TileEntity> teClazz) {
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
    this.teClazz = teClazz;
  }

  private MachineObject(@Nonnull Class<?> clazz, @Nullable String blockMethodName, @Nullable String itemMethodName, Class<? extends TileEntity> teClazz) {
    this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
    this.clazz = clazz;
    this.blockMethodName = blockMethodName == null || blockMethodName.isEmpty() ? null : blockMethodName;
    this.itemMethodName = itemMethodName == null || itemMethodName.isEmpty() ? null : itemMethodName;
    this.teClazz = teClazz;
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
  public Class<? extends TileEntity> getTileClass() {
    return teClazz;
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
