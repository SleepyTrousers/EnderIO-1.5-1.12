package crazypants.enderio.teleport;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.ModObject;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.util.Lang;
import crazypants.util.Util;
import crazypants.vecmath.VecmathUtil;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector4d;

public class TravelPlatformController implements ITickHandler {

  public static final TravelPlatformController instance = new TravelPlatformController();

  private boolean wasJumping = false;

  private boolean showTargets = false;

  private BlockCoord selectedCoord;

  private final Set<BlockCoord> candidates = new HashSet<BlockCoord>();

  private int maxTravelDistanceSq = 32 * 32;

  private TravelPlatformController() {
  }

  public boolean showTargets() {
    return showTargets;
  }

  public boolean isBlockSelected(BlockCoord coord) {
    if(coord == null) {
      return false;
    }
    return coord.equals(selectedCoord);
  }

  public void addCandidate(BlockCoord coord) {
    candidates.add(coord);
  }

  public int getMaxTravelDistanceSq() {
    return maxTravelDistanceSq;
  }

  @Override
  public void tickStart(EnumSet<TickType> type, Object... tickData) {
    if(type.contains(TickType.CLIENT)) {
      EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
      if(player == null) {
        return;
      }
      showTargets = isStaffEquipped(player) || isOnTravelBlock(player);
      if(showTargets) {
        updateSelectedTarget(player);
      } else {
        selectedCoord = null;
      }
      MovementInput input = player.movementInput;
      if(input.jump && !wasJumping && showTargets && selectedCoord != null) {
        if(travelToSelectedTarget(player)) {
          input.jump = false;
        }
      }
      wasJumping = input.jump;
      candidates.clear();
    }
  }

  public boolean travelToSelectedTarget(EntityPlayer player) {
    if(isValidTarget(player, selectedCoord)) {
      sendTravelEvent(selectedCoord);
      return true;
    }
    player.sendChatToPlayer(ChatMessageComponent.createFromText(Lang.localize("blockTravelPlatform.invalidTarget")));
    return false;
  }

  private boolean isStaffEquipped(EntityClientPlayerMP player) {
    if(player.getCurrentEquippedItem() == null) {
      return false;
    }
    return ModObject.itemTravelStaff.actualId == player.getCurrentEquippedItem().itemID;
  }

  private boolean isValidTarget(EntityPlayer player, BlockCoord bc) {
    if(bc == null) {
      return false;
    }
    World w = player.worldObj;
    return canTeleportTo(bc.getLocation(ForgeDirection.UP), w) && canTeleportTo(bc.getLocation(ForgeDirection.UP).getLocation(ForgeDirection.UP), w);
  }

  private boolean canTeleportTo(BlockCoord bc, World w) {
    int blockId = w.getBlockId(bc.x, bc.y, bc.z);
    Block block = Util.getBlock(blockId);
    if(block == null || block.isAirBlock(w, bc.x, bc.y, bc.z)) {
      return true;
    }
    final AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(w, bc.x, bc.y, bc.z);
    return aabb == null || aabb.getAverageEdgeLength() < 0.7;
  }

  private void updateSelectedTarget(EntityClientPlayerMP player) {
    selectedCoord = null;
    if(candidates.isEmpty()) {
      return;
    }

    Vector3d eye = RenderUtil.getEyePositionEio(player);
    Vec3 look = player.getLookVec();

    Vector3d b = new Vector3d(eye);
    b.add(look.xCoord, look.yCoord, look.zCoord);

    Vector3d c = new Vector3d(eye);
    c.add(0, 1, 0);

    Vector4d leftPlane = new Vector4d();
    VecmathUtil.computePlaneEquation(eye, b, c, leftPlane);

    c.set(eye);
    c.add(leftPlane.x, leftPlane.y, leftPlane.z);

    Vector4d upPlane = new Vector4d();
    VecmathUtil.computePlaneEquation(eye, b, c, upPlane);

    double closestDistance = Double.MAX_VALUE;
    Vector3d point = new Vector3d();
    for (BlockCoord bc : candidates) {
      point.set(bc.x + 0.5, bc.y + 0.5, bc.z + 0.5);
      double dl = VecmathUtil.distanceFromPointToPlane(leftPlane, point);
      double du = VecmathUtil.distanceFromPointToPlane(upPlane, point);
      double dSq = (dl * dl) + (du * du);
      if(dSq < closestDistance) {
        selectedCoord = bc;
        closestDistance = dSq;
      }
    }

    if(selectedCoord != null) {
      if(selectedCoord.distanceSquared(new BlockCoord((int) eye.x, (int) eye.y, (int) eye.z)) > maxTravelDistanceSq) {
        selectedCoord = null;
      }
    }
  }

  private void sendTravelEvent(BlockCoord coord) {
    Packet p = TravelPlatformPacketHandler.createMovePacket(coord.x, coord.y, coord.z);
    PacketDispatcher.sendPacketToServer(p);
  }

  private boolean isOnTravelBlock(EntityClientPlayerMP player) {
    World world = Minecraft.getMinecraft().theWorld;
    if(world != null && player != null) {
      int x = MathHelper.floor_double(player.posX);
      int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
      int z = MathHelper.floor_double(player.posZ);
      return world.getBlockId(x, y, z) == ModObject.blockTravelPlatform.actualId;
    }
    return false;
  }

  @Override
  public void tickEnd(EnumSet<TickType> type, Object... tickData) {
  }

  @Override
  public EnumSet<TickType> ticks() {
    return EnumSet.of(TickType.CLIENT);
  }

  @Override
  public String getLabel() {
    return "TravelClientTickHandler";
  }
}