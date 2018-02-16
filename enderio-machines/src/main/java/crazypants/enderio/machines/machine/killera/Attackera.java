package crazypants.enderio.machines.machine.killera;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.enderio.core.common.transform.EnderCoreMethods.ICreeperTarget;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.UserIdent;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import crazypants.enderio.base.power.wireless.WirelessChargedLocation;
import crazypants.enderio.machines.config.config.KillerJoeConfig;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

class Attackera extends FakePlayerEIO implements ICreeperTarget {

  private static final UUID uuid = UUID.fromString("3baa66fa-a69a-11e4-89d3-123b93f75cba");
  private static final GameProfile DUMMY_PROFILE = new GameProfile(uuid, "[Killer Joe]");

  private final @Nonnull TileKillerJoe killerJoe;
  protected WirelessChargedLocation chargedLocation;

  @Nonnull
  ItemStack prevWeapon = ItemStack.EMPTY;

  public Attackera(@Nonnull TileKillerJoe killerJoe, @Nonnull UserIdent owner) {
    super(killerJoe.getWorld(), killerJoe.getLocation(), (owner == UserIdent.NOBODY || StringUtils.isBlank(owner.getPlayerName())) ? DUMMY_PROFILE
        : new GameProfile(uuid, "[" + owner.getPlayerName() + "'s Killer Joe]"));
    this.killerJoe = killerJoe;
    setOwner(owner);
    inventory = new InventoryKillerJoe(this, killerJoe);
    if (!world.isRemote) {
      chargedLocation = new WirelessChargedLocation(killerJoe);
    }
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
      killerJoe.markDirty();
    }
    ticksSinceLastSwing++;
  }

  public int getTicksSinceLastSwing() {
    return ticksSinceLastSwing;
  }

  @Override
  public boolean isCreeperTarget(@Nonnull EntityCreeper swellingCreeper) {
    return KillerJoeConfig.killerProvokesCreeperExpolosions.get();
  }

}