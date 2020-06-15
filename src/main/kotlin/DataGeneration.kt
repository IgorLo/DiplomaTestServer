import Generation.ACTIVITY_TYPES
import Generation.SUBJECTS
import Utils.randomEvent
import com.github.javafaker.Faker
import storage.StorageHelper
import storage.entities.Entities
import storage.entities.Entities.School
import storage.entities.Entities.Student
import storage.entities.Entities.Group
import storage.entities.Entities.StudentSet
import kotlin.random.Random

fun main() {

//    StorageHelper.studentRepository.deleteAll<Student>()

    val plans = mutableListOf<Entities.Plan>()

    for (i in 1..Generation.PLANS){
        val plan = Entities.Plan()
        plan.name = "План №$i"
        plan.spec = Generation.TRAINING_SPEC.random() + "_" + Generation.TRAINING_SUBSPEC.random()
        plans.add(plan)
    }

    StorageHelper.planRepository.saveAll(plans)

    val activities = mutableListOf<Entities.Activity>()

    for (subject in SUBJECTS) {
        for (type in ACTIVITY_TYPES) {
            val activity = Entities.Activity()
            activity.activityType = type
            activity.subjectName = subject
            activities.add(activity)
        }
    }

    StorageHelper.activityRepository.saveAll(activities)

    val schools = mutableListOf<School>()

    for (schoolName in Generation.DEFAULT_SCHOOL_NAMES) {
        val school = School()
        school.name = schoolName
        schools.add(school)
    }

    StorageHelper.schoolRepository.saveAll(schools)

    for (plan in plans){
        plan.school = schools.random()
    }

    StorageHelper.planRepository.saveOrUpdateAll(plans)

    val groups = mutableListOf<Group>()
    for (i in 1..Generation.GROUPS) {
        val group = Group()
        group.name = Utils.randomGroupName()
        group.school = schools.random()
        groups.add(group)
    }

    StorageHelper.groupRepository.saveAll(groups)

    val students = mutableListOf<Student>()
    for (i in 1..Generation.STUDENTS) {
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
    for (i in 1..Generation.STUDENT_SETS) {
        val set = StudentSet()
        set.name = Utils.randomStudentSetName()
        sets.add(set)
    }

    StorageHelper.studentSetRepository.saveAll(sets)

    sets.forEach { set ->
        groups.forEach { group ->
            if (randomEvent(0.05)) {
                group.sets.add(set)
                set.groups.add(group)
            }
        }
        students.forEach { student ->
            if (randomEvent(0.05)) {
                student.sets.add(set)
                set.students.add(student)
            }
        }
    }

    StorageHelper.groupRepository.saveOrUpdateAll(groups)
    StorageHelper.studentRepository.saveOrUpdateAll(students)
    StorageHelper.studentSetRepository.saveOrUpdateAll(sets)

    val teachers = mutableListOf<Entities.Teacher>()

    for (i in 1..Generation.TEACHERS) {
        val teacher = Entities.Teacher()
        teacher.school = schools.random()
        teacher.fromHours = Random.nextInt(150, 180)
        teacher.toHours = Random.nextInt(230, 250)
        teacher.rate = Random.nextInt(100) / 100.0
        teacher.name = Faker.instance().name().fullName()
        teachers.add(teacher)
    }

    StorageHelper.teacherRepository.saveAll(teachers)

    for (teacher in teachers){
        for (i in 1..6){
            teacher.activities.add(activities.random())
        }
    }

    StorageHelper.teacherRepository.saveOrUpdateAll(teachers)

    for (activity in activities){
        teachers.random().activities.add(activity)
    }

    StorageHelper.teacherRepository.saveOrUpdateAll(teachers)

    //Теперь у нас есть

    // Школы,
    // Группы,
    // Студенты,
    // Множества,
    // Активности,
    // Учителя
    // Планы

    val planTasks = mutableListOf<Entities.PlanTask>()

    for (group in groups){
        val planTask = Entities.PlanTask()
        planTask.hours = Random.nextInt(20, 50)
        planTask.activity = activities.random()
        planTask.group = group
        planTask.plan = plans.random()
        planTask.planId = planTask.plan?.id
        if (randomEvent(0.2)){
            val suitableTeacher = teachers.filter { teacher ->
                teacher.activities.contains(planTask!!.activity)
            }.random() //TODO брать наименее нагруженного
            planTask.teacher = suitableTeacher
        }
        planTasks.add(planTask)
    }

    for (i in 1..Generation.PLAN_TASKS){
        val planTask = Entities.PlanTask()
        planTask.hours = Random.nextInt(20, 50)
        planTask.activity = activities.random()
        planTask.group = groups.random()
        planTask.plan = plans.random()
        planTask.planId = planTask.plan?.id
        if (randomEvent(0.6)){
            val suitableTeacher = teachers.filter { teacher ->
                teacher.activities.contains(planTask!!.activity)
            }.random() //TODO брать наименее нагруженного
            planTask.teacher = suitableTeacher
        }
        planTasks.add(planTask)
    }

    StorageHelper.planTaskRepository.saveAll(planTasks)

}
