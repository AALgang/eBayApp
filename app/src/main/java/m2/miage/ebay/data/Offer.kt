package m2.miage.ebay.data

import java.util.*

data class Offer(
    val name: String,
    val description: String?,
    val price: Double?,
    val startDate: Date?,
    val image: String?,
    val active: Boolean?,
    val ownerId: String)

data class User(
    val username: String,
    val name: String,
    val email: String,
    val identifier: String
)