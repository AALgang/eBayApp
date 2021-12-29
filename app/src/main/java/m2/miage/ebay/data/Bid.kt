package m2.miage.ebay.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

@Parcelize
data class Bid(
    val acheteur: String,
    val date: Date?,
    val prix: String?
) : Parcelable