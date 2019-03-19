package crazypants.enderio.machines.machine.spawner;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.power.forge.item.PoweredBlockItem;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.spawner.DummyRecipe;
import crazypants.enderio.base.recipe.spawner.EntityDataRegistry;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.machines.config.config.SpawnerConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.util.CapturedMob;
import crazypants.enderio.util.Prep;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.init.MachineObject.block_powered_spawner;

public class BlockPoweredSpawner extends AbstractPoweredTaskBlock<TilePoweredSpawner>
    implements IAdvancedTooltipProvider, IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveTESR {

  public static final @Nonnull String KEY_SPAWNED_BY_POWERED_SPAWNER = "spawnedByPoweredSpawner";

  public static BlockPoweredSpawner create(@Nonnull IModObject modObject) {
    MachineRecipeRegistry.instance.registerRecipe(MachineObject.block_powered_spawner.getUnlocalisedName(), new DummyRecipe());

    BlockPoweredSpawner res = new BlockPoweredSpawner(modObject);
    MinecraftForge.EVENT_BUS.register(res);
    res.init();
    return res;
  }

  private Field fieldpersistenceRequired;

  protected BlockPoweredSpawner(@Nonnull IModObject modObject) {
    super(modObject);
    setShape(mkShape(BlockFaceShape.SOLID, BlockFaceShape.SOLID, BlockFaceShape.UNDEFINED, BlockFaceShape.SOLID));

    try {
      fieldpersistenceRequired = ReflectionHelper.findField(EntityLiving.class, "field_82179_bU", "persistenceRequired");
    } catch (Exception e) {
      Log.error("BlockPoweredSpawner: Could not find field: persistenceRequired");
    }
  }

  @SubscribeEvent
  public void handleAnvilEvent(AnvilUpdateEvent evt) {
    if (Prep.isInvalid(evt.getLeft()) || evt.getLeft().getCount() != 1 || evt.getLeft().getItem() != block_powered_spawner.getItem()) {
      return;
    }
    if (Prep.isInvalid(evt.getRight()) || evt.getRight().getCount() != 1 || evt.getRight().getItem() != ModObject.itemBrokenSpawner.getItem()) {
      return;
    }

    CapturedMob spawnerType = CapturedMob.create(evt.getRight());
    if (spawnerType == null || isBlackListed(spawnerType.getEntityName())) {
      return;
    }

    evt.setCost(SpawnerConfig.powerSpawnerAddSpawnerCost.get());
    evt.setOutput(evt.getLeft().copy());
    evt.getOutput().setTagCompound(spawnerType.toNbt(evt.getOutput().getTagCompound()));
  }

  @SubscribeEvent
  public void onLivingUpdate(LivingUpdateEvent livingUpdate) {

    Entity ent = livingUpdate.getEntityLiving();
    if (!ent.getEntityData().hasKey(KEY_SPAWNED_BY_POWERED_SPAWNER)) {
      return;
    }
    if (fieldpersistenceRequired == null) {
      ent.getEntityData().removeTag(KEY_SPAWNED_BY_POWERED_SPAWNER);
      return;
    }

    long spawnTime = ent.getEntityData().getLong(KEY_SPAWNED_BY_POWERED_SPAWNER);
    long livedFor = livingUpdate.getEntity().world.getTotalWorldTime() - spawnTime;
    if (livedFor > SpawnerConfig.poweredSpawnerDespawnTimeSeconds.get() * 20) {
      try {
        fieldpersistenceRequired.setBoolean(livingUpdate.getEntityLiving(), false);

        ent.getEntityData().removeTag(KEY_SPAWNED_BY_POWERED_SPAWNER);
      } catch (Exception e) {
        Log.warn("BlockPoweredSpawner.onLivingUpdate: Error occured allowing entity to despawn: " + e);
        ent.getEntityData().removeTag(KEY_SPAWNED_BY_POWERED_SPAWNER);
      }
    }
  }

  public static boolean isBlackListed(@Nonnull ResourceLocation entityId) {
    return EntityDataRegistry.getInstance().isBlackListedForSpawning(entityId);
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TilePoweredSpawner te) {
    return new ContainerPoweredSpawner(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TilePoweredSpawner te) {
    return new GuiPoweredSpawner(player.inventory, te);
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    CapturedMob mob = CapturedMob.create(itemstack);
    if (mob != null) {
      list.add(mob.getDisplayName());
      list.add(Lang.TOOLTIP_SPAWNER_COST.get(LangPower.FLOAT_NF.format(EntityDataRegistry.getInstance().getCostMultiplierFor(mob.getEntityName()))));
    } else {
      list.add(Lang.SPAWNER_EMPTY.get());
    }
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    if (CapturedMob.containsSoul(itemstack)) {
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, "tile.block_powered_spawner");
    } else {
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, "tile.block_powered_spawner.empty");
    }
  }

  @Override
  @Nullable
  public PoweredBlockItem createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new PoweredBlockItem(this) {
      @Override
      public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
          @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (CapturedMob.containsSoul(itemstack)) {
          return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        } else {
          player.sendStatusMessage(Lang.STATUS_SPAWNER_UNBOUND.toChatServer(), true);
          return EnumActionResult.FAIL;
        }
      }
    });
  };

  @SuppressWarnings("null")
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    super.getSubBlocks(tab, list);
    if (SpawnerConfig.poweredSpawnerAddAllMobsCreative.get()) {
      CapturedMob.getAllSouls().apply(new Callback<CapturedMob>() {
        @Override
        public void apply(@Nonnull CapturedMob mob) {
          list.add(mob.toStack(BlockPoweredSpawner.this, 0, 1));
        }
      });
    } else {
      list.add(CapturedMob.create(new ResourceLocation("enderman")).toStack(this, 0, 1));
      list.add(CapturedMob.create(new ResourceLocation("chicken")).toStack(this, 0, 1));
      list.add(CapturedMob.create(new ResourceLocation("skeleton")).toStack(this, 0, 1));
      list.add(CapturedMob.create(new ResourceLocation("wither_skeleton")).toStack(this, 0, 1));
      list.add(CapturedMob.create(new ResourceLocation("stray")).toStack(this, 0, 1));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.SOUL_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.SOUL_MAPPER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TilePoweredSpawner tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TilePoweredSpawner.class, new PoweredSpawnerSpecialRenderer());
  }

}
