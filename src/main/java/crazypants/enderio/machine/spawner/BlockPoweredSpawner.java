package crazypants.enderio.machine.spawner;

import java.util.List;

import net.minecraft.block.BlockMobSpawner;
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
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.waila.IWailaInfoProvider;
import crazypants.util.Lang;
import crazypants.util.Util;

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
  
  public static BlockPoweredSpawner create() {       
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPoweredSpawner.unlocalisedName, new DummyRecipe());
    
    //Ensure costs are loaded at startup
    PoweredSpawnerConfig.getInstance();
    
    BlockPoweredSpawner res = new BlockPoweredSpawner();
    MinecraftForge.EVENT_BUS.register(res);
    res.init();
    return res;
  }  
  
  protected BlockPoweredSpawner() {
    super(ModObject.blockPoweredSpawner, TilePoweredSpawner.class);    
  }

  @SubscribeEvent
  public void onBreakEvent(BlockEvent.BreakEvent evt) {
    if(evt.block instanceof BlockMobSpawner) {
      if(evt.getPlayer() != null && !evt.getPlayer().capabilities.isCreativeMode && !evt.getPlayer().worldObj.isRemote) {
        TileEntity tile = evt.getPlayer().worldObj.getTileEntity(evt.x, evt.y, evt.z);
        if(tile instanceof TileEntityMobSpawner) {
          
          if(Math.random() > Config.brokenSpawnerDropChance) {            
            return;
          }
          
          TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
          MobSpawnerBaseLogic logic = spawner.func_145881_a();
          if(logic != null) {
            String name = logic.getEntityNameToSpawn();
            if(name != null && !isBlackListed(name)) {
              ItemStack drop = ItemBrokenSpawner.createStackForMobType(name);
              Util.dropItems(evt.getPlayer().worldObj, drop, evt.x, evt.y, evt.z, true);
            }
          }
        }
      }
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
  
  public boolean isBlackListed(String entityId) {   
    return PoweredSpawnerConfig.getInstance().isBlackListed(entityId);
  }
  
  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TilePoweredSpawner) {
      return new ContainerPoweredSpawner(player.inventory, (TilePoweredSpawner)te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TilePoweredSpawner) {
      return new GuiPoweredSpawner(player.inventory, (TilePoweredSpawner)te);
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
      list.add(Lang.localize("tile.blockPoweredSpawner.tooltip.empty", false));
    }
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {       
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    String type = getSpawnerTypeFromItemStack(itemstack);
    if(type == null) {
      TooltipAddera.addDetailedTooltipFromResources(list, "tile.blockPoweredSpawner.empty");
    } else {
      TooltipAddera.addDetailedTooltipFromResources(list, "tile.blockPoweredSpawner");
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
}
