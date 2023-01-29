package crazypants.enderio.machine.tank;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidContainerItem;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.tool.SmartTank;

public class BlockItemTank extends ItemBlockWithMetadata implements IAdvancedTooltipProvider, IFluidContainerItem {

    public BlockItemTank() {
        super(EnderIO.blockTank, EnderIO.blockTank);
        setHasSubtypes(true);
        setCreativeTab(EnderIOTab.tabEnderIO);
    }

    public BlockItemTank(Block block) {
        super(block, block);
        setHasSubtypes(true);
        setCreativeTab(EnderIOTab.tabEnderIO);
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int meta = par1ItemStack.getItemDamage();
        String result = super.getUnlocalizedName(par1ItemStack);
        if (meta == 1) {
            result += ".advanced";
        }
        return result;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        ItemStack stack = new ItemStack(this, 1, 0);
        par3List.add(stack);
        stack = new ItemStack(this, 1, 1);
        par3List.add(stack);
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        EnderIO.blockTank.addCommonEntries(itemstack, entityplayer, list, flag);
    }

    @Override
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        EnderIO.blockTank.addBasicEntries(itemstack, entityplayer, list, flag);
    }

    @Override
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        EnderIO.blockTank.addDetailedEntries(itemstack, entityplayer, list, flag);
    }

    private static final FluidTank dummy = new SmartTank(FluidRegistry.WATER, 16000);

    private FluidTank loadTank(ItemStack stack) {
        if (stack.hasTagCompound()) {
            FluidTank tank = TileTank.loadTank(stack.getTagCompound());
            return tank != null ? tank : dummy;
        }
        return dummy;
    }

    private void saveTank(ItemStack stack, FluidTank tank) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        TileTank.saveTank(stack.getTagCompound(), tank);
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        return loadTank(container).getFluid();
    }

    @Override
    public int getCapacity(ItemStack container) {
        return loadTank(container).getCapacity();
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        FluidTank tank = loadTank(container);
        int ret = tank.fill(resource, doFill);
        saveTank(container, tank);
        return ret;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        FluidTank tank = loadTank(container);
        FluidStack ret = tank.drain(maxDrain, doDrain);
        saveTank(container, tank);
        return ret;
    }
}
