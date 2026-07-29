package com.wadii.data.repo


import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.city.City
import com.wadii.domain.model.city.InsertCity
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.country.InsertCountry
import com.wadii.domain.model.province.InsertProvince
import com.wadii.domain.model.province.Province
import com.wadii.domain.repo.CountryRepo
import com.wadii.utils.RequestState

class CountryRepoImpl(private val apiService: ApiService) : CountryRepo {
    override suspend fun getCountryList(response: (RequestState<BaseResponse<List<Country>>>) -> Unit) =
        apiService.getCountryList(response)

    override suspend fun insertCountry(
        insertCountry: InsertCountry,
        response: (RequestState<BaseResponse<Country>>) -> Unit
    ) = apiService.insertCountry(insertCountry, response)

    override suspend fun updateCountry(
        insertCountry: InsertCountry,
        response: (RequestState<BaseResponse<Country>>) -> Unit
    ) = apiService.updateCountry(insertCountry, response)

    override suspend fun deleteCountry(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = apiService.deleteCountry(id, response)

    override suspend fun getProvinceList(response: (RequestState<BaseResponse<List<Province>>>) -> Unit) =
        apiService.getProvinceList(response)

    override suspend fun getProvinceByCountryId(
        countryId: Long,
        response: (RequestState<BaseResponse<List<Province>>>) -> Unit
    ) = apiService.getProvinceByCountryId(countryId, response)

    override suspend fun insertProvince(
        insertProvince: InsertProvince,
        response: (RequestState<BaseResponse<Province>>) -> Unit
    ) = apiService.insertProvince(insertProvince, response)

    override suspend fun updateProvince(
        insertProvince: InsertProvince,
        response: (RequestState<BaseResponse<Province>>) -> Unit
    ) = apiService.updateProvince(insertProvince, response)

    override suspend fun deleteProvince(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = apiService.deleteProvince(id, response)

    override suspend fun getCityList(response: (RequestState<BaseResponse<List<City>>>) -> Unit) =
        apiService.getCityList(response)

    override suspend fun getCitiesByProvinceId(
        provinceId: Long,
        response: (RequestState<BaseResponse<List<City>>>) -> Unit
    ) = apiService.getCitiesByProvinceId(provinceId, response)

    override suspend fun insertCity(
        insertCity: InsertCity,
        response: (RequestState<BaseResponse<City>>) -> Unit
    ) = apiService.insertCity(insertCity, response)

    override suspend fun updateCity(
        insertCity: InsertCity,
        response: (RequestState<BaseResponse<City>>) -> Unit
    ) = apiService.updateCity(insertCity, response)

    override suspend fun deleteCity(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = apiService.deleteCity(id, response)

}
