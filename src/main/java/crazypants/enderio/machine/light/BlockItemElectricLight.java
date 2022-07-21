package crazypants.enderio.machine.light;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockItemElectricLight extends ItemBlockWithMetadata implements IResourceTooltipProvider {

    public enum Type {
        ELECTRIC("item.itemElectricLight", false, true, false),
        ELECTRIC_INV("item.itemElectricLightInverted", true, true, false),
        BASIC("item.itemLight", false, false, false),
        BASIC_INV("item.itemLightInverted", true, false, false),
        WIRELESS("item.itemWirelessLight", false, true, true),
        WIRELESS_INV("item.itemWirelessLightInverted", true, true, true);

        final String unlocName;
        final boolean isInverted;
        final boolean isPowered;
        final boolean isWireless;

        private Type(String unlocName, boolean isInverted, boolean isPowered, boolean isWireless) {
            this.unlocName = unlocName;
            this.isInverted = isInverted;
            this.isPowered = isPowered;
            this.isWireless = isWireless;
        }
    }

    public BlockItemElectricLight(Block block) {
        super(block, block);
        setCreativeTab(EnderIOTab.tabEnderIO);
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int meta = par1ItemStack.getItemDamage();
        meta = MathHelper.clamp_int(meta, 0, Type.values().length - 1);
        return Type.values()[meta].unlocName;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (Type type : Type.values()) {
            par3List.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public boolean placeBlockAt(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ,
            int metadata) {
        if (!world.setBlock(x, y, z, field_150939_a, 0, 3)) {
            return false;
        }
        if (world.getBlock(x, y, z) == field_150939_a) {
            ForgeDirection onFace = ForgeDirection.values()[side].getOpposite();
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileElectricLight) {
                TileElectricLight el = ((TileElectricLight) te);
                el.setFace(onFace);
                Type t = Type.values()[metadata];
                el.setInverted(t.isInverted);
                el.setRequiresPower(t.isPowered);
                el.setWireless(t.isWireless);
            }
        }
        return true;
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
        return getUnlocalizedName(itemStack);
    }
}
