package joshie.enchiridion.wiki.data;

import static joshie.enchiridion.wiki.WikiHelper.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.helpers.ClientHelper;
import joshie.enchiridion.wiki.WikiCategory;
import joshie.enchiridion.wiki.WikiPage;
import joshie.enchiridion.wiki.gui.GuiHistory;
import joshie.enchiridion.wiki.mode.DisplayMode;
import joshie.enchiridion.wiki.mode.SaveMode;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class WikiData {
    private HashMap<String, Data> translate = new HashMap();
    private HashSet<WikiPage> pages = new HashSet();
    private ArrayList<WikiPage> sorted = new ArrayList();
    private static final WikiData instance = new WikiData();

    public static WikiData instance() {
        return instance;
    }

    public void addData(String key, Data data) {
        this.translate.put(key, data);
    }

    public void addPage(WikiPage page) {
        pages.add(page);
    }

    public void removePage(WikiPage page) {
        pages.remove(page);
        translate.remove(page.getUnlocalized() + "." + ClientHelper.getLang());
        WikiCategory cat = page.getCategory();
        cat.getPages().remove(page);
        cat.markDirty();
        GuiHistory.delete();
        SaveMode.getInstance().markDirty();
        if (EConfig.EDIT_ENABLED) {
            gui.setMode(SaveMode.getInstance());
        } else gui.setMode(DisplayMode.getInstance());
    }

    public ArrayList<WikiPage> getPages() {
        return sorted;
    }

    public void updateSearch(String str) {
        if (str == null || str.equals("")) sorted = new ArrayList(pages);
        else {
            sorted = new ArrayList();
            for (WikiPage page : pages) {
                String search = str.toLowerCase();
                if (search.startsWith("@")) {
                    if (page.getCategory().getTab().getMod().getTitle().toLowerCase().contains(search.substring(1))) {
                        if (page.getData().isPrioritised()) {
                            sorted.add(0, page);
                        } else sorted.add(page);
                    }
                } else if (page.getTitle().toLowerCase().contains(search)) {
                    sorted.add(page);
                }

                if (sorted.size() >= 15) {
                    break;
                }
            }
        }
    }

    public String translateToLocal(String unlocalized) {
        String ret = getData(unlocalized, ClientHelper.getLang()).getLocalisation();
        if (ret == null || ret.equals(unlocalized + "." + ClientHelper.getLang()) || ret.equals("")) {
            return StatCollector.translateToLocal("wiki." + unlocalized.replace(" ", "").toLowerCase());
        } else return ret;
    }

    public Data getData(String unlocalized, String lang) {
        String key = unlocalized + "." + lang;
        Data data = translate.get(key);
        if (data != null) {
            return data;
        } else {
            data = translate.get(unlocalized + ".en_US");
            if (data != null) {
                return data;
            } else {
                data = new Data(key);
                translate.put(key, data);
                return data;
            }
        }
    }

    public DataPage getPage(String unlocalized, String lang) {
        String key = unlocalized + "." + lang;
        Data data = translate.get(key);
        if (data instanceof DataPage) {
            return (DataPage) data;
        } else {
            data = translate.get(unlocalized + ".en_US");
            if (data instanceof DataPage) {
                return (DataPage) data;
            } else {
                data = new DataPage(key);
                translate.put(key, data);
                return (DataPage) data;
            }
        }
    }

    public DataTab getTab(String unlocalized, String lang) {
        String key = unlocalized + "." + lang;
        Data data = translate.get(key);
        if (data instanceof DataTab) {
            return (DataTab) data;
        } else {
            data = translate.get(unlocalized + ".en_US");
            if (data instanceof DataTab) {
                return (DataTab) data;
            } else {
                data = new DataTab(key, new ItemStack(Blocks.stone));
                translate.put(key, data);
                return (DataTab) data;
            }
        }
    }
}
