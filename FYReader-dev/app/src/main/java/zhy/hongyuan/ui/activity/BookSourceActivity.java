/*
 * This file is part of FYReader.
 * FYReader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FYReader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FYReader.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2020 - 2022 fengyuecanzhu
 */

package zhy.hongyuan.ui.activity;

import android.app.Activity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;
import java.util.List;

import xyz.fycz.myreader.R;
import zhy.hongyuan.application.App;
import zhy.hongyuan.base.BaseTabActivity;
import xyz.fycz.myreader.databinding.ActivityBooksourceBinding;
import zhy.hongyuan.ui.fragment.DIYSourceFragment;
import zhy.hongyuan.ui.fragment.SubscribeSourceFragment;

/**
 * @author  hongyuan
 * @date 2021/2/10 9:14
 */
public class BookSourceActivity extends BaseTabActivity<ActivityBooksourceBinding> {

    private SearchView searchView;

    @Override
    protected void bindView() {
        binding = ActivityBooksourceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        super.bindView();
    }

    @Override
    protected List<Fragment> createTabFragments() {
        return Arrays.asList(new SubscribeSourceFragment(this),
                new DIYSourceFragment(this));
    }

    @Override
    protected List<String> createTabTitles() {
        return Arrays.asList("订阅书源", "DIY书源");
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        setStatusBarColor(R.color.colorPrimary, true);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        binding.tabTlIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (!TextUtils.isEmpty(searchView.getQuery())) {
                    App.getHandler().postDelayed(() ->{
                        List<Fragment> fragments = getSupportFragmentManager().getFragments();
                        switch (tab.getPosition()) {
                            case 0:
                                ((SubscribeSourceFragment) fragments.get(0)).startSearch("");
                                break;
                            case 1:
                                ((DIYSourceFragment) fragments.get(1)).startSearch("");
                                break;
                        }
                    }, 300);
                }
                searchView.onActionViewCollapsed();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_search, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        searchView = (SearchView) search.getActionView();
        TextView textView = searchView.findViewById(R.id.search_src_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        searchView.setQueryHint("搜索书源");
        searchView.setMaxWidth(getResources().getDisplayMetrics().widthPixels);
        searchView.onActionViewCollapsed();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                switch (binding.tabVp.getCurrentItem()) {
                    case 0:
                        ((SubscribeSourceFragment) fragments.get(0)).startSearch(newText);
                        break;
                    case 1:
                        ((DIYSourceFragment) fragments.get(1)).startSearch(newText);
                        break;
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void finish() {
        setResult(Activity.RESULT_OK);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (!"".contentEquals(searchView.getQuery())) {
            searchView.onActionViewCollapsed();
        } else {
            super.onBackPressed();
        }
    }

    public SearchView getSearchView() {
        return searchView;
    }
}
