package com.teacheronline.utils

object Constants {

    const val TOKEN_KEY = "TOKEN_KEY"
    const val DEVELOPMENT_MODE = true
    const val APP_LANGUAGE = "APP_LANGUAGE"
    const val USER_KEY = "USER_KEY"
    const val THEME_KEY = "THEME_KEY"

    // Placeholder — no real backend deployed yet. Swap once the TeacherOnline server is hosted.
    // Doc: base is `http://<host>:8080`, no `/api/v1` prefix in practice.
    const val IP = "172.28.0.61"
    const val BASE_URL = "http://$IP:1010"
    const val WS_URL = "ws://$IP:1010/web-socket"

    //auth (doc §2)
    const val REGISTER = "$BASE_URL/auth/register"
    const val LOGIN = "$BASE_URL/auth/login"
    const val AUTH_SHOW = "$BASE_URL/auth/show"

    //teachers (doc §3 — HTTP status = body.statusCode)
    const val TEACHERS_SHOW_ALL = "$BASE_URL/teachers/show-all"
    fun TEACHERS_SHOW(id: Long) = "$BASE_URL/teachers/show/$id"
    const val TEACHERS_INSERT = "$BASE_URL/teachers/insert"
    const val TEACHERS_UPDATE = "$BASE_URL/teachers/update"
    fun TEACHERS_DELETE(id: Long) = "$BASE_URL/teachers/delete/$id"

    //subjects (doc §4 — HTTP status = body.statusCode)
    const val SUBJECTS_SHOW_ALL = "$BASE_URL/subjects/show-all"
    fun SUBJECTS_SHOW(id: Long) = "$BASE_URL/subjects/show/$id"
    const val SUBJECTS_INSERT = "$BASE_URL/subjects/insert"
    const val SUBJECTS_UPDATE = "$BASE_URL/subjects/update"
    fun SUBJECTS_DELETE(id: Long) = "$BASE_URL/subjects/delete/$id"

    //languages (doc §5 — HTTP status = body.statusCode)
    const val LANGUAGES_SHOW_ALL = "$BASE_URL/languages/show-all"
    fun LANGUAGES_SHOW(id: Long) = "$BASE_URL/languages/show/$id"
    const val LANGUAGES_INSERT = "$BASE_URL/languages/insert"
    const val LANGUAGES_UPDATE = "$BASE_URL/languages/update"
    fun LANGUAGES_DELETE(id: Long) = "$BASE_URL/languages/delete/$id"

    //educational centers (doc §6 — HTTP status = body.statusCode)
    const val CENTERS_SHOW_ALL = "$BASE_URL/educational_centers/show-all"
    fun CENTERS_SHOW(id: Long) = "$BASE_URL/educational_centers/show/$id"
    const val CENTERS_INSERT = "$BASE_URL/educational_centers/insert"
    const val CENTERS_UPDATE = "$BASE_URL/educational_centers/update"
    fun CENTERS_DELETE(id: Long) = "$BASE_URL/educational_centers/delete/$id"

    //students (doc §7)
    const val STUDENTS_CREATE = "$BASE_URL/students/create-student"
    const val STUDENTS_UPDATE = "$BASE_URL/students/update-student"
    const val STUDENTS_ALL = "$BASE_URL/students/all-students"
    fun STUDENTS_BY_ID(id: Long) = "$BASE_URL/students/students/$id"
    fun STUDENTS_BY_EMAIL(email: String) = "$BASE_URL/students/student-by-email/$email"
    fun STUDENTS_BY_PHONE(phone: String) = "$BASE_URL/students/student-by-phone/$phone"
    fun STUDENTS_DELETE(id: Long) = "$BASE_URL/students/delete-student/$id"
    fun STUDENTS_BY_COURSE(courseId: Long) = "$BASE_URL/students/students-by-course/$courseId"

    //parents (PARENTS_API.md)
    const val PARENTS_ENROLL = "$BASE_URL/parents/enroll"
    fun PARENTS_ADD_STUDENTS(parentId: Long) = "$BASE_URL/parents/$parentId/students"
    const val PARENTS_ALL = "$BASE_URL/parents/all-parents"

    //attendance (doc §8)
    const val ATTENDANCE_CREATE = "$BASE_URL/attendance/create-attendance"
    const val ATTENDANCE_ALL = "$BASE_URL/attendance/all-attendance"
    fun ATTENDANCE_MARK_BY_STUDENT(id: Long) = "$BASE_URL/attendance/student-attendance/$id"

    //configs (doc §9)
    fun CONFIG(key: String) = "$BASE_URL/configs/$key"

    //levels (doc §10)
    const val LEVELS_CREATE = "$BASE_URL/levels/create-level"
    const val LEVELS_UPDATE = "$BASE_URL/levels/update-level"
    const val LEVELS_ALL = "$BASE_URL/levels/all-levels"

    //payment extra (doc §11)
    const val PAYMENT_EXTRA_CREATE = "$BASE_URL/payment-extra/create-payment-extra"

    //payment out (doc §12)
    const val PAYMENT_OUT_CREATE = "$BASE_URL/payment-out/create-payment-out"

    //payment student (doc §13)
    const val PAYMENT_STUDENT_CREATE = "$BASE_URL/payment-student/create-payment-student"
    const val PAYMENT_STUDENT_UPDATE = "$BASE_URL/payment-student/update-payment-student"
    fun PAYMENT_STUDENT_DELETE(id: Long) = "$BASE_URL/payment-student/delete-payment-student/$id"
    fun PAYMENT_STUDENT_GET(id: Long) = "$BASE_URL/payment-student/get-payment-student/$id"
    fun PAYMENT_STUDENT_ALL_FOR_STUDENT(studentId: Long) =
        "$BASE_URL/payment-student/get-all-payment-student/$studentId"

    fun PAYMENT_STUDENT_SUBJECT_ACCESS(studentId: Long, subjectId: Long) =
        "$BASE_URL/payment-student/$studentId/subject/$subjectId/access"

    fun PAYMENT_STUDENT_SUBJECTS_STATUS_MAP(studentId: Long) =
        "$BASE_URL/payment-student/$studentId/subjects/status"

    fun PAYMENT_STUDENT_SUBJECTS_STATUS_LIST(studentId: Long) =
        "$BASE_URL/payment-student/subjects/status/$studentId"

    //schedule (doc §14)
    const val SCHEDULE_CREATE = "$BASE_URL/schedule/create-schedule"
    const val SCHEDULE_COUNT = "$BASE_URL/schedule/count"

    //secretary (doc §15)
    const val SECRETARY_CREATE = "$BASE_URL/secretary/create-secretary"
    const val SECRETARY_SHOW_ALL = "$BASE_URL/secretary/show-all"

    //translation (doc §16, top-level)
    const val TRANSLATE = "$BASE_URL/translate"

    //chat (not in API_DOCUMENTATION.md yet — kept for when a /messages API lands server-side;
    //today these calls will 404 against the real backend, only WebSocket call-signaling/presence work)
    const val CHAT_LIST = "$BASE_URL/messages/chat-list"
    const val CHAT_SEND_MESSAGE = "$BASE_URL/messages/insert"
    const val SHOW_ALL_MESSAGES = "$BASE_URL/messages/show-all"
    const val CONVERSATION = "$BASE_URL/messages/conversation"

    //livekit
    const val LIVEKIT_TOKEN = "$BASE_URL/livekit/token"

    //firebase (web config — public, safe to ship client-side; NOT the admin.json service account)
    const val FIREBASE_API_KEY = "AIzaSyChi41C0aEzzjctisYpqpHvzJKN6uD0v8A"
    const val FIREBASE_AUTH_DOMAIN = "wadii-kmp.firebaseapp.com"
    const val FIREBASE_PROJECT_ID = "wadii-kmp"
    const val FIREBASE_STORAGE_BUCKET = "wadii-kmp.firebasestorage.app"
    const val FIREBASE_MESSAGING_SENDER_ID = "1041244088662"
    const val FIREBASE_APP_ID = "1:1041244088662:web:292b831dddb6727ed40ef7"
    const val FIREBASE_VAPID_KEY =
        "BCZXCUTLg2DQq9WT9AL6AZxTGq37_s8c8s8gFQSIIdojIPahkUaeXcBBrhk4252YCa5Rg0yyOS9jBn7MHO4RAHE"
}
