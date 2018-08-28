package com.kuainiugroup.paydayloan.module.weex

@Suppress("MemberVisibilityCanBePrivate")
/**
 * Created By: sqq
 * Created Time: 18/6/13 下午6:13.
 */
class WeexDebugCons {
    companion object {

        const val WEEX_TPL_KEY = "_wx_tpl"

        //hot refresh
        const val HOT_REFRESH_CONNECT = 0x111
        const val HOT_REFRESH_DISCONNECT = HOT_REFRESH_CONNECT + 1
        const val HOT_REFRESH_REFRESH = HOT_REFRESH_DISCONNECT + 1
        const val HOT_REFRESH_CONNECT_ERROR = HOT_REFRESH_REFRESH + 1
    }
}