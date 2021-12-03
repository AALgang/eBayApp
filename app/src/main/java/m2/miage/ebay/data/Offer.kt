package m2.miage.ebay.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Offer (
    val id: String,
    val name: String,
    val description: String?,
    val price: Double?,
    val dateDebut: String?,
    val image: String?,
    val active: Boolean?,
    val ownerId: String): Parcelable

@Parcelize
data class User(
    val username: String,
    val name: String,
    val email: String,
    val identifier: String
): Parcelable