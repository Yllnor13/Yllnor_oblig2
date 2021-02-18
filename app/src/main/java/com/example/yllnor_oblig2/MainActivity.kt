package com.example.yllnor_oblig2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.*
import javax.sql.DataSource

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var alpacapartyListe = mutableListOf<AlpacaParty>()
        val path = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/alpacaparties.json"
        val gson = Gson()
        var adapter : PartyAdapter
        val recycler = findViewById<RecyclerView>(R.id.recyclerview)

        /*runBlocking {
            try {
                println(Fuel.get(path).awaitString()) // "{"origin":"127.0.0.1"}"
                val respons = gson.fromJson(Fuel.get(path).awaitString(), AlpacaParty::class.java)
                /*for (i in 0 until respons.size){
                    print(respons[i].name)
                }*/
                Log.d("API fetching", respons.toString())
            } catch(exception: Exception) {
                println("A network request exception was thrown: ${exception.message}")
            }
        }*/
        CoroutineScope(newSingleThreadContext("henter")).launch(Dispatchers.IO){
            try {
                //println(Fuel.get(path).awaitString()) // "{"origin":"127.0.0.1"}"
                val respons = gson.fromJson(Fuel.get(path).awaitString(), AlpacaParty::class.java)
                /*for (i in 0 until respons.size){
                    print(respons[i].name)
                }*/
                //Log.d("API fetching", respons.toString())
                alpacapartyListe.add(respons)
                Log.d("liste test", alpacapartyListe.toString())

                recycler.apply{
                    recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = PartyAdapter(alpacapartyListe)
                    recycler.adapter = adapter
                }
            } catch(exception: Exception) {
                println("A network request exception was thrown: ${exception.message}")
            }
        }
    }
}

// result generated from /json