package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.city.City
import com.wadii.domain.model.city.InsertCity
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.country.InsertCountry
import com.wadii.domain.model.province.InsertProvince
import com.wadii.domain.model.province.Province
import com.wadii.utils.RequestState


interface CountryRepo {

    suspend fun getCountryList(response: (RequestState<BaseResponse<List<Country>>>) -> Unit)

    suspend fun insertCountry(insertCountry: InsertCountry, response: (RequestState<BaseResponse<Country>>) -> Unit)

    suspend fun updateCountry(insertCountry: InsertCountry, response: (RequestState<BaseResponse<Country>>) -> Unit)

    suspend fun deleteCountry(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)

    suspend fun getProvinceList(response: (RequestState<BaseResponse<List<Province>>>) -> Unit)

    suspend fun getProvinceByCountryId(
        countryId: Long,
        response: (RequestState<BaseResponse<List<Province>>>) -> Unit
    )

    suspend fun insertProvince(insertProvince: InsertProvince, response: (RequestState<BaseResponse<Province>>) -> Unit)

    suspend fun updateProvince(insertProvince: InsertProvince, response: (RequestState<BaseResponse<Province>>) -> Unit)

    suspend fun deleteProvince(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)

    suspend fun getCityList(response: (RequestState<BaseResponse<List<City>>>) -> Unit)

    suspend fun getCitiesByProvinceId(
        provinceId: Long,
        response: (RequestState<BaseResponse<List<City>>>) -> Unit
    )

    suspend fun insertCity(insertCity: InsertCity, response: (RequestState<BaseResponse<City>>) -> Unit)

    suspend fun updateCity(insertCity: InsertCity, response: (RequestState<BaseResponse<City>>) -> Unit)

    suspend fun deleteCity(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)

}
