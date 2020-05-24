package storage.repository

import org.hibernate.Session
import storage.StorageHelper
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable


class DataRepository<T>() {

    fun save(session: Session, entity: T): Serializable? = session.save(entity)

    fun saveOrUpdate(session: Session, entity: T) = session.saveOrUpdate(entity)

    fun update(session: Session, entity: T) = session.update(entity)

    fun delete(session: Session, entity: T) = session.delete(entity)

    inline fun <reified T> findById(session: Session, id: Long) : T? {
        val selectResult = findByColumnName<T>(session, "id", id.toString())
        return if (selectResult == null) {
            null
        } else {
            when {
                selectResult.size > 1 -> {
                    null
                }
                selectResult.isEmpty() -> {
                    return null
                }
                else -> {
                    selectResult.first()
                }
            }
        }
    }

    inline fun <reified T> findByColumnName(session: Session, columnName: String, whereParam: String): List<T>? {
        val cb = session.criteriaBuilder
        val cq = cb.createQuery(T::class.java)
        val root: Root<T> = cq.from(T::class.java)
        val formedQuery = cq.select(root).where(cb.equal(root.get<T>(columnName), whereParam))

        return session.createQuery(formedQuery).list() as List<T>?
    }

    inline fun <reified T> findAllEntities(session: Session): List<T>? {
        val cb = session.criteriaBuilder
        val cq = cb.createQuery(T::class.java)
        val root: Root<T> = cq.from(T::class.java)
        val all: CriteriaQuery<T> = cq.select(root)

        val allQuery: TypedQuery<T> = session.createQuery(all)
        return allQuery.resultList
    }

    fun saveAll(list: MutableList<T>) {
        list.forEach { item ->
            StorageHelper.transaction {
                StorageHelper.TransactionResult(false, save(it, item))
            }
        }
    }

    fun saveOrUpdateAll(list: MutableList<T>) {
        list.forEach { item ->
            StorageHelper.transaction {
                StorageHelper.TransactionResult(false, saveOrUpdate(it, item))
            }
        }
    }

    inline fun <reified E> deleteAll() {
        StorageHelper.transaction {session ->
            val entities = findAllEntities<E>(session)
            if (entities == null){
                StorageHelper.TransactionResult(true, null)
            } else {
                entities.forEach {
                    delete(session, it as T)
                }
                StorageHelper.TransactionResult(false, null)
            }
        }
    }

}
