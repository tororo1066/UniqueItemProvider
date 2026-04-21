package tororo1066.uniqueitemprovider.inject

import org.bukkit.configuration.ConfigurationSection

abstract class AbstractInjectorData: Cloneable {

    abstract fun load(configurationSection: ConfigurationSection)

    abstract fun save(configurationSection: ConfigurationSection)

    public override fun clone(): AbstractInjectorData {
        return super.clone() as AbstractInjectorData
    }
}