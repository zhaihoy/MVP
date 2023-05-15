/*
 * This file is part of panda.
 * panda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * panda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with panda.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2020 - 2022 熊猫（XMDS）
 */

package zhy.hongyuan.util.utils

import android.content.Context
import android.util.Log
import dalvik.system.DexClassLoader
import kotlinx.coroutines.launch
import me.fycz.maple.MapleUtils
import xyz.fycz.dynamic.AppParam
import xyz.fycz.dynamic.IAppLoader
import zhy.hongyuan.application.App
import zhy.hongyuan.common.APPCONST
import zhy.hongyuan.common.URLCONST.DEFAULT_PLUGIN_CONFIG_URL
import zhy.hongyuan.entity.PluginConfig
import zhy.hongyuan.model.third3.Coroutine
import zhy.hongyuan.model.third3.http.getProxyClient
import zhy.hongyuan.model.third3.http.newCallResponseBody
import zhy.hongyuan.model.third3.http.text
import zhy.hongyuan.util.SharedPreUtils
import java.io.File
import java.util.*


/**
 * @author  hongyuan
 * @date 2022/3/29 12:36
 */
object PluginUtils {

    val TAG = PluginUtils.javaClass.simpleName
    private var appLoader: IAppLoader? = null
    var config: PluginConfig? = null
    var hasLoad = false
    var loadSuccess = false
    var errorMsg = ""

    fun init() {
        val pluginConfigUrl =
            SharedPreUtils.getInstance().getString("pluginConfigUrl", DEFAULT_PLUGIN_CONFIG_URL)
        Coroutine.async {
            val oldConfig = GSON.fromJsonObject<PluginConfig>(
                SharedPreUtils.getInstance().getString("pluginConfig")
            ) ?: PluginConfig("dynamic.dex", 100)
            launch { loadAppLoader(App.getmContext(), config) }
            val configJson = getProxyClient().newCallResponseBody {
                url(pluginConfigUrl)
            }.text()
            config = GSON.fromJsonObject<PluginConfig>(configJson)
            if (config != null) {
                if (config!!.versionCode > oldConfig.versionCode) {
                    downloadPlugin(config!!)
                    SharedPreUtils.getInstance().putString("pluginConfig", configJson)
                }
            } else {
                config = oldConfig
            }
            if (config!!.md5.lowercase(Locale.getDefault())
                != getPluginMD5(config!!)?.lowercase(Locale.getDefault())
            ) {
                downloadPlugin(config!!)
            }
        }.onSuccess {
            loadAppLoader(App.getmContext(), config)
        }
    }

    private suspend fun downloadPlugin(config: PluginConfig) {
        val res = getProxyClient().newCallResponseBody {
            url(config.url)
        }
        FileUtils.getFile(APPCONST.PLUGIN_DIR_PATH + config.name)
            .writeBytes(res.byteStream().readBytes())
    }

    private fun getPluginMD5(config: PluginConfig): String? {
        return MD5Utils.getFileMD5s(FileUtils.getFile(APPCONST.PLUGIN_DIR_PATH + config.name), 32)
    }

    private fun loadAppLoader(context: Context, config: PluginConfig?) {
        if (hasLoad) return
        config?.let {
            val pluginPath = APPCONST.PLUGIN_DIR_PATH + it.name
            val desFile = File(pluginPath)
            if (desFile.exists()) {
                val dexClassLoader = DexClassLoader(
                    pluginPath,
                    FileUtils.getCachePath(),
                    null,
                    context.classLoader
                )
                try {
                    val libClazz = dexClassLoader.loadClass("xyz.fycz.dynamic.AppLoadImpl")
                    appLoader = libClazz.newInstance() as IAppLoader?
                    appLoader?.run {
                        val appParam = AppParam()
                        appParam.classLoader = context.classLoader
                        appParam.packageName = context.packageName
                        appParam.appInfo = context.applicationInfo
                        onLoad(appParam)
                        hasLoad = true
                        loadSuccess = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    errorMsg = e.stackTraceToString()
                }
            } else {
                errorMsg = pluginPath + "文件不存在"
                Log.d(TAG, pluginPath + "文件不存在")
            }
        }
    }

    fun getPluginLoadInfo(): String {
        return try {
            MapleUtils.callMethod(appLoader, "getPluginLoadInfo") as String
        } catch (e: Exception) {
            ""
        }
    }
}