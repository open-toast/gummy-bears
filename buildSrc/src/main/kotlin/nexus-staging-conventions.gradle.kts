plugins {
    id("io.codearte.nexus-staging")
}

if (isRelease()) {
    nexusStaging {
        username = Remote.USERNAME
        password = Remote.PASSWORD
        packageGroup = "com.toasttab"
        numberOfRetries = 50
    }
}
