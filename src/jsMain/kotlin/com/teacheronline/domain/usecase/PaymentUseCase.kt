package com.teacheronline.domain.usecase

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

class PaymentUseCase(private val repo: PaymentRepo) {
    suspend fun createExtra(request: PaymentExtraDto, response: (RequestState<BaseResponse<PaymentExtra>>) -> Unit) = repo.createExtra(request, response)
    suspend fun createOut(request: PaymentOutDto, response: (RequestState<BaseResponse<PaymentOut>>) -> Unit) = repo.createOut(request, response)

    suspend fun createStudentPayment(request: PaymentStudentDto, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) = repo.createStudentPayment(request, response)
    suspend fun updateStudentPayment(request: PaymentStudentDto, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) = repo.updateStudentPayment(request, response)
    suspend fun deleteStudentPayment(id: Long, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) = repo.deleteStudentPayment(id, response)
    suspend fun getStudentPayment(id: Long, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) = repo.getStudentPayment(id, response)
    suspend fun allForStudent(studentId: Long, response: (RequestState<BaseResponse<List<PaymentStudent>>>) -> Unit) = repo.allForStudent(studentId, response)
    suspend fun subjectAccess(studentId: Long, subjectId: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) = repo.subjectAccess(studentId, subjectId, response)
    suspend fun subjectsStatusMap(studentId: Long, response: (RequestState<BaseResponse<Map<String, List<Subject>>>>) -> Unit) = repo.subjectsStatusMap(studentId, response)
    suspend fun subjectsStatusList(studentId: Long, response: (RequestState<BaseResponse<List<PaymentStudentSubjectResponse>>>) -> Unit) = repo.subjectsStatusList(studentId, response)
}
