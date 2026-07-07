package com.wadii.data.repo


import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.city.City
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.province.Province
import com.wadii.domain.repo.CountryRepo
import com.wadii.utils.RequestState

class CountryRepoImpl(private val apiService: ApiService) : CountryRepo {
    override suspend fun getCountryList(response: (RequestState<BaseResponse<List<Country>>>) -> Unit) =
        apiService.getCountryList(response)

    override suspend fun getProvinceByCountryId(
        countryId: Long,
        response: (RequestState<BaseResponse<List<Province>>>) -> Unit
    ) = apiService.getProvinceByCountryId(countryId, response)

    override suspend fun getCitiesByProvinceId(
        provinceId: Long,
        response: (RequestState<BaseResponse<List<City>>>) -> Unit
    ) = apiService.getCitiesByProvinceId(provinceId, response)



}