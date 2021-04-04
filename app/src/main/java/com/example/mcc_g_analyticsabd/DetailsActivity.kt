package com.example.mcc_g_analyticsabd

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DetailsActivity : AppCompatActivity() {
    var mFirebaseAnalytics: FirebaseAnalytics? = null
    var img_image:ImageView? = null
    var txt_name:TextView? = null
    var txt_price:TextView? = null
    var txt_description:TextView? = null
    var now : Long? = null
    var end : Long? = null
    var sharedPreferences: SharedPreferences? = null
    var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        img_image = findViewById(R.id.item_imag)
        txt_name = findViewById(R.id.name)
        txt_description = findViewById(R.id.description)
        var obj: item = intent.getSerializableExtra("obj") as item
        Toast.makeText(this,obj.name,Toast.LENGTH_LONG).show()

        sharedPreferences = this.getSharedPreferences("timer",
            MODE_PRIVATE
        )
        db = FirebaseFirestore.getInstance()

        Picasso.get()
            .load(obj.image)
            .into(img_image)
        txt_name!!.text = obj.name
        txt_price!!.text = "Price : ${obj.price}"
        txt_description!!.text = obj.description
    }

    override fun onResume() {
        super.onResume()
        TrackScreenViews("Product Details","DetailsActivity")
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