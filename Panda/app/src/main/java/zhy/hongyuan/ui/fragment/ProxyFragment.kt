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

package zhy.hongyuan.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import com.kongzue.dialogx.dialogs.BottomMenu
import io.reactivex.disposables.Disposable
import zhy.panda.myreader.R
import zhy.hongyuan.base.BaseFragment
import zhy.hongyuan.base.adapter2.onClick
import zhy.hongyuan.base.observer.MySingleObserver
import zhy.panda.myreader.databinding.FragmentProxySettingBinding
import zhy.hongyuan.model.sourceAnalyzer.BookSourceManager
import zhy.hongyuan.ui.dialog.DialogCreator.OnMultiDialogListener
import zhy.hongyuan.ui.dialog.MultiChoiceDialog
import zhy.hongyuan.ui.dialog.MyAlertDialog
import zhy.hongyuan.util.SharedPreUtils
import zhy.hongyuan.util.ToastUtils

/**
 * @author  hongyuan
 * @date 2022/3/24 10:42
 */
class ProxyFragment : BaseFragment() {

    private lateinit var binding: FragmentProxySettingBinding
    private var proxyType: Int = 0
    private var enableProxy: Boolean = false
    private lateinit var proxyHost: String
    private lateinit var proxyPort: String
    private lateinit var proxyUsername: String
    private lateinit var proxyPassword: String
    private var mNoProxySourcesDia: AlertDialog? = null
    private val proxyTypeArr = arrayOf("http", "socks5")

    override fun bindView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentProxySettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initData(savedInstanceState: Bundle?) {
        enableProxy = SharedPreUtils.getInstance().getBoolean("enableProxy")
        proxyType = SharedPreUtils.getInstance().getInt("proxyType")
        proxyHost = SharedPreUtils.getInstance().getString("proxyHost")
        proxyPort = SharedPreUtils.getInstance().getString("proxyPort")
        proxyUsername = SharedPreUtils.getInstance().getString("proxyUsername")
        proxyPassword = SharedPreUtils.getInstance().getString("proxyPassword")
    }

    override fun initWidget(savedInstanceState: Bundle?) {
        binding.scEnableProxy.isChecked = enableProxy
        if (enableProxy) binding.llContent.visibility = View.VISIBLE
        binding.tvProxyType.text = proxyTypeArr[proxyType]
        binding.tvProxyHost.text = proxyHost.ifEmpty { "请输入代理服务器地址" }
        binding.tvProxyPort.text = proxyPort.ifEmpty { "请选择代理服务器端口" }
        binding.tvProxyUsername.text = proxyUsername.ifEmpty { "请输入代理认证用户名" }
        binding.tvProxyPassword.text = proxyPassword.ifEmpty { "请输入代理认证密码" }
    }

    override fun initClick() {
        binding.rlEnableProxy.onClick {
            enableProxy = !enableProxy
            binding.scEnableProxy.isChecked = enableProxy
            SharedPreUtils.getInstance().putBoolean("enableProxy", enableProxy)
            binding.llContent.visibility = if (enableProxy) View.VISIBLE else View.GONE
        }

        binding.llProxyType.onClick {
            BottomMenu.show(getString(R.string.proxy_type), proxyTypeArr)
                .setSelection(proxyType)
                .setOnMenuItemClickListener { _: BottomMenu?, _: CharSequence?, which: Int ->
                    proxyType = which
                    SharedPreUtils.getInstance().putInt("proxyType", which)
                    initWidget(null)
                    false
                }.setCancelButton(R.string.cancel)
        }
        binding.llProxyHost.onClick {
            var tem = ""
            MyAlertDialog.createInputDia(
                context, getString(R.string.proxy_host),
                "", proxyHost, true, 100,
                { text: String -> tem = text }
            ) { dialog: DialogInterface, _: Int ->
                proxyHost = tem
                SharedPreUtils.getInstance().putString("proxyHost", proxyHost)
                initWidget(null)
                dialog.dismiss()
            }
        }
        binding.llProxyPort.onClick {
            val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_number_picker, null)
            val threadPick = view.findViewById<NumberPicker>(R.id.number_picker)
            threadPick.maxValue = 99999
            threadPick.minValue = 10
            threadPick.value = if (proxyPort.isEmpty()) 1080 else proxyPort.toInt()
            threadPick.setOnScrollListener { _: NumberPicker?, _: Int -> }
            MyAlertDialog.build(context)
                .setTitle(R.string.proxy_port)
                .setView(view)
                .setPositiveButton(R.string.confirm) { _: DialogInterface?, _: Int ->
                    proxyPort = threadPick.value.toString()
                    SharedPreUtils.getInstance().putString("proxyPort", proxyPort)
                    initWidget(null)
                }.setNegativeButton(R.string.cancel, null)
                .show()
        }
        binding.llProxyUsername.onClick {
            var tem = ""
            MyAlertDialog.createInputDia(
                context, getString(R.string.proxy_username),
                "", proxyUsername, true, 100,
                { text: String -> tem = text }
            ) { dialog: DialogInterface, _: Int ->
                proxyUsername = tem
                SharedPreUtils.getInstance().putString("proxyUsername", proxyUsername)
                initWidget(null)
                dialog.dismiss()
            }
        }
        binding.llProxyPassword.onClick {
            var tem = ""
            MyAlertDialog.createInputDia(
                context, getString(R.string.proxy_password),
                "", proxyPassword, true, 100,
                { text: String -> tem = text }
            ) { dialog: DialogInterface, _: Int ->
                proxyPassword = tem
                SharedPreUtils.getInstance().putString("proxyPassword", proxyPassword)
                initWidget(null)
                dialog.dismiss()
            }
        }

        binding.llNoProxySources.onClick {
            if (mNoProxySourcesDia != null) {
                mNoProxySourcesDia?.show()
                return@onClick
            }
            val sources = BookSourceManager.getAllNoLocalSource()
            val mSourcesName = arrayOfNulls<CharSequence>(sources.size)
            val isNoProxy = BooleanArray(sources.size)
            var dSourceCount = 0
            for ((i, source) in sources.withIndex()) {
                mSourcesName[i] = source.sourceName
                val noProxy = source.getNoProxy()
                if (noProxy) dSourceCount++
                isNoProxy[i] = noProxy
            }
            mNoProxySourcesDia = MultiChoiceDialog().create(context, getString(R.string.no_proxy_sources_tip),
                mSourcesName, isNoProxy, dSourceCount,
                { _: DialogInterface?, _: Int ->
                    BookSourceManager.saveDatas(sources)
                        .subscribe(object : MySingleObserver<Boolean?>() {
                            override fun onSubscribe(d: Disposable) {
                                addDisposable(d)
                            }

                            override fun onSuccess(aBoolean: Boolean) {
                                if (aBoolean) {
                                    ToastUtils.showSuccess("保存成功")
                                }
                            }
                        })
                }, null, object : OnMultiDialogListener {
                    override fun onItemClick(
                        dialog: DialogInterface,
                        which: Int,
                        isChecked: Boolean
                    ) {
                        sources[which].setNoProxy(isChecked)
                    }

                    override fun onSelectAll(isSelectAll: Boolean) {
                        for (source in sources) {
                            source.setNoProxy(isSelectAll)
                        }
                    }
                })
        }
    }
}