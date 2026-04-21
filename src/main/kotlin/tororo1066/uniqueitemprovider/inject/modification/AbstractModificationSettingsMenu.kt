package tororo1066.uniqueitemprovider.inject.modification

import tororo1066.uniqueitemprovider.inject.AbstractInjectorData
import tororo1066.uniqueitemprovider.inject.AbstractInjectorSettingsMenu
import tororo1066.uniqueitemprovider.inject.ItemInjector
import tororo1066.uniqueitemprovider.inject.inject.AbstractInject

abstract class AbstractModificationSettingsMenu<T: AbstractInjectorData>(
    injector: ItemInjector,
    inject: AbstractModification<T>,
    currentData: T,
): AbstractInjectorSettingsMenu<T>(
    injector,
    inject,
    currentData,
    onSave = { data ->
        injector.modificationData = data
    }
)