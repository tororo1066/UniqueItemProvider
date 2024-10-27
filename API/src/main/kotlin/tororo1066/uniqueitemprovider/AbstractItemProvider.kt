package tororo1066.uniqueitemprovider

import org.bukkit.NamespacedKey
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerItemHeldEvent

abstract class AbstractItemProvider(val namespacedKey: NamespacedKey) {

    open fun onInteract(e: PlayerInteractEvent, item: IUniqueItem) {}

    open fun onConsume(e: PlayerItemConsumeEvent, item: IUniqueItem) {}

    open fun onDrop(e: PlayerDropItemEvent, item: IUniqueItem) {}

    open fun onItemClick(e: InventoryClickEvent, item: IUniqueItem) {}

    open fun onHeld(e: PlayerItemHeldEvent, item: IUniqueItem) {}
}