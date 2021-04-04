package com.example.mcc_g_analyticsabd

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity2 : AppCompatActivity() {
    var mFirebaseAnalytics:FirebaseAnalytics? = null
    var btn_float: View? = null
    var db: FirebaseFirestore? = null
    var items:MutableList<item>? = null
    var itemOne:Map<String, Any>? = null
    var re_items: RecyclerView? = null
    var category_id:Int? =  null
    var now : Long? = null
    var end : Long? = null
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        btn_float = findViewById(R.id.btn_float)
        db = FirebaseFirestore.getInstance()
        items = mutableListOf()
        re_items = findViewById(R.id.controller)
        category_id = intent.getIntExtra("category_id", 0)
        Log.e("LOI", "sssssssssssssss ${category_id.toString()}")
        sharedPreferences = this.getSharedPreferences("timer",
            Context.MODE_PRIVATE)
//        Toast.makeText(this,category_id.toString(),Toast.LENGTH_LONG).show()
        btn_float!!.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra("category_id", category_id)
            startActivity(intent)
        }

        db!!.collection("items")
            .get()
            .addOnCompleteListener {result->
                if(result.isSuccessful){
                    for( document in result.result!!){
                        Log.e("LOI", document.data.toString())

                        itemOne = document.data
                        if(category_id == itemOne!!["category_id"].toString().toInt()){
                            items!!.add(item(itemOne!!["name"].toString(),itemOne!!["price"].toString().toInt(),itemOne!!["description"].toString(),itemOne!!["image"].toString()))
                        }
                    }
                    Log.e("LOI", items!!.size.toString())


                    val contactAdapter = ItemAdapter(this,items!!)
                    re_items!!.adapter = contactAdapter
                }else{
                    Log.e("LOI", "document.data.toString()")

                }

            }
    }

    override fun onResume() {
        super.onResume()
        TrackScreenViews("All Products","MainActivity2")
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