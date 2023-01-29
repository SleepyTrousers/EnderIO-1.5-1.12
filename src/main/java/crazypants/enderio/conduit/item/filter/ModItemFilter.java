package crazypants.enderio.conduit.item.filter;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.gui.item.IItemFilterGui;
import crazypants.enderio.conduit.gui.item.ModItemFilterGui;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.NetworkedInventory;
import io.netty.buffer.ByteBuf;

public class ModItemFilter implements IItemFilter {

    private final String[] mods = new String[3];
    private boolean blacklist = false;

    public String setMod(int index, ItemStack itemStack) {
        if (index < 0 || index >= mods.length) {
            return null;
        }

        if (itemStack == null || itemStack.getItem() == null) {
            setMod(index, (String) null);
            return null;
        }
        UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(itemStack.getItem());
        if (ui == null) {
            setMod(index, (String) null);
            return null;
        }
        String targetMod = ui.modId;
        if (targetMod == null) {
            setMod(index, (String) null);
            return null;
        }
        setMod(index, targetMod);
        return targetMod;
    }

    public void setMod(int index, String mod) {
        if (index < 0 || index >= mods.length) {
            return;
        }
        mods[index] = mod;
    }

    public String getModAt(int index) {
        if (index < 0 || index >= mods.length) {
            return null;
        }
        return mods[index];
    }

    public void setBlacklist(boolean value) {
        blacklist = value;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    @Override
    public boolean doesItemPassFilter(NetworkedInventory inv, ItemStack item) {
        if (item == null || item.getItem() == null) {
            return false;
        }
        UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(item.getItem());
        if (ui == null) {
            return false;
        }
        String targetMod = ui.modId;
        if (targetMod != null) {
            for (String mod : mods) {
                if (targetMod.equals(mod)) {
                    return !blacklist;
                }
            }
        }
        return blacklist;
    }

    @Override
    public boolean doesFilterCaptureStack(NetworkedInventory inv, ItemStack item) {
        return false;
    }

    @Override
    public boolean isSticky() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void createGhostSlots(List<GhostSlot> slots, int xOffset, int yOffset, Runnable cb) {}

    @Override
    public int getSlotCount() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemFilterGui getGui(GuiExternalConnection gui, IItemConduit itemConduit, boolean isInput) {
        return new ModItemFilterGui(gui, itemConduit, isInput);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtRoot) {
        for (int i = 0; i < mods.length; i++) {
            String mod = nbtRoot.getString("mod" + i);
            if (mod == null || mod.isEmpty() || "-".equals(mod)) {
                mods[i] = null;
            } else {
                mods[i] = mod;
            }
            if (nbtRoot.hasKey("blacklist")) {
                blacklist = nbtRoot.getBoolean("blacklist");
            } else {
                blacklist = false;
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtRoot) {
        for (int i = 0; i < mods.length; i++) {
            String mod = mods[i];
            if (mod == null || mod.trim().isEmpty()) {
                nbtRoot.setString("mod" + i, "-");
            } else {
                nbtRoot.setString("mod" + i, mod);
            }
        }
        nbtRoot.setBoolean("blacklist", blacklist);
    }

    @Override
    public void writeToByteBuf(ByteBuf buf) {
        NBTTagCompound root = new NBTTagCompound();
        writeToNBT(root);
        NetworkUtil.writeNBTTagCompound(root, buf);
    }

    @Override
    public void readFromByteBuf(ByteBuf buf) {
        NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
        readFromNBT(tag);
    }

    @Override
    public String getUnlocalizedName() {
        return "gui.mod_item_filter";
    }
}
