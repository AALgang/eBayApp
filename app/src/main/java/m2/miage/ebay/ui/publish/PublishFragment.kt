package m2.miage.ebay.ui.publish

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_publish.*
import m2.miage.ebay.R
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import java.util.jar.Manifest
import androidx.annotation.NonNull

import com.google.firebase.storage.StorageMetadata
import androidx.core.app.ActivityCompat.startActivityForResult

import androidx.core.content.FileProvider

import android.provider.MediaStore
import com.google.firebase.auth.FirebaseAuth


class PublishFragment : Fragment() {

    val PICK_IMAGE_REQUEST = 234
    var image_uri = ""
    val db = Firebase.firestore
    //val storage = Firebase.storage
    val storage = Firebase.storage("gs://miage-ebay.appspot.com")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_publish, container, false)
    }

    //TODO : vérifier que la date entrée est bien supèrieure a la date du jour
    //TODO : faire la page plus bg sinon alexis taper moi
    //TODO : faire une classe générique pour l'ajout d'image dans la base

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_annuler.setOnClickListener{ View ->
            //Toast.makeText(context, "bouton annuler cliqué ", Toast.LENGTH_SHORT).show()
            clearAll()
        }

        bt_valider.setOnClickListener { View ->
            //Create a new offer with datas
            val Annonce = hashMapOf(
                "active" to true,
                "dateDebut" to date.text.toString(),
                "desc" to tb_description.text.toString(),
                "nom" to tb_Name.text.toString(),
                "photo" to image_uri,
                "prixInitial" to tb_prix.text.toString().toInt(),
                "proprietaire" to FirebaseAuth.getInstance().currentUser?.uid
            )

            //récup user
            FirebaseAuth.getInstance().currentUser?.uid

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
            image_uri = pushPicture(data)


        }
    }

    fun pushPicture(data: Intent?) :String{
        val storageRef = storage.reference
        var imageUri : String = ""
        val selectedImageUri = data!!.data
        val imgageIdInStorage = selectedImageUri!!.lastPathSegment!! //here you can set whatever Id you need
        storageRef.child(imgageIdInStorage).putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                val urlTask = taskSnapshot.storage.downloadUrl
                Log.i("TEST",taskSnapshot.storage.downloadUrl.toString())
                urlTask.addOnSuccessListener { uri ->
                    Log.i("TEST","POUET ça marche pour l'uri ${uri}")
                    imageUri = uri.toString()
                }
            }
            .addOnFailureListener { e ->
                // Handle unsuccessful upload
                Toast.makeText(context,"Raté nullos ",Toast.LENGTH_LONG).show()
                Log.i("TEST",e.toString())
                Log.i("TEST",e.message.toString())
            }

        return imageUri
    }



}
