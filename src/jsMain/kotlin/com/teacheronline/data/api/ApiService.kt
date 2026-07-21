package com.teacheronline.data.api

import com.teacheronline.domain.model.Attendance
import com.teacheronline.domain.model.AttendanceMonth
import com.teacheronline.domain.model.AttendanceRequest
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Config
import com.teacheronline.domain.model.ConfigDto
import com.teacheronline.domain.model.CreateSubjectsTeacher
import com.teacheronline.domain.model.EducationalCenter
import com.teacheronline.domain.model.EducationalCenterDto
import com.teacheronline.domain.model.Language
import com.teacheronline.domain.model.LanguageDto
import com.teacheronline.domain.model.Level
import com.teacheronline.domain.model.LevelDto
import com.teacheronline.domain.model.PaymentExtra
import com.teacheronline.domain.model.PaymentExtraDto
import com.teacheronline.domain.model.PaymentOut
import com.teacheronline.domain.model.PaymentOutDto
import com.teacheronline.domain.model.PaymentStudent
import com.teacheronline.domain.model.PaymentStudentDto
import com.teacheronline.domain.model.PaymentStudentSubjectResponse
import com.teacheronline.domain.model.EnrollRequest
import com.teacheronline.domain.model.Parent
import com.teacheronline.domain.model.Schedule
import com.teacheronline.domain.model.ScheduleDto
import com.teacheronline.domain.model.Secretary
import com.teacheronline.domain.model.SecretaryDto
import com.teacheronline.domain.model.Student
import com.teacheronline.domain.model.StudentRequest
import com.teacheronline.domain.model.Subject
import com.teacheronline.domain.model.SubjectRequest
import com.teacheronline.domain.model.Teacher
import com.teacheronline.domain.model.auth.AuthRequest
import com.teacheronline.domain.model.auth.login.User
import com.teacheronline.domain.model.call.livekit.LiveKitTokenRequest
import com.teacheronline.domain.model.call.livekit.LiveKitTokenResponse
import com.teacheronline.domain.model.chat.ChatContact
import com.teacheronline.domain.model.chat.InsertMessage
import com.teacheronline.domain.model.chat.InsertResponse
import com.teacheronline.domain.model.chat.PageMessages
import com.teacheronline.domain.model.chat.ShowAllMessagesResponse
import com.teacheronline.utils.Constants
import com.teacheronline.utils.RequestState
import com.teacheronline.utils.deleteApiResponse
import com.teacheronline.utils.getApiResponse
import com.teacheronline.utils.getStatusCode
import com.teacheronline.utils.postApiResponse
import com.teacheronline.utils.putApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.put as ktorPut

class ApiService(private val client: HttpClient) {

    //auth (doc §2, public)
    suspend fun login(request: AuthRequest, response: (RequestState<BaseResponse<User>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<AuthRequest, BaseResponse<User>>(urlString = Constants.LOGIN, body = request))
    }

    suspend fun register(request: AuthRequest, response: (RequestState<BaseResponse<User>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<AuthRequest, BaseResponse<User>>(urlString = Constants.REGISTER, body = request))
    }

    suspend fun authShow(id: Long, response: (RequestState<BaseResponse<User>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<Map<String, Long>, BaseResponse<User>>(urlString = Constants.AUTH_SHOW, body = mapOf("id" to id)))
    }

    //teachers (doc §3 — HTTP status = body.statusCode)
    suspend fun teacherShow(id: Long, response: (RequestState<BaseResponse<Teacher>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Teacher>>(urlString = Constants.TEACHERS_SHOW(id)))
    }

    suspend fun teachersShowAll(response: (RequestState<BaseResponse<List<Teacher>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Teacher>>>(urlString = Constants.TEACHERS_SHOW_ALL))
    }

    suspend fun teacherInsert(request: CreateSubjectsTeacher, response: (RequestState<BaseResponse<Teacher>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<CreateSubjectsTeacher, BaseResponse<Teacher>>(urlString = Constants.TEACHERS_INSERT, body = request))
    }

    suspend fun teacherUpdate(request: CreateSubjectsTeacher, response: (RequestState<BaseResponse<Teacher>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<CreateSubjectsTeacher, BaseResponse<Teacher>>(urlString = Constants.TEACHERS_UPDATE, body = request))
    }

    suspend fun teacherDelete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.TEACHERS_DELETE(id)))
    }

    //subjects (doc §4 — HTTP status = body.statusCode)
    suspend fun subjectShow(id: Long, response: (RequestState<BaseResponse<Subject>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Subject>>(urlString = Constants.SUBJECTS_SHOW(id)))
    }

    suspend fun subjectsShowAll(response: (RequestState<BaseResponse<List<Subject>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Subject>>>(urlString = Constants.SUBJECTS_SHOW_ALL))
    }

    suspend fun subjectInsert(request: SubjectRequest, response: (RequestState<BaseResponse<Subject>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<SubjectRequest, BaseResponse<Subject>>(urlString = Constants.SUBJECTS_INSERT, body = request))
    }

    suspend fun subjectUpdate(request: SubjectRequest, response: (RequestState<BaseResponse<Subject>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<SubjectRequest, BaseResponse<Subject>>(urlString = Constants.SUBJECTS_UPDATE, body = request))
    }

    suspend fun subjectDelete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.SUBJECTS_DELETE(id)))
    }

    //languages (doc §5 — HTTP status = body.statusCode)
    suspend fun languageShow(id: Long, response: (RequestState<BaseResponse<Language>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Language>>(urlString = Constants.LANGUAGES_SHOW(id)))
    }

    suspend fun languagesShowAll(response: (RequestState<BaseResponse<List<Language>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Language>>>(urlString = Constants.LANGUAGES_SHOW_ALL))
    }

    suspend fun languageInsert(request: LanguageDto, response: (RequestState<BaseResponse<Language>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<LanguageDto, BaseResponse<Language>>(urlString = Constants.LANGUAGES_INSERT, body = request))
    }

    suspend fun languageUpdate(request: LanguageDto, response: (RequestState<BaseResponse<Language>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<LanguageDto, BaseResponse<Language>>(urlString = Constants.LANGUAGES_UPDATE, body = request))
    }

    suspend fun languageDelete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.LANGUAGES_DELETE(id)))
    }

    //educational centers (doc §6 — HTTP status = body.statusCode)
    suspend fun centerShow(id: Long, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<EducationalCenter>>(urlString = Constants.CENTERS_SHOW(id)))
    }

    suspend fun centersShowAll(response: (RequestState<BaseResponse<List<EducationalCenter>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<EducationalCenter>>>(urlString = Constants.CENTERS_SHOW_ALL))
    }

    suspend fun centerInsert(request: EducationalCenterDto, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<EducationalCenterDto, BaseResponse<EducationalCenter>>(urlString = Constants.CENTERS_INSERT, body = request))
    }

    suspend fun centerUpdate(request: EducationalCenterDto, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<EducationalCenterDto, BaseResponse<EducationalCenter>>(urlString = Constants.CENTERS_UPDATE, body = request))
    }

    suspend fun centerDelete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Boolean>>(urlString = Constants.CENTERS_DELETE(id)))
    }

    //students (doc §7)
    suspend fun studentCreate(request: StudentRequest, response: (RequestState<BaseResponse<Student>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<StudentRequest, BaseResponse<Student>>(urlString = Constants.STUDENTS_CREATE, body = request))
    }

    suspend fun studentUpdate(request: StudentRequest, response: (RequestState<BaseResponse<Student>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<StudentRequest, BaseResponse<Student>>(urlString = Constants.STUDENTS_UPDATE, body = request))
    }

    suspend fun studentsAll(response: (RequestState<BaseResponse<List<Student>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Student>>>(urlString = Constants.STUDENTS_ALL))
    }

    suspend fun studentById(id: Long, response: (RequestState<BaseResponse<Student>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Student>>(urlString = Constants.STUDENTS_BY_ID(id)))
    }

    suspend fun studentByEmail(email: String, response: (RequestState<BaseResponse<Student>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Student>>(urlString = Constants.STUDENTS_BY_EMAIL(email)))
    }

    suspend fun studentByPhone(phone: String, response: (RequestState<BaseResponse<Student>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Student>>(urlString = Constants.STUDENTS_BY_PHONE(phone)))
    }

    suspend fun studentDelete(id: Long, response: (RequestState<BaseResponse<Student>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<Student>>(urlString = Constants.STUDENTS_DELETE(id)))
    }

    suspend fun studentsByCourse(courseId: Long, response: (RequestState<BaseResponse<List<Student>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Student>>>(urlString = Constants.STUDENTS_BY_COURSE(courseId)))
    }

    //parents (PARENTS_API.md)
    suspend fun parentEnroll(request: EnrollRequest, response: (RequestState<BaseResponse<Parent>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<EnrollRequest, BaseResponse<Parent>>(urlString = Constants.PARENTS_ENROLL, body = request))
    }

    suspend fun parentAddStudents(parentId: Long, students: List<StudentRequest>, response: (RequestState<BaseResponse<Parent>>) -> Unit) {
        response(RequestState.Loading)
        response(
            client.postApiResponse<List<StudentRequest>, BaseResponse<Parent>>(
                urlString = Constants.PARENTS_ADD_STUDENTS(parentId),
                body = students
            )
        )
    }

    suspend fun parentsAll(response: (RequestState<BaseResponse<List<Parent>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Parent>>>(urlString = Constants.PARENTS_ALL))
    }

    //attendance (doc §8)
    suspend fun attendanceCreate(request: AttendanceRequest, response: (RequestState<BaseResponse<Attendance>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<AttendanceRequest, BaseResponse<Attendance>>(urlString = Constants.ATTENDANCE_CREATE, body = request))
    }

    suspend fun attendanceAll(response: (RequestState<BaseResponse<List<Attendance>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Attendance>>>(urlString = Constants.ATTENDANCE_ALL))
    }

    suspend fun attendanceMarkByStudent(studentId: Long, response: (RequestState<BaseResponse<Attendance>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<BaseResponse<Attendance>>(urlString = Constants.ATTENDANCE_MARK_BY_STUDENT(studentId)))
    }

    //configs (doc §9 — HTTP status always 200, check body.statusCode)
    suspend fun configGet(key: String, response: (RequestState<BaseResponse<ConfigDto>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<ConfigDto>>(urlString = Constants.CONFIG(key)))
    }

    suspend fun configPut(key: String, value: String, response: (RequestState<BaseResponse<Config>>) -> Unit) {
        response(RequestState.Loading)
        response(
            try {
                val res = client.ktorPut(Constants.CONFIG(key)) { setBody(value) }
                if (res.status.value == 200) {
                    RequestState.Success(res.body<BaseResponse<Config>>())
                } else {
                    RequestState.Error(res.bodyAsText(), res.getStatusCode())
                }
            } catch (e: Exception) {
                RequestState.Error("Network error: ${e.message}")
            }
        )
    }

    //levels (doc §10)
    suspend fun levelCreate(request: LevelDto, response: (RequestState<BaseResponse<Level>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<LevelDto, BaseResponse<Level>>(urlString = Constants.LEVELS_CREATE, body = request))
    }

    suspend fun levelUpdate(request: LevelDto, response: (RequestState<BaseResponse<Level>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<LevelDto, BaseResponse<Level>>(urlString = Constants.LEVELS_UPDATE, body = request))
    }

    suspend fun levelsAll(response: (RequestState<BaseResponse<List<Level>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Level>>>(urlString = Constants.LEVELS_ALL))
    }

    //payment extra (doc §11)
    suspend fun paymentExtraCreate(request: PaymentExtraDto, response: (RequestState<BaseResponse<PaymentExtra>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<PaymentExtraDto, BaseResponse<PaymentExtra>>(urlString = Constants.PAYMENT_EXTRA_CREATE, body = request))
    }

    //payment out (doc §12)
    suspend fun paymentOutCreate(request: PaymentOutDto, response: (RequestState<BaseResponse<PaymentOut>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<PaymentOutDto, BaseResponse<PaymentOut>>(urlString = Constants.PAYMENT_OUT_CREATE, body = request))
    }

    //payment student (doc §13)
    suspend fun paymentStudentCreate(request: PaymentStudentDto, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<PaymentStudentDto, BaseResponse<PaymentStudent>>(urlString = Constants.PAYMENT_STUDENT_CREATE, body = request))
    }

    suspend fun paymentStudentUpdate(request: PaymentStudentDto, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) {
        response(RequestState.Loading)
        response(client.putApiResponse<PaymentStudentDto, BaseResponse<PaymentStudent>>(urlString = Constants.PAYMENT_STUDENT_UPDATE, body = request))
    }

    suspend fun paymentStudentDelete(id: Long, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) {
        response(RequestState.Loading)
        response(client.deleteApiResponse<BaseResponse<PaymentStudent>>(urlString = Constants.PAYMENT_STUDENT_DELETE(id)))
    }

    suspend fun paymentStudentGet(id: Long, response: (RequestState<BaseResponse<PaymentStudent>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<PaymentStudent>>(urlString = Constants.PAYMENT_STUDENT_GET(id)))
    }

    suspend fun paymentStudentAllForStudent(studentId: Long, response: (RequestState<BaseResponse<List<PaymentStudent>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<PaymentStudent>>>(urlString = Constants.PAYMENT_STUDENT_ALL_FOR_STUDENT(studentId)))
    }

    suspend fun paymentStudentSubjectAccess(studentId: Long, subjectId: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Boolean>>(urlString = Constants.PAYMENT_STUDENT_SUBJECT_ACCESS(studentId, subjectId)))
    }

    suspend fun paymentStudentSubjectsStatusMap(studentId: Long, response: (RequestState<BaseResponse<Map<String, List<Subject>>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Map<String, List<Subject>>>>(urlString = Constants.PAYMENT_STUDENT_SUBJECTS_STATUS_MAP(studentId)))
    }

    suspend fun paymentStudentSubjectsStatusList(studentId: Long, response: (RequestState<BaseResponse<List<PaymentStudentSubjectResponse>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<PaymentStudentSubjectResponse>>>(urlString = Constants.PAYMENT_STUDENT_SUBJECTS_STATUS_LIST(studentId)))
    }

    //schedule (doc §14)
    suspend fun scheduleCreate(request: ScheduleDto, response: (RequestState<BaseResponse<Schedule>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<ScheduleDto, BaseResponse<Schedule>>(urlString = Constants.SCHEDULE_CREATE, body = request))
    }

    suspend fun scheduleCount(request: AttendanceMonth, response: (RequestState<BaseResponse<Long>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<Long>>(urlString = Constants.SCHEDULE_COUNT) { setBody(request) })
    }

    //secretary (doc §15)
    suspend fun secretaryCreate(request: SecretaryDto, response: (RequestState<BaseResponse<Secretary>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<SecretaryDto, BaseResponse<Secretary>>(urlString = Constants.SECRETARY_CREATE, body = request))
    }

    suspend fun secretaryShowAll(response: (RequestState<BaseResponse<List<Secretary>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<Secretary>>>(urlString = Constants.SECRETARY_SHOW_ALL))
    }

    //translation (doc §16 — plain string response, NOT wrapped in ApiResponse)
    suspend fun translate(text: String, targetLanguage: String): String {
        return try {
            client.get(Constants.TRANSLATE) {
                parameter("text", text)
                parameter("targetLanguage", targetLanguage)
            }.bodyAsText()
        } catch (e: Exception) {
            "Translation failed: ${e.message}"
        }
    }

    //livekit
    suspend fun getLiveKitToken(request: LiveKitTokenRequest, response: (RequestState<BaseResponse<LiveKitTokenResponse>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<LiveKitTokenRequest, BaseResponse<LiveKitTokenResponse>>(urlString = Constants.LIVEKIT_TOKEN, body = request))
    }

    //chat — not documented server-side yet (see Constants.CHAT_*); wired for when it lands.
    suspend fun chatList(response: (RequestState<BaseResponse<List<ChatContact>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<ChatContact>>>(urlString = Constants.CHAT_LIST))
    }

    suspend fun insertMessage(insertMessage: InsertMessage, response: (RequestState<BaseResponse<InsertResponse>>) -> Unit) {
        response(RequestState.Loading)
        response(client.postApiResponse<InsertMessage, BaseResponse<InsertResponse>>(urlString = Constants.CHAT_SEND_MESSAGE, body = insertMessage))
    }

    suspend fun showAllMessages(response: (RequestState<BaseResponse<List<ShowAllMessagesResponse>>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<List<ShowAllMessagesResponse>>>(urlString = Constants.SHOW_ALL_MESSAGES))
    }

    suspend fun conversation(userId: Long, page: Int = 0, response: (RequestState<BaseResponse<PageMessages>>) -> Unit) {
        response(RequestState.Loading)
        response(client.getApiResponse<BaseResponse<PageMessages>>(urlString = "${Constants.CONVERSATION}/$userId") {
            parameter("page", page)
        })
    }
}
