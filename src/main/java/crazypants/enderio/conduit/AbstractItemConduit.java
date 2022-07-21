package crazypants.enderio.conduit;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.Util;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import java.util.List;
import mods.immibis.microblocks.api.IMicroblockCoverSystem;
import mods.immibis.microblocks.api.IMicroblockSupporterTile;
import mods.immibis.microblocks.api.MicroblockAPIUtils;
import mods.immibis.microblocks.api.Part;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class AbstractItemConduit extends Item implements IConduitItem {

    protected ModObject modObj;

    protected ItemConduitSubtype[] subtypes;

    protected IIcon[] icons;

    protected AbstractItemConduit(ModObject modObj, ItemConduitSubtype... subtypes) {
        this.modObj = modObj;
        this.subtypes = subtypes;
        setCreativeTab(EnderIOTab.tabEnderIO);
        setUnlocalizedName(modObj.unlocalisedName);
        setMaxStackSize(64);
        setHasSubtypes(true);
    }

    protected void init() {
        GameRegistry.registerItem(this, modObj.unlocalisedName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        icons = new IIcon[subtypes.length];
        int index = 0;
        for (ItemConduitSubtype subtype : subtypes) {
            icons[index] = IIconRegister.registerIcon(subtype.iconKey);
            index++;
        }
    }

    @Override
    public boolean onItemUse(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ) {
        if (MicroblocksUtil.supportMicroblocks() && tryAddToMicroblocks(stack, player, world, x, y, z, side)) {
            return true;
        }

        BlockCoord placeAt = Util.canPlaceItem(stack, EnderIO.blockConduitBundle, player, world, x, y, z, side);
        if (placeAt != null) {
            if (!world.isRemote) {
                if (world.setBlock(placeAt.x, placeAt.y, placeAt.z, EnderIO.blockConduitBundle, 0, 1)) {
                    TileEntity te = world.getTileEntity(placeAt.x, placeAt.y, placeAt.z);
                    if (te instanceof IConduitBundle) {
                        IConduitBundle bundle = (IConduitBundle) te;
                        bundle.addConduit(createConduit(stack, player));
                        ConduitUtil.playBreakSound(Block.soundTypeMetal, world, placeAt.x, placeAt.y, placeAt.z);
                    }
                }
            }
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
            return true;

        } else {

            ForgeDirection dir = ForgeDirection.values()[side];
            int placeX = x + dir.offsetX;
            int placeY = y + dir.offsetY;
            int placeZ = z + dir.offsetZ;

            if (world.getBlock(placeX, placeY, placeZ) == EnderIO.blockConduitBundle) {

                IConduitBundle bundle = (TileConduitBundle) world.getTileEntity(placeX, placeY, placeZ);
                if (bundle == null) {
                    System.out.println("AbstractItemConduit.onItemUse: Bundle null");
                    return false;
                }
                if (!bundle.hasType(getBaseConduitType())) {
                    if (!world.isRemote) {
                        IConduit con = createConduit(stack, player);
                        if (con == null) {
                            System.out.println("AbstractItemConduit.onItemUse: Conduit null.");
                            return false;
                        }
                        bundle.addConduit(con);
                        ConduitUtil.playBreakSound(Block.soundTypeMetal, world, placeX, placeY, placeZ);
                        if (!player.capabilities.isCreativeMode) {
                            stack.stackSize--;
                        }
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private boolean tryAddToMicroblocks(
            ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te.getClass().getName().equals("mods.immibis.microblocks.TileMicroblockContainer")) {
            IMicroblockCoverSystem covers = ((IMicroblockSupporterTile) te).getCoverSystem();
            world.setBlock(x, y, z, EnderIO.blockConduitBundle);
            EnderIO.blockConduitBundle.onBlockActivated(world, x, y, z, player, side, 0, 0, 0);
            IMicroblockCoverSystem newCovers = MicroblockAPIUtils.createMicroblockCoverSystem(
                    (IMicroblockSupporterTile) world.getTileEntity(x, y, z));
            for (Part p : covers.getAllParts()) {
                newCovers.addPart(p);
            }
            ((TileConduitBundle) world.getTileEntity(x, y, z)).covers = newCovers;
            return true;
        }
        return false;
    }

    @Override
    public boolean onItemUseFirst(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ) {
        // Conduit replacement
        if (player.isSneaking()) {
            return false;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te == null || !(te instanceof IConduitBundle)) {
            return false;
        }
        IConduitBundle bundle = (IConduitBundle) te;
        IConduit existingConduit = bundle.getConduit(getBaseConduitType());
        if (existingConduit == null) {
            return false;
        }
        ItemStack existingConduitAsItemStack = existingConduit.createItem();
        if (!ItemUtil.areStacksEqual(existingConduitAsItemStack, stack)) {
            if (!world.isRemote) {
                IConduit newConduit = createConduit(stack, player);
                if (newConduit == null) {
                    System.out.println("AbstractItemConduit.onItemUse: Conduit null.");
                    return false;
                }
                bundle.removeConduit(existingConduit);
                bundle.addConduit(newConduit);
                if (!player.capabilities.isCreativeMode) {
                    stack.stackSize--;
                    for (ItemStack drop : existingConduit.getDrops()) {
                        if (!player.inventory.addItemStackToInventory(drop)) {
                            ItemUtil.spawnItemInWorldWithRandomMotion(world, drop, x, y, z);
                        }
                    }
                    player.inventoryContainer.detectAndSendChanges();
                }
                return true;
            } else {
                player.swingItem();
            }
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        damage = MathHelper.clamp_int(damage, 0, subtypes.length - 1);
        return icons[damage];
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, subtypes.length - 1);
        return subtypes[i].unlocalisedName;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int j = 0; j < subtypes.length; ++j) {
            par3List.add(new ItemStack(this, 1, j));
        }
    }
}
