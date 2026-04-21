package tororo1066.uniqueitemprovider.inject

import org.bukkit.Material
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.uniqueitemprovider.inject.inject.AbstractInject
import tororo1066.uniqueitemprovider.inject.modification.AbstractModification
import tororo1066.uniqueitemprovider.updateProvider.UpdateProviderItem
import java.io.File

class EditItemInjector(itemInjector: ItemInjector? = null): SInventory(SJavaPlugin.plugin, "Edit Item Injector", 6) {

    val injector = itemInjector?.clone() ?: ItemInjector()
    val isEdit = itemInjector != null

    override fun renderMenu(p: Player): Boolean {
        fillItem(SInventoryItem(Material.GRAY_STAINED_GLASS_PANE).setCanClick(false))


        val injectItemStack = SInventoryItem(Material.BOOK)
            .setDisplayName("§a条件の設定")
            .setCanClick(false)
            .setClickEvent {
                val inv = object : LargeSInventory("§a条件の設定") {
                    override fun renderMenu(p: Player): Boolean {
                        val items = arrayListOf<SInventoryItem>()

                        injector.injects.forEach { (inject, data) ->
                            items.add(
                                SInventoryItem(inject.icon)
                                    .setDisplayName("§a${inject.name}")
                                    .addLore(
                                        "§d${inject.description}",
                                        "",
                                        "§bクリックして設定を変更する"
                                    )
                                    .setCanClick(false)
                                    .setClickEvent {
                                        val settingsMenu = createSettingsMenu(inject, data)
                                        moveChildInventory(settingsMenu, p)
                                    }
                            )
                        }

                        items.add(
                            SInventoryItem(Material.EMERALD_BLOCK)
                                .setDisplayName("§a条件を追加する")
                                .setCanClick(false)
                                .setClickEvent {
                                    val selectMenu = object : LargeSInventory("§a条件を追加する") {
                                        override fun renderMenu(p: Player): Boolean {
                                            val items = arrayListOf<SInventoryItem>()

                                            AbstractInject.injects.forEach { (_, inject) ->
                                                items.add(
                                                    SInventoryItem(inject.icon)
                                                        .setDisplayName("§a${inject.name}")
                                                        .setLore("§d${inject.description}")
                                                        .setCanClick(false)
                                                        .setClickEvent {
                                                            injector.injects[inject] = inject.createData()
                                                            p.closeInventory()
                                                        }
                                                )
                                            }

                                            setResourceItems(items)
                                            return true
                                        }
                                    }

                                    moveChildInventory(selectMenu, p)
                                }
                        )

                        setResourceItems(items)
                        return true
                    }
                }

                moveChildInventory(inv, p)
            }
        setItem(20, injectItemStack)

        val modification = injector.modification

        val modificationItemStack = SInventoryItem(modification?.icon ?: Material.BARRIER)
            .setDisplayName("§a変更の設定")
            .addLore(
                if (modification != null) {
                    listOf(
                        "§d${modification.name}",
                        "§d${modification.description}",
                        "",
                        "§bクリックして設定を変更する",
                        "§e右クリックで変更を設定する"
                    )
                } else {
                    listOf(
                        "§c未設定",
                        "",
                        "§bクリックして変更を設定する"
                    )
                }
            )
            .setCanClick(false)
            .setClickEvent { e ->
                if (modification != null && !e.isRightClick) {
                    val settingsMenu = createSettingsMenu(modification, injector.modificationData!!)
                    moveChildInventory(settingsMenu, p)
                } else {
                    val selectMenu = object : LargeSInventory("§a変更を選択する") {
                        override fun renderMenu(p: Player): Boolean {
                            val items = arrayListOf<SInventoryItem>()

                            AbstractModification.modifications.forEach { (_, modification) ->
                                items.add(
                                    SInventoryItem(modification.icon)
                                        .setDisplayName("§a${modification.name}")
                                        .setLore("§d${modification.description}")
                                        .setCanClick(false)
                                        .setClickEvent {
                                            injector.modification = modification
                                            injector.modificationData = modification.createData()
                                            p.closeInventory()
                                        }
                                )
                            }

                            setResourceItems(items)
                            return true
                        }
                    }

                    moveChildInventory(selectMenu, p)
                }
            }

        setItem(24, modificationItemStack)

        setItems(
            45..53,
            SInventoryItem(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("§a保存する")
                .setCanClick(false)
                .setClickEvent {
                    if (!isEdit) {
                        p.closeInventory()
                        SJavaPlugin.sInput.sendInputCUI(p, String::class.java, "キーを入力してください(/cancelでキャンセル)", action = { key ->
                            if (UpdateProviderItem.items.containsKey(key)) {
                                p.sendMessage("§cそのキーは既に使われています")
                                open(p)
                                return@sendInputCUI
                            }
                            SJavaPlugin.sInput.sendInputCUI(p, String::class.java, "保存先を入力してください((folder/file)のように指定する,/cancelでキャンセル)", action = { path ->
                                injector.key = key
                                injector.path = File(SJavaPlugin.plugin.dataFolder, "injectors/$path.yml").path
                                ItemInjector.injectors[key] = injector
                                    .also { it.save() }
                                p.sendMessage("§a保存しました")
                            }, onCancel = {
                                open(p)
                            })
                        }, onCancel = {
                            open(p)
                        })
                    } else {
                        p.closeInventory()
                        ItemInjector.injectors[injector.key] = injector
                            .also { it.save() }
                        p.sendMessage("§a保存しました")
                    }
                }
        )

        return true
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: AbstractInjectorData> createSettingsMenu(inject: AbstractInjector<T>, data: AbstractInjectorData): AbstractInjectorSettingsMenu<T> {
        return inject.createSettingsMenu(injector, data as T)
    }
}