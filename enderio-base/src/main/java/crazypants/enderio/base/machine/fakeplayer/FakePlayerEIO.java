package crazypants.enderio.base.machine.fakeplayer;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.UserIdent;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.util.Prep;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class FakePlayerEIO extends FakePlayer {

  @Nonnull
  ItemStack prevWeapon = Prep.getEmpty();
  private final @Nonnull WorldServer origWorld;

  public FakePlayerEIO(World world, BlockPos pos, GameProfile profile) {
    super(FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(world.provider.getDimension()), profile);
    origWorld = super.getServerWorld();
    posX = pos.getX() + 0.5;
    posY = pos.getY() + 0.5;
    posZ = pos.getZ() + 0.5;
    // ItemInWorldManager will access this field directly and can crash
    connection = new FakeNetHandlerPlayServer(this);
  }

  // These do things with packets...which crashes since the net handler is null. Potion effects are not needed anyways.
  @Override
  protected void onNewPotionEffect(@Nonnull PotionEffect p_70670_1_) {
  }

  @Override
  protected void onChangedPotionEffect(@Nonnull PotionEffect p_70695_1_, boolean p_70695_2_) {
  }

  @Override
  protected void onFinishedPotionEffect(@Nonnull PotionEffect p_70688_1_) {
  }

  @Override
  protected void playEquipSound(@Nullable ItemStack stack) {
  }

  private @Nonnull UserIdent owner = UserIdent.NOBODY;

  /**
   * Returns the UUID of the player who is responsible for this FakePlayer or null if no player is responsible or known. May return the UUID of another fake
   * player if the block was placed by one.
   */
  public UUID getOwner() {
    return owner == UserIdent.NOBODY ? null : owner.getUUID();
  }

  public @Nonnull FakePlayerEIO setOwner(@Nullable UserIdent owner) {
    this.owner = owner == null ? UserIdent.NOBODY : owner;
    return this;
  }

  public void clearOwner() {
    this.owner = UserIdent.NOBODY;
  }

  @Override
  public @Nonnull WorldServer getServerWorld() {
    return origWorld;
  }

  @Override
  public void onItemPickup(@Nonnull Entity entityIn, int quantity) {
    if (world instanceof WorldServer) {
      // EntityLivingBase will unconditionally cast world to WorldServer
      super.onItemPickup(entityIn, quantity);
      // no else needed, if it's not a server world there will be no attached tracker anyway
    }
  }

}
