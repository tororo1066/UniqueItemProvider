package tororo1066.uniqueitemprovider.inject.inject

import tororo1066.uniqueitemprovider.inject.AbstractInjectorData
import tororo1066.uniqueitemprovider.inject.AbstractInjectorSettingsMenu
import tororo1066.uniqueitemprovider.inject.ItemInjector

abstract class AbstractInjectSettingsMenu<T: AbstractInjectorData>(
    injector: ItemInjector,
    inject: AbstractInject<T>,
    currentData: T,
): AbstractInjectorSettingsMenu<T>(
    injector,
    inject,
    currentData,
    onSave = { data ->
        injector.injects[inject] = data
    }
)