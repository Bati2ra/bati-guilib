package net.bati.guilib.gui.components;

import net.bati.guilib.CommonInitializer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public interface IWidgetsStorage {

    HashMap<String, Widget> getWidgets();

    @Nullable
    default Widget getWidget(String key) {
        return getWidgets().get(key);
    }

    default void addWidget(Widget widget) {
        if(getWidgets().containsKey(widget.getIdentifier())) {
            CommonInitializer.LOGGER.warn("[{}] Widget name [{}] is repeated, skipping....", widget, widget.getIdentifier());
            return;
        }
        widget.init();
        widget.setParent(this instanceof Widget ? (Widget)this : null);
        this.getWidgets().put(widget.getIdentifier(), widget);
    }

    default void addWidgets(Widget... widgets) {
        for(Widget widget : widgets)
            addWidget(widget);
    }

    default void removeWidget(String widgetIdentifier) {
        if(!getWidgets().containsKey(widgetIdentifier)) return;
        getWidgets().remove(widgetIdentifier);
    }

}
