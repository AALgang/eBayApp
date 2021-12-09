package m2.miage.ebay.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Offer (
    val id: String,
    val name: String,
    val description: String?,
    val price: Double?,
    val dateDebut: String?,
    val image: String?,
    val active: Boolean?,
    val ownerId: String?,
    val bid: Bid?): Parcelable