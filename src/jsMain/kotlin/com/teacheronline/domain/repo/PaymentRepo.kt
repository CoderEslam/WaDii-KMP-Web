package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.PaymentExtra
import com.teacheronline.domain.model.PaymentExtraDto
import com.teacheronline.domain.model.PaymentOut
import com.teacheronline.domain.model.PaymentOutDto
import com.teacheronline.domain.model.PaymentStudent
import com.teacheronline.domain.model.PaymentStudentDto
import com.teacheronline.domain.model.PaymentStudentSubjectResponse
import com.teacheronline.domain.model.Subject
import com.teacheronline.utils.RequestState

interface PaymentRepo {
    suspend fun createExtra(request: PaymentExtraDto, response: (RequestState<BaseResponse<PaymentExtra>>) -> Unit)
    suspend fun createOut(request: PaymentOutDto, response: (RequestState<BaseResponse<PaymentOut>>) -> Unit)

    suspend fun createStudentPayment(request: PaymentStudentDto, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit)
    suspend fun updateStudentPayment(request: PaymentStudentDto, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit)
    suspend fun deleteStudentPayment(id: Long, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit)
    suspend fun getStudentPayment(id: Long, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit)
    suspend fun allForStudent(studentId: Long, response: (RequestState<BaseResponse<List<PaymentStudent>>>) -> Unit)
    suspend fun subjectAccess(studentId: Long, subjectId: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
    suspend fun subjectsStatusMap(studentId: Long, response: (RequestState<BaseResponse<Map<String, List<Subject>>>>) -> Unit)
    suspend fun subjectsStatusList(studentId: Long, response: (RequestState<BaseResponse<List<PaymentStudentSubjectResponse>>>) -> Unit)
}
