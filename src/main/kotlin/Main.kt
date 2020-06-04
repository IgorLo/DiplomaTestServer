import storage.entities.Entities.Group
import storage.entities.Entities.Student
import storage.StorageHelper
import storage.entities.Entities
import kotlin.random.Random

fun main() {
//
    val subclasses = Entities::class.nestedClasses
    println(subclasses.map { it::class.java }.toString())

}