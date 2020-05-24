package storage.entities

import com.beust.klaxon.Json
import javax.persistence.*

object Entities {

    @Entity
    @Table(name = "t_student")
    class Student(){
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1

        @Column(unique = false, nullable = false)
        lateinit var name: String

        @Json(ignored = true)
        @ManyToOne(fetch = FetchType.EAGER)
        var group : Group? = null

        @Transient
        var groupId : Long? = null

        @Json(ignored = true)
        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        @JoinTable(
            name = "student_sets",
            joinColumns = [JoinColumn(name = "student_id")],
            inverseJoinColumns = [JoinColumn(name = "set_id")]
        )
        var sets : MutableSet<StudentSet> = mutableSetOf()

        @Transient
        var setIds : List<Long> = emptyList()

        @PostLoad
        private fun postLoad(){
            groupId = group?.id
            setIds = sets.map { it.id }
        }

    }

    @Entity
    @Table(name = "t_group")
    class Group(){
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1

        @Column(unique = false, nullable = false)
        lateinit var name: String

        @OneToMany(cascade = [CascadeType.ALL], mappedBy="group", fetch = FetchType.EAGER)
        var students : MutableSet<Student> = mutableSetOf()

        @Json(ignored = true)
        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        @JoinTable(
            name = "group_sets",
            joinColumns = [JoinColumn(name = "group_id")],
            inverseJoinColumns = [JoinColumn(name = "set_id")]
        )
        var sets : MutableSet<StudentSet> = mutableSetOf()

        @Transient
        var setIds: List<Long> = emptyList()

        @PostLoad
        private fun postLoad(){
            setIds = sets.map { it.id }
        }

        //TODO SCHOOL
    }

    @Entity
    @Table(name = "t_set")
    class StudentSet(){
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1

        @Column(unique = false, nullable = false)
        lateinit var name: String

        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, mappedBy = "sets")
        var students : MutableSet<Student> = mutableSetOf()

        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, mappedBy = "sets")
        var groups : MutableSet<Group> = mutableSetOf()

    }

}
