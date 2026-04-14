#!/bin/bash
echo "Building..."
./gradlew :core:classes -q
echo "Starting Client..."
java -cp "$(./gradlew -q :core:printClasspath)" com.gdx.game.network.DemoClient
