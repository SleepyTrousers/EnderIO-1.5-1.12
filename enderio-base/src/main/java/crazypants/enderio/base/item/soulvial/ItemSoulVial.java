package crazypants.enderio.base.item.soulvial;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.interfaces.IOverlayRenderAware;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.recipe.spawner.EntityDataRegistry;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.render.itemoverlay.MobNameOverlayRenderHelper;
import crazypants.enderio.util.CapturedMob;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;
import net.minecraftforge.server.permission.context.TargetContext;

public class ItemSoulVial extends Item implements IResourceTooltipProvider, IHaveRenderers, IOverlayRenderAware, IModObject.LifecycleInit {

  public final @Nonnull IBehaviorDispenseItem DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem() {
    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    @Override
    protected @Nonnull ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
      ItemStack secondaryResult = Prep.getEmpty();
      EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);

      CapturedMob capturedMob = CapturedMob.create(stack);

      if (capturedMob != null) {
        if (capturedMob.spawn(source.getWorld(), source.getBlockPos().offset(enumfacing).down(), EnumFacing.UP, true)) {
          stack.shrink(1);
          secondaryResult = new ItemStack(ItemSoulVial.this);
        }
      } else {
        for (EntityLivingBase entity : source.getWorld().<EntityLivingBase> getEntitiesWithinAABB(EntityLivingBase.class,
            new AxisAlignedBB(source.getBlockPos().offset(enumfacing)), EntitySelectors.NOT_SPECTATING)) {
          if (!(entity instanceof IEntityOwnable) || ((IEntityOwnable) entity).getOwnerId() == null) {
            capturedMob = CapturedMob.create(entity);
            if (capturedMob != null) {
              entity.setDead();
              if (entity.isDead) {
                stack.shrink(1);
                secondaryResult = capturedMob.toStack(ItemSoulVial.this, 1, 1);
                break;
              }
            }
          }
        }
      }

      if (Prep.isValid(secondaryResult)) {
        if (Prep.isInvalid(stack)) {
          stack = secondaryResult;
        } else {
          TileEntity blockTileEntity = source.getBlockTileEntity();
          if (!(blockTileEntity instanceof TileEntityDispenser) || ((TileEntityDispenser) blockTileEntity).addItemStack(secondaryResult) < 0) {
            Block.spawnAsEntity(source.getWorld(), source.getBlockPos().offset(enumfacing), secondaryResult);
          }
        }
      }

      return stack;
    }
  };

  private @Nonnull String permissionPickupOwned = "(item not initialized)";
  private @Nonnull String permissionPickup = "(item not initialized)";
  private @Nonnull String permissionPlace = "(item not initialized)";

  @Override
  public void init(@Nonnull IModObject modObject, @Nonnull FMLInitializationEvent event) {
    permissionPickupOwned = PermissionAPI.registerNode(EnderIO.DOMAIN + ".soulvial.pickup_owned", DefaultPermissionLevel.OP,
        "Permission to pickup an entity that is owned by another player with Ender IO's soul vessel");
    permissionPickup = PermissionAPI.registerNode(EnderIO.DOMAIN + ".soulvial.pickup", DefaultPermissionLevel.ALL,
        "Permission to pickup an entity with Ender IO's soul vessel");
    permissionPlace = PermissionAPI.registerNode(EnderIO.DOMAIN + ".soulvial.place", DefaultPermissionLevel.ALL,
        "Permission to place down an entity with Ender IO's soul vessel");
  }

  public static ItemSoulVial create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemSoulVial(modObject);
  }

  protected ItemSoulVial(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxStackSize(16);
    BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DISPENSER_BEHAVIOR);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(modObject.getRegistryName(), "variant=empty"));
    ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(modObject.getRegistryName(), "variant=full"));
  }

  @Override
  public int getMetadata(@Nonnull ItemStack stack) {
    if (CapturedMob.containsSoul(stack)) {
      return 1;
    }
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean hasEffect(@Nonnull ItemStack item) {
    return CapturedMob.containsSoul(item);
  }

  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {

    if (world.isRemote) {
      return EnumActionResult.PASS;
    }

    ItemStack itemstack = player.getHeldItem(hand);

    CapturedMob capturedMob = CapturedMob.create(itemstack);
    if (capturedMob == null) {
      return EnumActionResult.SUCCESS;
    }

    if (!PermissionAPI.hasPermission(player.getGameProfile(), permissionPlace, new BlockPosContext(player, pos, null, side))) {
      player.sendMessage(Lang.SOUL_VIAL_DENIED.toChatServer());
      return EnumActionResult.SUCCESS;
    }

    if (!capturedMob.spawn(world, pos, side, true)) {
      return EnumActionResult.SUCCESS;
    }

    if (!player.capabilities.isCreativeMode) {
      itemstack.shrink(1);
      final ItemStack emptyVial = new ItemStack(this);
      if (Prep.isInvalid(itemstack)) {
        player.setHeldItem(hand, emptyVial);
      } else if (!player.inventory.addItemStackToInventory(emptyVial)) {
        player.dropItem(emptyVial, false);
      }
      player.inventoryContainer.detectAndSendChanges();
    }

    return EnumActionResult.SUCCESS;
  }

  @Override
  public boolean itemInteractionForEntity(@Nonnull ItemStack item, @Nonnull EntityPlayer player, @Nonnull EntityLivingBase entity, @Nonnull EnumHand hand) {
    if (entity.world.isRemote) {
      return false;
    }
    boolean isCreative = player.capabilities.isCreativeMode;
    if (CapturedMob.containsSoul(item) && !isCreative) {
      return false;
    }

    // check the cases we have a message for first
    if (entity instanceof EntityPlayer) {
      player.sendMessage(Lang.SOUL_VIAL_DENIED_PLAYER.toChatServer());
    } else if (CapturedMob.isBlacklisted(entity)) {
      player.sendMessage(Lang.SOUL_VIAL_DENIED_AALISTED.toChatServer());
    } else if (entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null && !player.equals(((IEntityOwnable) entity).getOwner())
        && !PermissionAPI.hasPermission(player.getGameProfile(), permissionPickupOwned, new TargetContext(player, entity))) {
      player.sendMessage(Lang.SOUL_VIAL_DENIED_OWNED_PET.toChatServer());
      return false;
    } else if (!PermissionAPI.hasPermission(player.getGameProfile(), permissionPickup, new TargetContext(player, entity))) {
      player.sendMessage(Lang.SOUL_VIAL_DENIED.toChatServer());
      return false;
    } else if (!entity.isEntityAlive()) {
      player.sendMessage(Lang.SOUL_VIAL_DENIED_DEAD.toChatServer());
      return false;
    }

    // then check if that entity can be picked up at all
    CapturedMob capturedMob = CapturedMob.create(entity);
    if (capturedMob == null) {
      player.sendMessage(Lang.SOUL_VIAL_DENIED_UNKNOWN.toChatServer());
      return false;
    }

    ItemStack capturedMobVessel = capturedMob.toStack(this, 1, 1);

    player.swingArm(hand);
    if (!isCreative) {
      entity.setDead();
      if (entity.isDead) {
        // Forge Bug: if the current itemstack is left empty,
        // forge replaces the hotbar item with EMPTY itself.
        // So we do not shrink the stack when it is 1.
        if (item.getCount() > 1) {
          item.shrink(1);
          // Since this stack still exists, add the new vial to the first location, or drop
          if (!player.inventory.addItemStackToInventory(capturedMobVessel)) {
            player.dropItem(capturedMobVessel, false);
          }
        } else {
          // Otherwise, just replace the stack
          player.setHeldItem(hand, capturedMobVessel);
        }
        player.inventoryContainer.detectAndSendChanges();
        return true;
      }
    } else {
      if (!player.inventory.addItemStackToInventory(capturedMobVessel)) {
        player.dropItem(capturedMobVessel, false);
      }
      player.inventoryContainer.detectAndSendChanges();
      return true;
    }
    return false;
  }

  public @Nonnull ItemStack createVesselWithEntityStub(ResourceLocation entityId) {
    CapturedMob capturedMob = CapturedMob.create(entityId);
    if (capturedMob == null) {
      return Prep.getEmpty();
    }

    return capturedMob.toStack(this, 1, 1);
  }

  public @Nonnull ItemStack createVesselWithEntity(Entity entity) {
    CapturedMob capturedMob = CapturedMob.create(entity);
    if (capturedMob == null) {
      return Prep.getEmpty();
    }

    return capturedMob.toStack(this, 1, 1);
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (tab == getCreativeTab()) {
      super.getSubItems(tab, list);
    } else if (tab == EnderIOTab.tabEnderIOMobs) {
      for (CapturedMob capturedMob : CapturedMob.getAllSouls()) {
        if (!EntityDataRegistry.getInstance().isBlackListedForSoulVial(capturedMob.getEntityName())) {
          list.add(capturedMob.toStack(this, 1, 1));
        }
      }
    }
  }

  @Override
  public @Nonnull CreativeTabs[] getCreativeTabs() {
    return new CreativeTabs[] { getCreativeTab(), EnderIOTab.tabEnderIOMobs };
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    CapturedMob capturedMob = CapturedMob.create(stack);
    if (capturedMob != null) {
      tooltip.add(capturedMob.getDisplayName());

      float health = capturedMob.getHealth();
      if (health >= 0) {
        float maxHealth = capturedMob.getMaxHealth();
        if (maxHealth >= 0) {
          tooltip.add(Lang.SOUL_VIAL_HEALTH.get(String.format("%3.1f/%3.1f", health, maxHealth)));
        } else {
          tooltip.add(Lang.SOUL_VIAL_HEALTH.get(String.format("%3.1f", health)));
        }
      }

      String fluidName = capturedMob.getFluidName();
      if (fluidName != null) {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid != null) {
          String localizedName = fluid.getLocalizedName(new FluidStack(fluid, 1));
          tooltip.add(Lang.SOUL_VIAL_FLUID.get(localizedName));
        }
      }

      DyeColor color = capturedMob.getColor();
      if (color != null) {
        tooltip.add(Lang.SOUL_VIAL_COLOR.get(color.getLocalisedName()));
      }
    } else {
      tooltip.add(Lang.SOUL_VIAL_EMPTY.get());
    }
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    MobNameOverlayRenderHelper.doItemOverlayIntoGUI(stack, xPosition, yPosition);
  }

}
