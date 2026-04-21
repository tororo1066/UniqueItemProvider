package tororo1066.uniqueitemprovider.inject.modification

import org.bukkit.inventory.ItemStack
import tororo1066.uniqueitemprovider.inject.AbstractInjector
import tororo1066.uniqueitemprovider.inject.AbstractInjectorData
import tororo1066.uniqueitemprovider.inject.modification.modifications.AddUpdateProviderModification
import tororo1066.uniqueitemprovider.inject.modification.modifications.ItemReplaceModification

abstract class AbstractModification<T: AbstractInjectorData>: AbstractInjector<T>() {

    abstract fun modify(itemStack: ItemStack, data: T): ItemStack

    companion object {
        val modifications = listOf(
            ItemReplaceModification,
            AddUpdateProviderModification
        ).associateBy { it.name }
    }
}