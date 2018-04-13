package crazypants.enderio.machines.machine.vacuum;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.NBTAction;
import com.enderio.core.common.inventory.Callback;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.Filters.PredicateItemStack;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.MagnetUtil;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.capability.ItemTools;
import crazypants.enderio.base.filter.FilterHandler;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.ITileFilterContainer;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.base.filter.item.items.IItemFilterItemUpgrade;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.enderio.base.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.render.ranged.IRanged;
import crazypants.enderio.base.render.ranged.RangeParticle;
import crazypants.enderio.machines.config.config.VacuumConfig;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

@Storable
public class TileVacuumChest extends AbstractCapabilityMachineEntity
    implements IRedstoneModeControlable, IPaintable.IPaintableTileEntity, IRanged, ITileFilterContainer {

  private static PredicateItemStack PREDICATE_FILTER = new PredicateItemStack() {
    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return input.getItem() instanceof IItemFilterItemUpgrade;
    }
  };

  private Callback<ItemStack> CALLBACK_FILTER = new Callback<ItemStack>() {
    @Override
    public void onChange(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack) {
      if (filter != null) {
        FilterRegistry.writeFilterToStack(filter, oldStack);
      }
      filter = FilterRegistry.<IItemFilter> getFilterForUpgrade(newStack);
      forceUpdatePlayers();
    }
  };

  public static final int ITEM_ROWS = 3;
  public static final int ITEM_COLS = 9;
  public static final int ITEM_SLOTS = ITEM_COLS * ITEM_ROWS;
  public static final int FILTER_SLOTS = 5;

  @Store
  private int range = VacuumConfig.vacuumChestRange.get();

  @Store(handler = FilterHandler.class)
  private IItemFilter filter;

  @Store({ NBTAction.CLIENT })
  private boolean clientActive;

  public TileVacuumChest() {
    for (int i = 0; i < ITEM_SLOTS; i++) {
      getInventory().add(EnderInventory.Type.INOUT, "slot" + i, new InventorySlot());
    }

    getInventory().add(EnderInventory.Type.UPGRADE, "filter", new InventorySlot(PREDICATE_FILTER, null, CALLBACK_FILTER, 1));

    redstoneControlMode = RedstoneControlMode.IGNORE;
  }

  @Override
  public boolean isActive() {
    if (world.isRemote) {
      return clientActive;
    }
    final boolean active = redstoneCheckPassed && !isFull();
    if (active != clientActive) {
      clientActive = active;
      markDirty();
    }
    return active;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (isActive()) {
      doHoover();
    }
    return false;
  }

  @Override
  protected void updateEntityClient() {
    YetaUtil.refresh(this);
    if (isActive()) {
      doHoover();
    }
  }

  private List<EntityItem> selectEntitiesWithinAABB(World worldIn, AxisAlignedBB bb) {
    List<EntityItem> result = new ArrayList<EntityItem>();
    final int maxItems = VacuumConfig.vacuumChestMaxItems.get();

    final int minChunkX = MathHelper.floor((bb.minX) / 16.0D);
    final int maxChunkX = MathHelper.floor((bb.maxX) / 16.0D);
    final int minChunkZ = MathHelper.floor((bb.minZ) / 16.0D);
    final int maxChunkZ = MathHelper.floor((bb.maxZ) / 16.0D);
    final int minChunkY = MathHelper.floor((bb.minY) / 16.0D);
    final int maxChunkY = MathHelper.floor((bb.maxY) / 16.0D);

    for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
      for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
        Chunk chunk = worldIn.getChunkFromChunkCoords(chunkX, chunkZ);
        final ClassInheritanceMultiMap<Entity>[] entityLists = chunk.getEntityLists();
        final int minChunkYClamped = MathHelper.clamp(minChunkY, 0, entityLists.length - 1);
        final int maxChunkYClamped = MathHelper.clamp(maxChunkY, 0, entityLists.length - 1);
        for (int chunkY = minChunkYClamped; chunkY <= maxChunkYClamped; ++chunkY) {
          for (Entity entity : entityLists[chunkY]) {
            if (!entity.isDead && (entity instanceof EntityItem) && entity.getEntityBoundingBox().intersects(bb)
                && (filter == null || filter.doesItemPassFilter(null, ((EntityItem) entity).getItem())) && MagnetUtil.shouldAttract(getPos(), entity)) {
              result.add((EntityItem) entity);
              if (maxItems > 0 && maxItems <= result.size()) {
                return result;
              }
            }
          }
        }
      }
    }

    return result;
  }

  private void doHoover() {
    int rangeSqr = range * range;
    for (EntityItem entity : selectEntitiesWithinAABB(getWorld(), getBounds())) {
      double x = (pos.getX() + 0.5D - entity.posX);
      double y = (pos.getY() + 0.5D - entity.posY);
      double z = (pos.getZ() + 0.5D - entity.posZ);

      double distance = Math.sqrt(x * x + y * y + z * z);
      if (distance < 1.25 || range == 0) {
        hooverEntity(entity);
      } else {
        double speed = 0.06;
        double distScale = 1.0 - Math.min(0.9, (distance - 1) / rangeSqr);
        distScale *= distScale;

        entity.motionX += x / distance * distScale * speed;
        entity.motionY += y / distance * distScale * 0.4;
        entity.motionZ += z / distance * distScale * speed;
      }
    }
  }

  private void hooverEntity(Entity entity) {
    if (!world.isRemote) {
      if (entity instanceof EntityItem && !entity.isDead) {
        EntityItem item = (EntityItem) entity;
        ItemStack stack = item.getItem().copy();

        int numInserted = ItemTools.doInsertItem(getInventory().getView(Type.INPUT), stack);

        stack.shrink(numInserted);
        item.setItem(stack);
        if (Prep.isInvalid(stack)) {
          item.setDead();
        }
      }
    }
  }

  private boolean isFull() {
    for (InventorySlot slot : getInventory().getView(Type.INPUT)) {
      final ItemStack stackInSlot = slot.getStackInSlot(0);
      if (Prep.isInvalid(stackInSlot) || stackInSlot.getCount() < stackInSlot.getMaxStackSize()) {
        return false;
      }
    }
    return true;
  }

  private int limitRange(int rangeIn) {
    return Math.max(0, Math.min(VacuumConfig.vacuumChestRange.get(), rangeIn));
  }

  public void setRange(int range) {
    this.range = limitRange(range);
    markDirty();
  }

  public boolean hasItemFilter() {
    return filter != null;
  }

  public IItemFilter getItemFilter() {
    return filter;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  // RANGE

  private boolean showingRange;

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  private final static Vector4f color = new Vector4f(.11f, .11f, .94f, .4f);

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if (showingRange) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<TileVacuumChest>(this, color));
    }
  }

  @Override
  public @Nonnull BoundingBox getBounds() {
    return new BoundingBox(getPos()).expand(getRange() + (range == 0 ? 1 / 32f : 0));
  }

  public float getRange() {
    return range;
  }

  // RANGE END

  @Override
  public void setFilter(int filterIndex, int param, @Nonnull IFilter filter) {
    this.filter = (IItemFilter) filter;
  }

  @Override
  public IFilter getFilter(int filterIndex, int param1) {
    return filter;
  }

  @Override
  @Nullable
  public IItemHandler getInventoryForSnapshot(int filterIndex, int param1) {
    return getInventory();
  }

}
