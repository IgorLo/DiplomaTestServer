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
        return if (result.isEmpty){
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
        return if (result.isEmpty){
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
        return if (result.isEmpty){
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
        return if (result.isEmpty){
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
        return if (result.isEmpty){
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
        return if (result.isEmpty){
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
                if (student == null){
                    return@forEach
                } else {
                    addStudentToSet(session, student, newStudentSet)
                }
            }
            request.groupIds.forEach { groupId ->
                val group = StorageHelper.groupRepository.findById<Entities.Group>(session, groupId)
                if (group == null){
                    return@forEach
                } else {
                    addGroupToSet(session, group, newStudentSet)
                }
            }
            return@transaction StorageHelper.TransactionResult(false, null)
        }
    }

    fun addStudentToSet(session: Session, student: Entities.Student, set: Entities.StudentSet){
        student.sets.add(set)
        set.students.add(student)
        StorageHelper.studentRepository.save(session, student)
        StorageHelper.studentSetRepository.save(session, set)
    }

    fun addGroupToSet(session: Session, group: Entities.Group, set: Entities.StudentSet){
        group.sets.add(set)
        set.groups.add(group)
        StorageHelper.groupRepository.save(session, group)
        StorageHelper.studentSetRepository.save(session, set)
    }

}