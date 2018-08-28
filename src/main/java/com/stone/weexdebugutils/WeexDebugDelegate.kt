package com.stone.weexdebugutils

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import com.google.zxing.client.android.CaptureActivity
import com.google.zxing.client.android.Intents
import com.kuainiugroup.paydayloan.module.weex.WeexDebugCons
import com.stone.log.Logs
import com.stone.qpermission.reqPermissions
import com.yanzhenjie.permission.Permission
import org.jetbrains.anko.toast
import java.net.URL

/**
 * Created By: sqq
 * Created Time: 8/24/18 4:57 PM.
 *
 *
 */
class WeexDebugDelegate(private val act: Activity, private val jsBundle: String, private val wsPort: String = "8082", private val onReloadWeex: (url: String) -> Unit) {

    private var mJsUri = jsBundle

    private var mWeexHandler: Handler? = null

    private fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            WeexDebugCons.HOT_REFRESH_CONNECT -> HotRefreshManager.connect(msg.obj.toString())
            WeexDebugCons.HOT_REFRESH_DISCONNECT -> HotRefreshManager.disConnect()
            WeexDebugCons.HOT_REFRESH_REFRESH -> onReloadWeex.invoke(mJsUri)
            WeexDebugCons.HOT_REFRESH_CONNECT_ERROR -> act.toast("hot refresh connect error!")
        }
        return false
    }


    fun onCreateOptionsMenu(menu: Menu): Boolean {
        act.menuInflater.inflate(R.menu.menu_weex_debug, menu)
        return true
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionScan -> startCapture(act)
            R.id.actionRefresh -> onReloadWeex.invoke(mJsUri)
            else -> return false
        }
        return true
    }

    fun startWeexHotRefresh(): WeexDebugDelegate {
        if (jsBundle.startsWith("http")) {
            mWeexHandler = Handler { handleMessage(it) }
            HotRefreshManager.setHandler(mWeexHandler!!)
            val host = URL(jsBundle).host
            val wsUrl = "ws://$host:$wsPort"
            Logs.d("startWeexHotRefresh: $wsUrl")
            mWeexHandler!!.obtainMessage(WeexDebugCons.HOT_REFRESH_CONNECT, 0, 0, wsUrl).sendToTarget()
        } else {
            // 服务器短链 do nothing
        }
        return this
    }

    fun onDestroy() {
        mWeexHandler?.obtainMessage(WeexDebugCons.HOT_REFRESH_DISCONNECT)?.sendToTarget()
        mWeexHandler?.removeCallbacksAndMessages(null)
        mWeexHandler = null
    }

    companion object {
        const val REQ_WEEX_SCAN_CODE = 0x81

        /**
         * 打开二维码扫描页面
         * 配合 [handleScanResult] 使用的
         */
        fun startCapture(act: Activity) {
            act.reqPermissions(Permission.CAMERA) {
                //通过action参数唤起，将解析结果返回外部处理
                val intent = Intent(act, CaptureActivity::class.java)
                intent.action = Intents.Scan.ACTION
                act.startActivityForResult(intent, REQ_WEEX_SCAN_CODE)
            }
        }

        /**
         *
         * 处理 扫描二维码的结果返回
         */
        fun handleScanResult(requestCode: Int, resultCode: Int, data: Intent?, onScanCallback: (result: String?) -> Unit) {
            if (requestCode == REQ_WEEX_SCAN_CODE && resultCode == Activity.RESULT_OK) {
                val codeResult = data?.getStringExtra(Intents.Scan.RESULT)
                Logs.i("二维码地址：$codeResult")
                onScanCallback.invoke(codeResult)
            }
        }


    }
}