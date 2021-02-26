package com.example.yllnor_oblig2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.sql.DataSource

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
        var valget = ""
        val valg = resources.getStringArray(R.array.valg)
        //var stemmer : MutableList<Int>

        ArrayAdapter.createFromResource(this, R.array.valg, android.R.layout.simple_spinner_item). also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) //setter items i resources inn i spinneren
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{ //hva som skjer når items i spinneren er valgt
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                valget = valg[position] //gjoer valget om til det som er valgt på spinneren
                when(valget){
                    "valgdistrikt 1" ->{ //dersom man velger foerste posisjon
                        CoroutineScope(newSingleThreadContext("valgdistrikt1")).launch(Dispatchers.Main){
                            var hentvalgdistrikt = async{
                                try{
                                    //println(Fuel.get(valg1).awaitString()) // "{"origin":"127.0.0.1"}"
                                    val mlid : List<Id> = gson.fromJson(Fuel.get(valg1).awaitString(), Array<Id>::class.java).toList()//henter json og gjoer det om til objekter som er satt i liste
                                    //Log.d("ListePrint", mlid.toString()) //sjekker om det funket
                                    var id1 = 0//variabler for aa vite hvor mange stemmer det er
                                    var id2 = 0
                                    var id3 = 0
                                    var id4 = 0
                                    for(i in mlid){ //fordeler stemmer
                                        when(i.id){
                                            "1" -> {
                                                id1++
                                            }
                                            "2" -> {
                                                id2++
                                            }
                                            "3" -> {
                                                id3++
                                            }
                                            "4" -> {
                                                id4++
                                            }
                                        }
                                    }
                                    //Log.d("id1 total", id1.toString())
                                    //Log.d("id2 total", id2.toString())
                                    //Log.d("id3 total", id3.toString())
                                    //Log.d("id4 total", id4.toString()) //sjekker om den tellte alt riktig
                                    var stemmer = mutableListOf<Int>(id1, id2, id3, id4)
                                    val totalmengdestemmer = stemmer.sum() //finner total mengden av stemmer for a regne ut prosent

                                    var pos = 0
                                    for(i in alpacapartyListe){//fordeler stemmene til hver alpakaparti (la til enda en val i dataklassen for aa faa til dette
                                        for (j in i.parties!!){
                                            var prosent = (stemmer[pos].toDouble() / totalmengdestemmer) * 100
                                            prosent = String.format("%.2f", prosent).toDouble()
                                            j.stemme = stemmer[pos].toString() + " stemmer, " + prosent.toString() + "% av totalen"
                                            pos++
                                        }
                                    }
                                    recycler.apply{ //oppdaterer recyclerviewet med ny informasjon
                                        recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                                        adapter = PartyAdapter(alpacapartyListe)
                                        recycler.adapter = adapter
                                    }
                                } catch(exception: Exception) {
                                    println("A network request exception was thrown: ${exception.message}")
                                }
                            }
                            //valgdistrikt = hentvalgdistrikt.await()
                        }
                    }
                    "valgdistrikt 2" ->{
                        CoroutineScope(newSingleThreadContext("valgdistrikt2")).launch(Dispatchers.Main){ //gjoer det samme som "valgdistrikt 1"
                            var hentvalgdistrikt = async{
                                try{
                                    //println(Fuel.get(valg2).awaitString()) // "{"origin":"127.0.0.1"}"
                                    val mlid : List<Id> = gson.fromJson(Fuel.get(valg2).awaitString(), Array<Id>::class.java).toList()
                                    Log.d("ListePrint", mlid.toString())
                                    var id1 = 0
                                    var id2 = 0
                                    var id3 = 0
                                    var id4 = 0
                                    for(i in mlid){
                                        when(i.id){
                                            "1" -> {
                                                id1++
                                            }
                                            "2" -> {
                                                id2++
                                            }
                                            "3" -> {
                                                id3++
                                            }
                                            "4" -> {
                                                id4++
                                            }
                                        }
                                    }
                                    Log.d("id1 total", id1.toString())
                                    Log.d("id2 total", id2.toString())
                                    Log.d("id3 total", id3.toString())
                                    Log.d("id4 total", id4.toString())
                                    var stemmer = mutableListOf<Int>(id1, id2, id3, id4)
                                    val totalmengdestemmer = stemmer.sum()

                                    var pos = 0
                                    for(i in alpacapartyListe){
                                        for (j in i.parties!!){
                                            var prosent = (stemmer[pos].toDouble() / totalmengdestemmer) * 100
                                            prosent = String.format("%.2f", prosent).toDouble()
                                            j.stemme = stemmer[pos].toString() + " stemmer, " + prosent.toString() + "% av totalen"
                                            pos++
                                        }
                                    }
                                    recycler.apply{
                                        recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                                        adapter = PartyAdapter(alpacapartyListe)
                                        recycler.adapter = adapter
                                    }
                                } catch(exception: Exception) {
                                    println("A network request exception was thrown: ${exception.message}")
                                }
                            }
                            //valgdistriktListe = hentvalgdistrikt.await()
                        }
                    }
                    "valgdistrikt 3" ->{
                        CoroutineScope(newSingleThreadContext("valgdistrikt2")).launch(Dispatchers.Main){ //henter inn xml og lager objekter via xml parser
                            var hentvalgdistrikt = async{
                                try{
                                    val xml = Fuel.get(valg3).awaitString()
                                    //Log.d("ListePrint xml", xml.toString()) //sjekket om det funket
                                    val inputStream = xml.byteInputStream()
                                    //Log.d("ListePrint inputstream", inputStream.toString())
                                    val listOfParties = XmlParser().parse(inputStream) //parser informasjonen og faar tilbake liste med partier og hvor mange stemmer de fikk
                                    //Log.d("party objekter liste", listOfParties.toString())
                                    var id1 = 0
                                    var id2 = 0
                                    var id3 = 0
                                    var id4 = 0
                                    for(i in listOfParties){ //fordeler stemmene til variabler som teller stemmer
                                        when(i.id){
                                            "1" -> {
                                                id1 = i.votes!!
                                            }
                                            "2" -> {
                                                id2 = i.votes!!
                                            }
                                            "3" -> {
                                                id3 = i.votes!!
                                            }
                                            "4" -> {
                                                id4 = i.votes!!
                                            }
                                        }
                                    }
                                    //Log.d("id1 total", id1.toString())
                                    //Log.d("id2 total", id2.toString())
                                    //Log.d("id3 total", id3.toString())
                                    //Log.d("id4 total", id4.toString())
                                    var stemmer = mutableListOf<Int>(id1, id2, id3, id4)
                                    val totalmengdestemmer = stemmer.sum()

                                    var pos = 0 //gaar gjennom alpakaparti lista og fordeler stemmer + % av helheten de fikk, som over
                                    for(i in alpacapartyListe){
                                        for (j in i.parties!!){
                                            var prosent = (stemmer[pos].toDouble() / totalmengdestemmer) * 100
                                            prosent = String.format("%.2f", prosent).toDouble()
                                            j.stemme = stemmer[pos].toString() + " stemmer, " + prosent.toString() + "% av totalen"
                                            pos++
                                        }
                                    }
                                    recycler.apply{ //oppdaterer recycler viewet
                                        recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                                        adapter = PartyAdapter(alpacapartyListe)
                                        recycler.adapter = adapter
                                    }
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
        CoroutineScope(newSingleThreadContext("henter")).launch(Dispatchers.IO){ //dette er for aa sette opp lista i begynnelsen
            var apiCall = async{
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