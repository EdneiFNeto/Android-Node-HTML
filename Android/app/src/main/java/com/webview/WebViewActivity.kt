package com.webview

import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

class WebViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                WebViewComponent()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WebViewComponent() {
    val webViewState = rememberWebViewState()

    if (webViewState.loader) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
            content = {
                Surface(
                    color = Color.Transparent,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    content = {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Carregando...",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            Button(onClick = {
                            }) {
                                Text(text = "cancelar")
                            }
                        }
                    }
                )
            }
        )
    }

    val url = "https://zoop.com.br"
    val chromeClient = webChromeClient(webViewState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Web",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = {
                            webViewState.webView?.goBack()
                        }) {
                            Icon(
                                Icons.Filled.KeyboardArrowLeft,
                                contentDescription = null
                            )
                        }

                        IconButton(onClick = {
                            webViewState.webView?.goForward()
                        }) {
                            Icon(
                                Icons.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                },
                actions = {
                },
                backgroundColor = Color.Red,
                contentColor = Color.White,
                modifier = Modifier.height(40.dp)
            )
        }, content = {
            AndroidView(factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    webChromeClient = chromeClient
                    webViewClient = webViewClient(webViewState)

                    loadUrl(url)
                    webViewState.webView = this
                }
            }, update = {
                webViewState.webView = it
            })
        })
}

@Composable
private fun webChromeClient(webViewState: WebViewState) = remember {
    object : WebChromeClient() {
        override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
            super.onReceivedIcon(view, icon)
            Log.d(TAG, "Icon $icon")
            webViewState.icon = icon
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            Log.d(TAG, "title $title")
            webViewState.title = title ?: ""
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            webViewState.percent = newProgress
        }

        val TAG = "webChromeClient"
    }
}

private fun webViewClient(webViewState: WebViewState): WebViewClient {
    return object : WebViewClient() {
        override fun onPageStarted(
            view: WebView, url: String,
            favicon: Bitmap?
        ) {
            Log.d("onPageStarted", "On Page started")
            webViewState.loader = true
            webViewState.backEnabled = true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.d("onPageFinished", "On Page Finished")
            webViewState.loader = false
            webViewState.forwardEnabled = true
        }
    }
}

class WebViewState(
    title: String,
    icon: Bitmap?,
    percent: Int,
    backEnabled: Boolean,
    forwardEnabled: Boolean,
    loader: Boolean,
    webView: WebView?,
) {
    var title by mutableStateOf(title)
    var icon by mutableStateOf(icon)
    var percent by mutableStateOf(percent)
    var backEnabled by mutableStateOf(backEnabled)
    var forwardEnabled by mutableStateOf(forwardEnabled)
    var loader by mutableStateOf(loader)
    var webView by mutableStateOf(webView)
}

@Composable
fun rememberWebViewState(
    title: String? = null,
    percent: Int = 0,
    icon: Bitmap? = null,
    backEnabled: Boolean = false,
    forwardEnabled: Boolean = false,
    loader: Boolean = false,
    webView: WebView? = null
) = remember {
    WebViewState(
        title = title ?: "",
        icon = icon,
        percent = percent,
        backEnabled = backEnabled,
        forwardEnabled = forwardEnabled,
        loader = loader,
        webView = webView,
    )
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