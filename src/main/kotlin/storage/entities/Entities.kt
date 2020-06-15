package storage.entities

import com.beust.klaxon.Json
import javax.persistence.*

object Entities {

    @Entity
    @Table(name = "t_student")
    class Student() {
        @Json(name = "key")
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1

        @Column(unique = false, nullable = false)
        lateinit var name: String

        @Json(ignored = true)
        @ManyToOne(fetch = FetchType.EAGER)
        var group: Group? = null

        @Transient
        var groupId: Long? = null

        @Transient
        var groupName: String? = null

        @Json(ignored = true)
        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        @JoinTable(
            name = "student_sets",
            joinColumns = [JoinColumn(name = "student_id")],
            inverseJoinColumns = [JoinColumn(name = "set_id")]
        )
        var sets: MutableSet<StudentSet> = mutableSetOf()

        @Transient
        var setIds: List<Long> = emptyList()

        @Transient
        var setNames: List<String> = emptyList()

        @PostLoad
        private fun postLoad() {
            groupId = group?.id
            groupName = group?.name
            setIds = sets.map { it.id }
            setNames = sets.map { it.name }
        }

    }

    @Entity
    @Table(name = "t_group")
    class Group() {
        @Json(name = "key")
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1

        @Column(unique = false, nullable = false)
        lateinit var name: String

        @ManyToOne(fetch = FetchType.EAGER)
        var school: School? = null

        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "group", fetch = FetchType.EAGER)
        var students: MutableSet<Student> = mutableSetOf()

        @Json(ignored = true)
        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        @JoinTable(
            name = "group_sets",
            joinColumns = [JoinColumn(name = "group_id")],
            inverseJoinColumns = [JoinColumn(name = "set_id")]
        )
        var sets: MutableSet<StudentSet> = mutableSetOf()

        @Transient
        var setIds: List<Long> = emptyList()

        @PostLoad
        private fun postLoad() {
            setIds = sets.map { it.id }
        }
    }

    @Entity
    @Table(name = "t_set")
    class StudentSet() {
        @Json(name = "key")
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1

        @Column(unique = false, nullable = false)
        lateinit var name: String

        @Json(ignored = true)
        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, mappedBy = "sets")
        var students: MutableSet<Student> = mutableSetOf()

        @Json(ignored = true)
        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, mappedBy = "sets")
        var groups: MutableSet<Group> = mutableSetOf()

        @Json(name = "students")
        @Transient
        var allStudents: MutableSet<Student> = mutableSetOf()

        @PostLoad
        private fun postLoad() {
            allStudents = students
            groups.forEach {
                allStudents.addAll(it.students)
            }
        }

    }

    @Entity
    @Table(name = "t_sсhool")
    class School() {
        @Json(name = "key")
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1

        @Column(unique = false, nullable = false)
        lateinit var name: String
    }

    @Entity
    @Table(name = "t_teacher")
    class Teacher() {
        @Json(name = "key")
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1

        @Column(unique = false, nullable = false)
        lateinit var name: String

        @Column(unique = false, nullable = false)
        var fromHours: Int = -1

        @Column(unique = false, nullable = false)
        var toHours: Int = -1

        @Column(unique = false, nullable = false)
        var rate: Double = -1.0

        @ManyToOne(fetch = FetchType.EAGER)
        var school: School? = null

        @Transient
        var schoolName: String? = null

        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "teacher", fetch = FetchType.EAGER)
        var tasks: MutableSet<PlanTask> = mutableSetOf()

        @Transient
        var currentHours: Int? = null

        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        @JoinTable(
            name = "teacher_activities",
            joinColumns = [JoinColumn(name = "teacher_id")],
            inverseJoinColumns = [JoinColumn(name = "activity_id")]
        )
        var activities: MutableSet<Activity> = mutableSetOf()

        @PostLoad
        private fun postLoad() {
            currentHours = tasks.map { it.hours }.sum() //TODO умный расчёт
            schoolName = school?.name
        }
    }

    @Entity
    @Table(name = "t_activity")
    class Activity() {
        @Json(name = "key")
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1

        @Column(unique = false, nullable = false)
        lateinit var subjectName: String

        @Column(unique = false, nullable = false)
        lateinit var activityType: String
    }

    @Entity
    @Table(name = "t_plan_task")
    class PlanTask() {
        @Json(name = "key")
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1

        @Json(ignored = true)
        @ManyToOne(fetch = FetchType.EAGER)
        var activity: Activity? = null

        @Transient
        var discipline: String? = null

        @Transient
        var activityType: String? = null

        @Json(ignored = true)
        @ManyToOne(fetch = FetchType.EAGER)
        var group: Group? = null

        @Transient
        var groupId: Long? = -1

        @Transient
        var groupName: String? = null

        @Column(unique = false, nullable = false)
        var hours: Int = -1

        @Json(ignored = true)
        @ManyToOne(fetch = FetchType.EAGER)
        var plan: Plan? = null

        @Transient
        var planId: Long? = -1

        @Transient
        var planName: String? = null

        @Json(ignored = true)
        @ManyToOne(fetch = FetchType.EAGER)
        var teacher: Teacher? = null

        @Transient
        var teacherName: String? = ""

        @Transient
        var teacherId: Long? = -1

        @PostLoad
        private fun postLoad() {
            planId = plan?.id
            planName = plan?.name
            teacherName = teacher?.name
            teacherId = teacher?.id
            groupName = group?.name
            groupId = group?.id
            discipline = activity?.subjectName
            activityType = activity?.activityType
        }
    }

    @Entity
    @Table(name = "t_plan")
    class Plan() {
        @Json(name = "key")
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1

        @Column(unique = false, nullable = false)
        lateinit var name: String

        @Column(unique = false, nullable = false)
        lateinit var spec: String

        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "plan", fetch = FetchType.EAGER)
        var tasks: MutableSet<PlanTask> = mutableSetOf()

        @Transient
        var totalHours: Int = -1

        @Json(ignored = true)
        @ManyToOne(fetch = FetchType.EAGER)
        var school: School? = null

        @Transient
        var schoolName: String? = ""

        @Transient
        var currentHours: Int = -1

        @PostLoad
        private fun postLoad() {
            tasks.forEach { task ->
                totalHours += task.hours
                if (task.teacher != null) {
                    currentHours += task.hours
                }
            }
            schoolName = school?.name
        }
    }

}
