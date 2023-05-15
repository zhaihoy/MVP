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

package zhy.hongyuan.model.storage

import android.os.Handler
import android.os.Looper
import com.kongzue.dialogx.dialogs.BottomMenu
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import xyz.fycz.myreader.R
import zhy.hongyuan.base.observer.MySingleObserver
import zhy.hongyuan.common.APPCONST
import zhy.hongyuan.util.SharedPreUtils
import zhy.hongyuan.util.ToastUtils
import zhy.hongyuan.util.ZipUtils
import zhy.hongyuan.util.utils.FileUtils
import zhy.hongyuan.util.webdav.WebDav
import zhy.hongyuan.util.webdav.http.HttpAuth
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

object WebDavHelp {
    private val zipFilePath = FileUtils.getCachePath() + "/backup" + ".zip"
    private val unzipFilesPath by lazy {
        FileUtils.getCachePath()
    }

    private fun getWebDavUrl(): String {
        var url = SharedPreUtils.getInstance().getString("webdavUrl", APPCONST.DEFAULT_WEB_DAV_URL)
        if (url.isNullOrEmpty()) {
            url = APPCONST.DEFAULT_WEB_DAV_URL
        }
        if (!url.endsWith("/")) url += "/"
        return url
    }

    private fun initWebDav(): Boolean {
        val account = SharedPreUtils.getInstance().getString("webdavAccount", "")
        val password = SharedPreUtils.getInstance().getString("webdavPassword", "")
        if (!account.isNullOrBlank() && !password.isNullOrBlank()) {
            HttpAuth.auth = HttpAuth.Auth(account, password)
            return true
        }
        return false
    }

    fun getWebDavFileNames(): ArrayList<String> {
        val url = getWebDavUrl()
        val names = arrayListOf<String>()
        try {
            if (initWebDav()) {
                var files = WebDav(url + "panda/").listFiles()
                val sortType = SharedPreUtils.getInstance().getInt("sortType")
                if (sortType == 0) files = files.reversed()
                val max = SharedPreUtils.getInstance().getInt("restoreNum", 30)
                for (index: Int in 0 until min(max, files.size)) {
                    files[index].displayName?.let {
                        names.add(it)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return names
    }

    fun showRestoreDialog(names: ArrayList<String>, callBack: Restore.CallBack?): Boolean {
        return if (names.isNotEmpty()) {
            /*context.selector(title = "选择恢复文件", items = names) { _, index ->
                if (index in 0 until 30.coerceAtLeast(names.size)) {
                    restoreWebDav(names[index], callBack)
                }
            }*/
            BottomMenu.build().setTitle("选择恢复文件")
                .setMenuStringList(names)
                .setOnMenuItemClickListener { _, _, which ->
                    if (which in 0 until 30.coerceAtLeast(names.size)) {
                        restoreWebDav(names[which], callBack)
                    }
                    false
                }
                .setCancelButton(R.string.cancel)
                .show()
            true
        } else {
            false
        }
    }

    private fun restoreWebDav(name: String, callBack: Restore.CallBack?) {
        Single.create(SingleOnSubscribe<Boolean> { e ->
            getWebDavUrl().let {
                val file = WebDav(it + "panda/" + name)
                file.downloadTo(zipFilePath, true)
                @Suppress("BlockingMethodInNonBlockingContext")
                ZipUtils.unzipFile(zipFilePath, unzipFilesPath)
            }
            e.onSuccess(true)
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MySingleObserver<Boolean>() {
                    override fun onSuccess(t: Boolean) {
                        Restore.restore(unzipFilesPath, callBack)
                    }
                })
    }

    fun backUpWebDav(path: String) {
        try {
            if (initWebDav()) {
                val paths = arrayListOf(*Backup.backupFileNames)
                for (i in 0 until paths.size) {
                    paths[i] = path + File.separator + paths[i]
                }
                FileUtils.deleteFile(zipFilePath)
                if (ZipUtils.zipFiles(paths, zipFilePath)) {
                    WebDav(getWebDavUrl() + "panda").makeAsDir()
                    val putUrl = getWebDavUrl() + "panda/backup" +
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(Date(System.currentTimeMillis())) + ".zip"
                    WebDav(putUrl).upload(zipFilePath)
                }
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                ToastUtils.showError("WebDav\n${e.localizedMessage}")
            }
        }
    }
}