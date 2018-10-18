package crazypants.enderio.machines.darksteel.upgrade.wet;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.machines.config.config.UpgradeConfig;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    if (EnergyUpgradeManager.getEnergyStored(boots) < Math.min(UpgradeConfig.wetEnergyUsePerCobblestoneConverstion.get(),
        UpgradeConfig.wetEnergyUsePerObsidianConverstion.get()))
      return;
    BlockPos playerPos = player.getPosition();
    double range = UpgradeConfig.wetRange.get();
    World world = player.getEntityWorld();

    for (BlockPos pos : BlockPos.getAllInBox(playerPos.add(-range, -1.0D, -range), playerPos.add(range, -1.0D, range))) {
      if (pos.distanceSqToCenter(player.posX, player.posY, player.posZ) > range * range || world.getBlockState(pos).getMaterial() != Material.LAVA)
        continue;
      if (world.getBlockState(pos).getValue(BlockLiquid.LEVEL) == 0
          && EnergyUpgradeManager.getEnergyStored(boots) >= UpgradeConfig.wetEnergyUsePerObsidianConverstion.get()) {
        EnergyUpgradeManager.extractEnergy(boots, item, UpgradeConfig.wetEnergyUsePerObsidianConverstion, false);
        world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
        triggerLiquidConversionEffects(world, pos);
      } else if (world.getBlockState(pos).getValue(BlockLiquid.LEVEL) > 0 && world.getBlockState(pos).getValue(BlockLiquid.LEVEL) <= 4
          && EnergyUpgradeManager.getEnergyStored(boots) >= UpgradeConfig.wetEnergyUsePerCobblestoneConverstion.get()) {
        EnergyUpgradeManager.extractEnergy(boots, item, UpgradeConfig.wetEnergyUsePerCobblestoneConverstion, false);
        world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
        triggerLiquidConversionEffects(world, pos);
      }
    }
  }

  @SideOnly(Side.CLIENT)
  protected void triggerLiquidConversionEffects(World world, BlockPos pos) {
    double d0 = pos.getX();
    double d1 = pos.getY();
    double d2 = pos.getZ();
    world.playSound((EntityPlayer) null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.05F,
        2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
    for (int i = 0; i < 4; ++i) {
      world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D);
    }
  }

}
