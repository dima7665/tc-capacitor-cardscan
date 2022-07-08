import com.stripe.android.model.Address

class Card {
}

data class CardData(
    val name: String? = null,
    val address: Address? = null,
    val isPrimary: Int? = 0
)
