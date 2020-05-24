import storage.entities.Entities.Group
import storage.entities.Entities.Student
import storage.StorageHelper
import kotlin.random.Random

fun main() {
//
    val student = Student()
    student.name = "Студент" + Random.nextInt(100000, 999999)
    StorageHelper.transaction {
        StorageHelper.TransactionResult(false, StorageHelper.studentRepository.save(it, student))
    }

//    val group = Group()
//    group.name = "Одна из групп"
//    StorageHelper.transaction {
//        StorageHelper.TransactionResult(false, StorageHelper.groupRepository.save(it, group))
//    }

    val allStudents = StorageHelper.transaction {
        StorageHelper.TransactionResult(false, StorageHelper.studentRepository.findAllEntities<Student>(it))
    }

    val oldGroup = StorageHelper.transaction {
        StorageHelper.TransactionResult(false, StorageHelper.groupRepository.findById<Group>(it, 23))
    }

    if (oldGroup.isPresent){
        println(oldGroup.get().name)
    }

    if (allStudents.isPresent){
        allStudents.get().forEach { currentStudent ->
//            if (Random.nextBoolean() && oldGroup.isPresent){
//                oldGroup.get().students.add(currentStudent)
//                currentStudent.group = oldGroup.get()
//                StorageHelper.transaction {
//                    StorageHelper.TransactionResult(false, StorageHelper.groupRepository.update(it, oldGroup.get()))
//                }
//                StorageHelper.transaction {
//                    StorageHelper.TransactionResult(false, StorageHelper.studentRepository.update(it, currentStudent))
//                }
//            }
            println(currentStudent.name + " " + currentStudent.group)
        }
    }

//    if (oldGroup.isPresent){
//        StorageHelper.transaction {
//            StorageHelper.TransactionResult(false, StorageHelper.groupRepository.update(it, oldGroup.get()))
//        }
//        println("saved")
//    }


//    val session = StorageHelper.sessionFactory.openSession()
//    println(StorageHelper.studentRepository.findById(session, 7)!!.name)


//    val app = Javalin.create()
//
//    app.routes {
//        path("test") {
//            get(Controller::getTestData)
//        }
//    }
//
//    app.start(3000)

}