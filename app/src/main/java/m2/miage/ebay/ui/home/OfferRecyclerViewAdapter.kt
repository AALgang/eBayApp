package m2.miage.ebay.ui.home

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.offer_item_activity.view.*
import m2.miage.ebay.R
import m2.miage.ebay.data.Bid
import m2.miage.ebay.data.Offer
import m2.miage.ebay.data.User
import m2.miage.ebay.ui.post.PostActivity
import m2.miage.ebay.util.DateUtil.Companion.isOfferActive
import m2.miage.ebay.util.Resource
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class OfferRecyclerViewAdapter(listOffer: List<Offer>) : RecyclerView.Adapter<OfferRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context
    var userReference: MutableLiveData<User> = MutableLiveData()
    var list = listOffer

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val offer: Offer = list.get(position)
            holder.name_post.text = offer.name
            holder.price_post.text = context.getString(R.string.txt_devise, offer.price.toString())
            holder.time_post.text = offer.dateDebut.toString().replace("T", " ")
            holder.owner_post.text = offer.ownerId

            // Affichage des chip en fonction de l'enchère disponible ou non
            if (isOfferActive(LocalDateTime.parse(offer.dateDebut.toString(), DateTimeFormatter.ISO_DATE_TIME))) {
                holder.chip_active.setChipBackgroundColorResource(R.color.teal_200)
                holder.chip_active.text = context.getString(R.string.chip_available)
            } else {
                holder.chip_active.setChipBackgroundColorResource(R.color.red)
                holder.chip_active.text = context.getString(R.string.chip_not_available)
            }

            Glide.with(context).load(offer.image).into(holder.image_post)

            holder.itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val context = v?.context as AppCompatActivity

                    FirebaseAuth.getInstance().currentUser?.let {
                        var bid = Firebase.firestore.collection("Offers").document(offer.id)
                            .collection("bid").orderBy("date").get().addOnSuccessListener {

                                Toast.makeText(context, it.documents.get(0).toString(), Toast.LENGTH_SHORT).show()

                                val intent = Intent(context, PostActivity::class.java)
                                intent.putExtra(
                                    "Offer", Offer(
                                        id = offer.id, name = offer.name,
                                        description = offer.description, price = offer.price,
                                        dateDebut = offer.dateDebut, image = offer.image,
                                        active = offer.active, ownerId = offer.ownerId
                                    )
                                )

                                ContextCompat.startActivity(context, intent, null)

                            }

                    }
                }
            })

        }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.offer_item_activity, parent, false)
        return ViewHolder(view)
    }

    private fun getUser(userRef: String) {

        val db = Firebase.firestore
        db.collection("users").document(userRef).addSnapshotListener { user, error ->

            user?.let {
                userReference.value = User(user.getString("avatar_uri"),
                    user.getString("location"),
                    user.getString("mail").toString(),
                    user.getString("name").toString(),
                    user.getString("psuedo").toString())
            }
        }
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val name_post = itemview.findViewById<TextView>(R.id.txt_name)
        val price_post = itemview.findViewById<TextView>(R.id.txt_price)
        val image_post = itemview.findViewById<ImageView>(R.id.img_post)
        val owner_post = itemview.findViewById<TextView>(R.id.txt_owner)
        val time_post = itemview.findViewById<TextView>(R.id.txt_time)
        val chip_active = itemview.findViewById<Chip>(R.id.chip_active)
    }
}