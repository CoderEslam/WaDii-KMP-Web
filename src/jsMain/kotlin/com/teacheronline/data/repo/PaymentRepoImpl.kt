package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.PaymentExtra
import com.teacheronline.domain.model.PaymentExtraDto
import com.teacheronline.domain.model.PaymentOut
import com.teacheronline.domain.model.PaymentOutDto
import com.teacheronline.domain.model.PaymentStudent
import com.teacheronline.domain.model.PaymentStudentDto
import com.teacheronline.domain.model.PaymentStudentSubjectResponse
import com.teacheronline.domain.model.Subject
import com.teacheronline.domain.repo.PaymentRepo
import com.teacheronline.utils.RequestState

class PaymentRepoImpl(private val apiService: ApiService) : PaymentRepo {
    override suspend fun createExtra(request: PaymentExtraDto, response: (RequestState<BaseResponse<PaymentExtra>>) -> Unit) =
        apiService.paymentExtraCreate(request, response)

    override suspend fun createOut(request: PaymentOutDto, response: (RequestState<BaseResponse<PaymentOut>>) -> Unit) =
        apiService.paymentOutCreate(request, response)

    override suspend fun createStudentPayment(request: PaymentStudentDto, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) =
        apiService.paymentStudentCreate(request, response)

    override suspend fun updateStudentPayment(request: PaymentStudentDto, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) =
        apiService.paymentStudentUpdate(request, response)

    override suspend fun deleteStudentPayment(id: Long, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) =
        apiService.paymentStudentDelete(id, response)

    override suspend fun getStudentPayment(id: Long, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) =
        apiService.paymentStudentGet(id, response)

    override suspend fun allForStudent(studentId: Long, response: (RequestState<BaseResponse<List<PaymentStudent>>>) -> Unit) =
        apiService.paymentStudentAllForStudent(studentId, response)

    override suspend fun subjectAccess(studentId: Long, subjectId: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.paymentStudentSubjectAccess(studentId, subjectId, response)

    override suspend fun subjectsStatusMap(studentId: Long, response: (RequestState<BaseResponse<Map<String, List<Subject>>>>) -> Unit) =
        apiService.paymentStudentSubjectsStatusMap(studentId, response)

    override suspend fun subjectsStatusList(studentId: Long, response: (RequestState<BaseResponse<List<PaymentStudentSubjectResponse>>>) -> Unit) =
        apiService.paymentStudentSubjectsStatusList(studentId, response)
}
