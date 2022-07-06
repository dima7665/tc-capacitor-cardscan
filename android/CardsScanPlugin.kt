package com.sav.tenantcloud.capplugins.cardscan

import android.content.Intent
import androidx.activity.result.ActivityResult
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin
import com.sav.tenantcloud.R
import com.sav.tenantcloud.ui.cardscan.CardScanActivity

@CapacitorPlugin(name = "CardScan")
class CardScanPlugin: Plugin() {

    @PluginMethod
    fun show(call: PluginCall) {
        val intent = Intent(context, CardScanActivity::class.java)

        startActivityForResult(call, intent, "scanCardResult")
    }

    @ActivityCallback
    private fun scanCardResult(call: PluginCall?, result: ActivityResult) {
        if (call == null) {
            return
        }

        val cardNumber = result.data?.getStringExtra(PluginCallKeys.cardScanResultNumber)

        if (cardNumber != null) {
            val ret = JSObject().apply {
                put(PluginCallKeys.cardNumber, cardNumber)
            }

            call.resolve(ret)
        } else {
            call.reject(activity.getString(R.string.card_scan_failure))
        }
    }
}
