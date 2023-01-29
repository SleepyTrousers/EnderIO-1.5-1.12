package crazypants.enderio.conduit.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.common.Optional;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.gas.IGasConduit;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.me.IMEConduit;
import crazypants.enderio.conduit.oc.IOCConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;

public class GuiExternalConnection extends GuiContainerBaseEIO {

    private static final int TAB_HEIGHT = 24;

    private static int nextButtonId = 1;

    public static int nextButtonId() {
        return nextButtonId++;
    }

    public static int nextButtonIds(int count) {
        int id = nextButtonId;
        nextButtonId += count;
        return id;
    }

    private static final Map<Class<? extends IConduit>, Integer> TAB_ORDER = new HashMap<Class<? extends IConduit>, Integer>();

    static {
        TAB_ORDER.put(IItemConduit.class, 0);
        TAB_ORDER.put(ILiquidConduit.class, 1);
        TAB_ORDER.put(IRedstoneConduit.class, 2);
        TAB_ORDER.put(IPowerConduit.class, 3);
        TAB_ORDER.put(IMEConduit.class, 4);
        TAB_ORDER.put(IGasConduit.class, 5);
        TAB_ORDER.put(IOCConduit.class, 6);
    }

    final InventoryPlayer playerInv;
    final IConduitBundle bundle;
    private final ForgeDirection dir;

    private final List<IConduit> conduits = new ArrayList<IConduit>();
    private final List<ITabPanel> tabs = new ArrayList<ITabPanel>();
    private int activeTab = 0;

    private int tabYOffset = 4;

    private final ExternalConnectionContainer container;

    public GuiExternalConnection(InventoryPlayer playerInv, IConduitBundle bundle, ForgeDirection dir) {
        super(new ExternalConnectionContainer(playerInv, bundle, dir), "externalConduitConnection", "itemFilter");
        container = (ExternalConnectionContainer) inventorySlots;
        this.playerInv = playerInv;
        this.bundle = bundle;
        this.dir = dir;

        ySize = 166 + 29 + 48;
        xSize = 206;

        container.setInoutSlotsVisible(false, false);

        List<IConduit> cons = new ArrayList<IConduit>(bundle.getConduits());
        Collections.sort(cons, new Comparator<IConduit>() {

            @Override
            public int compare(IConduit o1, IConduit o2) {
                Integer int1 = TAB_ORDER.get(o1.getBaseConduitType());
                if (int1 == null) {
                    return 1;
                }
                Integer int2 = TAB_ORDER.get(o2.getBaseConduitType());
                if (int2 == null) {
                    return 1;
                }
                return Integer.compare(int1, int2);
            }
        });

        for (IConduit con : cons) {
            if (con.containsExternalConnection(dir) || con.canConnectToExternal(dir, true)) {
                @SuppressWarnings("LeakingThisInConstructor")
                ITabPanel tab = TabFactory.instance.createPanelForConduit(this, con);
                if (tab != null) {
                    conduits.add(con);
                    tabs.add(tab);
                    tab.deactivate();
                }
            }
        }
    }

    public IConduitBundle getBundle() {
        return bundle;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        ((ExternalConnectionContainer) inventorySlots).createGhostSlots(getGhostSlots());
        for (int i = 0; i < tabs.size(); i++) {
            if (i == activeTab) {
                tabs.get(i).onGuiInit(guiLeft + 10, guiTop, xSize - 20, ySize - 20);
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Returns true when slots outside of standard gui bounds are clicked to prevent setting ghost filters dropping
     * items on the ground.
     */
    private boolean handleOobClick(int x, int y, int button) {
        boolean outOfBounds = x < guiLeft || y < guiTop || x >= guiLeft + xSize || y >= guiTop + ySize;
        boolean hasItem = playerInv.getItemStack() != null;
        if (outOfBounds && hasItem && !getGhostSlots().isEmpty()) {
            GhostSlot slot = getGhostSlot(x, y);
            if (slot != null) {
                ghostSlotClicked(slot, x, y, button);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void mouseMovedOrUp(int x, int y, int button) {
        if (handleOobClick(x, y, button)) {
            return;
        }
        super.mouseMovedOrUp(x, y, button);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        // handle out-of-bounds ghost slots
        if (handleOobClick(x, y, button)) {
            return;
        }

        int tabLeftX = xSize;
        int tabRightX = tabLeftX + 22;

        int minY = tabYOffset;
        int maxY = minY + (conduits.size() * TAB_HEIGHT);

        x = (x - guiLeft);
        y = (y - guiTop);

        if (x > tabLeftX && x < tabRightX + 24) {
            if (y > minY && y < maxY) {
                tabs.get(activeTab).deactivate();
                getGhostSlots().clear();
                activeTab = (y - minY) / 24;
                initGui();
                return;
            }
        }
        tabs.get(activeTab).mouseClicked(x, y, button);
        super.mouseClicked(x + guiLeft, y + guiTop, button);
    }

    public void setSize(int x, int y) {
        xSize = x;
        ySize = y;
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        super.actionPerformed(guiButton);
        tabs.get(activeTab).actionPerformed(guiButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;
        int tabX = sx + xSize - 3;

        Tessellator tes = Tessellator.instance;
        tes.startDrawingQuads();
        for (int i = 0; i < tabs.size(); i++) {
            if (i != activeTab) {
                RenderUtil.bindTexture(IconEIO.TEXTURE);
                IconEIO.map.render(IconEIO.INACTIVE_TAB, tabX, sy + tabYOffset + (i * 24));
                IWidgetIcon icon = tabs.get(i).getIcon();
                icon.getMap().render(icon, tabX - 1, sy + tabYOffset + (i * TAB_HEIGHT) + 4);
            }
        }

        tes.draw();

        int textureHeight = 166 + 29;
        if (tabs.isEmpty()) {
            bindGuiTexture();
        } else {
            ITabPanel tab = tabs.get(activeTab);
            RenderUtil.bindTexture(tab.getTexture());
            if (tab instanceof BaseSettingsPanel) {
                textureHeight = ((BaseSettingsPanel) tab).getTextureHeight();
            }
        }
        drawTexturedModalRect(sx, sy, 0, 0, this.xSize, textureHeight);

        RenderUtil.bindTexture(IconEIO.TEXTURE);
        tes.startDrawingQuads();
        IconEIO.map.render(IconEIO.ACTIVE_TAB, tabX, sy + tabYOffset + (activeTab * TAB_HEIGHT));

        if (tabs.size() > 0) {
            IWidgetIcon icon = tabs.get(activeTab).getIcon();
            icon.getMap().render(icon, tabX - 1, sy + tabYOffset + (activeTab * TAB_HEIGHT) + 4);
            tes.draw();
            tabs.get(activeTab).render(par1, par2, par3);
        } else {
            tes.draw();
        }

        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    }

    public ForgeDirection getDir() {
        return dir;
    }

    public ExternalConnectionContainer getContainer() {
        return container;
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public boolean hideItemPanelSlot(GuiContainer gc, int x, int y, int w, int h) {
        int sx = (width - xSize) / 2;
        int sy = (height - ySize) / 2;
        if (tabs.size() > 0) {
            int tabX = sx + xSize - 3;
            int tabY = sy + tabYOffset;

            if ((x + w) >= tabX && x < (tabX + 14) && (y + h) >= tabY && y < (tabY + tabs.size() * TAB_HEIGHT)) {
                return true;
            }
        }
        List<GhostSlot> slots = getGhostSlots();
        if (slots != null && !slots.isEmpty()) {
            for (GhostSlot slot : slots) {
                int slotX = sx + slot.x;
                int slotY = sy + slot.y;
                if ((x + w) >= slotX && x < (slotX + 20) && (y + h) >= slotY && y < (slotY + 20)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Optional.Method(modid = "NotEnoughItems")
    public boolean handleDragNDrop(GuiContainer gc, int x, int y, ItemStack is, int button) {
        if (super.handleDragNDrop(gc, x, y, is, button)) {
            return true;
        }
        x -= guiLeft;
        y -= guiTop;
        if (is != null && is.stackSize > 0 && !tabs.isEmpty() && tabs.get(activeTab) instanceof LiquidSettings) {
            LiquidSettings settings = (LiquidSettings) tabs.get(activeTab);
            if (settings.setFilterFromItem(x, y, is)) {
                is.stackSize = 0;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void drawFakeItemStack(int x, int y, ItemStack stack) {
        super.drawFakeItemStack(x, y, stack);
        itemRender.renderItemOverlayIntoGUI(fontRendererObj, mc.renderEngine, stack, x, y, "");
    }

    public void clearGhostSlots() {
        getGhostSlots().clear();
        ((ExternalConnectionContainer) inventorySlots).createGhostSlots(getGhostSlots());
    }
}
