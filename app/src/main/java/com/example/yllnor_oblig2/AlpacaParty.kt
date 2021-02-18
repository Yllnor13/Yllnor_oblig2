package com.example.yllnor_oblig2

data class Alpaca (val id: String?, val name: String?, val leader: String?, val img: String?, val color: String?)

data class AlpacaParty(val parties: List<Alpaca>?)