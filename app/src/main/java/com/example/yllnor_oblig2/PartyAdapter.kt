package com.example.yllnor_oblig2

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class PartyAdapter(val alpacapartier : List<AlpacaParty>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return alpacapartier.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PartyAdapter ->{
                /*for(i in alpacapartier){
                    holder.ordne(i.get(position))
                }*/
                holder.ordne(alpacapartier.get(position))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.element, parent, false)
        return PartyAdapter(v)
    }

    inner class PartyAdapter(itemView: View) : RecyclerView.ViewHolder(itemView){
        val alpacaBilde = itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.image)
        val alpacaNavn = itemView.findViewById<TextView>(R.id.navn)
        val alpacaLag = itemView.findViewById<TextView>(R.id.lag)
        val farge = itemView.findViewById<View>(R.id.farge)

        fun ordne(nyalp : AlpacaParty){
            for(i in nyalp.parties.orEmpty()){
                alpacaNavn.setText(i.leader)
                alpacaLag.setText(i.name)
                farge.setBackgroundColor(Color.parseColor(i.color))
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)

                Glide.with(itemView.context).
                applyDefaultRequestOptions(requestOptions).
                load(i.img).
                into(alpacaBilde)
            }
        }
    }
}