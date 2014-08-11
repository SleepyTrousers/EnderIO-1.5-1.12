package crazypants.enderio.machine.killera;

import java.util.List;

import com.mojang.authlib.GameProfile;

import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
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
    // TODO Auto-generated method stub
    return false;
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
    
    FakePlayer fakee = getAttackera();
    List<EntityLivingBase> entsInBounds = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, getKillBounds());
    if(!entsInBounds.isEmpty()) {
      for (EntityLivingBase ent : entsInBounds) {   
        DamageSource ds = DamageSource.causePlayerDamage(fakee);
        float damageAmount = 2;
        boolean res = ent.attackEntityFrom(ds, damageAmount);
        if(res) {
          return false;
        }
      }      
    }
    return false;
  }

  private FakePlayer getAttackera() {
    if(attackera == null) {
      attackera = new FakePlayer(MinecraftServer.getServer().worldServerForDimension(worldObj.provider.dimensionId), new GameProfile(null, "KillerJoe" + getLocation()));      
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
