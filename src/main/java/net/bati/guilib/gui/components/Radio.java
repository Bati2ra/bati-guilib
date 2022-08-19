package net.bati.guilib.gui.components;

import net.bati.guilib.CommonInitializer;

public class Radio extends AlignedContainer {

    public Radio(String identifier) {
        super(identifier);
    }


    public void addOption(Checkbox checkbox) {
        if(getWidgets().containsKey(checkbox.getIdentifier())) {
            CommonInitializer.LOGGER.warn("[{}] Widget name [{}] is repeated, skipping....", checkbox, checkbox.getIdentifier());
            return;
        }
        checkbox.init();

        checkbox.setParent(this);
        this.getWidgets().put(checkbox.getIdentifier(), checkbox);
    }

    @Override
    public void addWidget(Widget widget) {

    }

    public void updateWidgets(String exception) {
        getWidgets().forEach((key, value) -> {
            if(key.contentEquals(exception)) return;

            if( ((Checkbox)value).checked) {
                ((Checkbox) value).checked = false;
            }
        });
    }
}
