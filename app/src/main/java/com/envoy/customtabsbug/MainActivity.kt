package com.envoy.customtabsbug

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.android.synthetic.main.activity_main.hello_world

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    hello_world.setOnClickListener { startCustomTabs() }
  }

  private fun startCustomTabs() {
    Log.d("CTBCTB", "startCustomTabs called, doing a bindCustomTabsService")
    val webUrl = "https://google.com"
    CustomTabsClient.bindCustomTabsService(this, this.packageName,
      object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
          Log.d("CTBCTB", "onCustomTabsServiceConnected")
          client.warmup(0) // Warms up the browser to make navigation faster.
          val chromeSession = client.newSession(object : CustomTabsCallback() {
            override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
              Log.d("CTBCTB", "onNavigationEvent($navigationEvent, $extras")
              super.onNavigationEvent(navigationEvent, extras)
            }
          })
          if (chromeSession == null) {
            Log.d("CTBCTB", "no session created!!!!")
          } else {
            Log.d("CTBCTB", "initChromeClient created session $chromeSession, calling mayLaunchUrl")
            chromeSession.mayLaunchUrl(Uri.parse(webUrl), null, null)
            renderChromeView(webUrl, chromeSession)
          }
        }

        override fun onServiceDisconnected(name: ComponentName) {
          Log.d("CTBCTB", "onServiceDisconnected")
        }
      })
  }

  private fun renderChromeView(webUrl: String, chromeSession: CustomTabsSession) {
    Log.d("CTBCTB", "renderChromeView with session $chromeSession")
    val intent = CustomTabsIntent.Builder(chromeSession)
      .enableUrlBarHiding()
      .setShowTitle(true)
      .setToolbarColor(ContextCompat.getColor(this, android.R.color.white))
      .build()
    intent.launchUrl(this, Uri.parse(webUrl))
  }
}
