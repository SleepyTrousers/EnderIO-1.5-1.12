package crazypants.enderio.machines.machine.farm;

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
    super(null, null, wrapped.provider, null, false);
    this.wrapped = wrapped;
    this.player = player;
  }

  @Override
  public boolean spawnEntity(Entity entityIn) {
    if (entityIn instanceof EntityItem) {
      final EntityItem entityItem = (EntityItem) entityIn;
      ItemStack itemstack = entityItem.getEntityItem();
      int hook = ForgeEventFactory.onItemPickup(entityItem, player, itemstack);
      if (hook >= 0) {
        if (hook == 1 || player.inventory.addItemStackToInventory(itemstack)) {
          FMLCommonHandler.instance().firePlayerItemPickupEvent(player, entityItem);

          if (itemstack.getCount() <= 0) {
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
  protected IChunkProvider createChunkProvider() {
    return null; // FIXME
  }

  @Override
  protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
    return true;
  }

  @Override
  public World init() {
    return wrapped.init();
  }

  @Override
  public Biome getBiome(BlockPos pos) {
    return wrapped.getBiome(pos);
  }

  @Override
  public Biome getBiomeForCoordsBody(BlockPos pos) {
    return wrapped.getBiomeForCoordsBody(pos);
  }

  @Override
  public BiomeProvider getBiomeProvider() {
    return wrapped.getBiomeProvider();
  }

  @Override
  public void initialize(WorldSettings settings) {
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
  public IBlockState getGroundAboveSeaLevel(BlockPos pos) {
    return wrapped.getGroundAboveSeaLevel(pos);
  }

  @Override
  public boolean isAirBlock(BlockPos pos) {
    return wrapped.isAirBlock(pos);
  }

  @Override
  public boolean isBlockLoaded(BlockPos pos) {
    return wrapped.isBlockLoaded(pos);
  }

  @Override
  public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty) {
    return wrapped.isBlockLoaded(pos, allowEmpty);
  }

  @Override
  public boolean isAreaLoaded(BlockPos center, int radius) {
    return wrapped.isAreaLoaded(center, radius);
  }

  @Override
  public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty) {
    return wrapped.isAreaLoaded(center, radius, allowEmpty);
  }

  @Override
  public boolean isAreaLoaded(BlockPos from, BlockPos to) {
    return wrapped.isAreaLoaded(from, to);
  }

  @Override
  public boolean isAreaLoaded(BlockPos from, BlockPos to, boolean allowEmpty) {
    return wrapped.isAreaLoaded(from, to, allowEmpty);
  }

  @Override
  public boolean isAreaLoaded(StructureBoundingBox box) {
    return wrapped.isAreaLoaded(box);
  }

  @Override
  public boolean isAreaLoaded(StructureBoundingBox box, boolean allowEmpty) {
    return wrapped.isAreaLoaded(box, allowEmpty);
  }

  @Override
  public Chunk getChunkFromBlockCoords(BlockPos pos) {
    return wrapped.getChunkFromBlockCoords(pos);
  }

  @Override
  public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
    return wrapped.getChunkFromChunkCoords(chunkX, chunkZ);
  }

  @Override
  public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
    return wrapped.setBlockState(pos, newState, flags);
  }

  @Override
  public void markAndNotifyBlock(BlockPos pos, Chunk chunk, IBlockState iblockstate, IBlockState newState, int flags) {
    wrapped.markAndNotifyBlock(pos, chunk, iblockstate, newState, flags);
  }

  @Override
  public boolean setBlockToAir(BlockPos pos) {
    return wrapped.setBlockToAir(pos);
  }

  @Override
  public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
    return wrapped.destroyBlock(pos, dropBlock);
  }

  @Override
  public boolean setBlockState(BlockPos pos, IBlockState state) {
    return wrapped.setBlockState(pos, state);
  }

  @Override
  public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
    wrapped.notifyBlockUpdate(pos, oldState, newState, flags);
  }
  
  @Override
  public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType, boolean p_175722_3_) {
    wrapped.notifyNeighborsRespectDebug(pos, blockType, p_175722_3_);
  }

  @Override
  public void markBlocksDirtyVertical(int x1, int z1, int x2, int z2) {
    wrapped.markBlocksDirtyVertical(x1, z1, x2, z2);
  }

  @Override
  public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax) {
    wrapped.markBlockRangeForRenderUpdate(rangeMin, rangeMax);
  }

  @Override
  public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
    wrapped.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
  }

  @Override
  public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType, boolean updateObservers) {
    wrapped.notifyNeighborsOfStateChange(pos, blockType, updateObservers);
  }

  @Override
  public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
    wrapped.notifyNeighborsOfStateExcept(pos, blockType, skipSide);
  }

  @Override
  public boolean isBlockTickPending(BlockPos pos, Block blockType) {
    return wrapped.isBlockTickPending(pos, blockType);
  }

  @Override
  public boolean canSeeSky(BlockPos pos) {
    return wrapped.canSeeSky(pos);
  }

  @Override
  public boolean canBlockSeeSky(BlockPos pos) {
    return wrapped.canBlockSeeSky(pos);
  }

  @Override
  public int getLight(BlockPos pos) {
    return wrapped.getLight(pos);
  }

  @Override
  public int getLightFromNeighbors(BlockPos pos) {
    return wrapped.getLightFromNeighbors(pos);
  }

  @Override
  public int getLight(BlockPos pos, boolean checkNeighbors) {
    return wrapped.getLight(pos, checkNeighbors);
  }

  @Override
  public BlockPos getHeight(BlockPos pos) {
    return wrapped.getHeight(pos);
  }

  @Override
  public int getHeight(int p_189649_1_, int p_189649_2_) {
    return wrapped.getHeight(p_189649_1_, p_189649_2_);
  }

  @Override
  public int getChunksLowestHorizon(int x, int z) {
    return wrapped.getChunksLowestHorizon(x, z);
  }

  @Override
  public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos) {
    return wrapped.getLightFromNeighborsFor(type, pos);
  }

  @Override
  public int getLightFor(EnumSkyBlock type, BlockPos pos) {
    return wrapped.getLightFor(type, pos);
  }

  @Override
  public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue) {
    wrapped.setLightFor(type, pos, lightValue);
  }

  @Override
  public void notifyLightSet(BlockPos pos) {
    wrapped.notifyLightSet(pos);
  }

  @Override
  public int getCombinedLight(BlockPos pos, int lightValue) {
    return wrapped.getCombinedLight(pos, lightValue);
  }

  @Override
  public float getLightBrightness(BlockPos pos) {
    return wrapped.getLightBrightness(pos);
  }

  @Override
  public IBlockState getBlockState(BlockPos pos) {
    return wrapped.getBlockState(pos);
  }

  @Override
  public boolean isDaytime() {
    return wrapped.isDaytime();
  }

  @Override
  @Nullable
  public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end) {
    return wrapped.rayTraceBlocks(start, end);
  }

  @Override
  @Nullable
  public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end, boolean stopOnLiquid) {
    return wrapped.rayTraceBlocks(start, end, stopOnLiquid);
  }

  @Override
  @Nullable
  public RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox,
      boolean returnLastUncollidableBlock) {
    return wrapped.rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
  }

  @Override
  public void playSound(@Nullable EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
    wrapped.playSound(player, pos, soundIn, category, volume, pitch);
  }

  @Override
  public void playSound(@Nullable EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
    wrapped.playSound(player, x, y, z, soundIn, category, volume, pitch);
  }

  @Override
  public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
    wrapped.playSound(x, y, z, soundIn, category, volume, pitch, distanceDelay);
  }

  @Override
  public void playRecord(BlockPos blockPositionIn, @Nullable SoundEvent soundEventIn) {
    wrapped.playRecord(blockPositionIn, soundEventIn);
  }

  @Override
  public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed,
      int... parameters) {
    wrapped.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
  }

  @Override
  public void spawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed,
      double zSpeed, int... parameters) {
    wrapped.spawnParticle(particleType, ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
  }

  @Override
  public boolean addWeatherEffect(Entity entityIn) {
    return wrapped.addWeatherEffect(entityIn);
  }

  @Override
  public void onEntityAdded(Entity entityIn) {
    wrapped.onEntityAdded(entityIn);
  }

  @Override
  public void onEntityRemoved(Entity entityIn) {
    wrapped.onEntityRemoved(entityIn);
  }

  @Override
  public void removeEntity(Entity entityIn) {
    wrapped.removeEntity(entityIn);
  }

  @Override
  public void removeEntityDangerously(Entity entityIn) {
    wrapped.removeEntityDangerously(entityIn);
  }

  @Override
  public void addEventListener(IWorldEventListener listener) {
    wrapped.addEventListener(listener);
  }

  @Override
  public List<AxisAlignedBB> getCollisionBoxes(@Nullable Entity entityIn, AxisAlignedBB aabb) {
    return wrapped.getCollisionBoxes(entityIn, aabb);
  }
  
  @Override
  public boolean /*isInsideWorldBorder*/ func_191503_g(Entity p_191503_1_) {
    return wrapped.func_191503_g(p_191503_1_);
  }

  @Override
  public void removeEventListener(IWorldEventListener listener) {
    wrapped.removeEventListener(listener);
  }

  @Override
  public boolean collidesWithAnyBlock(AxisAlignedBB bbox) {
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
  public Vec3d getSkyColor(Entity entityIn, float partialTicks) {
    return wrapped.getSkyColor(entityIn, partialTicks);
  }

  @Override
  public Vec3d getSkyColorBody(Entity entityIn, float partialTicks) {
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
  public Vec3d getCloudColour(float partialTicks) {
    return wrapped.getCloudColour(partialTicks);
  }

  @Override
  public Vec3d getCloudColorBody(float partialTicks) {
    return wrapped.getCloudColorBody(partialTicks);
  }

  @Override
  public Vec3d getFogColor(float partialTicks) {
    return wrapped.getFogColor(partialTicks);
  }

  @Override
  public BlockPos getPrecipitationHeight(BlockPos pos) {
    return wrapped.getPrecipitationHeight(pos);
  }

  @Override
  public BlockPos getTopSolidOrLiquidBlock(BlockPos pos) {
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
  public boolean isUpdateScheduled(BlockPos pos, Block blk) {
    return wrapped.isUpdateScheduled(pos, blk);
  }

  @Override
  public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
    wrapped.scheduleUpdate(pos, blockIn, delay);
  }

  @Override
  public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority) {
    wrapped.updateBlockTick(pos, blockIn, delay, priority);
  }

  @Override
  public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority) {
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
  public boolean addTileEntity(TileEntity tile) {
    return wrapped.addTileEntity(tile);
  }

  @Override
  public void addTileEntities(Collection<TileEntity> tileEntityCollection) {
    wrapped.addTileEntities(tileEntityCollection);
  }

  @Override
  public void updateEntity(Entity ent) {
    wrapped.updateEntity(ent);
  }

  @Override
  public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate) {
    wrapped.updateEntityWithOptionalForce(entityIn, forceUpdate);
  }

  @Override
  public boolean checkNoEntityCollision(AxisAlignedBB bb) {
    return wrapped.checkNoEntityCollision(bb);
  }

  @Override
  public boolean checkNoEntityCollision(AxisAlignedBB bb, @Nullable Entity entityIn) {
    return wrapped.checkNoEntityCollision(bb, entityIn);
  }

  @Override
  public boolean checkBlockCollision(AxisAlignedBB bb) {
    return wrapped.checkBlockCollision(bb);
  }

  @Override
  public boolean containsAnyLiquid(AxisAlignedBB bb) {
    return wrapped.containsAnyLiquid(bb);
  }

  @Override
  public boolean isFlammableWithin(AxisAlignedBB bb) {
    return wrapped.isFlammableWithin(bb);
  }

  @Override
  public boolean handleMaterialAcceleration(AxisAlignedBB bb, Material materialIn, Entity entityIn) {
    return wrapped.handleMaterialAcceleration(bb, materialIn, entityIn);
  }

  @Override
  public boolean isMaterialInBB(AxisAlignedBB bb, Material materialIn) {
    return wrapped.isMaterialInBB(bb, materialIn);
  }

  @Override
  public Explosion createExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean isSmoking) {
    return wrapped.createExplosion(entityIn, x, y, z, strength, isSmoking);
  }

  @Override
  public Explosion newExplosion(@Nullable Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
    return wrapped.newExplosion(entityIn, x, y, z, strength, isFlaming, isSmoking);
  }

  @Override
  public float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
    return wrapped.getBlockDensity(vec, bb);
  }

  @Override
  public boolean extinguishFire(@Nullable EntityPlayer player, BlockPos pos, EnumFacing side) {
    return wrapped.extinguishFire(player, pos, side);
  }

  @Override
  public String getDebugLoadedEntities() {
    return wrapped.getDebugLoadedEntities();
  }

  @Override
  public String getProviderName() {
    return wrapped.getProviderName();
  }

  @Override
  @Nullable
  public TileEntity getTileEntity(BlockPos pos) {
    return wrapped.getTileEntity(pos);
  }

  @Override
  public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn) {
    wrapped.setTileEntity(pos, tileEntityIn);
  }

  @Override
  public void removeTileEntity(BlockPos pos) {
    wrapped.removeTileEntity(pos);
  }

  @Override
  public void markTileEntityForRemoval(TileEntity tileEntityIn) {
    wrapped.markTileEntityForRemoval(tileEntityIn);
  }

  @Override
  public boolean isBlockFullCube(BlockPos pos) {
    return wrapped.isBlockFullCube(pos);
  }

  @Override
  public boolean isBlockNormalCube(BlockPos pos, boolean _default) {
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
  protected void playMoodSoundAndCheckLight(int p_147467_1_, int p_147467_2_, Chunk chunkIn) {
  }

  @Override
  protected void updateBlocks() {
  }

  @Override
  public void immediateBlockTick(BlockPos pos, IBlockState state, Random random) {
    wrapped.immediateBlockTick(pos, state, random);
  }

  @Override
  public boolean canBlockFreezeWater(BlockPos pos) {
    return wrapped.canBlockFreezeWater(pos);
  }

  @Override
  public boolean canBlockFreezeNoWater(BlockPos pos) {
    return wrapped.canBlockFreezeNoWater(pos);
  }

  @Override
  public boolean canBlockFreeze(BlockPos pos, boolean noWaterAdj) {
    return wrapped.canBlockFreeze(pos, noWaterAdj);
  }

  @Override
  public boolean canBlockFreezeBody(BlockPos pos, boolean noWaterAdj) {
    return wrapped.canBlockFreezeBody(pos, noWaterAdj);
  }

  @Override
  public boolean canSnowAt(BlockPos pos, boolean checkLight) {
    return wrapped.canSnowAt(pos, checkLight);
  }

  @Override
  public boolean canSnowAtBody(BlockPos pos, boolean checkLight) {
    return wrapped.canSnowAtBody(pos, checkLight);
  }

  @Override
  public boolean checkLight(BlockPos pos) {
    return wrapped.checkLight(pos);
  }

  @Override
  public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos) {
    return wrapped.checkLightFor(lightType, pos);
  }

  @Override
  public boolean tickUpdates(boolean p_72955_1_) {
    return wrapped.tickUpdates(p_72955_1_);
  }

  @Override
  @Nullable
  public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunkIn, boolean p_72920_2_) {
    return wrapped.getPendingBlockUpdates(chunkIn, p_72920_2_);
  }

  @Override
  @Nullable
  public List<NextTickListEntry> getPendingBlockUpdates(StructureBoundingBox structureBB, boolean p_175712_2_) {
    return wrapped.getPendingBlockUpdates(structureBB, p_175712_2_);
  }

  @Override
  public List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, AxisAlignedBB bb) {
    return wrapped.getEntitiesWithinAABBExcludingEntity(entityIn, bb);
  }

  @Override
  public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
    return wrapped.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
  }

  @Override
  public <T extends Entity> List<T> getEntities(Class<? extends T> entityType, Predicate<? super T> filter) {
    return wrapped.getEntities(entityType, filter);
  }

  @Override
  public <T extends Entity> List<T> getPlayers(Class<? extends T> playerType, Predicate<? super T> filter) {
    return wrapped.getPlayers(playerType, filter);
  }

  @Override
  public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> classEntity, AxisAlignedBB bb) {
    return wrapped.getEntitiesWithinAABB(classEntity, bb);
  }

  @Override
  public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable Predicate<? super T> filter) {
    return wrapped.getEntitiesWithinAABB(clazz, aabb, filter);
  }

  @Override
  @Nullable
  public <T extends Entity> T findNearestEntityWithinAABB(Class<? extends T> entityType, AxisAlignedBB aabb, T closestTo) {
    return wrapped.findNearestEntityWithinAABB(entityType, aabb, closestTo);
  }

  @Override
  @Nullable
  public Entity getEntityByID(int id) {
    return wrapped.getEntityByID(id);
  }

  @Override
  public List<Entity> getLoadedEntityList() {
    return wrapped.getLoadedEntityList();
  }

  @Override
  public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
    wrapped.markChunkDirty(pos, unusedTileEntity);
  }

  @Override
  public int countEntities(Class<?> entityType) {
    return wrapped.countEntities(entityType);
  }

  @Override
  public void loadEntities(Collection<Entity> entityCollection) {
    wrapped.loadEntities(entityCollection);
  }

  @Override
  public void unloadEntities(Collection<Entity> entityCollection) {
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
  public int getStrongPower(BlockPos pos, EnumFacing direction) {
    return wrapped.getStrongPower(pos, direction);
  }

  @Override
  public WorldType getWorldType() {
    return wrapped.getWorldType();
  }

  @Override
  public int getStrongPower(BlockPos pos) {
    return wrapped.getStrongPower(pos);
  }

  @Override
  public boolean isSidePowered(BlockPos pos, EnumFacing side) {
    return wrapped.isSidePowered(pos, side);
  }

  @Override
  public int getRedstonePower(BlockPos pos, EnumFacing facing) {
    return wrapped.getRedstonePower(pos, facing);
  }

  @Override
  public boolean isBlockPowered(BlockPos pos) {
    return wrapped.isBlockPowered(pos);
  }

  @Override
  public int isBlockIndirectlyGettingPowered(BlockPos pos) {
    return wrapped.isBlockIndirectlyGettingPowered(pos);
  }

  @Override
  @Nullable
  public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance) {
    return wrapped.getClosestPlayerToEntity(entityIn, distance);
  }

  @Override
  @Nullable
  public EntityPlayer getNearestPlayerNotCreative(Entity entityIn, double distance) {
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
  public EntityPlayer getNearestAttackablePlayer(Entity entityIn, double maxXZDistance, double maxYDistance) {
    return wrapped.getNearestAttackablePlayer(entityIn, maxXZDistance, maxYDistance);
  }

  @Override
  @Nullable
  public EntityPlayer getNearestAttackablePlayer(BlockPos pos, double maxXZDistance, double maxYDistance) {
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
  public EntityPlayer getPlayerEntityByName(String name) {
    return wrapped.getPlayerEntityByName(name);
  }

  @Override
  @Nullable
  public EntityPlayer getPlayerEntityByUUID(UUID uuid) {
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
  public BlockPos getSpawnPoint() {
    return wrapped.getSpawnPoint();
  }

  @Override
  public void setSpawnPoint(BlockPos pos) {
    wrapped.setSpawnPoint(pos);
  }

  @Override
  public void joinEntityInSurroundings(Entity entityIn) {
    wrapped.joinEntityInSurroundings(entityIn);
  }

  @Override
  public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
    return wrapped.isBlockModifiable(player, pos);
  }

  @Override
  public boolean canMineBlockBody(EntityPlayer player, BlockPos pos) {
    return wrapped.canMineBlockBody(player, pos);
  }

  @Override
  public void setEntityState(Entity entityIn, byte state) {
    wrapped.setEntityState(entityIn, state);
  }

  @Override
  public IChunkProvider getChunkProvider() {
    return wrapped.getChunkProvider();
  }

  @Override
  public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
    wrapped.addBlockEvent(pos, blockIn, eventID, eventParam);
  }

  @Override
  public ISaveHandler getSaveHandler() {
    return wrapped.getSaveHandler();
  }

  @Override
  public WorldInfo getWorldInfo() {
    return wrapped.getWorldInfo();
  }

  @Override
  public GameRules getGameRules() {
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
  public boolean isRainingAt(BlockPos strikePosition) {
    return wrapped.isRainingAt(strikePosition);
  }

  @Override
  public boolean isBlockinHighHumidity(BlockPos pos) {
    return wrapped.isBlockinHighHumidity(pos);
  }

  @Override
  @Nullable
  public MapStorage getMapStorage() {
    return wrapped.getMapStorage();
  }

  @Override
  public void setData(String dataID, WorldSavedData worldSavedDataIn) {
    wrapped.setData(dataID, worldSavedDataIn);
  }

  @Override
  public int getUniqueDataId(String key) {
    return wrapped.getUniqueDataId(key);
  }

  @Override
  public void playBroadcastSound(int id, BlockPos pos, int data) {
    wrapped.playBroadcastSound(id, pos, data);
  }

  @Override
  public void playEvent(int type, BlockPos pos, int data) {
    wrapped.playEvent(type, pos, data);
  }

  @Override
  public void playEvent(@Nullable EntityPlayer player, int type, BlockPos pos, int data) {
    wrapped.playEvent(player, type, pos, data);
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
  public Random setRandomSeed(int p_72843_1_, int p_72843_2_, int p_72843_3_) {
    return wrapped.setRandomSeed(p_72843_1_, p_72843_2_, p_72843_3_);
  }

  @Override
  public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
    return wrapped.addWorldInfoToCrashReport(report);
  }

  @Override
  public double getHorizon() {
    return wrapped.getHorizon();
  }

  @Override
  public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
    wrapped.sendBlockBreakProgress(breakerId, pos, progress);
  }

  @Override
  public Calendar getCurrentDate() {
    return wrapped.getCurrentDate();
  }

  @Override
  public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable NBTTagCompound compund) {
    wrapped.makeFireworks(x, y, z, motionX, motionY, motionZ, compund);
  }

  @Override
  public Scoreboard getScoreboard() {
    return wrapped.getScoreboard();
  }

  @Override
  public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) {
    wrapped.updateComparatorOutputLevel(pos, blockIn);
  }

  @Override
  public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
    return wrapped.getDifficultyForLocation(pos);
  }

  @Override
  public EnumDifficulty getDifficulty() {
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
  public VillageCollection getVillageCollection() {
    return wrapped.getVillageCollection();
  }

  @Override
  public WorldBorder getWorldBorder() {
    return wrapped.getWorldBorder();
  }

  @Override
  public boolean isSpawnChunk(int x, int z) {
    return wrapped.isSpawnChunk(x, z);
  }

  @Override
  public boolean isSideSolid(BlockPos pos, EnumFacing side) {
    return wrapped.isSideSolid(pos, side);
  }

  @Override
  public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
    return wrapped.isSideSolid(pos, side, _default);
  }

  @Override
  public ImmutableSetMultimap<ChunkPos, Ticket> getPersistentChunks() {
    return wrapped.getPersistentChunks();
  }

  @Override
  public Iterator<Chunk> getPersistentChunkIterable(Iterator<Chunk> chunkIterator) {
    return wrapped.getPersistentChunkIterable(chunkIterator);
  }

  @Override
  public int getBlockLightOpacity(BlockPos pos) {
    return wrapped.getBlockLightOpacity(pos);
  }

  @Override
  public int countEntities(EnumCreatureType type, boolean forSpawnCount) {
    return wrapped.countEntities(type, forSpawnCount);
  }

  @Override
  protected void initCapabilities() {
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    return wrapped.hasCapability(capability, facing);
  }

  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    return wrapped.getCapability(capability, facing);
  }

  @Override
  public MapStorage getPerWorldStorage() {
    return wrapped.getPerWorldStorage();
  }

  @Override
  public void sendPacketToServer(Packet<?> packetIn) {
    wrapped.sendPacketToServer(packetIn);
  }

  @Override
  public LootTableManager getLootTableManager() {
    return wrapped.getLootTableManager();
  }

}
