import com.pulumi.docker.RemoteImage
import com.pulumi.docker.RemoteImageArgs

println("Hello, World!")

ctx.log().info("bungle")

val ubuntuRemoteImage = RemoteImage(
    "ubuntuRemoteImage2", RemoteImageArgs.builder()
        .name("ubuntu:latest")
        .build()
)

alpine {

}