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

package zhy.hongyuan.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import zhy.panda.myreader.databinding.FragmentBookmarkBinding;
import zhy.hongyuan.ui.presenter.BookMarkPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookMarkFragment extends Fragment {

    private FragmentBookmarkBinding binding;
    private BookMarkPresenter mBookMarkPresenter;


    public BookMarkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        mBookMarkPresenter = new BookMarkPresenter(this);
        mBookMarkPresenter.start();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public ListView getLvBookmarkList() {
        return binding.lvBookmarkList;
    }

    public BookMarkPresenter getmBookMarkPresenter() {
        return mBookMarkPresenter;
    }
}
