package com.example.kasirjose

import com.example.kasirjose.util.FormatHelper
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatHelperTest {

    @Test
    fun testFormatRupiah() {
        assertEquals("Rp3.500", FormatHelper.formatRupiah(3500.0))
        assertEquals("Rp10.000", FormatHelper.formatRupiah(10000.0))
        assertEquals("Rp125.000", FormatHelper.formatRupiah(125000.0))
        assertEquals("Rp0", FormatHelper.formatRupiah(0.0))
    }
}
