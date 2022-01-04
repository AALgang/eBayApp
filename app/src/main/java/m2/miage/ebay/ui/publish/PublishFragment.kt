package m2.miage.ebay.ui.publish

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_publish.*
import m2.miage.ebay.R

import android.text.Editable
import android.text.TextUtils.substring
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.ZonedDateTime


class PublishFragment : Fragment() {

    val PICK_IMAGE_REQUEST = 234
    var image_url = ""
    val db = Firebase.firestore
    //val storage = Firebase.storage
    val storage = Firebase.storage("gs://miage-ebay.appspot.com")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_publish, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_annuler.setOnClickListener{ View ->
            //Toast.makeText(context, "bouton annuler cliqué ", Toast.LENGTH_SHORT).show()
            clearAll()
        }

        bt_valider.setOnClickListener { View ->
            //vérification que la date est bien supèrieure à la date du jour
            if(isValid(date.text.toString())) {
                val jour = substring(date.text,0,2)
                val mois = substring(date.text,3,5)
                val an = substring(date.text,6,10)
                val time = substring(LocalTime.now().toString(),0,8)
                val dateFormate = an+"-"+mois+"-"+jour+"T"+time

               /*
               val dateFormate = LocalDate.of(an.toInt(),mois.toInt(),jour.toInt(),hour.toInt(),minute.toInt())
               val currentDateTime = LocalDateTime.now()
                val compare = compareToDay(dateFormate,currentDateTime)

                Toast.makeText(context,compare.toString(),Toast.LENGTH_LONG)*/
                //teste si la date est au bon format

                //Create a new offer with datas
                val Annonce = hashMapOf(
                    "active" to true,
                    "dateDebut" to dateFormate,
                    "desc" to tb_description.text.toString(),
                    "nom" to tb_Name.text.toString(),
                    "photo" to image_url,
                    "prixInitial" to tb_prix.text.toString(),
                    "proprietaire" to FirebaseAuth.getInstance().currentUser?.uid
                )
                //Toast.makeText(context, Annonce.toString(),Toast.LENGTH_LONG).show()
                Log.d("TEST", Annonce.toString())

                //Add a new document with a generated ID
                db.collection("Offers")
                    .add(Annonce)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(context, "INSERTION EFFECTUEE", Toast.LENGTH_SHORT).show()
                        Log.d("TEST", "DocumentSnapshot added with ID: ${documentReference.id}")
                        clearAll()
                    }
                    .addOnFailureListener { e ->
                        //Toast.makeText(context, "Error adding document", Toast.LENGTH_SHORT).show()
                        Toast.makeText(context, "ERREUR D'INSERTION", Toast.LENGTH_SHORT).show()
                        Log.w("TEST", "Error adding document", e)
                    }
            }else{
                Toast.makeText(context,"Veuillez entrer un format de date correct pour valider",Toast.LENGTH_LONG)
                tv_title8.setTextColor(Color.RED)
                tv_error.setText("Merci d'entrer une date au bon format pour pouvoir valider")
                tv_error.setTextColor(Color.RED)
            }
        }

        bt_add.setOnClickListener{
            openGalleryForImage()
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        //startActivityForResult(intent, 1000)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    fun clearAll(){
        tb_prix.text.clear()
        tb_Name.text.clear()
        tb_description.text.clear()
        date.text.clear()
        imageView.setImageURI(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            // I'M GETTING THE URI OF THE IMAGE AS DATA AND SETTING IT TO THE IMAGEVIEW
                Log.i("TEST",data?.data.toString())
            imageView.setImageURI(data?.data)
            //uploadImageToFirebase(data?.data)
            //data?.data?.let { uploadImageToFirebase(it) }
            pushPicture(data)
        }
    }

    fun pushPicture(data: Intent?) :String{
        val storageRef = storage.reference
        var imageUrl : String = ""
        val selectedImageUri = data!!.data
        val imgageIdInStorage = selectedImageUri!!.lastPathSegment!! //here you can set whatever Id you need
        storageRef.child(imgageIdInStorage).putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                val urlTask = taskSnapshot.storage.downloadUrl
                Log.i("TEST",taskSnapshot.storage.downloadUrl.toString())
                urlTask.addOnSuccessListener { uri ->
                    Log.i("TEST","POUET ça marche pour l'url ${uri}")
                    imageUrl = uri.toString()
                    image_url = imageUrl
                }
            }
            .addOnFailureListener { e ->
                // Handle unsuccessful upload
                Toast.makeText(context,"Raté nullos ",Toast.LENGTH_LONG).show()
                Log.i("TEST",e.toString())
                Log.i("TEST",e.message.toString())
            }

        Toast.makeText(context,imageUrl,Toast.LENGTH_LONG).show()
        return imageUrl
    }

    fun isValid(dateStr: String?): Boolean {
        val format = SimpleDateFormat("dd/MM/yyyy")
        format.isLenient = false
        try {
            format.parse(dateStr)
        } catch (e: ParseException) {
            return false
        }
        return true
    }
/*
    fun compareToDay(date1: LocalDateTime?, date2: LocalDateTime?): Int {
        val sdf = SimpleDateFormat("yyyyMMdd")
        return sdf.format(date1).compareTo(sdf.format(date2))
    }
*/

}
