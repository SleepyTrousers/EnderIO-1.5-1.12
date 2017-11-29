package crazypants.enderio.machines.machine.spawner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiID;
import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.machine.render.RenderMappers;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.spawner.DummyRecipe;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.util.CapturedMob;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.init.MachineObject.block_powered_spawner;

public class BlockPoweredSpawner extends AbstractMachineBlock<TilePoweredSpawner> implements IAdvancedTooltipProvider, IPaintable.INonSolidBlockPaintableBlock,
    IPaintable.IWrenchHideablePaint, IHaveTESR {
 
  public static final String KEY_SPAWNED_BY_POWERED_SPAWNER = "spawnedByPoweredSpawner";

  public static BlockPoweredSpawner create(@Nonnull IModObject modObject) {
    MachineRecipeRegistry.instance.registerRecipe(MachineObject.block_powered_spawner.getUnlocalisedName(), new DummyRecipe());

    PacketHandler.INSTANCE.registerMessage(PacketUpdateNotification.class, PacketUpdateNotification.class, PacketHandler.nextID(), Side.CLIENT);

    //Ensure costs are loaded at startup
    PoweredSpawnerConfig.getInstance();

    BlockPoweredSpawner res = new BlockPoweredSpawner(modObject);
    MinecraftForge.EVENT_BUS.register(res);
    res.init();
    return res;
  }

  private Field fieldpersistenceRequired;

  protected BlockPoweredSpawner(@Nonnull IModObject modObject) {
    super(modObject, TilePoweredSpawner.class);

    try {
      fieldpersistenceRequired = ReflectionHelper.findField(EntityLiving.class, "field_82179_bU", "persistenceRequired");
    } catch (Exception e) {
      Log.error("BlockPoweredSpawner: Could not find field: persistenceRequired");
    }
  }

  @SubscribeEvent
  public void handleAnvilEvent(AnvilUpdateEvent evt) {
    if (evt.getLeft() == null || evt.getLeft().getCount() != 1 || evt.getLeft().getItem() != Item.getItemFromBlock(block_powered_spawner.getBlock())) {
      return;
    }
    if (evt.getRight() == null || evt.getRight().getCount() != 1 || evt.getRight().getItem() != ModObject.itemBrokenSpawner.getItem()) {
      return;
    }

    CapturedMob spawnerType = CapturedMob.create(evt.getRight());
    if (spawnerType == null || isBlackListed(spawnerType.getEntityName())) {
      return;
    }

    evt.setCost(Config.powerSpawnerAddSpawnerCost);
    evt.setOutput(evt.getLeft().copy());
    if(evt.getOutput().getTagCompound() == null) {
      evt.getOutput().setTagCompound(new NBTTagCompound());
    }
    evt.getOutput().getTagCompound().setBoolean("eio.abstractMachine", true);
    spawnerType.toNbt(evt.getOutput().getTagCompound());
  }

  @SubscribeEvent
  public void onLivingUpdate(LivingUpdateEvent livingUpdate) {

    Entity ent = livingUpdate.getEntityLiving();
    if(!ent.getEntityData().hasKey(KEY_SPAWNED_BY_POWERED_SPAWNER)) {
      return;
    }
    if(fieldpersistenceRequired == null) {
      ent.getEntityData().removeTag(KEY_SPAWNED_BY_POWERED_SPAWNER);
      return;
    }

    long spawnTime = ent.getEntityData().getLong(KEY_SPAWNED_BY_POWERED_SPAWNER);
    long livedFor = livingUpdate.getEntity().world.getTotalWorldTime() - spawnTime;
    if(livedFor > Config.poweredSpawnerDespawnTimeSeconds*20) {      
      try {
        fieldpersistenceRequired.setBoolean(livingUpdate.getEntityLiving(), false);
        
        ent.getEntityData().removeTag(KEY_SPAWNED_BY_POWERED_SPAWNER);
      } catch (Exception e) {
        Log.warn("BlockPoweredSpawner.onLivingUpdate: Error occured allowing entity to despawn: " + e);
        ent.getEntityData().removeTag(KEY_SPAWNED_BY_POWERED_SPAWNER);
      }
    }
  }

  public static boolean isBlackListed(ResourceLocation entityId) {
    return PoweredSpawnerConfig.getInstance().isBlackListed(entityId);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TilePoweredSpawner) {
      return new ContainerPoweredSpawner(player.inventory, (TilePoweredSpawner) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TilePoweredSpawner) {
      return new GuiPoweredSpawner(player.inventory, (TilePoweredSpawner) te);
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_POWERED_SPAWNER;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    CapturedMob mob = CapturedMob.create(itemstack);
    if (mob != null) {
      list.add(mob.getDisplayName());
    } else {
      list.add(EnderIO.lang.localizeExact("tile.blockPoweredSpawner.tooltip.empty"));
    }
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    if (CapturedMob.containsSoul(itemstack)) {
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, "tile.blockPoweredSpawner");
    } else {
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, "tile.blockPoweredSpawner.empty");
    }
  }

  @SuppressWarnings("null")
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
    super.getSubBlocks(item, tab, list);
    list.add(CapturedMob.create(new ResourceLocation("enderman")).toStack(item, 0, 1));
    list.add(CapturedMob.create(new ResourceLocation("chicken")).toStack(item, 0, 1));
    list.add(CapturedMob.create(new ResourceLocation("skeleton")).toStack(item, 0, 1));
    list.add(CapturedMob.create(new ResourceLocation("wither_skeleton")).toStack(item, 0, 1));
    list.add(CapturedMob.create(new ResourceLocation("stray")).toStack(item, 0, 1));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
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
