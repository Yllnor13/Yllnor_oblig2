package com.example.yllnor_oblig2

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView

class PartyAdapter(private val alpacapartier : List<AlpacaParty>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        //return alpacapartier.size
        var total = 0 //alpacapartier.size er lik 1, men det er 4 alpakaen, saa den ma inn i hver liste og telle den opp
        for(i in alpacapartier){
            for(j in i.parties!!){
                total ++
            }
        }
        return total
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PartyAdapter ->{
                //var total = 0
                for(i in alpacapartier){ //gaar gjennom hver alpakaparty i lista og bruker adapater for aa sette informasjonen i kort
                    i.parties?.let { holder.bind(it[position]) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder { //lager recyclerview viewholder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.element, parent, false)
        return PartyAdapter(v)
    }

    inner class PartyAdapter(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val alpacaBilde: CircleImageView = itemView.findViewById(R.id.image)  //lagrer viewene i kortet
        private val alpacaNavn: TextView = itemView.findViewById(R.id.navn)
        private val alpacaLag: TextView = itemView.findViewById(R.id.lag)
        private val farge: View = itemView.findViewById(R.id.farge)
        private val stemmer: TextView = itemView.findViewById(R.id.stemmer)

        fun bind(nyalp : Alpaca){  //naar den faar alpakka objekt saa vil den fordele verdiene til viewene som tilhoerer den
            alpacaNavn.text = nyalp.leader
            alpacaLag.text = nyalp.name
            farge.setBackgroundColor(Color.parseColor(nyalp.color))
            stemmer.text = nyalp.stemmetekst
            val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context).applyDefaultRequestOptions(requestOptions).load(nyalp.img).into(alpacaBilde) //henter bildet fra nettet
        }
    }
}