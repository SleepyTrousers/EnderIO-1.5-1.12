package crazypants.enderio.machines.darksteel.upgrade.wet;

import javax.annotation.Nonnull;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.network.PacketSpawnParticles;
import crazypants.enderio.base.potion.PotionUtil;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.config.config.UpgradeConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.block.BlockLiquid;
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

public class WetUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "wet";

  public static final @Nonnull WetUpgrade WET1 = new WetUpgrade(1, PotionUtil.getEmptyPotion(true));
  public static final @Nonnull WetUpgrade WET2 = new WetUpgrade(2, new ItemStack(Items.WATER_BUCKET));
  public static final @Nonnull WetUpgrade WET3 = new WetUpgrade(3, new ItemStack(MachineObject.block_reservoir.getItemNN()));
  public static final @Nonnull WetUpgrade WET4 = new WetUpgrade(4, new ItemStack(Blocks.SPONGE, 1, 1));
  public static final @Nonnull WetUpgrade WET5 = new WetUpgrade(5, new ItemStack(MachineObject.block_reservoir.getItemNN(), 4));

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
    if ((EnderIO.proxy.getServerTickCount() & 0b11) > 0 && !player.isInLava())
      return;
    double range = CapacitorKey.WET_UPGRADE_RANGE.get(capData);
    double sqRange = range * range;
    int powerNeeded = CapacitorKey.WET_UPGRADE_POWER_USE.get(capData);
    int cobblestonePowerNeeded = (int) (CapacitorKey.WET_UPGRADE_POWER_USE.get(capData) * UpgradeConfig.cobblestoneModifier.get());
    double heightUp = CapacitorKey.WET_UPGRADE_HEIGHT_UP.get(capData);
    double heightDown = CapacitorKey.WET_UPGRADE_HEIGHT_DOWN.get(capData);
    BlockPos playerPos = player.getPosition();
    World world = player.getEntityWorld();
    PacketSpawnParticles particles = new PacketSpawnParticles();
    if (!world.isRemote && player.isInLava() && player instanceof EntityPlayerMP
        && (world.getBlockState(playerPos).getBlock() == Blocks.LAVA || world.getBlockState(playerPos).getBlock() == Blocks.FLOWING_LAVA)) {
      world.setBlockState(playerPos, (world.getBlockState(playerPos).getValue(BlockLiquid.LEVEL) == 0 ? Blocks.OBSIDIAN : Blocks.COBBLESTONE).getDefaultState());
      EnergyUpgradeManager.extractEnergy(boots, Math.min(powerNeeded, cobblestonePowerNeeded), false);
      ((EntityPlayerMP) player).connection.setPlayerLocation(player.posX, Math.floor(player.posY) + 1.01, player.posZ, player.rotationYaw,
          player.rotationPitch);
    }
    for (BlockPos pos : BlockPos.getAllInBox(playerPos.add(-range, heightDown - 1, -range), playerPos.add(range, heightUp - 1, range))) {
      if (pos.distanceSqToCenter(playerPos.getX(), pos.getY(), playerPos.getZ()) < sqRange
          && (world.getBlockState(pos).getBlock() == Blocks.LAVA || world.getBlockState(pos).getBlock() == Blocks.FLOWING_LAVA)) {
        boolean legalBlock = range <= 1.5;
        if (!legalBlock) {
          for (BlockPos airCheck : BlockPos.getAllInBox(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            if (world.isAirBlock(airCheck)) {
              legalBlock = true;
              break;
            }
          }
        }
        if (legalBlock) {
          if (!world.isRemote) {
            int flowLevel = world.getBlockState(pos).getValue(BlockLiquid.LEVEL);
            int energyStored = EnergyUpgradeManager.getEnergyStored(boots);
            if (flowLevel == 0 && energyStored >= powerNeeded) {
              EnergyUpgradeManager.extractEnergy(boots, powerNeeded, false);
              world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
            } else if (energyStored >= cobblestonePowerNeeded) {
              EnergyUpgradeManager.extractEnergy(boots, cobblestonePowerNeeded, false);
              world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
            } else {
              PacketHandler.INSTANCE.sendToAllAround(particles, playerPos, world);
              return;
            }
          }
          world.playSound((EntityPlayer) null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.1F,
              2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
          for (int i = 0; i < 4; ++i) {
            particles.add(pos.getX() + Math.random(), pos.getY() + 1.2D, pos.getZ() + Math.random(), 1, EnumParticleTypes.SMOKE_NORMAL);
          }
        }
      }
    }
    PacketHandler.INSTANCE.sendToAllAround(particles, playerPos, world);
  }
}
