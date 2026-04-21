package tororo1066.uniqueitemprovider.inject

import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerItemHeldEvent
import tororo1066.tororopluginapi.annotation.SEventHandler

class ItemInjectListeners {
    @SEventHandler(priority = EventPriority.LOWEST)
    fun onHeld(e: PlayerItemHeldEvent) {
        val itemStack = e.player.inventory.getItem(e.newSlot) ?: return

        val injectors = ItemInjector.injectors.values
        for (injector in injectors) {
            val itemStack = injector.tryInject(itemStack, e.player) ?: continue
            e.player.inventory.setItem(e.newSlot, itemStack)
            break
        }
    }
}