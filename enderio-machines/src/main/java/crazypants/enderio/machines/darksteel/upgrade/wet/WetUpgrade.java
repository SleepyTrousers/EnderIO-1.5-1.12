package crazypants.enderio.machines.darksteel.upgrade.wet;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.network.PacketSpawnParticles;
import crazypants.enderio.machines.config.config.UpgradeConfig;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WetUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "wet";

  public static final @Nonnull WetUpgrade INSTANCE = new WetUpgrade();

  public WetUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.wet", new ItemStack(MachineObject.block_reservoir.getItemNN(), 4), UpgradeConfig.wetCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(EntityEquipmentSlot.FEET) && EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack);
  }

  @Override
  public void onPlayerTick(@Nonnull ItemStack boots, @Nonnull IDarkSteelItem item, @Nonnull EntityPlayer player) {
    if ((EnderIO.proxy.getServerTickCount() & 0b11) > 0 || !player.onGround || EnergyUpgradeManager.getEnergyStored(boots) < //
    Math.min(UpgradeConfig.wetEnergyUsePerCobblestoneConverstion.get(), UpgradeConfig.wetEnergyUsePerObsidianConverstion.get()))
      return;
    BlockPos playerPos = player.getPosition();
    double range = UpgradeConfig.wetRange.get() * player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() / 0.1D;
    double sqRange = range * range;
    World world = player.getEntityWorld();
    PacketSpawnParticles particles = new PacketSpawnParticles();
    for (BlockPos pos : BlockPos.getAllInBox(playerPos.add(-range, -1.0D, -range), playerPos.add(range, -1.0D, range))) {
      if (pos.distanceSqToCenter(player.posX, player.posY, player.posZ) <= sqRange
          && (world.getBlockState(pos).getBlock() == Blocks.LAVA || world.getBlockState(pos).getBlock() == Blocks.FLOWING_LAVA) && world.isAirBlock(pos.up())) {
        int level = world.getBlockState(pos).getValue(BlockLiquid.LEVEL);
        Block target = level == 0 ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
        IValue<Integer> energyTarget = level == 0 ? UpgradeConfig.wetEnergyUsePerObsidianConverstion : UpgradeConfig.wetEnergyUsePerCobblestoneConverstion;
        if (EnergyUpgradeManager.extractEnergy(boots, item, energyTarget, true) == energyTarget.get()) {
          if (!world.isRemote) {
            world.setBlockState(pos, target.getDefaultState());
          }
          world.playSound((EntityPlayer) null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.1F,
              2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
          EnergyUpgradeManager.extractEnergy(boots, item, energyTarget, false);
          for (int i = 0; i < 4; ++i) {
            particles.add(pos.getX() + Math.random(), pos.getY() + 1.2D, pos.getZ() + Math.random(), 1, EnumParticleTypes.SMOKE_NORMAL);
          }
        }
      }
    }
    PacketHandler.INSTANCE.sendToAllAround(particles, playerPos, world);
  }

}
