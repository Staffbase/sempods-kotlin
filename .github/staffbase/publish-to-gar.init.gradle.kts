// Staffbase fork only — never part of an upstream pull request. See STAFFBASE.md.
//
// Adds the Staffbase Artifact Registry as a publishing target without touching the build, so the
// fork carries no change to upstream's build files. Gradle derives `garUsername`/`garPassword` from
// the repository name; the release workflow passes them as `ORG_GRADLE_PROJECT_gar*`.
import org.gradle.api.publish.PublishingExtension

allprojects {
  plugins.withId("maven-publish") {
    extensions.configure<PublishingExtension> {
      repositories {
        maven {
          name = "gar"
          url = uri("https://europe-maven.pkg.dev/staffbase-artifacts/maven-publish")
          credentials(PasswordCredentials::class)
        }
      }
    }
  }
}
