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

        //TODO SCHOOL
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
        var allStudents : MutableSet<Student> = mutableSetOf()

        @PostLoad
        private fun postLoad() {
            allStudents = students
            groups.forEach {
                allStudents.addAll(it.students)
            }
        }

    }

}
