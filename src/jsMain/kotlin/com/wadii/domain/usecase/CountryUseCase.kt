package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.city.City
import com.wadii.domain.model.city.InsertCity
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.country.InsertCountry
import com.wadii.domain.model.province.InsertProvince
import com.wadii.domain.model.province.Province
import com.wadii.domain.repo.CountryRepo
import com.wadii.utils.RequestState


class CountryUseCase(private val countryRepo: CountryRepo) {

    suspend fun getCountryList(response: (RequestState<BaseResponse<List<Country>>>) -> Unit) =
        countryRepo.getCountryList(response)

    suspend fun insertCountry(
        insertCountry: InsertCountry,
        response: (RequestState<BaseResponse<Country>>) -> Unit
    ) = countryRepo.insertCountry(insertCountry, response)

    suspend fun updateCountry(
        insertCountry: InsertCountry,
        response: (RequestState<BaseResponse<Country>>) -> Unit
    ) = countryRepo.updateCountry(insertCountry, response)

    suspend fun deleteCountry(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = countryRepo.deleteCountry(id, response)

    suspend fun getProvinceList(response: (RequestState<BaseResponse<List<Province>>>) -> Unit) =
        countryRepo.getProvinceList(response)

    suspend fun getProvinceByCountryId(
        countryId: Long,
        response: (RequestState<BaseResponse<List<Province>>>) -> Unit
    ) = countryRepo.getProvinceByCountryId(countryId, response)

    suspend fun insertProvince(
        insertProvince: InsertProvince,
        response: (RequestState<BaseResponse<Province>>) -> Unit
    ) = countryRepo.insertProvince(insertProvince, response)

    suspend fun updateProvince(
        insertProvince: InsertProvince,
        response: (RequestState<BaseResponse<Province>>) -> Unit
    ) = countryRepo.updateProvince(insertProvince, response)

    suspend fun deleteProvince(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = countryRepo.deleteProvince(id, response)

    suspend fun getCityList(response: (RequestState<BaseResponse<List<City>>>) -> Unit) =
        countryRepo.getCityList(response)

    suspend fun getCitiesByProvinceId(
        provinceId: Long,
        response: (RequestState<BaseResponse<List<City>>>) -> Unit
    ) = countryRepo.getCitiesByProvinceId(provinceId, response)

    suspend fun insertCity(
        insertCity: InsertCity,
        response: (RequestState<BaseResponse<City>>) -> Unit
    ) = countryRepo.insertCity(insertCity, response)

    suspend fun updateCity(
        insertCity: InsertCity,
        response: (RequestState<BaseResponse<City>>) -> Unit
    ) = countryRepo.updateCity(insertCity, response)

    suspend fun deleteCity(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = countryRepo.deleteCity(id, response)

}
