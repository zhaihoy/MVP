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

package zhy.hongyuan.util.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import zhy.hongyuan.application.App;

/**
 * @author  hongyuan
 * @date 2020/8/14 22:04
 */
public class NotificationClickReceiver extends BroadcastReceiver {

    public static final String CANCEL_ACTION = "cancelAction";

    @Override
    public void onReceive(Context context, Intent intent) {
        //todo 跳转之前要处理的逻辑
        if (CANCEL_ACTION.equals(intent.getAction())){
            App.getApplication().shutdownThreadPool();
        }
    }
}
