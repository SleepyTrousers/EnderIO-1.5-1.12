package crazypants.enderio.item;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.EntityUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IHaveRenderers;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSoulVessel extends Item implements IResourceTooltipProvider,IHaveRenderers {

  public static ItemSoulVessel create() {
    ItemSoulVessel result = new ItemSoulVessel();
    result.init();
    return result;
  }

  private List<String> blackList;

  protected ItemSoulVessel() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemSoulVessel.unlocalisedName);
    setMaxStackSize(64);
    blackList = new ArrayList<String>();
    for (String ent : Config.soulVesselBlackList) {
      blackList.add(ent);
    }
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemSoulVessel.unlocalisedName);
  }

  public void addEntityToBlackList(String entityName) {
    blackList.add(entityName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {    
    ClientUtil.regRenderer(this, 0, "itemSoulVessel");
    ClientUtil.regRenderer(this, 1, "itemSoulVesselFull");
  }

  @Override
  public int getMetadata(ItemStack stack) {
    if (containsSoul(stack)) {
      return 1;
    }
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean hasEffect(ItemStack item) {
    return containsSoul(item);
  }

  @Override
  public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
    if (world.isRemote) {
      return true;
    }
    if (!containsSoul(itemstack)) {
      return false;
    }
    if (player == null) {
      return false;
    }

    Entity mob;
    NBTTagCompound root = itemstack.getTagCompound();
    if (root.hasKey("isStub")) {
      String entityId = root.getString("id");
      mob = EntityList.createEntityByName(entityId, world);
    } else {
      mob = EntityList.createEntityFromNBT(root, world);
    }
    if (mob == null) {
      return true;
    }
    mob.readFromNBT(root);

    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();

    Block blk = world.getBlockState(pos).getBlock();
    double spawnX = x + side.getFrontOffsetX() + 0.5;
    double spawnY = y + side.getFrontOffsetY();
    double spawnZ = z + side.getFrontOffsetZ() + 0.5;
    if (side == EnumFacing.UP && (blk instanceof BlockFence || blk instanceof BlockWall)) {
      spawnY += 0.5;
    }
    mob.setLocationAndAngles(spawnX, spawnY, spawnZ, world.rand.nextFloat() * 360.0F, 0);

    boolean spaceClear = world.checkNoEntityCollision(mob.getEntityBoundingBox()) && world.getCollidingBoundingBoxes(mob, mob.getEntityBoundingBox()).isEmpty();
    if (!spaceClear) {
      return false;
    }

    if (itemstack.hasDisplayName() && mob instanceof EntityLiving) {
      ((EntityLiving) mob).setCustomNameTag(itemstack.getDisplayName());
    }

    world.spawnEntityInWorld(mob);
    if (mob instanceof EntityLiving) {
      ((EntityLiving) mob).playLivingSound();
    }

    Entity riddenByEntity = mob.riddenByEntity;
    while (riddenByEntity != null) {
      riddenByEntity.setLocationAndAngles(spawnX, spawnY, spawnZ, world.rand.nextFloat() * 360.0F, 0.0F);
      world.spawnEntityInWorld(riddenByEntity);
      if (riddenByEntity instanceof EntityLiving) {
        ((EntityLiving) riddenByEntity).playLivingSound();
      }
      riddenByEntity = riddenByEntity.riddenByEntity;
    }

    if (!player.capabilities.isCreativeMode) {
      if (itemstack.stackSize > 1) {
        itemstack.stackSize--;
        if (itemstack.stackSize == 0) {
          System.out.println("ItemSoulVessel.onItemUse: !!!!!!!!!");
          player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(this));
        } else if (!player.inventory.addItemStackToInventory(new ItemStack(this))) {
          player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, new ItemStack(this)));
        }
        player.inventoryContainer.detectAndSendChanges();
      } else {
        itemstack.setTagCompound(null);
      }
    }

    return true;
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack item, EntityPlayer player, EntityLivingBase entity) {

    if (entity.worldObj.isRemote) {
      return false;
    }
    boolean isCreative = player != null && player.capabilities.isCreativeMode;
    if (containsSoul(item) && !isCreative) {
      return false;
    }
    if (entity instanceof EntityPlayer) {
      return false;
    }

    String entityId = EntityList.getEntityString(entity);
    if (isBlackListed(entityId)) {
      return false;
    }

    if (!Config.soulVesselCapturesBosses && entity instanceof IBossDisplayData) {
      return false;
    }

    NBTTagCompound root = new NBTTagCompound();
    root.setString("id", entityId);
    entity.writeToNBT(root);

    ItemStack capturedMobVessel = new ItemStack(this);
    capturedMobVessel.setTagCompound(root);
    setDisplayNameFromEntityNameTag(capturedMobVessel, entity);

    player.swingItem();
    if (!isCreative) {
      entity.setDead();
      if (entity.isDead) {
        item.stackSize--;
        if (!player.inventory.addItemStackToInventory(capturedMobVessel)) {
          entity.worldObj.spawnEntityInWorld(new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, capturedMobVessel));
        }
        player.setCurrentItemOrArmor(0, item);
        ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
        return true;
      }
    } else {
      //Inventory full, drop it in the world!
      if (!player.inventory.addItemStackToInventory(capturedMobVessel)) {
        entity.worldObj.spawnEntityInWorld(new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, capturedMobVessel));
      }
      ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
      return true;
    }
    return false;
  }

  public ItemStack createVesselWithEntityStub(String entityId) {
    NBTTagCompound root = new NBTTagCompound();
    root.setString("id", entityId);
    root.setBoolean("isStub", true);

    ItemStack res = new ItemStack(this);
    res.setTagCompound(root);
    return res;
  }

  public ItemStack createVesselWithEntity(Entity ent) {

    String entityId = EntityList.getEntityString(ent);
    NBTTagCompound root = new NBTTagCompound();
    root.setString("id", entityId);
    ent.writeToNBT(root);

    ItemStack res = new ItemStack(this);
    res.setTagCompound(root);

    setDisplayNameFromEntityNameTag(res, ent);
    return res;
  }

  private void setDisplayNameFromEntityNameTag(ItemStack item, Entity ent) {
    if (ent instanceof EntityLiving) {
      EntityLiving entLiv = (EntityLiving) ent;
      if (entLiv.hasCustomName()) {
        String name = entLiv.getCustomNameTag();
        if (name.length() > 0) {
          item.setStackDisplayName(name);
        }
      }
    }
  }

  public boolean containsSoul(ItemStack item) {
    if (item == null) {
      return false;
    }
    if (item.getItem() != this) {
      return false;
    }
    return item.getTagCompound() != null && item.getTagCompound().hasKey("id");
  }

  public String getMobTypeFromStack(ItemStack item) {
    if (!containsSoul(item)) {
      return null;
    }
    if (item.getTagCompound() == null || !item.getTagCompound().hasKey("id")) {
      return null;
    }
    return item.getTagCompound().getString("id");
  }

  /** Support for displaying fluid name of captured Moo Fluids cow */
  private String getFluidNameFromStack(ItemStack item) {
    if (!containsSoul(item)) {
      return null;
    }
    if (!item.getTagCompound().hasKey("FluidName")) {
      return null;
    }
    return item.getTagCompound().getString("FluidName");
  }

  private DyeColor getColorFromStack(ItemStack item) {
    if (!containsSoul(item)) {
      return null;
    }
    if (!item.getTagCompound().hasKey("Color")) {
      return null;
    }
    int colorIdx = item.getTagCompound().getInteger("Color");
    if (colorIdx < 0 || colorIdx > 15) {
      return null;
    }
    return DyeColor.values()[15 - colorIdx];
  }

  private float getHealthFromStack(ItemStack item) {
    if (!containsSoul(item)) {
      return Float.NaN;
    }
    if (!item.getTagCompound().hasKey("HealF")) {
      return Float.NaN;
    }
    return item.getTagCompound().getFloat("HealF");
  }

  private NBTTagCompound getAttributeFromStack(ItemStack item, String name) {
    if (!containsSoul(item)) {
      return null;
    }
    NBTBase tag = item.getTagCompound().getTag("Attributes");
    if (tag instanceof NBTTagList) {
      NBTTagList attributes = (NBTTagList) tag;
      for (int i = 0; i < attributes.tagCount(); i++) {
        NBTTagCompound attrib = attributes.getCompoundTagAt(i);
        if (attrib.hasKey("Name") && name.equals(attrib.getString("Name"))) {
          return attrib;
        }
      }
    }
    return null;
  }

  private float getMaxHealthFromStack(ItemStack item) {
    NBTTagCompound maxHealthAttrib = getAttributeFromStack(item, "generic.maxHealth");
    if (maxHealthAttrib == null) {
      return Float.NaN;
    }
    if (!maxHealthAttrib.hasKey("Base")) {
      return Float.NaN;
    }
    return maxHealthAttrib.getFloat("Base");
  }

  private boolean isBlackListed(String entityId) {
    for (String str : blackList) {
      if (str != null && str.equals(entityId)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    if (par1ItemStack != null) {
      String mobName = getMobTypeFromStack(par1ItemStack);
      if (mobName != null) {
        par3List.add(EntityUtil.getDisplayNameForEntity(mobName));
      } else {
        par3List.add(EnderIO.lang.localize("item.itemSoulVessel.tooltip.empty"));
      }

      float health = getHealthFromStack(par1ItemStack);
      if (health >= 0) {
        float maxHealth = getMaxHealthFromStack(par1ItemStack);
        String msg = EnderIO.lang.localize("item.itemSoulVessel.tooltip.health");
        if (maxHealth >= 0) {
          par3List.add(String.format("%s %3.1f/%3.1f", msg, health, maxHealth));
        } else {
          par3List.add(String.format("%s %3.1f", msg, health));
        }
      }

      String fluidName = getFluidNameFromStack(par1ItemStack);
      if (fluidName != null) {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid != null) {
          // TODO: 1.8
          par3List.add(EnderIO.lang.localize("item.itemSoulVessel.tooltip.fluidname") + " " + fluidName);
          // par3List.add(EnderIO.lang.localize("item.itemSoulVessel.tooltip.fluidname")
          // + " " + fluid.getLocalizedName());
        }
      }

      DyeColor color = getColorFromStack(par1ItemStack);
      if (color != null) {
        par3List.add(EnderIO.lang.localize("item.itemSoulVessel.tooltip.color") + " " + color.getLocalisedName());
      }
    }
  }

}
