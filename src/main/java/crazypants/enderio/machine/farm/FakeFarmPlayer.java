package crazypants.enderio.machine.farm;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import crazypants.enderio.Log;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.stats.StatBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * This is not in the FakePlayer hierarchy for reasons.
 *
 */
public class FakeFarmPlayer extends EntityPlayerMP {

  private static final UUID uuid = UUID.fromString("c1ddfd7f-120a-4437-8b64-38660d3ec62d");

  private static GameProfile DUMMY_PROFILE = new GameProfile(uuid, "[EioFarmer]");

  public FakeFarmPlayer(WorldServer world) {
    super(FMLCommonHandler.instance().getMinecraftServerInstance(), world, DUMMY_PROFILE, new PlayerInteractionManager(world));
    // ItemInWorldManager will access this field directly and can crash
    connection = new FakeNetHandlerPlayServer(this);
  }

  @Override
  public boolean canCommandSenderUseCommand(int i, String s) {
    return false;
  }

  // @Override
  // public ChunkCoordinates getPlayerCoordinates() {
  // return new ChunkCoordinates(0, 0, 0);
  // }

  @Override
  public void addStat(StatBase par1StatBase, int par2) {
  }

  @Override
  public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
  }

  @Override
  public boolean isEntityInvulnerable(DamageSource source) {
    return true;
  }

  @Override
  public boolean canAttackPlayer(EntityPlayer player) {
    return false;
  }

  @Override
  public void onDeath(DamageSource source) {
    return;
  }

  @Override
  public void onUpdate() {
    return;
  }

  @Override
  public boolean canPlayerEdit(BlockPos p_175151_1_, EnumFacing p_175151_2_, ItemStack p_175151_3_) {
    return true;
  }

  @Override
  public void setWorld(World p_70029_1_) {
    Log.warn("Ender IO Farming station fake player is being transfered to world '" + p_70029_1_ + "'. Trying to reject transfer. Call stack follows:");
    Thread.dumpStack();
  }

}
