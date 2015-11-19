package crazypants.enderio.machine.farm;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import crazypants.enderio.Log;

/**
 * This is not in the FakePlayer hierarchy for reasons.
 *
 */
public class FakeFarmPlayer extends EntityPlayerMP {

  private static final UUID uuid = UUID.fromString("c1ddfd7f-120a-4437-8b64-38660d3ec62d");

  private static GameProfile DUMMY_PROFILE = new GameProfile(uuid, "[EioFarmer]");

  public FakeFarmPlayer(WorldServer world) {
    super(FMLCommonHandler.instance().getMinecraftServerInstance(), world, DUMMY_PROFILE, new ItemInWorldManager(world));
    // ItemInWorldManager will access this field directly and can crash
    playerNetServerHandler = new FakeNetHandlerPlayServer(this);
  }

  @Override
  public boolean canCommandSenderUseCommand(int i, String s) {
    return false;
  }

  @Override
  public ChunkCoordinates getPlayerCoordinates() {
    return new ChunkCoordinates(0, 0, 0);
  }

  @Override
  public void addStat(StatBase par1StatBase, int par2) {
  }

  @Override
  public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
  }

  @Override
  public boolean isEntityInvulnerable() {
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
  public void travelToDimension(int dim) {
    return;
  }

  @Override
  public void func_147100_a(C15PacketClientSettings pkt) {
    return;
  }

  @Override
  public boolean canPlayerEdit(int par1, int par2, int par3, int par4, ItemStack par5ItemStack) {
    return true;
  }

  @Override
  public void setWorld(World p_70029_1_) {
    Log.warn("Ender IO Farming station fake player is being transfered to world '" + p_70029_1_
        + "'. Trying to reject transfer. Call stack follows:");
    Thread.dumpStack();
  }

}
