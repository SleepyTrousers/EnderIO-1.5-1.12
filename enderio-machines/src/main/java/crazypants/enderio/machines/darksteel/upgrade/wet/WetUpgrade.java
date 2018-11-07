package crazypants.enderio.machines.darksteel.upgrade.wet;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.blockiterators.CubicBlockIterator;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.network.PacketSpawnParticles;
import crazypants.enderio.base.potion.PotionUtil;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.config.config.UpgradeConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public class WetUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "wet";

  public static final @Nonnull WetUpgrade WET1 = new WetUpgrade(1, PotionUtil.getEmptyPotion(false));
  public static final @Nonnull WetUpgrade WET2 = new WetUpgrade(2, new ItemStack(Items.WATER_BUCKET));
  public static final @Nonnull WetUpgrade WET3 = new WetUpgrade(3, new ItemStack(MachineObject.block_reservoir.getItemNN()));
  public static final @Nonnull WetUpgrade WET4 = new WetUpgrade(4, new ItemStack(Blocks.SPONGE, 1, 1));
  public static final @Nonnull WetUpgrade WET5 = new WetUpgrade(5, new ItemStack(MachineObject.block_reservoir.getItemNN(), 4));

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    final IForgeRegistry<IDarkSteelUpgrade> registry = event.getRegistry();
    registry.register(WET1);
    registry.register(WET2);
    registry.register(WET3);
    registry.register(WET4);
    registry.register(WET5);
  }

  protected final @Nonnull ICapacitorData capData;
  protected final int level;

  public WetUpgrade(int level, @Nonnull ItemStack item) {
    super(UPGRADE_NAME, level, "enderio.darksteel.upgrade.wet_" + level, item, UpgradeConfig.wetCost.get(level - 1));
    this.level = level;
    this.capData = new ICapacitorData() {

      @Override
      public float getUnscaledValue(@Nonnull ICapacitorKey key) {
        return level;
      }

      @Override
      @Nonnull
      public String getUnlocalizedName() {
        return "enderio.darksteel.upgrade.wet_" + level;
      }

      @Override
      @Nonnull
      public String getLocalizedName() {
        return EnderIO.lang.localize(getUnlocalizedName());
      }

    };
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(EntityEquipmentSlot.FEET) && EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack);
  }

  @Override
  public void onPlayerTick(@Nonnull ItemStack boots, @Nonnull IDarkSteelItem item, @Nonnull EntityPlayer player) {
    if (((EnderIO.proxy.getServerTickCount() & 0b11) > 0 || !player.onGround) && !player.isInLava() || EnergyUpgradeManager.getEnergyStored(boots) == 0
        || player.world.isRemote)
      return;
    double range = CapacitorKey.WET_UPGRADE_RANGE.get(capData);
    double sqRange = range * range;
    double heightUp = CapacitorKey.WET_UPGRADE_HEIGHT_UP.get(capData);
    double heightDown = CapacitorKey.WET_UPGRADE_HEIGHT_DOWN.get(capData);
    BlockPos playerPos = player.getPosition();
    World world = player.getEntityWorld();
    NNList<BlockPos> toChange = new NNList<BlockPos>();
    for (BlockPos pos : BlockPos.getAllInBox(playerPos.add(-range, -heightDown - 1, -range), playerPos.add(range, heightUp - 1, range))) {
      if (pos.distanceSqToCenter(playerPos.getX(), pos.getY(), playerPos.getZ()) < sqRange
          && (world.getBlockState(pos).getBlock() == Blocks.LAVA || world.getBlockState(pos).getBlock() == Blocks.FLOWING_LAVA)) {
        for (CubicBlockIterator iter = new CubicBlockIterator(pos, 1); iter.hasNext();) {
          BlockPos airCheck = iter.next();
          if (world.isAirBlock(airCheck) && !toChange.contains(pos)) {
            toChange.add(pos);
            break;
          }
        }
      }
    }
    if (!toChange.isEmpty()) {
      int powerNeeded = CapacitorKey.WET_UPGRADE_POWER_USE.get(capData);
      int cobblestonePowerNeeded = (int) (powerNeeded * UpgradeConfig.cobblestoneModifier.get());
      PacketSpawnParticles particles = new PacketSpawnParticles();
      IBlockState obsidian = Blocks.OBSIDIAN.getDefaultState();
      IBlockState cobblestone = Blocks.COBBLESTONE.getDefaultState();
      for (NNIterator<BlockPos> iter = toChange.fastIterator(); iter.hasNext();) {
        BlockPos pos = iter.next();
        if (pos.getY() < playerPos.getY()
            || validForTransformationAbovePlayer(world, pos, toChange)) {
          int flowLevel = world.getBlockState(pos).getValue(BlockLiquid.LEVEL);
          if (!transformBlock(world, pos, flowLevel == 0 ? obsidian : cobblestone, boots, flowLevel == 0 ? powerNeeded : cobblestonePowerNeeded, particles)) {
            if (EnergyUpgradeManager.getEnergyStored(boots) < powerNeeded && EnergyUpgradeManager.getEnergyStored(boots) < cobblestonePowerNeeded) {
              break;
            }
          }
          if (pos.equals(playerPos) && player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.setPlayerLocation(player.posX, Math.floor(player.posY) + 1.01, player.posZ, player.rotationYaw,
                player.rotationPitch);
          }
        }
      }
      PacketHandler.INSTANCE.sendToAllAround(particles, playerPos, world);
    }
  }

  private boolean transformBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull ItemStack boots, int power,
      PacketSpawnParticles particles) {
    if (EnergyUpgradeManager.getEnergyStored(boots) < power) {
      return false;
    }
    world.setBlockState(pos, state);
    EnergyUpgradeManager.extractEnergy(boots, power, false);
    world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.1F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
    for (int i = 0; i < 4; ++i) {
      particles.add(pos.getX() + Math.random(), pos.getY() + 1.2D, pos.getZ() + Math.random(), 1, EnumParticleTypes.SMOKE_NORMAL);
    }
    return true;
  }

  private boolean validForTransformationAbovePlayer(World world, BlockPos pos, NNList<BlockPos> list) {
    BlockPos up = pos.up();
    return list.contains(pos) && (world.getBlockState(up).getBlock() != Blocks.LAVA && world.getBlockState(up).getBlock() != Blocks.FLOWING_LAVA
        || validForTransformationAbovePlayer(world, up, list));
  }
}
