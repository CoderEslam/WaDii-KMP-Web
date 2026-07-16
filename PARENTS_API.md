# Parents API — `/parents`

For frontend integration. Covers the `Parent` entity and its relation to `Student` (one parent → many students).

Follows the same conventions as `API_DOCUMENTATION.md`: every endpoint returns the `ApiResponse<T>` envelope, and **HTTP status is always 200** — always branch on `body.statusCode`, not the transport status code.

```kotlin
data class ApiResponse<T>(
    val data: T?,
    val message: String,
    val statusCode: Int,
    val timestamp: String
)
```

---

## The relationship

- `Parent` has many `Student` (`parent.students`).
- `Student` belongs to one `Parent` (`student.parent`, nullable — a student can exist without a parent assigned).
- The FK lives on `Student` (`parent_id`). `Parent.students` is just the read-side view of that.

```kotlin
data class Parent(
    val id: Long,
    val parentContact: String,
    val phone: String,
    val user: User,
    val students: List<Student>   // each student's own `parent` field is omitted here to avoid a cycle
)
```

`Student` (updated shape — see note at the bottom about the old doc):
```kotlin
data class Student(
    val id: Long,
    val phone: String,
    val user: User,
    val parent: Parent?,          // nullable; its own `students` list is omitted to avoid a cycle
    val subjects: List<Subject>,
    val level: Level,
    val schedules: List<Schedule>,
    val educationalCenters: List<EducationalCenter>,
    val exams: List<Exam>
)
```

---

## Endpoints

| Method | Path | Auth |
|---|---|---|
| POST | `/parents/create-parent` | **ADMIN, SECRETARY** |
| POST | `/parents/update-parent` | any valid token (self-service — see note) |
| POST | `/parents/enroll` | **ADMIN, SECRETARY** |
| POST | `/parents/{parentId}/students` | any valid token with role ADMIN, PARENTS, or SECRETARY |
| GET | `/parents/all-parents` | any valid token |
| GET | `/parents/{id}` | any valid token |
| GET | `/parents/by-email/{email}` | any valid token |
| GET | `/parents/by-phone/{phone}` | any valid token |
| DELETE | `/parents/delete/{id}` | any valid token (no ownership check) |

### `POST /parents/create-parent`
Creates a `User` login (role `PARENTS`) and a `Parent` record. Optionally links *already-existing* students by id.

```kotlin
data class ParentRequest(
    val parentContact: String,
    val phone: String,
    val studentIds: Set<Long>?,   // optional — ids of existing students to attach to this parent
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val fcmToken: String
)
```
`404` in the body if `email` is already registered.

⚠️ `studentIds` is **additive, not a replace**: any student ids you pass get their `parent` pointed at this new parent (overwriting whatever parent they had before). Students not listed are left untouched — this endpoint never removes existing links.

Response: `ApiResponse<Parent>`.

### `POST /parents/update-parent`
Self-service, same pattern as `/students/update-student`: there's no id in the request — the server resolves *your own* parent record from your JWT (creating one if you don't have one yet) and updates it.

Request body: same `ParentRequest` as above. `studentIds` has the same additive semantics described above (omit it / send empty to leave your current students unchanged).

Response: `ApiResponse<Parent>`.

### `POST /parents/enroll`
**The "sign up a parent with their kids in one shot" endpoint.** Creates the parent's login + record, then creates each entry in `students` as a brand-new student account (own login, level, subjects, schedule) attached to that new parent — all in a single database transaction.

```kotlin
data class EnrollRequest(
    val parentContact: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val fcmToken: String,
    val students: List<StudentRequest>
)

data class StudentRequest(
    val parentId: Long? = null,   // ignored here — the server fills this in with the new parent's id
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

**All-or-nothing**: if the parent's email is already taken, or *any* student in the list fails (duplicate email, missing/invalid `levelId`, etc.), the whole call is rolled back — no parent and no students are created. The failure reason comes back as `body.message` with `statusCode` `409` (email conflict) or the underlying student-creation error.

Response: `ApiResponse<Parent>` — on success, `data.students` contains all the newly created students.

### `POST /parents/{parentId}/students`
**"Enroll another kid under an existing parent"** — same atomic new-student-creation behavior as `/enroll`, but targets an already-existing parent by path id instead of creating one.

Request body: `List<StudentRequest>` (raw JSON array, not wrapped in an object). `parentId` on each entry is ignored/overwritten.

`404` if `{parentId}` doesn't exist. Same all-or-nothing rollback behavior as `/enroll` if any student fails.

Response: `ApiResponse<Parent>` — `data.students` reflects the full, updated student list for this parent.

⚠️ **Known gap**: this endpoint accepts the `PARENTS` role but does **not** verify that the caller's own parent id matches `{parentId}` in the path — any authenticated parent token can currently add students under *any* parent id. Same pre-existing gap as `/students/update-student`. Flag to backend if this needs locking down before going live with parent-facing clients.

### `GET /parents/all-parents`
Response: `ApiResponse<List<Parent>>`.

### `GET /parents/{id}`, `GET /parents/by-email/{email}`, `GET /parents/by-phone/{phone}`
Response: `ApiResponse<Parent>`. `404` in the body if not found.

### `DELETE /parents/delete/{id}`
Response: `ApiResponse<Parent>` (`data` is `null` on success — see message). `404` in the body if not found. Note: this does **not** cascade-delete the parent's students or their `User` login; students are simply left with a dangling reference until reassigned (`parent_id` is nullable so the FK itself won't break, but `getParentById` for the old id will just 404 afterward).

---

## Note on the existing `API_DOCUMENTATION.md`
Section 7 (Students) there predates this feature and still lists `parentId`/`parentContact` as direct fields on `Student` — that's now stale (that data moved to `Parent`). It's being corrected there; use the `Student` shape above as the current source of truth for the `parent` relation.
