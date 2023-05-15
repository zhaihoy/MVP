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

package zhy.hongyuan.model.third3

import android.util.Base64
import android.util.Log
import zhy.hongyuan.common.APPCONST
import zhy.hongyuan.greendao.service.CacheManager
import zhy.hongyuan.greendao.service.CookieStore
import zhy.hongyuan.model.third3.analyzeRule.JsExtensions
import zhy.hongyuan.util.utils.EncoderUtils
import zhy.hongyuan.util.utils.GSON
import zhy.hongyuan.util.utils.fromJsonObject
import javax.script.SimpleBindings

/**
 * 可在js里调用,source.xxx()
 */
@Suppress("unused")
abstract class BaseSource : JsExtensions {

    //var concurrentRate: String? // 并发率
    //var loginUrl: String?       // 登录地址
    //var loginUi: String?       // 登录UI
    //var header: String?        // 请求头

    open fun getConcurrentRateKt(): String? = null // 并发率

    open fun getLoginUrlKt(): String? = null  // 登录地址

    open fun getHeader(): String? = null //  请求头

    open fun getTag(): String = ""

    open fun getKey(): String = ""

    open fun getNoProxy(): Boolean = false

    /*fun loginUi(): List<RowUi>? {
        return GSON.fromJsonArray(loginUi)
    }*/

    fun getLoginJs(): String? {
        val loginJs = getLoginUrlKt()
        return when {
            loginJs == null -> null
            loginJs.startsWith("@js:") -> loginJs.substring(4)
            loginJs.startsWith("<js>") ->
                loginJs.substring(4, loginJs.lastIndexOf("<"))
            else -> loginJs
        }
    }

    fun login() {
        getLoginJs()?.let {
            evalJS(it)
        }
    }

    /**
     * 解析header规则
     */
    fun getHeaderMap(hasLoginHeader: Boolean = false) = HashMap<String, String>().apply {
        this[APPCONST.UA_NAME] = APPCONST.DEFAULT_USER_AGENT
        getHeader()?.let {
            GSON.fromJsonObject<Map<String, String>>(
                when {
                    it.startsWith("@js:", true) ->
                        evalJS(it.substring(4)).toString()
                    it.startsWith("<js>", true) ->
                        evalJS(it.substring(4, it.lastIndexOf("<"))).toString()
                    else -> it
                }
            )?.let { map ->
                putAll(map)
            }
        }
        if (hasLoginHeader) {
            getLoginHeaderMap()?.let {
                putAll(it)
            }
        }
    }

    /**
     * 获取用于登录的头部信息
     */
    fun getLoginHeader(): String? {
        return CacheManager.get("loginHeader_${getKey()}")
    }

    fun getLoginHeaderMap(): Map<String, String>? {
        val cache = getLoginHeader() ?: return null
        return GSON.fromJsonObject(cache)
    }

    /**
     * 保存登录头部信息,map格式,访问时自动添加
     */
    fun putLoginHeader(header: String) {
        CacheManager.put("loginHeader_${getKey()}", header)
    }

    fun removeLoginHeader() {
        CacheManager.delete("loginHeader_${getKey()}")
    }

    /**
     * 获取用户信息,可以用来登录
     * 用户信息采用aes加密存储
     */
    fun getLoginInfo(): String? {
        try {
            val key = APPCONST.androidId.encodeToByteArray(0, 8)
            val cache = CacheManager.get("userInfo_${getKey()}") ?: return null
            val encodeBytes = Base64.decode(cache, Base64.DEFAULT)
            val decodeBytes = EncoderUtils.decryptAES(encodeBytes, key)
                ?: return null
            return String(decodeBytes)
        } catch (e: Exception) {
            Log.e("BaseSource","" + e.localizedMessage)
            return null
        }
    }

    fun getLoginInfoMap(): Map<String, String>? {
        return GSON.fromJsonObject(getLoginInfo())
    }

    /**
     * 保存用户信息,aes加密
     */
    fun putLoginInfo(info: String): Boolean {
        return try {
            val key = (APPCONST.androidId).encodeToByteArray(0, 8)
            val encodeBytes = EncoderUtils.encryptAES(info.toByteArray(), key)
            val encodeStr = Base64.encodeToString(encodeBytes, Base64.DEFAULT)
            CacheManager.put("userInfo_${getKey()}", encodeStr)
            true
        } catch (e: Exception) {
            Log.e("BaseSource","" + e.localizedMessage)
            false
        }
    }

    fun removeLoginInfo() {
        CacheManager.delete("userInfo_${getKey()}")
    }

    fun setVariable(variable: String?) {
        if (variable != null) {
            CacheManager.put("sourceVariable_${getKey()}", variable)
        } else {
            CacheManager.delete("sourceVariable_${getKey()}")
        }
    }

    fun getVariable(): String? {
        return CacheManager.get("sourceVariable_${getKey()}")
    }

    /**
     * 执行JS
     */
    @Throws(Exception::class)
    fun evalJS(jsStr: String, bindingsConfig: SimpleBindings.() -> Unit = {}): Any? {
        val bindings = SimpleBindings()
        bindings.apply(bindingsConfig)
        bindings["java"] = this
        bindings["source"] = this
        bindings["baseUrl"] = getKey()
        bindings["cookie"] = CookieStore
        bindings["cache"] = CacheManager
        return APPCONST.SCRIPT_ENGINE.eval(jsStr, bindings)
    }
}