package com.robote.onlinestore


import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

object Constants {

    const val onSale = "Disponible"
    const val soldOut = "Vendido"

    var categories = arrayOf(
        "Móbiles",
        "Ordenadores/Laptops",
        "Electrónica y electrodomesticos",
        "Vehículos",
        "Consolas y videojuegos",
        "Hogar y muebles",
        "Belleza y cuidado personal",
        "Libros",
        "Deportes"
    )

    val condition = arrayOf(
        "Nuevo",
        "Usado",
        "Renovado"
    )

    fun getDeviceTime(): Long {
        return System.currentTimeMillis()
    }

    fun getDate(time: Long): String {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = time

        return DateFormat.format("dd/MM/yyyy", calendar).toString()
    }
}