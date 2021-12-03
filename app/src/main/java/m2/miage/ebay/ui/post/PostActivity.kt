package m2.miage.ebay.ui.post

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_post.*
import m2.miage.ebay.R
import m2.miage.ebay.data.Offer
import m2.miage.ebay.databinding.ActivityPostBinding
import m2.miage.ebay.util.DateUtil.Companion.isOfferActive
import m2.miage.ebay.util.Resource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val binding: ActivityPostBinding = DataBindingUtil.setContentView(this, R.layout.activity_post)
        binding.offer = intent.getParcelableExtra("Offer")

        binding.offer?.let {
            Glide.with(this).load(it.image!!).into(imgPost)
        }

        btn_pay.isEnabled = isOfferActive(LocalDateTime.parse(binding.offer?.dateDebut.toString(), DateTimeFormatter.ISO_DATE_TIME))
        txt_new_price.isEnabled = btn_pay.isEnabled

        btn_pay.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Erreur de saisie")

            if (!txt_price.text.isNullOrEmpty() && !txt_new_price.text.isNullOrEmpty()) {

                val actualPrice: Double = txt_price.text.toString().toDouble()
                val newPrice: Double = txt_new_price.text.toString().toDouble()

                if (actualPrice >= newPrice) {
                    builder.setMessage("Le nouveau prix ne peux pas être inférieur au prix actuel !")
                    builder.setNeutralButton("Ok", { dialog, which -> dialog.cancel() })
                    builder.show()
                } else {
                    Firebase.firestore.collection("Enchere").whereEqualTo("produit", binding.offer?.id).get().addOnSuccessListener{ doc ->

                        Toast.makeText(this, doc.size().toString(), Toast.LENGTH_LONG).show()

                        // Add a new document with a generated ID
                        FirebaseAuth.getInstance().currentUser?.let {
                            Firebase.firestore.collection("Enchere").document(it.uid)
                                .update("price", txt_new_price.text.toString())
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Payé", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                }

            }
        }
    }
}