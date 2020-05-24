import Utils.randomEvent
import com.github.javafaker.Faker
import storage.StorageHelper
import storage.entities.Entities.Student
import storage.entities.Entities.Group
import storage.entities.Entities.StudentSet

fun main() {

//    StorageHelper.studentRepository.deleteAll<Student>()

    val groups = mutableListOf<Group>()
    for (i in 1..5) {
        val group = Group()
        group.name = Utils.randomGroupName()
        groups.add(group)
    }

    StorageHelper.groupRepository.saveAll(groups)

    val students = mutableListOf<Student>()
    for (i in 1..100) {
        val student = Student()
        student.name = Faker.instance().name().fullName()
        students.add(student)
    }

    StorageHelper.studentRepository.saveAll(students)

    students.forEach { student ->
        val randomGroup = groups.random()
        student.group = randomGroup
        randomGroup.students.add(student)
    }

    StorageHelper.groupRepository.saveOrUpdateAll(groups)
    StorageHelper.studentRepository.saveOrUpdateAll(students)

    val sets = mutableListOf<StudentSet>()
    for (i in 1..10){
        val set = StudentSet()
        set.name = Utils.randomStudentSetName()
        sets.add(set)
    }

    StorageHelper.studentSetRepository.saveAll(sets)

    sets.forEach { set ->
        groups.forEach { group ->
            if (randomEvent(0.5)){
                group.sets.add(set)
                set.groups.add(group)
            }
        }
        students.forEach { student ->
            if (randomEvent(0.5)){
                student.sets.add(set)
                set.students.add(student)
            }
        }
    }

    StorageHelper.groupRepository.saveOrUpdateAll(groups)
    StorageHelper.studentRepository.saveOrUpdateAll(students)
    StorageHelper.studentSetRepository.saveOrUpdateAll(sets)

}
