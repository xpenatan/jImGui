plugins {
    `kotlin-dsl`
}

repositories {
    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
    }
    mavenCentral()
}

dependencies {
    implementation("com.github.xpenatan:easy-publishing:-SNAPSHOT")
}
