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
 * Copyright (C) 2020 - 2022 fengyuecanzhu
 */

package zhy.hongyuan.base.adapter2.animations

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View


class AlphaInAnimation @JvmOverloads constructor(private val mFrom: Float = DEFAULT_ALPHA_FROM) :
    BaseAnimation {

    override fun getAnimators(view: View): Array<Animator> =
        arrayOf(ObjectAnimator.ofFloat(view, "alpha", mFrom, 1f))

    companion object {

        private const val DEFAULT_ALPHA_FROM = 0f
    }
}
