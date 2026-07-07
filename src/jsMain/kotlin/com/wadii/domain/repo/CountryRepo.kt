package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.city.City
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.province.Province
import com.wadii.utils.RequestState


interface CountryRepo {

    suspend fun getCountryList(response: (RequestState<BaseResponse<List<Country>>>) -> Unit)

    suspend fun getProvinceByCountryId(
        countryId: Long,
        response: (RequestState<BaseResponse<List<Province>>>) -> Unit
    )

    suspend fun getCitiesByProvinceId(
        provinceId: Long,
        response: (RequestState<BaseResponse<List<City>>>) -> Unit
    )

}