package com.sav.tenantcloud.ui.cardscan

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sav.tenantcloud.capplugins.cardscan.PluginCallKeys
import com.stripe.android.stripecardscan.cardscan.CardScanSheet
import com.stripe.android.stripecardscan.cardscan.CardScanSheetResult

class CardScanActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cardScanSheet = CardScanSheet.create(this, "pk_test_...", ::onScanFinished)

        cardScanSheet.present()

    }

    override fun onDestroy() {
        Log.d("ScanCard", "onDestroy activity")
        super.onDestroy()
    }

    private fun onScanFinished(result: CardScanSheetResult) {
        Log.d("ScanCard", "onScanFinished activity - ${result.toString()}")

        when (result) {
            is CardScanSheetResult.Completed -> {
                intent.putExtra(PluginCallKeys.cardScanResultNumber, result.scannedCard.pan)
            }
            is CardScanSheetResult.Canceled -> {
                intent.putExtra(PluginCallKeys.cardScanResultError, result.reason.toString())
            }
            is CardScanSheetResult.Failed -> {
                intent.putExtra(PluginCallKeys.cardScanResultError, result.error.message.toString())
            }
        }

        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
