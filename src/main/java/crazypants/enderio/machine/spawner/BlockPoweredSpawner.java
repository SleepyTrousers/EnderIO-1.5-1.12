package crazypants.enderio.machine.spawner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.BlockMobSpawner;
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
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.waila.IWailaInfoProvider;

public class BlockPoweredSpawner extends AbstractMachineBlock<TilePoweredSpawner> implements IAdvancedTooltipProvider {

  public static void writeMobTypeToNBT(NBTTagCompound nbt, String type) {
    if(nbt == null) {
      return;
    }
    if(type == null) {
      nbt.removeTag("mobType");
    } else {
      nbt.setString("mobType", type);
    }
  }

  public static String readMobTypeFromNBT(NBTTagCompound nbt) {
    if(nbt == null) {
      return null;
    }
    if(!nbt.hasKey("mobType")) {
      return null;
    }
    return nbt.getString("mobType");
  }

  public static String getSpawnerTypeFromItemStack(ItemStack stack) {
    if(stack == null || stack.getItem() != Item.getItemFromBlock(EnderIO.blockPoweredSpawner)) {
      return null;
    }
    return readMobTypeFromNBT(stack.stackTagCompound);
  }

  public static final String KEY_SPAWNED_BY_POWERED_SPAWNER = "spawnedByPoweredSpawner";

  public static BlockPoweredSpawner create() {
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPoweredSpawner.unlocalisedName, new DummyRecipe());

    PacketHandler.INSTANCE.registerMessage(PacketMode.class, PacketMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketUpdateNotification.class, PacketUpdateNotification.class, PacketHandler.nextID(),
        Side.CLIENT);

    //Ensure costs are loaded at startup
    PoweredSpawnerConfig.getInstance();

    BlockPoweredSpawner res = new BlockPoweredSpawner();
    MinecraftForge.EVENT_BUS.register(res);
    FMLCommonHandler.instance().bus().register(res);
    res.init();
    return res;
  }

  private final List<UniqueIdentifier> toolBlackList = new ArrayList<UniqueIdentifier>();

  private Field fieldpersistenceRequired;

  protected BlockPoweredSpawner() {
    super(ModObject.blockPoweredSpawner, TilePoweredSpawner.class);

    String[] blackListNames = Config.brokenSpawnerToolBlacklist;
    for (String name : blackListNames) {
      toolBlackList.add(new UniqueIdentifier(name));
    }

    try {
      fieldpersistenceRequired = ReflectionHelper.findField(EntityLiving.class, "field_82179_bU", "persistenceRequired");
    } catch (Exception e) {
      Log.error("BlockPoweredSpawner: Could not find field: persistenceRequired");
    }
  }

  private final Map<BlockCoord, ItemStack> dropCache = new HashMap<BlockCoord, ItemStack>();

  @SubscribeEvent
  public void onBreakEvent(BlockEvent.BreakEvent evt) {
    if(evt.block instanceof BlockMobSpawner) {
      if(evt.getPlayer() != null && !evt.getPlayer().capabilities.isCreativeMode && !evt.getPlayer().worldObj.isRemote && !evt.isCanceled()) {
        TileEntity tile = evt.getPlayer().worldObj.getTileEntity(evt.x, evt.y, evt.z);
        if(tile instanceof TileEntityMobSpawner) {

          if(Math.random() > Config.brokenSpawnerDropChance) {
            return;
          }
          
          ItemStack equipped = evt.getPlayer().getCurrentEquippedItem();
          if(equipped != null) {
            for (UniqueIdentifier uid : toolBlackList) {
              Item blackListItem = GameRegistry.findItem(uid.modId, uid.name);
              if(blackListItem == equipped.getItem()) {
                return;
              }
            }
          }

          TileEntityMobSpawner spawner = (TileEntityMobSpawner) tile;
          MobSpawnerBaseLogic logic = spawner.func_145881_a();
          if(logic != null) {
            String name = logic.getEntityNameToSpawn();
            if(name != null && !isBlackListed(name)) {
              ItemStack drop = ItemBrokenSpawner.createStackForMobType(name);
              dropCache.put(new BlockCoord(evt.x, evt.y, evt.z), drop);

              for (int i = (int) (Math.random() * 7); i > 0; i--) {
                logic.spawnDelay = 0;
                logic.updateSpawner();
              }

            }
          }
        }
      } else {
        dropCache.put(new BlockCoord(evt.x, evt.y, evt.z), null);
      }
    }
  }

  @SubscribeEvent
  public void onHarvestDropsEvent(BlockEvent.HarvestDropsEvent evt) {
    if (!evt.isCanceled() && evt.block instanceof BlockMobSpawner) {
      BlockCoord bc = new BlockCoord(evt.x, evt.y, evt.z);
      if (dropCache.containsKey(bc)) {
        ItemStack stack = dropCache.get(bc);
        if (stack != null) {
          evt.drops.add(stack);
        }
      } else {
        // A spawner was broken---but not by a player. The TE has been
        // invalidated already, but we might be able to recover it.
        try {
          for (Object object : evt.world.loadedTileEntityList) {
            if (object instanceof TileEntityMobSpawner) {
              TileEntityMobSpawner spawner = (TileEntityMobSpawner) object;
              if (spawner.getWorldObj() == evt.world && spawner.xCoord == evt.x && spawner.yCoord == evt.y
                  && spawner.zCoord == evt.z) {
                // Bingo!
                MobSpawnerBaseLogic logic = spawner.func_145881_a();
                if (logic != null) {
                  String name = logic.getEntityNameToSpawn();
                  if (name != null && !isBlackListed(name)) {
                    evt.drops.add(ItemBrokenSpawner.createStackForMobType(name));
                  }
                }
              }
            }
          }
        } catch (Exception e) {
          // Risky recovery failed. Happens.
        }
      }
    }
  }

  @SubscribeEvent
  public void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      dropCache.clear();
    }
  }

  @SubscribeEvent
  public void handleAnvilEvent(AnvilUpdateEvent evt) {
    if(evt.left == null || evt.left.stackSize != 1 || evt.left.getItem() != Item.getItemFromBlock(EnderIO.blockPoweredSpawner) ||
        evt.right == null || ItemBrokenSpawner.getMobTypeFromStack(evt.right) == null) {
      return;
    }

    String spawnerType = ItemBrokenSpawner.getMobTypeFromStack(evt.right);
    if(isBlackListed(spawnerType)) {
      return;
    }

    evt.cost = Config.powerSpawnerAddSpawnerCost;
    evt.output = evt.left.copy();
    if(evt.output.stackTagCompound == null) {
      evt.output.stackTagCompound = new NBTTagCompound();
    }
    evt.output.stackTagCompound.setBoolean("eio.abstractMachine", true);
    writeMobTypeToNBT(evt.output.stackTagCompound, spawnerType);

  }

  @SubscribeEvent
  public void onLivingUpdate(LivingUpdateEvent livingUpdate) {

    Entity ent = livingUpdate.entityLiving;
    if(!ent.getEntityData().hasKey(KEY_SPAWNED_BY_POWERED_SPAWNER)) {
      return;
    }
    if(fieldpersistenceRequired == null) {
      ent.getEntityData().removeTag(KEY_SPAWNED_BY_POWERED_SPAWNER);
      return;
    }

    long spawnTime = ent.getEntityData().getLong(KEY_SPAWNED_BY_POWERED_SPAWNER);
    long livedFor = livingUpdate.entity.worldObj.getTotalWorldTime() - spawnTime;
    if(livedFor > Config.poweredSpawnerDespawnTimeSeconds*20) {
      try {
        fieldpersistenceRequired.setBoolean(livingUpdate.entityLiving, false);
        ent.getEntityData().removeTag(KEY_SPAWNED_BY_POWERED_SPAWNER);
      } catch (Exception e) {
        Log.warn("BlockPoweredSpawner.onLivingUpdate: Error occured allowing entity to despawn: " + e);
        ent.getEntityData().removeTag(KEY_SPAWNED_BY_POWERED_SPAWNER);
      }
    }
  }

  public boolean isBlackListed(String entityId) {
    return PoweredSpawnerConfig.getInstance().isBlackListed(entityId);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TilePoweredSpawner) {
      return new ContainerPoweredSpawner(player.inventory, (TilePoweredSpawner) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TilePoweredSpawner) {
      return new GuiPoweredSpawner(player.inventory, (TilePoweredSpawner) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_POWERED_SPAWNER;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:poweredSpawnerFrontActive";
    }
    return "enderio:poweredSpawnerFront";
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    String type = getSpawnerTypeFromItemStack(itemstack);
    if(type != null) {
      list.add(StatCollector.translateToLocal("entity." + type + ".name"));
    } else {
      list.add(EnderIO.lang.localizeExact("tile.blockPoweredSpawner.tooltip.empty"));
    }
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    String type = getSpawnerTypeFromItemStack(itemstack);
    if(type == null) {
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, "tile.blockPoweredSpawner.empty");
    } else {
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, "tile.blockPoweredSpawner");
    }
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
    TilePoweredSpawner te = (TilePoweredSpawner) world.getTileEntity(x, y, z);
    tooltip.add(te.getEntityName());
  }

  @Override
  public int getDefaultDisplayMask(World world, int x, int y, int z) {
    return IWailaInfoProvider.BIT_DETAILED;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item item, CreativeTabs tab, List list) {
    super.getSubBlocks(item, tab, list);
    list.add(createItemStackForMob("Enderman"));
    list.add(createItemStackForMob("Chicken"));
  }

  protected ItemStack createItemStackForMob(String mob) {
    ItemStack stack = new ItemStack(this);
    stack.stackTagCompound = new NBTTagCompound();
    stack.stackTagCompound.setBoolean("eio.abstractMachine", true);
    writeMobTypeToNBT(stack.stackTagCompound, mob);
    return stack;
  }

  private static class DropInfo {

    BlockEvent.BreakEvent evt;
    ItemStack drop;

    DropInfo(BreakEvent evt, ItemStack stack) {
      super();
      this.evt = evt;
      drop = stack;
    }

    void doDrop() {
      if(evt.isCanceled()) {
        return;
      }

      Util.dropItems(evt.getPlayer().worldObj, drop, evt.x, evt.y, evt.z, true);
    }

  }
}
