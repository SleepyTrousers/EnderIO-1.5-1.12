package crazypants.enderio.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.conduit.ConduitDisplayMode;

public class ItemYetaWrench extends Item implements IToolWrench {

  public static ItemYetaWrench create() {
    if(Config.useSneakMouseWheelYetaWrench) {
      PacketHandler.instance.addPacketProcessor(new YetaWrenchPacketProcessor());
      TickRegistry.registerTickHandler(new YetaWrenchTickHandler(), Side.CLIENT);
    }
    ItemYetaWrench result = new ItemYetaWrench();
    result.init();
    return result;
  }

  protected ItemYetaWrench() {
    super(ModObject.itemYetaWrench.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemYetaWrench.unlocalisedName);
    setMaxStackSize(1);
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.itemYetaWrench.name);
    GameRegistry.registerItem(this, ModObject.itemYetaWrench.unlocalisedName);
    new YetaWrenchOverlayRenderer(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:yetaWrench");
  }

  @Override
  public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
    int blockId = world.getBlockId(x, y, z);
    Block block = Block.blocksList[blockId];
    if(block != null && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
      player.swingItem();
      return !world.isRemote;
    }
    return false;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack equipped, World world, EntityPlayer player) {
    if(!Config.useSneakRightClickYetaWrench) {
      return equipped;
    }
    if(!player.isSneaking()) {
      return equipped;
    }
    ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(equipped);
    if(curMode == null) {
      curMode = ConduitDisplayMode.ALL;
    }
    ConduitDisplayMode newMode = curMode.next();
    ConduitDisplayMode.setDisplayMode(equipped, newMode);
    return equipped;
  }

  @Override
  public boolean canWrench(EntityPlayer player, int x, int y, int z) {
    return true;
  }

  @Override
  public void wrenchUsed(EntityPlayer player, int x, int y, int z) {
    player.swingItem();
  }

  @Override
  public boolean shouldPassSneakingClickToBlock(World par2World, int par4, int par5, int par6) {
    return true;
  }

}
