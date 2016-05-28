package crazypants.enderio.machine;

import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;
import com.mojang.authlib.GameProfile;

import crazypants.enderio.machine.farm.FakeNetHandlerPlayServer;
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
  
  protected void playEquipSound(@Nullable ItemStack stack) {  
  }

}
