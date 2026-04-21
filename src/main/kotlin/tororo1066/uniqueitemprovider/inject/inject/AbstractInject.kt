package tororo1066.uniqueitemprovider.inject.inject

import org.bukkit.inventory.ItemStack
import tororo1066.uniqueitemprovider.inject.AbstractInjector
import tororo1066.uniqueitemprovider.inject.AbstractInjectorData
import tororo1066.uniqueitemprovider.inject.inject.injects.DisplayNameInject

abstract class AbstractInject<T: AbstractInjectorData>: AbstractInjector<T>() {
    abstract fun canInject(itemStack: ItemStack, data: T): Boolean

    companion object {
        val injects = listOf<AbstractInject<*>>(
            DisplayNameInject
        ).associateBy { it.name }
    }
}