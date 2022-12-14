package com.akhmadaldi.storyapp.utils

import org.junit.Assert
import org.junit.Test

class EmailValidationTest{
    @Test
    fun `given correct Email format then should return true`() {
        val email = "aldiakhmad19@example.com"
        Assert.assertTrue(EmailValidation.isValidEmail(email))
    }

    @Test
    fun `given wrong Email format then should return false`() {
        val wrongFormat = "aldiakhmad19gmail.com"
        Assert.assertFalse(EmailValidation.isValidEmail(wrongFormat))
    }
}