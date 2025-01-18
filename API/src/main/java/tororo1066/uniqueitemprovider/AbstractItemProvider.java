package tororo1066.uniqueitemprovider;

import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractItemProvider {

    protected final NamespacedKey namespacedKey;

    abstract public @NotNull PersistentDataContainer createPersistentDataContainer(@NotNull PersistentDataContainer container);

    abstract public void loadPersistentDataContainer(@NotNull PersistentDataContainer container);

    public AbstractItemProvider(@NotNull NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public void onInteract(@NotNull PlayerInteractEvent e, @NotNull IUniqueItem item) {}

    public void onConsume(@NotNull PlayerItemConsumeEvent e, @NotNull IUniqueItem item) {}

    public void onDrop(@NotNull PlayerDropItemEvent e, @NotNull IUniqueItem item) {}

    public void onItemClick(@NotNull InventoryClickEvent e, @NotNull IUniqueItem item) {}

    public void onHeld(@NotNull PlayerItemHeldEvent e, @NotNull IUniqueItem item) {}
}