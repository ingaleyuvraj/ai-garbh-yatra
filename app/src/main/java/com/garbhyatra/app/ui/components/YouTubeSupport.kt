package com.garbhyatra.app.ui.components

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.garbhyatra.app.R
import java.net.URLEncoder

private fun searchUrl(query: String): String =
    "https://m.youtube.com/results?search_query=" + URLEncoder.encode(query, "UTF-8")

private fun embedHtml(youtubeId: String): String = """
    <html><head><meta name="viewport" content="width=device-width, initial-scale=1"></head>
    <body style="margin:0;padding:0;background:#000;">
    <iframe width="100%" height="100%"
        src="https://www.youtube.com/embed/$youtubeId?playsinline=1&autoplay=1&rel=0"
        frameborder="0"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowfullscreen></iframe>
    </body></html>
""".trimIndent()

/**
 * Opens the given YouTube content using the official YouTube app (falling back to a
 * browser). If [youtubeId] is provided it opens that video directly; otherwise it opens
 * a YouTube search for [query]. No copyrighted media is bundled in the app.
 */
fun openYouTube(context: Context, youtubeId: String?, query: String?) {
    val url = when {
        !youtubeId.isNullOrBlank() -> "https://www.youtube.com/watch?v=$youtubeId"
        !query.isNullOrBlank() ->
            "https://www.youtube.com/results?search_query=" +
                URLEncoder.encode(query, "UTF-8")
        else -> return
    }
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // No handler available; silently ignore.
    }
}

/**
 * In-app YouTube playback surface. When [youtubeId] is provided it shows the official
 * IFrame embed (a clean inline player). Otherwise it loads the mobile YouTube search
 * results for [query] inside the same WebView, so playback stays within the app.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InAppYouTubePlayer(
    youtubeId: String?,
    query: String?,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(16.dp)),
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    // Keep http(s) navigation inside the WebView; block app-redirect schemes
                    // (vnd.youtube://, intent://) so the user is not bounced out of the app.
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val scheme = request?.url?.scheme ?: return false
                        return scheme != "http" && scheme != "https"
                    }
                }
                if (!youtubeId.isNullOrBlank()) {
                    loadDataWithBaseURL(
                        "https://www.youtube.com",
                        embedHtml(youtubeId),
                        "text/html",
                        "utf-8",
                        null
                    )
                } else if (!query.isNullOrBlank()) {
                    loadUrl(searchUrl(query))
                }
            }
        },
        onRelease = { it.destroy() }
    )
}

/**
 * Full-width modal dialog that plays a YouTube track inside the app. Shows a title bar
 * with a close button and a secondary "open in YouTube" action.
 */
@Composable
fun YouTubePlayerDialog(
    titleMr: String?,
    youtubeId: String?,
    query: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = titleMr.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(android.R.string.cancel))
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.Black, RoundedCornerShape(16.dp))
                ) {
                    InAppYouTubePlayer(youtubeId = youtubeId, query = query)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { openYouTube(context, youtubeId, query) }) {
                        Icon(
                            Icons.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(stringResource(R.string.audio_open_youtube))
                    }
                }
            }
        }
    }
}
