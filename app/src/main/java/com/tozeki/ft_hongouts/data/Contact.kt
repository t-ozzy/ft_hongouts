package com.tozeki.ft_hongouts.data

data class Contact(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val email: String? = null,
    val memo: String? = null
)
