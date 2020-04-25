package com.example.kandji_widget

class Item(hieroglyph: String, meanings: String, hiragana: String, reading: String) {

    private val hieroglyph = hieroglyph
    private val meanings = meanings
    private val hiragana = hiragana
    private val reading = reading

    fun getHieroglyph(): String {
        return hieroglyph
    }

    fun getMeanings(): String {
        return meanings
    }

    fun getHiragana(): String {
        return hiragana
    }

    fun getReading(): String {
        return reading
    }

}