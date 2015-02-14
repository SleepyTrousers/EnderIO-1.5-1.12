package crazypants.enderio.teleport.telepad;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import crazypants.util.BlockCoord;

public class ItemCoordsCard extends Item implements IResourceTooltipProvider {

  public static ItemCoordsCard create() {
    ItemCoordsCard ret = new ItemCoordsCard();
    GameRegistry.registerItem(ret, ModObject.itemCoordsCard.unlocalisedName);
    return ret;
  }

  private ItemCoordsCard() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemCoordsCard.unlocalisedName);
    setTextureName("EnderIO:" + ModObject.itemCoordsCard.unlocalisedName);
    setMaxStackSize(1);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void getSubItems(Item item, CreativeTabs p_150895_2_, @SuppressWarnings("rawtypes") List list) {
    ItemStack stack = new ItemStack(item);
    init(stack);
    list.add(stack);
  }

  @Override
  public void onCreated(ItemStack stack, World world, EntityPlayer player) {
    super.onCreated(stack, world, player);
    init(stack);
  }

  private void init(ItemStack stack) {
    stack.stackTagCompound = new NBTTagCompound();
    new BlockCoord().writeToNBT(stack.stackTagCompound);
  }

  @Override
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
    if(!world.isRemote) {
      BlockCoord bc = new BlockCoord(player);
      setCoords(stack, bc);
      onCoordsChanged(player, bc);
    }
    player.swingItem();
    return super.onItemRightClick(stack, world, player);
  }

  @Override
  public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof ITelePad) {
      ITelePad tp = (ITelePad) te;
      if(tp.canBlockBeAccessed(player)) {
        BlockCoord bc = getCoords(stack);
        BlockCoord cur = new BlockCoord(tp.getX(), tp.getY(), tp.getZ());
        if(!bc.equals(cur)) {
          tp.setCoords(getCoords(stack));
          player.addChatMessage(new ChatComponentText("Set Coords: " + bc.chatString()));
        }
      } else {
        BlockTravelAnchor.sendPrivateChatMessage(player, tp.getPlacedBy());
      }
    } else {
      BlockCoord bc = new BlockCoord(x, y, z);
      if(!player.isSneaking()) {
        ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
        bc = bc.getLocation(dir);
      }
      setCoords(stack, bc);
      onCoordsChanged(player, bc);
    }
    return true;
  }

  private void onCoordsChanged(EntityPlayer player, BlockCoord bc) {
    player.addChatMessage(new ChatComponentText("New Coords: " + bc.chatString()));
  }

  public void setCoords(ItemStack stack, BlockCoord bc) {
    bc.writeToNBT(stack.stackTagCompound);
  }

  public BlockCoord getCoords(ItemStack stack) {
    return new BlockCoord().readFromNBT(stack.stackTagCompound);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }
}
