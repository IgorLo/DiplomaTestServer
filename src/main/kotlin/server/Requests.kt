package server

object Requests {

    data class NewSet(
        val name: String,
        val studentIds: List<Long>,
        val groupIds: List<Long>
    )

}
