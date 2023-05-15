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

package zhy.hongyuan.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import xyz.fycz.myreader.R;
import zhy.hongyuan.base.BaseActivity;
import zhy.hongyuan.base.BitIntentDataManager;
import xyz.fycz.myreader.databinding.ActivityCatalogBinding;
import zhy.hongyuan.greendao.entity.Book;
import zhy.hongyuan.ui.adapter.TabFragmentPageAdapter;
import zhy.hongyuan.ui.fragment.BookMarkFragment;
import zhy.hongyuan.ui.fragment.CatalogFragment;

/**
 * 书籍目录activity
 */
public class CatalogActivity extends BaseActivity<ActivityCatalogBinding> {

    private SearchView searchView;

    private Book mBook;

    private TabFragmentPageAdapter tabAdapter;

    /*******************Public**********************************/

    public Book getmBook() {
        return mBook;
    }

    @Override
    protected void bindView() {
        binding = ActivityCatalogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    /*********************Initialization****************************/


    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mBook = (Book) BitIntentDataManager.getInstance().getData(getIntent());
    }


    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        setStatusBarColor(R.color.colorPrimary, true);
        tabAdapter = new TabFragmentPageAdapter(getSupportFragmentManager());
        tabAdapter.addFragment(new CatalogFragment(), "目录");
        tabAdapter.addFragment(new BookMarkFragment(), "书签");
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        binding.catalogVp.setAdapter(tabAdapter);
        binding.catalogVp.setOffscreenPageLimit(2);
        binding.catalogTab.setupWithViewPager(binding.catalogVp);
    }


    /*************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_search, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        searchView = (SearchView) search.getActionView();
        searchView.setMaxWidth(getResources().getDisplayMetrics().widthPixels);
        searchView.onActionViewCollapsed();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                switch (binding.catalogVp.getCurrentItem()){
                    case 0:
                        ((CatalogFragment) tabAdapter.getItem(0)).getmCatalogPresent().startSearch(newText);
                        break;
                    case 1:
                        ((BookMarkFragment) tabAdapter.getItem(1)).getmBookMarkPresenter().startSearch(newText);
                        break;
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
