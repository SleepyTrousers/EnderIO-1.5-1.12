package crazypants.enderio.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import com.enderio.core.client.handlers.SpecialTooltipHandler.ITooltipCallback;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.generator.stirling.StirlingGeneratorContainer;
import crazypants.enderio.machine.generator.stirling.TileEntityStirlingGenerator;
import crazypants.enderio.machine.power.PowerDisplayUtil;

public class TooltipHandlerBurnTime implements ITooltipCallback {

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        int time = 0;
        TileEntityStirlingGenerator gen = getStirlingGen(itemstack);
        if (isStirlingGen(itemstack, gen)) {
            int rate = gen.getPowerUsePerTick();
            String msg = String.format(
                    "%s %s %s %s %s %s%s",
                    EnderIO.lang.localize("power.generates"),
                    PowerDisplayUtil.formatPower((long) gen.getBurnTime(itemstack) * rate),
                    PowerDisplayUtil.abrevation(),
                    EnderIO.lang.localize("power.generation_rate"),
                    PowerDisplayUtil.formatPower(rate),
                    PowerDisplayUtil.abrevation(),
                    PowerDisplayUtil.perTickStr());

            list.add(msg);
        } else if (Config.addFurnaceFuelTootip && (time = TileEntityFurnace.getItemBurnTime(itemstack)) > 0) {
            list.add(EnderIO.lang.localize("tooltip.burntime") + " " + time);
        }
    }

    @Override
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {}

    @Override
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {}

    @Override
    public boolean shouldHandleItem(ItemStack item) {
        int time = TileEntityFurnace.getItemBurnTime(item);
        return time > 0 || isStirlingGen(item);
    }

    private TileEntityStirlingGenerator getStirlingGen(ItemStack stack) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player != null && player.openContainer instanceof StirlingGeneratorContainer) {
            AbstractMachineEntity te = ((StirlingGeneratorContainer) player.openContainer).getInv();
            if (te instanceof TileEntityStirlingGenerator) {
                return (TileEntityStirlingGenerator) te;
            }
        }
        return null;
    }

    private boolean isStirlingGen(ItemStack stack) {
        return isStirlingGen(stack, getStirlingGen(stack));
    }

    private boolean isStirlingGen(ItemStack stack, TileEntityStirlingGenerator gen) {
        return gen == null ? false : gen.getBurnTime(stack) > 0;
    }
}
