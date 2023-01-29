package crazypants.enderio.machine.power;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;

public class PowerDisplayUtil {

    public static final NumberFormat INT_NF = NumberFormat.getIntegerInstance();
    public static final NumberFormat FLOAT_NF = NumberFormat.getInstance();

    // Handle french local 'non breaking space' character used to separate thousands.
    // This is not rendered correctly and cannot be parsed by minecraft so replace it with a regular space
    private static final boolean REPLACE_NBSP;
    private static final char NBSP = (char) 160;

    static {
        boolean res = false;
        if (INT_NF instanceof DecimalFormat) {
            DecimalFormatSymbols syms = ((DecimalFormat) INT_NF).getDecimalFormatSymbols();
            if (syms.getGroupingSeparator() == NBSP) {
                res = true;
            }
        }
        REPLACE_NBSP = res;
    }

    public static String perTickStr() {
        return EnderIO.lang.localize("power.tick");
    }

    public static String ofStr() {
        return EnderIO.lang.localize("gui.powerMonitor.of");
    }

    static {
        FLOAT_NF.setMinimumFractionDigits(1);
        FLOAT_NF.setMaximumFractionDigits(1);
    }

    public static String getStoredEnergyString(ItemStack item) {
        if (item == null) {
            return null;
        }
        if (!(item.getItem() instanceof IEnergyContainerItem)) {
            return null;
        }

        IEnergyContainerItem ci = (IEnergyContainerItem) item.getItem();
        return EnderIO.lang.localize("item.tooltip.power") + " "
                + PowerDisplayUtil.formatPower(ci.getEnergyStored(item))
                + "/"
                + PowerDisplayUtil.formatPower(ci.getMaxEnergyStored(item))
                + " "
                + PowerDisplayUtil.abrevation();
    }

    public static String formatPowerPerTick(int powerPerTick) {
        return formatPower(powerPerTick) + " " + abrevation() + perTickStr();
    }

    public static String formatStoredPower(int amount, int capacity) {
        return formatPower(amount) + "/" + formatPower(capacity) + " " + PowerDisplayUtil.abrevation();
    }

    public static String formatPower(long amount) {
        String str = INT_NF.format(amount);
        if (REPLACE_NBSP) {
            str = str.replace(NBSP, ' ');
        }
        return str;
    }

    public static String formatPower(int powerRF) {
        String str = INT_NF.format(powerRF);
        if (REPLACE_NBSP) {
            str = str.replace(NBSP, ' ');
        }
        return str;
    }

    public static String formatPowerFloat(float averageRfTickSent) {
        return FLOAT_NF.format(averageRfTickSent);
    }

    public static Integer parsePower(String power) {
        if (power == null) {
            return null;
        }
        try {
            if (REPLACE_NBSP) {
                power = power.replace(' ', NBSP);
            }
            Number d = INT_NF.parse(power);
            if (d == null) {
                return null;
            }
            return d.intValue();
        } catch (Exception e) {
            return null;
        }
    }

    public static String abrevation() {
        return EnderIO.lang.localize("power.rf");
    }

    @SideOnly(Side.CLIENT)
    public static int parsePower(GuiTextField tf) {
        String txt = tf.getText();
        try {
            Integer power = PowerDisplayUtil.parsePower(txt);
            if (power == null) {
                return -1;
            }
            return power.intValue();
        } catch (Exception e) {
            return -1;
        }
    }
}
