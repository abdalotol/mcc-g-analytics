package com.example.mcc_g_analyticsabd

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FormActivity : AppCompatActivity() {
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    var et_name:EditText? = null
    var et_price:EditText? = null
    var et_description:EditText? = null
    var image:ImageView? = null
    var btn_save:Button? = null
    val PICK_IMAGE_REQUEST = 1
    var filepath: Uri? = null
    var storageReference: StorageReference? = null
    var db: FirebaseFirestore? = null
    var category_id:Int? = null
    var now : Long? = null
    var end : Long? = null
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        category_id = intent.getIntExtra("category_id", 0)
        Toast.makeText(this, category_id.toString(), Toast.LENGTH_LONG).show()
        et_name = findViewById(R.id.ed_name)
        et_price = findViewById(R.id.ed_price)
        et_description = findViewById(R.id.ed_des)
        image = findViewById(R.id.image)
        btn_save = findViewById(R.id.btn_save)
        storageReference = FirebaseStorage.getInstance().reference
        db = FirebaseFirestore.getInstance()
        sharedPreferences = this.getSharedPreferences("timer",
            Context.MODE_PRIVATE)

        btn_save!!.setOnClickListener {view->
            if(et_name!!.text.isNotEmpty()){
                if(et_price!!.text.isNotEmpty()){
                    if(et_description!!.text.isNotEmpty()){
                        if(filepath != null) {
                            uploadImage(view)
                        }else{
                            Snackbar.make(view, "Pleas Choose Any Image", Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }else{
                        Snackbar.make(view, "The Description Must Not Be Empty", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }else{
                    Snackbar.make(view, "The Price Must Not Be Empty", Snackbar.LENGTH_LONG)
                        .show()
                }
            }else{
                Snackbar.make(view, "The Name Must Not Be Empty", Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        image!!.setOnClickListener {view->
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent,PICK_IMAGE_REQUEST)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null){
            filepath = data.data
            image!!.setImageURI(filepath)
        }
    }

    fun saveImageUri(uri: String){
        val item:HashMap<String, Any> = HashMap<String,Any>()
        item["image"] = uri
        item["category_id"] = category_id!!
        item["name"] = et_name!!.text.toString()
        item["price"] = et_price!!.text.toString().toInt()
        item["description"] = et_description!!.text.toString()
        db!!.collection("items").add(item)
        var intent = Intent(this,MainActivity2::class.java)
        intent.putExtra("category_id", category_id)
        startActivity(intent)
    }

    fun uploadImage(view: View){
        var imageReference = storageReference!!.child("images/${filepath!!.pathSegments}")
        imageReference.putFile(filepath!!)
            .addOnSuccessListener { uri->
                val url = imageReference.downloadUrl.addOnSuccessListener {
                    Log.e("test",it.toString())
                    saveImageUri(it.toString())

                }
                Snackbar.make(view, "The Image Upload Successfully", Snackbar.LENGTH_LONG)
                    .show()
            }
            .addOnFailureListener {
                Snackbar.make(view, "The Image Not Upload Successfully", Snackbar.LENGTH_LONG)
                    .show()
            }
    }

    override fun onResume() {
        super.onResume()
        TrackScreenViews("Add Product","FormActivity")
        now = System.currentTimeMillis()
        var screen = sharedPreferences!!.getInt("screen",0)
        if( screen != 0 ){
            var time = sharedPreferences!!.getInt("time",0)
            var pageName = sharedPreferences!!.getString("pageName","")
            val timer:HashMap<String, Any> = HashMap<String,Any>()
            timer["screen"] = screen
            timer["time"] = time
            timer["pageName"] = pageName!!
            db!!.collection("timer").add(timer)
        }
    }

    override fun onPause() {
        super.onPause()
        end = System.currentTimeMillis()
        val tiemSecond = (end!! - now!!)/1000
        var screen = sharedPreferences!!.getInt("screen",0)

        val editor:SharedPreferences.Editor =  sharedPreferences!!.edit()
        editor.putLong("time",tiemSecond)
        editor.putInt("screen",2)
        editor.putString("pageName","Product")


    }

    fun TrackScreenViews(screenName: String, screenClass: String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS,screenClass)
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
}