package crazypants.enderio.machine.fakeplayer;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;
import com.mojang.authlib.GameProfile;

import crazypants.util.UserIdent;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class FakePlayerEIO extends FakePlayer {

  ItemStack prevWeapon;

  public FakePlayerEIO(World world, BlockCoord pos, GameProfile profile) {
    super(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(world.provider.getDimension()), profile);
    posX = pos.x + 0.5;
    posY = pos.y + 0.5;
    posZ = pos.z + 0.5;
    // ItemInWorldManager will access this field directly and can crash
    connection = new FakeNetHandlerPlayServer(this);
  }

  // These do things with packets...which crash since the net handler is null. Potion effects are not needed anyways.
  @Override
  protected void onNewPotionEffect(PotionEffect p_70670_1_) {
  }

  @Override
  protected void onChangedPotionEffect(PotionEffect p_70695_1_, boolean p_70695_2_) {
  }

  @Override
  protected void onFinishedPotionEffect(PotionEffect p_70688_1_) {
  }
  
  @Override
  protected void playEquipSound(@Nullable ItemStack stack) {  
  }
  
//  @Override
//  public boolean canPlayerEdit(BlockPos p_175151_1_, EnumFacing p_175151_2_, @Nullable ItemStack p_175151_3_) {
//    return true;
//  }

  private @Nonnull UserIdent owner = UserIdent.nobody;

  /**
   * Returns the UUID of the player who is responsible for this FakePlayer or null if no player is responsible or known. May return the UUID of another fake
   * player if the block was placed by one.
   */
  public UUID getOwner() {
    return owner == UserIdent.nobody ? null : owner.getUUID();
  }

  public void setOwner(@Nullable UserIdent owner) {
    this.owner = owner == null ? UserIdent.nobody : owner;
  }

  public void clearOwner() {
    this.owner = UserIdent.nobody;
  }

}
