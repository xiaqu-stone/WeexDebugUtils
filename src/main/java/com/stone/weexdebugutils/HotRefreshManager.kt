package com.stone.weexdebugutils


import android.os.Handler
import com.kuainiugroup.paydayloan.module.weex.WeexDebugCons
import com.stone.log.Logs
import okhttp3.*
import okio.ByteString

/**
 * Created By: sqq
 * Created Time: 18/6/13 下午5:24.
 */
internal object HotRefreshManager {

    var mWebSocket: WebSocket? = null
    private lateinit var mHandler: Handler

    fun setHandler(handler: Handler): HotRefreshManager {
        mHandler = handler
        return this
    }

    fun disConnect() {
        mWebSocket?.close(1000, "activity finish!")
    }

    fun connect(url: String) {
        Logs.d(TAG, "the webSocket connect url ::$url")

        val request = Request.Builder().url(url).build()
        OkHttpClient().newWebSocket(request, WeexWebSocketListener(url))
    }

    class WeexWebSocketListener(private val mUrl: String) : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            Logs.i("onOpen ....webSocket = $webSocket,,response = $response")
            mWebSocket = webSocket
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            t?.printStackTrace()
            Logs.w(TAG, "onFailure:::${t?.message},,response=${response?.message()}")
            mWebSocket = null
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            super.onMessage(webSocket, text)
            Logs.d("WeexWebSocketListener.onMessage() called with: webSocket = [$webSocket], text = [$text]")
            if (text?.contains("refresh") == true) {
                mHandler.obtainMessage(WeexDebugCons.HOT_REFRESH_REFRESH, 0, 0, mUrl).sendToTarget()
            }
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
            super.onMessage(webSocket, bytes)
            Logs.d("WeexWebSocketListener.onMessage() called with: webSocket = [$webSocket], bytes = [$bytes]")
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            super.onClosed(webSocket, code, reason)
            Logs.i(TAG, "onClose:: code=$code, reason=$reason")
            mWebSocket = null
        }
    }

    const val TAG = "HotRefreshManager"

}