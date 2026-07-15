# TeacherOnline API Reference

For the Kotlin client app. Covers every REST endpoint currently implemented, plus the one WebSocket endpoint.

## 1. Basics

**Base URL**: `http://<host>:8080` — there is no `/api/v1` prefix in practice. (`application.properties` defines `api.prefix=/api/v1` but it's never wired into any controller, so ignore it.)

**Auth header**: `Authorization: Bearer <jwt>` on every request except the public paths listed below. The JWT is returned as `token` in the `User` object from `/auth/register` and `/auth/login`.

**Public (no token needed)**: `/auth/**`, `/web-socket/**`, `/ws/**`, `/ws-native/**`. (`countries/**`, `provinces/**`, `cities/**`, `/users/{filename}` are also whitelisted in security config but have no controller behind them yet — calling them 404s.)

**⚠️ Most important gotcha**: only 4 controllers (Teachers, Subjects, Languages, Educational Centers) set the real HTTP status code to match the result. Every other controller in this API always returns **HTTP 200**, even for a "not found" or "error" result — the actual outcome is only in the `statusCode` field of the JSON body. **Always branch on `body.statusCode`, not the HTTP status**, except for the 4 controllers explicitly marked "HTTP status = body.statusCode" below.

### Response envelope

Every JSON endpoint (except `/translate`, which returns a raw string) returns this shape:

```kotlin
data class ApiResponse<T>(
    val data: T?,
    val message: String,
    val statusCode: Int,
    val timestamp: String   // ISO-8601 LocalDateTime, e.g. "2026-07-15T10:30:00"
)
```

Status codes used in `statusCode` (and, on the 4 controllers noted above, as the real HTTP status too): `200` success, `400` bad request, `401` unauthorized (missing/invalid/expired token), `403` forbidden (valid token, wrong role), `404` not found, `409` conflict, `500` server error.

### Roles & enums

```kotlin
enum class Role { ADMIN, TEACHER, STUDENT, PARENTS, SECRETARY }
enum class AttendanceStatus { PRESENT, ABSENT, LATE }
```

---

## 2. Auth — `/auth` (public, no token required)

### `POST /auth/register`
Request:
```kotlin
data class AuthRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val fcmToken: String,
    val password: String,
    val role: Int   // accepted but currently IGNORED server-side — every new user is created as ADMIN regardless of this value
)
```
Response: `ApiResponse<User>` — `400` if the email is already registered.

### `POST /auth/login`
Request:
```kotlin
data class AuthRequest(
    val firstName: String? = null,  // unused for login
    val lastName: String? = null,   // unused for login
    val email: String,
    val fcmToken: String,
    val password: String,
    val role: Int = 0               // unused for login
)
```
Response: `ApiResponse<User>` — `404` unknown email, `400` wrong password. On success, `data.token` is the JWT to use for all subsequent requests.

### `POST /auth/show`
Request: `{"id": 1}` (raw `Map<String, Long>`, key must be `"id"`)
Response: `ApiResponse<User>` — `404` if no such user id.

### `User` shape (returned by all of the above, and nested wherever a Teacher/Student/Secretary is returned)
```kotlin
data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,   // ⚠️ bcrypt hash is included in every response — do not display, but be aware it's present
    val fcmToken: String,
    val token: String?,     // JWT; only set after register/login
    val role: Role,
    val educationalCenters: List<EducationalCenter>  // each with its own `users` field omitted to avoid a cycle
)
```

---

## 3. Teachers — `/teachers` — **HTTP status = body.statusCode**

All 4 controllers in this section share the same shape (`common.Controller<Entity, DTO, ID>`):

| Method | Path | Auth | Notes |
|---|---|---|---|
| GET | `/teachers/show/{id}` | any valid token | |
| GET | `/teachers/show-all` | any valid token | |
| POST | `/teachers/insert` | **ADMIN only** | |
| POST | `/teachers/update` | **ADMIN only** | |
| DELETE | `/teachers/delete/{id}` | **ADMIN only** | |

Request body for insert/update:
```kotlin
data class CreateSubjectsTeacher(
    val id: Long? = null,          // required for update, ignored for insert
    val firstName: String,
    val lastName: String,
    val email: String? = null,     // required for insert
    val password: String? = null,  // required for insert
    val fcmToken: String? = null,
    val phone: String,
    val subjectIds: Set<Long>
)
```

Response body: `ApiResponse<Teacher>` for show/insert/update, `ApiResponse<Boolean>` for delete (`true` on success), `ApiResponse<List<Teacher>>` for show-all.

```kotlin
data class Teacher(
    val id: Long,
    val phone: String,
    val user: User,
    val subjects: List<Subject>
    // `schedules` exists on the entity but is excluded from JSON entirely (server-side @JsonIgnore)
)
```

---

## 4. Subjects — `/subjects` — **HTTP status = body.statusCode**

Same 5 endpoints as Teachers (`/subjects/show/{id}`, `/subjects/show-all`, `/subjects/insert`, `/subjects/update`, `/subjects/delete/{id}`), same ADMIN-only rule on insert/update/delete.

Request body:
```kotlin
data class SubjectRequest(
    val id: Long? = null,   // required for update
    val name: String,
    val price: Double
)
```

Response: `ApiResponse<Subject>` (or `ApiResponse<Boolean>` for delete, `ApiResponse<List<Subject>>` for show-all).
```kotlin
data class Subject(
    val id: Long,
    val name: String,
    val price: Double,
    val teachers: List<Teacher>,   // each teacher's own `subjects` list omitted
    val students: List<Student>    // each student's own `subjects` list omitted
    // `schedules` excluded from JSON entirely
)
```

---

## 5. Languages — `/languages` — **HTTP status = body.statusCode**

Same 5 endpoints (`/languages/show/{id}`, `/languages/show-all`, `/languages/insert`, `/languages/update`, `/languages/delete/{id}`), ADMIN-only on writes.

Request body:
```kotlin
data class LanguageDto(
    val id: Long? = null,   // required for update
    val name: String,
    val prefix: String
)
```

Response: `ApiResponse<Language>` / `ApiResponse<Boolean>` (delete) / `ApiResponse<List<Language>>` (show-all).
```kotlin
data class Language(val id: Long, val name: String, val prefix: String)
```

---

## 6. Educational Centers — `/educational_centers` — **HTTP status = body.statusCode**

Same 5 endpoints, ADMIN-only on writes.

Request body:
```kotlin
data class EducationalCenterDto(
    val id: Long? = null,   // required for update
    val name: String,
    val address: String,
    val contactInfo: String
)
```

Response: `ApiResponse<EducationalCenter>` / `ApiResponse<Boolean>` (delete) / `ApiResponse<List<EducationalCenter>>` (show-all). Note: this DTO doesn't manage the `users`/`students` relations — those aren't editable through these endpoints yet.
```kotlin
data class EducationalCenter(
    val id: Long,
    val name: String,
    val address: String,
    val contactInfo: String,
    val users: List<User>,       // each user's own `educationalCenters` omitted
    val students: List<Student>
)
```

---

## 7. Students — `/students` — HTTP status is always 200, check `body.statusCode`

| Method | Path | Auth |
|---|---|---|
| POST | `/students/create-student` | **ADMIN only** |
| POST | `/students/update-student` | any valid token (self-service — see note) |
| GET | `/students/all-students` | any valid token |
| GET | `/students/students/{id}` | any valid token |
| GET | `/students/student-by-email/{email}` | any valid token |
| GET | `/students/student-by-phone/{phone}` | any valid token |
| DELETE | `/students/delete-student/{id}` | any valid token (no role check) |
| GET | `/students/students-by-course/{courseId}` | any valid token |

**Note on `update-student`**: it is not "update any student by id" — it updates (or creates, if none exists yet) the student profile belonging to whoever's token you send. There's no `studentId`/target id in the request; the server resolves it from your own JWT.

Request body for create/update:
```kotlin
data class StudentRequest(
    val parentId: Long,
    val parentContact: String,
    val phone: String,
    val subjectIds: Set<Long>,
    val scheduleIds: Set<Long>,
    val levelId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val fcmToken: String
)
```

Response: `ApiResponse<Student>` for create/update/get-by-id/-email/-phone/delete, `ApiResponse<List<Student>>` for all-students / students-by-course.
```kotlin
data class Student(
    val id: Long,
    val parentId: Long,
    val parentContact: String,
    val phone: String,
    val user: User,
    val subjects: List<Subject>,
    val level: Level,                          // level.students omitted
    val schedules: List<Schedule>,
    val educationalCenters: List<EducationalCenter>,
    val exams: List<Exam>
)
```

---

## 8. Attendance — `/attendance` — HTTP status is always 200, check `body.statusCode`

| Method | Path | Auth |
|---|---|---|
| POST | `/attendance/create-attendance` | any valid token (no role check) |
| GET | `/attendance/all-attendance` | any valid token |
| POST | `/attendance/student-attendance/{id}` | any valid token — `{id}` is a **student id**, marks attendance via the QR-code flow |

Request body for create-attendance:
```kotlin
data class AttendanceRequest(
    val status: AttendanceStatus,
    val studentId: Long,
    val subjectId: Long,
    val teacherId: Long
)
```

`student-attendance/{id}` has no body — it looks up the student's schedule for "now" and marks them present if within the configured time window (`attendance_time_range` config, default 15 min) of a matching class slot.

Response: `ApiResponse<Attendance>` (create, mark-by-qr) or `ApiResponse<List<Attendance>>` (all-attendance).
```kotlin
data class Attendance(
    val id: Long,
    val student: Student,
    val date: String,          // ISO-8601 LocalDateTime
    val status: AttendanceStatus,
    val subject: Subject,
    val teacher: Teacher
)
```

---

## 9. Config — `/configs` — HTTP status is always 200 (uses `ResponseEntity.ok(...)` unconditionally, ignores the inner statusCode), check `body.statusCode`

| Method | Path | Auth |
|---|---|---|
| GET | `/configs/{key}` | any valid token |
| PUT | `/configs/{key}` | any valid token — body is a **raw string**, not JSON |

**⚠️ GET and PUT return different shapes for the same resource:**

GET response — `ApiResponse<ConfigDto>`:
```kotlin
data class ConfigDto(val key: String, val value: Int)
```
If the key doesn't exist yet, this returns `200`/`data.value = 15` (a hardcoded default) rather than a `404`.

PUT request body: the raw new value as a plain string (e.g. `"20"`), not `{"value": 20}`.
PUT response — `ApiResponse<Config>`:
```kotlin
data class Config(val key: String, val value: String)   // value is a String here, not Int
```

---

## 10. Levels — `/levels` — HTTP status is always 200, check `body.statusCode`

| Method | Path | Auth |
|---|---|---|
| POST | `/levels/create-level` | any valid token (no role check) |
| POST | `/levels/update-level` | any valid token (no role check) |
| GET | `/levels/all-levels` | any valid token |

Request body:
```kotlin
data class LevelDto(val id: Long? = null, val name: String)   // id required for update
```

Response: `ApiResponse<Level>` (create/update), `ApiResponse<List<Level>>` (all-levels).
```kotlin
data class Level(val id: Long, val name: String, val students: List<Student>)
```

---

## 11. Payment Extra — `/payment-extra` — HTTP status is always 200, check `body.statusCode`

Only one endpoint exists — no list/get/update/delete yet.

### `POST /payment-extra/create-payment-extra`
Auth: any valid token (no role check).
Request:
```kotlin
data class PaymentExtraDto(val price: Double, val notes: String?)
```
Response: `ApiResponse<PaymentExtra>`
```kotlin
data class PaymentExtra(val id: Long, val dateTime: String, val price: Double, val notes: String?)
```

---

## 12. Payment Out — `/payment-out` — HTTP status is always 200, check `body.statusCode`

Only one endpoint exists — no list/get/update/delete yet.

### `POST /payment-out/create-payment-out`
Auth: any valid token (no role check).
Request:
```kotlin
data class PaymentOutDto(val price: Double, val notes: String?)
```
Response: `ApiResponse<PaymentOut>`
```kotlin
data class PaymentOut(val id: Long, val dateTime: String, val price: Double, val notes: String?)
```

---

## 13. Payment Student — `/payment-student` — HTTP status is always 200, check `body.statusCode`

| Method | Path | Auth |
|---|---|---|
| POST | `/payment-student/create-payment-student` | any valid token |
| PUT | `/payment-student/update-payment-student` | any valid token |
| DELETE | `/payment-student/delete-payment-student/{id}` | any valid token |
| GET | `/payment-student/get-payment-student/{id}` | any valid token |
| GET | `/payment-student/get-all-payment-student/{studentId}` | any valid token |
| GET | `/payment-student/{studentId}/subject/{subjectId}/access` | any valid token |
| GET | `/payment-student/{studentId}/subjects/status` | any valid token |
| GET | `/payment-student/subjects/status/{studentId}` | any valid token |

Request body for create/update:
```kotlin
data class PaymentStudentDto(
    val id: Long? = null,   // required for update
    val price: Double,
    val notes: String?,
    val studentId: Long,
    val teacherId: Long,
    val subjectId: Long
)
```

Response shapes:
- create / update / get / delete: `ApiResponse<PaymentStudent>`
- get-all-payment-student: `ApiResponse<List<PaymentStudent>>`
- `.../access`: `ApiResponse<Boolean>` — whether this student currently has an active (non-expired) subscription for that subject
- `.../subjects/status` (note: different path shape than the next one, same-ish purpose): `ApiResponse<Map<String, List<Subject>>>`
- `.../subjects/status/{studentId}`: `ApiResponse<List<PaymentStudentSubjectResponse>>`

```kotlin
data class PaymentStudent(
    val id: Long,
    val startDate: String,
    val endDate: String,
    val price: Double,
    val notes: String?,
    val student: Student,
    val teacher: Teacher,
    val subject: Subject,
    val accessValid: Boolean   // computed at request time: startDate < now < endDate
)

data class PaymentStudentSubjectResponse(
    val id: Long,
    val startDate: String,
    val endDate: String,
    val price: Double,
    val notes: String?,
    val subjectName: String,
    val accessValid: Boolean   // also computed at request time, same rule as above
)
```

---

## 14. Schedule — `/schedule` — HTTP status is always 200, check `body.statusCode`

| Method | Path | Auth |
|---|---|---|
| POST | `/schedule/create-schedule` | any valid token |
| GET | `/schedule/count` | any valid token — ⚠️ this is a GET with a **JSON request body** |

Request for create-schedule:
```kotlin
data class ScheduleDto(
    val dayOfWeek: String,       // e.g. "Monday"
    val timeSlotStart: String,   // "HH:mm:ss", e.g. "09:00:00"
    val timeSlotEnd: String,
    val teacherId: Long,
    val subjectId: Long
)
```
`404` in the body if `teacherId` or `subjectId` doesn't exist.

Request for `/schedule/count` (sent as a GET body):
```kotlin
data class AttendanceMonth(val subjectId: Long, val teacherId: Long, val startMonth: Int, val endMonth: Int)
```

Response: `ApiResponse<Schedule>` (create) or `ApiResponse<Long>` (count — total attendance count in that range).
```kotlin
data class Schedule(
    val id: Long,
    val dayOfWeek: String,
    val timeSlotStart: String,
    val timeSlotEnd: String
    // `teacher`, `subject`, `students` all excluded from JSON entirely (server-side @JsonIgnore)
)
```

---

## 15. Secretary — `/secretary` — HTTP status is always 200, check `body.statusCode`

| Method | Path | Auth |
|---|---|---|
| POST | `/secretary/create-secretary` | any valid token (no role check) |
| GET | `/secretary/show-all` | any valid token |

Request:
```kotlin
data class SecretaryDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val fcmToken: String,
    val phone: String,
    val teacherId: Long
)
```
`400` in the body if the email is already registered.

Response: `ApiResponse<Secretary>` (create), `ApiResponse<List<Secretary>>` (show-all).
```kotlin
data class Secretary(val id: Long, val phone: String, val user: User, val teacher: Teacher)
```

---

## 16. Translation — `/translate` (top-level, not under any feature prefix)

### `GET /translate?text=...&targetLanguage=...`
Auth: any valid token (not in the public-paths list).
Query params: `text` (string to translate), `targetLanguage` (target language code, e.g. `"es"`).
Response: **plain text string**, not wrapped in the `ApiResponse` envelope — the translated text, or an error string on failure.

---

## 17. WebSocket — `/web-socket/{userId}` (chat, presence, call signaling)

Not a REST endpoint — connect with a WebSocket client to:
```
ws://<host>:8080/web-socket/{yourUserId}?token=<jwt>
```
The `token` query param is required (this is how auth works for the socket — no `Authorization` header). `{userId}` in the path should match the id encoded in your JWT.

**Outgoing messages you send** (as JSON text frames), for call signaling only — chat message sending isn't implemented server-side yet beyond relaying call events:
```kotlin
data class OutgoingEvent(val event: String, val data: CallSignal)
// event is one of: "CALL_INVITE", "CALL_ACCEPT", "CALL_REJECT", "CALL_END"

data class CallSignal(
    val channelName: String,
    val callType: String,     // "VIDEO" or "AUDIO"
    val toUserId: Long,
    val fromUserId: Long? = null,   // ignored if sent — server overwrites this with your authenticated id
    val fromUserName: String? = null,
    val fromUserImage: String? = null,
    val provider: String? = null    // "AGORA" or "LIVEKIT"
)
```

**Incoming messages you'll receive:**
- Relayed call signal: `{"event": "CALL_INVITE" | "CALL_ACCEPT" | "CALL_REJECT" | "CALL_END", "data": CallSignal}`
- Presence broadcast (sent to *every* connected client whenever anyone connects/disconnects): `{"event": "PRESENCE", "data": {"userId": Long, "online": Boolean}}`

If you send `"CALL_INVITE"` and the target user has no open socket, the server automatically falls back to a push notification instead — no action needed from the caller side.

---

## 18. Not implemented yet

`ExamController` (`/exams`), `ChoiceController` (`/choices`), `QuestionController` (`/questions`) exist as empty stubs with no endpoints — calling anything under those paths 404s. The `Exam`/`Question`/`Choice` entities exist in the database schema but aren't exposed via API yet.
