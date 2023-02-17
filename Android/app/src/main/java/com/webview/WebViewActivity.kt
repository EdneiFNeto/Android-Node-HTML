package com.webview

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

class WebViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                WebViewComponent()
            }
        }
    }
}


@Composable
fun WebViewComponent() {
    val mUrl = "http://192.168.1.121:3334/"
    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            addJavascriptInterface(WebAppInterface(context), "Android")
            loadUrl(mUrl)
        }
    }, update = {
        it.loadUrl(mUrl)
    })
}


class WebAppInterface(context: Context?) {

    @JavascriptInterface
    fun showToast(data: String) {
        Log.d(TAG, "Get data: $data")
    }

    companion object {
        const val TAG = "WebAppInterface"
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WebViewComponent()
}