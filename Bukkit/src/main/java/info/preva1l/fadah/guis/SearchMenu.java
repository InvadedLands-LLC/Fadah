package info.preva1l.fadah.guis;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Menus;
import info.preva1l.fadah.utils.TaskManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.function.Consumer;

public class SearchMenu implements Listener {
    public SearchMenu(Player player, String placeholder, Consumer<String> callback) {
        AnvilGUI.Builder guiBuilder = new AnvilGUI.Builder().plugin(Fadah.getINSTANCE())
                .title(Menus.SEARCH_TITLE.toFormattedString());
        guiBuilder.text(placeholder);

        guiBuilder.onClick((slot, state) -> {
            if (slot != AnvilGUI.Slot.OUTPUT) {
                return Collections.emptyList();
            }

            String search = state.getText();

            return Collections.singletonList(AnvilGUI.ResponseAction.run(() ->
                    callback.accept((search != null && search.contains(placeholder)) ? null : search)));
        });

        guiBuilder.onClose((state)-> {
            String search = state.getText();
            TaskManager.Sync.runLater(Fadah.getINSTANCE(), () ->
                    callback.accept((search != null && search.contains(placeholder)) ? null : search),1L);
        });

        guiBuilder.open(player);
    }
}
