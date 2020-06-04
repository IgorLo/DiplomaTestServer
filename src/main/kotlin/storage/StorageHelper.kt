package storage

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import storage.entities.Entities
import storage.entities.Entities.StudentSet
import storage.entities.Entities.Group
import storage.entities.Entities.Student
import storage.entities.Entities.Teacher
import storage.entities.Entities.Activity
import storage.entities.Entities.School
import storage.repository.DataRepository
import java.util.*

object StorageHelper {

    val sessionFactory: SessionFactory by lazy {
        val configuration = Configuration()
        Entities::class.nestedClasses.forEach { subclass ->
            configuration.addAnnotatedClass(subclass.java)
        }
        configuration.configure()
        val builder = StandardServiceRegistryBuilder().applySettings(configuration.properties)
        configuration.buildSessionFactory(builder.build())
    }

    //TODO get repository by class

    val studentRepository : DataRepository<Student> by lazy {
        DataRepository<Student>()
    }

    val groupRepository : DataRepository<Group> by lazy {
        DataRepository<Group>()
    }

    val studentSetRepository : DataRepository<StudentSet> by lazy {
        DataRepository<StudentSet>()
    }

    val teacherRepository : DataRepository<Teacher> by lazy {
        DataRepository<Teacher>()
    }

    val schoolRepository : DataRepository<School> by lazy {
        DataRepository<School>()
    }

    val activityRepository : DataRepository<Activity> by lazy {
        DataRepository<Activity>()
    }

    val planRepository : DataRepository<Entities.Plan> by lazy {
        DataRepository<Entities.Plan>()
    }

    val planTaskRepository : DataRepository<Entities.PlanTask> by lazy {
        DataRepository<Entities.PlanTask>()
    }

    fun <R> transaction(f: (Session) -> TransactionResult<R>): Optional<R> {
        return try {
            sessionFactory.openSession().use {
                try {
                    it.beginTransaction()
                    val result = f(it)
                    if (result.isRollback) {
                        it.transaction.rollback()
                    } else {
                        it.flush()
                        it.transaction.commit()
                    }
                    Optional.ofNullable(result.data)
                } catch (e: Exception) {
                    e.printStackTrace()
                    it.transaction.rollback()
                    Optional.empty()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            Optional.empty()
        }
    }

    data class TransactionResult<T>(val isRollback: Boolean, val data: T? = null)

}