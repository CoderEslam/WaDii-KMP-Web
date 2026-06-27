package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.city.City
import com.wadii.domain.model.country.Country
import com.wadii.domain.model.province.Province
import com.wadii.domain.repo.CountryRepo
import com.wadii.utils.RequestState


class CountryUseCase(private val countryRepo: CountryRepo) {

    suspend fun getCountryList(response: (RequestState<BaseResponse<List<Country>>>) -> Unit) =
        countryRepo.getCountryList(response)

    suspend fun getProvinceByCountryId(
        countryId: Int,
        response: (RequestState<BaseResponse<List<Province>>>) -> Unit
    ) = countryRepo.getProvinceByCountryId(countryId, response)


    suspend fun getCitiesByProvinceId(
        provinceId: Int,
        response: (RequestState<BaseResponse<List<City>>>) -> Unit
    ) = countryRepo.getCitiesByProvinceId(provinceId, response)


}