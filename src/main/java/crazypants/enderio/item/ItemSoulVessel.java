package crazypants.enderio.item;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.material.Material;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemSoulVessel extends Item implements IResourceTooltipProvider {

  public static ItemSoulVessel create() {
    ItemSoulVessel result = new ItemSoulVessel();
    result.init();
    return result;
  }

  private IIcon filledIcon;
  
  protected ItemSoulVessel() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemSoulVessel.unlocalisedName);
    setMaxStackSize(1);    
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemSoulVessel.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister IIconRegister) {
    itemIcon = IIconRegister.registerIcon("enderio:soulVessel");
    filledIcon = IIconRegister.registerIcon("enderio:soulVesselFilled");
  }

  @Override
  public IIcon getIcon(ItemStack item, int arg1, EntityPlayer arg2, ItemStack arg3, int arg4) {
     if(containsSoul(item)) {
       return filledIcon;
     } 
     return itemIcon;         
  }  

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIconIndex(ItemStack item) {
    if(containsSoul(item)) {
      return filledIcon;
    } 
    return itemIcon;
  }

  @Override
  public boolean hasEffect(ItemStack item, int pass) {       
    return containsSoul(item);
  }

  @Override
  public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {

    if(world.isRemote) {
      return true;
    }  
    if(!containsSoul(itemstack)) {
      return true;
    }
    
    NBTTagCompound root = itemstack.stackTagCompound;    
    Entity mob = EntityList.createEntityFromNBT(root, world);
    if (mob == null) {
      return true;      
    }
    mob.readFromNBT(root);
    
    Block blk = world.getBlock(x,y,z);    
    double spawnX = x + Facing.offsetsXForSide[side] + 0.5;
    double spawnY = y + Facing.offsetsYForSide[side];
    double spawnZ = z + Facing.offsetsZForSide[side] + 0.5;
    if(side == ForgeDirection.UP.ordinal() && (blk instanceof BlockFence || blk instanceof BlockWall)) {
      spawnY += 0.5;
    }    
    mob.setLocationAndAngles(spawnX, spawnY, spawnZ, world.rand.nextFloat() * 360.0F, 0);  

    boolean spaceClear = world.checkNoEntityCollision(mob.boundingBox)
        && world.getCollidingBoundingBoxes(mob, mob.boundingBox).isEmpty();
    if(!spaceClear) {
      return false;
    }
   
//    if(mob instanceof EntityHorse) {
//      ((EntityHorse)mob).setHorseType(3);
//    }
    
    world.spawnEntityInWorld(mob);    
    if(mob instanceof EntityLiving) {
      ((EntityLiving)mob).playLivingSound();
    }  
    
    Entity riddenByEntity = mob.riddenByEntity;
    while(riddenByEntity != null) {      
      riddenByEntity.setLocationAndAngles(spawnX, spawnY, spawnZ, world.rand.nextFloat() * 360.0F, 0.0F);      
      world.spawnEntityInWorld(riddenByEntity);
      if(riddenByEntity instanceof EntityLiving) {
        ((EntityLiving)riddenByEntity).playLivingSound();
      }      
      riddenByEntity = riddenByEntity.riddenByEntity;
    }
    
    
    if(player == null || !player.capabilities.isCreativeMode) {
      itemstack.setTagCompound(null);
    }
    
    return true;
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack item, EntityPlayer player, EntityLivingBase entity) {

    if(entity.worldObj.isRemote) {
      return false;
    }
    boolean isCreative = player != null && player.capabilities.isCreativeMode;
    if(containsSoul(item) && !isCreative) {
      return false;
    }
    if(entity instanceof EntityPlayer) {
      return false;
    }
    
    String entityId = EntityList.getEntityString(entity);
    if(isBlackListed(entityId)) {
      return false;
    }
    
    if(!Config.soulVesselCapturesBosses && entity instanceof IBossDisplayData) {
      return false;
    }
    
    NBTTagCompound root = new NBTTagCompound();
    root.setString("id", entityId);    
    entity.writeToNBT(root);
    
    if(!isCreative) {
      entity.setDead();
      if(entity.isDead) {
        item.setTagCompound(root);        
        return true;
      }
    } else {
      item.setTagCompound(root);
      player.setCurrentItemOrArmor(0, item);
      return true;
    }
    return false;
  }

  private boolean isBlackListed(String entityId) {
    for(String str : Config.soulVesselBlackList) {
      if(str != null && str.equals(entityId)) {
        return true;
      }
    }
    return false;
  }

  private boolean containsSoul(ItemStack item) {
    if(item == null) {
      return false;
    }
    if(item.getItem() != this) {
      return false;
    }    
    return item.stackTagCompound != null && item.stackTagCompound.hasKey("id");    
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    if(par1ItemStack != null) {
      String mobName = getMobTypeFromStack(par1ItemStack);
      if(mobName != null) {
        par3List.add(StatCollector.translateToLocal("entity." + mobName + ".name"));
      } else {
        par3List.add("Empty");
      }
    }
  }

  private String getMobTypeFromStack(ItemStack item) {
    if(item == null || item.stackTagCompound == null || !item.stackTagCompound.hasKey("id")) {
      return null;
    }
    return item.stackTagCompound.getString("id");
  }

}
