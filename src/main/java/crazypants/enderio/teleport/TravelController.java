package crazypants.enderio.teleport;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Camera;
import com.enderio.core.common.vecmath.Matrix4d;
import com.enderio.core.common.vecmath.VecmathUtil;
import com.enderio.core.common.vecmath.Vector2d;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.EnderIO;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.config.Config;
import crazypants.enderio.config.Config.TRAVEL_BLACKLIST;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.teleport.packet.PacketOpenAuthGui;
import crazypants.enderio.teleport.packet.PacketTravelEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.enderio.core.common.util.stackable.Things.TRAVEL_BLACKLIST;
import static crazypants.enderio.init.ModObject.blockTelePad;

public class TravelController {

  public static final TravelController instance = new TravelController();

  private Random rand = new Random();

  private boolean wasJumping = false;

  private boolean wasSneaking = false;

  private int delayTimer = 0;

  private int timer = Config.travelAnchorCooldown;

  private boolean tempJump;

  private boolean tempSneak;

  private boolean showTargets = false;

  public BlockCoord onBlockCoord;

  public BlockCoord selectedCoord;

  Camera currentView = new Camera();

  private final HashMap<BlockCoord, Float> candidates = new HashMap<BlockCoord, Float>();

  private boolean selectionEnabled = true;

  private double fovRad;

  private double tanFovRad;

  private TravelController() {
  }

  private boolean doesHandAllowTravel(EnumHand hand) {
    return Config.travelStaffOffhandTravelEnabled || hand == EnumHand.MAIN_HAND;
  }

  private boolean doesHandAllowBlink(EnumHand hand) {
    return Config.travelStaffOffhandBlinkEnabled || hand == EnumHand.MAIN_HAND;
  }

  private boolean doesHandAllowEnderIO(EnumHand hand) {
    return Config.travelStaffOffhandEnderIOEnabled || hand == EnumHand.MAIN_HAND;
  }

  public boolean activateTravelAccessable(ItemStack equipped, EnumHand hand, World world, EntityPlayer player, TravelSource source) {
    if(!hasTarget()) {
      return false;
    }
    BlockCoord target = selectedCoord;
    TileEntity te = world.getTileEntity(target.getBlockPos());
    if(te instanceof ITravelAccessable) {
      ITravelAccessable ta = (ITravelAccessable) te;
      if(ta.getRequiresPassword(player)) {
        PacketOpenAuthGui p = new PacketOpenAuthGui(target.getBlockPos());
        PacketHandler.INSTANCE.sendToServer(p);
        return true;
      }
    }
    if (doesHandAllowTravel(hand)) {
      travelToSelectedTarget(player, equipped, hand, source, false);
      return true;
    }
    return true;
  }

  public boolean doBlink(ItemStack equipped, EnumHand hand, EntityPlayer player) {
    if (!doesHandAllowBlink(hand)) {
      return false;
    }
    Vector3d eye = Util.getEyePositionEio(player);
    Vector3d look = Util.getLookVecEio(player);

    Vector3d sample = new Vector3d(look);
    sample.scale(Config.travelStaffMaxBlinkDistance);
    sample.add(eye);
    Vec3d eye3 = new Vec3d(eye.x, eye.y, eye.z);
    Vec3d end = new Vec3d(sample.x, sample.y, sample.z);

    double playerHeight = player.getYOffset();
    //if you looking at you feet, and your player height to the max distance, or part there of
    double lookComp = -look.y * playerHeight;
    double maxDistance = Config.travelStaffMaxBlinkDistance + lookComp;

    RayTraceResult p = player.world.rayTraceBlocks(eye3, end, !Config.travelStaffBlinkThroughClearBlocksEnabled);
    if(p == null) {

      //go as far as possible
      for (double i = maxDistance; i > 1; i--) {

        sample.set(look);
        sample.scale(i);
        sample.add(eye);
        //we test against our feets location
        sample.y -= playerHeight;
        if (doBlinkAround(player, equipped, hand, sample, true)) {
          return true;
        }
      }
      return false;
    } else {

      List<RayTraceResult> res = Util.raytraceAll(player.world, eye3, end, !Config.travelStaffBlinkThroughClearBlocksEnabled);
      for (RayTraceResult pos : res) {
        if(pos != null) {
          IBlockState hitBlock = player.world.getBlockState(pos.getBlockPos());
          if(isBlackListedBlock(player, pos, hitBlock)) {
            BlockPos bp = pos.getBlockPos();
            maxDistance = Math.min(maxDistance, VecmathUtil.distance(eye, new Vector3d(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5)) - 1.5 - lookComp);
          }
        }
      }

      eye3 = new Vec3d(eye.x, eye.y, eye.z);

      Vector3d targetBc = new Vector3d(p.getBlockPos());
      double sampleDistance = 1.5;
      BlockPos bp = p.getBlockPos();
      double teleDistance = VecmathUtil.distance(eye, new Vector3d(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5)) + sampleDistance;

      while (teleDistance < maxDistance) {
        sample.set(look);
        sample.scale(sampleDistance);
        sample.add(targetBc);
        //we test against our feets location
        sample.y -= playerHeight;

        if (doBlinkAround(player, equipped, hand, sample, false)) {
          return true;
        }
        teleDistance++;
        sampleDistance++;
      }
      sampleDistance = -0.5;
      teleDistance = VecmathUtil.distance(eye, new Vector3d(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5)) + sampleDistance;
      while (teleDistance > 1) {
        sample.set(look);
        sample.scale(sampleDistance);
        sample.add(targetBc);
        //we test against our feets location
        sample.y -= playerHeight;

        if (doBlinkAround(player, equipped, hand, sample, false)) {
          return true;
        }
        sampleDistance--;
        teleDistance--;
      }
    }
    return false;
  }

  private boolean isBlackListedBlock(EntityPlayer player, RayTraceResult pos, IBlockState hitBlock) {
    return Config.TRAVEL_BLACKLIST.contains(hitBlock.getBlock())
        && (hitBlock.getBlockHardness(player.world, pos.getBlockPos()) < 0 || !Config.travelStaffBlinkThroughUnbreakableBlocksEnabled);
  }

  private boolean doBlinkAround(EntityPlayer player, ItemStack equipped, EnumHand hand, Vector3d sample, boolean conserveMomentum) {
    if (doBlink(player, equipped, hand, new BlockCoord((int) Math.floor(sample.x), (int) Math.floor(sample.y) - 1, (int) Math.floor(sample.z)),
        conserveMomentum)) {
      return true;
    }
    if (doBlink(player, equipped, hand, new BlockCoord((int) Math.floor(sample.x), (int) Math.floor(sample.y), (int) Math.floor(sample.z)), conserveMomentum)) {
      return true;
    }
    if (doBlink(player, equipped, hand, new BlockCoord((int) Math.floor(sample.x), (int) Math.floor(sample.y) + 1, (int) Math.floor(sample.z)),
        conserveMomentum)) {
      return true;
    }
    return false;
  }

  private boolean doBlink(EntityPlayer player, ItemStack equipped, EnumHand hand, BlockCoord coord, boolean conserveMomentum) {
    return travelToLocation(player, equipped, hand, TravelSource.STAFF_BLINK, coord, conserveMomentum);
  }

  public boolean showTargets() {
    return showTargets && selectionEnabled;
  }

  public void setSelectionEnabled(boolean b) {
    selectionEnabled = b;
    if(!selectionEnabled) {
      candidates.clear();
    }
  }

  public boolean isBlockSelected(BlockCoord coord) {
    if(coord == null) {
      return false;
    }
    return coord.equals(selectedCoord);
  }

  public void addCandidate(BlockCoord coord) {
    if(!candidates.containsKey(coord)) {
      candidates.put(coord, -1f);
    }
  }

  public int getMaxTravelDistanceSq() {
    return TravelSource.getMaxDistanceSq();
  }

  @SubscribeEvent
  public void onRender(RenderWorldLastEvent event) {

    Minecraft mc = Minecraft.getMinecraft();
    Vector3d eye = Util.getEyePositionEio(mc.player);
    Vector3d lookAt = Util.getLookVecEio(mc.player);
    lookAt.add(eye);
    Matrix4d mv = VecmathUtil.createMatrixAsLookAt(eye, lookAt, new Vector3d(0, 1, 0));

    float fov = Minecraft.getMinecraft().gameSettings.fovSetting;
    Matrix4d pr = VecmathUtil.createProjectionMatrixAsPerspective(fov, 0.05f, mc.gameSettings.renderDistanceChunks * 16, mc.displayWidth,
        mc.displayHeight);
    currentView.setProjectionMatrix(pr);
    currentView.setViewMatrix(mv);
    currentView.setViewport(0, 0, mc.displayWidth, mc.displayHeight);

    fovRad = Math.toRadians(fov);
    tanFovRad = Math.tanh(fovRad);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if(event.phase == TickEvent.Phase.END) {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      if(player == null) {
        return;
      }
      onBlockCoord = getActiveTravelBlock(player);
      boolean onBlock = onBlockCoord != null;
      showTargets = onBlock || isTravelItemActiveForSelecting(player);
      if(showTargets) {
        updateSelectedTarget(player);
      } else {
        selectedCoord = null;
      }
      MovementInput input = player.movementInput;
      if(input == null) {
        return;
      }
      tempJump = input.jump;
      tempSneak = input.sneak;

      // Handles teleportation if a target is selected
      if((input.jump && !wasJumping && onBlock && selectedCoord != null && delayTimer == 0)
          || (input.sneak && !wasSneaking && onBlock && selectedCoord != null && delayTimer == 0 && Config.travelAnchorSneak)) {

        onInput(player);
        delayTimer = timer;
      }
      // If there is no selected coordinate and the input is jump, go up
      if(input.jump && !wasJumping && onBlock && selectedCoord == null && delayTimer == 0) {

        updateVerticalTarget(player, 1);
        onInput(player);
        delayTimer = timer;

      }

      // If there is no selected coordinate and the input is sneak, go down
      if(input.sneak && !wasSneaking && onBlock && selectedCoord == null && delayTimer == 0) {
        updateVerticalTarget(player, -1);
        onInput(player);
        delayTimer = timer;
      }

      if(delayTimer != 0) {
        delayTimer--;
      }

      wasJumping = tempJump;
      wasSneaking = tempSneak;
      candidates.clear();
    }
  }

  private boolean hasTarget() {
    return selectedCoord != null;
  }

  private int getEnergyInTravelItem(ItemStack equipped) {
    if(equipped == null || !(equipped.getItem() instanceof IItemOfTravel)) {
      return 0;
    }
    return ((IItemOfTravel) equipped.getItem()).getEnergyStored(equipped);
  }

  public boolean isTravelItemActiveForRendering(EntityPlayer ep) {
    if (ep == null) {
      return false;
    }
    return isTravelItemActive(ep, ep.getHeldItemMainhand()) || (Config.travelStaffOffhandShowsTravelTargets && isTravelItemActive(ep, ep.getHeldItemOffhand()));
  }

  private boolean isTravelItemActiveForSelecting(EntityPlayer ep) {
    if (ep == null) {
      return false;
    }
    return isTravelItemActive(ep, ep.getHeldItemMainhand()) || isTravelItemActive(ep, ep.getHeldItemOffhand());
  }

  private boolean isTravelItemActive(EntityPlayer ep, ItemStack equipped) {
    return equipped != null && equipped.getItem() instanceof IItemOfTravel && ((IItemOfTravel) equipped.getItem()).isActive(ep, equipped);
  }

  private boolean travelToSelectedTarget(EntityPlayer player, ItemStack equipped, EnumHand hand, TravelSource source, boolean conserveMomentum) {
    return travelToLocation(player, equipped, hand, source, selectedCoord, conserveMomentum);
  }

  private boolean travelToLocation(EntityPlayer player, ItemStack equipped, EnumHand hand, TravelSource source, BlockCoord coord, boolean conserveMomentum) {

    if(source != TravelSource.STAFF_BLINK) {
      TileEntity te = player.world.getTileEntity(coord.getBlockPos());
      if(te instanceof ITravelAccessable) {
        ITravelAccessable ta = (ITravelAccessable) te;
        if(!ta.canBlockBeAccessed(player)) {
          player.addChatComponentMessage(new TextComponentTranslation("enderio.gui.travelAccessable.unauthorised"));
          return false;
        }
      }
    }

    int requiredPower = 0;
    requiredPower = getRequiredPower(player, equipped, source, coord);
    if(requiredPower < 0) {
      return false;
    }

    if(!isInRangeTarget(player, coord, source.getMaxDistanceTravelledSq())) {
      if(source != TravelSource.STAFF_BLINK) {
        player.addChatComponentMessage(new TextComponentTranslation("enderio.blockTravelPlatform.outOfRange"));
      }
      return false;
    }
    if(!isValidTarget(player, coord, source)) {
      if(source != TravelSource.STAFF_BLINK) {
        player.addChatComponentMessage(new TextComponentTranslation("enderio.blockTravelPlatform.invalidTarget"));
      }
      return false;
    }
    if (doClientTeleport(player, hand, coord, source, requiredPower, conserveMomentum)) {
      for (int i = 0; i < 6; ++i) {
        player.world.spawnParticle(EnumParticleTypes.PORTAL, player.posX + (rand.nextDouble() - 0.5D), player.posY + rand.nextDouble() * player.height - 0.25D,
            player.posZ + (rand.nextDouble() - 0.5D), (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(),
            (rand.nextDouble() - 0.5D) * 2.0D);
      }
    }
    return true;
  }

  private int getRequiredPower(EntityPlayer player, ItemStack equipped, TravelSource source, BlockCoord coord) {
    if (!isTravelItemActive(player, equipped)) {
      return 0;
    }
    int requiredPower;
    requiredPower = (int) (getDistance(player, coord) * source.getPowerCostPerBlockTraveledRF());
    int canUsePower = getEnergyInTravelItem(equipped);
    if(requiredPower > canUsePower) {
      player.addChatComponentMessage(new TextComponentTranslation("enderio.itemTravelStaff.notEnoughPower"));
      return -1;
    }
    return requiredPower;
  }

  private boolean isInRangeTarget(EntityPlayer player, BlockCoord bc, float maxSq) {
    return getDistanceSquared(player, bc) <= maxSq;
  }

  private double getDistanceSquared(EntityPlayer player, BlockCoord bc) {
    if(player == null || bc == null) {
      return 0;
    }
    Vector3d eye = Util.getEyePositionEio(player);
    Vector3d target = new Vector3d(bc.x + 0.5, bc.y + 0.5, bc.z + 0.5);
    return eye.distanceSquared(target);
  }

  private double getDistance(EntityPlayer player, BlockCoord coord) {
    return Math.sqrt(getDistanceSquared(player, coord));
  }

  private boolean isValidTarget(EntityPlayer player, BlockCoord bc, TravelSource source) {
    if(bc == null) {
      return false;
    }
    World w = player.world;
    BlockCoord baseLoc = bc;
    if(source != TravelSource.STAFF_BLINK) {
      //targeting a block so go one up
      baseLoc = bc.getLocation(EnumFacing.UP);
    }

    return canTeleportTo(player, source, baseLoc, w)
        && canTeleportTo(player, source, baseLoc.getLocation(EnumFacing.UP), w);
  }

  private boolean canTeleportTo(EntityPlayer player, TravelSource source, BlockCoord bc, World w) {
    if(bc.y < 1) {
      return false;
    }
    if(source == TravelSource.STAFF_BLINK && !Config.travelStaffBlinkThroughSolidBlocksEnabled) {
      Vec3d start = Util.getEyePosition(player);
      Vec3d target = new Vec3d(bc.x + 0.5f, bc.y + 0.5f, bc.z + 0.5f);
      if(!canBlinkTo(bc, w, start, target)) {
        return false;
      }
    }

    IBlockState bs = w.getBlockState(bc.getBlockPos());
    Block block = bs.getBlock();
    if(block == null || block.isAir(bs, w, bc.getBlockPos())) {
      return true;
    }
        
    final AxisAlignedBB aabb = bs.getBoundingBox(w, bc.getBlockPos());
    return aabb == null || aabb.getAverageEdgeLength() < 0.7;
  }

  private boolean canBlinkTo(BlockCoord bc, World w, Vec3d start, Vec3d target) {
    RayTraceResult p = w.rayTraceBlocks(start, target, !Config.travelStaffBlinkThroughClearBlocksEnabled);
    if(p != null) {
      if(!Config.travelStaffBlinkThroughClearBlocksEnabled) {
        return false;
      }
      IBlockState bs = w.getBlockState(p.getBlockPos());
      Block block = bs.getBlock();
      if(isClear(w, bs, block, p.getBlockPos())) {
        if(new BlockCoord(p).equals(bc)) {
          return true;
        }
        //need to step
        Vector3d sv = new Vector3d(start.xCoord, start.yCoord, start.zCoord);
        Vector3d rayDir = new Vector3d(target.xCoord, target.yCoord, target.zCoord);
        rayDir.sub(sv);
        rayDir.normalize();
        rayDir.add(sv);
        return canBlinkTo(bc, w, new Vec3d(rayDir.x, rayDir.y, rayDir.z), target);

      } else {
        return false;
      }
    }
    return true;
  }

  private boolean isClear(World w, IBlockState bs, Block block, BlockPos bp) {
    if(block == null || block.isAir(bs, w, bp)) {
      return true;
    }
    final AxisAlignedBB aabb = bs.getBoundingBox(w, bp);
    if(aabb == null || aabb.getAverageEdgeLength() < 0.7) {
      return true;
    }

    return block.getLightOpacity(bs, w, bp) < 2;
  }

  @SideOnly(Side.CLIENT)
  private void updateVerticalTarget(EntityPlayerSP player, int direction) {

    BlockCoord currentBlock = getActiveTravelBlock(player);
    World world = Minecraft.getMinecraft().world;
    for (int i = 0, y = currentBlock.y + direction; i < Config.travelAnchorMaximumDistance && y >= 0 && y <= 255; i++, y += direction) {

      //Circumvents the raytracing used to find candidates on the y axis
      TileEntity selectedBlock = world.getTileEntity(new BlockPos(currentBlock.x, y, currentBlock.z));

      if(selectedBlock instanceof ITravelAccessable) {
        ITravelAccessable travelBlock = (ITravelAccessable) selectedBlock;
        BlockCoord targetBlock = new BlockCoord(currentBlock.x, y, currentBlock.z);

        if(Config.travelAnchorSkipWarning) {
          if(travelBlock.getRequiresPassword(player)) {
            player.addChatComponentMessage(new TextComponentTranslation("enderio.gui.travelAccessable.skipLocked"));
          }

          if(travelBlock.getAccessMode() == ITravelAccessable.AccessMode.PRIVATE && !travelBlock.canUiBeAccessed(player)) {
            player.addChatComponentMessage(new TextComponentTranslation("enderio.gui.travelAccessable.skipPrivate"));
          }
          if(!isValidTarget(player, targetBlock, TravelSource.BLOCK)) {
            player.addChatComponentMessage(new TextComponentTranslation("enderio.gui.travelAccessable.skipObstructed"));
          }
        }
        if(travelBlock.canBlockBeAccessed(player) && isValidTarget(player, targetBlock, TravelSource.BLOCK)) {
          selectedCoord = targetBlock;
          return;
        }
      }
    }
  }

  @SideOnly(Side.CLIENT)
  private void updateSelectedTarget(EntityPlayerSP player) {
    selectedCoord = null;
    if(candidates.isEmpty()) {
      return;
    }

    double closestDistance = Double.MAX_VALUE;
    for (BlockCoord bc : candidates.keySet()) {
      if(!bc.equals(onBlockCoord)) {

        double d = addRatio(bc);
        if(d < closestDistance) {
          selectedCoord = bc;
          closestDistance = d;
        }
      }
    }

    if(selectedCoord != null) {

      Vector3d blockCenter = new Vector3d(selectedCoord.x + 0.5, selectedCoord.y + 0.5, selectedCoord.z + 0.5);
      Vector2d blockCenterPixel = currentView.getScreenPoint(blockCenter);

      Vector2d screenMidPixel = new Vector2d(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
      screenMidPixel.scale(0.5);

      double pixDist = blockCenterPixel.distance(screenMidPixel);
      double rat = pixDist / Minecraft.getMinecraft().displayHeight;
      if(rat != rat) {
        rat = 0;
      }
      if(rat > 0.07) {
        selectedCoord = null;
      }

    }
  }

  @SideOnly(Side.CLIENT)
  private void onInput(EntityPlayerSP player) {

    MovementInput input = player.movementInput;
    BlockCoord target = TravelController.instance.selectedCoord;
    if(target == null) {
      return;
    }

    TileEntity te = player.world.getTileEntity(new BlockPos(target.x, target.y, target.z));
    if(te instanceof ITravelAccessable) {
      ITravelAccessable ta = (ITravelAccessable) te;
      if(ta.getRequiresPassword(player)) {
        PacketOpenAuthGui p = new PacketOpenAuthGui(target.getBlockPos());
        PacketHandler.INSTANCE.sendToServer(p);
        return;
      }
    }

    if (travelToSelectedTarget(player, null, null, TravelSource.BLOCK, false)) {
      input.jump = false;
      try {
        ObfuscationReflectionHelper.setPrivateValue(EntityPlayer.class, (EntityPlayer) player, 0, "flyToggleTimer", "field_71101_bC");
      } catch (Exception e) {
        //ignore
      }
    }

  }

  public double getScaleForCandidate(Vector3d loc) {

    if(!currentView.isValid()) {
      return 1;
    }

    BlockCoord bc = new BlockCoord(loc.x, loc.y, loc.z);
    float ratio = -1;
    Float r = candidates.get(bc);
    if(r != null) {
      ratio = r;
    }
    if(ratio < 0) {
      //no cached value
      addRatio(bc);
      ratio = candidates.get(bc);
    }

    //smoothly zoom to a larger size, starting when the point is the middle 20% of the screen
    float start = 0.2f;
    float end = 0.01f;
    double mix = MathHelper.clamp((start - ratio) / (start - end), 0, 1);
    double scale = 1;
    if(mix > 0) {

      Vector3d eyePoint = Util.getEyePositionEio(EnderIO.proxy.getClientPlayer());
      scale = tanFovRad * eyePoint.distance(loc);

      //Using this scale will give us the block full screen, we will make it 20% of the screen
      scale *= Config.travelAnchorZoomScale;

      //only apply 70% of the scaling so more distance targets are still smaller than closer targets
      float nf = 1 - MathHelper.clamp((float) eyePoint.distanceSquared(loc) / TravelSource.STAFF.getMaxDistanceTravelledSq(), 0, 1);
      scale = scale * (0.3 + 0.7 * nf);

      scale = (scale * mix) + (1 - mix);
      scale = Math.max(1.01, scale);

    }
    return scale;
  }

  private double addRatio(BlockCoord bc) {
    Vector2d sp = currentView.getScreenPoint(new Vector3d(bc.x + 0.5, bc.y + 0.5, bc.z + 0.5));
    Vector2d mid = new Vector2d(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
    mid.scale(0.5);
    double d = sp.distance(mid);
    if(d != d) {
      d = 0f;
    }
    float ratio = (float) d / Minecraft.getMinecraft().displayWidth;
    candidates.put(bc, ratio);
    return d;
  }

  // Note: This is restricted to the current player
  public boolean doClientTeleport(Entity entity, EnumHand hand, BlockCoord bc, TravelSource source, int powerUse, boolean conserveMomentum) {

    TeleportEntityEvent evt = new TeleportEntityEvent(entity, source, bc.x, bc.y, bc.z, entity.dimension);
    if(MinecraftForge.EVENT_BUS.post(evt)) {
      return false;
    }

    bc = new BlockCoord(evt.getTargetX(), evt.getTargetY(), evt.getTargetZ());
    PacketTravelEvent p = new PacketTravelEvent(entity, bc.x, bc.y, bc.z, powerUse, conserveMomentum, source, hand);
    PacketHandler.INSTANCE.sendToServer(p);
    return true;
  }

  @SideOnly(Side.CLIENT)
  private BlockCoord getActiveTravelBlock(EntityPlayerSP player) {
    World world = Minecraft.getMinecraft().world;
    if(world != null && player != null) {
      int x = MathHelper.floor(player.posX);
      int y = MathHelper.floor(player.getEntityBoundingBox().minY) - 1;
      int z = MathHelper.floor(player.posZ);
      final BlockPos pos = new BlockPos(x, y, z);
      final Block block = world.getBlockState(pos).getBlock();
      if (block instanceof BlockTravelAnchor) {
        if (Config.telepadIsTravelAnchor || block != blockTelePad.getBlock()) {
          return new BlockCoord(x, y, z);
        }
      }
    }
    return null;
  }

}
