package crazypants.enderio.base.item.conduitprobe;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.tool.IHideFacades;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.render.IHaveRenderers;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemConduitProbe extends Item implements IResourceTooltipProvider, IHideFacades, IHaveRenderers {

  public static ItemConduitProbe create(@Nonnull IModObject modObject) {
    return new ItemConduitProbe(modObject);
  }

  protected ItemConduitProbe(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  public static boolean copyPasteSettings(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nonnull IConduitBundle bundle, @Nonnull EnumFacing dir) {
    if (player.world.isRemote) {
      return true;
    }

    boolean isCopy = player.isSneaking();
    boolean clearedData = false;

    if (!isCopy && !stack.hasTagCompound()) {
      return false;
    }

    boolean performedAction = false;
    Collection<IConduit> conduits = bundle.getConduits();
    for (IConduit conduit : conduits) {
      if (conduit.getExternalConnections().contains(dir)) {
        if (isCopy && !clearedData) {
          clearedData = true;
        }
        if (isCopy) {
          NBTTagCompound nbt = new NBTTagCompound();
          performedAction |= conduit.writeConnectionSettingsToNBT(dir, nbt);
          stack.setTagCompound(nbt);
        } else {
          NBTTagCompound nbt = stack.getTagCompound();
          performedAction |= nbt != null && conduit.readConduitSettingsFromNBT(dir, nbt);
        }
      }
    }

    if (isCopy && performedAction) {
      player.sendStatusMessage(Lang.GUI_PROBE_COPIED.toChatServer(), true);
    }

    return performedAction;
  }

  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer playerIn, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    ItemStack itemStack = playerIn.getHeldItem(hand);
    if (itemStack.getItemDamage() == 0) {
      if (PacketConduitProbe.canCreatePacket(world, pos)) {
        if (world.isRemote) {
          PacketHandler.INSTANCE.sendToServer(new PacketConduitProbe(pos, side));
        }
        return EnumActionResult.SUCCESS;
      }
    }
    return EnumActionResult.PASS;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  public boolean doesSneakBypassUse(@Nonnull ItemStack stack, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    return true;
  }

  @Override
  public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(modObject.getRegistryName(), "variant=probe"));
    ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(modObject.getRegistryName(), "variant=copy"));
  }

}
