package joshie.enchiridion.designer.features;

import static joshie.enchiridion.wiki.WikiHelper.mouseX;

import java.util.ArrayList;
import java.util.List;

import joshie.enchiridion.designer.DesignerHelper;
import joshie.enchiridion.helpers.ClientHelper;
import joshie.enchiridion.helpers.ItemHelper;
import joshie.enchiridion.helpers.StackHelper;
import net.minecraft.item.ItemStack;

import com.google.gson.annotations.Expose;

public class FeatureItem extends FeatureWithText {
    @Expose
    public String item;
    public ItemStack stack;
    public float size;
    private String search = "";
    protected boolean drawStack = true;

    //List of items
    private static ArrayList<ItemStack> sorted;
    private static int position;

    public FeatureItem() {
        width = 32;
        height = 32;
        item = "minecraft:stone";
    }

    protected void setItemStack(ItemStack stack) {
        this.stack = stack;
        this.item = StackHelper.getStringFromStack(stack);
    }

    @Override
    public void recalculate(int x, int y) {
        super.recalculate(x, y);
        height = width;
        size = (float) (width / 16D);
    }

    @Override
    public String getTextField() {
        return this.search;
    }

    @Override
    public void setTextField(String str) {
        this.search = str;
        this.updateSearch();
    }

    @Override
    public void drawFeature() {
        if (stack == null) {
            stack = StackHelper.getStackFromString(item);
        }

        if (drawStack) {
            DesignerHelper.drawStack(stack, left, top, size);
        }

        //Draw The Search stuff
        if (isSelected) {
            DesignerHelper.drawRect(-102, -55, -100, -37, 0xFFFFFFFF);
            DesignerHelper.drawRect(0, -55, 2, -37, 0xFFFFFFFF);
            DesignerHelper.drawRect(-102, -57, 2, -55, 0xFFFFFFFF);
            DesignerHelper.drawRect(-100, -55, 0, -37, 0xFF000000);
            DesignerHelper.drawRect(-100, -37, 0, -39, 0xFFFFFFFF);
            DesignerHelper.drawSplitString(getText(), -95, -50, 250, 0xFFFFFFFF);

            DesignerHelper.drawRect(-98, -37, -12, 230, 0xFF000000);
            DesignerHelper.drawRect(-100, -37, -98, 230, 0xFFFFFFFF);
            DesignerHelper.drawRect(-14, -37, -12, 230, 0xFFFFFFFF);
            DesignerHelper.drawRect(-98, 230, -12, 232, 0xFFFFFFFF);
            if (sorted != null) {
                int j = 0;
                int k = 0;
                for (int i = position; i < position + 80; i++) {
                    if (i >= 0 && i < sorted.size()) {
                        //Changed 32 > 16 && 2F > 1F && 7 > 4, and numbers to -95 and -30
                        DesignerHelper.drawStack(sorted.get(i), (j * 16) - 95, (k * 16) - 30, 1F);

                        j++;

                        if (j > 4) {
                            j = 0;
                            k++;
                        }
                    }
                }
            } else updateSearch();
        }
    }

    @Override
    public void click(int x, int y) {
        if (DesignerHelper.getGui().canEdit && isSelected) {
            int j = 0, k = 0;
            for (int i = position; i < position + 80; i++) {
                if (i >= 0 && i < sorted.size()) {
                    if (x >= (j * 16) - 95 && x <= (j * 16) - 95 + 16) {
                        if (y >= (k * 16) - 30 && y <= (k * 16) - 30 + 16) {
                            setItemStack(sorted.get(i));
                        }
                    }

                    j++;

                    if (j > 4) {
                        j = 0;
                        k++;
                    }
                }
            }
        }

        super.click(x, y);
    }

    @Override
    public void addTooltip(List list) {
        if (stack != null) {
            list.addAll(stack.getTooltip(ClientHelper.getPlayer(), false));
        }
    }

    @Override
    public boolean scroll(boolean scrolledDown) {
        if (isSelected) {
            if (mouseX <= 0) {
                if (scrolledDown) {
                    position = Math.min(sorted.size() - 200, position + 5);
                } else {
                    position = Math.max(0, position - 5);
                }

                return true;
            }
        }

        return false;
    }

    public void updateSearch() {
        ItemHelper.addInventory();

        if (search == null || search.equals("")) {
            sorted = new ArrayList(ItemHelper.allItems());
        } else {
            position = 0;
            sorted = new ArrayList();
            for (ItemStack stack : ItemHelper.allItems()) {
                try {
                    if (stack != null && stack.getItem() != null) {
                        if (stack.getDisplayName().toLowerCase().contains(search.toLowerCase())) {
                            sorted.add(stack);
                        }
                    }
                } catch (Exception e) {}
            }
        }
    }
}
