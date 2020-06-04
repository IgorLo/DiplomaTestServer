import com.github.javafaker.Faker
import kotlin.random.Random

object Utils {

    fun randomEvent(probability: Double): Boolean {
        if (probability > 1.0 || probability < 0.0) {
            throw IllegalStateException("Вероятность должна находится в пределах от 0 до 1")
        }
        return Random.nextDouble() < probability
    }

    fun randomGroupName(): String = Faker
        .instance()
        .number()
        .numberBetween(11111, 49999)
        .toString()
        .plus("/")
        .plus(
            Faker
                .instance()
                .number()
                .numberBetween(11111, 99999)
                .toString()
        )

    fun randomStudentSetName(): String = Faker.instance().rockBand().name()

}