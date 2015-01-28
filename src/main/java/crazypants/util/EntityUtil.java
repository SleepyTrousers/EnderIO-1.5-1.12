package crazypants.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import crazypants.vecmath.Vector3d;

public class EntityUtil {

  public static String getDisplayNameForEntity(String mobName) {
    return StatCollector.translateToLocal("entity." + mobName + ".name");
  }
  
  public static List<String> getAllRegisteredMobNames(boolean excludeBosses) {
    List<String> result = new ArrayList<String>();    
    Set<Map.Entry<Class, String>> entries = EntityList.classToStringMapping.entrySet();
    for(Map.Entry<Class, String> entry : entries) {
      if(EntityLiving.class.isAssignableFrom(entry.getKey()) ) {
        if(!excludeBosses || !IBossDisplayData.class.isAssignableFrom(entry.getKey())) {
          result.add(entry.getValue());
        }
      }
    }    
    return result;
  }
  
  private EntityUtil() {    
  }

  public static Vector3d getEntityPosition(Entity ent) {
    return new Vector3d(ent.posX, ent.posY, ent.posZ);
  }
  
  public static List<AxisAlignedBB> getCollidingBlockGeometry(World world, Entity entity) {
    AxisAlignedBB entityBounds = entity.boundingBox;
    ArrayList collidingBoundingBoxes = new ArrayList();
    int minX = MathHelper.floor_double(entityBounds.minX);
    int minY = MathHelper.floor_double(entityBounds.minY);
    int minZ = MathHelper.floor_double(entityBounds.minZ);
    int maxX = MathHelper.floor_double(entityBounds.maxX + 1.0D);    
    int maxY = MathHelper.floor_double(entityBounds.maxY + 1.0D);    
    int maxZ = MathHelper.floor_double(entityBounds.maxZ + 1.0D);
    for (int x = minX; x < maxX; x++) {
      for (int z = minZ; z < maxZ; z++) {
        for (int y = minY; y < maxY; y++) {
          Block block = world.getBlock(x, y, z);
          if(block != null) {
            block.addCollisionBoxesToList(world, x, y, z, entityBounds, collidingBoundingBoxes, entity);
          }
        }
      }
    }
    return collidingBoundingBoxes;
  }

  public static void spawnItemInWorldWithRandomMotion(World world, ItemStack item, int x, int y, int z) {
    if(item != null) {
      spawnItemInWorldWithRandomMotion(world, item, x + 0.5, y + 0.5, z + 0.5);
    }
  }

  public static void spawnItemInWorldWithRandomMotion(World world, ItemStack item, double x, double y, double z) {
    if(item != null) {
      spawnItemInWorldWithRandomMotion(new EntityItem(world, x, y, z, item));
    }
  }

  public static void spawnItemInWorldWithRandomMotion(EntityItem entity) {
    entity.delayBeforeCanPickup = 10;

    float f = (entity.worldObj.rand.nextFloat() * 0.1f) - 0.05f;
    float f1 = (entity.worldObj.rand.nextFloat() * 0.1f) - 0.05f;
    float f2 = (entity.worldObj.rand.nextFloat() * 0.1f) - 0.05f;

    entity.motionX += f;
    entity.motionY += f1;
    entity.motionZ += f2;

    entity.worldObj.spawnEntityInWorld(entity);
  }
}
