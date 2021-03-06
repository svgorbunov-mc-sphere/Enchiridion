package joshie.enchiridion.designer.features;

import joshie.enchiridion.designer.DesignerHelper;

public class FeatureBox extends FeatureColorable {
    @Override
    public void drawFeature() {
        super.drawFeature();

        DesignerHelper.drawRect(left, top, right, bottom, colorI);
    }
    
    @Override
    public void updateWidth(int change) {
        if (height <= 1) height = 1;
        
        width += change;
        if (width <= 1) {
            width = 1;
        }
    }
}
