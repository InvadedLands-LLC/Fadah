package info.preva1l.fadah.utils.guis;

import com.github.puregero.multilib.MultiLib;
import info.preva1l.fadah.Fadah;
import io.papermc.paper.event.packet.UncheckedSignChangeEvent;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SignPromptFactory {

    public void openPrompt(@Nonnull Player player, @Nonnull List<Component> lines, @Nonnull ResponseHandler responseHandler) {
        Sign sign = (Sign) Material.OAK_WALL_SIGN.createBlockData().createBlockState();
        Location location = player.getLocation().clone().add(player.getEyeLocation().getDirection().multiply(-3));

        for (int i = 0; i < 4; i++) {
            Component line = (lines.size() > i ? lines.get(i) : Component.empty());
            sign.getSide(Side.FRONT).line(i, line);
        }

        player.sendBlockChange(location, sign.getBlockData());
        player.sendBlockUpdate(location, sign);
        player.openVirtualSign(Position.block(location), Side.FRONT);

        final AtomicBoolean active = new AtomicBoolean(true);

        Bukkit.getPluginManager().registerEvents(
                new Listener() {
                    @EventHandler
                    public void onUncheckedSignChange(UncheckedSignChangeEvent event) {
                        Player receiver = event.getPlayer();

                        if (!receiver.getUniqueId().equals(player.getUniqueId())) {
                            return;
                        }

                        BlockPosition pos = event.getEditedBlockPosition();

                        if (pos.blockX() != location.getBlockX()
                                || pos.blockY() != location.getBlockY()
                                || pos.blockZ() != location.getBlockZ()) {
                            return;
                        }

                        if (!active.getAndSet(false)) {
                            return;
                        }

                        List<String> input = event.lines().stream()
                                .map(c -> PlainTextComponentSerializer.plainText().serialize(c))
                                .toList();
                        Response response = responseHandler.handleResponse(input);

                        if (response == Response.TRY_AGAIN) {
                            MultiLib.getEntityScheduler(player).runDelayed(Fadah.getINSTANCE(), o -> {
                                if (player.isOnline()) {
                                    openPrompt(player, lines, responseHandler);
                                }
                            }, null, 1);
                        }

                        HandlerList.unregisterAll(this);

                        MultiLib.getRegionScheduler().run(Fadah.getINSTANCE(), location, o -> {
                            BlockData blockData = location.getBlock().getBlockData();
                            player.sendBlockChange(location, blockData);
                        });
                    }
                }, Fadah.getINSTANCE()
        );
    }

    @FunctionalInterface
    public interface ResponseHandler {

        @Nonnull
        Response handleResponse(@Nonnull List<String> lines);

    }

    public enum Response {
        ACCEPTED,
        TRY_AGAIN
    }

}
