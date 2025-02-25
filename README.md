# GsAdmob

**Gradle**
**Step 1.** Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```css
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
**Step 2.** Add the dependency
```css
dependencies {
	        implementation 'com.github.vtabk2:GsAdmob:1.0.0'
	}
```