package crazypants.enderio.machine.invpanel;

import com.enderio.core.api.common.util.ITankAccess;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.conduit.item.ItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.generator.zombie.IHasNutrientTank;
import crazypants.enderio.machine.generator.zombie.PacketNutrientTank;
import crazypants.enderio.machine.invpanel.client.ClientDatabaseManager;
import crazypants.enderio.machine.invpanel.client.InventoryDatabaseClient;
import crazypants.enderio.machine.invpanel.server.InventoryDatabaseServer;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.tool.SmartTank;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileInventoryPanel extends AbstractMachineEntity implements IFluidHandler, ITankAccess, IHasNutrientTank {

    public static final int SLOT_CRAFTING_START = 0;
    public static final int SLOT_CRAFTING_RESULT = 9;
    public static final int SLOT_VIEW_FILTER = 10;
    public static final int SLOT_RETURN_START = 11;

    public static final int MAX_STORED_CRAFTING_RECIPES = 6;

    protected final SmartTank fuelTank;
    protected boolean tanksDirty;

    private InventoryDatabaseServer dbServer;
    private InventoryDatabaseClient dbClient;

    private boolean active;
    private boolean extractionDisabled;

    public InventoryPanelContainer eventHandler;
    private IItemFilter itemFilter;

    private int guiSortMode;
    private String guiFilterString = "";
    private boolean guiSync;

    private final ArrayList<StoredCraftingRecipe> storedCraftingRecipes;

    public TileInventoryPanel() {
        super(new SlotDefinition(0, 8, 11, 20, 21, 20));
        this.fuelTank = new SmartTank(EnderIO.fluidNutrientDistillation, Config.inventoryPanelFree ? 0 : 2000);
        this.storedCraftingRecipes = new ArrayList<StoredCraftingRecipe>();
    }

    public InventoryDatabaseServer getDatabaseServer() {
        return dbServer;
    }

    public InventoryDatabaseClient getDatabaseClient(int generation) {
        if (dbClient != null && dbClient.getGeneration() != generation) {
            ClientDatabaseManager.INSTANCE.destroyDatabase(dbClient.getGeneration());
            dbClient = null;
        }
        if (dbClient == null) {
            dbClient = ClientDatabaseManager.INSTANCE.getOrCreateDatabase(generation);
        }
        return dbClient;
    }

    public InventoryDatabaseClient getDatabaseClient() {
        return dbClient;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack var2, int side) {
        return false;
    }

    @Override
    protected boolean canExtractItem(int slot, ItemStack itemstack) {
        return !extractionDisabled && super.canExtractItem(slot, itemstack);
    }

    @Override
    protected boolean isMachineItemValidForSlot(int slot, ItemStack stack) {
        if (slot == SLOT_VIEW_FILTER && stack != null) {
            return FilterRegister.isItemFilter(stack) && FilterRegister.isFilterSet(stack);
        }
        return true;
    }

    @Override
    public ItemStack decrStackSize(int fromSlot, int amount) {
        ItemStack res = super.decrStackSize(fromSlot, amount);
        if (res != null && fromSlot < SLOT_CRAFTING_RESULT && eventHandler != null) {
            eventHandler.onCraftMatrixChanged(this);
        }
        if (res != null && fromSlot == SLOT_VIEW_FILTER) {
            updateItemFilter();
        }
        return res;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack contents) {
        super.setInventorySlotContents(slot, contents);
        if (slot < SLOT_CRAFTING_RESULT && eventHandler != null) {
            eventHandler.onCraftMatrixChanged(this);
        }
        if (slot == SLOT_VIEW_FILTER) {
            updateItemFilter();
        }
    }

    private void updateItemFilter() {
        itemFilter = FilterRegister.getFilterForUpgrade(inventory[SLOT_VIEW_FILTER]);
    }

    public IItemFilter getItemFilter() {
        return itemFilter;
    }

    @Override
    public boolean isActive() {
        return Config.inventoryPanelFree || active;
    }

    @Override
    public void doUpdate() {
        if (worldObj.isRemote) {
            updateEntityClient();
            return;
        }

        if (shouldDoWorkThisTick(20)) {
            scanNetwork();
        }

        if (forceClientUpdate) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            markDirty();
        }

        if (tanksDirty) {
            tanksDirty = false;
            PacketHandler.sendToAllAround(new PacketNutrientTank(this), this);
        }
    }

    private void scanNetwork() {
        ForgeDirection facingDir = getFacingDir();
        ForgeDirection backside = facingDir.getOpposite();

        ItemConduitNetwork icn = null;

        TileEntity te =
                worldObj.getTileEntity(xCoord + backside.offsetX, yCoord + backside.offsetY, zCoord + backside.offsetZ);
        if (te instanceof TileConduitBundle) {
            TileConduitBundle teCB = (TileConduitBundle) te;
            ItemConduit conduit = teCB.getConduit(ItemConduit.class);
            if (conduit != null) {
                icn = (ItemConduitNetwork) conduit.getNetwork();
            }
        }

        if (icn != null) {
            dbServer = icn.getDatabase();
            dbServer.sendChangeLogs();
            refuelPower(dbServer);

            if (active != dbServer.isOperational()) {
                active = dbServer.isOperational();
                forceClientUpdate = true;
            }
        } else {
            if (active) {
                forceClientUpdate = true;
            }
            dbServer = null;
            active = false;
        }
    }

    public float getAvailablePower() {
        return getPower() * Config.inventoryPanelPowerPerMB;
    }

    public void refuelPower(InventoryDatabaseServer db) {
        float missingPower = Config.inventoryPanelPowerPerMB * 0.5f - db.getPower();
        if (missingPower > 0) {
            int amount = (int) Math.ceil(missingPower / Config.inventoryPanelPowerPerMB);
            amount = Math.min(amount, getPower());
            if (amount > 0) {
                useNutrient(amount);
                dbServer.addPower(amount * Config.inventoryPanelPowerPerMB);
            }
        }
    }

    public void useNutrient(int amount) {
        fuelTank.drain(amount, true);
        tanksDirty = true;
    }

    private int getPower() {
        return Config.inventoryPanelFree ? 100 : fuelTank.getFluidAmount();
    }

    @Override
    protected boolean processTasks(boolean redstoneCheckPassed) {
        return false;
    }

    public int getGuiSortMode() {
        return guiSortMode;
    }

    public String getGuiFilterString() {
        return guiFilterString;
    }

    public boolean getGuiSync() {
        return guiSync;
    }

    public void setGuiParameter(int sortMode, String filterString, boolean sync) {
        this.guiSortMode = sortMode;
        this.guiFilterString = filterString;
        this.guiSync = sync;
        if (worldObj != null && worldObj.isRemote) {
            PacketHandler.INSTANCE.sendToServer(new PacketGuiSettings(this, sortMode, filterString, sync));
        } else {
            markDirty();
        }
    }

    public int getStoredCraftingRecipes() {
        return storedCraftingRecipes.size();
    }

    public StoredCraftingRecipe getStoredCraftingRecipe(int index) {
        if (index < 0 || index >= storedCraftingRecipes.size()) {
            return null;
        }
        return storedCraftingRecipes.get(index);
    }

    public void addStoredCraftingRecipe(StoredCraftingRecipe recipe) {
        if (worldObj != null && worldObj.isRemote) {
            PacketHandler.INSTANCE.sendToServer(
                    new PacketStoredCraftingRecipe(PacketStoredCraftingRecipe.ACTION_ADD, 0, recipe));
        } else {
            storedCraftingRecipes.add(recipe);
            markDirty();
            updateBlock();
        }
    }

    public void removeStoredCraftingRecipe(int index) {
        if (worldObj != null && worldObj.isRemote) {
            PacketHandler.INSTANCE.sendToServer(
                    new PacketStoredCraftingRecipe(PacketStoredCraftingRecipe.ACTION_DELETE, index, null));
        } else if (index >= 0 && index < storedCraftingRecipes.size()) {
            storedCraftingRecipes.remove(index);
            markDirty();
            updateBlock();
        }
    }

    public boolean isExtractionDisabled() {
        return extractionDisabled;
    }

    public void setExtractionDisabled(boolean extractionDisabled) {
        if (worldObj != null) {
            if (worldObj.isRemote) {
                PacketHandler.INSTANCE.sendToServer(new PacketSetExtractionDisabled(this, extractionDisabled));
            } else if (this.extractionDisabled != extractionDisabled) {
                this.extractionDisabled = extractionDisabled;
                PacketHandler.INSTANCE.sendToDimension(
                        new PacketUpdateExtractionDisabled(this, extractionDisabled), worldObj.provider.dimensionId);
            }
        }
    }

    /**
     * This is called by PacketUpdateExtractionDisabled on the client side
     * @param extractionDisabled if extraction is disabled
     */
    void updateExtractionDisabled(boolean extractionDisabled) {
        this.extractionDisabled = extractionDisabled;
    }

    @Override
    public void writeCommon(NBTTagCompound nbtRoot) {
        super.writeCommon(nbtRoot);
        fuelTank.writeCommon("fuelTank", nbtRoot);
        nbtRoot.setInteger("guiSortMode", guiSortMode);
        nbtRoot.setString("guiFilterString", guiFilterString);
        nbtRoot.setBoolean("guiSync", guiSync);
        nbtRoot.setBoolean("extractionDisabled", extractionDisabled);

        if (!storedCraftingRecipes.isEmpty()) {
            NBTTagList recipesNBT = new NBTTagList();
            for (StoredCraftingRecipe recipe : storedCraftingRecipes) {
                NBTTagCompound recipeNBT = new NBTTagCompound();
                recipe.writeToNBT(recipeNBT);
                recipesNBT.appendTag(recipeNBT);
            }
            nbtRoot.setTag("craftingRecipes", recipesNBT);
        }
    }

    @Override
    public void readCommon(NBTTagCompound nbtRoot) {
        super.readCommon(nbtRoot);
        fuelTank.readCommon("fuelTank", nbtRoot);
        guiSortMode = nbtRoot.getInteger("guiSortMode");
        guiFilterString = nbtRoot.getString("guiFilterString");
        guiSync = nbtRoot.getBoolean("guiSync");
        extractionDisabled = nbtRoot.getBoolean("extractionDisabled");
        faceModes = null;

        storedCraftingRecipes.clear();
        NBTTagList recipesNBT = (NBTTagList) nbtRoot.getTag("craftingRecipes");
        if (recipesNBT != null) {
            for (int idx = 0;
                    idx < recipesNBT.tagCount() && storedCraftingRecipes.size() < MAX_STORED_CRAFTING_RECIPES;
                    idx++) {
                NBTTagCompound recipeNBT = recipesNBT.getCompoundTagAt(idx);
                StoredCraftingRecipe recipe = new StoredCraftingRecipe();
                if (recipe.readFromNBT(recipeNBT)) {
                    storedCraftingRecipes.add(recipe);
                }
            }
        }

        if (eventHandler != null) {
            eventHandler.checkCraftingRecipes();
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtRoot) {
        super.readCustomNBT(nbtRoot);
        active = nbtRoot.getBoolean("active");
        updateItemFilter();
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtRoot) {
        super.writeCustomNBT(nbtRoot);
        nbtRoot.setBoolean("active", active);
    }

    @Override
    public String getMachineName() {
        return ModObject.blockInventoryPanel.unlocalisedName;
    }

    @Override
    public IoMode getIoMode(ForgeDirection face) {
        return face == getIODirection() ? IoMode.NONE : IoMode.DISABLED;
    }

    @Override
    public void setIoMode(ForgeDirection faceHit, IoMode mode) {}

    @Override
    public IoMode toggleIoModeForFace(ForgeDirection faceHit) {
        return getIoMode(faceHit);
    }

    private ForgeDirection getIODirection() {
        return getFacingDir().getOpposite();
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (from != getIODirection()) {
            return 0;
        }
        int res = fuelTank.fill(resource, doFill);
        if (res > 0 && doFill) {
            tanksDirty = true;
        }
        return res;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return from == getIODirection() && fuelTank.canFill(fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if (from == getIODirection()) {
            return new FluidTankInfo[] {fuelTank.getInfo()};
        } else {
            return new FluidTankInfo[0];
        }
    }

    @Override
    public FluidTank getInputTank(FluidStack forFluidType) {
        if (forFluidType != null && fuelTank.canFill(forFluidType.getFluid())) {
            return fuelTank;
        }
        return null;
    }

    @Override
    public FluidTank[] getOutputTanks() {
        return new FluidTank[0];
    }

    @Override
    public void setTanksDirty() {
        tanksDirty = true;
    }

    @Override
    public SmartTank getNutrientTank() {
        return fuelTank;
    }
}
