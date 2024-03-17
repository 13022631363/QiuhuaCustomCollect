plugins {
    id("java")
    id ("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.qiuhua.qiuhuacustomcollect"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()  //加载本地仓库
    mavenCentral()  //加载中央仓库
    maven("https://jitpack.io")
    maven {
        name = "spigotmc-repo"
        url = uri ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }  //SpigotMC仓库
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    compileOnly ("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")  //仅在编译时可用
    compileOnly (fileTree("src/libs/ItemsAdder_3.5.0.jar"))  //仅在编译时可用
    compileOnly("mysql:mysql-connector-java:8.0.33")
    compileOnly("com.zaxxer:HikariCP:5.0.1")//数据库连接池
    implementation("org.apache.httpcomponents.client5:httpclient5:5.1.3")
    implementation("org.apache.httpcomponents.client5:httpclient5-fluent:5.1.3")
    implementation("org.apache.httpcomponents.core5:httpcore5:5.2.4")
    implementation("org.apache.httpcomponents.core5:httpcore5-h2:5.2.4")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.withType<Jar>().configureEach {
    archiveFileName.set("QiuhuaCustomCollect-2.0.0.jar")
    destinationDirectory.set(File ("/Users/facered/Minecraft/server/Minecraft-1.20.2-server/plugins"))
}

tasks.withType<JavaCompile>{
    options.encoding = "UTF-8"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("1-full")
    dependencies {
        include(dependency("org.apache.httpcomponents.client5:httpclient5:5.1.3"))
        include(dependency("org.apache.httpcomponents.client5:httpclient5-fluent:5.1.3"))
        include(dependency("com.google.code.gson:gson:2.10.1"))
        include(dependency("org.apache.httpcomponents.core5:httpcore5:5.2.4"))
        include(dependency("org.apache.httpcomponents.core5:httpcore5-h2:5.2.4"))
    }
    manifest {
        attributes(mapOf("Main-Class" to "org.qiuhua.qiuhuacustomcollect.Main"))
    }
    mergeServiceFiles()
}