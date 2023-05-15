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

package zhy.hongyuan.common;

import zhy.hongyuan.util.SharedPreUtils;

public class URLCONST {

    public static final String OFFICIAL_WEB = "https://blog.csdn.net/qq_39956074";

    public static String LAN_ZOU_URL = "https://fycz.lanzoum.com";

    public static String APP_DIR_URL = LAN_ZOU_URL + "/b00ngso7e";

    public static final String SUB_SOURCE_URL = LAN_ZOU_URL + "/b07d6pupc";

    //字体下载
    public static final String FONT_DOWNLOAD_URL = "https://novel." + getDefaultDomain() + "/app/fonts/";

    public static final String APP_WEB_URL = "https://panda." + getDefaultDomain() + "/";

    public static final String BAI_DU_SEARCH = "https://m.baidu.com/s?word={key}";

    public static final String GOOGLE_SEARCH = "https://www.google.com/search?q={key}";

    public static final String YOU_DAO_SEARCH = "http://m.youdao.com/dict?le=eng&q={key}";

    public static final String FY_READER_URL = "https://panda." + getDefaultDomain();

    public static final String USER_URL = "http://101.43.83.105:12123";
    public static final String LOG_UPLOAD_URL = USER_URL + "/do/logUpload";
    public static final String THANKS_URL = USER_URL + "/thanks/";
    public static final String AD_URL = USER_URL + "/do/ad";

    public static final String DONATE = "https://panda.coding.net/p/img/d/img/git/raw/master/Donate";

    public static final String WX_ZSM = DONATE + "/wx_zsm.jpg";
    public static final String ZFB_SKM = DONATE + "/zfb_skm.jpg";
    public static final String QQ_SKM = DONATE + "/qq_skm.jpg";

    public static final String QUOTATION = "https://v1.hitokoto.cn/?encode=json&charset=utf-8";

    public static final String DEFAULT_PLUGIN_CONFIG_URL = "https://gitlab.com/fengyuecanzhu/panda-resource/-/raw/main/Plugin/release/config_panda.json";

    public static String getDefaultDomain() {
        return SharedPreUtils.getInstance().getString("domain", "fycz.me");
    }

}

