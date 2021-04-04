package com.example.mcc_g_analyticsabd


import android.app.usage.UsageStats
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log

import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.time.seconds


class MainActivity : AppCompatActivity() {
    var mFirebaseAnalytics:FirebaseAnalytics? = null
    var btn_food:Button? = null
    var btn_laptop:Button? = null
    var btn_phone:Button? = null
    var now : Long? = null
    var end : Long? = null
    var sharedPreferences: SharedPreferences? = null
    var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        btn_food = findViewById(R.id.btn_food)
        btn_laptop = findViewById(R.id.btn_laptop)
        btn_phone = findViewById(R.id.btn_phone)
        db = FirebaseFirestore.getInstance()
        sharedPreferences = this.getSharedPreferences("timer",
            Context.MODE_PRIVATE)
        btn_food!!.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("category_id", 1)
            startActivity(intent)
        }


        btn_laptop!!.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("category_id", 2)
            startActivity(intent)
        }

        btn_phone!!.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("category_id", 3)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        TrackScreenViews("All Categories","MainActivity")
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
            val editor:SharedPreferences.Editor =  sharedPreferences!!.edit()
            editor.putLong("time",tiemSecond)
            editor.putInt("screen",1)
            editor.putString("pageName","Categories")


    }

    fun TrackScreenViews(screenName: String, screenClass: String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS,screenClass)
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }


}