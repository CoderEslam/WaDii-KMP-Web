package com.wadii.domain.model.provider


data class ProviderRequest(
    val name: String = "",
    val userId: Long = -1,
    val frontIdImage: ByteArray? = null,
    val backIdImage: ByteArray? = null,
    val taxCardFront: ByteArray? = null,
    val taxCardBack: ByteArray? = null,
    val address: String = "",
    val phoneNumber: String = "",
    val serviceIds: List<Long> = emptyList(),
    val links: List<String> = emptyList()
)