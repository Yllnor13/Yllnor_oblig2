package com.example.yllnor_oblig2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var alpacapartyListe = mutableListOf<AlpacaParty>() //variabler som trengs
        val path = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/alpacaparties.json"
        val valg1 = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district1.json"
        val valg2 = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district2.json"
        val valg3 = "https://www.uio.no/studier/emner/matnat/ifi/IN2000/v21/obligatoriske-oppgaver/alpakkaland/district3.xml"
        val gson = Gson()
        var adapter : PartyAdapter
        val recycler = findViewById<RecyclerView>(R.id.recyclerview)
        var respons : AlpacaParty
        val spinner = findViewById<Spinner>(R.id.spinner)
        var valget: String
        val valg = resources.getStringArray(R.array.valg)
        //var stemmer : MutableList<Int>

        fun settInn(a : MutableList<AlpacaParty>, s : MutableList<Int>, t : Int){ //lagde metode istedenfor aa bruke samme kode 2 ganger
            var pos = 0 //gaar gjennom alpakaparti lista og fordeler stemmer + % av helheten de fikk, som over
            for(i in a){
                for (j in i.parties!!){
                    var prosent = (s[pos].toDouble() / t) * 100
                    prosent = String.format("%.2f", prosent).toDouble()
                    j.stemmetekst = s[pos].toString() + " stemmer, " + prosent.toString() + "% av totalen"
                    pos++
                }
            }
            runOnUiThread{
                recycler.apply{ //oppdaterer recycler viewet
                    recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = PartyAdapter(alpacapartyListe)
                    recycler.adapter = adapter
                }
            }
        }

        fun settInnStemmer(idListe : List<Id>, a : MutableList<AlpacaParty>) : MutableList<Int>{
            var stemmetall : Int
            for(i in a){
                for(j in i.parties!!){
                    stemmetall = 0
                    for(k in idListe){ //fordeler stemmer
                        if(k.id == j.id){
                            stemmetall++
                            j.stemme = stemmetall
                        }
                    }
                }
            }

            var stemmeliste = mutableListOf<Int>()

            for(i in a) {
                for (j in i.parties!!) {
                    stemmeliste.add(j.stemme!!)
                }
            }
            return stemmeliste
        }

        ArrayAdapter.createFromResource(this, R.array.valg, android.R.layout.simple_spinner_item). also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) //setter items i resources inn i spinneren
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{ //hva som skjer når items i spinneren er valgt
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                valget = valg[position] //gjoer valget om til det som er valgt på spinneren
                when(valget){
                    "valgdistrikt 1" ->{ //dersom man velger foerste posisjon
                        CoroutineScope(newSingleThreadContext("valgdistrikt1")).launch(Dispatchers.IO){
                            try{
                                //println(Fuel.get(valg1).awaitString()) // "{"origin":"127.0.0.1"}"
                                val mlid : List<Id> = gson.fromJson(Fuel.get(valg1).awaitString(), Array<Id>::class.java).toList()//henter json og gjoer det om til objekter som er satt i liste

                                val stemmer = settInnStemmer(mlid, alpacapartyListe)
                                val totalmengdestemmer = stemmer.sum() //finner total mengden av stemmer for a regne ut prosent
                                Log.d("valgdistrikt 1 a", alpacapartyListe.toString())
                                Log.d("valgdistrikt 1 s", stemmer.toString())
                                Log.d("valgdistrikt 1 t", totalmengdestemmer.toString())

                                settInn(alpacapartyListe, stemmer, totalmengdestemmer)
                                CoroutineScope(newSingleThreadContext("valgdistrikt1")).launch(Dispatchers.IO){

                                }
                            } catch(exception: Exception) {
                                println("A network request exception was thrown: ${exception.message}")
                            }
                        }
                    }
                    "valgdistrikt 2" ->{
                        CoroutineScope(newSingleThreadContext("valgdistrikt2")).launch(Dispatchers.IO){ //gjoer det samme som "valgdistrikt 1"
                            try{
                                //println(Fuel.get(valg2).awaitString()) // "{"origin":"127.0.0.1"}"
                                val mlid : List<Id> = gson.fromJson(Fuel.get(valg2).awaitString(), Array<Id>::class.java).toList()

                                val stemmer = settInnStemmer(mlid, alpacapartyListe)
                                val totalmengdestemmer = stemmer.sum()
                                Log.d("valgdistrikt 2 a", alpacapartyListe.toString())
                                Log.d("valgdistrikt 2 s", stemmer.toString())
                                Log.d("valgdistrikt 2 t", totalmengdestemmer.toString())
                                settInn(alpacapartyListe, stemmer, totalmengdestemmer)
                            } catch(exception: Exception) {
                                println("A network request exception was thrown: ${exception.message}")
                            }
                        }
                        //valgdistriktListe = hentvalgdistrikt.await()
                    }
                    "valgdistrikt 3" ->{
                        CoroutineScope(newSingleThreadContext("valgdistrikt2")).launch(Dispatchers.IO){ //henter inn xml og lager objekter via xml parser
                            async{
                                try{
                                    val xml = Fuel.get(valg3).awaitString()
                                    //Log.d("ListePrint xml", xml.toString()) //sjekket om det funket
                                    val inputStream = xml.byteInputStream()
                                    //Log.d("ListePrint inputstream", inputStream.toString())
                                    val listOfParties = XmlParser().parse(inputStream) //parser informasjonen og faar tilbake liste med partier og hvor mange stemmer de fikk
                                    //Log.d("party objekter liste", listOfParties.toString())
                                    var stemmer = mutableListOf<Int>()
                                    var stemmetall : Int
                                    for(i in alpacapartyListe){
                                        for(j in i.parties!!){
                                            for(k in listOfParties){ //fordeler stemmer
                                                if(k.id == j.id){
                                                    j.stemme = k.votes
                                                    k.votes?.let { stemmer.add(it) }
                                                }
                                            }
                                        }
                                    }
                                    //Log.d("id1 total", id1.toString())
                                    //Log.d("id2 total", id2.toString())
                                    //Log.d("id3 total", id3.toString())
                                    //Log.d("id4 total", id4.toString())
                                    val totalmengdestemmer = stemmer.sum()
                                    Log.d("valgdistrikt 3 a", alpacapartyListe.toString())
                                    Log.d("valgdistrikt 3 s", stemmer.toString())
                                    Log.d("valgdistrikt 3 t", totalmengdestemmer.toString())
                                    settInn(alpacapartyListe, stemmer, totalmengdestemmer)
                                } catch(exception: Exception) {
                                    println("A network request exception was thrown: ${exception.message}")
                                }
                            }
                            //valgdistriktListe = hentvalgdistrikt.await()
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(applicationContext,"Velg noe i drop down menyen",Toast.LENGTH_SHORT).show()
            }
        }


        CoroutineScope(newSingleThreadContext("henter")).launch(Dispatchers.IO){ //dette er for aa sette opp lista i begynnelsen
            val apiCall = async{
                try{
                    //println(Fuel.get(path).awaitString()) // "{"origin":"127.0.0.1"}"
                    respons = gson.fromJson(Fuel.get(path).awaitString(), AlpacaParty::class.java)
                    /*for (i in 0 until respons.size){
                        print(respons[i].name)
                    }*/
                    //Log.d("API fetching", respons.toString())
                    return@async respons
                } catch(exception: Exception) {
                    println("A network request exception was thrown: ${exception.message}")
                }
            }
            respons = apiCall.await() as AlpacaParty
            alpacapartyListe.add(respons)
            Log.d("coroutine main", alpacapartyListe.toString())
            recycler.apply{
                recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = PartyAdapter(alpacapartyListe)
                recycler.adapter = adapter
            }
        }
        //Log.d("liste test", alpacapartyListe.toString())
    }
}

// result generated from /json