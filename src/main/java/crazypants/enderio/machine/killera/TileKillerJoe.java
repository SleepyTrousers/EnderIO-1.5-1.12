package crazypants.enderio.machine.killera;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;

import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.render.BoundingBox;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;

public class TileKillerJoe extends AbstractMachineEntity /*
                                                          * implements
                                                          * IFluidHandler
                                                          */{

  protected AxisAlignedBB killBounds;
  protected FakePlayer attackera;

  public TileKillerJoe() {
    super(new SlotDefinition(1, 0, 0));
    powerHandler = PowerHandlerUtil.createHandler(new BasicCapacitor(0, 0), this, Type.MACHINE);
  }

  @Override
  public String getMachineName() {
    return ModObject.blockKillerJoe.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if(itemstack == null) {
      return false;
    }
    return itemstack.getItem() instanceof ItemSword;
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
  protected boolean processTasks(boolean redstoneCheckPassed) {

    if(worldObj.getTotalWorldTime() % 10 != 0) {
      return false;
    }

    float baseDamage = getBaseDamage();
    if(baseDamage <= 0) {
      return false;
    }

    List<EntityLivingBase> entsInBounds = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, getKillBounds());
    if(!entsInBounds.isEmpty()) {
      FakePlayer fakee = getAttackera();
      DamageSource ds = DamageSource.causePlayerDamage(fakee);
      for (EntityLivingBase ent : entsInBounds) {
        float enchDamage = getEnchantmentDamage(ent);
        boolean res = ent.attackEntityFrom(ds, baseDamage + enchDamage);
        if(res) {
          damageWeapon(ent);
          return false;
        }
      }
    }
    return false;
  }

  private float getEnchantmentDamage(EntityLivingBase ent) {
    ItemStack weaponStack = getStackInSlot(0);
    if(weaponStack == null) {
      return 0;
    }    
    return EnchantmentHelper.func_152377_a(weaponStack, ent.getCreatureAttribute());
  }

  private float getBaseDamage() {
    ItemStack weaponStack = getStackInSlot(0);
    if(weaponStack == null) {
      return 0;
    }
    Multimap atMods = weaponStack.getAttributeModifiers();    
    Collection ad = atMods.get("generic.attackDamage");
    if(ad.isEmpty()) {
      return 0;
    }
    
    float res = 0;
    for (Object obj : ad) {
      if(obj instanceof AttributeModifier) {
        AttributeModifier am = (AttributeModifier) obj;
        res += am.getAmount();        
      }
    }
    return res;
  }
  
  private void damageWeapon(EntityLivingBase ent) {
    ItemStack weaponStack = getStackInSlot(0);
    if(weaponStack == null) {
      return;
    }
    weaponStack.hitEntity(ent, getAttackera());
    if(weaponStack.stackSize <= 0) {
      setInventorySlotContents(0, null); 
    }    
  }

  private FakePlayer getAttackera() {
    if(attackera == null) {
      attackera = new FakePlayer(MinecraftServer.getServer().worldServerForDimension(worldObj.provider.dimensionId), new GameProfile(null, "KillerJoe"
          + getLocation()));
      attackera.posX = xCoord + 0.5;
      attackera.posY = yCoord + 0.5;
      attackera.posZ = zCoord + 0.5;
    }
    return attackera;
  }

  private AxisAlignedBB getKillBounds() {
    if(killBounds == null) {
      BoundingBox bb = new BoundingBox(getLocation());
      Vector3d min = bb.getMin();
      Vector3d max = bb.getMax();
      max.y += 2;
      min.y -= 2;

      ForgeDirection facingDir = ForgeDirection.getOrientation(facing);
      if(ForgeDirectionOffsets.isPositiveOffset(facingDir)) {
        max.add(ForgeDirectionOffsets.offsetScaled(facingDir, 4));
        min.add(ForgeDirectionOffsets.forDir(facingDir));
      } else {
        min.add(ForgeDirectionOffsets.offsetScaled(facingDir, 4));
        max.add(ForgeDirectionOffsets.forDir(facingDir));

      }
      if(facingDir.offsetX == 0) {
        min.x -= 2;
        max.x += 2;
      } else {
        min.z -= 2;
        max.z += 2;
      }
      killBounds = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
    }
    return killBounds;
  }

  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    return null;
  }

  public boolean canConnectEnergy(ForgeDirection from) {
    return false;
  }

}
