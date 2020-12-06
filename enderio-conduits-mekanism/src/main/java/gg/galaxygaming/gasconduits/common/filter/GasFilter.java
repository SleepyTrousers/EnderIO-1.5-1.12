package gg.galaxygaming.gasconduits.common.filter;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.integration.jei.IHaveGhostTargets;
import crazypants.enderio.util.NbtValue;
import gg.galaxygaming.gasconduits.common.utils.GasUtil;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class GasFilter implements IGasFilter {

    private final GasStack[] gases = new GasStack[5];
    private boolean isBlacklist;

    @Override
    public boolean isEmpty() {
        return Arrays.stream(gases).noneMatch(Objects::nonNull);
    }

    @Override
    public int size() {
        return gases.length;
    }

    @Override
    public GasStack getGasStackAt(int index) {
        return index < 0 || index >= gases.length ? null : gases[index];
    }

    @Override
    public boolean setGas(int index, @Nullable GasStack gas) {
        if (index < 0 || index >= gases.length) {
            return false;
        }
        gases[index] = gas == null || gas.getGas() == null ? null : gas;
        return true;
    }

    @Override
    public boolean setGas(int index, @Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            return setGas(index, (GasStack) null);
        }
        if (index < 0 || index >= gases.length) {
            return false;
        }
        GasStack f = GasUtil.getGasTypeFromItem(stack);
        if (f == null || f.getGas() == null) {
            return setGas(index, (GasStack) null);
        }
        return setGas(index, f);
    }

    @Override
    public boolean isBlacklist() {
        return isBlacklist;
    }

    @Override
    public void setBlacklist(boolean isBlacklist) {
        this.isBlacklist = isBlacklist;
    }

    @Override
    public boolean isDefault() {
        return !isBlacklist && isEmpty();
    }

    @Override
    public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
        NbtValue.FILTER_BLACKLIST.setBoolean(nbtRoot, isBlacklist);
        NBTTagList gasList = new NBTTagList();
        int index = 0;
        for (GasStack g : gases) {
            NBTTagCompound fRoot = new NBTTagCompound();
            if (g != null) {
                fRoot.setInteger("index", index);
                g.write(fRoot);
                gasList.appendTag(fRoot);
            }
            index++;
        }
        nbtRoot.setTag("gases", gasList);

    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
        isBlacklist = NbtValue.FILTER_BLACKLIST.getBoolean(nbtRoot);
        clear();

        NBTTagList tagList;
        if (nbtRoot.hasKey("gasses")) {
            //Load legacy data that was saved with a typo
            tagList = nbtRoot.getTagList("gasses", nbtRoot.getId());
        } else {
            tagList = nbtRoot.getTagList("gases", nbtRoot.getId());
        }
        for (int i = 0; i < tagList.tagCount(); i++) {
            gases[i] = GasStack.readFromNBT(tagList.getCompoundTagAt(i));
        }
    }

    private void clear() {
        Arrays.fill(gases, null);
    }

    @Override
    public boolean matchesFilter(GasStack drained) {
        if (drained == null || drained.getGas() == null) {
            return false;
        }
        if (isEmpty()) {
            return true;
        }
        for (GasStack f : gases) {
            if (f != null && f.isGasEqual(drained)) {
                return !isBlacklist;
            }
        }
        return isBlacklist;
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
        setGas(slot, stack);
    }

    @Override
    public void writeToByteBuf(@Nonnull ByteBuf buf) {
        NBTTagCompound root = new NBTTagCompound();
        writeToNBT(root);
        NetworkUtil.writeNBTTagCompound(root, buf);
    }

    @Override
    public void readFromByteBuf(@Nonnull ByteBuf buf) {
        NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
        readFromNBT(tag);
    }

    public void createGhostSlots(@Nonnull NNList<GhostSlot> slots, int xOffset, int yOffset, @Nullable Runnable cb) {
        int index = 0;
        int numRows = 1;
        int rowSpacing = 2;
        int numCols = 5;
        for (int row = 0; row < numRows; ++row) {
            for (int col = 0; col < numCols; ++col) {
                int x = xOffset + col * 18;
                int y = yOffset + row * 18 + rowSpacing * row;
                slots.add(new GasFilterGhostSlot(index, x, y, cb));
                index++;
            }
        }
    }

    @Override
    public int getSlotCount() {
        return gases.length;
    }

    class GasFilterGhostSlot extends GhostSlot implements IHaveGhostTargets.ICustomGhostSlot {

        private final Runnable cb;

        GasFilterGhostSlot(int slot, int x, int y, Runnable cb) {
            this.setX(x);
            this.setY(y);
            this.setSlot(slot);
            this.cb = cb;
        }

        @Override
        public void putStack(@Nonnull ItemStack stack, int realSize) {
            setGas(getSlot(), stack);
            cb.run();
        }

        @Override
        public @Nonnull
        ItemStack getStack() {
            return ItemStack.EMPTY;
        }

        @Override
        public void putIngredient(Object ingredient) {
            GasStack stack = null;
            if (ingredient instanceof Gas) {
                stack = new GasStack((Gas) ingredient, 0);
            } else if (ingredient instanceof GasStack) {
                stack = (GasStack) ingredient;
            }
            setGas(getSlot(), stack);
            cb.run();
        }

        @Override
        public boolean isType(Object ingredient) {
            return ingredient instanceof GasStack || ingredient instanceof Gas;
        }
    }

}