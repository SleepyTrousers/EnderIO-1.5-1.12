package crazypants.enderio.base.farming;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSetMultimap;

import crazypants.enderio.base.machine.fakeplayer.FakePlayerEIO;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PickupWorld extends World {

  private final @Nonnull World wrapped;
  private final @Nonnull FakePlayerEIO player;

  public PickupWorld(@Nonnull World wrapped, @Nonnull FakePlayerEIO player) {
    super(wrapped.getSaveHandler(), wrapped.getWorldInfo(), wrapped.provider, wrapped.profiler, false);
    this.wrapped = wrapped;
    this.player = player;
  }

  @Override
  public boolean spawnEntity(@Nonnull Entity entityIn) {
    if (entityIn instanceof EntityItem) {
      final EntityItem entityItem = (EntityItem) entityIn;
      ItemStack itemstack = entityItem.getEntityItem();
      int hook = ForgeEventFactory.onItemPickup(entityItem, player, itemstack);
      if (hook >= 0) {
        if (hook == 1 || player.inventory.addItemStackToInventory(itemstack)) {
          FMLCommonHandler.instance().firePlayerItemPickupEvent(player, entityItem);

          if (Prep.isInvalid(itemstack)) {
            entityItem.setDead();
            return true;
          }
        }
      }
    }
    entityIn.world = wrapped;
    return wrapped.spawnEntity(entityIn);
  }

  // from here on: just relays to the wrapped world

  @Override
  protected @Nonnull IChunkProvider createChunkProvider() {
    return new IChunkProvider() {
      @Override
      public Chunk getLoadedChunk(int x, int z) {
        return new EmptyChunk(PickupWorld.this, x, z);
      }

      @Override
      public @Nonnull Chunk provideChunk(int x, int z) {
        return new EmptyChunk(PickupWorld.this, x, z);
      }

      @Override
      public boolean tick() {
        return false;
      }

      @Override
      public @Nonnull String makeString() {
        return "";
      }

      @Override
      public boolean isChunkGeneratedAt(int x, int z) {
        return false;
      }
    };
  }

  @Override
  protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
    return true;
  }

  @Override
  public @Nonnull World init() {
    return wrapped.init();
  }

  @Override
  public @Nonnull Biome getBiomeForCoordsBody(@Nonnull BlockPos pos) {
    return wrapped.getBiomeForCoordsBody(pos);
  }

  @Override
  public @Nonnull BiomeProvider getBiomeProvider() {
    return wrapped.getBiomeProvider();
  }

  @Override
  public void initialize(@Nonnull WorldSettings settings) {
    wrapped.initialize(settings);
  }

  @Override
  @Nullable
  public MinecraftServer getMinecraftServer() {
    return wrapped.getMinecraftServer();
  }

  @Override
  public void setInitialSpawnLocation() {
    wrapped.setInitialSpawnLocation();
  }

  @Override
  public @Nonnull IBlockState getGroundAboveSeaLevel(@Nonnull BlockPos pos) {
    return wrapped.getGroundAboveSeaLevel(pos);
  }

  @Override
  public boolean isAirBlock(@Nonnull BlockPos pos) {
    return wrapped.isAirBlock(pos);
  }

  @Override
  public boolean isBlockLoaded(@Nonnull BlockPos pos) {
    return wrapped.isBlockLoaded(pos);
  }

  @Override
  public boolean isBlockLoaded(@Nonnull BlockPos pos, boolean allowEmpty) {
    return wrapped.isBlockLoaded(pos, allowEmpty);
  }

  @Override
  public boolean isAreaLoaded(@Nonnull BlockPos center, int radius) {
    return wrapped.isAreaLoaded(center, radius);
  }

  @Override
  public boolean isAreaLoaded(@Nonnull BlockPos center, int radius, boolean allowEmpty) {
    return wrapped.isAreaLoaded(center, radius, allowEmpty);
  }

  @Override
  public boolean isAreaLoaded(@Nonnull BlockPos from, @Nonnull BlockPos to) {
    return wrapped.isAreaLoaded(from, to);
  }

  @Override
  public boolean isAreaLoaded(@Nonnull BlockPos from, @Nonnull BlockPos to, boolean allowEmpty) {
    return wrapped.isAreaLoaded(from, to, allowEmpty);
  }

  @Override
  public boolean isAreaLoaded(@Nonnull StructureBoundingBox box) {
    return wrapped.isAreaLoaded(box);
  }

  @Override
  public boolean isAreaLoaded(@Nonnull StructureBoundingBox box, boolean allowEmpty) {
    return wrapped.isAreaLoaded(box, allowEmpty);
  }

  @Override
  public @Nonnull Chunk getChunkFromBlockCoords(@Nonnull BlockPos pos) {
    return wrapped.getChunkFromBlockCoords(pos);
  }

  @Override
  public @Nonnull Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
    return wrapped.getChunkFromChunkCoords(chunkX, chunkZ);
  }

  @Override
  public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull IBlockState newState, int flags) {
    return wrapped.setBlockState(pos, newState, flags);
  }

  @Override
  public void markAndNotifyBlock(@Nonnull BlockPos pos, @Nullable Chunk chunk, @Nonnull IBlockState iblockstate, @Nonnull IBlockState newState, int flags) {
    wrapped.markAndNotifyBlock(pos, chunk, iblockstate, newState, flags);
  }

  @Override
  public boolean setBlockToAir(@Nonnull BlockPos pos) {
    return wrapped.setBlockToAir(pos);
  }

  @Override
  public boolean destroyBlock(@Nonnull BlockPos pos, boolean dropBlock) {
    return wrapped.destroyBlock(pos, dropBlock);
  }

  @Override
  public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull IBlockState state) {
    return wrapped.setBlockState(pos, state);
  }

  @Override
  public void notifyBlockUpdate(@Nonnull BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState, int flags) {
    wrapped.notifyBlockUpdate(pos, oldState, newState, flags);
  }

  @Override
  public void markBlocksDirtyVertical(int x1, int z1, int x2, int z2) {
    wrapped.markBlocksDirtyVertical(x1, z1, x2, z2);
  }

  @Override
  public void markBlockRangeForRenderUpdate(@Nonnull BlockPos rangeMin, @Nonnull BlockPos rangeMax) {
    wrapped.markBlockRangeForRenderUpdate(rangeMin, rangeMax);
  }

  @Override
  public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
    wrapped.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
  }

  @Override
  public void notifyNeighborsOfStateExcept(@Nonnull BlockPos pos, @Nonnull Block blockType, @Nonnull EnumFacing skipSide) {
    wrapped.notifyNeighborsOfStateExcept(pos, blockType, skipSide);
  }

  @Override
  public boolean isBlockTickPending(@Nonnull BlockPos pos, @Nonnull Block blockType) {
    return wrapped.isBlockTickPending(pos, blockType);
  }

  @Override
  public boolean canSeeSky(@Nonnull BlockPos pos) {
    return wrapped.canSeeSky(pos);
  }

  @Override
  public boolean canBlockSeeSky(@Nonnull BlockPos pos) {
    return wrapped.canBlockSeeSky(pos);
  }

  @Override
  public int getLight(@Nonnull BlockPos pos) {
    return wrapped.getLight(pos);
  }

  @Override
  public int getLightFromNeighbors(@Nonnull BlockPos pos) {
    return wrapped.getLightFromNeighbors(pos);
  }

  @Override
  public int getLight(@Nonnull BlockPos pos, boolean checkNeighbors) {
    return wrapped.getLight(pos, checkNeighbors);
  }

  @Override
  public @Nonnull BlockPos getHeight(@Nonnull BlockPos pos) {
    return wrapped.getHeight(pos);
  }

  @Override
  public int getChunksLowestHorizon(int x, int z) {
    return wrapped.getChunksLowestHorizon(x, z);
  }

  @Override
  public int getLightFromNeighborsFor(@Nonnull EnumSkyBlock type, @Nonnull BlockPos pos) {
    return wrapped.getLightFromNeighborsFor(type, pos);
  }

  @Override
  public int getLightFor(@Nonnull EnumSkyBlock type, @Nonnull BlockPos pos) {
    return wrapped.getLightFor(type, pos);
  }

  @Override
  public void setLightFor(@Nonnull EnumSkyBlock type, @Nonnull BlockPos pos, int lightValue) {
    wrapped.setLightFor(type, pos, lightValue);
  }

  @Override
  public void notifyLightSet(@Nonnull BlockPos pos) {
    wrapped.notifyLightSet(pos);
  }

  @Override
  public int getCombinedLight(@Nonnull BlockPos pos, int lightValue) {
    return wrapped.getCombinedLight(pos, lightValue);
  }

  @Override
  public float getLightBrightness(@Nonnull BlockPos pos) {
    return wrapped.getLightBrightness(pos);
  }

  @Override
  public @Nonnull IBlockState getBlockState(@Nonnull BlockPos pos) {
    return wrapped.getBlockState(pos);
  }

  @Override
  public boolean isDaytime() {
    return wrapped.isDaytime();
  }

  @Override
  @Nullable
  public RayTraceResult rayTraceBlocks(@Nonnull Vec3d start, @Nonnull Vec3d end) {
    return wrapped.rayTraceBlocks(start, end);
  }

  @Override
  @Nullable
  public RayTraceResult rayTraceBlocks(@Nonnull Vec3d start, @Nonnull Vec3d end, boolean stopOnLiquid) {
    return wrapped.rayTraceBlocks(start, end, stopOnLiquid);
  }

  @Override
  @Nullable
  public RayTraceResult rayTraceBlocks(@Nonnull Vec3d vec31, @Nonnull Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox,
      boolean returnLastUncollidableBlock) {
    return wrapped.rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
  }

  @Override
  public void playSound(@Nullable EntityPlayer player1, @Nonnull BlockPos pos, @Nonnull SoundEvent soundIn, @Nonnull SoundCategory category, float volume,
      float pitch) {
    wrapped.playSound(player1, pos, soundIn, category, volume, pitch);
  }

  @Override
  public void playSound(@Nullable EntityPlayer player1, double x, double y, double z, @Nonnull SoundEvent soundIn, @Nonnull SoundCategory category,
      float volume, float pitch) {
    wrapped.playSound(player1, x, y, z, soundIn, category, volume, pitch);
  }

  @Override
  public void playSound(double x, double y, double z, @Nonnull SoundEvent soundIn, @Nonnull SoundCategory category, float volume, float pitch,
      boolean distanceDelay) {
    wrapped.playSound(x, y, z, soundIn, category, volume, pitch, distanceDelay);
  }

  @Override
  public void playRecord(@Nonnull BlockPos blockPositionIn, @Nullable SoundEvent soundEventIn) {
    wrapped.playRecord(blockPositionIn, soundEventIn);
  }

  @Override
  public void spawnParticle(@Nonnull EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed,
      @Nonnull int... parameters) {
    wrapped.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
  }

  @Override
  public void spawnParticle(@Nonnull EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed,
      double ySpeed, double zSpeed, @Nonnull int... parameters) {
    wrapped.spawnParticle(particleType, ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
  }

  @Override
  public boolean addWeatherEffect(@Nonnull Entity entityIn) {
    return wrapped.addWeatherEffect(entityIn);
  }

  @Override
  public void onEntityAdded(@Nonnull Entity entityIn) {
    wrapped.onEntityAdded(entityIn);
  }

  @Override
  public void onEntityRemoved(@Nonnull Entity entityIn) {
    wrapped.onEntityRemoved(entityIn);
  }

  @Override
  public void removeEntity(@Nonnull Entity entityIn) {
    wrapped.removeEntity(entityIn);
  }

  @Override
  public void removeEntityDangerously(@Nonnull Entity entityIn) {
    wrapped.removeEntityDangerously(entityIn);
  }

  @Override
  public void addEventListener(@Nonnull IWorldEventListener listener) {
    wrapped.addEventListener(listener);
  }

  @Override
  public @Nonnull List<AxisAlignedBB> getCollisionBoxes(@Nullable Entity entityIn, @Nonnull AxisAlignedBB aabb) {
    return wrapped.getCollisionBoxes(entityIn, aabb);
  }

  @Override
  public void removeEventListener(@Nonnull IWorldEventListener listener) {
    wrapped.removeEventListener(listener);
  }

  @Override
  public boolean collidesWithAnyBlock(@Nonnull AxisAlignedBB bbox) {
    return wrapped.collidesWithAnyBlock(bbox);
  }

  @Override
  public int calculateSkylightSubtracted(float p_72967_1_) {
    return wrapped.calculateSkylightSubtracted(p_72967_1_);
  }

  @Override
  public float getSunBrightnessFactor(float p_72967_1_) {
    return wrapped.getSunBrightnessFactor(p_72967_1_);
  }

  @Override
  public float getSunBrightness(float p_72971_1_) {
    return wrapped.getSunBrightness(p_72971_1_);
  }

  @Override
  public float getSunBrightnessBody(float p_72971_1_) {
    return wrapped.getSunBrightnessBody(p_72971_1_);
  }

  @Override
  public @Nonnull Vec3d getSkyColor(@Nonnull Entity entityIn, float partialTicks) {
    return wrapped.getSkyColor(entityIn, partialTicks);
  }

  @Override
  public @Nonnull Vec3d getSkyColorBody(@Nonnull Entity entityIn, float partialTicks) {
    return wrapped.getSkyColorBody(entityIn, partialTicks);
  }

  @Override
  public float getCelestialAngle(float partialTicks) {
    return wrapped.getCelestialAngle(partialTicks);
  }

  @Override
  public int getMoonPhase() {
    return wrapped.getMoonPhase();
  }

  @Override
  public float getCurrentMoonPhaseFactor() {
    return wrapped.getCurrentMoonPhaseFactor();
  }

  @Override
  public float getCurrentMoonPhaseFactorBody() {
    return wrapped.getCurrentMoonPhaseFactorBody();
  }

  @Override
  public float getCelestialAngleRadians(float partialTicks) {
    return wrapped.getCelestialAngleRadians(partialTicks);
  }

  @Override
  public @Nonnull Vec3d getCloudColour(float partialTicks) {
    return wrapped.getCloudColour(partialTicks);
  }

  @Override
  public @Nonnull Vec3d getCloudColorBody(float partialTicks) {
    return wrapped.getCloudColorBody(partialTicks);
  }

  @Override
  public @Nonnull Vec3d getFogColor(float partialTicks) {
    return wrapped.getFogColor(partialTicks);
  }

  @Override
  public @Nonnull BlockPos getPrecipitationHeight(@Nonnull BlockPos pos) {
    return wrapped.getPrecipitationHeight(pos);
  }

  @Override
  public @Nonnull BlockPos getTopSolidOrLiquidBlock(@Nonnull BlockPos pos) {
    return wrapped.getTopSolidOrLiquidBlock(pos);
  }

  @Override
  public float getStarBrightness(float partialTicks) {
    return wrapped.getStarBrightness(partialTicks);
  }

  @Override
  public float getStarBrightnessBody(float partialTicks) {
    return wrapped.getStarBrightnessBody(partialTicks);
  }

  @Override
  public boolean isUpdateScheduled(@Nonnull BlockPos pos, @Nonnull Block blk) {
    return wrapped.isUpdateScheduled(pos, blk);
  }

  @Override
  public void scheduleUpdate(@Nonnull BlockPos pos, @Nonnull Block blockIn, int delay) {
    wrapped.scheduleUpdate(pos, blockIn, delay);
  }

  @Override
  public void updateBlockTick(@Nonnull BlockPos pos, @Nonnull Block blockIn, int delay, int priority) {
    wrapped.updateBlockTick(pos, blockIn, delay, priority);
  }

  @Override
  public void scheduleBlockUpdate(@Nonnull BlockPos pos, @Nonnull Block blockIn, int delay, int priority) {
    wrapped.scheduleBlockUpdate(pos, blockIn, delay, priority);
  }

  @Override
  public void updateEntities() {
    wrapped.updateEntities();
  }

  @Override
  protected void tickPlayers() {
  }

  @Override
  public boolean addTileEntity(@Nonnull TileEntity tile) {
    return wrapped.addTileEntity(tile);
  }

  @Override
  public void addTileEntities(@Nonnull Collection<TileEntity> tileEntityCollection) {
    wrapped.addTileEntities(tileEntityCollection);
  }

  @Override
  public void updateEntity(@Nonnull Entity ent) {
    wrapped.updateEntity(ent);
  }

  @Override
  public void updateEntityWithOptionalForce(@Nonnull Entity entityIn, boolean forceUpdate) {
    wrapped.updateEntityWithOptionalForce(entityIn, forceUpdate);
  }

  @Override
  public boolean checkNoEntityCollision(@Nonnull AxisAlignedBB bb) {
    return wrapped.checkNoEntityCollision(bb);
  }

  @Override
  public boolean checkNoEntityCollision(@Nonnull AxisAlignedBB bb, @Nullable Entity entityIn) {
    return wrapped.checkNoEntityCollision(bb, entityIn);
  }

  @Override
  public boolean checkBlockCollision(@Nonnull AxisAlignedBB bb) {
    return wrapped.checkBlockCollision(bb);
  }

  @Override
  public boolean containsAnyLiquid(@Nonnull AxisAlignedBB bb) {
    return wrapped.containsAnyLiquid(bb);
  }

  @Override
  public boolean isFlammableWithin(@Nonnull AxisAlignedBB bb) {
    return wrapped.isFlammableWithin(bb);
  }

  @Override
  public boolean handleMaterialAcceleration(@Nonnull AxisAlignedBB bb, @Nonnull Material materialIn, @Nonnull Entity entityIn) {
    return wrapped.handleMaterialAcceleration(bb, materialIn, entityIn);
  }

  @Override
  public boolean isMaterialInBB(@Nonnull AxisAlignedBB bb, @Nonnull Material materialIn) {
    return wrapped.isMaterialInBB(bb, materialIn);
  }

  @Override
  public @Nonnull Explosion createExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean isSmoking) {
    return wrapped.createExplosion(entityIn, x, y, z, strength, isSmoking);
  }

  @Override
  public @Nonnull Explosion newExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
    return wrapped.newExplosion(entityIn, x, y, z, strength, isFlaming, isSmoking);
  }

  @Override
  public float getBlockDensity(@Nonnull Vec3d vec, @Nonnull AxisAlignedBB bb) {
    return wrapped.getBlockDensity(vec, bb);
  }

  @Override
  public boolean extinguishFire(@Nullable EntityPlayer player1, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return wrapped.extinguishFire(player1, pos, side);
  }

  @Override
  public @Nonnull String getDebugLoadedEntities() {
    return wrapped.getDebugLoadedEntities();
  }

  @Override
  public @Nonnull String getProviderName() {
    return wrapped.getProviderName();
  }

  @Override
  @Nullable
  public TileEntity getTileEntity(@Nonnull BlockPos pos) {
    return wrapped.getTileEntity(pos);
  }

  @Override
  public void setTileEntity(@Nonnull BlockPos pos, @Nullable TileEntity tileEntityIn) {
    wrapped.setTileEntity(pos, tileEntityIn);
  }

  @Override
  public void removeTileEntity(@Nonnull BlockPos pos) {
    wrapped.removeTileEntity(pos);
  }

  @Override
  public void markTileEntityForRemoval(@Nonnull TileEntity tileEntityIn) {
    wrapped.markTileEntityForRemoval(tileEntityIn);
  }

  @Override
  public boolean isBlockFullCube(@Nonnull BlockPos pos) {
    return wrapped.isBlockFullCube(pos);
  }

  @Override
  public boolean isBlockNormalCube(@Nonnull BlockPos pos, boolean _default) {
    return wrapped.isBlockNormalCube(pos, _default);
  }

  @Override
  public void calculateInitialSkylight() {
    wrapped.calculateInitialSkylight();
  }

  @Override
  public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
    wrapped.setAllowedSpawnTypes(hostile, peaceful);
  }

  @Override
  public void tick() {
    wrapped.tick();
  }

  @Override
  protected void calculateInitialWeather() {
  }

  @Override
  public void calculateInitialWeatherBody() {
    wrapped.calculateInitialWeatherBody();
  }

  @Override
  protected void updateWeather() {
  }

  @Override
  public void updateWeatherBody() {
    wrapped.updateWeatherBody();
  }

  @Override
  protected void playMoodSoundAndCheckLight(int p_147467_1_, int p_147467_2_, @Nonnull Chunk chunkIn) {
  }

  @Override
  protected void updateBlocks() {
  }

  @Override
  public void immediateBlockTick(@Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
    wrapped.immediateBlockTick(pos, state, random);
  }

  @Override
  public boolean canBlockFreezeWater(@Nonnull BlockPos pos) {
    return wrapped.canBlockFreezeWater(pos);
  }

  @Override
  public boolean canBlockFreezeNoWater(@Nonnull BlockPos pos) {
    return wrapped.canBlockFreezeNoWater(pos);
  }

  @Override
  public boolean canBlockFreeze(@Nonnull BlockPos pos, boolean noWaterAdj) {
    return wrapped.canBlockFreeze(pos, noWaterAdj);
  }

  @Override
  public boolean canBlockFreezeBody(@Nonnull BlockPos pos, boolean noWaterAdj) {
    return wrapped.canBlockFreezeBody(pos, noWaterAdj);
  }

  @Override
  public boolean canSnowAt(@Nonnull BlockPos pos, boolean checkLight) {
    return wrapped.canSnowAt(pos, checkLight);
  }

  @Override
  public boolean canSnowAtBody(@Nonnull BlockPos pos, boolean checkLight) {
    return wrapped.canSnowAtBody(pos, checkLight);
  }

  @Override
  public boolean checkLight(@Nonnull BlockPos pos) {
    return wrapped.checkLight(pos);
  }

  @Override
  public boolean checkLightFor(@Nonnull EnumSkyBlock lightType, @Nonnull BlockPos pos) {
    return wrapped.checkLightFor(lightType, pos);
  }

  @Override
  public boolean tickUpdates(boolean p_72955_1_) {
    return wrapped.tickUpdates(p_72955_1_);
  }

  @Override
  @Nullable
  public List<NextTickListEntry> getPendingBlockUpdates(@Nonnull Chunk chunkIn, boolean p_72920_2_) {
    return wrapped.getPendingBlockUpdates(chunkIn, p_72920_2_);
  }

  @Override
  @Nullable
  public List<NextTickListEntry> getPendingBlockUpdates(@Nonnull StructureBoundingBox structureBB, boolean p_175712_2_) {
    return wrapped.getPendingBlockUpdates(structureBB, p_175712_2_);
  }

  @Override
  public @Nonnull List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, @Nonnull AxisAlignedBB bb) {
    return wrapped.getEntitiesWithinAABBExcludingEntity(entityIn, bb);
  }

  @Override
  public @Nonnull List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, @Nonnull AxisAlignedBB boundingBox,
      @Nullable Predicate<? super Entity> predicate) {
    return wrapped.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
  }

  @Override
  public @Nonnull <T extends Entity> List<T> getEntities(@Nonnull Class<? extends T> entityType, @Nonnull Predicate<? super T> filter) {
    return wrapped.getEntities(entityType, filter);
  }

  @Override
  public @Nonnull <T extends Entity> List<T> getPlayers(@Nonnull Class<? extends T> playerType, @Nonnull Predicate<? super T> filter) {
    return wrapped.getPlayers(playerType, filter);
  }

  @Override
  public @Nonnull <T extends Entity> List<T> getEntitiesWithinAABB(@Nonnull Class<? extends T> classEntity, @Nonnull AxisAlignedBB bb) {
    return wrapped.getEntitiesWithinAABB(classEntity, bb);
  }

  @Override
  public @Nonnull <T extends Entity> List<T> getEntitiesWithinAABB(@Nonnull Class<? extends T> clazz, @Nonnull AxisAlignedBB aabb,
      @Nullable Predicate<? super T> filter) {
    return wrapped.getEntitiesWithinAABB(clazz, aabb, filter);
  }

  @Override
  @Nullable
  public <T extends Entity> T findNearestEntityWithinAABB(@Nonnull Class<? extends T> entityType, @Nonnull AxisAlignedBB aabb, @Nonnull T closestTo) {
    return wrapped.findNearestEntityWithinAABB(entityType, aabb, closestTo);
  }

  @Override
  @Nullable
  public Entity getEntityByID(int id) {
    return wrapped.getEntityByID(id);
  }

  @Override
  public @Nonnull List<Entity> getLoadedEntityList() {
    return wrapped.getLoadedEntityList();
  }

  @Override
  public void markChunkDirty(@Nonnull BlockPos pos, @Nonnull TileEntity unusedTileEntity) {
    wrapped.markChunkDirty(pos, unusedTileEntity);
  }

  @Override
  public int countEntities(@Nonnull Class<?> entityType) {
    return wrapped.countEntities(entityType);
  }

  @Override
  public void loadEntities(@Nonnull Collection<Entity> entityCollection) {
    wrapped.loadEntities(entityCollection);
  }

  @Override
  public void unloadEntities(@Nonnull Collection<Entity> entityCollection) {
    wrapped.unloadEntities(entityCollection);
  }

  @Override
  public int getSeaLevel() {
    return wrapped.getSeaLevel();
  }

  @Override
  public void setSeaLevel(int seaLevelIn) {
    wrapped.setSeaLevel(seaLevelIn);
  }

  @Override
  public int getStrongPower(@Nonnull BlockPos pos, @Nonnull EnumFacing direction) {
    return wrapped.getStrongPower(pos, direction);
  }

  @Override
  public @Nonnull WorldType getWorldType() {
    return wrapped.getWorldType();
  }

  @Override
  public int getStrongPower(@Nonnull BlockPos pos) {
    return wrapped.getStrongPower(pos);
  }

  @Override
  public boolean isSidePowered(@Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return wrapped.isSidePowered(pos, side);
  }

  @Override
  public int getRedstonePower(@Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
    return wrapped.getRedstonePower(pos, facing);
  }

  @Override
  public boolean isBlockPowered(@Nonnull BlockPos pos) {
    return wrapped.isBlockPowered(pos);
  }

  @Override
  public int isBlockIndirectlyGettingPowered(@Nonnull BlockPos pos) {
    return wrapped.isBlockIndirectlyGettingPowered(pos);
  }

  @Override
  @Nullable
  public EntityPlayer getClosestPlayerToEntity(@Nonnull Entity entityIn, double distance) {
    return wrapped.getClosestPlayerToEntity(entityIn, distance);
  }

  @Override
  @Nullable
  public EntityPlayer getNearestPlayerNotCreative(@Nonnull Entity entityIn, double distance) {
    return wrapped.getNearestPlayerNotCreative(entityIn, distance);
  }

  @Override
  @Nullable
  public EntityPlayer getClosestPlayer(double posX, double posY, double posZ, double distance, boolean spectator) {
    return wrapped.getClosestPlayer(posX, posY, posZ, distance, spectator);
  }

  @Override
  public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range) {
    return wrapped.isAnyPlayerWithinRangeAt(x, y, z, range);
  }

  @Override
  @Nullable
  public EntityPlayer getNearestAttackablePlayer(@Nonnull Entity entityIn, double maxXZDistance, double maxYDistance) {
    return wrapped.getNearestAttackablePlayer(entityIn, maxXZDistance, maxYDistance);
  }

  @Override
  @Nullable
  public EntityPlayer getNearestAttackablePlayer(@Nonnull BlockPos pos, double maxXZDistance, double maxYDistance) {
    return wrapped.getNearestAttackablePlayer(pos, maxXZDistance, maxYDistance);
  }

  @Override
  @Nullable
  public EntityPlayer getNearestAttackablePlayer(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance,
      @Nullable Function<EntityPlayer, Double> playerToDouble, @Nullable Predicate<EntityPlayer> p_184150_12_) {
    return wrapped.getNearestAttackablePlayer(posX, posY, posZ, maxXZDistance, maxYDistance, playerToDouble, p_184150_12_);
  }

  @Override
  @Nullable
  public EntityPlayer getPlayerEntityByName(@Nonnull String name) {
    return wrapped.getPlayerEntityByName(name);
  }

  @Override
  @Nullable
  public EntityPlayer getPlayerEntityByUUID(@Nonnull UUID uuid) {
    return wrapped.getPlayerEntityByUUID(uuid);
  }

  @Override
  public void sendQuittingDisconnectingPacket() {
    wrapped.sendQuittingDisconnectingPacket();
  }

  @Override
  public void checkSessionLock() throws MinecraftException {
    wrapped.checkSessionLock();
  }

  @Override
  public void setTotalWorldTime(long worldTime) {
    wrapped.setTotalWorldTime(worldTime);
  }

  @Override
  public long getSeed() {
    return wrapped.getSeed();
  }

  @Override
  public long getTotalWorldTime() {
    return wrapped.getTotalWorldTime();
  }

  @Override
  public long getWorldTime() {
    return wrapped.getWorldTime();
  }

  @Override
  public void setWorldTime(long time) {
    wrapped.setWorldTime(time);
  }

  @Override
  public @Nonnull BlockPos getSpawnPoint() {
    return wrapped.getSpawnPoint();
  }

  @Override
  public void setSpawnPoint(@Nonnull BlockPos pos) {
    wrapped.setSpawnPoint(pos);
  }

  @Override
  public void joinEntityInSurroundings(@Nonnull Entity entityIn) {
    wrapped.joinEntityInSurroundings(entityIn);
  }

  @Override
  public boolean isBlockModifiable(@Nonnull EntityPlayer player1, @Nonnull BlockPos pos) {
    return wrapped.isBlockModifiable(player1, pos);
  }

  @Override
  public boolean canMineBlockBody(@Nonnull EntityPlayer player1, @Nonnull BlockPos pos) {
    return wrapped.canMineBlockBody(player1, pos);
  }

  @Override
  public void setEntityState(@Nonnull Entity entityIn, byte state) {
    wrapped.setEntityState(entityIn, state);
  }

  @Override
  public @Nonnull IChunkProvider getChunkProvider() {
    return wrapped.getChunkProvider();
  }

  @Override
  public void addBlockEvent(@Nonnull BlockPos pos, @Nonnull Block blockIn, int eventID, int eventParam) {
    wrapped.addBlockEvent(pos, blockIn, eventID, eventParam);
  }

  @Override
  public @Nonnull ISaveHandler getSaveHandler() {
    // value available from constructor
    return super.saveHandler;
  }

  @Override
  public @Nonnull WorldInfo getWorldInfo() {
    // value available from constructor
    return super.worldInfo;
  }

  @Override
  public @Nonnull GameRules getGameRules() {
    return wrapped.getGameRules();
  }

  @Override
  public void updateAllPlayersSleepingFlag() {
    wrapped.updateAllPlayersSleepingFlag();
  }

  @Override
  public float getThunderStrength(float delta) {
    return wrapped.getThunderStrength(delta);
  }

  @Override
  public void setThunderStrength(float strength) {
    wrapped.setThunderStrength(strength);
  }

  @Override
  public float getRainStrength(float delta) {
    return wrapped.getRainStrength(delta);
  }

  @Override
  public void setRainStrength(float strength) {
    wrapped.setRainStrength(strength);
  }

  @Override
  public boolean isThundering() {
    return wrapped.isThundering();
  }

  @Override
  public boolean isRaining() {
    return wrapped.isRaining();
  }

  @Override
  public boolean isRainingAt(@Nonnull BlockPos strikePosition) {
    return wrapped.isRainingAt(strikePosition);
  }

  @Override
  public boolean isBlockinHighHumidity(@Nonnull BlockPos pos) {
    return wrapped.isBlockinHighHumidity(pos);
  }

  @Override
  @Nullable
  public MapStorage getMapStorage() {
    return wrapped.getMapStorage();
  }

  @Override
  public int getUniqueDataId(@Nonnull String key) {
    return wrapped.getUniqueDataId(key);
  }

  @Override
  public void playBroadcastSound(int id, @Nonnull BlockPos pos, int data) {
    wrapped.playBroadcastSound(id, pos, data);
  }

  @Override
  public void playEvent(int type, @Nonnull BlockPos pos, int data) {
    wrapped.playEvent(type, pos, data);
  }

  @Override
  public void playEvent(@Nullable EntityPlayer player1, int type, @Nonnull BlockPos pos, int data) {
    wrapped.playEvent(player1, type, pos, data);
  }

  @Override
  public int getHeight() {
    return wrapped.getHeight();
  }

  @Override
  public int getActualHeight() {
    return wrapped.getActualHeight();
  }

  @Override
  public @Nonnull Random setRandomSeed(int p_72843_1_, int p_72843_2_, int p_72843_3_) {
    return wrapped.setRandomSeed(p_72843_1_, p_72843_2_, p_72843_3_);
  }

  @Override
  public @Nonnull CrashReportCategory addWorldInfoToCrashReport(@Nonnull CrashReport report) {
    return wrapped.addWorldInfoToCrashReport(report);
  }

  @Override
  public double getHorizon() {
    return wrapped.getHorizon();
  }

  @Override
  public void sendBlockBreakProgress(int breakerId, @Nonnull BlockPos pos, int progress) {
    wrapped.sendBlockBreakProgress(breakerId, pos, progress);
  }

  @Override
  public @Nonnull Calendar getCurrentDate() {
    return wrapped.getCurrentDate();
  }

  @Override
  public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable NBTTagCompound compund) {
    wrapped.makeFireworks(x, y, z, motionX, motionY, motionZ, compund);
  }

  @Override
  public @Nonnull Scoreboard getScoreboard() {
    return wrapped.getScoreboard();
  }

  @Override
  public void updateComparatorOutputLevel(@Nonnull BlockPos pos, @Nonnull Block blockIn) {
    wrapped.updateComparatorOutputLevel(pos, blockIn);
  }

  @Override
  public @Nonnull DifficultyInstance getDifficultyForLocation(@Nonnull BlockPos pos) {
    return wrapped.getDifficultyForLocation(pos);
  }

  @Override
  public @Nonnull EnumDifficulty getDifficulty() {
    return wrapped.getDifficulty();
  }

  @Override
  public int getSkylightSubtracted() {
    return wrapped.getSkylightSubtracted();
  }

  @Override
  public void setSkylightSubtracted(int newSkylightSubtracted) {
    wrapped.setSkylightSubtracted(newSkylightSubtracted);
  }

  @Override
  public int getLastLightningBolt() {
    return wrapped.getLastLightningBolt();
  }

  @Override
  public void setLastLightningBolt(int lastLightningBoltIn) {
    wrapped.setLastLightningBolt(lastLightningBoltIn);
  }

  @Override
  public @Nonnull VillageCollection getVillageCollection() {
    return wrapped.getVillageCollection();
  }

  @Override
  public @Nonnull WorldBorder getWorldBorder() {
    return wrapped.getWorldBorder();
  }

  @Override
  public boolean isSpawnChunk(int x, int z) {
    return wrapped.isSpawnChunk(x, z);
  }

  @Override
  public boolean isSideSolid(@Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return wrapped.isSideSolid(pos, side);
  }

  @Override
  public boolean isSideSolid(@Nonnull BlockPos pos, @Nonnull EnumFacing side, boolean _default) {
    return wrapped.isSideSolid(pos, side, _default);
  }

  @Override
  public @Nonnull ImmutableSetMultimap<ChunkPos, Ticket> getPersistentChunks() {
    return wrapped.getPersistentChunks();
  }

  @Override
  public @Nonnull Iterator<Chunk> getPersistentChunkIterable(@Nonnull Iterator<Chunk> chunkIterator) {
    return wrapped.getPersistentChunkIterable(chunkIterator);
  }

  @Override
  public int getBlockLightOpacity(@Nonnull BlockPos pos) {
    return wrapped.getBlockLightOpacity(pos);
  }

  @Override
  public int countEntities(@Nonnull EnumCreatureType type, boolean forSpawnCount) {
    return wrapped.countEntities(type, forSpawnCount);
  }

  @Override
  protected void initCapabilities() {
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    return wrapped.hasCapability(capability, facing);
  }

  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    return wrapped.getCapability(capability, facing);
  }

  @Override
  public @Nonnull MapStorage getPerWorldStorage() {
    return wrapped.getPerWorldStorage();
  }

  @Override
  public void sendPacketToServer(@Nonnull Packet<?> packetIn) {
    wrapped.sendPacketToServer(packetIn);
  }

  @Override
  public @Nonnull LootTableManager getLootTableManager() {
    return wrapped.getLootTableManager();
  }

  @Override
  public @Nonnull Biome getBiome(@Nonnull BlockPos pos) {
    return wrapped.getBiome(pos);
  }

  @Override
  public boolean isChunkGeneratedAt(int x, int z) {
    return wrapped.isChunkGeneratedAt(x, z);
  }

  @Override
  public void notifyNeighborsRespectDebug(@Nonnull BlockPos pos, @Nonnull Block blockType, boolean p_175722_3_) {
    wrapped.notifyNeighborsRespectDebug(pos, blockType, p_175722_3_);
  }

  @Override
  public void updateObservingBlocksAt(@Nonnull BlockPos pos, @Nonnull Block blockType) {
    wrapped.updateObservingBlocksAt(pos, blockType);
  }

  @Override
  public void notifyNeighborsOfStateChange(@Nonnull BlockPos pos, @Nonnull Block blockType, boolean updateObservers) {
    wrapped.notifyNeighborsOfStateChange(pos, blockType, updateObservers);
  }

  @Override
  public void neighborChanged(@Nonnull BlockPos pos, @Nonnull Block p_190524_2_, @Nonnull BlockPos p_190524_3_) {
    wrapped.neighborChanged(pos, p_190524_2_, p_190524_3_);
  }

  @Override
  public void observedNeighborChanged(@Nonnull BlockPos pos, @Nonnull Block p_190529_2_, @Nonnull BlockPos p_190529_3_) {
    wrapped.observedNeighborChanged(pos, p_190529_2_, p_190529_3_);
  }

  @Override
  public int getHeight(int x, int z) {
    return wrapped.getHeight(x, z);
  }

  @Override
  public void spawnAlwaysVisibleParticle(int p_190523_1_, double p_190523_2_, double p_190523_4_, double p_190523_6_, double p_190523_8_, double p_190523_10_,
      double p_190523_12_, @Nonnull int... p_190523_14_) {
    wrapped.spawnAlwaysVisibleParticle(p_190523_1_, p_190523_2_, p_190523_4_, p_190523_6_, p_190523_8_, p_190523_10_, p_190523_12_, p_190523_14_);
  }

  @Override
  public boolean func_191503_g(@Nonnull Entity p_191503_1_) {
    return wrapped.func_191503_g(p_191503_1_);
  }

  @Override
  public boolean mayPlace(@Nonnull Block blockIn, @Nonnull BlockPos pos, boolean p_190527_3_, @Nonnull EnumFacing sidePlacedOn, @Nullable Entity placer) {
    return wrapped.mayPlace(blockIn, pos, p_190527_3_, sidePlacedOn, placer);
  }

  @Override
  @Nullable
  public EntityPlayer getClosestPlayer(double x, double y, double z, double p_190525_7_, @Nonnull Predicate<Entity> p_190525_9_) {
    return wrapped.getClosestPlayer(x, y, z, p_190525_7_, p_190525_9_);
  }

  @Override
  public void setData(@Nonnull String dataID, @Nonnull WorldSavedData worldSavedDataIn) {
    wrapped.setData(dataID, worldSavedDataIn);
  }

  @Override
  @Nullable
  public WorldSavedData loadData(@Nonnull Class<? extends WorldSavedData> clazz, @Nonnull String dataID) {
    return wrapped.loadData(clazz, dataID);
  }

  @Override
  @Nullable
  public BlockPos findNearestStructure(@Nonnull String p_190528_1_, @Nonnull BlockPos p_190528_2_, boolean p_190528_3_) {
    return wrapped.findNearestStructure(p_190528_1_, p_190528_2_, p_190528_3_);
  }

}
