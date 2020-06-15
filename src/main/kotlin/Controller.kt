import com.beust.klaxon.Klaxon
import org.hibernate.Session
import server.Requests
import storage.StorageHelper
import storage.entities.Entities

object Controller {

    fun getStudent(id: Long): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.studentRepository.findById<Entities.Student>(
                    it, id
                )
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(null)
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getGroup(id: Long): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.groupRepository.findById<Entities.Group>(
                    it, id
                )
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(null)
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getStudentSet(id: Long): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.groupRepository.findById<Entities.StudentSet>(
                    it, id
                )
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(null)
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getAllStudents(): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.studentRepository.findAllEntities<Entities.Student>(it)
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(emptyArray<Entities.Student>())
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getAllGroups(): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.groupRepository.findAllEntities<Entities.Group>(it)
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(emptyArray<Entities.Group>())
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getAllStudentSets(): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.studentSetRepository.findAllEntities<Entities.StudentSet>(it)
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(emptyArray<Entities.Student>())
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun addNewSet(request: Requests.NewSet) {
        StorageHelper.transaction { session ->
            val newStudentSet = Entities.StudentSet()
            newStudentSet.name = request.name
            StorageHelper.studentSetRepository.save(session, newStudentSet)
            request.studentIds.forEach { studentId ->
                val student = StorageHelper.studentRepository.findById<Entities.Student>(session, studentId)
                if (student == null) {
                    return@forEach
                } else {
                    addStudentToSet(session, student, newStudentSet)
                }
            }
            request.groupIds.forEach { groupId ->
                val group = StorageHelper.groupRepository.findById<Entities.Group>(session, groupId)
                if (group == null) {
                    return@forEach
                } else {
                    addGroupToSet(session, group, newStudentSet)
                }
            }
            return@transaction StorageHelper.TransactionResult(false, null)
        }
    }

    fun changePlanTaskTeacher(request: Requests.PlanTaskTeacherChange) {
        StorageHelper.transaction {
            val newTeacher = StorageHelper.teacherRepository.findById<Entities.Teacher>(it, request.teacherId)
            val planTask = StorageHelper.planTaskRepository.findById<Entities.PlanTask>(it, request.taskId)
            val oldTeacher = planTask?.teacher
            if (planTask == null) {
                return@transaction StorageHelper.TransactionResult(false, null)
            }
            if (newTeacher == null || request.teacherId == -1L) {
                planTask.teacher = null
                if (oldTeacher != null) {
                    oldTeacher.tasks.remove(planTask)
                    StorageHelper.teacherRepository.save(it, oldTeacher)
                }
                StorageHelper.planTaskRepository.save(it, planTask)
                return@transaction StorageHelper.TransactionResult(false, null)
            } else {
                if (!newTeacher.activities.contains(planTask.activity)) {
                    return@transaction StorageHelper.TransactionResult(false, null)
                }
                planTask.teacher = newTeacher
                newTeacher.tasks.add(planTask)
                if (oldTeacher != null) {
                    oldTeacher.tasks.remove(planTask)
                    StorageHelper.teacherRepository.save(it, oldTeacher)
                }
                StorageHelper.planTaskRepository.save(it, planTask)
                StorageHelper.teacherRepository.save(it, newTeacher)
                return@transaction StorageHelper.TransactionResult(false, null)
            }
        }
    }

    fun addStudentToSet(session: Session, student: Entities.Student, set: Entities.StudentSet) {
        student.sets.add(set)
        set.students.add(student)
        StorageHelper.studentRepository.save(session, student)
        StorageHelper.studentSetRepository.save(session, set)
    }

    fun addGroupToSet(session: Session, group: Entities.Group, set: Entities.StudentSet) {
        group.sets.add(set)
        set.groups.add(group)
        StorageHelper.groupRepository.save(session, group)
        StorageHelper.studentSetRepository.save(session, set)
    }

    fun getAllSchools(): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.schoolRepository.findAllEntities<Entities.School>(it)
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(emptyArray<Entities.School>())
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getSchool(id: Long): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.schoolRepository.findById<Entities.School>(
                    it, id
                )
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(null)
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getAllActivities(): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.activityRepository.findAllEntities<Entities.Activity>(it)
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(emptyArray<Entities.Activity>())
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getActivity(id: Long): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.activityRepository.findById<Entities.Activity>(
                    it, id
                )
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(null)
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getAllTeachers(): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.teacherRepository.findAllEntities<Entities.Teacher>(it)
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(emptyArray<Entities.Teacher>())
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getTeacher(id: Long): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.teacherRepository.findById<Entities.Teacher>(
                    it, id
                )
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(null)
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getAllPlans(): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.planRepository.findAllEntities<Entities.Plan>(it)
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(emptyArray<Entities.Plan>())
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getPlan(id: Long): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.planRepository.findById<Entities.Plan>(
                    it, id
                )
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(null)
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getAllPlanTasks(): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.planTaskRepository.findAllEntities<Entities.PlanTask>(it)
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(emptyArray<Entities.PlanTask>())
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getPlanTask(id: Long): String {
        val result = StorageHelper.transaction {
            return@transaction StorageHelper.TransactionResult(
                false,
                StorageHelper.planTaskRepository.findById<Entities.PlanTask>(
                    it, id
                )
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(null)
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getSuitableTeachers(taskId: Long): String {
        val result = StorageHelper.transaction {
            val task = StorageHelper.planTaskRepository.findById<Entities.PlanTask>(it, taskId);
            if (task == null) {
                return@transaction StorageHelper.TransactionResult<List<Entities.Teacher>>(
                    false,
                    emptyList()
                )
            }
            val suitableTeachers = StorageHelper
                .teacherRepository
                .findAllEntities<Entities.Teacher>(it)
                ?.filter { teacher ->
                    teacher.activities.contains(task.activity)
//                    if (teacher.activities.contains(task.activity)){
//                        if (task.teacher == null){
//                            return@filter true
//                        } else {
//                            return@filter task.teacher != teacher
//                        }
//                    } else {
//                        return@filter false
//                    }
                }
            return@transaction StorageHelper.TransactionResult<List<Entities.Teacher>>(
                false,
                suitableTeachers
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(emptyArray<Entities.Teacher>())
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

    fun getPossibleTasks(teacherId: Long): String {
        val result = StorageHelper.transaction {session ->
            val teacher = StorageHelper.teacherRepository.findById<Entities.Teacher>(session, teacherId)
            if (teacher == null) {
                return@transaction StorageHelper.TransactionResult<List<Entities.PlanTask>>(
                    false,
                    emptyList()
                )
            }
            val allTasks = StorageHelper.planTaskRepository.findAllEntities<Entities.PlanTask>(session)
            return@transaction StorageHelper.TransactionResult<List<Entities.PlanTask>>(
                false,
                allTasks!!.filter {task ->
                    (teacher.activities.contains(task.activity) && task.teacher != teacher)
                }
            )
        }
        return if (result.isEmpty) {
            Klaxon().toJsonString(emptyArray<Entities.PlanTask>())
        } else {
            Klaxon().toJsonString(result.get())
        }
    }

}