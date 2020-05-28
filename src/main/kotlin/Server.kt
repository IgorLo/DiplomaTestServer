import com.beust.klaxon.Klaxon
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import server.Requests

fun main() {
    val app = Javalin.create { config ->
        config.enableCorsForAllOrigins()
    }

    app.routes {
        path("students") {
            get { ctx -> ctx.result(Controller.getAllStudents()) }
            path(":id") {
                get { ctx -> ctx.result(Controller.getStudent(ctx.pathParam("id").toLong())) }
            }
        }
        path("groups") {
            get { ctx -> ctx.result(Controller.getAllGroups()) }
            path(":id") {
                get { ctx -> ctx.result(Controller.getGroup(ctx.pathParam("id").toLong())) }
            }
        }
        path("sets") {
            get { ctx -> ctx.result(Controller.getAllStudentSets()) }
            post { ctx ->
                val request = Klaxon().parse<Requests.NewSet>(ctx.body())
                if (request == null) {
                    ctx.status(500)
                } else {
                    Controller.addNewSet(request)
                }
            }
            path(":id") {
                get { ctx -> ctx.result(Controller.getStudentSet(ctx.pathParam("id").toLong())) }
            }
        }
    }

    app.start(3000)
}
