package com.example.kasirjose.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatHelper {

    private val indonesianLocale: Locale = Locale.forLanguageTag("id-ID")

    private val rupiahSymbols = DecimalFormatSymbols(indonesianLocale).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    private val rupiahFormatter = DecimalFormat("#,###", rupiahSymbols)

    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy", indonesianLocale)
    
    private val dateTimeFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm 'WIB'", indonesianLocale)

    fun formatRupiah(amount: Double): String {
        return "Rp${rupiahFormatter.format(amount.toLong())}"
    }

    fun formatTanggal(timestamp: Long): String {
        return dateFormatter.format(Date(timestamp))
    }

    fun formatTanggalWaktu(timestamp: Long): String {
        return dateTimeFormatter.format(Date(timestamp))
    }
}
