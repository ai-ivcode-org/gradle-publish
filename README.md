# gradle-publish

A small Gradle plugin (Kotlin) for **ivcode.org projects** that simplifies building and publishing Java artifacts.

This plugin automatically:

- Applies and configures the `java` and `maven-publish` plugins
- Produces a **sources JAR**
- Creates a **Maven publication**
- Publishes artifacts to the ivcode.org Maven repository hosted on S3

It handles **snapshots** and **releases** automatically:

```
s3://maven.ivcode.org/snapshot/   → for snapshot versions
s3://maven.ivcode.org/release/    → for release versions
```

AWS credentials are taken from environment variables:

```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
```

---

## Quick Info

- **Publication Name:** `mavenJava` (uses the project’s Java component)
- **Repository URL:** `s3://maven.ivcode.org/{snapshot|release}/`
- **Credentials:** Environment variables (see above)  
