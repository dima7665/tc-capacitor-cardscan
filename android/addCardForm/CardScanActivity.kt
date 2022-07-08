import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sav.tenantcloud.capplugins.cardscan.CardData
import com.sav.tenantcloud.capplugins.cardscan.PluginCallKeys
import com.sav.tenantcloud.databinding.CardScanBinding
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.createCardToken
import com.stripe.android.model.Address
import com.stripe.android.model.Token
import com.stripe.android.stripecardscan.cardscan.CardScanSheet
import com.stripe.android.stripecardscan.cardscan.CardScanSheetResult

class CardScanActivity: AppCompatActivity() {
    private lateinit var binding: CardScanBinding
    private lateinit var stripe: Stripe

    private val stripePublKey = "pk_test_XbyEVednW4Sk7CwyFo5Dt8Py"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CardScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardInputWidget.postalCodeEnabled = false

        stripe = Stripe(this, stripePublKey)
        val cardScanSheet = CardScanSheet.create(this, stripePublKey, ::onScanFinished)

        binding.scanCardButton.setOnClickListener {
            cardScanSheet.present()
        }

        binding.buttonCardFormClose.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

        binding.buttonSubmit.setOnClickListener {
            createToken()
        }

        binding.scanActionLabel.setOnClickListener {
            cardScanSheet.present()
        }
    }

    override fun onDestroy() {
        Log.d("ScanCard", "onDestroy activity")
        super.onDestroy()
    }

    private fun createToken() {
        val cardParams = binding.cardInputWidget.cardParams
        val address = Address(
            city = binding.cityField.text.toString(),
            line1 = binding.addressField.text.toString(),
            state = binding.stateField.text.toString(),
            postalCode = binding.zipField.text.toString()
            )
        cardParams?.address = address
        cardParams?.name = binding.nameField.text.toString()

        if (cardParams != null) {
            stripe.createCardToken(cardParams, null, null, object: ApiResultCallback<Token> {
                override fun onSuccess(result: Token) {
                    intent.putExtra(PluginCallKeys.cardScanResult, result.id)

                    val cardData = CardData("", address, binding.primarySwitch.isChecked.compareTo(true))

                    Log.d("ScanCard", "token - $result, ${binding.primarySwitch.isChecked}, ${binding.primarySwitch.isChecked.compareTo(true)}")
                    setResult(Activity.RESULT_OK, intent)
                }

                override fun onError(e: Exception) {
                    setResult(Activity.RESULT_CANCELED, intent)
                }
            })
        }

        finish()
    }

    private fun onScanFinished(result: CardScanSheetResult) {
        Log.d("ScanCard", "onScanFinished activity - ${result.toString()}")

        when (result) {
            is CardScanSheetResult.Completed -> {
//                intent.putExtra(PluginCallKeys.cardScanResult, result.scannedCard.pan)
                binding.cardInputWidget.setCardNumber(result.scannedCard.pan)
            }
            is CardScanSheetResult.Canceled -> {
                intent.putExtra(PluginCallKeys.cardScanError, result.reason.toString())
            }
            is CardScanSheetResult.Failed -> {
                intent.putExtra(PluginCallKeys.cardScanError, result.error.message.toString())
            }
        }
    }
}
