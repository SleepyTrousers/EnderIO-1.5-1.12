package crazypants.enderio.item.coordselector;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ChatUtil;
import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiID;
import crazypants.enderio.Lang;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ItemLocationPrintout extends Item implements IGuiHandler {

  public static ItemLocationPrintout create(@Nonnull IModObject modObject) {

    PacketHandler.INSTANCE.registerMessage(PacketUpdateLocationPrintout.Handler.class, PacketUpdateLocationPrintout.class, PacketHandler.nextID(), Side.SERVER);

    ItemLocationPrintout result = new ItemLocationPrintout(modObject);
    GuiID.registerGuiHandler(GuiID.GUI_ID_LOCATION_PRINTOUT, result);
    GuiID.registerGuiHandler(GuiID.GUI_ID_LOCATION_PRINTOUT_CREATE, result);

    GameRegistry.register(result);
    return result;
  }

  protected ItemLocationPrintout(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxStackSize(1);
  }

  @Override
  public boolean doesSneakBypassUse(@Nonnull ItemStack stack, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    return true;
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
    if (player.isSneaking() && TelepadTarget.readFromNBT(stack) != null) {
      GuiID.GUI_ID_LOCATION_PRINTOUT.openClientGui(world, player, hand.ordinal(), 0, 0);
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
    return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
  }

  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {

    ItemStack stack = player.getHeldItem(hand);
    TelepadTarget targ = TelepadTarget.readFromNBT(stack);
    if (targ == null) {
      player.sendMessage(new TextComponentString("No location? but how.."));
      return EnumActionResult.SUCCESS;
    }

    TileEntity te = worldIn.getTileEntity(pos);
    if (te instanceof ICoordinateAware.SingleTarget) {
      ICoordinateAware.SingleTarget tile = (ICoordinateAware.SingleTarget) te;
      return onTelepadClicked(stack, player, worldIn, tile, targ);
    } else if (te instanceof ICoordinateAware.MultipleTargets) {
      ICoordinateAware.MultipleTargets dd = (ICoordinateAware.MultipleTargets) te;
      dd.addTarget(targ);
      if (worldIn.isRemote) {
        player.sendMessage(Lang.PRINTOUT_ADDTARGET.toChat(targ.getChatString()));
      }
      return EnumActionResult.SUCCESS;
    }

    return EnumActionResult.PASS;
  }

  private @Nonnull EnumActionResult onTelepadClicked(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World worldIn,
      @Nonnull ICoordinateAware.SingleTarget tile, @Nonnull TelepadTarget targ) {
    if (tile.canBlockBeAccessed(player)) {
      tile.setTarget(targ);
      if (worldIn.isRemote) {
        player.sendMessage(Lang.PRINTOUT_SETTARGET.toChat(targ.getChatString()));
      }
    } else {
      sendPrivateChatMessage(player, tile.getOwner());
    }
    return EnumActionResult.SUCCESS;
  }

  public static void sendPrivateChatMessage(@Nonnull EntityPlayer player, @Nonnull UserIdent owner) {
    if (!player.isSneaking()) {
      ChatUtil.sendNoSpam(player, Lang.PRINTOUT_PRIVATE.get(TextFormatting.RED, owner.getPlayerName(), TextFormatting.WHITE));
    }
  }

  @Override
  public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull List<String> list, boolean p_77624_4_) {
    TelepadTarget target = TelepadTarget.readFromNBT(stack);
    if (target != null) {
      if (target.isValid()) {
        list.add(BlockCoord.chatString(new BlockPos(target.getLocation()), TextFormatting.GRAY));
        list.add(TelepadTarget.getDimenionName(target.getDimension()));
      }
    }
    super.addInformation(stack, player, list, p_77624_4_);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (GuiID.GUI_ID_LOCATION_PRINTOUT_CREATE.is(ID)) {

      int foundPaper = -1;
      for (int paperIndex = 0; paperIndex < player.inventoryContainer.inventorySlots.size() && foundPaper < 0; paperIndex++) {
        ItemStack invItem = player.inventoryContainer.inventorySlots.get(paperIndex).getStack();
        if (invItem.getItem() == Items.PAPER) {
          foundPaper = paperIndex;
        }
      }
      if (foundPaper < 0) {
        player.sendMessage(Lang.PRINTOUT_NOPAPER.toChat());
        return null;
      }

      TelepadTarget target = new TelepadTarget(new BlockPos(x, y, z), world.provider.getDimension());
      ItemStack stack = new ItemStack(this);
      target.writeToNBT(stack);
      return new GuiLocationPrintout(target, stack, foundPaper);
    } else {
      EnumHand hand = x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
      EntityEquipmentSlot slot = hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;

      TelepadTarget target = TelepadTarget.readFromNBT(player.getItemStackFromSlot(slot));
      if (target != null) {
        return new GuiLocationPrintout(target, player, slot);
      } else {
        return null;
      }
    }
  }

  @Override
  public void getSubItems(@Nonnull Item itemIn, @Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
    return;
  }

}
