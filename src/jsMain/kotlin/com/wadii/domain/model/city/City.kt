package com.wadii.domain.model.city

import com.wadii.domain.model.province.Province
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class City(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("provinceId")
    val province: com.wadii.domain.model.province.Province = _root_ide_package_.com.wadii.domain.model.province.Province()
) {

}