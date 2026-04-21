package tororo1066.uniqueitemprovider.inject

import org.bukkit.Material

abstract class AbstractInjector<T: AbstractInjectorData> {

    abstract val name: String
    abstract val description: String

    abstract val icon: Material

    abstract fun createSettingsMenu(injector: ItemInjector, currentData: T): AbstractInjectorSettingsMenu<T>

    abstract fun createData(): T
}