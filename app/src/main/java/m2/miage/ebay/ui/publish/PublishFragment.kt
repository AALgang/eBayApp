package m2.miage.ebay.ui.publish

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_publish.*
import m2.miage.ebay.R

class PublishFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_publish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = Firebase.firestore

        bt_annuler.setOnClickListener{ View ->
            //Toast.makeText(context, "bouton annuler cliqué ", Toast.LENGTH_SHORT).show()
        }

        bt_valider.setOnClickListener { View ->
            //Toast.makeText(context, "bouton valider cliqué ", Toast.LENGTH_SHORT).show()
            //Create a new offer with datas
            val Annonce = hashMapOf(
                "active" to true,
                "dateDebut" to date.text.toString(),
                "desc" to tb_description.text.toString(),
                "nom" to Integer.parseInt(tb_Name.text.toString()),
                "photo" to "https://medias.go-sport.com/media/resized/1300x/catalog/product/01/36/27/93/pouet-pouet_1_v1.jpg",
                "prixInitial" to tb_prix.text.toString(),
                "proprietaire" to "utilisateur/5nLqB9JSgBbvOYySt4QMqkh09r93"
            )

            // Add a new document with a generated ID
            db.collection("Offer")
                .add(Annonce)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(context, "DocumentSnapshot added with ID: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                    //Log.d("ADD", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->

                    Toast.makeText(context, "Error adding document", Toast.LENGTH_SHORT).show()
                    //Log.w("BUG", "Error adding document", e)
                }
        }

    }


}
