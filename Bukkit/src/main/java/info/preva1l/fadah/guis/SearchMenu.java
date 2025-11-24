package info.preva1l.fadah.guis;

import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.utils.TaskManager;
import info.preva1l.fadah.utils.guis.SignPromptFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class SearchMenu implements Listener {

    private static final SignPromptFactory signPromptFactory = new SignPromptFactory();
    private static final List<Component> lines = List.of(
            Component.empty(),
            Component.text("↑↑↑↑↑↑↑↑↑↑↑↑"),
            Component.text("Search")
    );

    public SearchMenu(Player player, String placeholder, Consumer<String> callback) {
        SignPromptFactory.ResponseHandler responseHandler = new SignPromptFactory.ResponseHandler() {
            @Override
            public @NotNull SignPromptFactory.Response handleResponse(@NotNull List<String> list) {
                String search = list.get(0);
                TaskManager.Sync.runLater(Fadah.getINSTANCE(), () -> {
                    callback.accept((search != null && search.contains(placeholder)) ? null : search);
                },1L);
                return SignPromptFactory.Response.ACCEPTED;
            }
        };
        signPromptFactory.openPrompt(player, lines, responseHandler);
    }

}
