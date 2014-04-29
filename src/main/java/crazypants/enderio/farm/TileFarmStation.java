package crazypants.enderio.farm;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.render.BoundingBox;
import crazypants.util.BlockCoord;
import crazypants.util.ItemUtil;

public class TileFarmStation extends AbstractMachineEntity implements IEntitySelector {

  private crazypants.util.BlockCoord lastScanned;
  private FakePlayer farmerJoe;

  private int farmSize = 4;

  @Override
  public String getInventoryName() {
    return EnderIO.blockFarmStation.getLocalizedName();
  }

  public TileFarmStation() {
    super(new SlotDefinition(6, 4));
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockFarmStation.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return slotDefinition.isInputSlot(i);
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public float getProgress() {
    return 0;
  }



  @Override
  public void updateEntity() {
    super.updateEntity();
    doHoover();
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {



    if(!redstoneCheckPassed) {
      return false;
    }

    if(worldObj.getWorldTime() % 10 != 0) {
      return false;
    }

    BlockCoord bc = getNextCoord();
    if(bc == null) {
      return false;
    }
    lastScanned = bc;

    Block block = worldObj.getBlock(bc.x, bc.y, bc.z);
    if(block == null) {
      return false;
    }
    int meta = worldObj.getBlockMetadata(bc.x, bc.y, bc.z);
    if(farmerJoe == null) {
      farmerJoe = FakePlayerFactory.getMinecraft(MinecraftServer.getServer().worldServerForDimension(worldObj.provider.dimensionId));
      farmerJoe.inventory.currentItem = 0;
      farmerJoe.inventory.setInventorySlotContents(0, new ItemStack(Items.iron_hoe));
    }

    FarmersComune.instance.prepareBlock(worldObj, bc, block, meta, farmerJoe);
    List<EntityItem> harvest = FarmersComune.instance.harvestBlock(worldObj, bc, block, meta, farmerJoe);
    if(harvest != null) {
      for (EntityItem ei : harvest) {
        if(ei != null) {
          worldObj.spawnEntityInWorld(ei);
        }
      }
    }

    return false;
  }

  private void doHoover() {
    //TODO: dont do this when your full
    BoundingBox bb = new BoundingBox(getLocation());
    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    aabb = aabb.expand(farmSize + 1, farmSize + 1, farmSize + 1);
    List<Entity> interestingItems = worldObj.selectEntitiesWithinAABB(Entity.class, aabb, this);

    for (Entity entity : interestingItems) {
      double x = (xCoord + 0.5D - entity.posX);
      double y = (yCoord + 0.5D - entity.posY);
      double z = (zCoord + 0.5D - entity.posZ);

      double distance = Math.sqrt(x * x + y * y + z * z);
      if(distance < 1.1) {
        hooverEntity(entity);
      } else {
        double var11 = 1.0 - distance / 15.0;

        if(var11 > 0.0D) {
          var11 *= var11;
          entity.motionX += x / distance * var11 * 0.05;
          entity.motionY += y / distance * var11 * 0.2;
          entity.motionZ += z / distance * var11 * 0.05;
        }
      }
    }
  }

  private void hooverEntity(Entity entity) {
    if(!worldObj.isRemote) {
      if(entity instanceof EntityItem && !entity.isDead) {
        EntityItem item = (EntityItem) entity;
        ItemStack stack = item.getEntityItem().copy();
        int numInserted = ItemUtil.doInsertItem(this, stack);
        stack.stackSize -= numInserted;
        if(stack.stackSize == 0) {
          item.setDead();
        } else {
          item.setEntityItemStack(stack);
        }
      }
    }

  }

  @Override
  public boolean isEntityApplicable(Entity entity) {
    if(entity.isDead) {
      return false;
    }
    if(entity instanceof IProjectile) {
      return entity.motionY < 0.01;
    }
    if(entity instanceof EntityItem) {
      ItemStack stack = ((EntityItem) entity).getEntityItem();
      //TODO: What if the inventory is full?
      return true;
    }
    return false;
  }

  private BlockCoord getNextCoord() {

    BlockCoord loc = getLocation();
    if(lastScanned == null) {
      lastScanned = new BlockCoord(loc.x - farmSize, loc.y, loc.z - farmSize);
      return lastScanned;
    }

    int nextX = lastScanned.x + 1;
    int nextZ = lastScanned.z;
    if(nextX > loc.x + farmSize) {
      nextX = loc.x - farmSize;
      nextZ += 1;
      if(nextZ > loc.z + farmSize) {
        lastScanned = null;
        return getNextCoord();
      }
    }
    return new BlockCoord(nextX, lastScanned.y, nextZ);
  }

}
