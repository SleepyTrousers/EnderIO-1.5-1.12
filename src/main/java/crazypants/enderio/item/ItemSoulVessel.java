package crazypants.enderio.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.input.Keyboard;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.CapturedMob;
import crazypants.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;
import net.minecraftforge.server.permission.context.TargetContext;

public class ItemSoulVessel extends Item implements IResourceTooltipProvider, IHaveRenderers, IOverlayRenderAware {

  private static String permissionPickupOwned = null;
  private static String permissionPickup = null;
  private static String permissionPlace = null;

  public static void initPhase() {
    permissionPickupOwned = PermissionAPI.registerNode(EnderIO.DOMAIN + ".soulvial.pickup_owned", DefaultPermissionLevel.OP,
        "Permission to pickup an entity that is owned by another player with Ender IO's soul vessel");
    permissionPickup = PermissionAPI.registerNode(EnderIO.DOMAIN + ".soulvial.pickup", DefaultPermissionLevel.ALL,
        "Permission to pickup an entity with Ender IO's soul vessel");
    permissionPlace = PermissionAPI.registerNode(EnderIO.DOMAIN + ".soulvial.place", DefaultPermissionLevel.ALL,
        "Permission to place down an entity with Ender IO's soul vessel");
  }

  public static ItemSoulVessel create() {
    ItemSoulVessel result = new ItemSoulVessel();
    result.init();
    return result;
  }

  private List<String> blackList;

  protected ItemSoulVessel() {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
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

    if (!PermissionAPI.hasPermission(player.getGameProfile(), permissionPlace, new BlockPosContext(player, pos, null, side))) {
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("soulvial.denied")));
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

    // first check if that entity can be picked up at all
    CapturedMob capturedMob = CapturedMob.create(entity);
    if (capturedMob == null) {
      return false;
    }

    // then check for reasons this specific one cannot
    if (entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null && !player.equals(((IEntityOwnable) entity).getOwner())
        && !PermissionAPI.hasPermission(player.getGameProfile(), permissionPickupOwned, new TargetContext(player, entity))) {
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("soulvial.owned.denied")));
      return false;
    }
    if (!PermissionAPI.hasPermission(player.getGameProfile(), permissionPickup, new TargetContext(player, entity))) {
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("soulvial.denied")));
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
    if (tab == getCreativeTab() || tab == EnderIOTab.tabNoTab) {
      super.getSubItems(itemIn, tab, subItems);
    }
    if (tab == EnderIOTab.tabEnderIO || tab == EnderIOTab.tabNoTab) {
      for (CapturedMob capturedMob : CapturedMob.getAllSouls()) {
        subItems.add(capturedMob.toStack(this, 1, 1));
      }
    }
  }

  @Override
  public CreativeTabs[] getCreativeTabs() {
    return new CreativeTabs[] { getCreativeTab(), EnderIOTab.tabEnderIO };
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return itemStack.isEmpty() ? null : getUnlocalizedName(itemStack);
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

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    doItemOverlayIntoGUI(stack, xPosition, yPosition);
  }

  @SideOnly(Side.CLIENT)
  public static void doItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    if (EnderIO.proxy.getClientPlayer().isSneaking() || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
      CapturedMob capturedMob = CapturedMob.create(stack);
      if (capturedMob != null) {
        String name = capturedMob.getDisplayName();
        int idx = (int) ((EnderIO.proxy.getTickCount() / 4) % name.length());
        name = (name + " " + name).substring(idx, idx + 3);

        FontRenderer fr = Minecraft.getMinecraft().getRenderManager().getFontRenderer();

        if (fr == null) {
          // The hotbar can be rendered before the render manager is initialized...
          return;
        }

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        fr.drawStringWithShadow(name, xPosition + 8 - fr.getStringWidth(name) / 2, yPosition + 5, 0xFF0030B0);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
      }
    }
  }

}
