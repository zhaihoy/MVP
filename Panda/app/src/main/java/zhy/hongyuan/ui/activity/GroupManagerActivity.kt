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

package zhy.hongyuan.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import zhy.panda.myreader.R
import zhy.hongyuan.application.App
import zhy.hongyuan.base.BaseActivity
import zhy.panda.myreader.databinding.ActivityGroupManagerBinding
import zhy.panda.myreader.databinding.ItemGroupBinding
import zhy.hongyuan.greendao.entity.BookGroup
import zhy.hongyuan.ui.adapter.BookGroupAdapter
import zhy.hongyuan.ui.adapter.helper.ItemTouchCallback
import zhy.hongyuan.ui.dialog.BookGroupDialog
import zhy.hongyuan.util.SharedPreUtils

/**
 * @author  hongyuan
 * @date 2021/8/30 12:48
 */
class GroupManagerActivity : BaseActivity<ActivityGroupManagerBinding>() {

    private lateinit var adapter: BookGroupAdapter

    private lateinit var groupDialog: BookGroupDialog

    private lateinit var itemTouchHelper: ItemTouchHelper

    private var openGroup = true
    
    private lateinit var curBookGroupId: String

    override fun bindView() {
        binding = ActivityGroupManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setUpToolbar(toolbar: Toolbar?) {
        super.setUpToolbar(toolbar)
        setStatusBarColor(R.color.colorPrimary, true)
        supportActionBar?.title = getString(R.string.manage_book_group)
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        groupDialog = BookGroupDialog(this)
        groupDialog.initBookGroups(false)
        openGroup = SharedPreUtils.getInstance().getBoolean("openGroup", true)
        curBookGroupId = SharedPreUtils.getInstance().getString(getString(R.string.curBookGroupId), "")
    }

    override fun initWidget() {
        super.initWidget()
        binding.scBookGroup.isChecked = openGroup
        binding.recyclerView.visibility = if (openGroup) View.VISIBLE else View.GONE
        adapter = BookGroupAdapter(this, { itemTouchHelper.startDrag(it) }, groupDialog).apply {
            setItems(groupDialog.getmBookGroups())
            addFooterView {
                ItemGroupBinding.inflate(inflater, it, false).apply {
                    ivIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@GroupManagerActivity,
                            R.drawable.ic_plus
                        )
                    )
                    ivIcon.setColorFilter(resources.getColor(R.color.colorAccent))
                    tvGroupName.text = "添加分组"
                    ivMove.visibility = View.GONE
                    root.setOnClickListener {
                        groupDialog.showAddOrRenameGroupDia(false, true, 0,
                            object : BookGroupDialog.OnGroup() {
                                override fun change() {

                                }

                                override fun addGroup(group: BookGroup) {
                                    App.getHandler().postDelayed(
                                        { adapter.addItem(group) }, 300
                                    )
                                }
                            })
                    }
                }
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        val itemTouchCallback = ItemTouchCallback()
        itemTouchCallback.setOnItemTouchListener(adapter)
        itemTouchCallback.setLongPressDragEnable(true)
        itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    override fun initClick() {
        super.initClick()
        binding.rlBookGroup.setOnClickListener {
            openGroup = !openGroup
            binding.scBookGroup.isChecked = openGroup
            SharedPreUtils.getInstance().putBoolean("openGroup", openGroup)
            if (openGroup) {
                binding.recyclerView.visibility = View.VISIBLE
                SharedPreUtils.getInstance().putString(getString(R.string.curBookGroupId), curBookGroupId)
            } else {
                binding.recyclerView.visibility = View.GONE
                SharedPreUtils.getInstance().putString(getString(R.string.curBookGroupId), "")
            }
            setResult(RESULT_OK)
        }
    }

}
