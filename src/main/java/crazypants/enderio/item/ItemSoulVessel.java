package crazypants.enderio.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.CapturedMob;
import crazypants.util.ClientUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
    setUnlocalizedName(ModObject.itemSoulVessel.getUnlocalisedName());
    setRegistryName(ModObject.itemSoulVessel.getUnlocalisedName());
    setMaxStackSize(64);
    blackList = new ArrayList<String>();
    for (String ent : Config.soulVesselBlackList) {
      blackList.add(ent);
    }
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {    
    ClientUtil.regRenderer(this, 0, "itemSoulVessel");
    ClientUtil.regRenderer(this, 1, "itemSoulVesselFull");
  }

  @Override
  public int getMetadata(ItemStack stack) {
    if (CapturedMob.containsSoul(stack)) {
      return 1;
    }
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean hasEffect(ItemStack item) {
    return CapturedMob.containsSoul(item);
  }
  
  

  @Override
  public @Nonnull EnumActionResult onItemUse(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX,
      float hitY, float hitZ) {

    if (world.isRemote || player == null) {
      return EnumActionResult.PASS;
    }

    CapturedMob capturedMob = CapturedMob.create(itemstack);
    if (capturedMob == null) {
      return EnumActionResult.SUCCESS;
    }

    if (!capturedMob.spawn(world, pos, side, true)) {
      return EnumActionResult.SUCCESS;
    }

    if (!player.capabilities.isCreativeMode) {
      if (itemstack.stackSize > 1) {
        itemstack.stackSize--;
        if (itemstack.stackSize == 0) {
          player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(this));
        } else if (!player.inventory.addItemStackToInventory(new ItemStack(this))) {
          player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, new ItemStack(this)));
        }
        player.inventoryContainer.detectAndSendChanges();
      } else {
        itemstack.setTagCompound(null);
      }
    }
    
    return EnumActionResult.SUCCESS;
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack item, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
    if (entity.worldObj.isRemote || player == null) {
      return false;
    }
    boolean isCreative = player.capabilities.isCreativeMode;
    if (CapturedMob.containsSoul(item) && !isCreative) {
      return false;
    }

    CapturedMob capturedMob = CapturedMob.create(entity);
    if (capturedMob == null) {
      return false;
    }

    ItemStack capturedMobVessel = capturedMob.toStack(this, 1, 1);

    player.swingArm(hand);
    if (!isCreative) {
      entity.setDead();
      if (entity.isDead) {
        item.stackSize--;
        if (!player.inventory.addItemStackToInventory(capturedMobVessel)) {
          entity.worldObj.spawnEntityInWorld(new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, capturedMobVessel));
        }
        player.setHeldItem(hand, item);
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
    CapturedMob capturedMob = CapturedMob.create(entityId, null);
    if (capturedMob == null) {
      return null;
    }

    return capturedMob.toStack(this, 1, 1);
  }

  public ItemStack createVesselWithEntity(Entity entity) {
    CapturedMob capturedMob = CapturedMob.create(entity);
    if (capturedMob == null) {
      return null;
    }

    return capturedMob.toStack(this, 1, 1);
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    super.getSubItems(itemIn, tab, subItems);
    if (tab != null) {
      for (CapturedMob capturedMob : CapturedMob.getAllSouls()) {
        subItems.add(capturedMob.toStack(this, 1, 1));
      }
    }
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return itemStack == null ? null : getUnlocalizedName(itemStack);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    CapturedMob capturedMob = CapturedMob.create(par1ItemStack);
    if (capturedMob != null) {
      par3List.add(capturedMob.getDisplayName());

      float health = capturedMob.getHealth();
      if (health >= 0) {
        float maxHealth = capturedMob.getMaxHealth();
        String msg = EnderIO.lang.localize("item.itemSoulVessel.tooltip.health");
        if (maxHealth >= 0) {
          par3List.add(String.format("%s %3.1f/%3.1f", msg, health, maxHealth));
        } else {
          par3List.add(String.format("%s %3.1f", msg, health));
        }
      }

      String fluidName = capturedMob.getFluidName();
      if (fluidName != null) {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid != null) {
          String unlocalizedName = fluid.getUnlocalizedName();
          String name = unlocalizedName == null ? fluidName : I18n.format(unlocalizedName);
          par3List.add(EnderIO.lang.localize("item.itemSoulVessel.tooltip.fluidname") + " " + name);
        }
      }

      DyeColor color = capturedMob.getColor();
      if (color != null) {
        par3List.add(EnderIO.lang.localize("item.itemSoulVessel.tooltip.color") + " " + color.getLocalisedName());
      }
      } else {
        par3List.add(EnderIO.lang.localize("item.itemSoulVessel.tooltip.empty"));
      }
  }

}
