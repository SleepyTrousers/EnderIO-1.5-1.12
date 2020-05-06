package crazypants.enderio.machines.machine.killera;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.enderio.core.common.interfaces.ICreeperTarget;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.UserIdent;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import crazypants.enderio.base.power.wireless.WirelessChargedLocation;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.KillerJoeConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
class FakePlayerKillerJoe extends FakePlayerEIO implements ICreeperTarget {

  private static final UUID uuid = UUID.fromString("3baa66fa-a69a-11e4-89d3-123b93f75cba");
  private static final GameProfile DUMMY_PROFILE = new GameProfile(uuid, "[Killer Joe]");

  private final @Nonnull TileKillerJoe te;
  protected WirelessChargedLocation chargedLocation;

  @Nonnull
  ItemStack prevWeapon = ItemStack.EMPTY;

  public FakePlayerKillerJoe(@Nonnull TileKillerJoe te, @Nonnull UserIdent owner) {
    super(te.getWorld(), te.getLocation(), makeGameProfile(owner));
    this.te = te;
    setOwner(owner);
    inventory = new InventoryKillerJoe(this, te);
    if (!world.isRemote) {
      chargedLocation = new WirelessChargedLocation(te);
    }
  }

  private static @Nonnull GameProfile makeGameProfile(UserIdent owner) {
    return (owner == UserIdent.NOBODY || StringUtils.isBlank(owner.getPlayerName())) ? DUMMY_PROFILE
        : new GameProfile(uuid, "[" + owner.getPlayerName() + "'s Killer Joe]");
  }

  @Override
  public void onUpdate() {
    ItemStack prev = prevWeapon;
    ItemStack cur = getHeldItemMainhand();
    if (!ItemStack.areItemStacksEqual(cur, prev)) {
      if (!prev.isEmpty()) {
        getAttributeMap().removeAttributeModifiers(prev.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
      }

      if (!cur.isEmpty()) {
        getAttributeMap().applyAttributeModifiers(cur.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
      }
      prevWeapon = cur.copy();
    }

    if (chargedLocation != null && chargedLocation.chargeItems(new NNList<>(cur))) {
      te.markDirty();
    }
    ticksSinceLastSwing++;
  }

  public int getTicksSinceLastSwing() {
    return ticksSinceLastSwing;
  }

  @Override
  public void attackTargetEntityWithCurrentItem(@Nonnull Entity targetEntity) {
    onGround = true; // sweep attacks need this
    faceEntity(targetEntity); // sweep attack particles use this
    super.attackTargetEntityWithCurrentItem(targetEntity);
  }

  // taken from EntityLiving and simplified
  public void faceEntity(Entity entityIn) {
    double d0 = entityIn.posX - this.posX;
    double d2 = entityIn.posZ - this.posZ;
    double d1;

    if (entityIn instanceof EntityLivingBase) {
      EntityLivingBase entitylivingbase = (EntityLivingBase) entityIn;
      d1 = entitylivingbase.posY + entitylivingbase.getEyeHeight() - (this.posY + this.getEyeHeight());
    } else {
      d1 = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0D - (this.posY + this.getEyeHeight());
    }

    double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
    rotationPitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI))));
    rotationYaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F);
  }

  // don't let Creepers blow us up

  @Override
  public boolean isCreeperTarget(@Nonnull EntityCreeper swellingCreeper) {
    return KillerJoeConfig.killerProvokesCreeperExplosions.get();
  }

  // don't let Zombies summon aid

  @SubscribeEvent
  public static void onSummonAid(SummonAidEvent event) {
    if (event.getAttacker() instanceof FakePlayerKillerJoe && !KillerJoeConfig.killerProvokesZombieHordes.get()) {
      event.setResult(Result.DENY);
    }
  }

}